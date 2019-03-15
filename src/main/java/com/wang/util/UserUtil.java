package com.wang.util;

import com.wang.model.UserDto;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;
import java.util.Set;

/**
 * 获取当前登录用户工具类
 * @author Wang926454
 * @date 2019/3/15 11:45
 */
@Component
public class UserUtil {

    /**
     * 获取当前登录用户
     * @param
     * @return com.wang.model.UserDto
     * @author Wang926454
     * @date 2019/3/15 11:48
     */
    public UserDto getUser() {
        return (UserDto) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 获取当前登录用户Id
     * @param
     * @return com.wang.model.UserDto
     * @author Wang926454
     * @date 2019/3/15 11:48
     */
    public Integer getUserId() {
        return getUser().getId();
    }

    /**
     * 获取当前登录用户Token
     * @param
     * @return com.wang.model.UserDto
     * @author Wang926454
     * @date 2019/3/15 11:48
     */
    public String getToken() {
        PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
        if (principalCollection != null) {
            Set<String> realmNames = principalCollection.getRealmNames();
            for (String realmName : realmNames) {
                return realmName;
            }
        }
        return null;
    }

    /**
     * 获取当前登录用户Account
     * @param
     * @return com.wang.model.UserDto
     * @author Wang926454
     * @date 2019/3/15 11:48
     */
    public String getAccount() {
        return getUser().getAccount();
    }
}
