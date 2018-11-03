## 《分布式服务框架XXL-RPC》

[![Build Status](https://travis-ci.org/xuxueli/xxl-rpc.svg?branch=master)](https://travis-ci.org/xuxueli/xxl-rpc)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-rpc/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-rpc/)
[![GitHub release](https://img.shields.io/github/release/xuxueli/xxl-rpc.svg)](https://github.com/xuxueli/xxl-rpc/releases)
[![License](https://img.shields.io/badge/license-GPLv3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0.html)
[![donate](https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat-square)](http://www.xuxueli.com/page/donate.html)


## 一、简介

### 1.1 概述
XXL-RPC 是一个分布式服务框架，提供稳定高性能的RPC远程服务调用功能。现已开放源代码，开箱即用。

### 1.2 特性
- 1、快速接入：接入步骤非常简洁，两分钟即可上手；
- 2、服务透明：系统完整的封装了底层通信细节，开发时调用远程服务就像调用本地服务，在提供远程调用能力时不损失本地调用的语义简洁性；
- 3、多调用方案：支持 SYNC、ONEWAY、FUTURE、CALLBACK 等方案；
- 4、多通讯方案：支持 TCP 和 HTTP 两种通讯方式进行服务调用；其中 TCP 提供可选方案 NETTY 或 MINA ，HTTP 提供可选方案 Jetty；
- 5、多序列化方案：支持 HESSIAN、HESSIAN1、PROTOSTUFF、JSON 等方案；
- 6、注册中心：可选组件，支持服务注册并动态发现；未启用注册中心时，支持直接指定服务提供方机器地址通讯；原生提供 local 与 zookeeper 两种服务注册中心可选方案；
- 7、软负载均衡及容错：服务提供方集群注册时，在使用软负载算法进行流量分发；
- 8、容错：服务提供方集群注册时，某个服务节点不可用时将会自动摘除，同时消费方将会移除失效节点将流量分发到其余节点，提高系统容错能力。
- 9、解决1+1问题：传统分布式通讯一般通过nginx或f5做集群服务的流量负载均衡，每次请求在到达目标服务机器之前都需要经过负载均衡机器，即1+1，这将会把流量放大一倍。而XXL-RPC将会从消费方直达服务提供方，每次请求直达目标机器，从而可以避免上述问题；
- 10、服务治理：提供服务治理中心，可在线管理注册的服务信息，如管理服务节点、节点权重等；（计划中）
- 11、服务监控：可在线监控服务调用统计信息以及服务健康状况等（计划中）；
- 12、高兼容性：得益于优良的兼容性与模块化设计，不限制外部框架；除 spring/springboot 环境之外，理论上支持运行在任何Java代码中，甚至main方法直接启动运行；


### 1.3 背景

**WHAT** ：RPC（Remote Procedure Call Protocol，远程过程调用），调用远程服务就像调用本地服务，在提供远程调用能力时不损失本地调用的语义简洁性；

**WHY** ：一般公司，尤其是大型互联网公司内部系统由上千上万个服务组成，不同的服务部署在不同机器，跑在不同的JVM上，此时需要解决两个问题：
- 1、如果我需要依赖别人的服务，但是别人的服务在远程机器上，我该如何调用？
- 2、如果其他团队需要使用我的服务，我该怎样发布自己的服务供他人调用？

**HOW** ：

答案：“XXL-RPC”：
- 1、如何调用：只需要知晓远程服务的stub和地址，即可方便的调用远程服务，同时调用透明化，就像调用本地服务一样简单；
- 2、如何发布：只需要提供自己服务的stub和地址，别人即可方便的调用我的服务，在开启注册中心的情况下服务动态发现，只需要提供服务的stub即可；

### 1.4 下载

#### 文档地址

- [中文文档](http://www.xuxueli.com/xxl-rpc/)

#### 源码仓库地址

源码仓库地址 | Release Download
--- | ---
[https://github.com/xuxueli/xxl-rpc](https://github.com/xuxueli/xxl-rpc) | [Download](https://github.com/xuxueli/xxl-rpc/releases)
[https://gitee.com/xuxueli0323/xxl-rpc](https://gitee.com/xuxueli0323/xxl-rpc) | [Download](https://gitee.com/xuxueli0323/xxl-rpc/releases)  


#### 技术交流
- [社区交流](http://www.xuxueli.com/page/community.html)


### 1.5 环境
- Maven3+
- Jdk1.7+
- Tomcat7+


## 二、快速入门（springboot版本）

### 2.1 准备工作

- 1、编译项目


    源码目录介绍：
    - /doc
    - /xxl-rpc-admin    ：服务治理、监控中心，非必选（暂未整理发布...）;
    - /xxl-rpc-core     ：核心依赖；
    - /xxl-rpc-samples  ：示例项目；
        - /xxl-rpc-executor-sample-frameless     ：无框架版本示例；
        - /xxl-rpc-executor-sample-springboot    ：springboot版本示例；
            - /xxl-rpc-executor-sample-springboot-api           ：公共API接口
            - /xxl-rpc-executor-sample-springboot-client        ：服务消费方 invoker 调用示例；
            - /xxl-rpc-executor-sample-springboot-server        ：服务提供方 provider 示例;
    
- 2、zookeeper集群（可选，选择ZK注册中心时才需要；）

### 2.2 配置部署“服务治理、监控中心”
忽略（非必选，暂未整理发布）
 
### 2.3 项目中使用XXL-RPC
以示例项目 “xxl-rpc-executor-sample-springboot” 为例讲解；

#### 2.3.1 开发“服务API”
开发RPC服务的 “接口 / interface” 和 “数据模型 / DTO”；

    
    可参考如下代码：
    com.xxl.rpc.sample.api.DemoService
    com.xxl.rpc.sample.api.dto.UserDTO

#### 2.3.2 配置开发“服务提供方”
- 1、配置 “maven依赖”：  

需引入：XXL-RPC核心依赖 + 公共API接口依赖（如选择ZK注册中心需引入ZK依赖，否则忽略；）

```
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-rpc-core</artifactId>
    <version>${parent.version}</version>
</dependency>
```

- 2、配置“服务提供方 ProviderFactory”
    
```
// 参考代码位置：com.xxl.rpc.sample.server.conf.XxlRpcProviderConfig

@Bean
public XxlRpcSpringProviderFactory xxlRpcSpringProviderFactory() {

    XxlRpcSpringProviderFactory providerFactory = new XxlRpcSpringProviderFactory();
    providerFactory.setPort(port);
    
    logger.info(">>>>>>>>>>> xxl-rpc provider config init success.");
    return providerFactory;
}
```

ProviderFactory 参数 | 说明
--- | ---
netType | 服务通讯方案，可选范围：JETTY（默认）、NETTY、MINA； 
serialize | 序列化方案，可选范围: HESSIAN（默认）、HESSIAN1、PROTOSTUFF、JSON；
ip |  服务方IP，为空自动获取机器IP，支持手动指定；
port | 服务方端口，默认 7080 
accessToken | 服务鉴权Token，非空时生效；
serviceRegistryClass | 服务注册中心，可选范围：LocalServiceRegistry.class、ZkServiceRegistry.class；支持灵活自由扩展；
serviceRegistryParam | 服务注册中心启动参数，参数说明可参考各注册中心实现的 start() 的方法注释；


- 3、开发“服务实现类”

实现 “服务API” 的接口，开发业务逻辑代码；

    可参考如下代码：
    com.xxl.rpc.sample.api.DemoService
    
    注意：
    1、添加 “@Service” 注解：被Spring容器扫描识别为SpringBean；
    2、添加 “@XxlRpcService” 注解：被 “XXL-RPC” 的 ProviderFactory 扫描识别，进行Provider服务注册，如果开启注册中心同时也会进行注册中心服务注册； 

“@XxlRpcService” 注解参数 | 说明
--- | ---
version | 服务版本，默认空；可据此区分同一个“服务API” 的不同版本；


#### 2.3.3 配置开发“服务消费方”

- 1、配置 “maven依赖”：  

需引入：XXL-RPC核心依赖 + 公共API接口依赖（如选择ZK注册中心需引入ZK依赖，否则忽略；）

```
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-rpc-core</artifactId>
    <version>${parent.version}</version>
</dependency>
```

- 2、配置“服务消费方 InvokerFactory”
    
```
// 参考代码位置：com.xxl.rpc.sample.client.conf.XxlRpcInvokerConfig

@Bean
public XxlRpcSpringInvokerFactory xxlJobExecutor() {

    XxlRpcSpringInvokerFactory invokerFactory = new XxlRpcSpringInvokerFactory();

    logger.info(">>>>>>>>>>> xxl-rpc invoker config init success.");
    return invokerFactory;
}
```

InvokerFactory 参数 | 说明
--- | ---
serviceRegistryClass | 服务注册中心，可选范围：LocalServiceRegistry.class、ZkServiceRegistry.class；支持灵活自由扩展；
serviceRegistryParam | 服务注册中心启动参数，参数说明可参考各注册中心实现的 start() 的方法注释；

- 3、注入并实用远程服务

```
// 参考代码位置：com.xxl.rpc.sample.client.controller.IndexController

@XxlRpcReference
private DemoService demoService;

……
UserDTO user = demoService.sayHi(name);
……

```

“@XxlRpcReference” 注解参数 | 说明
--- | ---
netType | 服务通讯方案，可选范围：JETTY（默认）、NETTY、MINA； 
serializer | 序列化方案，可选范围: HESSIAN（默认）、HESSIAN1、PROTOSTUFF、JSON；
address | 服务远程地址，ip:port 格式；选填；非空时将会优先实用该服务地址，为空时会从注册中心服务地址发现；
accessToken | 服务鉴权Token，非空时生效；
version | 服务版本，默认空；可据此区分同一个“服务API” 的不同版本；
timeout | 服务超时时间，单位毫秒；
callType | 请求类型，可选范围：SYNC（默认）、ONEWAY、FUTURE、CALLBACK； 


#### 2.3.4 测试

```
// 参考代码位置：com.xxl.rpc.sample.client.controller.IndexController
```
代码中将上面配置的消费方 invoker 远程服务注入到测试 Controller 中使用，调用该服务，查看看是否正常。
如果正常，说明该接口项目通过XXL-RPC从 client 项目调用了 server 项目中的服务，夸JVM进行了一次RPC通讯。

访问该Controller地址即可进行测试：http://127.0.0.1:8080/?name=jack



## 三、快速入门（frameless 无框架版本）
 
得益于优良的兼容性与模块化设计，不限制外部框架；除 spring/springboot 环境之外，理论上支持运行在任何Java代码中，甚至main方法直接启动运行；

以示例项目 “xxl-rpc-executor-sample-frameless” 为例讲解；

### 3.1 API方式创建“服务提供者”：
```
// 参考代码位置：com.xxl.rpc.sample.server.XxlRpcServerApplication

// init
XxlRpcProviderFactory providerFactory = new XxlRpcProviderFactory();
providerFactory.initConfig(NetEnum.JETTY, Serializer.SerializeEnum.HESSIAN.getSerializer(), null, 7080, null, null, null);

// add services
providerFactory.addService(DemoService.class.getName(), null, new DemoServiceImpl());

// start
providerFactory.start();

TimeUnit.HOURS.sleep(1);

// stop
providerFactory.stop();
```
### 3.2 API方式创建“服务消费者”：

```
// 参考代码位置：com.xxl.rpc.sample.client.XxlRpcClientAplication

DemoService demoService = (DemoService) new XxlRpcReferenceBean(NetEnum.JETTY, Serializer.SerializeEnum.HESSIAN.getSerializer(), CallType.SYNC,
				DemoService.class, null, 500, "127.0.0.1:7080", null, null).getObject();

UserDTO userDTO = demoService.sayHi("jack");

System.out.println(userDTO);
```


## 四、系统设计

### 4.1 系统架构图
![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-rpc/master/doc/images/img_DNq6.png "在这里输入图片标题")

### 4.2 核心思想
提供稳定高性能的RPC远程服务调用功能，简化分布式服务通讯开发。

### 4.3 角色构成
- 1、provider：服务提供方；
- 2、invoker：服务消费方；
- 3、registry：服务注册中心；
- 4、serializer: 序列化模块；
- 5、remoting：服务通讯模块；
- 6、admin：服务治理、监控中心：管理服务节点信息，统计服务调用次数、QPS和健康情况；（暂未整理发布...）

### 4.4 RPC工作原理剖析
![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-rpc/master/doc/images/img_XEVY.png "在这里输入图片标题")

概念：
- 1、serialization：序列化，通讯数据需要经过序列化，从而支持在网络中传输；
- 2、deserialization：反序列化，服务接受到序列化的请求数据，需要序列化为底层原始数据；
- 3、stub：体现在XXL-RPC为服务的api接口；
- 4、skeleton：体现在XXL-RPC为服务的实现api接口的具体服务；
- 5、proxy：根据远程服务的stub生成的代理服务，对开发人员透明；
- 6、provider：远程服务的提供方；
- 7、consumer：远程服务的消费方；

RPC通讯，可大致划分为四个步骤，可参考上图进行理解：
- 1、consumer发起请求：consumer会根据远程服务的stub实例化远程服务的代理服务，在发起请求时，代理服务会封装本次请求相关底层数据，如服务iface、methos、params等等，然后将数据经过serialization之后发送给provider；
- 2、provider接收请求：provider接收到请求数据，首先会deserialization获取原始请求数据，然后根据stub匹配目标服务并调用；
- 3、provider响应请求：provider在调用目标服务后，封装服务返回数据并进行serialization，然后把数据传输给consumer；
- 4、consumer接收响应：consumer接受到相应数据后，首先会deserialization获取原始数据，然后根据stub生成调用返回结果，返回给请求调用处。结束。

### 4.5 TCP通讯模型
![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-rpc/master/doc/images/img_b1IX.png "在这里输入图片标题")

consumer和provider采用NIO方式通讯，可选NETTY或MINA方案，高吞吐高并发；但是仅仅依靠单个TCP连接进行数据传输存在瓶颈和风险，因此XXL-RPC在consumer端自身实现了内部连接池，consumer和provider之间为了一个连接池，当尽情底层通讯是会取出一条TCP连接进行通讯（可参考上图）。

### 4.6 sync-over-async
![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-rpc/master/doc/images/img_pMtS.png "在这里输入图片标题")

XXL-RPC采用NIO进行底层通讯，但是NIO是异步通讯模型，调用线程并不会阻塞获取调用结果，因此，XXL-RPC实现了在异步通讯模型上的同步调用，即“sync-over-async”，实现原理如下，可参考上图进行理解：

- 1、每次请求会生成一个唯一的RequestId和一个RpcResponse，托管到请求池中。
- 2、调度线程，执行RpcResponse的get方法阻塞获取本次请求结果；
- 3、然后，底层通过NIO方式发起调用，provider异步响应请求结果，然后根据RequestId寻找到本次调用的RpcResponse，设置响应结果后唤醒调度线程。
- 4、调度线程被唤醒，返回异步响应的请求数据。

### 4.7 注册中心
![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-rpc/master/doc/images/img_m3Ma.png "在这里输入图片标题")

原理：        
XXL-RPC中每个服务在zookeeper中对应一个节点，如图"iface name"节点，该服务的每一个provider机器对应"iface name"节点下的一个子节点，如图中"192.168.0.1:9999"、"192.168.0.2:9999"和"192.168.0.3:9999"，子节点类型为zookeeper的EPHMERAL类型，该类型节点有个特点，当机器和zookeeper集群断掉连接后节点将会被移除。consumer底层可以从zookeeper获取到可提供服务的provider集群地址列表，从而可以向其中一个机器发起RPC调用。

XXL-RPC支持两种方式设置远程服务地址：        
- 1、手动设置服务地址：需要为每个远程服务手动配置服务地址；
- 2、zookeeper注册中心：采用Zookeeper作为注册中心，服务自动注册和动态发现；


## 五、版本更新日志
### 5.1 版本 v1.1 新特性
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

### 5.2 版本 v1.2.0 [2018-10-26]
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

### 5.3 版本 v1.2.1 [迭代中]
- 1、ZK初始化时unlock逻辑调整，优化断线重连特性；
- 2、除了springboot类型示例；新增无框架示例项目 "xxl-rpc-executor-sample-frameless"。不依赖第三方框架，只需main方法即可启动运行；

### TODO
- 提高系统可用性，以部分功能暂时不可达为代价，防止服务整体缓慢或雪崩
    - 限流=防止负载过高，导致服务雪崩；client、server，双向限流；方法级，QPS限流；在途请求数，流控依据；
    - 降级=10s内超阈值（异常、超时）；拒绝服务、默认值；
        - 超过（熔断模式）：99.9% 返回默认值，0.1%真实请求；
        - 未超过：熔断模式下，每 10s 增加 10% 的流量，直至恢复；
    - 服务隔离：超时较多的请求，自动路由到 “慢线程池” ，避免占用公共线程池；
    - 预热控制，刚启动的节点，只会分配比较少的请求；逐步增大，直至平均。帮助新节点启动；
- 服务注册中心, 节点支持单个移除
- 支持HTTP异步响应，至此底层remoting层通讯全异步化；
- 底层Log整理，RPC报错时打印完整Log，包括请求地址，请求参数等；
- zk注册中心初始化时取消对集群状态强依赖，底层异常时循环检测；

## 六、其他

### 6.1 项目贡献
欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-rpc/issues/) 讨论新特性或者变更。

### 6.2 用户接入登记
更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-rpc/issues/2 ) 登记，登记仅仅为了产品推广。

### 6.3 开源协议和版权
产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2015-present, xuxueli.

---
### 捐赠
无论金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](http://www.xuxueli.com/page/donate.html )
