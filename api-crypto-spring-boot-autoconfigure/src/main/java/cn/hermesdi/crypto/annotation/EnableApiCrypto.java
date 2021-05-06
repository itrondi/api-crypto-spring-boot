package cn.hermesdi.crypto.annotation;

import cn.hermesdi.crypto.advice.DecryptRequestBodyAdvice;
import cn.hermesdi.crypto.advice.EncryptResponseBodyAdvice;
import cn.hermesdi.crypto.config.ApiCryptoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * ApiCrypto 自动装配注解
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EncryptResponseBodyAdvice.class, DecryptRequestBodyAdvice.class, ApiCryptoConfig.class})
public @interface EnableApiCrypto {
}
