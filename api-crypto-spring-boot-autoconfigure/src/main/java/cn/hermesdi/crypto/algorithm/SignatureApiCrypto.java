package cn.hermesdi.crypto.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hermesdi.crypto.annotation.signature.SignatureCrypto;
import cn.hermesdi.crypto.bean.ApiCryptoBody;
import cn.hermesdi.crypto.bean.InputMessage;
import cn.hermesdi.crypto.config.ApiCryptoConfig;
import cn.hermesdi.crypto.exception.ApiDecodeException;
import cn.hermesdi.crypto.exception.ApiEncryptException;
import cn.hermesdi.crypto.ov.IApiRequestBody;
import cn.hermesdi.crypto.ov.IApiResponseBody;
import cn.hermesdi.crypto.util.RandomStrUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Author hermes·di
 * @Date 2021/4/21 0021 22:25
 * @Describe 签名接口实现
 */
public class SignatureApiCrypto implements ApiCryptoAlgorithm {

    private static final Log logger = LogFactory.getLog(SignatureApiCrypto.class);

    private final ObjectMapper objectMapper;

    private final IApiRequestBody iApiRequestBody;

    private final IApiResponseBody iApiResponseBody;

    private final ApiCryptoConfig apiCryptoConfig;

    public SignatureApiCrypto(ApiCryptoConfig apiCryptoConfig, ObjectMapper objectMapper, IApiRequestBody iApiRequestBody, IApiResponseBody iApiResponseBody) {
        this.apiCryptoConfig = apiCryptoConfig;
        this.objectMapper = objectMapper;
        this.iApiRequestBody = iApiRequestBody;
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

        ApiCryptoBody apiCryptoBody = this.requestBody(annotation,httpInputMessage, iApiRequestBody, objectMapper, logger);

        if (!StringUtils.hasText(apiCryptoBody.getData()) ||
                !StringUtils.hasText(apiCryptoBody.getNonce()) ||
                !StringUtils.hasText(apiCryptoBody.getSignStr()) ||
                Objects.isNull(apiCryptoBody.getTimestamp())
        ) {
            logger.error("【ApiCrypto】 Missing required parameters.(缺少必需的参数)");
            throw new ApiDecodeException("【ApiCrypto】 Missing required parameters.(缺少必需的参数)");
        }

        long timeout = apiCryptoConfig.getSignature().getTimeout();
        if (annotation.timeout() > 0) {
            timeout = annotation.timeout();
        }

        if (timeout > 0) {
            long time = (System.currentTimeMillis() / 1000) - apiCryptoBody.getTimestamp();
            if (time > timeout) {
                logger.error("【ApiCrypto】 Signature timed out.(签名已超时)");
                throw new ApiDecodeException("【ApiCrypto】 Signature timed out.(签名已超时)");
            }
        }

        // TODO 如果没有配置
        String secretKey = apiCryptoConfig.getSignature().getSecretKey();
        if (StringUtils.hasText(annotation.SecretKey())) {
            secretKey = annotation.SecretKey();
        }

        if (!signature(apiCryptoBody, secretKey).equals(apiCryptoBody.getSignStr())) {
            logger.error("【ApiCrypto】 Verify signature failed.(验证签名失败)");
            throw new ApiDecodeException("【ApiCrypto】 Verify signature failed.(验证签名失败)");
        }

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(apiCryptoBody.getData().getBytes(StandardCharsets.UTF_8));
            return new InputMessage(inputStream, httpInputMessage.getHeaders());
        } catch (Exception e) {
            logger.error("【ApiCrypto】 Verify data conversion to stream failed.（验证后的字符串转换为输入流失败）， ERROR：" + e.getMessage());
            throw new ApiDecodeException("【ApiCrypto】 Verify data conversion to stream failed.（验证后的字符串转换为输入流失败）， ERROR：" + e.getMessage());
        }
    }

    @Override
    public Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        SignatureCrypto annotation = this.getAnnotation(methodParameter, SignatureCrypto.class);

        // 转成json字符串
        String data;

        try {
            data = objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            logger.error("【ApiCrypto】 The response body conversion to JSON failed. Please respond to data correctly.(响应body转换JSON失败，请正确响应数据)， ERROR：" + e.getMessage());
            throw new ApiEncryptException("【ApiCrypto】 The response body conversion to JSON failed. Please respond to data correctly.(响应body转换JSON失败，请正确响应数据)， ERROR：" + e.getMessage());
        }

        String secretKey = apiCryptoConfig.getSignature().getSecretKey();
        if (StringUtils.hasText(annotation.SecretKey())) {
            secretKey = annotation.SecretKey();
        }

        ApiCryptoBody apiCryptoBody = new ApiCryptoBody();
        apiCryptoBody.setData(data);
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
     * 签名
     *
     * @param apiCryptoBody: 签名数据
     * @param secretKey:     密钥
     * @return java.lang.String
     * @Author hermes·di
     */
    private String signature(ApiCryptoBody apiCryptoBody, String secretKey) {
        String str = "data=" + apiCryptoBody.getData() +
                "&timestamp=" + apiCryptoBody.getTimestamp() +
                "&nonce=" + apiCryptoBody.getNonce() +
                "&key=" + secretKey;
        return DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8));
    }
}
