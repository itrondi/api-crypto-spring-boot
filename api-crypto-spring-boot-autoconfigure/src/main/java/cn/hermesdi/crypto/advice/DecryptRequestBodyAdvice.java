package cn.hermesdi.crypto.advice;

import cn.hermesdi.crypto.algorithm.ApiCryptoAlgorithm;
import cn.hermesdi.crypto.annotation.NotDecrypt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * @Author hermes·di
 * @Date 2020/7/6 0006 11:41
 * @Describe 请求解密类
 */
@ControllerAdvice
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

    private static final Log logger = LogFactory.getLog(DecryptRequestBodyAdvice.class);

    @Autowired(required = false)
    private List<ApiCryptoAlgorithm> apiCryptoAlgorithms;

    private ApiCryptoAlgorithm apiCryptoAlgorithm;

    @Override
    public boolean supports(MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 方法上有排除解密注解
        if (parameter.hasMethodAnnotation(NotDecrypt.class)) {
            return false;
        }

        if (Objects.nonNull(apiCryptoAlgorithms)) {
            logger.debug("【ApiCrypto】 all Decrypt Algorithm : [" + apiCryptoAlgorithms + "]");

            for (ApiCryptoAlgorithm a : apiCryptoAlgorithms) {
                if (a.isCanRealize(parameter, false)) {
                    apiCryptoAlgorithm = a;
                    return true;
                }
            }
        } else {
            logger.debug("【ApiCrypto】 no Decrypt Algorithm.( 没有可用的 ApiCryptoAlgorithm 实现 )");
        }
        return false;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return apiCryptoAlgorithm.beforeBodyRead(inputMessage, parameter, targetType, converterType);
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
