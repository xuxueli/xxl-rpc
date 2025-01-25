## 《RPC服务框架XXL-RPC》

[![Actions Status](https://github.com/xuxueli/xxl-rpc/workflows/Java%20CI/badge.svg)](https://github.com/xuxueli/xxl-rpc/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-rpc/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-rpc/)
[![GitHub release](https://img.shields.io/github/release/xuxueli/xxl-rpc.svg)](https://github.com/xuxueli/xxl-rpc/releases)
[![GitHub stars](https://img.shields.io/github/stars/xuxueli/xxl-rpc)](https://github.com/xuxueli/xxl-rpc/)
![License](https://img.shields.io/github/license/xuxueli/xxl-rpc.svg)
[![donate](https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat-square)](https://www.xuxueli.com/page/donate.html)

[TOCM]

[TOC]

## 一、简介

### 1.1 概述
XXL-RPC 是一个RPC服务框架，提供一站式服务通信及运营能力。拥有“轻量级、高性能、负载均衡、故障容错、安全性、注册发现、服务治理”等分布式特性。现已开放源代码，开箱即用。

### 1.2 特性

- 1、易学易用：无缝集成SpringBoot，三分钟即可上手；
- 2、服务透明：系统完整的封装了底层通信细节，开发时调用远程服务就像调用本地服务，在提供远程调用能力时不损失本地调用的语义简洁性；
- 3、多调用类型：支持多种调用类型，包括：SYNC、ONEWAY、FUTURE、CALLBACK 等；
- 4、多通讯协议：支持多种通讯协议，支持TCP、HTTP；
- 5、多序列化方案：支持多种序列化协议，包括：HESSIAN/2、HESSIAN1、Gson、PROTOSTUFF、KRYO 等序列化方案；
- 6、注册中心：内置服务注册中心支持服务动态发现，提供轻量级、一站式解决方案。也支持扩展集成其他注册中心，或者不使用注册中心、直接指定服务提供方机器地址调用；
- 7、负载均衡：支持多种负载均衡策略，包括：轮询、随机、LRU、LFU、一致性HASH等；
- 8、服务治理：提供服务治理能力，支持在线管理注册的服务信息，如服务锁定、IP禁用……等；
- 9、服务监控：支持在线监控服务调用统计信息以及服务健康状况等（计划中）；
- 10、故障容错：支持自动巡检线上服务并摘除故障节点，消费方实时感知并移除失效节点将流量分发到其余节点，提高系统容错能力。
- 11、高兼容性：得益于优良的兼容性与模块化设计，不限制技术栈；除 spring/springboot 技术栈之外，理论上支持运行在任何Java代码中，甚至main方法直接启动运行；
- 12、泛化调用：支持服务调用方直接发起服务调用，不依赖服务方提供的API；
- 13、服务安全：支持序列化安全空间机制，以及通讯token加密机制；


### 1.3 背景

RPC（Remote Procedure Call Protocol，远程过程调用），调用远程服务就像调用本地服务，在提供远程调用能力时不损失本地调用的语义简洁性；

一般公司，尤其是大型互联网公司内部系统由上千上万个服务组成，不同的服务部署在不同机器，跑在不同的JVM上，此时需要解决两个问题：
- 1、如果我需要依赖别人的服务，但是别人的服务在远程机器上，我该如何调用？
- 2、如果其他团队需要使用我的服务，我该怎样发布自己的服务供他人调用？

“XXL-RPC”可以高效的解决这个问题：
- 1、如何调用：只需要知晓远程服务的stub和地址，即可方便的调用远程服务，同时调用透明化，就像调用本地服务一样简单；
- 2、如何发布：只需要提供自己服务的stub和地址，别人即可方便的调用我的服务，在开启注册中心的情况下服务动态发现，只需要提供服务的stub即可；

### 1.4 下载

#### 文档地址

- [中文文档](https://www.xuxueli.com/xxl-rpc/)

#### 源码仓库地址

源码仓库地址 | Release Download
--- | ---
[https://github.com/xuxueli/xxl-rpc](https://github.com/xuxueli/xxl-rpc) | [Download](https://github.com/xuxueli/xxl-rpc/releases)
[https://gitee.com/xuxueli0323/xxl-rpc](https://gitee.com/xuxueli0323/xxl-rpc) | [Download](https://gitee.com/xuxueli0323/xxl-rpc/releases)  


#### 技术交流
- [社区交流](https://www.xuxueli.com/page/community.html)


### 1.5 环境
- Jdk1.8+
- XXL-CONF 1.9.0+ (可选，支持无注册中心使用；默认适配 “xxl-conf” 实现动态服务注册与发现。)


## 二、快速入门

XXL-RPC 支持多种使用方式，并提供轻量级内置注册中心，下面分别介绍使用方式：
- springboot版本：与springboot无缝集成，默认适配 “xxl-conf” 实现动态服务注册与发现；
- frameless 无框架版本：不具体依赖项目框架，只需要JDK即可集成使用；

### 2.1、springboot 版本示例

#### 2.1.1、服务注册中心搭建

基于 XXL-CONF 搭建 “轻量级注册中心”：一行命令启动注册中心，一站式提供服务动态注册发现能力。
- XXL-CONF：分布式服务管理平台，作为服务 配置中心 与 注册中心，提供 动态配置管理、服务注册与发现 等核心能力；
- Github：https://github.com/xuxueli/xxl-conf 
- 官方文档：https://www.xuxueli.com/xxl-conf/
```
// 说明：xxl-conf 详细配置可参考官方文档
docker pull xuxueli/xxl-conf-admin
docker run -p 8080:8080 -v /tmp:/data/applogs --name xxl-conf-admin  -d xuxueli/xxl-conf-admin
```

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-conf/images/img_12.png "在这里输入图片标题")

#### 2.1.2、配置 “maven依赖”

```
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-rpc-core</artifactId>
    <version>${parent.version}</version>
</dependency>
```

#### 2.1.3、XXL-PRC接入配置

与Spring无缝集成，也支持无框架接入。
参考 xxl-rpc-sample-springboot 示例项目，client 和 server 两个sample子项目的 application.properties
- client配置：/xxl-rpc/xxl-rpc-samples/xxl-rpc-sample-springboot/xxl-rpc-sample-springboot-server/src/main/resources/application.properties
- server配置：/xxl-rpc/xxl-rpc-samples/xxl-rpc-sample-springboot/xxl-rpc-sample-springboot-client/src/main/resources/application.properties

配置项 | 说明
--- | ---
xxl.conf.client.appname | 服务唯一标识AppName；字母数字及中划线组成，必填
xxl.conf.client.env | 服务隔离环境，必填
xxl.conf.admin.address | XXL-CONF地址信息，多个逗号分隔，必填
xxl.conf.admin.accesstoken | XXL-CONF地址信息，必填（可以在 XXL-CONF “系统管理->AccessToken” 菜单申请）
xxl-rpc.invoker.open | 服务消费者，启用开关；
xxl-rpc.provider.open | 服务提供者，启用开关
xxl-rpc.provider.port | 服务提供者，服务通讯端口
xxl-rpc.provider.corePoolSize | 服务提供者，业务线程池core大小，小于0启动默认值
xxl-rpc.provider.maxPoolSize | 服务提供者，业务线程池max大小，小于0启动默认值


上述配置，本质将会驱动 XxlRpcSpringFactory 配置及初始化，如下：
```
XxlRpcSpringFactory factory = new XxlRpcSpringFactory();
factory.setBaseConfig(new BaseConfig(env, appname));
factory.setRegister(new XxlRpcRegister(address, accesstoken));
factory.setInvokerConfig(new InvokerConfig(invokerOpen));
factory.setProviderConfig(providerOpen ?
        new ProviderConfig(
                NettyServer.class,
                JsonbSerializer.class,
                port,
                corePoolSize,
                maxPoolSize,
                null) : new ProviderConfig(providerOpen));
```

#### 2.1.4、业务代码开发

- a、接口定义代码：
```
public interface DemoService {

  public UserDTO load(String name);
  
}
```

- b、服务端代码：注解式，一行代码将现有接口转换成 XXL-RPC 服务。
```
@XxlRpcService
@Service
public class DemoServiceImpl implements DemoService {

  @Override
  public UserDTO load(String name) {
    return new UserDTO("jack", "hello world");
  }

}
```

服务提供方 XxlRpcService 注解参数说明：

@XxlRpcService 注解参数 | 说明
--- | ---
version | 服务版本，默认空；可据此区分同一个“服务API” 的不同版本；


- 3、调用端代码： 注解式，一行代码引入 XXL- RPC 服务。
```
@XxlRpcReference(appname = "xxl-appname")
private DemoService demoService;

... 
UserDTO userDTO = demoService.sayHi(name);
```

服务消费方 XxlRpcReference 注解参数说明：

“@XxlRpcReference” 注解参数 | 说明
--- | ---
appname | 服务提供方appname，用于服务发现；必填；
version | 服务版本，隔离相同服务不通版本，默认空；选填；
callType | 请求类型，可选范围：SYNC（默认）、ONEWAY、FUTURE、CALLBACK；选填；
loadBalance | 负载均衡类型，可选范围：ROUND（默认）、RANDOM、LRU、LFU、CONSISTENT_HASH；选填；
timeout | 服务超时时间，单位毫秒；选填；


#### d、测试

参考代码位置：com.xxl.rpc.sample.client.controller.IndexController

上述代码底层将发送一次 XXL-RPC 请求，client 项目调用了 server 项目中的服务，夸JVM进行了一次RPC通讯。

访问该Controller地址即可进行测试：http://127.0.0.1:8081/?name=jack
![输入图片说明](https://www.xuxueli.com/doc/static/xxl-rpc/images/img_02.png "在这里输入图片标题")

### 2.2、frameless 无框架版本
 
得益于优良的兼容性与模块化设计，不限制外部框架；除 spring/springboot 环境之外，理论上支持运行在任何Java代码中，甚至main方法直接启动运行；

以示例项目 “xxl-rpc-sample-frameless” 为例讲解；该示例项目以直连IP方式进行演示，也可以选择接入注册中心方式使用。

#### a、API方式创建“服务提供者”：
```
// 参考代码位置：com.xxl.rpc.sample.server.XxlRpcServerApplication

// 1、XxlRpcBootstrap：XXL-RPC 初始化
XxlRpcBootstrap rpcBootstrap = new XxlRpcBootstrap();
rpcBootstrap.setBaseConfig(new BaseConfig("test", "xxl-rpc-sample-frameless-server"));
rpcBootstrap.setProviderConfig(new ProviderConfig(NettyServer.class, JsonbSerializer.class, null, -1, -1, 7080, null));

rpcBootstrap.start();

// 2、add services：服务信息注册，提供给远程RPC请求使用
rpcBootstrap.getProvider().addService(DemoService.class.getName(), null, new DemoServiceImpl());
```

#### b、API方式创建“服务消费者”：
```
// 参考代码位置：com.xxl.rpc.sample.client.XxlRpcClientAplication

// 1、LocalRegister：本地注册中心 初始化，维护远程服务地址信息
LocalRegister localRegister = new LocalRegister();
localRegister.register(new RegisterInstance("test", "xxl-rpc-sample-frameless-server", "127.0.0.1", 7080, null));

// 2、XxlRpcBootstrap：XXL-RPC 初始化
XxlRpcBootstrap rpcBootstrap = new XxlRpcBootstrap();
rpcBootstrap.setBaseConfig(new BaseConfig("test", "xxl-rpc-sample-frameless-client"));
rpcBootstrap.setRegister(localRegister);
rpcBootstrap.setInvokerConfig(new InvokerConfig(true, NettyClient.class, JsonbSerializer.class, null));

……

// 3、XxlRpcReferenceBean build：创建远程服务代理对象，同步调用方式
DemoService demoService_SYNC = buildReferenceBean(rpcBootstrap, CallType.SYNC);

// 4、发起RPC请求，测试结果输出
UserDTO userDTO = demoService.sayHi("[SYNC]jack");
System.out.println(userDTO);
```


## 三、系统设计

### 3.1 系统架构图
![输入图片说明](https://www.xuxueli.com/doc/static/xxl-rpc/images/img_DNq6.png "在这里输入图片标题")

### 3.2 核心思想
提供稳定高性能的RPC远程服务调用功能，简化分布式服务通讯开发。

### 3.3 架构角色

| 架构角色   | 说明                                                                        |
|--------|---------------------------------------------------------------------------|
| Provider | 服务提供者，提供RPC服务端能力，包括RCP/HTTP Server、业务Service扫描维护、服务执行等能力。                 | 
| Invoker | 服务调用者/消费者，提供RPC客户端能力，包括RCP/HTTP Client、业务请求代理、负载均衡、多方式请求调用能力。             |
| Serializer | 序列化模块，提供通讯数据序列化能力，提供包括JSONB、Hessian、KRYO、PROTOSTUFF…等多种可扩展方案。             |
| Remoting | 网络通讯模块，提供底层网络通讯能力，提供包括Netty/TCP、Netty/HTTP、Jetty、Mina…等多种可扩展方案。           |
| Register | 服务注册模块，提供服务注册、发现能力，提供包括 XxlRpcAdmin(官方内置)、Zookeeper、Nacos、Consul…等多种可扩展方案。 |
| Boot | 服务启动引导模块，提供SpringBoot、无框架等技术栈快速集成能力，如SpringBoot可全程配置化接入、注解式开发等。           |

### 3.4 RPC工作原理
![输入图片说明](https://www.xuxueli.com/doc/static/xxl-rpc/images/img_XEVY.png "在这里输入图片标题")

概念：
- 1、serialization：序列化，通讯数据需要经过序列化，从而支持在网络中传输；
- 2、deserialization：反序列化，服务接受到序列化的请求数据，需要序列化为底层原始数据；
- 3、stub：体现在XXL-RPC为服务的api接口；
- 4、skeleton：体现在XXL-RPC为服务的实现api接口的具体服务；
- 5、proxy：根据远程服务的stub生成的代理服务，对开发人员透明；
- 6、provider：远程服务的提供方；
- 7、consumer：远程服务的消费方；

RPC通讯，可大致划分为四个步骤，可参考上图进行理解：（XXL-RPC提供了多种调用方案，此处以 “SYNC” 方案为例讲解；）
- 1、consumer发起请求：consumer会根据远程服务的stub实例化远程服务的代理服务，在发起请求时，代理服务会封装本次请求相关底层数据，如服务iface、methos、params等等，然后将数据经过serialization之后发送给provider；
- 2、provider接收请求：provider接收到请求数据，首先会deserialization获取原始请求数据，然后根据stub匹配目标服务并调用；
- 3、provider响应请求：provider在调用目标服务后，封装服务返回数据并进行serialization，然后把数据传输给consumer；
- 4、consumer接收响应：consumer接受到相应数据后，首先会deserialization获取原始数据，然后根据stub生成调用返回结果，返回给请求调用处。结束。

### 3.5 TCP通讯模型
![输入图片说明](https://www.xuxueli.com/doc/static/xxl-rpc/images/img_b1IX.png "在这里输入图片标题")

consumer和provider采用NIO方式通讯，其中TCP通讯方案可选NETTY具体选型，高吞吐高并发；但是仅仅依靠单个TCP连接进行数据传输存在瓶颈和风险，因此XXL-RPC在consumer端自身实现了内部连接池，consumer和provider之间为了一个连接池，当尽情底层通讯是会取出一条TCP连接进行通讯（可参考上图）。

### 3.6 sync-over-async 原理
![输入图片说明](https://www.xuxueli.com/doc/static/xxl-rpc/images/img_pMtS.png "在这里输入图片标题")

XXL-RPC采用NIO进行底层通讯，但是NIO是异步通讯模型，调用线程并不会阻塞获取调用结果，因此，XXL-RPC实现了在异步通讯模型上的同步调用，即“sync-over-async”，实现原理如下，可参考上图进行理解：

- 1、每次请求会生成一个唯一的RequestId和一个RpcResponse，托管到请求池中。
- 2、调度线程，执行RpcResponse的get方法阻塞获取本次请求结果；
- 3、然后，底层通过NIO方式发起调用，provider异步响应请求结果，然后根据RequestId寻找到本次调用的RpcResponse，设置响应结果后唤醒调度线程。
- 4、调度线程被唤醒，返回异步响应的请求数据。

### 3.7 多通讯协议
XXL-RPC提供多中通讯方案：支持 TCP 和 HTTP 两种通讯方式进行服务调用；其中 TCP 提供可选方案 NETTY  ，HTTP 提供可选方案 NETTY_HTTP（新版本移除了Mina和Jetty通讯方案，主推Netty；如果有需要可以参考旧版本；）；

如果需要切换XXL-RPC“通讯方案”，只需要执行以下两个步骤即可：
- a、引入通讯依赖包，排除掉其他方案依赖，各方案依赖如下：
  - NETTY：依赖 netty-all ；
  - NETTY_HTTP：依赖 netty-all ；
- b、修改通讯枚举，需要同时在“服务方”与“消费方”两端一同修改，通讯枚举属性代码位置如下：
  - 服务工厂 "XxlRpcSpringProviderFactory.netType" ：可参考springboot示例组件初始化代码；
  - 服务引用注解 "XxlRpcReference.netType" | 服务Bean对象 "XxlRpcReferenceBean.netType" ：可参考springboot示例组件初始化代码；

### 3.7 注册中心  
内置服务注册中心支持服务动态发现，提供轻量级、一站式解决方案。也支持扩展集成其他注册中心，或者不使用注册中心、直接指定服务提供方机器地址调用；

注册中心项目内容可参考章节 “四、轻量级服务注册中心”。

### 3.8 泛化调用
XXL-RPC 提供 "泛化调用" 支持，服务调用方不依赖服务方提供的API；泛化调用通常用于框架集成，比如 "网关平台、跨语言调用、测试平台" 等；
开启 "泛化调用" 时服务方不需要做任何调整，仅需要调用方初始化一个泛化调用服务Reference （"XxlRpcGenericService"） 即可。


“XxlRpcGenericService#invoke” 请求参数 | 说明
--- | ---
String iface | 服务接口类名
String version | 服务版本
String method | 服务方法
String[] parameterTypes | 服务方法形参-类型，如 "int、java.lang.Integer、java.util.List、java.util.Map ..."
Object[] args | 服务方法形参-数据


```
// 服务Reference初始化-注解方式示例
@XxlRpcReference
private XxlRpcGenericService genericService;
	
// 服务Reference初始化-API方式示例
XxlRpcGenericService genericService = (XxlRpcGenericService) new XxlRpcReferenceBean(……).getObject();

// 调用方示例
Object result = genericService.invoke(
            "com.xxl.rpc.sample.server.service.Demo2Service",
            null,
            "sum",
            new String[]{"int", "int"},
            new Object[]{1, 2}
    );


// 服务方示例
public class Demo2ServiceImpl implements Demo2Service {

    @Override
    public int sum(int a, int b) {
        return a + b;
    }

}
```

## 四、版本更新日志

#### v1.0.0（XXL-REGISTRY） Release Notes[2018-12-01]
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

#### v1.0.1（XXL-REGISTRY） Release Notes[2018-12-20]
- 1、访问令牌（accessToken）：为提升系统安全性，注册中心和客户端进行安全性校验，双方AccessToken匹配才允许通讯；
- 2、底层通讯参数统一：请求参数统一由 postbody 发送接收，数据格式见公共消息体 "XxlRegistryParamVO"，内部包含 accessToken、biz、env 等属性；
- 3、环境属性 "env" 长度限制调整为 "2~255" ，兼容 "qa"、"dev" 等短环境标识；
- 4、升级 pom 依赖至较新版本；

#### v1.0.2（XXL-REGISTRY） Release Notes[2018-02-21]
- 1、服务端空值也支持响应，客户端注册信息发现null值缓存，避免缓存穿透；
- 2、客户端配置监控逻辑优化，避免异常情况下重试请求太频繁；
- 3、客户端日志优化：仅变更日志保留为info级别，非核心日志调整为debug级别；
- 4、内部JSON组件优化，支持多级父类属性序列化；
- 5、移除冗余属性，如version等；
- 6、服务注册中心全量同步线程优化，对齐起始时间，避免集群节点数据不一致；

#### v1.1.0（XXL-REGISTRY） Release Notes[2019-11-16]
- 1、注册日志文件加载方式优化，修复文件名乱码问题；
- 2、修复服务注册version不匹配问题；
- 3、升级依赖版本，如slf4j-api/spring-boot/mybatis/mysql等；
- 4、小概率情况下底层通讯乱码问题修复；

### v1.1（XXL-RPC） Release Notes
- 1、快速接入：接入步骤非常简洁，两分钟即可上手；
- 2、服务透明：系统完整的封装了底层通信细节，开发时调用远程服务就像调用本地服务，在提供远程调用能力时不损失本地调用的语义简洁性；
- 3、注册中心（可选）：支持使用zookeeper作为服务注册中心，服务注册并动态发现。同时，也可以不使用注册中心，直接指定服务提供方机器地址进行RPC通讯；
- 4、软负载均衡及容错：服务提供方集群注册时，在使用软负载算法进行流量分发；
- 5、容错：服务提供方集群注册时，某个服务节点不可用时将会自动摘除，同时消费方将会移除失效节点将流量分发到其余节点，提高系统容错能力。
- 6、TCP/HTTP通讯：支持TCP和HTTP两种通讯方式进行服务调用，其中TCP通讯可以执行NETTY或MINA作为可选通讯方案，以提供高效的服务通讯支持；
- 7、序列化：支持hessian、protobuf和jackson等多种序列化方案；
- 8、服务治理：提供服务治理中心，可在线管理注册的服务信息，如管理服务节点、节点权重等；（部分实现）
- 9、服务监控：可在线监控服务调用统计信息以及服务健康状况等（计划中）；
- 10、解决1+1问题：传统分布式通讯一般通过nginx或f5做集群服务的流量负载均衡，如hessian，每次请求在到达目标服务机器之前都需要经过负载均衡机器，即1+1，这将会把流量放大一倍。而XXL-RPC将会从消费方至服务提供方建立TCP长连接，每次请求直达目标机器，从而可以避免上述问题；

### v1.2.0（XXL-RPC） Release Notes[2018-10-26]
- 1、核心模块重度重构：模块化划分、包名重构；
- 2、轻量级/模块化改造：移除对具体组件的依赖，如ZK、Netty、Mina等，改为可选扩展方式；
- 3、支持多种请求方式，如：SYNC、ONEWAY、FUTURE、CALLBACK 等；
- 4、各模块扩展改为非强制依赖：扩展依赖需要单独进行 maven 引入（provided类型）；提供强制依赖最小精简选型组合 "jetty + hessian + 无注册中心"；
- 5、服务AccessToken鉴权；
- 6、支持HTTP异步请求，线程优化，统一通讯流程；
- 7、可选ZK注册中心重构，不依赖配置文件，通过代码初始化；
- 8、可选ZK注册中心初始化逻辑优化，避免并发初始化，阻塞至TCP连接创建成功才允许后续操作；
- 9、推送core到maven中央仓库；
- 10、服务注册逻辑优化，旧方案以 "iface" 接口包名进行服务注册, 改为结合 "iface + version" 作为 serviceKey 进行注册，便于接口多服务复用；

### v1.2.1（XXL-RPC） Release Notes[2018-11-09]
- 1、内置注册中心选择ZK时逻辑优化，ZK初始化时unlock逻辑调整，优化断线重连特性；
- 2、除了springboot类型示例；新增无框架示例项目 "xxl-rpc-sample-frameless"。不依赖第三方框架，只需main方法即可启动运行；
- 3、选型http通讯方式时，校验为IP端口格式地址则主动添加地址前缀；
- 4、RPC异步请求逻辑优化，请求异常时主动通知Client端，避免无效等待时间；
- 5、http通讯方式选型jetty时，线程池升级为QueuedThreadPool，修复jetty9.4版本server自动销毁问题；
- 6、Server新增 "/services" 目录功能，可查看在线服务列表；

### v1.2.2（XXL-RPC） Release Notes[2018-11-26]
- 1、默认通讯方案切换为 Netty，可选方案依赖均调整为 provided 类型；提供强制依赖最小精简选型组合 "netty + hessian + 无注册中心(推荐采用：XXL-RPC原生注册中心)"；
- 2、XXL-RPC原生注册中心：底层抽象注册中心模块，并原生提供自研基于DB的注册中心，真正实现开箱即用，更轻量级、降低第三方依赖；至今XXL-RPC以提供三种注册中心具体实现："XXL-RPC原生注册中心方案"，"ZK方案"，"Local方案"；其中"XXL-RPC原生注册中心方案"特性如下：
    - 轻量级：基于DB与磁盘文件，只需要提供一个DB实例即可，无第三方依赖；
    - 实时性：借助内部广播机制，新服务上线、下线，可以在1s内推送给客户端；
    - 数据同步：注册中心内部10s会全量同步一次磁盘数据，清理无效服务，确保服务数据实时可用；
    - 性能：服务发现时仅读磁盘文件，性能非常高；服务注册、摘除时通过磁盘文件校验，防止重复注册操作；
    - 扩展性：可方便、快速的横向扩展，只需保证 "注册中心" 配置一致即可，可借助负载均衡组件如Nginx快速集群部署；
    - 多状态：服务内置三种状态：正常状态=支持动态注册、发现，服务注册信息实时更新；锁定状态=人工维护注册信息，服务注册信息固定不变；禁用状态=禁止使用，服务注册信息固定为空；
    - 跨语言：注册中心提供HTTP接口供客户端实用，语言无关，通用性更强；
    - 兼容性：“XXL-RPC原生轻量级注册中心”虽然为XXL-RPC设计，但是不限于XXL-RPC使用。兼容支持任何服务框架服务注册实用，如dubbo、springboot等；
    - 容器化：提供官方docker镜像，并实时更新推送dockerhub，进一步实现"XXL-RPC原生注册中心方案"产品开箱即用；
- 3、XXL-RPC客户端适配"XXL-RPC原生注册中心"，可快速接入，只需要切换注册中心实现为 "NativeServiceRegistry" 即可，文档由专门章节介绍；
- 4、注册中心启动参数位置调整，与注册中心实现关联；
- 5、服务提供者参数优化，IP为空时原生动态获取，核心参数启动时增强校验；
- 6、注册模块API优化，改为批量模式进一步提升性能；
- 7、文档增强，注册中心配置切换、通讯方案配置切换说明；
- 8、IP工具类优化，兼容 Inet6Address 格式地址；
- 9、Netty销毁逻辑优化；
- 10、扩展第三方注册中心ZK底层逻辑优化，避免旧注册信息无法清理的问题；

### v1.3.0 Release Notes[2018-12-02]
- 1、原生注册中心拆分为独立项目 "xxl-registry"（https://github.com/xuxueli/xxl-registry ），提供服务注册restful服务，并提送响应client端依赖用于简化接入难度；
- 2、NativeServiceRegistry 更名为 XxlRegistryServiceRegistry；
- 3、POM依赖升级，冗余POM清理；
- 4、代码优化：XxlRpcInvokerFactory 移除 static 代码块及相关组件，进一步实现组件无状态；
- 5、服务注册逻辑优化，避免地址重复生成；

### v1.3.1 Release Notes[2018-12-21]
- 1、负载均衡/软负载：提供丰富的负载均衡策略，包括：轮询、随机、LRU、LFU、一致性HASH等；
- 2、服务发现注册逻辑优化：支持批量注册、摘除，升级 xxl-registry 至 v1.0.1；
- 3、新增jfinal类型示例项目 "xxl-rpc-sample-jfinal" 支持jfinal项目快速接入分布式RPC服务功能；高兼容性，原则上支持任务框架，甚至main方法直接运行；
- 4、TCP通讯方案Server端Channel线程优化（线程参数=60/300/1000），避免IO线程阻塞于业务；
- 5、TCP通讯方案Client端Channel线程优化（线程参数=10/100/1000），避免IO线程阻塞于callback业务；
- 6、TCP通讯方案Client初始化逻辑优化；
- 7、TCP长连销毁逻辑优化；
- 8、底层Log整理，RPC报错时打印完整Log，包括请求地址，请求参数等；
- 9、Server端销毁逻辑优化；
- 10、static代码块优化，进行组件无状态优化：response factory等；迁移到invoke factory上来；
- 11、升级多项pom依赖至较新稳定版本；


### v1.3.2 Release Notes[2019-02-21]
- 1、泛化调用：服务调用方不依赖服务方提供的API；
- 2、新增通讯方案 "NETTY_HTTP"；
- 3、新增序列化方案 "KRYO"；
- 4、通讯效率优化：TCP连接池取消，改为单一长连接，移除commons-pool2依赖；
- 5、RPC请求路由时空地址处理优化；
- 6、通讯连接池address参数优化，出IP:PORT格式外兼容支持常规URL格式地址；
- 7、线程名称优化，便于适配监控快速进行线程定位；

### v1.4.0 Release Notes[2019-04-20]
- 1、LRU路由更新不及时问题修复；
- 2、JettyClient Buffer 默认长度调整为5M；
- 3、Netty Http客户端配置优化；
- 4、升级依赖版本，如netty/mina/spring等

### v1.4.1 Release Notes[2019-05-23]
- 1、客户端长连优化，修复初始化时服务不可用导致长连冗余创建的问题；
- 2、升级依赖版本，如netty/mina/jetty/jackson/spring/spring-boot等;
- 3、空闲链接自动回收：服务端与客户端主动检测空闲链接并回收，及时释放相关资源(netty、mina)；空闲超10min自动释放；

### v1.4.2 Release Notes[2019-11-18]
- 1、长连心跳保活：客户端周期性发送心跳请求给服务端保活；服务端连续三次未收到心跳时，销毁连接；
- 2、服务线程优化，支持自定义线程参数；
- 3、API重构：初始化枚举改为接口实例，方便扩展；
- 4、代码优化，ConcurrentHashMap变量类型改为ConcurrentMap，避免因不同版本实现不同导致的兼容性问题；
- 5、Netty Http客户端优化，识别并过滤非法响应数据；
- 6、通讯方案收敛：主推Netty和Netty_Http，移除Mina和Jetty内置扩展，如有需求自行扩展维护；
- 7、序列化方案收敛：主推HESSIAN和HESSIAN1，移除protostuff、KRYO、JACKSON内置扩展，如有需求自行扩展维护；
- 8、升级依赖版本，如netty/mina/hessian/jackson/zookeeper等;

### v1.5.0 Release Notes[2019-11-22]
- 1、IpUtil优化：增加连通性校，过滤明确非法的网卡；

### v1.6.0 Release Notes[2020-04-15]
- 1、为方便维护，合并xxl-registry至xxl-rpc，模块名为xxl-rpc-admin;
- 2、一致性哈希路由策略优化：默认虚拟节点数量调整为100，提高路由的均衡性；
- 3、RPC Client端，复用单例EventLoopGroup线程池，降低资源开销；
- 4、RPC Server端，新增属性 ”注册地址/registryAddress“，优先使用该属性作为注册地址，为空时使用服务 ”IP:PORT“ 作为注册地址。从而更灵活的支持容器类型执行器动态IP和动态映射端口问题。

### v1.7.0 Release Notes[2022-10-02]
- 1、开源协议：由 GPLv3 调整为 Apache2.0 开源协议；
- 2、路由策略：轮训路由策略代码优化，修复小概率下并发问题；
- 3、代码重构：默认注册中心代码结构重构，废弃冗余 "biz" 属性；
- 4、版本升级：依赖版本升级，如netty、spring、gson等;
- 5、数据库编码：调整为utf8mb4；
- 6、restful api：序列化组件调整为Gson；
- 7、服务磁盘注册数据：序列化组件调整为Gson；

### v1.7.1 Release Notes[2024-11-24]
- 1、【升级】多个项目依赖升级至较新稳定版本，涉及netty、gson、springboot、mybatis等；
- 2、【优化】Hessian启用NonSerializable拦截，安全性提升。
- 3、【优化】服务注册环境标识字段长度调整，上限调整为50；
- 4、【优化】代码重构；
- 5、【合并】合并 [PR-55](https://github.com/xuxueli/xxl-rpc/pull/55)，解决OpenTelemetry集成问题；
- 6、【合并】合并 [PR-53](https://github.com/xuxueli/xxl-rpc/pull/53)，调整NettyConnectClient类锁问题；
- 7、【合并】合并 [PR-51](https://github.com/xuxueli/xxl-rpc/pull/51)，解决解决RST异常问题；

### v1.8.0 Release Notes[2024-11-24]
- 1、【重构】针对 “Core/核心模块” 进行架构模块化重构，拆分 Provider、Invoker、Serializer、Remoting、Registry 和 Boot 等六个核心模块：
  - Provider：定位为服务提供者，提供RPC服务端能力，包括RCP/HTTP Server、业务Service扫描维护、服务执行等能力。
  - Invoker：定位为服务调用者，提供RPC客户端能力，包括RCP/HTTP Client、业务请求代理、负载均衡、多方式请求调用能力。
  - Serializer：定位为序列化组件，提供通讯数据序列化能力，提供包括JSONB、Hessian、KRYO、PROTOSTUFF…等多种可扩展方案。
  - Remoting：定位为通讯组件，提供底层网络通讯能力，提供包括Netty/TCP、Netty/HTTP、Jetty、Mina…等多种可扩展方案。
  - Register：定位为注册中心，提供服务注册、发现能力，提供包括 XxlRpcRegister(官方内置/xxl-rpc-admin)、Zookeeper、Nacos、Consul、ETCD…等多种可扩展方案。
  - Boot：定位为启动引导模块，提供SpringBoot、无框架等技术栈快速集成能力，如SpringBoot可全程配置化接入、注解式开发等。
- 2、【重构】针对 “Admin/服务管理模块” 进行模型以及UI重构，提供 环境/命名空间、应用、鉴权、服务注册节点 等在线运营管控能力。
- 3、【优化】服务注册从接口维度调整为应用维度，降低服务注册压力，提升稳定性及系统负载。
- 4、【优化】XxlRpcReferenceBean 移除冗余属性，注册信息收敛至Register，降低认知成本、提升可维护性。
- 5、【安全】默认序列化方案调整为 JSONB，并进行兜底安全过滤，提升序列化性能、以及安全性；

### v1.8.1 Release Notes[2025-01-01]
- 1、【安全】序列化安全性增强，默认开启package安全空间机制；
- 2、【扩展】序列化扩展性增强，支持自定义序列化package白名单；
- 3、【优化】序列化类型主动检测，提升问题定位效率；
- 4、【能力】服务注册发现实效性提升，优化long-polling逻辑；
- 5、【扩展】模块 xxl-rpc-netty-shade 独立拆分，与Core模块解耦；
- 6、【优化】通讯组件选择HttpServer时，HttpObjectAggregator限制调大至20M，支持大消息传输；
- 7、【升级】多个项目依赖升级至较新稳定版本，涉及 xxl-rpc-netty-shade、netty、slf4j 等；


### v1.9.0 Release Notes[2025-01-24]
- 1、【优化】服务底层代码重构优化，精简依赖、减少依赖包体；
- 2、【调整】内置注册中心XxlRpcRegister(xxl-rpc-admin)迁移，整合至XXL-CONF：
  - XXL-CONF：一站式服务管理平台（配置中心、注册中心），提供 动态配置管理、服务注册及发现能力；降低中间件认知及运维成本。
  - Github：https://github.com/xuxueli/xxl-conf ）；
  - 官方文档：https://www.xuxueli.com/xxl-conf/
- 3、【调整】服务注册中心逻辑调整，借助 XXL-CONF 的OpenApi 实现 动态服务注册与发现；
- 4、【优化】优化获取本地IP地址逻辑，调整了获取本地地址顺序；
- 5、【升级】多个项目依赖升级至较新稳定版本；

### v.1.9.1 Release Notes[迭代中]
- 1、【TODO】新增SimpleHttpServer，仅支持同步请求，简化CallType复杂度；


### TODO LIST
- 提高系统可用性，以部分功能暂时不可达为代价，防止服务整体缓慢或雪崩
    - 限流=防止负载过高，导致服务雪崩；client、server，双向限流；方法级，QPS限流；在途请求数，流控依据；
    - 降级=10s内超阈值（异常、超时）；拒绝服务、默认值；
        - 超过（熔断模式）：99.9% 返回默认值，0.1%真实请求；
        - 未超过：熔断模式下，每 10s 增加 10% 的流量，直至恢复；
    - 服务隔离：超时较多的请求，自动路由到 “慢线程池” ，避免占用公共线程池；
    - 预热控制，刚启动的节点，只会分配比较少的请求；逐步增大，直至平均。帮助新节点启动；
- 支持HTTP异步响应，至此底层remoting层通讯全异步化；
- zk注册中心初始化时取消对集群状态强依赖，底层异常时循环检测；
- Server启动失败时，ZK销毁中断问题修复，偶发；
- 服务提供者iface获取方式优化，兼容代理方式获取接口 “getProxiedInterfaces”；
- 演进计划：
    - 通讯：remoting模块；TCP、HTTP、HTTP2可选方案；
    - 限流：ratelimit模块；滑动窗口方式，单机限流，请求/响应方双向限流；[ING]
    - 网关：servlet3 + 泛化调用模块；计划：基于DB轻量级注册中心，服务动态发现，自动转发；
- admin-服务监控（《xxl-trace》）:
    - tps，99线；
    - 成功率；
    - 调用链：
- rpc filter：方便埋点、监控等；
- 服务治理实现，服务调用量，成功率，1min上报一次； 
- static代码块移除，进行组件无状态优化，jetty/pool/等；
- 接入方配置方式优化，provider与invoker配置合并至新组建；
- 新增 appname 属性，为后续服务 trace 做准备；
- 新增 nutz 类型示例项目;
- Server/Client失败尽量响应，避免等到到timeout；
- 线程隔离：通讯线程池拆分为Fast/Slow两个，针对响应较慢服务方法请求，降级使用Slow线程池；考虑是否可以方法级隔离线程池，避免线程阻塞；
- rpc时钟参数仅记录，取消时钟校验逻辑；
- 调用链追踪，监控；结合 xxl-apm 与 xxl-rpc filter共同演进；
- 限流-熔断-降级，结合xxl-registry与xxl-rpc filter共同演进；
- 长连心跳、断线重连、空闲连接回收；
- 服务注册中心：
    - 服务端：注册IP黑名单、白名单；
    - 客户端：瞬间下线拒绝，比如超过 90% 服务批量下线，本次下线标记失败，连续三次才生效；防止注册中心故障，导致整体服务不可用；
    - 注册方式附属信息；
        - 服务注册，支持节点权重配置；
        - 注册服务支持Tag属性；如机房TAG，客户端优先使用本机房，即机房TAG一致的服务；
    - springboot、dubbo 示例；
    - 单机版本：H2数据库;
    - 异地多活：注册中心无中心服务；
    - 同机房读：服务支持Region属性，优先使用本Region服务；
- 客户端并发锁超时优化；
- 路由对象支持可配置，当前根据iface，太固定；

## 五、其他

### 5.1 项目贡献
欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-rpc/issues/) 讨论新特性或者变更。

### 5.2 用户接入登记
更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-rpc/issues/2 ) 登记，登记仅仅为了产品推广。

### 5.3 开源协议和版权
产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。

- Licensed under the Apache License, Version 2.0.
- Copyright (c) 2015-present, xuxueli.

---
### 捐赠
无论金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](https://www.xuxueli.com/page/donate.html )
