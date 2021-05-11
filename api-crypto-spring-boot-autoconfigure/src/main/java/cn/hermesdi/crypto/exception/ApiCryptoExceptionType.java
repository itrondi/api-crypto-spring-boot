package cn.hermesdi.crypto.exception;

/**
 * 异常类型枚举
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public enum ApiCryptoExceptionType {
    /**
     * 请求体转 ApiCryptoBody 失败，请检查请求JSON格式是否正确.
     */
    REQUEST_TO_BEAN("【ApiCrypto】 请求内容转 ApiCryptoBody 失败，请检查请求JSON格式是否正确."),

    /**
     * 字符串转换为输入流失败
     */
    STRING_TO_INPUT_STREAM("【ApiCrypto】 字符串转换为输入流失败."),

    /**
     * 响应内容转 JSON 字符串出现异常
     */
    RESPONSE_TO_JSON("【ApiCrypto】 响应内容转 JSON 字符串出现异常."),

    /**
     * 缺少必要的参数 vi
     */
    PARAM_VI_MISSING("【ApiCrypto】 缺少必要的参数 \"vi\"，请检查JSON字段."),

    /**
     * 缺少必要的参数 data
     */
    PARAM_DATA_MISSING("【ApiCrypto】 缺少必要的参数 \"data\"，请检查JSON字段."),

    /**
     * 缺少必要的参数 nonce
     */
    PARAM_NONCE_MISSING("【ApiCrypto】 缺少必要的参数 \"nonce\"，请检查JSON字段."),

    /**
     * 缺少必要的参数 timestamp
     */
    PARAM_TIMESTAMP_MISSING("【ApiCrypto】 缺少必要的参数 \"timestamp\"，请检查JSON字段."),

    /**
     * 缺少必要的参数 signStr
     */
    PARAM_SIGN_MISSING("【ApiCrypto】 缺少必要的参数 \"signStr\"，请检查JSON字段."),

// 编码

    /**
     * 内容编码失败
     */
    ENCODING_FAILED("【ApiCrypto】 内容编码失败."),
    /**
     * 内容解码失败
     */
    DECODING_FAILED("【ApiCrypto】 内容编码失败."),

// 加密解密

    /**
     * 解密后数据为空
     */
    DATA_EMPTY("【ApiCrypto】 解密后数据为空."),

    /**
     * 加密失败
     */

    ENCRYPTION_FAILED("【ApiCrypto】 加密失败."),
    /**
     * 解密失败
     */

    DECRYPTION_FAILED("【ApiCrypto】 解密失败."),
    /**
     * 请先配置加密或解密的必要参数
     */
    REQUIRED_CRYPTO_PARAM("【ApiCrypto】 请先配置加密或解密的必要参数."),

    /**
     * 请先配置秘钥
     */
    NO_SECRET_KEY("【ApiCrypto】 请先配置秘钥."),

    /**
     * 请先配置秘钥 privateKey.
     */
    NO_PRIVATE_KEY("【ApiCrypto】 请先配置秘钥 privateKey."),

    /**
     * 请先配置秘钥 publicKey.
     */
    NO_PUBLIC_KEY("【ApiCrypto】 请先配置秘钥 publicKey."),
// 签名

    /**
     * 签名已超时
     */
    SIGNATURE_TIMED_OUT("【ApiCrypto】 签名已超时."),

    /**
     * 验证签名失败
     */
    VERIFY_SIGNATURE_FAILED("【ApiCrypto】 验证签名失败."),

    /**
     * 签名失败
     */
    SIGNATURE_FAILED("【ApiCrypto】 签名失败."),

    /**
     * 请先配置签名必要的参数
     */
    REQUIRED_SIGNATURE_PARAM("【ApiCrypto】 请先配置签名的必要参数.");

    /**
     * 错误消息
     */
    private final String message;

    ApiCryptoExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
