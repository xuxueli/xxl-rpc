# 《分布式服务通讯框架XXL-RPC》
## 一、简介

#### 1.1 概述
XXL-RPC是一个分布式服务通讯框架，提供稳定高性能的RPC远程服务调用功能。现已开放源代码，开箱即用。

#### 1.2 特性
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

#### 1.3 背景

**WHAT** ：RPC（Remote Procedure Call Protocol，远程过程调用），调用远程服务就像调用本地服务，在提供远程调用能力时不损失本地调用的语义简洁性；

**WHY** ：一般公司，尤其是大型互联网公司内部系统由上千上万个服务组成，不同的服务部署在不同机器，跑在不同的JVM上，此时需要解决两个问题：
- 1、如果我需要依赖别人的服务，但是别人的服务在远程机器上，我该如何调用？
- 2、如果其他团队需要使用我的服务，我该怎样发布自己的服务供他人调用？

**HOW** ：

答案：“XXL-RPC”：
- 1、如何调用：只需要知晓远程服务的stub和地址，即可方便的调用远程服务，同时调用透明化，就像调用本地服务一样简单；
- 2、如何发布：只需要提供自己服务的stub和地址，别人即可方便的调用我的服务，在开启注册中心的情况下服务动态发现，只需要提供服务的stub即可；

