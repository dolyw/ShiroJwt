## ShiroJwt

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/wang926454/ShiroJwt/pulls)
[![GitHub stars](https://img.shields.io/github/stars/wang926454/ShiroJwt.svg?style=social&label=Stars)](https://github.com/wang926454/ShiroJwt)
[![GitHub forks](https://img.shields.io/github/forks/wang926454/ShiroJwt.svg?style=social&label=Fork)](https://github.com/wang926454/ShiroJwt)

> 前端地址:[https://github.com/wang926454/VueStudy/tree/master/VueStudy08-JWT](https://github.com/wang926454/VueStudy/tree/master/VueStudy08-JWT)

#### 疑问查看

1. [#14 重复请求会不会生成多个token](https://github.com/dolyw/ShiroJwt/issues/14)
2. [#19 跨域sso问题](https://github.com/dolyw/ShiroJwt/issues/19)
3. [#29 Token刷新并发处理](https://github.com/dolyw/ShiroJwt/issues/29)

<img src="https://cdn.jsdelivr.net/gh/wliduo/CDN@master/feed/qq.png" height="180"></img>

有疑问请扫码加**QQ**群交流: **779168604**

#### 项目相关

* JavaDoc:[https://apidoc.gitee.com/dolyw/ShiroJwt](https://apidoc.gitee.com/dolyw/ShiroJwt)
* 接口文档:[https://note.dolyw.com/shirojwt/ShiroJwt-Interface.html](https://note.dolyw.com/shirojwt/ShiroJwt-Interface.html)
* 教程目录:[https://note.dolyw.com/shirojwt](https://note.dolyw.com/shirojwt)
* 改为数据库形式(MySQL):[https://note.dolyw.com/shirojwt/ShiroJwt02-MySQL.html](https://note.dolyw.com/shirojwt/ShiroJwt02-MySQL.html)
* 解决无法直接返回401错误:[https://note.dolyw.com/shirojwt/ShiroJwt03-401.html](https://note.dolyw.com/shirojwt/ShiroJwt03-401.html)
* 实现Shiro的Cache(Redis)功能:[https://note.dolyw.com/shirojwt/ShiroJwt04-Redis.html](https://note.dolyw.com/shirojwt/ShiroJwt04-Redis.html)

#### 项目介绍

1. RESTful API
2. Maven集成Mybatis Generator(逆向工程)
3. Shiro + Java-JWT实现无状态鉴权机制(Token)
4. 密码加密(采用AES-128 + Base64的方式)
5. 集成Redis(Jedis)
6. 重写Shiro缓存机制(Redis)
7. Redis中保存RefreshToken信息(做到JWT的可控性)
8. 根据RefreshToken自动刷新AccessToken

##### 关于Shiro + Java-JWT实现无状态鉴权机制(Token)

> 1. 首先**Post**用户名与密码到**user/login**登入，成功返回加密的**AccessToken**，失败直接返回401错误(帐号或密码不正确)
> 2. 以后访问都带上这个**AccessToken**即可
> 3. 鉴权流程主要是重写了**Shiro**的入口过滤器**JWTFilter**(**BasicHttpAuthenticationFilter**)，判断请求**Header**里面是否包含**Authorization**字段
> 4. 有就进行**Shiro**的**Token**登录认证授权(用户访问每一个需要权限的请求必须在**Header**中添加**Authorization**字段存放**AccessToken**)，没有就以游客直接访问(有权限管控的话，以游客访问就会被拦截)

##### 关于AES-128 + Base64当两个用户的明文密码相同时进行加密，会发现数据库中存在相同结构的暗文密码

> 大部分是以**MD5 + 盐**的形式解决了这个问题(详细自己百度)，我采用**AES-128 + Base64**是以帐号+密码的形式进行加密密码，因为帐号具有唯一性，所以也不会出现相同结构的暗文密码这个问题

##### 关于将Jedis工具类与SpringBoot整合

> 本来是直接将**JedisUtil**注入为**Bean**，每次使用直接`@Autowired`注入使用即可，但是在重写**Shiro**的**CustomCache**无法注入**JedisUtil**，所以就改成静态注入**JedisPool连接池**，**JedisUtil工具类**还是直接调用静态方法，无需`@Autowired`注入

##### 关于Redis中保存RefreshToken信息(做到JWT的可控性)

> 1. 登录认证通过后返回**AccessToken**信息(在**AccessToken**中**保存当前的时间戳和帐号**)
> 2. 同时在**Redis**中设置一条以**帐号为Key，Value为当前时间戳(登录时间)**的**RefreshToken**
> 3. 现在认证时必须**AccessToken**没失效以及**Redis**存在所对应的**RefreshToken**，且**RefreshToken时间戳**和**AccessToken信息中时间戳一致**才算认证通过，这样可以做到**JWT的可控性**
> 4. 如果重新登录获取了新的**AccessToken**，旧的**AccessToken**就认证不了，因为**Redis**中所存放的的**RefreshToken时间戳信息**只会和最新生成的**AccessToken信息中携带的时间戳一致**，这样每个用户就只能使用最新的**AccessToken**认证
> 5. **Redis**的**RefreshToken**也可以用来判断用户是否在线，如果删除**Redis**的某个**RefreshToken**，那这个**RefreshToken**所对应的**AccessToken**之后也无法通过认证了，就相当于控制了用户的登录，可以剔除用户

##### 关于根据RefreshToken自动刷新AccessToken

> 1. 本身**AccessToken的过期时间为5分钟**(配置文件可配置)，**RefreshToken过期时间为30分钟**(配置文件可配置)
> 2. 当登录后时间过了5分钟之后，当前**AccessToken**便会过期失效，再次带上**AccessToken**访问**JWT**会抛出**TokenExpiredException**异常说明**Token**过期
> 3. 开始判断是否要**进行AccessToken刷新**，**Redis查询当前用户的RefreshToken是否存在**，**以及这个RefreshToken所携带时间戳**和**过期AccessToken所携带的时间戳**是否**一致**
> 4. **如果存在且一致就进行AccessToken刷新，设置过期时间为5分钟(配置文件可配置)，时间戳为当前最新时间戳，同时也设置RefreshToken中的时间戳为当前最新时间戳，刷新过期时间重新为30分钟过期(配置文件可配置)**
> 5. 最终将刷新的**AccessToken**存放在**Response的Header中的Authorization字段**返回(前端进行获取替换，下次用新的**AccessToken**进行访问)

#### 软件架构

1. SpringBoot + Mybatis核心框架
2. PageHelper插件 + 通用Mapper插件
3. Shiro + Java-JWT无状态鉴权认证机制
4. Redis(Jedis)缓存框架

#### 安装教程

1. 数据库帐号密码默认为root，如有修改，请自行修改配置文件application.yml
2. 解压后执行src\main\resources\sql\MySQL.sql脚本创建数据库和表
3. Redis需要自行安装Redis服务，端口密码默认
4. SpringBoot直接启动即可，测试工具PostMan

#### 使用说明

##### Mybatis Generator使用(可视化自定义模板快速生成基础代码:[https://github.com/wang926454/ViewGenerator](https://github.com/wang926454/ViewGenerator))

先配置src\main\resources\generator\generatorConfig.xml文件(默认配置都在原来包的下一级reverse包下)，在pom.xml这一级目录(即项目根目录下)的命令行窗口执行(前提是配置了mvn)(IDEA可以直接在Maven窗口Plugins中双击执行)
```shell
mvn mybatis-generator:generate
```

##### PostMan使用(Token获取及使用)

```java
先设置Content-Type为application/json
```
![image text](https://docs.dolyw.com/Project/ShiroJwt/image/20181006001.PNG)
```text
然后填写请求参数帐号密码信息
```
![image text](https://docs.dolyw.com/Project/ShiroJwt/image/20181006002.PNG)
```text
进行请求访问，请求访问成功
```
![image text](https://docs.dolyw.com/Project/ShiroJwt/image/20181006003.PNG)
```java
点击查看Header信息的Authorization属性即是Token字段
```
![image text](https://docs.dolyw.com/Project/ShiroJwt/image/20181006004.PNG)
```java
访问需要权限的请求将Token字段放在Header信息的Authorization属性访问即可
```
![image text](https://docs.dolyw.com/Project/ShiroJwt/image/20181006005.PNG)
```java
Token的自动刷新也是在Token失效时返回新的Token在Header信息的Authorization属性
```

#### 搭建参考

1. 感谢SmithCruise的Shiro+JWT+Spring Boot Restful简易教程:[https://www.jianshu.com/p/f37f8c295057](https://www.jianshu.com/p/f37f8c295057)
2. 感谢王洪玉的[Shiro入门]（一）使用Redis作为缓存管理器:[https://blog.csdn.net/why15732625998/article/details/78729254](https://blog.csdn.net/why15732625998/article/details/78729254)
3. 感谢袋🐴饲养员的springboot(七).springboot整合jedis实现redis缓存:[http://www.cnblogs.com/GodHeng/p/9301330.html](http://www.cnblogs.com/GodHeng/p/9301330.html)
4. 感谢W_Z_W_888的spring注入静态变量的三种方法及其注意事项:[https://blog.csdn.net/W_Z_W_888/article/details/79979103](https://blog.csdn.net/W_Z_W_888/article/details/79979103)
5. 感谢天降风云的Vue2.0+ElementUI+PageHelper实现的表格分页:[https://blog.csdn.net/u012907049/article/details/70237457](https://blog.csdn.net/u012907049/article/details/70237457)
6. 感谢yaxx的Vuejs之axios获取Http响应头:[https://segmentfault.com/a/1190000009125333](https://segmentfault.com/a/1190000009125333)
7. 感谢Twilight的解决使用jwt刷新token带来的问题:[https://segmentfault.com/a/1190000013151506](https://segmentfault.com/a/1190000013151506)
8. 感谢chuhx的shiro拦截器，返回json数据:[https://blog.csdn.net/chuhx/article/details/51148877](https://blog.csdn.net/chuhx/article/details/51148877)
9. 感谢yidao620c的Shiro自带拦截器配置规则:[https://github.com/yidao620c/SpringBootBucket/tree/master/springboot-jwt](https://github.com/yidao620c/SpringBootBucket/tree/master/springboot-jwt)

#### 参与贡献

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
