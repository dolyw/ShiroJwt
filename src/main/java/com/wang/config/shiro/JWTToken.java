package com.wang.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * TODO：JWTToken
 * @author Wang926454
 * @date 2018/8/30 14:06
 */
public class JWTToken implements AuthenticationToken {
    /**
     * Token
     */
    private String token;

    public JWTToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
