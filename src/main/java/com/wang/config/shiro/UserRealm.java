package com.wang.config.shiro;

import com.wang.mapper.PermissionMapper;
import com.wang.mapper.RoleMapper;
import com.wang.mapper.UserMapper;
import com.wang.model.PermissionDto;
import com.wang.model.RoleDto;
import com.wang.model.UserDto;
import com.wang.util.JWTUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TODO：自定义Realm
 * @author Wang926454
 * @date 2018/8/30 14:10
 */
@Service
public class UserRealm extends AuthorizingRealm {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRealm.class);

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    @Autowired
    public UserRealm(UserMapper userMapper, RoleMapper roleMapper, PermissionMapper permissionMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    /**
     * 大坑，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        String account = JWTUtil.getUsername(principals.toString());
        UserDto userDto = new UserDto();
        userDto.setAccount(account);
        // 查询用户角色
        List<RoleDto> roleDtos = roleMapper.findRoleByUser(userDto);
        for (int i = 0, roleLen = roleDtos.size(); i < roleLen; i++) {
            RoleDto roleDto = roleDtos.get(i);
            // 添加角色
            simpleAuthorizationInfo.addRole(roleDto.getName());
            // 根据用户角色查询权限
            List<PermissionDto> permissionDtos = permissionMapper.findPermissionByRole(roleDto);
            for (int j = 0, perLen = permissionDtos.size(); j < perLen; j++) {
                PermissionDto permissionDto = permissionDtos.get(j);
                // 添加权限
                simpleAuthorizationInfo.addStringPermission(permissionDto.getPerCode());
            }
        }
        return simpleAuthorizationInfo;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String username = JWTUtil.getUsername(token);
        if (username == null) {
            throw new AuthenticationException("token invalid");
        }
        // 查询用户是否存在
        UserDto userDto = new UserDto();
        userDto.setAccount(username);
        userDto = userMapper.selectOne(userDto);
        if (userDto == null) {
            throw new AuthenticationException("User didn't existed!");
        }
        if (! JWTUtil.verify(token, username, userDto.getPassword())) {
            throw new AuthenticationException("username or password error");
        }
        return new SimpleAuthenticationInfo(token, token, "userRealm");
    }
}