#### 1.4 下载
源码地址 (将会在两个git仓库同步发布最新代码)
- [github地址](https://github.com/xuxueli/xxl-rpc)
- [gitee地址](http://gitee.com/xuxueli0323/xxl-rpc)

博客地址
- [oschina地址](http://my.oschina.net/xuxueli/blog/738279)
- [cnblogs地址](http://www.cnblogs.com/xuxueli/p/4845111.html)

##### 技术交流群 (仅作技术交流)

- [社区交流](http://www.xuxueli.com/page/community.html)

#### 1.5 环境
- Maven3+
- Jdk1.7+
- Tomcat7+

## 二、系统设计

#### 2.1 系统架构图
![输入图片说明](https://static.oschina.net/uploads/img/201608/26161518_DNq6.png "在这里输入图片标题")


架构图模块解读:
- 1、proxy: 消费方,远程服务代理
- 2、Registry: 注册中心
- 3、request-response: 请求、响应的消息体约定
- 4、serializer: 序列化模块
- 5、sync-over-async: 同步的异步, 在NIO通讯模型上实现同步调用
- 6、rpc: 远程过程调用, 具体的服务执行过程


#### 2.2 核心思想
提供稳定高性能的RPC远程服务调用功能，简化分布式服务通讯开发。

#### 2.3 角色构成
- 1、provider：服务提供方；
- 2、consumer：服务消费方；
- 3、registry：服务注册中心：注册和发现服务；
- 4、admin：服务治理中心：管理服务节点信息（部分实现）；
- 5、monitor：服务监控中心：统计服务调用次数、QPS和健康情况（规划中...）；

#### 2.4 RPC工作原理剖析
![输入图片说明](https://static.oschina.net/uploads/img/201608/26162040_XEVY.png "在这里输入图片标题")

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

#### 2.5 TCP通讯模型
![输入图片说明](https://static.oschina.net/uploads/img/201608/26162328_b1IX.png "在这里输入图片标题")

consumer和provider采用NIO方式通讯，可选NETTY或MINA方案，高吞吐高并发；但是仅仅依靠单个TCP连接进行数据传输存在瓶颈和风险，因此XXL-RPC在consumer端自身实现了内部连接池，consumer和provider之间为了一个连接池，当尽情底层通讯是会取出一条TCP连接进行通讯（可参考上图）。

#### 2.6 sync-over-async
![输入图片说明](https://static.oschina.net/uploads/img/201608/26162406_pMtS.png "在这里输入图片标题")

XXL-RPC采用NIO进行底层通讯，但是NIO是异步通讯模型，调用线程并不会阻塞获取调用结果，因此，XXL-RPC实现了在异步通讯模型上的同步调用，即“sync-over-async”，实现原理如下，可参考上图进行理解：

- 1、每次请求会生成一个唯一的RequestId和一个RpcResponse，托管到请求池中。
- 2、调度线程，执行RpcResponse的get方法阻塞获取本次请求结果；
- 3、然后，底层通过NIO方式发起调用，provider异步响应请求结果，然后根据RequestId寻找到本次调用的RpcResponse，设置响应结果后唤醒调度线程。
- 4、调度线程被唤醒，返回异步响应的请求数据。

#### 2.7 注册中心
![输入图片说明](https://static.oschina.net/uploads/img/201608/26162441_m3Ma.png "在这里输入图片标题")

原理：        
XXL-RPC中每个服务在zookeeper中对应一个节点，如图"iface name"节点，该服务的每一个provider机器对应"iface name"节点下的一个子节点，如图中"192.168.0.1:9999"、"192.168.0.2:9999"和"192.168.0.3:9999"，子节点类型为zookeeper的EPHMERAL类型，该类型节点有个特点，当机器和zookeeper集群断掉连接后节点将会被移除。consumer底层可以从zookeeper获取到可提供服务的provider集群地址列表，从而可以向其中一个机器发起RPC调用。

XXL-RPC支持两种方式设置远程服务地址：        
- 1、手动设置服务地址：需要为每个远程服务手动配置服务地址；
- 2、zookeeper注册中心：采用Zookeeper作为注册中心，服务自动注册和动态发现；

## 三、快速入门
#### 3.1 准备工作
- 1、zookeeper集群（可选，如果不开启服务注册功能，可忽略）；
- 2、编译项目

![输入图片说明](https://static.oschina.net/uploads/img/201608/26191137_ZUd7.png "在这里输入图片标题")

源码目录介绍：
- /doc
- /xxl-rpc-admin         （服务治理中心，如果不开启服务注册功能，可忽略）
- /xxl-rpc-core            （核心包）
- /xxl-rpc-example     （示例example项目）
    - /xxl-rpc-example-api        （示例example项目，公共API接口）
    - /xxl-rpc-example-client    （示例example项目，服务消费方consumer调用示例）
    - /xxl-rpc-example-server  （示例example项目，服务提供方provider示例）

#### 3.2 配置部署“服务治理中心”（如果不开启服务注册功能，可忽略）

    项目：xxl-rpc-admin

- 1、配置Zookeeper地址：

    在磁盘地址创建配置文件“/data/webapps/xxl-conf.properties”，配置内容如下：
    ```
    // zookeeper集群时，多个地址用逗号分隔
    zkserver=127.0.0.1:2181
    ```
- 2、配置登录账号密码：

![输入图片说明](https://static.oschina.net/uploads/img/201608/26201131_MDbk.png "在这里输入图片标题")

#### 3.3 项目中使用XXL-RPC（以示例项目xxl-rpc-example为例讲解）

##### 配置Zookeeper地址（如果不开启服务注册功能，可忽略）

    在磁盘地址创建配置文件“/data/webapps/xxl-conf.properties”，配置内容如下：
    ```
    // zookeeper集群时，多个地址用逗号分隔
    zkserver=127.0.0.1:2181
    ```

##### 1、开发“公共API接口”（参考xxl-rpc-example-api）
- 1、开发一个API接口

![输入图片说明](https://static.oschina.net/uploads/img/201608/26203550_XsDP.png "在这里输入图片标题")

- 2、开发一个API接口所需要的DTO对象，注意需要实现序列化接口

![输入图片说明](https://static.oschina.net/uploads/img/201608/26203550_XsDP.png "在这里输入图片标题")

##### 2、开发“服务提供方”（参考xxl-rpc-example-server）
- 1、配置maven依赖：XXL-RPC核心依赖 + 公共API接口依赖

![输入图片说明](https://static.oschina.net/uploads/img/201608/26204911_92fr.png "在这里输入图片标题")

- 2、配置“RPC服务工厂” + 配置服务扫描包路径

RPC服务工厂参数 | 说明
-- | --
port | (可选) TCP通讯端口，默认7080
http_port | (可选) HTTP通讯端口,默认7070
netcom |  (可选) TCP通讯方案,默认NETTY, 可选范围: NETTY、MINA
serializer | (可选) 序列化方案,默认HESSIAN, 可选范围: HESSIAN、PROTOSTUFF、JSON
zookeeper_switch |  (可选) 是否启动Zookeeper注册中心, 默认false, 可选范围: true、false。如果不启动注册中心，服务工厂将不会向注册中心中注册服务。

![输入图片说明](https://static.oschina.net/uploads/img/201608/26205854_9StD.png "在这里输入图片标题")

- 3、开发“provider服务实现类”，实现api接口的功能
    ```
        // XXL-RPC中远程服务，通过@XxlRpcService的方式进行识别，value值为服务API接口类。同时，因为需要将该服务托管给Spring维护，所以示例中类上要加上@Service注解（通过XML方式配置的服务，可以忽略该注解）。
        // 因此：示例中服务实现类上需要加以下两个注解。
        @XxlRpcService(IDemoService.class)
        @Service
    ```

![输入图片说明](https://static.oschina.net/uploads/img/201608/26210203_D0Rr.png "在这里输入图片标题")

##### 3、开发“服务消费方”（参考xxl-rpc-example-client）
- 1、配置maven依赖：XXL-RPC核心依赖 + 公共API接口依赖

![输入图片说明](https://static.oschina.net/uploads/img/201608/26211323_Cc12.png "在这里输入图片标题")

- 2、消费方consumer，远程服务配置

consumer参数 | 说明
-- | --
serverAddress |(可选) 服务Provider地址, 为空则开启Zookeeper注册中心, 从注册中心动态发现服务, 否则将使用改定的固定地址;
netcom |  (可选) TCP通讯方案,默认NETTY, 可选范围: NETTY、MINA
serializer | (可选) 序列化方案,默认HESSIAN, 可选范围: HESSIAN、PROTOSTUFF、JSON
iface | (必选) 服务对应的api接口;

![输入图片说明](https://static.oschina.net/uploads/img/201608/26211554_vkA9.png "在这里输入图片标题")

##### 3、测试

测试代码如下，访问该Controller地址即可进行测试（http://localhost:8080/xxl-rpc-example-client/）：

![输入图片说明](https://static.oschina.net/uploads/img/201608/26212621_JeRY.png "在这里输入图片标题")

代码中将上面配置的消费方consumer远程服务注入到测试Controller中使用，调用该服务，查看看是否正常。如果正常，说明xxl-rpc-example-client项目通过XXL-RPC调用了项目xxl-rpc-example-server中的服务，夸JVM进行了一次RPC通讯。

##### 4、API方式创建“消费方consumer”，调用远程服务（可参考上文“consumer参数配置”）
- 1、TCP方式：

![输入图片说明](https://static.oschina.net/uploads/img/201608/26213901_INv2.png "在这里输入图片标题")

- 2、HTTP方式：

![输入图片说明](https://static.oschina.net/uploads/img/201608/26213958_f0Lh.png "在这里输入图片标题")

## 四、版本更新日志
#### 4.1 版本V1.1 新特性
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

#### 规划中
- 目前使用 iface 接口包名进行服务注册, 新增属性 registry-key 用来服务注册,便于接口复用,如果为空则默认使用 iface 接口包名; zookeeper-switch 改为 registry-switch;
- 提高系统可用性，以部分功能暂时不可达为代价，防止服务整体缓慢或雪崩
    - 限流=防止负载过高，导致服务雪崩；client、server，双向限流；方法级，QPS限流；在途请求数，流控依据；
    - 降级=10s内超阈值（异常、超时）；拒绝服务、默认值；
        - 超过（熔断模式）：99.9% 返回默认值，0.1%真实请求；
        - 未超过：熔断模式下，每 10s 增加 10% 的流量，直至恢复；
    - 服务隔离：超时较多的请求，自动路由到 “慢线程池” ，避免占用公共线程池；
    - 预热控制，刚启动的节点，只会分配比较少的请求；逐步增大，直至平均。帮助新节点启动；
- 服务注册中心, 节点支持单个移除


## 五、其他

#### 7.1 报告问题
XXL-RPC托管在Github上，如有问题可在 [ISSUES](https://github.com/xuxueli/xxl-rpc/issues/) 上提问，也可以加入上文技术交流群；

#### 7.2 接入登记
更多接入公司，欢迎在github [登记](https://github.com/xuxueli/xxl-rpc/issues/2 )