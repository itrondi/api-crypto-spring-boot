package cn.hermesdi.crypto.annotation.digests;

import cn.hermesdi.crypto.annotation.ApiCrypto;
import cn.hermesdi.crypto.constants.CryptoType;
import cn.hermesdi.crypto.constants.DigestsType;
import cn.hermesdi.crypto.constants.EncodingType;

import java.lang.annotation.*;

/**
 * 摘要算法注解（MD、SHA）
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCrypto(encryptType = CryptoType.DIGEST, decryptType = CryptoType.DIGEST)
public @interface DigestsCrypto {

    /**
     * 摘要加密类型
     *
     * @return cn.hermesdi.crypto.constants.DigestsType 摘要加密 类型枚举
     * @author hermes-di
     **/
    DigestsType type() default DigestsType.MD5;

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
