package cn.hermesdi.crypto.constants;

/**
 * @Author hermes·di
 * @Date 2021/4/26 11:30
 * @Describe 编码类型枚举
 */
public enum EncodingType {

    /**
     * base64编码
     */
    BASE64("base64"),

    /**
     * Url Base64
     */
    URL_BASE64("UrlBase64"),

    /**
     * 十六进制编码
     */
    HEX("hexadecimal"),

    /**
     * 使用默认配置的编码类型
     */
    DEFAULT("default config"),

    /**
     * 无需编码
     */
    NONE("none");

    /**
     * 描述
     */
    private String name;

    EncodingType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
