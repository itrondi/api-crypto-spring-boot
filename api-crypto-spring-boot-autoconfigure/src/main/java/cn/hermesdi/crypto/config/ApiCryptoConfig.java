package cn.hermesdi.crypto.config;

import cn.hermesdi.crypto.constants.EncodingType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author hermes·di
 * @Date 2020/7/6 0006 11:42
 * @Describe 配置类
 */
@Configuration
@ConfigurationProperties(prefix = "api.cn.hermesdi.crypto")
public class ApiCryptoConfig {
    /**
     * 配置对称性密钥
     */
    private Map<String, String> symmetricKey = new HashMap<>();
    /**
     * 配置数据签名
     */
    private Signature signature = new Signature();

    /**
     * 配置全局编码类型
     */
    private EncodingType encodingType = EncodingType.BASE64;


    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    public static class Signature {
        /**
         * 配置验证签名的超时时间(秒),小于1无效(默认).
         */
        private Long timeout = 0L;
        /**
         * 配置签名的密钥
         */
        private String secretKey;

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public Long getTimeout() {
            return timeout;
        }

        public void setTimeout(Long timeout) {
            this.timeout = timeout;
        }
    }


    public EncodingType getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(EncodingType encodingType) {
        this.encodingType = encodingType;
    }

    public Map<String, String> getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(Map<String, String> symmetricKey) {
        this.symmetricKey = symmetricKey;
    }
}
