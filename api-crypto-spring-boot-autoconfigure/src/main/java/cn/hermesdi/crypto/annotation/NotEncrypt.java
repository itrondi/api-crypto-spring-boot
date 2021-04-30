package cn.hermesdi.crypto.annotation;

import java.lang.annotation.*;

/**
 * @Author hermes·di
 * @Date 2020/7/6 0006 11:31
 * @Describe 忽略 加密 注解
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotEncrypt {
}
