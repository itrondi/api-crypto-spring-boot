package cn.hermesdi.crypto.annotation;

import cn.hermesdi.crypto.advice.DecryptRequestBodyAdvice;
import cn.hermesdi.crypto.advice.EncryptResponseBodyAdvice;
import cn.hermesdi.crypto.config.ApiCryptoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author hermes·di
 * @Date 2020/7/8 0008 11:48
 * @Describe 自动装配注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EncryptResponseBodyAdvice.class, DecryptRequestBodyAdvice.class, ApiCryptoConfig.class})
public @interface EnableApiCrypto {
}
