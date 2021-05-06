package cn.hermesdi.crypto.ov;

import cn.hermesdi.crypto.bean.ApiCryptoBody;

import java.lang.annotation.Annotation;

/**
 * 自定义加密数据响应格式接口，实现该接口重写 responseBody 方法自定义返回体
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
@FunctionalInterface
public interface IApiResponseBody {

    /**
     * 自定义加密数据响应体格式
     *
     * @param annotation: 执行注解
     * @param cryptoBody: 响应数据
     * @return java.lang.Object 响应体
     * @Author hermes·di
     */
    Object responseBody(Annotation annotation, ApiCryptoBody cryptoBody);
}
