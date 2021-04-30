package cn.hermesdi.crypto.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author hermes·di
 * @Date 2020/7/6 0006 23:37
 * @Describe 随机字符串工具类
 */
public class RandomStrUtil {

    public static final String BASE_CHAR_NUMBER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * 随机生成指定位数的数字字符串
     *
     * @param length: 长度
     * @Author hermes·di
     */
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
