package com.wang.model.common;

/**
 * TODO：常量
 * @author Wang926454
 * @date 2018/9/3 16:03
 */
public class Constant {
    /**
     * 1
     */
    public static final Integer INTEGER_1 = 1;

    /**
     * 2
     */
    public static final Integer INTEGER_2 = 2;

    /**
     * redis-OK
     */
    public final static String OK = "OK";

    /**
     * redis过期时间，以秒为单位，一分钟
     */
    public final static int EXRP_MINUTE = 60;

    /**
     * redis过期时间，以秒为单位，一小时
     */
    public final static int EXRP_HOUR = 60 * 60;

    /**
     * redis过期时间，以秒为单位，一天
     */
    public final static int EXRP_DAY = 60 * 60 * 24;

}
