package cn.hermesdi.crypto.config;

import cn.hermesdi.crypto.constants.EncodingType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置类
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
@Configuration
@ConfigurationProperties(prefix = "api.crypto")
public class ApiCryptoConfig {
    /**
     * 配置对称性密钥
     */
    private Map<String, String> symmetric = new HashMap<>();

    /**
     * 配置非对称性密钥
     */
    private Map<String, AsymmetryKey> asymmetry = new HashMap<>();

    /**
     * 配置数据签名
     */
    private Signature signature = new Signature();

    /**
     * 配置全局加密解密编码类型
     */
    private EncodingType encodingType = EncodingType.BASE64;

    /**
     * 配置全局加密解密处理字符集
     */
    private Charset charset = StandardCharsets.UTF_8;


    public static class AsymmetryKey {
        /**
         * 非对称性 公钥
         */
        private String publicKey;

        /**
         * 非对称性 私钥
         */
        private String privateKey;

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }
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


    public Map<String, AsymmetryKey> getAsymmetry() {
        return asymmetry;
    }

    public void setAsymmetry(Map<String, AsymmetryKey> asymmetry) {
        this.asymmetry = asymmetry;
    }

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public EncodingType getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(EncodingType encodingType) {
        this.encodingType = encodingType;
    }

    public Map<String, String> getSymmetric() {
        return symmetric;
    }

    public void setSymmetric(Map<String, String> symmetric) {
        this.symmetric = symmetric;
    }
}
