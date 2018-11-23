CREATE database if NOT EXISTS `xxl-rpc` default character set utf8 collate utf8_general_ci;
use `xxl-rpc`;


CREATE TABLE `XXL_RPC_REGISTRY` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `biz` varchar(255) NOT NULL COMMENT '业务标识',
  `env` varchar(255) NOT NULL COMMENT '环境标识',
  `key` varchar(255) NOT NULL COMMENT '注册Key',
  `data` text NOT NULL COMMENT '注册Value有效数据',
  `version` varchar(255) NOT NULL COMMENT '版本',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态：0-正常、1-锁定',
  PRIMARY KEY (`id`),
  KEY `I_b_e_k` (`biz`,`env`,`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `XXL_RPC_REGISTRY_DATA` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `biz` varchar(255) NOT NULL COMMENT '业务标识',
  `env` varchar(255) NOT NULL COMMENT '环境标识',
  `key` varchar(255) NOT NULL COMMENT '注册Key',
  `value` varchar(255) NOT NULL COMMENT '注册Value',
  `updateTime` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `XXL_RPC_REGISTRY_MESSAGE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '消息类型：0-注册更新',
  `data` text NOT NULL COMMENT '消息内容',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

