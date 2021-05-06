package cn.hermesdi.crypto.annotation.asymmetry;

import cn.hermesdi.crypto.annotation.ApiCrypto;
import cn.hermesdi.crypto.constants.AsymmetryType;
import cn.hermesdi.crypto.constants.CryptoType;
import cn.hermesdi.crypto.constants.EncodingType;

import java.lang.annotation.*;

/**
 * 非对称性算法注解（RSA）
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCrypto(encryptType = CryptoType.ASYMMETRY, decryptType = CryptoType.ASYMMETRY)
public @interface AsymmetryCrypto {


    /**
     * 加密/解密类型
     *
     * @return cn.hermesdi.crypto.constants.AsymmetryType 非对称性 加密/解密 类型枚举
     * @author hermes-di
     **/
    AsymmetryType type() default AsymmetryType.RSA_ECB_PKCS1Padding;

    /**
     * 公钥加密，配置该项时将优先使用
     *
     * @return java.lang.String 字符串
     * @author hermes-di
     **/
    String publicKey() default "";

    /**
     * 私钥解密，配置该项时将优先使用
     *
     * @return java.lang.String 字符串
     * @author hermes-di
     **/
    String privateKey() default "";

    /**
     * 秘钥编码类型
     * <p>
     * 默认为配置文件配置的编码类型
     *
     * @return cn.hermesdi.crypto.constants.EncodingType 编码 类型枚举
     * @author hermes-di
     **/
    EncodingType keyEncodingType() default EncodingType.DEFAULT;

    /**
     * 内容编码类型
     * <p>
     * 默认为配置文件配置的编码类型
     *
     * @return cn.hermesdi.crypto.constants.EncodingType 编码 类型枚举
     * @author hermes-di
     **/
    EncodingType contentEncodingType() default EncodingType.DEFAULT;
}
