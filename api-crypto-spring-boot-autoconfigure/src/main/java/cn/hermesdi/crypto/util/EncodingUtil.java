package cn.hermesdi.crypto.util;

import cn.hermesdi.crypto.constants.EncodingType;
import cn.hermesdi.crypto.exception.ApiCryptoExceptionType;
import cn.hermesdi.crypto.exception.ApiDecodeException;
import cn.hermesdi.crypto.exception.ApiEncryptException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.encoders.UrlBase64;

import java.nio.charset.Charset;

/**
 * 编码工具类
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public class EncodingUtil {

    private static final Log logger = LogFactory.getLog(EncodingUtil.class);

    /**
     * 编码实现
     *
     * @param encodingType: 编码类型
     * @param encoding:     编码内容
     * @return java.lang.String 编码字符串
     * @author hermes-di
     **/
    public static String encode(EncodingType encodingType, byte[] encoding) {
        try {
            switch (encodingType) {
                case HEX:
                    return Hex.toHexString(encoding);
                case BASE64:
                    return Base64.toBase64String(encoding);
                case URL_BASE64:
                    return new String(UrlBase64.encode(encoding));
                case NONE:
                    return new String(encoding);
            }
        } catch (Exception e) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.ENCODING_FAILED;
            logger.error(exceptionType.getMessage() + " ERROR：" + e.getMessage());
            throw new ApiEncryptException(exceptionType);
        }
        return null;
    }

    /**
     * 解码实现
     *
     * @param encodingType: 编码类型
     * @param encoding:     编码内容
     * @return byte[] 解码字节数组
     * @author hermes-di
     **/
    public static byte[] decode(EncodingType encodingType, byte[] encoding) {
        try {
            switch (encodingType) {
                case HEX:
                    return Hex.decode(encoding);
                case BASE64:
                    return Base64.decode(encoding);
                case URL_BASE64:
                    return UrlBase64.decode(encoding);
                case NONE:
                    return encoding;
            }
        } catch (Exception e) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.DECODING_FAILED;
            logger.error(exceptionType.getMessage() + " ERROR：" + e.getMessage());
            throw new ApiDecodeException(exceptionType);
        }
        return null;
    }

    /**
     * 解码实现
     *
     * @param encodingType: 编码类型
     * @param encoding:     编码内容
     * @param charset:      字符集
     * @return byte[] 解码字节数组
     * @author hermes-di
     **/
    public static byte[] decode(EncodingType encodingType, String encoding, Charset charset) {
        return decode(encodingType, encoding.getBytes(charset));
    }

}
