package cn.hermesdi.crypto.annotation.signature;

import cn.hermesdi.crypto.annotation.ApiCrypto;
import cn.hermesdi.crypto.constants.CryptoType;

import java.lang.annotation.*;

/**
 * @Author hermes·di
 * @Date 2021/4/20 0020 18:37
 * @Describe 签名注解
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCrypto(encryptType = CryptoType.SIGNATURE, decryptType = CryptoType.SIGNATURE)
public @interface SignatureCrypto {


    /**
     * 自定义超时时间 （优先）
     * <p>
     * 小于等于 "0" 不限制
     *
     * @Author hermes·di
     */
    long timeout() default 0L;

    /**
     * 自定义签名 秘钥（优先）
     *
     * @Author hermes·di
     */
    String SecretKey() default "";
}
