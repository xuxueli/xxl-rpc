## 《分布式服务框架XXL-RPC》

[![Actions Status](https://github.com/xuxueli/xxl-rpc/workflows/Java%20CI/badge.svg)](https://github.com/xuxueli/xxl-rpc/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-rpc/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-rpc/)
[![GitHub release](https://img.shields.io/github/release/xuxueli/xxl-rpc.svg)](https://github.com/xuxueli/xxl-rpc/releases)
[![License](https://img.shields.io/badge/license-GPLv3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0.html)
[![donate](https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat-square)](https://www.xuxueli.com/page/donate.html)


## 一、简介

### 1.1 概述
XXL-RPC 是一个分布式服务框架，提供稳定高性能的RPC远程服务调用功能。拥有"高性能、分布式、注册中心、负载均衡、服务治理"等特性。现已开放源代码，开箱即用。

### 1.2 特性

- 1、快速接入：接入步骤非常简洁，两分钟即可上手；
- 2、服务透明：系统完整的封装了底层通信细节，开发时调用远程服务就像调用本地服务，在提供远程调用能力时不损失本地调用的语义简洁性；
- 3、多调用方案：支持 SYNC、ONEWAY、FUTURE、CALLBACK 等方案；
- 4、多通讯方案：支持 TCP 和 HTTP 两种通讯方式进行服务调用；
- 5、多序列化方案：支持 HESSIAN、HESSIAN1 等方案；
- 6、负载均衡/软负载：提供丰富的负载均衡策略，包括：轮询、随机、LRU、LFU、一致性HASH等；
- 7、注册中心：可选组件，支持服务注册并动态发现（内置“XXL-REGISTRY 轻量级注册中心”（推荐）、“Local注册中心”等）；可选择不启用，直接指定服务提供方机器地址通讯；
- 8、服务治理：提供服务治理中心，可在线管理注册的服务信息，如服务锁定、禁用等；
- 9、服务监控：可在线监控服务调用统计信息以及服务健康状况等（计划中）；
- 10、容错：服务提供方集群注册时，某个服务节点不可用时将会自动摘除，同时消费方将会移除失效节点将流量分发到其余节点，提高系统容错能力。
- 11、解决1+1问题：传统分布式通讯一般通过nginx或f5做集群服务的流量负载均衡，每次请求在到达目标服务机器之前都需要经过负载均衡机器，即1+1，这将会把流量放大一倍。而XXL-RPC将会从消费方直达服务提供方，每次请求直达目标机器，从而可以避免上述问题；
- 12、高兼容性：得益于优良的兼容性与模块化设计，不限制外部框架；除 spring/springboot 环境之外，理论上支持运行在任何Java代码中，甚至main方法直接启动运行；
- 13、泛化调用：服务调用方不依赖服务方提供的API；


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
- Maven3+
- Jdk1.7+
- Tomcat7+


## 二、快速入门（springboot版本）

### 2.1 准备工作

- 1、编译项目

    源码目录介绍：
    - /doc
    - /xxl-rpc-core     ：核心依赖；
    - /xxl-rpc-samples  ：示例项目；
        - /xxl-rpc-sample-frameless     ：无框架版本示例；
        - /xxl-rpc-sample-springboot    ：springboot版本示例；
            - /xxl-rpc-sample-springboot-api           ：公共API接口
            - /xxl-rpc-sample-springboot-client        ：服务消费方 invoker 调用示例；
            - /xxl-rpc-sample-springboot-server        ：服务提供方 provider 示例;
        - / xxl-rpc-sample-jfinal       ：jfinal版本示例；

### 2.2 配置部署 “分布式服务注册中心XXL-REGISTRY”

推荐使用 "XXL-REGISTRY" 作为注册中心。可前往 XXL-REGISTRY (https://github.com/xuxueli/xxl-registry ) 查看部署文档。非常轻量级，一分钟可完成部署工作。

 
### 2.3 项目中使用XXL-RPC
以示例项目 “xxl-rpc-sample-springboot” 为例讲解；

#### 2.3.1 开发“服务API”
开发RPC服务的 “接口 / interface” 和 “数据模型 / DTO”；

    
    可参考如下代码：
    com.xxl.rpc.sample.api.DemoService
    com.xxl.rpc.sample.api.dto.UserDTO

#### 2.3.2 配置开发“服务提供方”
- 1、配置 “maven依赖”：  

需引入：XXL-RPC核心依赖 + 公共API接口依赖

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
    providerFactory.setServer(NettyServer.class);
    providerFactory.setSerializer(HessianSerializer.class);
    providerFactory.setCorePoolSize(-1);
    providerFactory.setMaxPoolSize(-1);
    providerFactory.setIp(null);
    providerFactory.setPort(port);
    providerFactory.setAccessToken(null);
    providerFactory.setServiceRegistry(XxlRegistryServiceRegistry.class);
    providerFactory.setServiceRegistryParam(new HashMap<String, String>() {{
        put(XxlRegistryServiceRegistry.XXL_REGISTRY_ADDRESS, address);
        put(XxlRegistryServiceRegistry.ENV, env);
    }});

    logger.info(">>>>>>>>>>> xxl-rpc provider config init finish.");
    return providerFactory;
}
```

ProviderFactory 参数 | 说明
--- | ---
setServer | 服务通讯方案，可选范围：NettyServer（默认）、NettyHttpServer ;
setSerializer | 序列化方案，可选范围: HessianSerializer（默认）、Hessian1Serializer ;
setCorePoolSize | 业务线程池core大小
setMaxPoolSize | 业务线程是max大小
ip |  服务方IP，为空自动获取机器IP，支持手动指定
port | 服务方端口，默认 7080 
accessToken | 服务鉴权Token，非空时生效；
setServiceRegistry | 服务注册中心，可选范围：XxlRegistryServiceRegistry.class、LocalServiceRegistry.class；支持灵活自由扩展；
setServiceRegistryParam | 服务注册中心启动参数，参数说明可参考各注册中心实现的 start() 的方法注释；


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

需引入：XXL-RPC核心依赖 + 公共API接口依赖

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
    invokerFactory.setServiceRegistryClass(XxlRegistryServiceRegistry.class);
    invokerFactory.setServiceRegistryParam(new HashMap<String, String>(){{
        put(XxlRegistryServiceRegistry.XXL_REGISTRY_ADDRESS, address);
        put(XxlRegistryServiceRegistry.ENV, env);
    }});

    logger.info(">>>>>>>>>>> xxl-rpc invoker config init finish.");
    return invokerFactory;
}
```

InvokerFactory 参数 | 说明
--- | ---
serviceRegistryClass | 服务注册中心，可选范围：XxlRegistryServiceRegistry.class、LocalServiceRegistry.class；支持灵活自由扩展；
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
client | 服务通讯方案，可选范围：NettyClient（默认）、NettyHttpClient ; 
serializer | 序列化方案，可选范围: HESSIAN（默认）、HESSIAN1；
callType | 请求类型，可选范围：SYNC（默认）、ONEWAY、FUTURE、CALLBACK；
loadBalance | 负载均衡类型，可选范围：ROUND（默认）、RANDOM、LRU、LFU、CONSISTENT_HASH；
version | 服务版本，默认空；可据此区分同一个“服务API” 的不同版本；
timeout | 服务超时时间，单位毫秒；
address | 服务远程地址，ip:port 格式；选填；非空时将会优先实用该服务地址，为空时会从注册中心服务地址发现；
accessToken | 服务鉴权Token，非空时生效；

 


#### 2.3.4 测试

```
// 参考代码位置：com.xxl.rpc.sample.client.controller.IndexController
```
代码中将上面配置的消费方 invoker 远程服务注入到测试 Controller 中使用，调用该服务，查看看是否正常。
如果正常，说明该接口项目通过XXL-RPC从 client 项目调用了 server 项目中的服务，夸JVM进行了一次RPC通讯。

访问该Controller地址即可进行测试：http://127.0.0.1:8081/?name=jack



## 三、快速入门（frameless 无框架版本）
 
得益于优良的兼容性与模块化设计，不限制外部框架；除 spring/springboot 环境之外，理论上支持运行在任何Java代码中，甚至main方法直接启动运行；

以示例项目 “xxl-rpc-sample-frameless” 为例讲解；该示例项目以直连IP方式进行演示，也可以选择接入注册中心方式使用，如接入 XXL-REGISTRY。

### 3.1 API方式创建“服务提供者”：
```
// 参考代码位置：com.xxl.rpc.sample.server.XxlRpcServerApplication

// init
XxlRpcProviderFactory providerFactory = new XxlRpcProviderFactory();
providerFactory.setServer(NettyServer.class);
providerFactory.setSerializer(HessianSerializer.class);
providerFactory.setCorePoolSize(-1);
providerFactory.setMaxPoolSize(-1);
providerFactory.setIp(null);
providerFactory.setPort(7080);
providerFactory.setAccessToken(null);
providerFactory.setServiceRegistry(null);
providerFactory.setServiceRegistryParam(null);

// add services
providerFactory.addService(DemoService.class.getName(), null, new DemoServiceImpl());

// start
providerFactory.start();

while (!Thread.currentThread().isInterrupted()) {
    TimeUnit.HOURS.sleep(1);
}

// stop
providerFactory.stop();
```
### 3.2 API方式创建“服务消费者”：

```
// 参考代码位置：com.xxl.rpc.sample.client.XxlRpcClientAplication

// init client
XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
referenceBean.setClient(NettyClient.class);
referenceBean.setSerializer(HessianSerializer.class);
referenceBean.setCallType(CallType.SYNC);
referenceBean.setLoadBalance(LoadBalance.ROUND);
referenceBean.setIface(DemoService.class);
referenceBean.setVersion(null);
referenceBean.setTimeout(500);
referenceBean.setAddress("127.0.0.1:7080");
referenceBean.setAccessToken(null);
referenceBean.setInvokeCallback(null);
referenceBean.setInvokerFactory(null);

DemoService demoService = (DemoService) referenceBean.getObject();

// test
UserDTO userDTO = demoService.sayHi("[SYNC]jack");
System.out.println(userDTO);
```


## 四、系统设计

### 4.1 系统架构图
![输入图片说明](https://www.xuxueli.com/doc/static/xxl-rpc/images/img_DNq6.png "在这里输入图片标题")

### 4.2 核心思想
提供稳定高性能的RPC远程服务调用功能，简化分布式服务通讯开发。

### 4.3 角色构成
- 1、provider：服务提供方；
- 2、invoker：服务消费方；
- 3、serializer: 序列化模块；
- 4、remoting：服务通讯模块；
- 5、registry：服务注册中心；
- 6、admin：服务治理、监控中心：管理服务节点信息，统计服务调用次数、QPS和健康情况；（非必选，暂未整理发布...）

### 4.4 RPC工作原理剖析
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

### 4.5 TCP通讯模型
![输入图片说明](https://www.xuxueli.com/doc/static/xxl-rpc/images/img_b1IX.png "在这里输入图片标题")

consumer和provider采用NIO方式通讯，其中TCP通讯方案可选NETTY具体选型，高吞吐高并发；但是仅仅依靠单个TCP连接进行数据传输存在瓶颈和风险，因此XXL-RPC在consumer端自身实现了内部连接池，consumer和provider之间为了一个连接池，当尽情底层通讯是会取出一条TCP连接进行通讯（可参考上图）。

### 4.6 sync-over-async
![输入图片说明](https://www.xuxueli.com/doc/static/xxl-rpc/images/img_pMtS.png "在这里输入图片标题")

XXL-RPC采用NIO进行底层通讯，但是NIO是异步通讯模型，调用线程并不会阻塞获取调用结果，因此，XXL-RPC实现了在异步通讯模型上的同步调用，即“sync-over-async”，实现原理如下，可参考上图进行理解：

- 1、每次请求会生成一个唯一的RequestId和一个RpcResponse，托管到请求池中。
- 2、调度线程，执行RpcResponse的get方法阻塞获取本次请求结果；
- 3、然后，底层通过NIO方式发起调用，provider异步响应请求结果，然后根据RequestId寻找到本次调用的RpcResponse，设置响应结果后唤醒调度线程。
- 4、调度线程被唤醒，返回异步响应的请求数据。

### 4.7 注册中心

XXL-RPC的注册中心，是可选组件，支持服务注册并动态发现；

可选择不启用，直接指定服务提供方机器地址通讯；

选择启用时，内置可选方案：“XXL-REGISTRY 轻量级注册中心”（推荐）、“ZK注册中心”、“Local注册中心”等；

##### a、XXL-REGISTRY 轻量级注册中心（推荐）

推荐使用内置的 "XXL-REGISTRY" 作为注册中心。可前往 XXL-REGISTRY (https://github.com/xuxueli/xxl-registry ) 查看部署文档。非常轻量级，一分钟可完成部署工作。

更易于集群部署、横向扩展，搭建与学习成本更低，推荐采用该方式；


##### b、ZK注册中心
内置“ZK注册中心”，可选组件，结构图如下：

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-rpc/images/img_m3Ma.png "在这里输入图片标题")

原理：        
XXL-RPC中每个服务在zookeeper中对应一个节点，如图"iface name"节点，该服务的每一个provider机器对应"iface name"节点下的一个子节点，如图中"192.168.0.1:9999"、"192.168.0.2:9999"和"192.168.0.3:9999"，子节点类型为zookeeper的EPHMERAL类型，该类型节点有个特点，当机器和zookeeper集群断掉连接后节点将会被移除。consumer底层可以从zookeeper获取到可提供服务的provider集群地址列表，从而可以向其中一个机器发起RPC调用。


### 4.8 在线服务目录  
服务提供方新增 "/services" 服务目录功能，可查看在线服务列表；暂时仅针对NETTY_HTTP通讯方案，浏览器访问地址 "{端口地址}/services" 即可。

### 4.9 如何切换“通讯方案”选型
XXL-RPC提供多中通讯方案：支持 TCP 和 HTTP 两种通讯方式进行服务调用；其中 TCP 提供可选方案 NETTY  ，HTTP 提供可选方案 NETTY_HTTP （新版本移除了Mina和Jetty通讯方案，主推Netty；如果有需要可以参考旧版本；）；

如果需要切换XXL-RPC“通讯方案”，只需要执行以下两个步骤即可：
- a、引入通讯依赖包，排除掉其他方案依赖，各方案依赖如下：
    - NETTY：依赖 netty-all ；
    - NETTY_HTTP：依赖 netty-all ；
- b、修改通讯枚举，需要同时在“服务方”与“消费方”两端一同修改，通讯枚举属性代码位置如下：
    - 服务工厂 "XxlRpcSpringProviderFactory.netType" ：可参考springboot示例组件初始化代码；
    - 服务引用注解 "XxlRpcReference.netType" | 服务Bean对象 "XxlRpcReferenceBean.netType" ：可参考springboot示例组件初始化代码；


### 4.9 如何切换“注册中心”选型
XXL-RPC的注册中心，是一个可选组件，不强制依赖；支持服务注册并动态发现；     
可选择不启用，直接指定服务提供方机器地址通讯；     
选择启用时，原生提供多种开箱即用的注册中心可选方案，包括：“XXL-RPC原生轻量级注册中心”、“ZK注册中心”、“Local注册中心”等；  

如果需要切换XXL-RPC“注册中心”，只需要执行以下两个步骤即可：
- a、引入注册注册中心依赖包，排除掉其他方案依赖，各方案依赖如下：
    - XXL-RPC原生轻量级注册中心：轻量级、无第三方依赖；
    - ZK注册中心：依赖 zookeeper
    - Local注册中心：轻量级、零依赖；
- b、修改注册中心配置，需要同时在“服务方”与“消费方”两端一同修改，代码位置如下：
    - XxlRpcSpringProviderFactory.serviceRegistryClass：注册中心实现类，可选：XxlRegistryServiceRegistry.class、LocalServiceRegistry.class、ZkServiceRegistry.class
    - XxlRpcSpringProviderFactory.serviceRegistryParam：注册中心启动参数，各种注册中心启动参数不同，可参考其 start 方案了解；
    
### 4.10 泛化调用
XXL-RPC 提供 "泛华调用" 支持，服务调用方不依赖服务方提供的API；泛化调用通常用于框架集成，比如 "网关平台、跨语言调用、测试平台" 等；
开启 "泛华调用" 时服务方不需要做任何调整，仅需要调用方初始化一个泛华调用服务Reference （"XxlRpcGenericService"） 即可。

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

### 5.3 版本 v1.2.1 Release Notes[2018-11-09]
- 1、内置注册中心选择ZK时逻辑优化，ZK初始化时unlock逻辑调整，优化断线重连特性；
- 2、除了springboot类型示例；新增无框架示例项目 "xxl-rpc-sample-frameless"。不依赖第三方框架，只需main方法即可启动运行；
- 3、选型http通讯方式时，校验为IP端口格式地址则主动添加地址前缀；
- 4、RPC异步请求逻辑优化，请求异常时主动通知Client端，避免无效等待时间；
- 5、http通讯方式选型jetty时，线程池升级为QueuedThreadPool，修复jetty9.4版本server自动销毁问题；
- 6、Server新增 "/services" 目录功能，可查看在线服务列表；

### 5.4 版本 v1.2.2 Release Notes[2018-11-26]
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

### 5.5 版本 v1.3.0 Release Notes[2018-12-02]
- 1、原生注册中心拆分为独立项目 "xxl-registry"（https://github.com/xuxueli/xxl-registry ），提供服务注册restful服务，并提送响应client端依赖用于简化接入难度；
- 2、NativeServiceRegistry 更名为 XxlRegistryServiceRegistry；
- 3、POM依赖升级，冗余POM清理；
- 4、代码优化：XxlRpcInvokerFactory 移除 static 代码块及相关组件，进一步实现组件无状态；
- 5、服务注册逻辑优化，避免地址重复生成；

### 5.6 版本 v1.3.1 Release Notes[2018-12-21]
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


### 5.7 版本 v1.3.2 Release Notes[2019-02-21]
- 1、泛化调用：服务调用方不依赖服务方提供的API；
- 2、新增通讯方案 "NETTY_HTTP"；
- 3、新增序列化方案 "KRYO"；
- 4、通讯效率优化：TCP连接池取消，改为单一长连接，移除commons-pool2依赖；
- 5、RPC请求路由时空地址处理优化；
- 6、通讯连接池address参数优化，出IP:PORT格式外兼容支持常规URL格式地址；
- 7、线程名称优化，便于适配监控快速进行线程定位；

### 5.8 版本 v1.4.0 Release Notes[2019-04-20]
- 1、LRU路由更新不及时问题修复；
- 2、JettyClient Buffer 默认长度调整为5M；
- 3、Netty Http客户端配置优化；
- 4、升级依赖版本，如netty/mina/spring等

### 5.9 版本 v1.4.1 Release Notes[2019-05-23]
- 1、客户端长连优化，修复初始化时服务不可用导致长连冗余创建的问题；
- 2、升级依赖版本，如netty/mina/jetty/jackson/spring/spring-boot等;
- 3、空闲链接自动回收：服务端与客户端主动检测空闲链接并回收，及时释放相关资源(netty、mina)；空闲超10min自动释放；

### 5.10 版本 v1.4.2 Release Notes[2019-11-18]
- 1、长连心跳保活：客户端周期性发送心跳请求给服务端保活；服务端连续三次未收到心跳时，销毁连接；
- 2、服务线程优化，支持自定义线程参数；
- 3、API重构：初始化枚举改为接口实例，方便扩展；
- 4、代码优化，ConcurrentHashMap变量类型改为ConcurrentMap，避免因不同版本实现不同导致的兼容性问题；
- 5、Netty Http客户端优化，识别并过滤非法响应数据；
- 6、通讯方案收敛：主推Netty和Netty_Http，移除Mina和Jetty内置扩展，如有需求自行扩展维护；
- 7、序列化方案收敛：主推HESSIAN和HESSIAN1，移除protostuff、KRYO、JACKSON内置扩展，如有需求自行扩展维护；
- 8、升级依赖版本，如netty/mina/hessian/jackson/zookeeper等;


### 5.11 版本 v1.5.0 Release Notes[迭代中]
- 1、IpUtil优化：增加连通性校，过滤明确非法的网卡；


### TODO
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
    - 网关：servlet3 + 泛华调用模块；计划：基于DB轻量级注册中心，服务动态发现，自动转发；
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
- [ING]"ConnectClient#clientLock" 优化，复用连接对象；
- 长连心跳、断线重连、空闲连接回收；


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
无论金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](https://www.xuxueli.com/page/donate.html )
