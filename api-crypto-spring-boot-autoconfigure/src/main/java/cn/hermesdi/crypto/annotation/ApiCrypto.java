package cn.hermesdi.crypto.annotation;

import cn.hermesdi.crypto.constants.CryptoType;

import java.lang.annotation.*;

/**
 * @Author hermes·di
 * @Date 2021/4/15 0015 17:01
 * @Describe 加密、解密 标记注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface ApiCrypto {

    /**
     * 加密 类型（枚举）
     *
     * @Author hermes·di
     */
    CryptoType encryptType();


    /**
     * 解密 类型（枚举）
     *
     * @Author hermes·di
     */
    CryptoType decryptType();
}
