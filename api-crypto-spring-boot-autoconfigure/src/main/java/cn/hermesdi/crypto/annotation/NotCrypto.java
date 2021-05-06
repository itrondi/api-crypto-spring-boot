package cn.hermesdi.crypto.annotation;

import java.lang.annotation.*;

/**
 * 忽略 解密/解密
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@NotDecrypt
@NotEncrypt
public @interface NotCrypto {
}
