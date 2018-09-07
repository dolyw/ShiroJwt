package com.wang.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.wang.util.PropertiesUtil;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * JAVA-JWT工具类
 * @author Wang926454
 * @date 2018/8/30 11:45
 */
public class JWTUtil {

    /**
     * 过期时间改为从配置文件获取
     */
    // private static final long EXPIRE_TIME = 5 * 60 * 1000;

    /**
     * 校验token是否正确
     * @param token Token
	 * @param secret 私钥
     * @return boolean 是否正确
     * @author Wang926454
     * @date 2018/8/31 9:05
     */
    public static boolean verify(String token, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获得Token中的信息无需secret解密也能获得
     * @param token
     * @return java.lang.String Token中包含的帐号
     * @author Wang926454
     * @date 2018/8/31 9:07
     */
    public static String getAccount(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("account").asString();
        } catch (JWTDecodeException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成签名
     * @param account 帐号
	 * @param secret 私钥
     * @return java.lang.String 返回加密的Token
     * @author Wang926454
     * @date 2018/8/31 9:07
     */
    public static String sign(String account, String secret) {
        try {
            // 获取Token过期时间，读取配置文件
            PropertiesUtil.readProperties("config.properties");
            String tokenExpireTime = PropertiesUtil.getProperty("tokenExpireTime");
            // 此处过期时间是以毫秒为单位，所以乘以1000
            Date date = new Date(System.currentTimeMillis() + Long.parseLong(tokenExpireTime) * 1000);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带account帐号信息
            return JWT.create()
                    .withClaim("account", account)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
