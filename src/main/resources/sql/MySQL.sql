drop database sbapijwt;

create database sbapijwt;

use sbapijwt;

create table user (
id int primary key auto_increment COMMENT "ID",
account varchar(20)  COMMENT "帐号",
password varchar(20) COMMENT "密码",
username varchar(20) COMMENT "昵称",
reg_time datetime not null COMMENT "注册时间"
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT "用户表";

CREATE TABLE role (
id int primary key auto_increment COMMENT "ID",
name varchar(128) not null unique COMMENT "角色名称"
/*available int COMMENT '是否可用 1-可用 0-不可用'*/
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT "角色表";


CREATE TABLE permission (
id int primary key auto_increment COMMENT "ID",
name varchar(128) COMMENT '资源名称',
/*type varchar(32) COMMENT '资源类型：menu,permission,button',
url varchar(128) COMMENT '访问url地址',*/
per_code varchar(128) not null unique COMMENT '权限代码字符串'
/*available int COMMENT '是否可用 1-可用 0-不可用'*/
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT "资源表";

CREATE TABLE user_role (
id int primary key auto_increment COMMENT "ID",
user_id int COMMENT '用户id',
role_id int COMMENT '角色id',
foreign key (user_id) references user (id),
foreign key (role_id) references role (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT "用户角色表";

CREATE TABLE role_permission (
id int primary key auto_increment COMMENT "ID",
role_id int COMMENT '角色id',
permission_id int COMMENT '权限id',
foreign key (role_id) references role (id),
foreign key (permission_id) references permission (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT "角色资源表";

insert into user values(null, "admin", "admin", "admin", now());
insert into user values(null, "wang", "wang", "wang", now());
insert into user values(null, "guest", "guest", "guest", now());

insert into role values(null, "admin");
insert into role values(null, "customer");

insert into user_role values(null, 1, 1);
insert into user_role values(null, 2, 2);

insert into permission values(null, "查看用户", "user:view");
insert into permission values(null, "操作用户", "user:edit");

insert into role_permission values(null, 1, 1);
insert into role_permission values(null, 1, 2);
insert into role_permission values(null, 2, 1);

