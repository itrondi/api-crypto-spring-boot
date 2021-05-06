package cn.hermesdi.crypto.algorithm;

import cn.hermesdi.crypto.exception.ApiCryptoExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hermesdi.crypto.annotation.encoding.EncodingCrypto;
import cn.hermesdi.crypto.bean.ApiCryptoBody;
import cn.hermesdi.crypto.config.ApiCryptoConfig;
import cn.hermesdi.crypto.constants.EncodingType;
import cn.hermesdi.crypto.exception.ApiDecodeException;
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
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 编码 实现
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
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
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.PARAM_DATA_MISSING;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

        byte[] decode = EncodingUtil.decode(encodingType, apiCryptoBody.getData().getBytes(StandardCharsets.UTF_8));

        return this.stringToInputStream(decode, httpInputMessage.getHeaders(), logger);
    }


    @Override
    public Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        EncodingCrypto annotation = this.getAnnotation(methodParameter, EncodingCrypto.class);

        String json = responseBody(body, objectMapper, logger);

        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

        String encode = EncodingUtil.encode(encodingType, json.getBytes(StandardCharsets.UTF_8));

        ApiCryptoBody apiCryptoBody = new ApiCryptoBody().setData(encode);

        // 使用自定义响应体
        if (iApiResponseBody != null) {
            return iApiResponseBody.responseBody(annotation, apiCryptoBody);
        }

        return apiCryptoBody;
    }
}
