## ShiroJwt(dev)

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/wang926454/ShiroJwt/pulls)
[![GitHub stars](https://img.shields.io/github/stars/wang926454/ShiroJwt.svg?style=social&label=Stars)](https://github.com/wang926454/ShiroJwt)
[![GitHub forks](https://img.shields.io/github/forks/wang926454/ShiroJwt.svg?style=social&label=Fork)](https://github.com/wang926454/ShiroJwt)

> Master分支:[https://github.com/dolyw/ShiroJwt](https://github.com/dolyw/ShiroJwt)  
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

开发分支主要在后台解决了 [#29 Token刷新并发处理](https://github.com/dolyw/ShiroJwt/issues/29) 这个问题

`Token` 刷新的锁是直接使用的 `synchronized` 关键字，如果需要可以修改为 `Redission` 分布式锁

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
