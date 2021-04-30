package cn.hermesdi.crypto.annotation.digests;

import cn.hermesdi.crypto.annotation.ApiCrypto;
import cn.hermesdi.crypto.constants.CryptoType;
import cn.hermesdi.crypto.constants.DigestsType;

import java.lang.annotation.*;

/**
 * @Author hermes·di
 * @Date 2021/4/20 0020 18:17
 * @Describe 摘要算法注解（MD、SHA）
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCrypto(encryptType = CryptoType.DIGEST, decryptType = CryptoType.DIGEST)
public @interface DigestsCrypto {

    /**
     * 加密类型
     *
     * @Author hermes·di
     */
    DigestsType type() default DigestsType.MD5;
}
