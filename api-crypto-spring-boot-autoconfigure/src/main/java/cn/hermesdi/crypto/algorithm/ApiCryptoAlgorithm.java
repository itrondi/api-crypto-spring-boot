package cn.hermesdi.crypto.algorithm;

import cn.hermesdi.crypto.bean.ApiCryptoBody;
import cn.hermesdi.crypto.exception.ApiDecodeException;
import cn.hermesdi.crypto.ov.IApiRequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @Author hermes·di
 * @Date 2021/4/15 0015 11:14
 * @Describe 加密/解密 实现函数
 */
@FunctionalInterface
public interface ApiCryptoAlgorithm {

    /**
     * 注解判断
     *
     * @param methodParameter   : 执行方法参数
     * @param requestOrResponse : request (true) / Response(false)
     * @return boolean
     * @Author hermes·di
     */
    boolean isCanRealize(MethodParameter methodParameter, boolean requestOrResponse);


    /**
     * 请求前（ 可自定义解密方式）
     *
     * @param httpInputMessage: 请求数据体
     * @param methodParameter:  执行的方法参数
     * @param type:             执行目标类型
     * @param aClass:           消息转换器
     * @return org.springframework.http.HttpInputMessage ：解密后
     * @throws IOException : 异常
     * @Author hermes·di
     */
    default HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        return httpInputMessage;
    }

    /**
     * 响应前（ 可自定义加密方式 ）
     *
     * @param body:               响应体（加密前）
     * @param methodParameter:    执行的方法参数
     * @param mediaType:          交互数据类型
     * @param aClass:             消息转换器
     * @param serverHttpRequest:  请求体
     * @param serverHttpResponse: 响应体
     * @return java.lang.Object : 最终响应体
     * @Author hermes·di
     */
    default Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        return body;
    }


    /**
     * 获取方法或类上指定注解
     *
     * @param methodParameter: 方法参数
     * @param annotationType:  注解类型
     * @return A
     * @Author hermes·di
     * @Date 2021/4/21 0021 10:10
     */
    default <A extends Annotation> A getAnnotation(MethodParameter methodParameter, Class<A> annotationType) {
        return Optional.ofNullable(methodParameter.getMethodAnnotation(annotationType))
                .orElse(methodParameter.getDeclaringClass().getAnnotation(annotationType));
    }

    /**
     * 请求体转JSON
     *
     * @param annotation:       执行注解
     * @param httpInputMessage: 输入
     * @param iApiRequestBody:  自定义解析
     * @param objectMapper:     json
     * @param logger:           日志
     * @return cn.hermesdi.cn.hermesdi.crypto.bean.ApiCryptoBody
     * @Author hermes·di
     * @Date 2021/4/26 0026 21:15
     */
    default ApiCryptoBody requestBody(Annotation annotation, HttpInputMessage httpInputMessage, IApiRequestBody iApiRequestBody, ObjectMapper objectMapper, Log logger) throws IOException {
        ApiCryptoBody apiCryptoBody;
        if (iApiRequestBody != null) {
            // 自定义请求体解析格式
            apiCryptoBody = iApiRequestBody.requestBody(annotation, httpInputMessage.getBody());
        } else {
            try {
                // 默认解析格式
                apiCryptoBody = objectMapper.readValue(httpInputMessage.getBody(), ApiCryptoBody.class);
            } catch (Exception e) {
                logger.error("【ApiCrypto】 Failed to convert the body to JSON. Please check the JSON format of the body.(未能将body转换为JSON。请查看正文的JSON格式)， ERROR：" + e.getMessage());
                throw new ApiDecodeException("【ApiCrypto】 Failed to convert the body to JSON. Please check the JSON format of the body.(未能将body转换为JSON。请查看正文的JSON格式)， ERROR：" + e.getMessage());
            }
        }
        return apiCryptoBody;
    }

}

