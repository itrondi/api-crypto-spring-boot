package cn.hermesdi.crypto.algorithm;

import cn.hermesdi.crypto.annotation.AsymmetryCrypto;
import cn.hermesdi.crypto.bean.ApiCryptoBody;
import cn.hermesdi.crypto.config.ApiCryptoConfig;
import cn.hermesdi.crypto.constants.EncodingType;
import cn.hermesdi.crypto.exception.ApiCryptoExceptionType;
import cn.hermesdi.crypto.exception.ApiDecodeException;
import cn.hermesdi.crypto.exception.ApiEncryptException;
import cn.hermesdi.crypto.ov.IApiRequestBody;
import cn.hermesdi.crypto.ov.IApiResponseBody;
import cn.hermesdi.crypto.util.CryptoUtil;
import cn.hermesdi.crypto.util.EncodingUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 非对称性 加密/解密 实现
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 **/
public class AsymmetryApiCrypto implements ApiCryptoAlgorithm {
    private static final Log logger = LogFactory.getLog(AsymmetryApiCrypto.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApiCryptoConfig apiCryptoConfig;

    private IApiRequestBody iApiRequestBody;

    private IApiResponseBody iApiResponseBody;

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
        AsymmetryCrypto annotation = this.getAnnotation(methodParameter, AsymmetryCrypto.class);
        return !Objects.isNull(annotation);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        AsymmetryCrypto annotation = getAnnotation(methodParameter, AsymmetryCrypto.class);

        ApiCryptoBody apiCryptoBody = this.requestBody(annotation, httpInputMessage, iApiRequestBody, objectMapper, logger);

        if (annotation.verifySignature() && !StringUtils.hasText(apiCryptoBody.getSignStr())) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.PARAM_SIGN_MISSING;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        if (!StringUtils.hasText(apiCryptoBody.getData())) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.PARAM_DATA_MISSING;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        // 密钥
        String privateKey = getPrivateKey(annotation);
        String publicKey = getPublicKey(annotation);

        // 内容编码
        EncodingType contentEncodingType = getContentEncodingType(annotation);
        // 秘钥编码
        EncodingType keyEncodingType = getKeyEncodingType(annotation);


        // 验证签名
        if (annotation.verifySignature()) {
            boolean bo = false;
            try {
                bo = CryptoUtil.resSignatureVerify(
                        annotation.signatureType(),
                        EncodingUtil.decode(contentEncodingType, apiCryptoBody.getData(), apiCryptoConfig.getCharset()),
                        EncodingUtil.decode(contentEncodingType, apiCryptoBody.getSignStr(), apiCryptoConfig.getCharset()),
                        EncodingUtil.decode(keyEncodingType, publicKey, apiCryptoConfig.getCharset())
                );
            } catch (Exception e) {
                ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.VERIFY_SIGNATURE_FAILED;
                logger.error(exceptionType.getMessage() + " ERROR：" + e.getMessage());
                throw new ApiDecodeException(exceptionType);
            }

            if (!bo) {
                ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.VERIFY_SIGNATURE_FAILED;
                logger.error(exceptionType.getMessage());
                throw new ApiDecodeException(exceptionType);
            }
        }

        // 解密
        String encryptData;
        try {
            encryptData = CryptoUtil.asymmetry(
                    annotation.type().getType(),
                    annotation.type().getMethod(),
                    Cipher.DECRYPT_MODE,
                    privateKey,
                    keyEncodingType,
                    apiCryptoBody.getData(),
                    contentEncodingType,
                    apiCryptoConfig.getCharset()
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

        return this.stringToInputStream(encryptData.getBytes(apiCryptoConfig.getCharset()), httpInputMessage.getHeaders(), logger);
    }

    @Override
    public Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        AsymmetryCrypto annotation = getAnnotation(methodParameter, AsymmetryCrypto.class);

        String privateKey = getPrivateKey(annotation);
        String publicKey = getPublicKey(annotation);

        String json = responseBody(body, objectMapper, logger);

        // 内容编码
        EncodingType contentEncodingType = getContentEncodingType(annotation);
        // 秘钥编码
        EncodingType keyEncodingType = getKeyEncodingType(annotation);

        String encryptData;

        try {
            encryptData = CryptoUtil.asymmetry(
                    annotation.type().getType(),
                    annotation.type().getMethod(),
                    Cipher.ENCRYPT_MODE,
                    publicKey,
                    keyEncodingType,
                    json,
                    contentEncodingType,
                    apiCryptoConfig.getCharset()
            );
        } catch (Exception e) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.ENCRYPTION_FAILED;
            logger.error(exceptionType.getMessage() + " ERROR：" + e.getMessage());
            throw new ApiEncryptException(exceptionType);
        }

        // 使用默认响应体
        ApiCryptoBody apiCryptoBody = new ApiCryptoBody().setData(encryptData);

        if (annotation.signature()) {
            byte[] signature = null;
            try {
                signature = CryptoUtil.resSignature(
                        annotation.signatureType(),
                        EncodingUtil.decode(contentEncodingType, apiCryptoBody.getData(), apiCryptoConfig.getCharset()),
                        EncodingUtil.decode(keyEncodingType, privateKey, apiCryptoConfig.getCharset())
                );
            } catch (Exception e) {
                ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.SIGNATURE_FAILED;
                logger.error(exceptionType.getMessage() + " ERROR：" + e.getMessage());
                throw new ApiDecodeException(exceptionType);
            }

            if (Objects.isNull(signature) || signature.length < 1) {
                ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.SIGNATURE_FAILED;
                logger.error(exceptionType.getMessage());
                throw new ApiDecodeException(exceptionType);
            }

            apiCryptoBody.setSignStr(EncodingUtil.encode(contentEncodingType, signature));

        }

        // 使用自定义响应体
        if (iApiResponseBody != null) {
            return iApiResponseBody.responseBody(annotation, apiCryptoBody);
        }

        if (body instanceof String) {
            return responseBody(apiCryptoBody, objectMapper, logger);
        } else {
            return apiCryptoBody;
        }
    }

    private String getPrivateKey(AsymmetryCrypto annotation) {
        String privateKey = StringUtils.hasText(annotation.privateKey()) ? annotation.privateKey() : apiCryptoConfig.getAsymmetry().get(annotation.type().getType()).getPrivateKey();
        if (!StringUtils.hasText(privateKey)) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.NO_PRIVATE_KEY;
            logger.error(exceptionType.getMessage() + " ERROR：(无效的秘钥,请在配置文件 asymmetry 或注解中配置秘钥 privateKey)");
            throw new ApiEncryptException(exceptionType);
        }
        return privateKey;
    }

    private String getPublicKey(AsymmetryCrypto annotation) {
        String publicKey = StringUtils.hasText(annotation.publicKey()) ? annotation.publicKey() : apiCryptoConfig.getAsymmetry().get(annotation.type().getType()).getPublicKey();
        if (!StringUtils.hasText(publicKey)) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.NO_PUBLIC_KEY;
            logger.error(exceptionType.getMessage() + " ERROR：(无效的秘钥,请在配置文件 asymmetry 或注解中配置秘钥 publicKey)");
            throw new ApiEncryptException(exceptionType);
        }
        return publicKey;
    }


    private EncodingType getContentEncodingType(AsymmetryCrypto annotation) {
        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.contentEncodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.contentEncodingType();
        }
        return encodingType;
    }


    private EncodingType getKeyEncodingType(AsymmetryCrypto annotation) {
        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.keyEncodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.keyEncodingType();
        }
        return encodingType;
    }
}
