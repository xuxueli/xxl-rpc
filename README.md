<p align="center">
    <img src="https://raw.githubusercontent.com/xuxueli/xxl-job/master/doc/images/xxl-logo.jpg" width="150">
    <h3 align="center">XXL-RPC</h3>
    <p align="center">
        XXL-RPC, A high performance, distributed RPC framework.
        <br>
        <a href="https://www.xuxueli.com/xxl-rpc/"><strong>-- Home Page --</strong></a>
        <br>
        <br>
        <a href="https://travis-ci.org/xuxueli/xxl-rpc">
            <img src="https://travis-ci.org/xuxueli/xxl-rpc.svg?branch=master" >
        </a>
        <a href="https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-rpc/">
            <img src="https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-rpc/badge.svg" >
        </a>
        <a href="https://github.com/xuxueli/xxl-rpc/releases">
            <img src="https://img.shields.io/github/release/xuxueli/xxl-rpc.svg" >
        </a>
         <a href="http://www.gnu.org/licenses/gpl-3.0.html">
            <img src="https://img.shields.io/badge/license-GPLv3-blue.svg" >
         </a>
         <a href="https://www.xuxueli.com/page/donate.html">
            <img src="https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat-square" >
         </a>
    </p>    
</p>


## Introduction

XXL-RPC is a high performance, distributed RPC framework.Provides a stable and high performance RPC function.
Features such as "high-performance、distributed、service-registry、load-balancing、service-governance" 
Now, it's already open source, real "out-of-the-box".

XXL-RPC 是一个分布式服务框架，提供稳定高性能的RPC远程服务调用功能。拥有"高性能、分布式、注册中心、负载均衡、服务治理"等特性。现已开放源代码，开箱即用。


## Documentation
- [中文文档](https://www.xuxueli.com/xxl-rpc/)


## Features

- 1、快速接入：接入步骤非常简洁，两分钟即可上手；
- 2、服务透明：系统完整的封装了底层通信细节，开发时调用远程服务就像调用本地服务，在提供远程调用能力时不损失本地调用的语义简洁性；
- 3、多调用方案：支持 SYNC、ONEWAY、FUTURE、CALLBACK 等方案；
- 4、多通讯方案：支持 TCP 和 HTTP 两种通讯方式进行服务调用；其中 TCP 提供可选方案 NETTY 或 MINA ，HTTP 提供可选方案 NETTY_HTTP 或 Jetty；
- 5、多序列化方案：支持 HESSIAN、HESSIAN1、PROTOSTUFF、JSON 等方案；
- 6、负载均衡/软负载：提供丰富的负载均衡策略，包括：轮询、随机、LRU、LFU、一致性HASH等；
- 7、注册中心：可选组件，支持服务注册并动态发现；可选择不启用，直接指定服务提供方机器地址通讯；选择启用时，内置可选方案：“XXL-REGISTRY 轻量级注册中心”（推荐）、“ZK注册中心”、“Local注册中心”等；
- 8、服务治理：提供服务治理中心，可在线管理注册的服务信息，如服务锁定、禁用等；
- 9、服务监控：可在线监控服务调用统计信息以及服务健康状况等（计划中）；
- 10、容错：服务提供方集群注册时，某个服务节点不可用时将会自动摘除，同时消费方将会移除失效节点将流量分发到其余节点，提高系统容错能力。
- 11、解决1+1问题：传统分布式通讯一般通过nginx或f5做集群服务的流量负载均衡，每次请求在到达目标服务机器之前都需要经过负载均衡机器，即1+1，这将会把流量放大一倍。而XXL-RPC将会从消费方直达服务提供方，每次请求直达目标机器，从而可以避免上述问题；
- 12、高兼容性：得益于优良的兼容性与模块化设计，不限制外部框架；除 spring/springboot 环境之外，理论上支持运行在任何Java代码中，甚至main方法直接启动运行；
- 13、泛化调用：服务调用方不依赖服务方提供的API；


## Communication

- [社区交流](https://www.xuxueli.com/page/community.html)


## Contributing
Contributions are welcome! Open a pull request to fix a bug, or open an [Issue](https://github.com/xuxueli/xxl-rpc/issues/) to discuss a new feature or change.

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-rpc/issues/) 讨论新特性或者变更。

## 接入登记
更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-rpc/issues/2 ) 登记，登记仅仅为了产品推广。


## Copyright and License
This product is open source and free, and will continue to provide free community technical support. Individual or enterprise users are free to access and use.

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2015-present, xuxueli.

产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。


## Donate
No matter how much the amount is enough to express your thought, thank you very much ：）     [To donate](https://www.xuxueli.com/page/donate.html )

无论金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](https://www.xuxueli.com/page/donate.html )
