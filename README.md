# SBAPIJWT

> 前端地址:[https://github.com/wang926454/VueStudy/tree/master/VueStudy08-JWT](https://github.com/wang926454/VueStudy/tree/master/VueStudy08-JWT)

### 搭建参考

1. 感谢SmithCruise的Shiro+JWT+Spring Boot Restful简易教程:[https://www.jianshu.com/p/f37f8c295057](https://www.jianshu.com/p/f37f8c295057)
2. 感谢王洪玉的[Shiro入门]（一）使用Redis作为缓存管理器:[https://blog.csdn.net/why15732625998/article/details/78729254](https://blog.csdn.net/why15732625998/article/details/78729254)
3. 感谢袋🐴饲养员的springboot(七).springboot整合jedis实现redis缓存:[http://www.cnblogs.com/GodHeng/p/9301330.html](http://www.cnblogs.com/GodHeng/p/9301330.html)
4. 感谢W_Z_W_888的spring注入静态变量的三种方法及其注意事项:[https://blog.csdn.net/W_Z_W_888/article/details/79979103](https://blog.csdn.net/W_Z_W_888/article/details/79979103)
5. 感谢天降风云的Vue2.0+ElementUI+PageHelper实现的表格分页:[https://blog.csdn.net/u012907049/article/details/70237457](https://blog.csdn.net/u012907049/article/details/70237457)
6. 感谢yaxx的Vuejs之axios获取Http响应头:[https://segmentfault.com/a/1190000009125333](https://segmentfault.com/a/1190000009125333)
7. 感谢Twilight的解决使用jwt刷新token带来的问题:[https://segmentfault.com/a/1190000013151506](https://segmentfault.com/a/1190000013151506)
7. 感谢chuhx的shiro拦截器，返回json数据:[https://blog.csdn.net/chuhx/article/details/51148877](https://blog.csdn.net/chuhx/article/details/51148877)

### 项目介绍

1. RESTful API
2. Maven集成Mybatis Geneator
3. Shiro + Java-JWT实现无状态鉴权机制(Token)
4. 密码加密(未使用Shiro自带的MD5 + 盐的方式)，采用AES-128 + Base64的方式
5. 集成Redis(Jedis)，重写Shiro缓存机制(Redis)
6. 将Jedis工具类与SpringBoot整合，项目启动注入JedisPool连接池
7. Redis中保存RefreshToken信息，做到JWT的可控性
8. 根据RefreshToken自动刷新AccessToken

#### 关于Shiro + Java-JWT实现无状态鉴权机制(Token)
```txt
首先Post用户名与密码到user/login进行登入，如果成功返回一个加密的AccessToken，失败的话直接返回401错误(帐号或密码不正确)，以
后访问都带上这个AccessToken即可，鉴权流程主要是重写了Shiro的入口过滤器JWTFilter(BasicHttpAuthenticationFilter)，判断请求
Header里面是否包含Authorization字段，有就进行Shiro的Token登录认证授权(用户访问每一个需要权限的请求必须在Header中添加Author
ization字段存放AccessToken)，没有就以游客直接访问(有权限管控的话，以游客访问就会被拦截)
```

#### 关于AES-128 + Base64当两个用户的明文密码相同时进行加密，会发现数据库中存在相同结构的暗文密码
```txt
Shiro默认是以MD5 + 盐的形式解决了这个问题(详细自己百度)，我采用AES-128 + Base64是以帐号+密码的形式进行加密密码，因为帐号具
有唯一性，所以也不会出现相同结构的暗文密码这个问题
```

#### 关于将Jedis工具类与SpringBoot整合
```txt
本来是直接将JedisUtil注入为Bean，每次使用直接@Autowired注入使用即可，但是在重写Shiro的CustomCache无法注入JedisUtil，所以
就改成静态注入JedisPool连接池，JedisUtil工具类还是直接调用静态方法，无需@Autowired注入
```

#### 关于Redis中保存RefreshToken信息，做到JWT的可控性
```txt
登录认证通过后返回AccessToken信息(在AccessToken中保存当前的时间戳和帐号)，同时在Redis中设置一条以帐号为Key，Value为当前时
间戳(登录时间)的RefreshToken，现在认证时必须AccessToken没失效以及Redis存在所对应的RefreshToken，且RefreshToken时间戳和Ac
cessToken信息中时间戳一致才算认证通过，这样可以做到JWT的可控性，如果重新登录获取了新的AccessToken，旧的AccessToken就认证不
了，因为Redis中所存放的的RefreshToken时间戳信息只会和最新的AccessToken信息中携带的时间戳一致，这样每个用户就只能使用最新的
AccessToken认证，Redis的RefreshToken也可以用来判断用户是否在线，如果删除Redis的某个RefreshToken，那这个RefreshToken所对
应的AccessToken之后也无法通过认证了，就相当于控制了用户的登录，可以剔除用户
```

#### 关于根据RefreshToken自动刷新AccessToken
```txt
本身AccessToken的过期时间为5分钟(配置文件可配置)，RefreshToken过期时间为30分钟(配置文件可配置)，当登录后时间过了5分钟之后
，当前AccessToken便会过期失效，再次带上AccessToken访问JWT会抛出TokenExpiredException异常说明Token过期，开始判断是否要进
行AccessToken刷新，首先Redis查询RefreshToken是否存在，以及时间戳和过期AccessToken所携带的时间戳是否一致，如果存在且一致就
进行AccessToken刷新，且设置RefreshToken的过期时间为剩余过期时间加上一个AccessToken的过期时间，主要详情查看JWTFilter代码，
最终将刷新的AccessToken存放在Response的Header中的Authorization字段返回(前端进行获取替换，下次用新的AccessToken进行访问)
```

### 软件架构

1. SpringBoot + Mybatis核心框架
2. PageHelper插件 + 通用Mapper插件
3. Shiro + Java-JWT无状态鉴权认证机制
4. Redis(Jedis)缓存框架

### 安装教程

1. 数据库帐号密码默认为root，如有修改，请自行修改配置文件application.yml
2. 解压后执行src\main\resources\sql\MySQL.sql脚本创建数据库和表
3. Redis需要自行安装Redis服务，端口密码默认
4. SpringBoot直接启动即可，测试工具PostMan

### 使用说明

#### Mybatis Geneator使用

##### 先配置src\main\resources\generator\generatorConfig.xml文件(默认配置都在原来包的下一级reverse包下)，在pom.xml这一级目录(即项目根目录下)的命令行窗口执行(前提是配置了mvn)(IDEA可以直接在Maven窗口Plugins中双击执行)
```
mvn mybatis-generator:generate
```

#### PostMan使用

##### Token获取及使用
```txt
先设置Content-Type为application/json
```
![image text](https://github.com/wang926454/Reader/blob/master/SBAPIJWT/image/201810060001.PNG)
```txt
然后填写请求参数帐号密码信息
```
![image text](https://github.com/wang926454/Reader/blob/master/SBAPIJWT/image/201810060002.PNG)
```txt
进行请求访问，请求访问成功
```
![image text](https://github.com/wang926454/Reader/blob/master/SBAPIJWT/image/201810060003.PNG)
```txt
点击查看Header信息的Authorization属性即是Token字段
```
![image text](https://github.com/wang926454/Reader/blob/master/SBAPIJWT/image/201810060004.PNG)
```txt
访问需要权限的请求将Token字段放在Header信息的Authorization属性访问即可
```
![image text](https://github.com/wang926454/Reader/blob/master/SBAPIJWT/image/201810060005.PNG)
```txt
Token的自动刷新也是在Token失效时返回新的Token在Header信息的Authorization属性
```

### 接口文档

#### 接口目录

[1、获取用户列表](#1获取用户列表)<br/>
[2、获取在线用户](#2获取在线用户)<br/>
[3、登录授权](#3登录授权)<br/>
[4、测试登录](#4测试登录)<br/>
[5、测试登录注解](#5测试登录注解)<br/>
[6、获取指定用户](#6获取指定用户)<br/>
[7、新增用户](#7新增用户)<br/>
[8、更新用户](#8更新用户)<br/>
[9、删除用户](#9删除用户)<br/>
[10、剔除在线用户](#10剔除在线用户)<br/>

#### 接口列表

#### 1、获取用户列表

##### 请求URL:  
```
http://localhost:8080/user
```

##### 示例：
 [http://localhost:8080/user?page=1&&rows=10](http://localhost:8080/user?page=1&&rows=10)

##### 请求方式: 
```
GET
```

##### 请求参数

|参数|是否必选|数据类型|参数类型|说明|
|:- |:-: |:-:  |:-: |:- |
|Authorization |Y |string |header |Token |
|page |Y |int |query |当前页数 |
|rows |Y |int |query |每页条数 |

##### 返回示例：

```javascript
{
    "code": 200,
    "msg": "查询成功(Query was successful)",
    "data": {
        "data": [
            {
                "id": 1,
                "account": "admin",
                "password": "QUJBNUYyM0M3OTNEN0I4MUFBOTZBOTkwOEI1NDI0MUE=",
                "username": "admin",
                "regTime": "2018-10-06 17:25:16",
                "loginTime": null
            },
            ...  // 共10条数据
        ],
        "count": 3
    }
}
```

#### 2、获取在线用户

##### 请求URL:  
```
http://localhost:8080/user/online
```

##### 示例：
 [http://localhost:8080/user/online](http://localhost:8080/user/online)

##### 请求方式: 
```
GET
```

##### 请求参数

|参数|是否必选|数据类型|参数类型|说明|
|:- |:-: |:-:  |:-: |:- |
|Authorization |Y |string |header |Token |

##### 返回示例：

```javascript
{
    "code": 200,
    "msg": "查询成功(Query was successful)",
    "data": [
        {
            "id": 1,
            "account": "admin",
            "password": "QUJBNUYyM0M3OTNEN0I4MUFBOTZBOTkwOEI1NDI0MUE=",
            "username": "admin",
            "regTime": "2018-10-06 17:25:16",
            "loginTime": "2018-10-06 21:10:02"
        }
    ]
}
```

#### 3、登录授权

##### 请求URL:  
```
http://localhost:8080/user/login
```

##### 请求方式: 
```
POST
```

##### 请求参数

|参数|是否必选|数据类型|参数类型|说明|
|:- |:-: |:-:  |:-: |:- |
|account |Y |string |param |帐号 |
|password |Y |string |param |密码 |

##### 返回示例：

```javascript
{
    "code": 200,
    "msg": "登录成功(Login Success.)",
    "data": null
}
```

#### 4、测试登录

##### 请求URL:  
```
http://localhost:8080/user/article
```

##### 示例：
 [http://localhost:8080/user/article](http://localhost:8080/user/article)

##### 请求方式: 
```
GET
```

##### 请求参数

|参数|是否必选|数据类型|参数类型|说明|
|:- |:-: |:-:  |:-: |:- |
|Authorization |N |string |header |Token |

##### 返回示例：

```javascript
{
    "code": 200,
    "msg": "您已经登录了(You are already logged in)",
    "data": null
}
```

#### 5、测试登录注解

##### 请求URL:  
```
http://localhost:8080/user/article2
```

##### 示例：
 [http://localhost:8080/user/article2](http://localhost:8080/user/article2)

##### 请求方式: 
```
GET
```

##### 请求参数

|参数|是否必选|数据类型|参数类型|说明|
|:- |:-: |:-:  |:-: |:- |
|Authorization |Y |string |header |Token |

##### 返回示例：

```javascript
{
    "code": 200,
    "msg": "您已经登录了(You are already logged in)",
    "data": null
}
```

#### 6、获取指定用户

##### 请求URL:  
```
http://localhost:8080/user/{id}
```

##### 示例：
 [http://localhost:8080/user/1](http://localhost:8080/user/1)

##### 请求方式: 
```
GET
```

##### 请求参数

|参数|是否必选|数据类型|参数类型|说明|
|:- |:-: |:-:  |:-: |:- |
|Authorization |Y |string |header |Token |
|id |Y |int |param |用户ID |

##### 返回示例：

```javascript
{
    "code": 200,
    "msg": "查询成功(Query was successful)",
    "data": {
        "id": 1,
        "account": "admin",
        "password": "QUJBNUYyM0M3OTNEN0I4MUFBOTZBOTkwOEI1NDI0MUE=",
        "username": "admin",
        "regTime": "2018-10-06 17:25:16",
        "loginTime": null
    }
}
```

#### 7、新增用户

##### 请求URL:  
```
http://localhost:8080/user
```

##### 请求方式: 
```
POST
```

##### 请求参数

|参数|是否必选|数据类型|参数类型|说明|
|:- |:-: |:-:  |:-: |:- |
|Authorization |Y |string |header |Token |
|account |Y |string |param |帐号 |
|password |Y |string |param |密码 |
|username |Y |string |param |用户名 |

##### 返回示例：

```javascript
{
    "code": 200,
    "msg": "新增成功(Insert Success)",
    "data": {
        "id": 5,
        "account": "wang926454",
        "password": "ODg2OTZCOTEzNDA2NURGQjhBNjQzNTJBODE1QzNEQTI=",
        "username": "wang926454",
        "regTime": "2018-10-06 21:43:09",
        "loginTime": null
    }
}
```

#### 8、更新用户

##### 请求URL:  
```
http://localhost:8080/user
```

##### 请求方式: 
```
PUT
```

##### 请求参数

|参数|是否必选|数据类型|参数类型|说明|
|:- |:-: |:-:  |:-: |:- |
|Authorization |Y |string |header |Token |
|id |Y |int |param |用户ID |
|account |Y |string |param |帐号 |
|password |Y |string |param |密码 |
|username |Y |string |param |用户名 |

##### 返回示例：

```javascript
{
    "code": 200,
    "msg": "更新成功(Update Success)",
    "data": {
        "id": 5,
        "account": "wang926454",
        "password": "ODg2OTZCOTEzNDA2NURGQjhBNjQzNTJBODE1QzNEQTI=",
        "username": "wang926454",
        "regTime": null,
        "loginTime": null
    }
}
```

#### 9、删除用户

##### 请求URL:  
```
http://localhost:8080/user/{id}
```

##### 示例：
 [http://localhost:8080/user/5](http://localhost:8080/user/5)

##### 请求方式: 
```
DELETE
```

##### 请求参数

|参数|是否必选|数据类型|参数类型|说明|
|:- |:-: |:-:  |:-: |:- |
|Authorization |Y |string |header |Token |
|id |Y |int |param |用户ID |

##### 返回示例：

```javascript
{
    "code": 200,
    "msg": "删除成功(Delete Success)",
    "data": null
}
```

#### 10、剔除在线用户

##### 请求URL:  
```
http://localhost:8080/user/online/{id}
```

##### 示例：
 [http://localhost:8080/user/online/1](http://localhost:8080/user/online/1)

##### 请求方式: 
```
DELETE
```

##### 请求参数

|参数|是否必选|数据类型|参数类型|说明|
|:- |:-: |:-:  |:-: |:- |
|Authorization |Y |string |header |Token |
|id |Y |int |param |用户ID |

##### 返回示例：

```javascript
{
    "code": 200,
    "msg": "剔除成功(Delete Success)",
    "data": null
}
```

### 参与贡献

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
