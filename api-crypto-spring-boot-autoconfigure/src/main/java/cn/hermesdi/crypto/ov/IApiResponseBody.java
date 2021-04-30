package cn.hermesdi.crypto.ov;

import cn.hermesdi.crypto.bean.ApiCryptoBody;

import java.lang.annotation.Annotation;

/**
 * @Author hermes·di
 * @Date 2020/7/7 0007 13:39
 * @Describe 自定义加密数据响应格式接口，实现该接口重写 responseBody 方法自定义返回体
 */
@FunctionalInterface
public interface IApiResponseBody {

    /**
     * 自定义加密数据响应体格式
     *
     * @param annotation: 执行注解
     * @param cryptoBody: 响应数据
     * @return java.lang.Object
     * @Author hermes·di
     */
    Object responseBody(Annotation annotation, ApiCryptoBody cryptoBody);
}
