package cn.hermesdi.crypto.util;

import cn.hermesdi.crypto.constants.EncodingType;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

/**
 * 加密、解密 工具类
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public class CryptoUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 摘要算法实现
     *
     * @param digest  :  加密方式
     * @param content : 加密内容
     * @param charset : 字符集
     * @return java.lang.String 加密后文本
     * @author hermes-di
     **/
    public static String digest(Digest digest, EncodingType encodingType, String content, Charset charset) {
        byte[] bytes = content.getBytes(charset);
        digest.update(bytes, 0, bytes.length);
        byte[] data = new byte[digest.getDigestSize()];
        digest.doFinal(data, 0);
        return EncodingUtil.encode(encodingType, data);
    }

    /**
     * 对称性实现
     *
     * @param type:         类型
     * @param method:       填充方式
     * @param mode:         解密、加密
     * @param key:          key
     * @param content:      内容
     * @param encodingType: 内容编码类型
     * @param iv:           偏移量
     * @param charset:      字符集
     * @return java.lang.String 加密/解密后文本
     * @author hermes-di
     **/
    public static String symmetric(String type, String method, int mode, String key, String content, EncodingType encodingType, String iv, Charset charset) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(charset), type);
        Cipher cipher = Cipher.getInstance(method, "BC");

        if (Objects.nonNull(iv)) {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(charset));
            cipher.init(mode, secretKeySpec, ivSpec);
        } else {
            cipher.init(mode, secretKeySpec);
        }

        // 加密
        if (mode == Cipher.ENCRYPT_MODE) {
            return EncodingUtil.encode(
                    encodingType, cipher.doFinal(content.getBytes(charset))
            );
        }

        // 解密
        if (mode == Cipher.DECRYPT_MODE) {
            return new String(
                    cipher.doFinal(EncodingUtil.decode(encodingType, content, charset))
            );
        }
        return null;
    }


    /**
     * 非对称性实现
     *
     * @param type:                类型
     * @param method:              填充方式
     * @param mode:                解密、加密
     * @param key:                 publicKey 或 privateKey
     * @param keyEncodingType:     key编码类型
     * @param content:             内容
     * @param contentEncodingType: 内容编码类型
     * @param charset:             字符集
     * @return java.lang.String 加密/解密后文本
     * @author hermes-di
     **/
    public static String asymmetry(String type, String method, int mode, String key, EncodingType keyEncodingType, String content, EncodingType contentEncodingType, Charset charset) throws Exception {
        byte[] decodeKey = EncodingUtil.decode(keyEncodingType, key.getBytes(charset));

        assert decodeKey != null;

        KeyFactory keyFactory = KeyFactory.getInstance(type, "BC");
        Cipher cipher = Cipher.getInstance(method, "BC");

        // 加密
        if (mode == Cipher.ENCRYPT_MODE) {
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(decodeKey));
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = cipher.doFinal(content.getBytes(charset));
            return EncodingUtil.encode(contentEncodingType, bytes);
        }

        // 解密
        if (mode == Cipher.DECRYPT_MODE) {
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodeKey));
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decodeContent = EncodingUtil.decode(contentEncodingType, content, charset);
            return new String(cipher.doFinal(decodeContent));
        }

        return null;
    }

}
