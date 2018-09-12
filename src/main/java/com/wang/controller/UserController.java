package com.wang.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.util.JedisUtil;
import com.wang.exception.CustomException;
import com.wang.exception.CustomUnauthorizedException;
import com.wang.model.UserDto;
import com.wang.model.common.Constant;
import com.wang.model.common.ResponseBean;
import com.wang.service.IUserService;
import com.wang.util.common.PropertiesUtil;
import com.wang.util.EncrypAESUtil;
import com.wang.util.JWTUtil;
import com.wang.util.common.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * UserController
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
     * 获取所有用户
     * @param 
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:41
     */
    @GetMapping
    @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
    public ResponseBean user(UserDto userDto){
        PageHelper.startPage(userDto.getPage(), userDto.getRows());
        List<UserDto> userDtos = userService.selectAll();
        PageInfo<UserDto> selectPage = new PageInfo<UserDto>(userDtos);
        if(userDtos == null || userDtos.size() <= 0){
            throw new CustomException("查询失败(Query Failure)");
        }
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("count", selectPage.getTotal());
        map.put("data", selectPage.getList());
        return new ResponseBean(200, "查询成功(Query was successful)", map);
    }

    /**
     * 查询Redis中的RefreshToken(在线用户)
     * @param 
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/9/6 9:58
     */
    @GetMapping("/online")
    @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
    public ResponseBean online(){
        List<Object> userDtos = new ArrayList<Object>();
        // 查询所有Redis键
        Set<String> keys = JedisUtil.keysS(Constant.PREFIX_SHIRO_REFRESH_TOKEN + "*");
        for (String key : keys) {
            if(JedisUtil.exists(key)){
                Map<String, Object> userDto = new HashMap<String, Object>();
                // 根据:分割key，获取最后一个字符(帐号)
                String[] strArray = key.split(":");
                UserDto userDtoTemp = new UserDto();
                userDtoTemp.setAccount(strArray[strArray.length - 1]);
                userDtoTemp = userService.selectOne(userDtoTemp);
                userDto.put("id", userDtoTemp.getId());
                userDto.put("account", userDtoTemp.getAccount());
                userDto.put("username", userDtoTemp.getUsername());
                userDto.put("regTime", userDtoTemp.getRegTime());
                userDto.put("loginTime", JedisUtil.getObject(key));
                userDtos.add(userDto);
            }
        }
        if(userDtos == null || userDtos.size() <= 0){
            throw new CustomException("查询失败(Query Failure)");
        }
        return new ResponseBean(200, "查询成功(Query was successful)", userDtos);
    }

    /**
     * 获取指定用户
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
     * 新增用户
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
        userDtoTemp = userService.selectOne(userDtoTemp);
        if(userDtoTemp != null && StringUtil.isNotBlank(userDtoTemp.getPassword())){
            throw new CustomUnauthorizedException("该帐号已存在(Account exist.)");
        }
        userDto.setRegTime(new Date());
        // 密码以帐号+密码的形式进行AES加密
        if(userDto.getPassword().length() > 8){
            throw new CustomException("密码最多8位(Password up to 8 bits.)");
        }
        String key = EncrypAESUtil.Encrytor(userDto.getAccount() + userDto.getPassword());
        userDto.setPassword(key);
        int count = userService.insert(userDto);
        if(count <= 0){
            throw new CustomException("新增失败(Insert Failure)");
        }
        return new ResponseBean(200, "新增成功(Insert Success)", userDto);
    }

    /**
     * 更新用户
     * @param userDto
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:42
     */
    @PutMapping
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public ResponseBean update(@RequestBody UserDto userDto) {
        // 查询数据库密码
        UserDto userDtoTemp = new UserDto();
        userDtoTemp.setId(userDto.getId());
        userDtoTemp = userService.selectOne(userDtoTemp);
        // FIXME: 如果不一样就说明用户修改了密码，重新加密密码(这个处理不太好，但是没有想到好的处理方式)
        if(!userDtoTemp.getPassword().equals(userDto.getPassword())){
            // 密码以帐号+密码的形式进行AES加密
            if(userDto.getPassword().length() > 8){
                throw new CustomException("密码最多8位(Password up to 8 bits.)");
            }
            String key = EncrypAESUtil.Encrytor(userDto.getAccount() + userDto.getPassword());
            userDto.setPassword(key);
        }
        int count = userService.updateByPrimaryKeySelective(userDto);
        if(count <= 0){
            throw new CustomException("更新失败(Update Failure)");
        }
        return new ResponseBean(200, "更新成功(Update Success)", userDto);
    }

    /**
     * 删除用户
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
     * 剔除在线用户
     * @param id
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/9/6 10:20
     */
    @DeleteMapping("/online/{id}")
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public ResponseBean deleteOnline(@PathVariable("id") Integer id){
        UserDto userDto = userService.selectByPrimaryKey(id);
        if(JedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + userDto.getAccount())){
            if(JedisUtil.delKey(Constant.PREFIX_SHIRO_REFRESH_TOKEN + userDto.getAccount()) > 0){
                return new ResponseBean(200, "剔除成功(Delete Success)", null);
            }
        }
        throw new CustomException("剔除失败，Account不存在(Deletion Failed. Account does not exist.)");
    }

    /**
     * 登录授权
     * @param userDto
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/8/30 16:21
     */
    @PostMapping("/login")
    public ResponseBean login(@RequestBody UserDto userDto) {
        // 查询数据库中的帐号信息
        UserDto userDtoTemp = new UserDto();
        userDtoTemp.setAccount(userDto.getAccount());
        userDtoTemp = userService.selectOne(userDtoTemp);
        if(userDtoTemp == null){
            throw new CustomUnauthorizedException("该帐号不存在(The account does not exist.)");
        }
        // 密码进行AES解密
        String key = EncrypAESUtil.Decryptor(userDtoTemp.getPassword());
        // 因为密码加密是以帐号+密码的形式进行加密的，所以解密后的对比是帐号+密码
        if (key.equals(userDto.getAccount() + userDto.getPassword())) {
            // 清除可能存在的Shiro权限信息缓存
            if(JedisUtil.exists(Constant.PREFIX_SHIRO_CACHE + userDto.getAccount())){
                JedisUtil.delKey(Constant.PREFIX_SHIRO_CACHE + userDto.getAccount());
            }
            // 获取RefreshToken过期时间，读取配置文件
            PropertiesUtil.readProperties("config.properties");
            String refreshTokenExpireTime = PropertiesUtil.getProperty("refreshTokenExpireTime");
            // 设置RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
            String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            JedisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + userDto.getAccount(), currentTimeMillis, Integer.parseInt(refreshTokenExpireTime));
            // 返回AccessToken，时间戳为当前时间戳
            return new ResponseBean(200, "登录成功(Login Success.)", JWTUtil.sign(userDto.getAccount(), currentTimeMillis));
        } else {
            throw new CustomUnauthorizedException("帐号或密码错误(Account or Password Error.)");
        }
    }

    /**
     * 测试登录
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
     * @RequiresAuthentication和subject.isAuthenticated()返回true一个性质
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