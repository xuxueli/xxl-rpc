#
# XXL-RPC
# Copyright (c) 2015-present, xuxueli.

CREATE database if NOT EXISTS `xxl_rpc` default character set utf8mb4 collate utf8mb4_unicode_ci;
use `xxl_rpc`;

SET NAMES utf8mb4;

CREATE TABLE `xxl_rpc_registry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `env` varchar(50) NOT NULL COMMENT '环境标识',
  `key` varchar(255) NOT NULL COMMENT '注册Key',
  `data` text NOT NULL COMMENT '注册Value有效数据',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态：0-正常、1-锁定',
  PRIMARY KEY (`id`),
  UNIQUE KEY `I_e_k` (`env`,`key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_rpc_registry_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `env` varchar(50) NOT NULL COMMENT '环境标识',
  `key` varchar(255) NOT NULL COMMENT '注册Key',
  `value` varchar(255) NOT NULL COMMENT '注册Value',
  `updateTime` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `I_e_k_v` (`env`,`key`,`value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_rpc_registry_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '消息类型：0-注册更新',
  `data` text NOT NULL COMMENT '消息内容',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

commit;



/*
    xxl_rpc_service：服务
        appname：服务唯一标识（如 xx-project ）
        name：服务名称
        desc：服务描述
        access_token：服务注册token
    xxl_rpc_environment：环境
        env：环境唯一标识（如 dev、test、prod ）
        name：环境名称
        desc：环境描述
    xxl_rpc_instance：注册节点实例
        appname：服务唯一标识
        env：环境唯一标识
        tag：标签（空-正常服务；非空-预留业务链路使用；）
        ip：IP地址
        port：端口号
        weight：权重（百分比）
        status：健康状态（0-正常；1-禁用；2-锁定/人工注册；）
    xxl_rpc_broadcast：
        type：消息类型（1-服务注册；）
        content：消息正文，json结构体
    xxl_rpc_user
        username：用户名
        password：密码，md5(原始密码)
        token：登录token，每次登录由“md5(password + 时间戳/随机加盐)”生成；
        role：角色（管理员-全部读写；普通用户-只读）

#####
    - registry/remove/discovery
    - monitor(long-polling)

#####
    - 1、provider：服务提供者；
    - 2、invoker：服务消费者；
    - 3、serializer: 序列化模块；
    - 4、remoting：网络通讯模块；
    - 5、registry：服务注册模块；
    - 6、admin：服务管理中心，提供服务注册、运营管理、健康监控等能力（非必选）；

*/

##### 参考 conf
/*
CREATE TABLE `xxl_conf_env` (
                                `env` varchar(100) NOT NULL COMMENT 'Env',
                                `title` varchar(100) NOT NULL COMMENT '环境名称',
                                `order` tinyint(4) NOT NULL DEFAULT '0' COMMENT '显示排序',
                                PRIMARY KEY (`env`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `xxl_conf_project` (
                                    `appname` varchar(100) NOT NULL COMMENT 'AppName',
                                    `title` varchar(100) NOT NULL COMMENT '项目名称',
                                    PRIMARY KEY (`appname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `xxl_conf_user` (
                                 `username` varchar(100) NOT NULL COMMENT '账号',
                                 `password` varchar(100) NOT NULL COMMENT '密码',
                                 `permission` tinyint(4) NOT NULL DEFAULT '0' COMMENT '权限：0-普通用户、1-管理员',
                                 `permission_data` varchar(1000) DEFAULT NULL COMMENT '权限配置数据',
                                 PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `xxl_conf_node` (
                                 `env` varchar(100) NOT NULL COMMENT 'Env',
                                 `key` varchar(200) NOT NULL COMMENT '配置Key',
                                 `appname` varchar(100) NOT NULL COMMENT '所属项目AppName',
                                 `title` varchar(100) NOT NULL COMMENT '配置描述',
                                 `value` varchar(2000) DEFAULT NULL COMMENT '配置Value',
                                 PRIMARY KEY (`env`,`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `xxl_conf_node_log` (
                                     `env` varchar(255) NOT NULL COMMENT 'Env',
                                     `key` varchar(200) NOT NULL COMMENT '配置Key',
                                     `title` varchar(100) NOT NULL COMMENT '配置描述',
                                     `value` varchar(2000) DEFAULT NULL COMMENT '配置Value',
                                     `addtime` datetime NOT NULL COMMENT '操作时间',
                                     `optuser` varchar(100) NOT NULL COMMENT '操作人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `xxl_conf_node_msg` (
                                     `id` int(11) NOT NULL AUTO_INCREMENT,
                                     `addtime` datetime NOT NULL,
                                     `env` varchar(100) NOT NULL COMMENT 'Env',
                                     `key` varchar(200) NOT NULL COMMENT '配置Key',
                                     `value` varchar(2000) DEFAULT NULL COMMENT '配置Value',
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
*/

