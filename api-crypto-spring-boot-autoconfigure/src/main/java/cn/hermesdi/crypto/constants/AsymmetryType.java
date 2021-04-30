package cn.hermesdi.crypto.constants;

/**
 * @Author hermes·di
 * @Date 2021/4/27 19:58
 * @Describe 非对称性 加密/解密 类型
 */
public enum AsymmetryType {

    /**
     * ECB 模式
     */
    RSA_ECB_NoPadding("RSA", "RSA/ECB/NoPadding"),
    RSA_ECB_PKCS1Padding("RSA", "RSA/ECB/PKCS1Padding"),

    /**
     * NONE 模式
     */
    RSA_NONE_NoPadding("RSA", "RSA/None/NoPadding"),
    RSA_NONE_PKCS1Padding("RSA", "RSA/None/PKCS1Padding");

    private final String type;
    private final String method;

    AsymmetryType(String type, String method) {
        this.type = type;
        this.method = method;
    }

    public String getType() {
        return type;
    }

    public String getMethod() {
        return method;
    }
}
