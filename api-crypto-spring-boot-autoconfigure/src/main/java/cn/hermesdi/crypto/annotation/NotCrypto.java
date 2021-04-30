package cn.hermesdi.crypto.annotation;

import java.lang.annotation.*;

/**
 * @Author hermes·di
 * @Date 2020/7/6 0006 11:31
 * @Describe 忽略 解密/解密
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@NotDecrypt
@NotEncrypt
public @interface NotCrypto {
}
