# SBAPIJWT

#### 搭建参考

1. 感谢SmithCruise的Shiro+JWT+Spring Boot Restful简易教程:[https://www.jianshu.com/p/f37f8c295057](https://www.jianshu.com/p/f37f8c295057)

#### 项目介绍

1. SpringBoot整合Mybatis + 分页插件PageHelper + 通用Mapper插件 + Maven集成Mybatis Geneator
2. 整合Shiro + JWT认证方式
3. RESTful API

#### 软件架构

1. SpringBoot + Mybatis + PageHelper + 通用Mapper + Shiro + Java-JWT

#### 安装教程

1. 数据库帐号密码默认为root，如有修改，请自行修改配置文件application.yml
2. 解压后执行src\main\resources\sql\MySQL.sql脚本创建数据库和表
3. SpringBoot直接启动即可，测试工具PostMan

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
