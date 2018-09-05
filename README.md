# SBAPIJWT

> 前端地址:[https://github.com/wang926454/VueStudy/tree/master/VueStudy08-JWT](https://github.com/wang926454/VueStudy/tree/master/VueStudy08-JWT)

#### 搭建参考

1. 感谢SmithCruise的Shiro+JWT+Spring Boot Restful简易教程:[https://www.jianshu.com/p/f37f8c295057](https://www.jianshu.com/p/f37f8c295057)
2. 感谢王洪玉的[Shiro入门]（一）使用Redis作为缓存管理器:[https://blog.csdn.net/why15732625998/article/details/78729254](https://blog.csdn.net/why15732625998/article/details/78729254)

#### 项目介绍

1. RESTful API
2. Maven集成Mybatis Geneator
3. 密码加密(未使用Shiro自带的MD5 + 盐的方式)，采用AES-128 + Base64的方式
4. 集成Redis(Jedis)，重写Shiro缓存机制(Redis)

##### 关于AES-128 + Base64加密后当两个用户的密码相同时，会发现数据库中存在相同结构的密码
```txt
Shiro默认是以MD5 + 盐的形式解决了这个问题(详细自己百度)，我采用AES-128 + Base64是以帐号+密码的形式进行加密，因为帐号具有唯一
性，所以也不会出现相同结构密码这个问题
```

#### 软件架构

1. SpringBoot + Mybatis核心框架
2. PageHelper插件 + 通用Mapper插件
3. Shiro + Java-JWT + AES-128认证
4. Redis(Jedis)缓存

#### 安装教程

1. 数据库帐号密码默认为root，如有修改，请自行修改配置文件application.yml
2. 解压后执行src\main\resources\sql\MySQL.sql脚本创建数据库和表
3. Redis需要自行安装Redis服务，端口密码默认，启动服务后正常启动即可
4. SpringBoot直接启动即可，测试工具PostMan

#### 使用说明

1. Mybatis Geneator使用，先配置src\main\resources\generator\generatorConfig.xml文件(默认配置都在原来包的下一级reverse包下)，在pom.xml这一级目录(即项目根目录下)的命令行窗口执行(前提是配置了mvn)(IDEA可以直接在Maven窗口Plugins中双击执行)
```
mvn mybatis-generator:generate
```

#### 参与贡献

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
