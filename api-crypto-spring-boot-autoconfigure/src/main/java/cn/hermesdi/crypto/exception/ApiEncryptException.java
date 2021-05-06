package cn.hermesdi.crypto.exception;

/**
 * 加密异常类
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public class ApiEncryptException extends RuntimeException {
    private ApiCryptoExceptionType exceptionType;

    public ApiEncryptException(ApiCryptoExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

    public ApiEncryptException(String message) {
        super(message);
    }

    public ApiEncryptException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiEncryptException(Throwable cause) {
        super(cause);
    }

    public ApiCryptoExceptionType getExceptionType() {
        return exceptionType;
    }

}
