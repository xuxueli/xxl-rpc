<p align="center">
    <img src="https://www.xuxueli.com/doc/static/xxl-job/images/xxl-logo.jpg" width="150">
    <h3 align="center">XXL-REGISTRY</h3>
    <p align="center">
        XXL-REGISTRY, A lightweight distributed service registry and discovery platform.
        <br>
        <a href="https://www.xuxueli.com/xxl-registry/"><strong>-- Home Page --</strong></a>
        <br>
        <br>
        <a href="https://github.com/xuxueli/xxl-registry/actions">
            <img src="https://github.com/xuxueli/xxl-registry/workflows/Java%20CI/badge.svg" >
        </a>
        <a href="https://hub.docker.com/r/xuxueli/xxl-registry-admin/">
            <img src="https://img.shields.io/badge/docker-passing-brightgreen.svg" >
        </a>
        <a href="https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-registry/">
            <img src="https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-registry/badge.svg" >
        </a>
        <a href="https://github.com/xuxueli/xxl-registry/releases">
            <img src="https://img.shields.io/github/release/xuxueli/xxl-registry.svg" >
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

XXL-REGISTRY is a lightweight distributed service registry and discovery platform.
Features such as "lightweight, second dynamic registration, multi-environment, cross-language, cross-room deployment".
Now, it's already open source, real "out-of-the-box".

XXL-REGISTRY 是一个轻量级分布式服务注册中心，拥有"轻量级、秒级注册上线、多环境、跨语言、跨机房"等特性。现已开放源代码，开箱即用。


## Documentation
- [中文文档](https://www.xuxueli.com/xxl-registry/)


## Features

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


## Communication

- [社区交流](https://www.xuxueli.com/page/community.html)


## Contributing
Contributions are welcome! Open a pull request to fix a bug, or open an [Issue](https://github.com/xuxueli/xxl-registry/issues/) to discuss a new feature or change.

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-registry/issues/) 讨论新特性或者变更。

## 接入登记
更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-registry/issues/1 ) 登记，登记仅仅为了产品推广。


## Copyright and License
This product is open source and free, and will continue to provide free community technical support. Individual or enterprise users are free to access and use.

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2015-present, xuxueli.

产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。


## Donate
No matter how much the amount is enough to express your thought, thank you very much ：）     [To donate](https://www.xuxueli.com/page/donate.html )

无论金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](https://www.xuxueli.com/page/donate.html )
