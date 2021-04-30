package cn.hermesdi.crypto.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hermesdi.crypto.annotation.encoding.EncodingCrypto;
import cn.hermesdi.crypto.bean.ApiCryptoBody;
import cn.hermesdi.crypto.bean.InputMessage;
import cn.hermesdi.crypto.config.ApiCryptoConfig;
import cn.hermesdi.crypto.constants.EncodingType;
import cn.hermesdi.crypto.exception.ApiDecodeException;
import cn.hermesdi.crypto.exception.ApiEncryptException;
import cn.hermesdi.crypto.ov.IApiRequestBody;
import cn.hermesdi.crypto.ov.IApiResponseBody;
import cn.hermesdi.crypto.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Author hermes·di
 * @Date 2021/4/26 15:58
 * @Describe 编码实现
 */
public class EncodingApiCrypto implements ApiCryptoAlgorithm {
    private static final Log logger = LogFactory.getLog(EncodingApiCrypto.class);


    private final ObjectMapper objectMapper;

    private final IApiRequestBody iApiRequestBody;

    private final IApiResponseBody iApiResponseBody;

    private final ApiCryptoConfig apiCryptoConfig;

    public EncodingApiCrypto(ApiCryptoConfig apiCryptoConfig, ObjectMapper objectMapper, IApiRequestBody iApiRequestBody, IApiResponseBody iApiResponseBody) {
        this.apiCryptoConfig = apiCryptoConfig;
        this.objectMapper = objectMapper;
        this.iApiRequestBody = iApiRequestBody;
        this.iApiResponseBody = iApiResponseBody;
    }

    @Override
    public boolean isCanRealize(MethodParameter methodParameter, boolean requestOrResponse) {
        EncodingCrypto annotation = this.getAnnotation(methodParameter, EncodingCrypto.class);
        return !Objects.isNull(annotation);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        EncodingCrypto annotation = this.getAnnotation(methodParameter, EncodingCrypto.class);

        ApiCryptoBody apiCryptoBody = this.requestBody(annotation, httpInputMessage, iApiRequestBody, objectMapper, logger);

        if (!StringUtils.hasText(apiCryptoBody.getData())) {
            logger.error("【ApiCrypto】 Missing required parameters.(缺少必需的参数)");
            throw new ApiDecodeException("【ApiCrypto】 Missing required parameters.(缺少必需的参数)");
        }

        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

        byte[] decode = EncodingUtil.decode(encodingType, apiCryptoBody.getData().getBytes(StandardCharsets.UTF_8));

        try {
            assert decode != null;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(decode);
            return new InputMessage(inputStream, httpInputMessage.getHeaders());
        } catch (Exception e) {
            logger.error("【ApiCrypto】 Failed to convert the encoded string to the input stream.（未能将已编码的字符串转换为输入流）， ERROR：" + e.getMessage());
            throw new ApiDecodeException("【ApiCrypto】 Failed to convert the encoded string to the input stream.（未能将已编码的字符串转换为输入流）， ERROR：" + e.getMessage());
        }
    }


    @Override
    public Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        EncodingCrypto annotation = this.getAnnotation(methodParameter, EncodingCrypto.class);

        // 转成json字符串
        String data;

        try {
            data = objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            logger.error("【ApiCrypto】 The response body conversion to JSON failed. Please respond to data correctly.(响应body转换JSON失败，请正确响应数据)， ERROR：" + e.getMessage());
            throw new ApiEncryptException("【ApiCrypto】 The response body conversion to JSON failed. Please respond to data correctly.(响应body转换JSON失败，请正确响应数据)， ERROR：" + e.getMessage());
        }


        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

        String encode = EncodingUtil.encode(encodingType, data.getBytes(StandardCharsets.UTF_8));

        ApiCryptoBody apiCryptoBody = new ApiCryptoBody().setData(encode);

        // 使用自定义响应体
        if (iApiResponseBody != null) {
            return iApiResponseBody.responseBody(annotation, apiCryptoBody);
        }

        return apiCryptoBody;
    }
}
