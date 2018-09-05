package com.wang.controller;

import com.wang.exception.CustomException;
import com.wang.exception.UnauthorizedException;
import com.wang.model.UserDto;
import com.wang.model.common.ResponseBean;
import com.wang.service.IUserService;
import com.wang.util.encryp.EncrypAESUtil;
import com.wang.config.jwt.JWTUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * TODO：
 * @author Wang926454
 * @date 2018/8/29 15:45
 */
@RestController
@RequestMapping("/user")
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
    @GetMapping
    @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
    public ResponseBean user(){
        List<UserDto> userDtos = userService.selectAll();
        if(userDtos == null || userDtos.size() <= 0){
            throw new CustomException("查询失败(Query Failure)");
        }
        return new ResponseBean(200, "查询成功(Query was successful)", userDtos);
    }

    /**
     * TODO：获取指定用户
     * @param id
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:42
     */
    @GetMapping("/{id}")
    @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
    public ResponseBean findById(@PathVariable("id") Integer id){
        UserDto userDto = userService.selectByPrimaryKey(id);
        if(userDto == null){
            throw new CustomException("查询失败(Query Failure)");
        }
        return new ResponseBean(200, "查询成功(Query was successful)", userDto);
    }

    /**
     * TODO：新增用户
     * @param userDto
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:42
     */
    @PostMapping
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public ResponseBean add(@RequestBody UserDto userDto) {
        // 判断当前帐号是否存在
        UserDto userDtoTemp = new UserDto();
        userDtoTemp.setAccount(userDto.getAccount());
        if(userService.selectOne(userDtoTemp) != null){
            throw new CustomException("account already exists");
        }
        userDto.setRegTime(new Date());
        // 密码以帐号+密码的形式进行AES加密
        String key = EncrypAESUtil.Encrytor(userDto.getAccount() + userDto.getPassword());
        userDto.setPassword(key);
        int count = userService.insert(userDto);
        if(count <= 0){
            throw new CustomException("新增失败(Insert Failure)");
        }
        return new ResponseBean(200, "新增成功(Insert Success)", userDto);
    }

    /**
     * TODO：更新用户
     * @param userDto
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:42
     */
    @PutMapping
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public ResponseBean update(@RequestBody UserDto userDto) {
        // 判断当前帐号是否存在
        UserDto userDtoTemp = new UserDto();
        userDtoTemp.setAccount(userDto.getAccount());
        if(userService.selectOne(userDtoTemp) != null){
            throw new CustomException("account already exists");
        }
        // 密码以帐号+密码的形式进行AES加密
        String key = EncrypAESUtil.Encrytor(userDto.getAccount() + userDto.getPassword());
        userDto.setPassword(key);
        int count = userService.updateByPrimaryKeySelective(userDto);
        if(count <= 0){
            throw new CustomException("更新失败(Update Failure)");
        }
        return new ResponseBean(200, "更新成功(Update Success)", userDto);
    }

    /**
     * TODO：删除用户
     * @param id
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:43
     */
    @DeleteMapping("/{id}")
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public ResponseBean delete(@PathVariable("id") Integer id){
        int count = userService.deleteByPrimaryKey(id);
        if(count <= 0){
            throw new CustomException("删除失败，ID不存在(Deletion Failed. ID does not exist.)");
        }
        return new ResponseBean(200, "删除成功(Delete Success)", null);
    }

    /**
     * TODO：登录授权
     * @param userDto
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/8/30 16:21
     */
    @PostMapping("/login")
    public ResponseBean login(@RequestBody UserDto userDto) {
        UserDto userDtoTemp = new UserDto();
        userDtoTemp.setAccount(userDto.getAccount());
        // 查询数据库中的密码
        userDtoTemp = userService.selectOne(userDtoTemp);
        // 进行AES解密
        String key = EncrypAESUtil.Decryptor(userDtoTemp.getPassword());
        // 对比，因为密码加密是以帐号+密码的形式进行加密的，所以解密后的对比是帐号+密码
        if (key.equals(userDto.getAccount() + userDto.getPassword())) {
            return new ResponseBean(200, "登录成功(Login Success)", JWTUtil.sign(userDto.getAccount(), key));
        } else {
            throw new UnauthorizedException("帐号或密码错误(Account or Password Error)");
        }
    }

    /**
     * TODO：测试登录
     * @param
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/8/30 16:18
     */
    @GetMapping("/article")
    public ResponseBean article() {
        Subject subject = SecurityUtils.getSubject();
        // 登录了返回true
        if (subject.isAuthenticated()) {
            return new ResponseBean(200, "您已经登录了(You are already logged in)", null);
        } else {
            return new ResponseBean(200, "你是游客(You are guest)", null);
        }
    }

    /**
     * TODO：@RequiresAuthentication和subject.isAuthenticated()返回true一个性质
     * @param 
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/8/30 16:18
     */
    @GetMapping("/article2")
    @RequiresAuthentication
    public ResponseBean requireAuth() {
        return new ResponseBean(200, "您已经登录了(You are already logged in)", null);
    }

}