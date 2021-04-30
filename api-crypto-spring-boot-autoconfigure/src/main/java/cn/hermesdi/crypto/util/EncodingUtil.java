package cn.hermesdi.crypto.util;

import cn.hermesdi.crypto.constants.EncodingType;
import cn.hermesdi.crypto.exception.ApiDecodeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.encoders.UrlBase64;

import java.nio.charset.StandardCharsets;

/**
 * @Author hermes·di
 * @Date 2021/4/26 11:27
 * @Describe 编码工具类
 */
public class EncodingUtil {

    private static final Log logger = LogFactory.getLog(EncodingUtil.class);

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
            logger.error("【ApiCrypto】 Encoding failure.(编码失败) ERROR：" + e.getMessage());
            throw new ApiDecodeException("【ApiCrypto】 Encoding failure.(编码失败) ERROR：" + e.getMessage());
        }
        return null;
    }

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
            logger.error("【ApiCrypto】 Decoding failure.(解码失败) ERROR：" + e.getMessage());
            throw new ApiDecodeException("【ApiCrypto】 Decoding failure.(解码失败) ERROR：" + e.getMessage());
        }
        return null;
    }

    public static byte[] decode(EncodingType encodingType, String encoding) {
        return decode(encodingType, encoding.getBytes(StandardCharsets.UTF_8));
    }

}
