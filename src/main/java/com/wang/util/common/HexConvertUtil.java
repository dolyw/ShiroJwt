package com.wang.util.common;

/**
 * 进制转换工具
 * @author dolyw.com
 * @date 2018/8/31 17:23
 */
public class HexConvertUtil {

    private HexConvertUtil() {}

    /**
     * 1
     */
    private static final Integer INTEGER_1 = 1;

    /**
     * 2
     */
    private static final Integer INTEGER_2 = 2;

    /**
     * 将二进制转换成16进制
     * @param bytes
     * @return java.lang.String
     * @author dolyw.com
     * @date 2018/8/31 17:20
     */
    public static String parseByte2HexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte buff : bytes) {
            String hex = Integer.toHexString(buff & 0xFF);
            if (hex.length() == INTEGER_1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     * @param hexStr
     * @return byte[]
     * @author dolyw.com
     * @date 2018/8/31 17:21
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < INTEGER_1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / INTEGER_2];
        for (int i = 0, len = hexStr.length() / INTEGER_2; i < len; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
