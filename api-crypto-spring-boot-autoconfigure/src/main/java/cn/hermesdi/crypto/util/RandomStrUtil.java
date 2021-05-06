package cn.hermesdi.crypto.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机字符串工具类
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public class RandomStrUtil {

    public static final String BASE_CHAR_NUMBER = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 随机生成指定位数的数字字符串
     *
     * @param length: 长度
     * @return java.lang.String 生成的字符串
     * @author hermes-di
     **/
    public static String getRandomNumber(int length) {
        final StringBuilder sb = new StringBuilder(length);
        if (length < 1) {
            length = 1;
        }
        int baseLength = BASE_CHAR_NUMBER.length();
        for (int i = 0; i < length; i++) {
            int number = getRandom().nextInt(baseLength);
            sb.append(BASE_CHAR_NUMBER.charAt(number));
        }
        return sb.toString();
    }

    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }
}
