<p align="center">
    <img src="https://www.xuxueli.com/doc/static/xxl-job/images/xxl-logo.jpg" width="150">
    <h3 align="center">XXL-RPC</h3>
    <p align="center">
        XXL-RPC, A high performance, distributed RPC framework.
        <br>
        <a href="https://www.xuxueli.com/xxl-rpc/"><strong>-- Home Page --</strong></a>
        <br>
        <br>
        <a href="https://github.com/xuxueli/xxl-rpc/actions">
            <img src="https://github.com/xuxueli/xxl-rpc/workflows/Java%20CI/badge.svg" >
        </a>
        <a href="https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-rpc/">
            <img src="https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-rpc/badge.svg" >
        </a>
        <a href="https://github.com/xuxueli/xxl-rpc/releases">
            <img src="https://img.shields.io/github/release/xuxueli/xxl-rpc.svg" >
        </a>
        <a href="https://github.com/xuxueli/xxl-rpc/">
            <img src="https://img.shields.io/github/stars/xuxueli/xxl-rpc" >
        </a>
        <a href="https://hub.docker.com/r/xuxueli/xxl-rpc-admin/">
            <img src="https://img.shields.io/docker/pulls/xuxueli/xxl-rpc-admin" >
        </a>
        <img src="https://img.shields.io/github/license/xuxueli/xxl-rpc.svg" >
        <a href="https://www.xuxueli.com/page/donate.html">
            <img src="https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat-square" >
        </a>
    </p>    
</p>


## Introduction

XXL-RPC is a high performance, distributed RPC framework.Provides a stable and high performance RPC function.
Features such as "high-performance、distributed、service-registry、load-balancing、service-governance" 
Now, it's already open source, real "out-of-the-box".

XXL-RPC 是一个RPC服务框架，提供一站式服务开发及运营能力。拥有“轻量级、高性能、分布式、动态注册、负载均衡、服务治理”等特性。现已开放源代码，开箱即用。

## Documentation
- [中文文档](https://www.xuxueli.com/xxl-rpc/)

## Communication

- [社区交流](https://www.xuxueli.com/page/community.html)


## Features

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


## Contributing
Contributions are welcome! Open a pull request to fix a bug, or open an [Issue](https://github.com/xuxueli/xxl-rpc/issues/) to discuss a new feature or change.

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-rpc/issues/) 讨论新特性或者变更。

## 接入登记
更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-rpc/issues/2 ) 登记，登记仅仅为了产品推广。


## Copyright and License
This product is open source and free, and will continue to provide free community technical support. Individual or enterprise users are free to access and use.

- Licensed under the Apache License, Version 2.0.
- Copyright (c) 2015-present, xuxueli.

产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。


## Donate
No matter how much the amount is enough to express your thought, thank you very much ：）     [To donate](https://www.xuxueli.com/page/donate.html )

无论金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](https://www.xuxueli.com/page/donate.html )
