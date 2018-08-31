package com.wang.controller;

import com.wang.exception.UnauthorizedException;
import com.wang.model.UserDto;
import com.wang.model.common.ResponseBean;
import com.wang.service.IUserService;
import com.wang.util.JWTUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO：
 * @author Wang926454
 * @date 2018/8/29 15:45
 */
@RestController
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * TODO：获取所有用户
     * @param 
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:41
     */
    @GetMapping("/user")
    @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
    public Map<String,Object> user(){
        List<UserDto> userDtos = userService.selectAll();
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("code", "200");
        map.put("data", userDtos);
        return map;
    }

    /**
     * TODO：获取指定用户
     * @param id
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:42
     */
    @GetMapping("/user/{id}")
    @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
    public Map<String,Object> findById(@PathVariable("id") Integer id){
        UserDto userDto = userService.selectByPrimaryKey(id);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("code", "200");
        map.put("data", userDto);
        return map;
    }

    /**
     * TODO：新增用户
     * @param userDto
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:42
     */
    @PostMapping("/user")
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public Map<String,Object> add(@RequestBody UserDto userDto){
        userDto.setRegTime(new Date());
        int count = userService.insert(userDto);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("code", "200");
        map.put("count", count);
        map.put("data", userDto);
        return map;
    }

    /**
     * TODO：更新用户
     * @param userDto
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:42
     */
    @PutMapping("/user")
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public Map<String,Object> update(@RequestBody UserDto userDto){
        int count = userService.updateByPrimaryKeySelective(userDto);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("code", "200");
        map.put("count", count);
        map.put("data", userDto);
        return map;
    }

    /**
     * TODO：删除用户
     * @param id
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:43
     */
    @DeleteMapping("/user/{id}")
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public Map<String,Object> delete(@PathVariable("id") Integer id){
        int count = userService.deleteByPrimaryKey(id);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("code", "200");
        map.put("count", count);
        map.put("data", null);
        return map;
    }

    /**
     * TODO：登录授权
     * @param userDto
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/8/30 16:21
     */
    @PostMapping("/user/login")
    public ResponseBean login(@RequestBody UserDto userDto) {
        UserDto userDtoTemp = new UserDto();
        userDtoTemp.setAccount(userDto.getAccount());
        userDtoTemp = userService.selectOne(userDtoTemp);
        if (userDtoTemp.getPassword().equals(userDto.getPassword())) {
            return new ResponseBean(200, "login success", JWTUtil.sign(userDto.getAccount(), userDto.getPassword()));
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * TODO：测试登录
     * @param
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/8/30 16:18
     */
    @GetMapping("/user/article")
    public ResponseBean article() {
        Subject subject = SecurityUtils.getSubject();
        // 登录了返回true
        if (subject.isAuthenticated()) {
            return new ResponseBean(200, "You are already logged in", null);
        } else {
            return new ResponseBean(200, "You are guest", null);
        }
    }

    /**
     * TODO：@RequiresAuthentication和subject.isAuthenticated()返回true一个性质
     * @param 
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/8/30 16:18
     */
    @GetMapping("/user/article2")
    @RequiresAuthentication
    public ResponseBean requireAuth() {
        return new ResponseBean(200, "You are already logged in", null);
    }

    /**
     * TODO：401没有权限异常
     * @param
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/8/30 16:18
     */
    @RequestMapping(path = "/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseBean unauthorized() {
        return new ResponseBean(401, "Unauthorized", null);
    }
}