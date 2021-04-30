package cn.hermesdi.crypto.constants;

/**
 * @Author hermes·di
 * @Date 2021/4/20 0020 18:18
 * @Describe 摘要加密类型
 */
public enum DigestsType {

    /**
     * MD2 加密
     */
    MD2("MD2"),

    /**
     * MD4 加密
     */
    MD4("MD4"),

    /**
     * MD5 加密
     */
    MD5("MD5"),

    /**
     * SHA1 加密
     */
    SHA1("SHA1"),

    /**
     * SHA3 加密
     */
    SHA3("SHA3"),

    /**
     * SHA224 加密
     */
    SHA224("SHA224"),

    /**
     * SHA256 加密
     */
    SHA256("SHA256"),

    /**
     * SHA384 加密
     */
    SHA384("SHA384"),

    /**
     * SHA512 加密
     */
    SHA512("SHA512"),

    /**
     * SHAKE 加密
     */
    SHAKE("SHAKE");

    /**
     * 加密类型
     */
    private final String type;

    DigestsType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
