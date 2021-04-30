package cn.hermesdi.crypto.annotation.asymmetry;

import cn.hermesdi.crypto.annotation.ApiCrypto;
import cn.hermesdi.crypto.constants.AsymmetryType;
import cn.hermesdi.crypto.constants.CryptoType;
import cn.hermesdi.crypto.constants.EncodingType;

import java.lang.annotation.*;

/**
 * @Author hermes·di
 * @Date 2021/4/27 19:51
 * @Describe 非对称性算法注解（RSA）
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
     * @Author hermes·di
     */
    AsymmetryType type() default AsymmetryType.RSA_ECB_PKCS1Padding;

    /**
     * 公钥加密，配置该项时将优先使用
     *
     * @Author hermes·di
     */
    String publicKey() default "";

    /**
     * 私钥解密，配置该项时将优先使用
     *
     * @Author hermes·di
     */
    String privateKey() default "";

    /**
     * 秘钥编码类型
     * <p>
     * 默认为配置文件配置的编码类型
     *
     * @Author hermes·di
     */
    EncodingType keyEncodingType() default EncodingType.DEFAULT;

    /**
     * 内容编码类型
     * <p>
     * 默认为配置文件配置的编码类型
     *
     * @Author hermes·di
     */
    EncodingType contentEncodingType() default EncodingType.DEFAULT;
}
