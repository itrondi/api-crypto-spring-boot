package cn.hermesdi.crypto.annotation.encoding;

import cn.hermesdi.crypto.annotation.ApiCrypto;
import cn.hermesdi.crypto.constants.CryptoType;
import cn.hermesdi.crypto.constants.EncodingType;

import java.lang.annotation.*;

/**
 * 编码注解
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCrypto(encryptType = CryptoType.ENCODING, decryptType = CryptoType.ENCODING)
public @interface EncodingCrypto {

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
