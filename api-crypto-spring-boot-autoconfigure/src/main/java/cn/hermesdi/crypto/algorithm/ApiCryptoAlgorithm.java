package cn.hermesdi.crypto.algorithm;

import cn.hermesdi.crypto.bean.ApiCryptoBody;
import cn.hermesdi.crypto.bean.InputMessage;
import cn.hermesdi.crypto.exception.ApiCryptoExceptionType;
import cn.hermesdi.crypto.exception.ApiDecodeException;
import cn.hermesdi.crypto.exception.ApiEncryptException;
import cn.hermesdi.crypto.ov.IApiRequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * 加密/解密 实现函数
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public interface ApiCryptoAlgorithm {

    /**
     * 注解判断
     *
     * @param methodParameter:   执行方法参数
     * @param requestOrResponse: request (true) / Response(false)
     * @return boolean
     * @author hermes-di
     **/
    boolean isCanRealize(MethodParameter methodParameter, boolean requestOrResponse);

    /**
     * 请求前（ 可自定义解密方式）
     *
     * @param httpInputMessage: 请求数据体
     * @param methodParameter:  执行的方法参数
     * @param type:             执行目标类型
     * @param aClass:           消息转换器
     * @return org.springframework.http.HttpInputMessage
     * @throws IOException: 可能会出现 IO 异常
     * @author hermes-di
     **/
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
     * @author hermes-di
     */
    default Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        return body;
    }


    /**
     * 获取方法或类上指定注解
     *
     * @param methodParameter: 方法参数
     * @param annotationType:  注解类型
     * @return A 注解类型
     * @author hermes-di
     */
    default <A extends Annotation> A getAnnotation(MethodParameter methodParameter, Class<A> annotationType) {
        return Optional.ofNullable(methodParameter.getMethodAnnotation(annotationType))
                .orElse(methodParameter.getDeclaringClass().getAnnotation(annotationType));
    }

    /**
     * 字符数组转换为输入流
     *
     * @param bytes:       字符串内容
     * @param httpHeaders: 请求头
     * @param logger:      日志
     * @return cn.hermesdi.crypto.bean.InputMessage
     * @author hermes-di
     **/
    default InputMessage stringToInputStream(byte[] bytes, HttpHeaders httpHeaders, Log logger) {
        try {
            assert bytes != null;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            return new InputMessage(inputStream, httpHeaders);
        } catch (Exception e) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.STRING_TO_INPUT_STREAM;
            logger.error(exceptionType.getMessage() + " ERROR：" + e.getMessage());
            throw new ApiDecodeException(exceptionType);
        }
    }

    /**
     * 请求体解析
     *
     * @param annotation:       执行注解
     * @param httpInputMessage: 输入
     * @param iApiRequestBody:  自定义解析
     * @param objectMapper:     json
     * @param logger:           日志
     * @return cn.hermesdi.crypto.bean.ApiCryptoBody
     * @throws ApiDecodeException: 解析失败出现异常
     * @author hermes-di
     **/
    default ApiCryptoBody requestBody(Annotation annotation, HttpInputMessage httpInputMessage, IApiRequestBody iApiRequestBody, ObjectMapper objectMapper, Log logger) {
        ApiCryptoBody apiCryptoBody;
        try {
            if (iApiRequestBody != null) {
                // 自定义请求体解析格式
                apiCryptoBody = iApiRequestBody.requestBody(annotation, httpInputMessage.getBody());
            } else {
                // 默认解析格式
                apiCryptoBody = objectMapper.readValue(httpInputMessage.getBody(), ApiCryptoBody.class);
            }
        } catch (Exception e) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.REQUEST_TO_BEAN;
            logger.error(exceptionType.getMessage() + " ERROR：" + e.getMessage());
            throw new ApiDecodeException(exceptionType);
        }
        return apiCryptoBody;
    }

    /**
     * 响应体解析
     *
     * @param body:         响应对象
     * @param objectMapper: json
     * @param logger:       日志
     * @return java.lang.String
     * @throws ApiEncryptException: 转JSON出现异常
     * @author hermes-di
     **/
    default String responseBody(Object body, ObjectMapper objectMapper, Log logger) {
        // 转成json字符串
        String json;
        try {
            json = objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.RESPONSE_TO_JSON;
            logger.error(exceptionType.getMessage() + " ERROR：" + e.getMessage());
            throw new ApiEncryptException(exceptionType);
        }
        return json;
    }

}

