package cn.hermesdi.crypto.constants;

/**
 * @Author hermes·di
 * @Date 2021/4/20 0020 18:39
 * @Describe 对称性 加密/解密 类型
 */
public enum SymmetricType {

    // ---------------------------------------- AES:秘钥要求：128/192/256 bits ---------------------------------------------

    AES_ECB_PKCS7_PADDING("AES", "AES/ECB/PKCS7Padding", false, 0),
    AES_ECB_PKCS5_PADDING("AES", "AES/ECB/PKCS5Padding", false, 0),

    /**
     * CBC 模式 ,会产生偏移量 iv
     */
    AES_CBC_PKCS7_PADDING("AES", "AES/CBC/PKCS7Padding", true, 16),
    AES_CBC_PKCS5_PADDING("AES", "AES/CBC/PKCS5Padding", true, 16),

    /**
     * CTR 模式 ,会产生偏移量 iv
     */
    AES_CTR_PKCS7_PADDING("AES", "AES/CTR/PKCS7Padding", true, 16),
    AES_CTR_PKCS5_PADDING("AES", "AES/CTR/PKCS5Padding", true, 16),

    /**
     * OFB 模式 ,会产生偏移量 iv
     */
    AES_OFB_PKCS7_PADDING("AES", "AES/OFB/PKCS7Padding", true, 16),
    AES_OFB_PKCS5_PADDING("AES", "AES/OFB/PKCS5Padding", true, 16),

    /**
     * CFB 模式 ,会产生偏移量 iv
     */
    AES_CFB_PKCS7_PADDING("AES", "AES/CFB/PKCS7Padding", true, 16),
    AES_CFB_PKCS5_PADDING("AES", "AES/CFB/PKCS5Padding", true, 16),

    // ---------------------------------------- DES : 秘钥要求：64 bits---------------------------------------------

    DES_ECB_PKCS7_PADDING("DES", "DES/ECB/PKCS7Padding", false, 0),
    DES_ECB_PKCS5_PADDING("DES", "DES/ECB/PKCS5Padding", false, 0),

    /**
     * CBC 模式 ,会产生偏移量 iv
     */
    DES_CBC_PKCS7_PADDING("DES", "DES/CBC/PKCS7Padding", true, 8),
    DES_CBC_PKCS5_PADDING("DES", "DES/CBC/PKCS5Padding", true, 8),

    /**
     * CTR 模式 ,会产生偏移量 iv
     */
    DES_CTR_PKCS7_PADDING("DES", "DES/CTR/PKCS7Padding", true, 8),
    DES_CTR_PKCS5_PADDING("DES", "DES/CTR/PKCS5Padding", true, 8),

    /**
     * OFB 模式 ,会产生偏移量 iv
     */
    DES_OFB_PKCS7_PADDING("DES", "DES/OFB/PKCS7Padding", true, 8),
    DES_OFB_PKCS5_PADDING("DES", "DES/OFB/PKCS5Padding", true, 8),

    /**
     * CFB 模式 ,会产生偏移量 iv
     */
    DES_CFB_PKCS7_PADDING("DES", "DES/CFB/PKCS7Padding", true, 8),
    DES_CFB_PKCS5_PADDING("DES", "DES/CFB/PKCS5Padding", true, 8),

    // ---------------------------------------- DESede(3DES) ：秘钥要求：128/192 bits ---------------------------------------------

    DESede_ECB_PKCS7_PADDING("DESede", "DESede/ECB/PKCS7Padding", false, 0),
    DESede_ECB_PKCS5_PADDING("DESede", "DESede/ECB/PKCS5Padding", false, 0),

    /**
     * CBC 模式 ,会产生偏移量 iv
     */
    DESede_CBC_PKCS7_PADDING("DESede", "DESede/CBC/PKCS7Padding", true, 8),
    DESede_CBC_PKCS5_PADDING("DESede", "DESede/CBC/PKCS5Padding", true, 8),

    /**
     * CTR 模式 ,会产生偏移量 iv
     */
    DESede_CTR_PKCS7_PADDING("DESede", "DESede/CTR/PKCS7Padding", true, 8),
    DESede_CTR_PKCS5_PADDING("DESede", "DESede/CTR/PKCS5Padding", true, 8),

    /**
     * OFB 模式 ,会产生偏移量 iv
     */
    DESede_OFB_PKCS7_PADDING("DESede", "DESede/OFB/PKCS7Padding", true, 8),
    DESede_OFB_PKCS5_PADDING("DESede", "DESede/OFB/PKCS5Padding", true, 8),

    /**
     * CFB 模式 ,会产生偏移量 iv
     */
    DESede_CFB_PKCS7_PADDING("DESede", "DESede/CFB/PKCS7Padding", true, 8),
    DESede_CFB_PKCS5_PADDING("DESede", "DESede/CFB/PKCS5Padding", true, 8);

    /**
     * 加密类型
     */
    private final String type;

    /**
     * 工作模式/填充方式
     */
    private final String method;

    /**
     * 是否产生偏移量
     */
    private final boolean produceIv;

    /**
     * iv 长度
     */
    private final int ivLength;

    SymmetricType(String type, String method, boolean produceIv, int ivLength) {
        this.type = type;
        this.method = method;
        this.produceIv = produceIv;
        this.ivLength = ivLength;
    }

    public String getMethod() {
        return method;
    }

    public String getType() {
        return type;
    }

    public boolean isProduceIv() {
        return produceIv;
    }

    public int getIvLength() {
        return ivLength;
    }
}

