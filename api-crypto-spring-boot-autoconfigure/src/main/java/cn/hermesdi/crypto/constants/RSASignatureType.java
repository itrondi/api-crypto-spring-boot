package cn.hermesdi.crypto.constants;

/**
 * RSA 签名类型
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public enum RSASignatureType {

    /**
     * MD2withRSA
     */
    MD2withRSA("MD2withRSA"),

    /**
     * MD5withRSA
     */
    MD5withRSA("MD5withRSA"),

    /**
     * SHA1withRSA
     */
    SHA1withRSA("SHA1withRSA"),

    /**
     * SHA224withRSA
     */
    SHA224withRSA("SHA224withRSA"),

    /**
     * SHA256withRSA
     */
    SHA256withRSA("SHA256withRSA"),

    /**
     * SHA384withRSA
     */
    SHA384withRSA("SHA384withRSA"),

    /**
     * SHA512withRSA
     */
    SHA512withRSA("SHA512withRSA");

    /**
     * 描述
     */
    private String type;

    RSASignatureType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}