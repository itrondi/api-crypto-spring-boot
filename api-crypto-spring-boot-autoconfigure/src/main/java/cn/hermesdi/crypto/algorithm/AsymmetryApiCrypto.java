package cn.hermesdi.crypto.algorithm;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 非对称性 加密/解密 实现
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 **/
public class AsymmetryApiCrypto implements ApiCryptoAlgorithm {
    @Override
    public boolean isCanRealize(MethodParameter methodParameter, boolean requestOrResponse) {
        return false;
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
