package com.wang.util.convert;

import com.wang.model.common.Constant;

/**
 * 进制转换
 * @author Wang926454
 * @date 2018/8/31 17:23
 */
public class HexConvertUtil {

    /**
     * 将二进制转换成16进制
     * @param buff
     * @return java.lang.String
     * @author Wang926454
     * @date 2018/8/31 17:20
     */
    public static String parseByte2HexStr(byte[] buff) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, len = buff.length; i < len; i++) {
            String hex = Integer.toHexString(buff[i] & 0xFF);
            if (hex.length() == Constant.INTEGER_1) {
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
     * @author Wang926454
     * @date 2018/8/31 17:21
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < Constant.INTEGER_1){
            return null;
        }
        byte[] result = new byte[hexStr.length() / Constant.INTEGER_2];
        for (int i = 0, len = hexStr.length() / Constant.INTEGER_2; i < len; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
