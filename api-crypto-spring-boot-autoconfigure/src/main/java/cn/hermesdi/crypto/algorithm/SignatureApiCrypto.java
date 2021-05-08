package cn.hermesdi.crypto.algorithm;

import cn.hermesdi.crypto.exception.ApiCryptoExceptionType;
import cn.hermesdi.crypto.exception.ApiEncryptException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hermesdi.crypto.annotation.signature.SignatureCrypto;
import cn.hermesdi.crypto.bean.ApiCryptoBody;
import cn.hermesdi.crypto.config.ApiCryptoConfig;
import cn.hermesdi.crypto.exception.ApiDecodeException;
import cn.hermesdi.crypto.ov.IApiRequestBody;
import cn.hermesdi.crypto.ov.IApiResponseBody;
import cn.hermesdi.crypto.util.RandomStrUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 签名 实现
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public class SignatureApiCrypto implements ApiCryptoAlgorithm {

    private static final Log logger = LogFactory.getLog(SignatureApiCrypto.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApiCryptoConfig apiCryptoConfig;

    private IApiRequestBody iApiRequestBody;

    private IApiResponseBody iApiResponseBody;

    public SignatureApiCrypto() {
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setiApiRequestBody(IApiRequestBody iApiRequestBody) {
        this.iApiRequestBody = iApiRequestBody;
    }

    public void setiApiResponseBody(IApiResponseBody iApiResponseBody) {
        this.iApiResponseBody = iApiResponseBody;
    }

    @Override
    public boolean isCanRealize(MethodParameter methodParameter, boolean requestOrResponse) {
        SignatureCrypto annotation = this.getAnnotation(methodParameter, SignatureCrypto.class);
        return !Objects.isNull(annotation);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        SignatureCrypto annotation = this.getAnnotation(methodParameter, SignatureCrypto.class);

        ApiCryptoBody apiCryptoBody = this.requestBody(annotation, httpInputMessage, iApiRequestBody, objectMapper, logger);

        if (!StringUtils.hasText(apiCryptoBody.getData())) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.PARAM_DATA_MISSING;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        if (!StringUtils.hasText(apiCryptoBody.getNonce())) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.PARAM_NONCE_MISSING;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        if (Objects.isNull(apiCryptoBody.getTimestamp())) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.PARAM_TIMESTAMP_MISSING;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        if (!StringUtils.hasText(apiCryptoBody.getSignStr())) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.PARAM_SIGN_MISSING;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }


        long timeout = apiCryptoConfig.getSignature().getTimeout();
        if (annotation.timeout() > 0) {
            timeout = annotation.timeout();
        }

        if (timeout > 0) {
            long time = (System.currentTimeMillis() / 1000) - apiCryptoBody.getTimestamp();
            if (time > timeout) {
                ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.SIGNATURE_TIMED_OUT;
                logger.error(exceptionType.getMessage());
                throw new ApiDecodeException(exceptionType);
            }
        }

        String secretKey = apiCryptoConfig.getSignature().getSecretKey();

        if (StringUtils.hasText(annotation.SecretKey())) {
            secretKey = annotation.SecretKey();
        }

        if (!StringUtils.hasText(secretKey)) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.REQUIRED_SIGNATURE_PARAM;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        if (!signature(apiCryptoBody, secretKey).equals(apiCryptoBody.getSignStr())) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.VERIFY_SIGNATURE_FAILED;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        return this.stringToInputStream(apiCryptoBody.getData().getBytes(apiCryptoConfig.getCharset()), httpInputMessage.getHeaders(), logger);
    }

    @Override
    public Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        SignatureCrypto annotation = this.getAnnotation(methodParameter, SignatureCrypto.class);

        String json = responseBody(body, objectMapper, logger);

        String secretKey = apiCryptoConfig.getSignature().getSecretKey();
        if (StringUtils.hasText(annotation.SecretKey())) {
            secretKey = annotation.SecretKey();
        }

        ApiCryptoBody apiCryptoBody = new ApiCryptoBody();
        apiCryptoBody.setData(json);
        apiCryptoBody.setNonce(RandomStrUtil.getRandomNumber(32));
        apiCryptoBody.setTimestamp(System.currentTimeMillis() / 1000);
        apiCryptoBody.setSignStr(signature(apiCryptoBody, secretKey));

        // 使用自定义响应体
        if (iApiResponseBody != null) {
            return iApiResponseBody.responseBody(annotation, apiCryptoBody);
        }

        return apiCryptoBody;
    }


    /**
     * @param apiCryptoBody: 签名数据
     * @param secretKey:     签名秘钥
     * @return java.lang.String
     * @author hermes-di
     **/
    private String signature(ApiCryptoBody apiCryptoBody, String secretKey) {
        try {
            String str = "data=" + apiCryptoBody.getData() +
                    "&timestamp=" + apiCryptoBody.getTimestamp() +
                    "&nonce=" + apiCryptoBody.getNonce() +
                    "&key=" + secretKey;
            return DigestUtils.md5DigestAsHex(str.getBytes(apiCryptoConfig.getCharset()));
        } catch (Exception e) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.SIGNATURE_FAILED;
            logger.error(exceptionType.getMessage() + " ERROR：" + e.getMessage());
            throw new ApiEncryptException(exceptionType);
        }
    }
}
