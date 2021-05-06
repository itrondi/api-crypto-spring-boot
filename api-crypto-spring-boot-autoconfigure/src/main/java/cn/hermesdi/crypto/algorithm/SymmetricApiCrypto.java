package cn.hermesdi.crypto.algorithm;

import cn.hermesdi.crypto.annotation.symmetric.SymmetricCrypto;
import cn.hermesdi.crypto.bean.ApiCryptoBody;
import cn.hermesdi.crypto.config.ApiCryptoConfig;
import cn.hermesdi.crypto.constants.EncodingType;
import cn.hermesdi.crypto.exception.ApiCryptoExceptionType;
import cn.hermesdi.crypto.exception.ApiDecodeException;
import cn.hermesdi.crypto.exception.ApiEncryptException;
import cn.hermesdi.crypto.ov.IApiRequestBody;
import cn.hermesdi.crypto.ov.IApiResponseBody;
import cn.hermesdi.crypto.util.CryptoUtil;
import cn.hermesdi.crypto.util.RandomStrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 对称性加密、解密 实现
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public class SymmetricApiCrypto implements ApiCryptoAlgorithm {

    private static final Log logger = LogFactory.getLog(SymmetricApiCrypto.class);

    private final ObjectMapper objectMapper;

    private final IApiRequestBody iApiRequestBody;

    private final IApiResponseBody iApiResponseBody;

    private final ApiCryptoConfig apiCryptoConfig;

    public SymmetricApiCrypto(ApiCryptoConfig apiCryptoConfig, ObjectMapper objectMapper, IApiRequestBody iApiRequestBody, IApiResponseBody iApiResponseBody) {
        this.apiCryptoConfig = apiCryptoConfig;
        this.objectMapper = objectMapper;
        this.iApiRequestBody = iApiRequestBody;
        this.iApiResponseBody = iApiResponseBody;
    }

    @Override
    public boolean isCanRealize(MethodParameter methodParameter, boolean requestOrResponse) {
        SymmetricCrypto annotation = this.getAnnotation(methodParameter, SymmetricCrypto.class);
        return !Objects.isNull(annotation);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        SymmetricCrypto annotation = this.getAnnotation(methodParameter, SymmetricCrypto.class);

        ApiCryptoBody apiCryptoBody = this.requestBody(annotation, httpInputMessage, iApiRequestBody, objectMapper, logger);

        if (!StringUtils.hasText(apiCryptoBody.getData())) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.PARAM_DATA_MISSING;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        if (annotation.type().isProduceIv()) {
            if (!StringUtils.hasText(apiCryptoBody.getIv())) {
                ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.PARAM_VI_MISSING;
                logger.error(exceptionType.getMessage());
                throw new ApiDecodeException(exceptionType);
            }
        } else {
            apiCryptoBody.setIv(null);
        }

        String secretKey = secretKey(annotation);


        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

        String encryptData;

        try {

            encryptData = CryptoUtil.symmetric(
                    annotation.type().getType(),
                    annotation.type().getMethod(),
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    apiCryptoBody.getData(),
                    encodingType,
                    apiCryptoBody.getIv() != null ? apiCryptoBody.getIv() : null
            );

        } catch (Exception e) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.DECRYPTION_FAILED;
            logger.error(exceptionType.getMessage() + " ERROR：" + e.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        if (!StringUtils.hasText(encryptData)) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.DATA_EMPTY;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        return this.stringToInputStream(encryptData.getBytes(StandardCharsets.UTF_8), httpInputMessage.getHeaders(), logger);
    }

    @Override
    public Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        SymmetricCrypto annotation = this.getAnnotation(methodParameter, SymmetricCrypto.class);

        String json = responseBody(body, objectMapper, logger);

        String secretKey = secretKey(annotation);

        String iv = null;

        if (annotation.type().isProduceIv()) {
            iv = RandomStrUtil.getRandomNumber(annotation.type().getIvLength());
        }

        if (StringUtils.hasText(annotation.SecretKey())) {
            secretKey = annotation.SecretKey();
        }

        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

        String encryptData;

        try {
            encryptData = CryptoUtil.symmetric(
                    annotation.type().getType(),
                    annotation.type().getMethod(),
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    json,
                    encodingType,
                    iv
            );

        } catch (Exception e) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.ENCRYPTION_FAILED;
            logger.error(exceptionType.getMessage() + " ERROR：" + e.getMessage());
            throw new ApiEncryptException(exceptionType);
        }

        // 使用默认响应体
        ApiCryptoBody apiCryptoBody = new ApiCryptoBody().setData(encryptData);
        if (annotation.type().isProduceIv()) {
            apiCryptoBody.setIv(iv);
        }

        // 使用自定义响应体
        if (iApiResponseBody != null) {
            return iApiResponseBody.responseBody(annotation, apiCryptoBody);
        }

        return apiCryptoBody;

    }

    /**
     * 获取 秘钥
     *
     * @param annotation: 执行注解
     * @return java.lang.String
     * @author hermes-di
     **/
    private String secretKey(SymmetricCrypto annotation) {
        String secretKey = apiCryptoConfig.getSymmetricKey().get(annotation.type().getType());

        if (StringUtils.hasText(annotation.SecretKey())) {
            secretKey = annotation.SecretKey();
        }

        if (!StringUtils.hasText(secretKey)) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.REQUIRED_CRYPTO_PARAM;
            logger.error(exceptionType.getMessage() + " ERROR：(无效的秘钥,请在配置文件或注解中秘钥配置)");
            throw new ApiEncryptException(exceptionType);
        }
        return secretKey;
    }

}
