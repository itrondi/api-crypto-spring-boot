package cn.hermesdi.crypto.annotation.symmetric;

import cn.hermesdi.crypto.annotation.ApiCrypto;
import cn.hermesdi.crypto.constants.CryptoType;
import cn.hermesdi.crypto.constants.EncodingType;
import cn.hermesdi.crypto.constants.SymmetricType;

import java.lang.annotation.*;

/**
 * @Author hermes·di
 * @Date 2021/4/20 0020 18:37
 * @Describe 对称性算法注解 AES，DES，DESede(3DES)
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCrypto(encryptType = CryptoType.SYMMETRIC, decryptType = CryptoType.SYMMETRIC)
public @interface SymmetricCrypto {

    /**
     * 加密/解密类型
     *
     * @Author hermes·di
     */
    SymmetricType type() default SymmetricType.AES_ECB_PKCS5_PADDING;

    /**
     * 自定义加密 秘钥（优先）
     *
     * @Author hermes·di
     */
    String SecretKey() default "";

    /**
     * 编码类型
     * <p>
     * 默认为配置文件配置的编码类型
     *
     * @Author hermes·di
     */
    EncodingType encodingType() default EncodingType.DEFAULT;
}
