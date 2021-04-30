package cn.hermesdi.crypto.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hermesdi.crypto.annotation.symmetric.SymmetricCrypto;
import cn.hermesdi.crypto.bean.ApiCryptoBody;
import cn.hermesdi.crypto.bean.InputMessage;
import cn.hermesdi.crypto.config.ApiCryptoConfig;
import cn.hermesdi.crypto.constants.EncodingType;
import cn.hermesdi.crypto.exception.ApiDecodeException;
import cn.hermesdi.crypto.exception.ApiEncryptException;
import cn.hermesdi.crypto.ov.IApiRequestBody;
import cn.hermesdi.crypto.ov.IApiResponseBody;
import cn.hermesdi.crypto.util.CryptoUtil;
import cn.hermesdi.crypto.util.RandomStrUtil;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Author hermes·di
 * @Date 2021/4/15 0015 11:45
 * @Describe 对称性加密、解密 实现
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
            logger.error("【ApiCrypto】 The data does not exist, check the request body JSON “data” field. (数据不存在，请检查请求体JSON “data” 字段。)");
            throw new ApiDecodeException("【ApiCrypto】 The data does not exist, check the request body JSON “data” field. (数据不存在，请检查请求体JSON “data” 字段。)");
        }

        if (annotation.type().isProduceIv()) {
            if (!StringUtils.hasText(apiCryptoBody.getIv())) {
                logger.error("【ApiCrypto】 The required iv does not exist. Check the JSON “iv” field in the request body. (需要的iv不存在。检查请求正文中的JSON “iv” 字段。)");
                throw new ApiDecodeException("【ApiCrypto】 The required iv does not exist. Check the JSON “iv” field in the request body. (需要的iv不存在。检查请求正文中的JSON “iv” 字段。)");
            }
        } else {
            apiCryptoBody.setIv(null);
        }

        String secretKey = secretKey(annotation);

        String encryptData;

        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

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
            logger.error("【ApiCrypto】 The decryption request body failed.(解密请求 body 失败)， ERROR：" + e.getMessage());
            throw new ApiEncryptException("【ApiCrypto】 The decryption request body failed.(解密请求 body 失败)，ERROR：" + e.getMessage());
        }

        if (!StringUtils.hasText(encryptData)) {
            logger.error("【ApiCrypto】 Data is empty after decryption.(解密后数据为空)");
            throw new ApiEncryptException("【ApiCrypto】 Data is empty after decryption.(解密后数据为空)");
        }

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(encryptData.getBytes(StandardCharsets.UTF_8));
            return new InputMessage(inputStream, httpInputMessage.getHeaders());
        } catch (Exception e) {
            logger.error("【ApiCrypto】 Decryption data conversion to stream failed.（解密后的字符串转换为输入流失败）， ERROR：" + e.getMessage());
            throw new ApiDecodeException("【ApiCrypto】 Decryption data conversion to stream failed.（解密后的字符串转换为输入流失败）， ERROR：" + e.getMessage());
        }
    }

    @Override
    public Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        SymmetricCrypto annotation = this.getAnnotation(methodParameter, SymmetricCrypto.class);

        // 转成json字符串
        String content;

        try {
            content = objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            logger.error("【ApiCrypto】 The response body conversion to JSON failed. Please respond to data correctly.(响应body转换JSON失败，请正确响应数据)， ERROR：" + e.getMessage());
            throw new ApiEncryptException("【ApiCrypto】 The response body conversion to JSON failed. Please respond to data correctly.(响应body转换JSON失败，请正确响应数据)， ERROR：" + e.getMessage());
        }

        String secretKey = secretKey(annotation);

        String iv = null;

        if (annotation.type().isProduceIv()) {
            iv = RandomStrUtil.getRandomNumber(annotation.type().getIvLength());
        }

        if (StringUtils.hasText(annotation.SecretKey())) {
            secretKey = annotation.SecretKey();
        }

        String encryptData;

        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

        try {

            encryptData = CryptoUtil.symmetric(
                    annotation.type().getType(),
                    annotation.type().getMethod(),
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    content,
                    encodingType,
                    iv
            );

        } catch (Exception e) {
            logger.error("【ApiCrypto】 The encrypted response body failed.(加密响应body失败)，ERROR：" + e.getMessage());
            throw new ApiEncryptException("【ApiCrypto】 The encrypted response body failed.(加密响应body失败)，ERROR：" + e.getMessage());
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

    private String secretKey(SymmetricCrypto annotation) {
        String secretKey = apiCryptoConfig.getSymmetricKey().get(annotation.type().getType());

        if (StringUtils.hasText(annotation.SecretKey())) {
            secretKey = annotation.SecretKey();
        }

        if (!StringUtils.hasText(secretKey)) {
            logger.error("【ApiCrypto】 Invalid secret key. Please configure the secret key in the configuration file or comment.(无效的秘钥,请在配置文件或注解中秘钥配置)");
            throw new ApiEncryptException("【ApiCrypto】 Invalid secret key. Please configure the secret key in the configuration file or comment.(无效的秘钥,请在配置文件或注解中秘钥配置)");
        }
        return secretKey;
    }

}
