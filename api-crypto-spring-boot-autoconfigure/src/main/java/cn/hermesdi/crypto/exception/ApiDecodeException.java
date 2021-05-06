package cn.hermesdi.crypto.exception;

/**
 * 解密异常类
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public class ApiDecodeException extends RuntimeException {
    private ApiCryptoExceptionType exceptionType;

    public ApiDecodeException(ApiCryptoExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

    public ApiDecodeException(String message) {
        super(message);
    }

    public ApiDecodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiDecodeException(Throwable cause) {
        super(cause);
    }

    public ApiCryptoExceptionType getExceptionType() {
        return exceptionType;
    }

}
