package cn.hermesdi.crypto.annotation.encoding;

import cn.hermesdi.crypto.annotation.ApiCrypto;
import cn.hermesdi.crypto.constants.CryptoType;
import cn.hermesdi.crypto.constants.EncodingType;

import java.lang.annotation.*;

/**
 * @Author hermes·di
 * @Date 2021/4/26 10:54
 * @Describe 编码注解
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
     * @Author hermes·di
     */
    EncodingType encodingType() default EncodingType.DEFAULT;
}
