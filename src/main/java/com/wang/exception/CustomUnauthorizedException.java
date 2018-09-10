package com.wang.exception;

/**
 * 自定义401无权限异常(UnauthorizedException)
 * @author Wang926454
 * @date 2018/8/30 13:59
 */
public class CustomUnauthorizedException extends RuntimeException {

    public CustomUnauthorizedException(String msg){
        super(msg);
    }

    public CustomUnauthorizedException() {
        super();
    }
}
