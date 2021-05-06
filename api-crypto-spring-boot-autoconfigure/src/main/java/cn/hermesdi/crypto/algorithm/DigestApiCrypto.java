package cn.hermesdi.crypto.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hermesdi.crypto.annotation.digests.DigestsCrypto;
import cn.hermesdi.crypto.bean.ApiCryptoBody;
import cn.hermesdi.crypto.ov.IApiResponseBody;
import cn.hermesdi.crypto.util.CryptoUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.*;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.util.Objects;

/**
 * 摘要加密 实现
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public class DigestApiCrypto implements ApiCryptoAlgorithm {
    private static final Log logger = LogFactory.getLog(DigestApiCrypto.class);

    @Autowired
    private ObjectMapper objectMapper;

    private IApiResponseBody iApiResponseBody;

    public DigestApiCrypto() {
    }

    public DigestApiCrypto(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public DigestApiCrypto(ObjectMapper objectMapper, IApiResponseBody iApiResponseBody) {
        this.objectMapper = objectMapper;
        this.iApiResponseBody = iApiResponseBody;
    }

    public DigestApiCrypto(IApiResponseBody iApiResponseBody) {
        this.iApiResponseBody = iApiResponseBody;
    }

    @Override
    public boolean isCanRealize(MethodParameter methodParameter, boolean requestOrResponse) {

        DigestsCrypto annotation = this.getAnnotation(methodParameter, DigestsCrypto.class);

        if (Objects.isNull(annotation)) {
            return false;
        }

        return !requestOrResponse;
    }

    @Override
    public Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        DigestsCrypto annotation = this.getAnnotation(methodParameter, DigestsCrypto.class);

        String json = responseBody(body, objectMapper, logger);

        Digest digest;

        if (Objects.isNull(annotation)) {
            digest = new MD5Digest();
        } else {
            switch (annotation.type()) {
                case MD2:
                    digest = new MD2Digest();
                    break;
                case MD4:
                    digest = new MD4Digest();
                    break;
                case SHA1:
                    digest = new SHA1Digest();
                    break;
                case SHA3:
                    digest = new SHA3Digest();
                    break;
                case SHA224:
                    digest = new SHA224Digest();
                    break;
                case SHA256:
                    digest = new SHA256Digest();
                    break;
                case SHA384:
                    digest = new SHA384Digest();
                    break;
                case SHA512:
                    digest = new SHA512Digest();
                    break;
                case SHAKE:
                    digest = new SHAKEDigest();
                    break;
                default:
                    digest = new MD5Digest();
            }
        }

        byte[] bytes = CryptoUtil.digest(digest, json);
        json = Hex.toHexString(bytes);

        ApiCryptoBody apiCryptoBody = new ApiCryptoBody().setData(json);

        // 使用自定义响应体
        if (iApiResponseBody != null) {
            return iApiResponseBody.responseBody(annotation, apiCryptoBody);
        }

        // 使用默认响应体
        return this.responseBody(apiCryptoBody, objectMapper, logger);
    }
}
