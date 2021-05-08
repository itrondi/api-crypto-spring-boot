package cn.hermesdi.crypto.algorithm;

import cn.hermesdi.crypto.annotation.asymmetry.AsymmetryCrypto;
import cn.hermesdi.crypto.config.ApiCryptoConfig;
import cn.hermesdi.crypto.ov.IApiRequestBody;
import cn.hermesdi.crypto.ov.IApiResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.io.IOException;
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
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        return ApiCryptoAlgorithm.super.beforeBodyRead(httpInputMessage, methodParameter, type, aClass);
    }

    @Override
    public Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        return ApiCryptoAlgorithm.super.responseBefore(body, methodParameter, mediaType, aClass, serverHttpRequest, serverHttpResponse);
    }
}
