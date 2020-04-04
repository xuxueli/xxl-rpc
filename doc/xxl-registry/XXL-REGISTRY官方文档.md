## 《分布式服务注册中心XXL-REGISTRY》

[![Actions Status](https://github.com/xuxueli/xxl-registry/workflows/Java%20CI/badge.svg)](https://github.com/xuxueli/xxl-registry/actions)
[![Docker Status](https://img.shields.io/badge/docker-passing-brightgreen.svg)](https://hub.docker.com/r/xuxueli/xxl-registry-admin/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-registry/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-registry/)
[![GitHub release](https://img.shields.io/github/release/xuxueli/xxl-registry.svg)](https://github.com/xuxueli/xxl-registry/releases)
[![License](https://img.shields.io/badge/license-GPLv3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0.html)
[![donate](https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat-square)](https://www.xuxueli.com/page/donate.html)

[TOCM]

[TOC]

## 一、简介

### 1.1 概述
XXL-REGISTRY 是一个轻量级分布式服务注册中心，拥有"轻量级、秒级注册上线、多环境、跨语言、跨机房"等特性。现已开放源代码，开箱即用。

### 1.2 特性

- 1、轻量级：基于DB与磁盘文件，只需要提供一个DB实例即可，无第三方依赖；
- 2、实时性：借助内部广播机制，新服务上线、下线，可以在1s内推送给客户端；
- 3、数据同步：注册中心会定期全量同步数据至磁盘文件，清理无效服务，确保服务数据实时可用；
- 4、性能：服务发现时仅读磁盘文件，性能非常高；服务注册、摘除时通过磁盘文件校验，防止重复注册操作；
- 5、扩展性：可方便、快速的横向扩展，只需保证服务注册中心配置一致即可，可借助负载均衡组件如Nginx快速集群部署；
- 6、多状态：服务内置三种状态：
    - 正常状态=支持动态注册、发现，服务注册信息实时更新；
    - 锁定状态=人工维护注册信息，服务注册信息固定不变；
    - 禁用状态=禁止使用，服务注册信息固定为空；
- 7、跨语言：注册中心提供HTTP接口（RESTFUL 格式）供客户端实用，语言无关，通用性更强；
- 8、兼容性：项目立项之初是为XXL-RPC量身设计，但是不限于XXL-RPC使用。兼容支持任何服务框架服务注册实用，如dubbo、springboot等；
- 9、跨机房：得益于服务注册中心集群关系对等特性，集群各节点提供幂等的配置服务；因此，异地跨机房部署时，只需要请求本机房服务注册中心即可，实现异地多活；
- 10、容器化：提供官方docker镜像，并实时更新推送dockerhub，进一步实现 "服务注册中心" 产品开箱即用；
- 11、访问令牌（accessToken）：为提升系统安全性，注册中心和客户端进行安全性校验，双方AccessToken匹配才允许通讯；


### 1.3 下载

#### 文档地址

- [中文文档](https://www.xuxueli.com/xxl-registry/)

#### 源码仓库地址

源码仓库地址 | Release Download
--- | ---
[https://github.com/xuxueli/xxl-registry](https://github.com/xuxueli/xxl-registry) | [Download](https://github.com/xuxueli/xxl-registry/releases)
[https://gitee.com/xuxueli0323/xxl-registry](https://gitee.com/xuxueli0323/xxl-registry) | [Download](https://gitee.com/xuxueli0323/xxl-registry/releases)  


#### 技术交流
- [社区交流](https://www.xuxueli.com/page/community.html)


### 1.4 环境
- Maven3+
- Jdk1.7+
- Mysql5.6+


## 二、快速入门

### 2.1 初始化 "服务注册中心" 数据库
请下载项目源码并解压，获取 "服务注册中心" 数据库初始化SQL脚本并执行即可

数据库初始化SQL脚本位置为:

    /xxl-registry/doc/db/xxl-registry-mysql.sql
    
"服务注册中心" 支持集群部署，集群情况下各节点务必连接同一个mysql实例;

### 2.2 编译项目
解压源码,按照maven格式将源码导入IDE, 使用maven进行编译即可，源码结构如下：

    - /doc
    - /xxl-registry-admin       ：分布式服务中心
    - /xxl-registry-client      ：客户端核心依赖；

### 2.3 配置部署“服务注册中心”

#### 步骤一：配置项目：
配置文件地址：

```
/xxl-registry/xxl-registry-admin/src/main/resources/application.properties
```

消息中心配置内容说明：

```
### 数据库配置
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxl-registry?Unicode=true&characterEncoding=UTF-8

### 服务注册数据磁盘同步目录
xxl.registry.data.filepath=/data/applogs/xxl-registry/registrydata

### 登陆信息配置
xxl.registry.login.username=admin
xxl.registry.login.password=123456
``` 

#### 步骤二：部署项目：

如果已经正确进行上述配置，可将项目编译打包部署。
访问地址：http://localhost:8080/xxl-registry-admin  (该地址接入方项目将会使用到，作为注册地址)，登录后运行界面如下图所示

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-registry/images/img_01.png "在这里输入图片标题")

至此“服务注册中心”项目已经部署成功。

#### 步骤三：服务注册中心集群（可选）：
服务注册中心支持集群部署，提升消息系统容灾和可用性。

集群部署时，几点要求和建议：
- DB配置保持一致；
- 登陆账号配置保持一致；
- 建议：推荐通过nginx为集群做负载均衡，分配域名。访问、客户端使用等操作均通过该域名进行。

#### 其他：Docker 镜像方式搭建消息中心：
- 下载镜像

```
// Docker地址：https://hub.docker.com/r/xuxueli/xxl-registry-admin/
docker pull xuxueli/xxl-registry-admin
```

- 创建容器并运行

```
docker run -p 8080:8080 -v /tmp:/data/applogs --name xxl-registry-admin  -d xuxueli/xxl-registry-admin

/**
* 如需自定义 mysql 等配置，可通过 "PARAMS" 指定，参数格式 RAMS="--key=value  --key2=value2" ；
* 配置项参考文件：/xxl-registry/xxl-registry-admin/src/main/resources/application.properties
*/
docker run -e PARAMS="--spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxl-registry?Unicode=true&characterEncoding=UTF-8" -p 8080:8080 -v /tmp:/data/applogs --name xxl-registry-admin  -d xuxueli/xxl-registry-admin
```

### 2.4 接入 "服务注册中心" 示例

#### a、XXL-RPC 接入示例；

XXL-RPC默认将 "XXL-REGISTRY" 作为原生注册中心。可前往 XXL-RPC (https://github.com/xuxueli/xxl-rpc ) 示例项目参考如何接入 "XXL-REGISTRY" 。

#### b、其他Java语言项目接入示例；

客户端maven依赖地址：

```
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-registry-client</artifactId>
    <version>${最新稳定版}</version>
</dependency>
```

其他Java服务框架，可以借助原生提供的客户端JAR包快速接入使用，建议参考 XXL-RPC 提供的实例项目；

客户端JAR包内封装了与注册中心API服务交互的客户端代码，原生提供两个客户端类供实用：

- 基础客户端类（com.xxl.registry.client.XxlRegistryBaseClient）：借助该客户端类，可方便的与注册中心进行注册数据交互，如：服务注册、续约、摘除、发现服务、监控等等；
- 增强客户端类（com.xxl.registry.client.XxlRegistryClient）：该类为增强版本客户端类，内置客户端服务续约线程和服务注册信息监控线程。
通过该客户端类注册的服务，底层线程将会主动维护续约操作，通过该客户端类发现的服务信息，底层线程将会主动定期刷新并实时监控变更。
同时对服务数据进行缓存处理，业务方可放心实用不用担心性能问题。


客户端API实用示例代码如下：
```
// 注册中心客户端（基础类）
XxlRegistryBaseClient registryClient = new XxlRegistryBaseClient("http://localhost:8080/xxl-registry-admin/", null, "xxl-rpc", "test");

// 注册中心客户端（增强类）
XxlRegistryClient registryClient = new XxlRegistryClient("http://localhost:8080/xxl-registry-admin/", null, "xxl-rpc", "test");
 

// 服务注册 & 续约：
List<XxlRegistryDataParamVO> registryDataList = new ArrayList<>();
registryDataList.add(new XxlRegistryDataParamVO("service01", "address01"));
registryDataList.add(new XxlRegistryDataParamVO("service02", "address02"));

registryClient.registry(registryDataList);


// 服务摘除：
List<XxlRegistryDataParamVO> registryDataList = new ArrayList<>();
registryDataList.add(new XxlRegistryDataParamVO("service01", "address01"));
registryDataList.add(new XxlRegistryDataParamVO("service02", "address02"));

registryClient.remove(registryDataList);


// 服务发现：
Set<String> keys = new TreeSet<>();
keys.add("service01");
keys.add("service02");

Map<String, TreeSet<String>> serviceData = registryClient.discovery(keys);


// 服务监控：
Set<String> keys = new TreeSet<>();
keys.add("service01");
keys.add("service02");

registryClient.monitor(keys);

```

其他Java服务框架，如dubbo、springboot等，接入 "XXL-REGISTRY" 的示例项目，后续将会整理推出。

#### c、非Java语言项目接入； 
非Java语言项目，可以借助提供的 RESTFUL 格式API接口实现服务注册与发现功能。

参考章节 "三、注册中心API服务"


## 三、注册中心API服务（RESTFUL 格式）
服务注册中心为支持服务注册与发现功能，提供的 RESTFUL 格式API接口如下：


#### 3.1、服务注册 & 续约 API
说明：新服务注册上线1s内广播通知接入方；需要接入方循环续约，否则服务将会过期（三倍于注册中心心跳时间）下线；

```
地址格式：{服务注册中心跟地址}/registry

请求参数说明：
 1、accessToken：请求令牌；
 2、biz：业务标识
 2、env：环境标识
 3、registryDataList：服务注册信息

请求数据格式如下，放置在 RequestBody 中，JSON格式：
 
    {
        "accessToken" : "xx",
        "biz" : "xx",
        "env" : "xx",
        "registryDataList" : [{
            "key" : "service01",
            "value" : "address01"
        }]
    }
    
```

#### 3.2、服务摘除 API
说明：新服务摘除下线1s内广播通知接入方；

```
地址格式：{服务注册中心跟地址}/remove

请求参数说明：
 1、accessToken：请求令牌；
 2、biz：业务标识
 2、env：环境标识
 3、registryDataList：服务注册信息

请求数据格式如下，放置在 RequestBody 中，JSON格式：
 
    {
        "accessToken" : "xx",
        "biz" : "xx",
        "env" : "xx",
        "registryDataList" : [{
            "key" : "service01",
            "value" : "address01"
        }]
    }

```

#### 3.3、服务发现 API
说明：查询在线服务地址列表；

```
地址格式：{服务注册中心跟地址}/discovery

请求参数说明：
 1、accessToken：请求令牌；
 2、biz：业务标识
 2、env：环境标识
 3、keys：服务注册Key列表
 
请求数据格式如下，放置在 RequestBody 中，JSON格式：
 
    {
        "accessToken" : "xx",
        "biz" : "xx",
        "env" : "xx",
        "keys" : [
            "service01",
            "service02"
        ]
    }

```

#### 3.4、服务监控 API
说明：long-polling 接口，主动阻塞一段时间（三倍于注册中心心跳时间）；直至阻塞超时或服务注册信息变动时响应；

```
地址格式：{服务注册中心跟地址}/monitor

请求参数说明：
 1、accessToken：请求令牌；
 2、biz：业务标识
 2、env：环境标识
 3、keys：服务注册Key列表
 
请求数据格式如下，放置在 RequestBody 中，JSON格式：
 
    {
        "accessToken" : "xx",
        "biz" : "xx",
        "env" : "xx",
        "keys" : [
            "service01",
            "service02"
        ]
    }
    
```


## 四、系统设计

### 4.1 系统架构图
![输入图片说明](https://www.xuxueli.com/doc/static/xxl-registry/images/img_02.png "在这里输入图片标题")

### 4.2 原理解析
XXL-REGISTRY内部通过广播机制，集群节点实时同步服务注册信息，确保一致。客户端借助 long pollong 实时感知服务注册信息，简洁、高效；

### 4.3 跨机房（异地多活）
得益于服务注册中心集群关系对等特性，集群各节点提供幂等的服务注册服务；因此，异地跨机房部署时，只需要请求本机房服务注册中心即可，实现异地多活；

举个例子：比如机房A、B 内分别部署服务注册中心集群节点。即机房A部署 a1、a2 两个服务注册中心服务节点，机房B部署 b1、b2 两个服务注册中心服务节点；

那么各机房内应用只需要请求本机房内部署的服务注册中心节点即可，不需要跨机房调用。即机房A内业务应用请求 a1、a2 获取配置、机房B内业务应用 b1、b2 获取配置。

这种跨机房部署方式实现了配置服务的 "异地多活"，拥有以下几点好处：

- 1、注册服务响应更快：注册请求本机房内搞定；
- 2、注册服务更稳定：注册请求不需要跨机房，不需要考虑复杂的网络情况，更加稳定；
- 2、容灾性：即使一个机房内服务注册中心全部宕机，仅会影响到本机房内应用加载服务，其他机房不会受到影响。

### 4.4 一致性
类似 Raft 方案，更轻量级、稳定；
- Raft：Leader统一处理变更操作请求，一致性协议的作用具化为保证节点间操作日志副本(log replication)一致，以term作为逻辑时钟(logical clock)保证时序，节点运行相同状态机(state machine)得到一致结果。
- xxl-registry：
    - Leader（统一处理分发变更请求）：DB消息表（仅变更时产生消息，消息量较小，而且消息轮训存在间隔，因此消息表压力不会太大；）；
    - state machine（顺序操作日志副本并保证结果一直）：顺序消费消息，保证本地数据一致，并通过周期全量同步进一步保证一致性；


## 五、版本更新日志
### 5.1 版本 v1.0.0 Release Notes[2018-12-01]
- 1、轻量级：基于DB与磁盘文件，只需要提供一个DB实例即可，无第三方依赖；
- 2、实时性：借助内部广播机制，新服务上线、下线，可以在1s内推送给客户端；
- 3、数据同步：注册中心内部10s会全量同步一次磁盘数据，清理无效服务，确保服务数据实时可用；
- 4、性能：服务发现时仅读磁盘文件，性能非常高；服务注册、摘除时通过磁盘文件校验，防止重复注册操作；
- 5、扩展性：可方便、快速的横向扩展，只需保证服务注册中心配置一致即可，可借助负载均衡组件如Nginx快速集群部署；
- 6、多状态：服务内置三种状态：
    - 正常状态=支持动态注册、发现，服务注册信息实时更新；
    - 锁定状态=人工维护注册信息，服务注册信息固定不变；
    - 禁用状态=禁止使用，服务注册信息固定为空；
- 7、跨语言：注册中心提供HTTP接口（RESTFUL 格式）供客户端实用，语言无关，通用性更强；
- 8、兼容性：项目立项之初是为XXL-RPC量身设计，但是不限于XXL-RPC使用。兼容支持任何服务框架服务注册实用，如dubbo、springboot等；
- 9、跨机房：得益于服务注册中心集群关系对等特性，集群各节点提供幂等的配置服务；因此，异地跨机房部署时，只需要请求本机房服务注册中心即可，实现异地多活；
- 10、容器化：提供官方docker镜像，并实时更新推送dockerhub，进一步实现 "服务注册中心" 产品开箱即用；
- 11、long polling 超时时间优化；服务端默认 30s 超时限制；客户端默认 60s 阻塞登台；二者以较小者为准，建议客户端大于服务端。

### 5.2 版本 v1.0.1 Release Notes[2018-12-20]
- 1、访问令牌（accessToken）：为提升系统安全性，注册中心和客户端进行安全性校验，双方AccessToken匹配才允许通讯；
- 2、底层通讯参数统一：请求参数统一由 postbody 发送接收，数据格式见公共消息体 "XxlRegistryParamVO"，内部包含 accessToken、biz、env 等属性；
- 3、环境属性 "env" 长度限制调整为 "2~255" ，兼容 "qa"、"dev" 等短环境标识；
- 4、升级 pom 依赖至较新版本；

### 5.3 版本 v1.0.2 Release Notes[2018-02-21]
- 1、服务端空值也支持响应，客户端注册信息发现null值缓存，避免缓存穿透；
- 2、客户端配置监控逻辑优化，避免异常情况下重试请求太频繁；
- 3、客户端日志优化：仅变更日志保留为info级别，非核心日志调整为debug级别；
- 4、内部JSON组件优化，支持多级父类属性序列化；
- 5、移除冗余属性，如version等； 
- 6、服务注册中心全量同步线程优化，对齐起始时间，避免集群节点数据不一致；

### 5.4 版本 v1.1.0 Release Notes[2019-11-16]
- 1、注册日志文件加载方式优化，修复文件名乱码问题；
- 2、修复服务注册version不匹配问题；
- 3、升级依赖版本，如slf4j-api/spring-boot/mybatis/mysql等；
- 4、小概率情况下底层通讯乱码问题修复；

### 5.4 版本 v1.1.1 Release Notes[迭代中]



### TODO
- 服务端：注册IP黑名单、白名单；
- 客户端：瞬间下线拒绝，比如超过 90% 服务批量下线，本次下线标记失败，连续三次才生效；防止注册中心故障，导致整体服务不可用；
- 注册方式附属信息；
    - 服务注册，支持节点权重配置；
    - 注册服务支持Tag属性；如机房TAG，客户端优先使用本机房，即机房TAG一致的服务；
- springboot、dubbo 示例；
- 单机版本：H2数据库;
- 异地多活：注册中心无中心服务；
- 同机房读：服务支持Region属性，优先使用本Region服务；


## 六、其他

### 6.1 项目贡献
欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-registry/issues/) 讨论新特性或者变更。

### 6.2 用户接入登记
更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-registry/issues/1 ) 登记，登记仅仅为了产品推广。

### 6.3 开源协议和版权
产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2015-present, xuxueli.

---
### 捐赠
无论金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](https://www.xuxueli.com/page/donate.html )
