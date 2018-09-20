package com.wang.config.shiro.jwt;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.wang.model.common.Constant;
import com.wang.util.JWTUtil;
import com.wang.util.JedisUtil;
import com.wang.util.common.PropertiesUtil;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT过滤
 * @author Wang926454
 * @date 2018/8/30 15:47
 */
public class JWTFilter extends BasicHttpAuthenticationFilter {
    /**
     * Logger
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 这里我们详细说明下为什么最终返回的都是true，即允许访问
     * 例如我们提供一个地址 GET /article
     * 登入用户和游客看到的内容是不同的
     * 如果在这里返回了false，请求会被直接拦截，用户看不到任何东西
     * 所以我们在这里返回true，Controller中可以通过 subject.isAuthenticated() 来判断用户是否登入
     * 如果有些资源只有登入用户才能访问，我们只需要在方法上面加上 @RequiresAuthentication 注解即可
     * 但是这样做有一个缺点，就是不能够对GET,POST等请求进行分别过滤鉴权(因为我们重写了官方的方法)，但实际上对应用影响不大
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 判断用户是否想要登入
        if (this.isLoginAttempt(request, response)) {
            try {
                // 进行Shiro的登录UserRealm
                this.executeLogin(request, response);
            } catch (Exception e) {
                // 认证出现异常跳转到/401，传递错误信息msg
                String msg = e.getMessage();
                // 获取应用异常(该Cause是导致抛出此throwable(异常)的throwable(异常))
                Throwable throwable = e.getCause();
                if(throwable != null && throwable instanceof SignatureVerificationException ){
                    // 该异常为JWT的Token认证失败(Token或者密钥不正确)
                    // throw (SignatureVerificationException) throwable;
                    msg = "Token或者密钥不正确(" + throwable.getMessage() + ")";
                } else if(throwable != null && throwable instanceof TokenExpiredException){
                    // 该异常为JWT的Token已过期
                    // TODO: 此处为AccessToken刷新，进行判断RefreshToken是否过期，未过期就进行正常访问且返回新的AccessToken
                    String token = this.getToken(request, response);
                    String account = JWTUtil.getClaim(token, "account");
                    if(JedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account)){
                        // Redis中RefreshToken还存在
                        String currentTimeMillisRedis = JedisUtil.getObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account).toString();
                        // 获取AccessToken时间戳，与RefreshToken的时间戳对比
                        if(JWTUtil.getClaim(token, "currentTimeMillis").equals(currentTimeMillisRedis)){
                            // 通过说明该AccessToken时间戳与RefreshToken时间戳一致，进行AccessToken刷新
                            // 获取当前时间戳
                            String currentTimeMillis = String.valueOf(System.currentTimeMillis());
                            // 获取RefreshToken剩余过期时间
                            Long refreshTokenExpireTimeRedis = JedisUtil.getExpireTime(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account);
                            // 获取AccessToken过期时间，读取配置文件
                            PropertiesUtil.readProperties("config.properties");
                            String accessTokenExpireTime = PropertiesUtil.getProperty("accessTokenExpireTime");
                            // 设置RefreshToken中的时间戳为当前时间戳，且过期时间为之前剩余过期时间加上一个新的AccessToken过期时间
                            JedisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account, currentTimeMillis,
                                    refreshTokenExpireTimeRedis.intValue() + Integer.parseInt(accessTokenExpireTime));
                            // 刷新AccessToken，设置时间戳为当前时间戳
                            token = JWTUtil.sign(account, currentTimeMillis);
                            // 将刷新的AccessToken存放在Response的Header中的Authorization字段返回
                            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                            httpServletResponse.setHeader("Authorization", token);
                            httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
                            // 进行Shiro的登录UserRealm
                            JWTToken jwtToken = new JWTToken(token);
                            // 提交给UserRealm进行认证，如果错误他会抛出异常并被捕获
                            this.getSubject(request, response).login(jwtToken);
                            // 如果没有抛出异常则代表登入成功，返回true
                            return true;
                        }
                    }
                    msg = "Token已过期(" + throwable.getMessage() + ")";
                } else{
                    // 应用异常不为空
                    if(throwable != null) {
                        // 获取应用异常msg
                        msg = throwable.getMessage();
                    }
                }
                this.forward401(request, response, msg);
            }
        }
        return true;
    }

    /**
     * 检测Header里面是否包含Authorization字段，有就进行Token登录认证授权
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        return authorization != null;
    }

    /**
     * 进行Token登录认证授权
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        JWTToken token = new JWTToken(this.getToken(request, response));
        // 提交给UserRealm进行认证，如果错误他会抛出异常并被捕获
        this.getSubject(request, response).login(token);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 获取Token
     */
    private String getToken(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("Authorization");
        return token;
    }

    /**
     * 将非法请求转发到/401
     */
    private void forward401(ServletRequest req, ServletResponse resp, String msg) {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) req;
            HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
            // 传递错误信息msg
            req.setAttribute("msg", msg);
            httpServletRequest.getRequestDispatcher("/401").forward(httpServletRequest, httpServletResponse);
        } catch (ServletException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
