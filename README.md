# 《分布式服务通讯框架XXL-RPC》
## 一、简介

#### 1.1 概述
XXL-RPC是一个分布式服务通讯框架，提供稳定高性能的RPC远程服务调用功能。现已开放源代码，开箱即用。

#### 1.2 特性
- 1、快速接入：接入步骤非常简洁，两分钟即可上手；
- 2、服务透明：调用远程服务就像调用本地服务，开发透明化，简化开发难度；
- 3、注册中心（可选）：支持使用zookeeper作为服务注册中心，服务注册并动态发现。同时，也可以不使用注册中心，直接指定服务提供方机器地址进行RPC通讯；
- 4、软负载均衡及容错：服务提供方集群注册时，在使用软负载算法进行流量分发；
- 5、容错：服务提供方集群注册时，某个服务节点不可用时将会自动摘除，同时消费方将会移除失效节点将流量分发到其余节点，提高系统容错能力。
- 6、TCP/HTTP通讯：支持TCP和HTTP两种通讯方式进行服务调用，其中TCP通讯可以执行NETTY或MINA作为可选通讯方案，以提供高效的服务通讯支持；
- 7、序列化：支持hessian、protobuf和jackson等多种序列化方案；
- 8、服务治理：提供服务治理中心，可在线管理注册的服务信息，如管理服务节点、节点权重等；（部分实现）
- 9、服务监控：可在线监控服务调用统计信息以及服务健康状况等（计划中）；
- 10、解决1+1问题：传统分布式通讯一般通过nginx或f5做集群服务的流量负载均衡，如hessian，每次请求在到达目标服务机器之前都需要经过负载均衡机器，即1+1，这将会把流量放大一倍。而XXL-RPC将会从消费方至服务提供方建立TCP长连接，每次请求直达目标机器，从而可以避免上述问题；

#### 1.3 下载
源码地址 (将会在两个git仓库同步发布最新代码)
- [github地址](https://github.com/xuxueli/xxl-rpc)
- [git.osc地址](http://git.oschina.net/xuxueli0323/xxl-rpc)

博客地址
- [oschina地址](http://my.oschina.net/xuxueli/blog/738279)
- [cnblogs地址](http://www.cnblogs.com/xuxueli/p/4845111.html)

技术交流群(仅作技术交流)：367260654    [![image](http://pub.idqqimg.com/wpa/images/group.png)](http://shang.qq.com/wpa/qunwpa?idkey=4686e3fe01118445c75673a66b4cc6b2c7ce0641528205b6f403c179062b0a52 )

#### 1.4 环境
- Maven3+
- Jdk1.7+
- Tomcat7+

## 二、系统设计

#### 2.1 系统架构图
![输入图片说明](https://static.oschina.net/uploads/img/201608/26161518_DNq6.png "在这里输入图片标题")

#### 2.2 核心思想
提供稳定高性能的RPC远程服务调用功能，简化分布式服务通讯开发。

#### 2.3 角色构成
- 1、provider：服务提供方；
- 2、consumer：服务消费方；
- 3、registry：服务注册中心：注册和发现服务；
- 4、admin：服务治理中心：管理服务节点和信息（部分实现）；
- 5、monitor：服务监控中心：统计服务调用次数、QPS和健康情况（规划中...）；

#### 2.4 RPC工作原理剖析
![输入图片说明](https://static.oschina.net/uploads/img/201608/26162040_XEVY.png "在这里输入图片标题")

#### 2.5 TCP通讯模型
![输入图片说明](https://static.oschina.net/uploads/img/201608/26162328_b1IX.png "在这里输入图片标题")

#### 2.6 sync-over-async
![输入图片说明](https://static.oschina.net/uploads/img/201608/26162406_pMtS.png "在这里输入图片标题")

#### 2.7 注册中心
![输入图片说明](https://static.oschina.net/uploads/img/201608/26162441_m3Ma.png "在这里输入图片标题")

## 三、快速入门
#### 3.1 服务提供方，配置部署
#### 3.2 服务消费方，配置部署
#### 3.3 注册中心，配置部署
#### 3.4 公共依赖
#### 3.5 API方式调用

## 四、版本更新日志
#### 4.1 版本 V1.1.x，新特性

#### 规划中

## 五、其他

#### 7.1 报告问题
XXL-JOB托管在Github上，如有问题可在 [ISSUES](https://github.com/xuxueli/xxl-job/issues/) 上提问，也可以加入技术交流群(仅作技术交流)：367260654

#### 7.2 接入登记
更多接入公司，欢迎在github [登记](https://github.com/xuxueli/xxl-job/issues/1 )

----------------------
github地址：https://github.com/xuxueli/xxl-rpc

git.osc地址：http://git.oschina.net/xuxueli0323/xxl-rpc

博客地址(内附使用教程)：http://www.cnblogs.com/xuxueli/p/4845111.html

技术交流群(仅作技术交流)：367260654

---
- 通讯方案 client pool + proxy + server
    - netty : netty + bytes
    - mina  : 同netty
    - jetty : httpclient post bytes + ip to address + jetty handler
    - servlet   : httpclient post bytes + servlet url + spring HttpRequestHandler, like hessian
- 序列化方案
    - hessian
    - protostuff
    - jackson
- 注册中心:
    - 数据格式: 每个api一个节点,每新增一台机器在对应api下新增一个节点
    - zk地址:维护在磁盘根目录 "/data/webapps/xxl-conf.properties"
    
##### V1.2待完善
- 1、zk逻辑重构
- 2、序列化方案精简
- 3、通讯方案
    - 每个服务, 暴露NIO服务(netty/mina), 同时暴露Http服务(jetty, 7777);
    - servlet方式rpc,抛弃
- 4、服务管理中心,重构
    - 注册服务列表和查看, 注册节点移除
- 5、NIO=7080、Jetty=7070