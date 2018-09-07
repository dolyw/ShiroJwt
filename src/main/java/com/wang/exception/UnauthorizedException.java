package com.wang.exception;

/**
 * 自定义异常
 * @author Wang926454
 * @date 2018/8/30 13:59
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String msg){
        super(msg);
    }

    public UnauthorizedException() {
        super();
    }
}
