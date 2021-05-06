package cn.hermesdi.crypto.annotation.symmetric;

import cn.hermesdi.crypto.annotation.ApiCrypto;
import cn.hermesdi.crypto.constants.CryptoType;
import cn.hermesdi.crypto.constants.EncodingType;
import cn.hermesdi.crypto.constants.SymmetricType;

import java.lang.annotation.*;

/**
 * 对称性算法注解 AES，DES，DESede(3DES) 等
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCrypto(encryptType = CryptoType.SYMMETRIC, decryptType = CryptoType.SYMMETRIC)
public @interface SymmetricCrypto {

    /**
     * 加密/解密类型
     * <p>
     * 默认 AES_ECB_PKCS5_PADDING
     *
     * @return cn.hermesdi.crypto.constants.SymmetricType 对称性 加密/解密 类型枚举
     * @author hermes-di
     **/
    SymmetricType type() default SymmetricType.AES_ECB_PKCS5_PADDING;

    /**
     * 自定义加密 秘钥（优先）
     *
     * @return java.lang.String 字符串
     * @author hermes-di
     **/
    String SecretKey() default "";

    /**
     * 编码类型
     * <p>
     * 默认为配置文件配置的编码类型
     *
     * @return cn.hermesdi.crypto.constants.EncodingType 编码 类型枚举
     * @author hermes-di
     **/
    EncodingType encodingType() default EncodingType.DEFAULT;
}
