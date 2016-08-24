# 分布式服务通讯框架xxl-rpc
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