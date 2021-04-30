package cn.hermesdi.crypto.ov;


import cn.hermesdi.crypto.bean.ApiCryptoBody;

import java.io.InputStream;
import java.lang.annotation.Annotation;

/**
 * @Author hermes·di
 * @Date 2020/7/7 0007 13:39
 * @Describe 自定义前端请求体格式化接口，实现该接口重写  requestBody 方法自定义解析 body
 */
@FunctionalInterface
public interface IApiRequestBody {

    /**
     * 请求 body 自定义解析
     *
     * @param annotation:  执行注解
     * @param inputStream: 前端请求的 inputStream
     * @Author hermes·di
     */
    ApiCryptoBody requestBody(Annotation annotation, InputStream inputStream);
}
