package cn.hermesdi.crypto.exception;

/**
 * @Author hermes·di
 * @Date 2020/7/6 0006 11:43
 * @Describe 解密异常类
 */
public class ApiDecodeException extends RuntimeException {

    public ApiDecodeException(String e) {
        super(e);
    }

}
