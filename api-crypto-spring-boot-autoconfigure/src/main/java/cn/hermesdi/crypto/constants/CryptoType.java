package cn.hermesdi.crypto.constants;

/**
 * 加密解密 类型枚举（用于描述）
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public enum CryptoType {

    /**
     * 签名
     */
    SIGNATURE("SIGNATURE", true, true),

    /**
     * DIGEST（摘要），支持 MD、SHA
     */
    DIGEST("DIGEST", false, false),

    /**
     * SYMMETRIC（对称性），支持 AES、DES
     */
    SYMMETRIC("SYMMETRIC", true, true),

    /**
     * ASYMMETRY（非对称性） 支持 RSA
     */
    ASYMMETRY("ASYMMETRY", true, true),

    /**
     * ENCODING(编码)
     */
    ENCODING("ENCODING", false, true),

    /**
     * 无效 加密/解密
     */
    DISABLED("禁用", false, false);


    /**
     * 算法名称
     */
    private String name;

    /**
     * 是否需要秘钥
     */
    private boolean requireKey;

    /**
     * 是否支持解密
     */
    private boolean canDecrypt;


    CryptoType(String name, boolean requireKey, boolean canDecrypt) {
    }

    CryptoType() {
    }

    public String getName() {
        return name;
    }

    public boolean isRequireKey() {
        return requireKey;
    }

    public boolean isCanDecrypt() {
        return canDecrypt;
    }
}

