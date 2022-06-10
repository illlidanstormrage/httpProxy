# 作业 HTTP的通信监控和回放

部署类似Microsoft PetshopWeb 应用基本实例，开发性能测试小工具能截获基于IE浏览器与Web服务器交互的数据报，修改数据包（比如简化起见，修改要搜索的关键字），再把修改后的内容使用多线程的方式发送到服务器。统计请求每个网页上主要元素（gif, css等）需要的时间，以及请求整个网页的时间。

# 实现

本例测试网站为 [https://wttr.in/Mianyang?0](https://wttr.in/Mianyang?0)

该网站 host 为 _wttr.in_ 使用GET方法请求，请求的URL为 **_{location}?0_**

## 功能

本例使用 netty 框架，建立代理服务器，截获浏览器与服务器之间的http报文，并修改GET请求的URL，达到目的。

其具体效果如下图所示：

启动代理服务器前：请求绵阳天气，返回绵阳的天气信息

![img.png](img/before.png)


启动代理服务器后：请求绵阳天气，返回广州的天气信息

![img.png](img/after.png)

## 代码使用方法

### 依赖

python >= 3.6

selenium == 4.0.0

microsoft edge浏览器驱动（需在python文件中指定路径或者添加到path）

JDK >= 8

### 使用方法

项目使用idea构建，用idea引入项目后

1. 运行 *src/test/java/Start* 启动服务器

2. 设置代理，将代理ip地址设置为 127.1.0.1:6677

3. cd src/test/python, 在终端使用python test.py 启动自动化脚本测试

## 目录结构

```
.
├─src   //源代码根目录
│  ├─main   //代码目录                                      
│  │  ├─java 
│  │  │  │  HttpProxyServer.java    //服务器启动代码
│  │  │  │ 
│  │  │  ├─bean   //bean目录
│  │  │  │      ClientRequest.java    //保存http报文主要信息
│  │  │  │      Const.java  //存放静态常量
│  │  │  │
│  │  │  └─handler  //存放报文处理代码
│  │  │      ├─edit  //存放修改报文代码
│  │  │      │      Editor.java  //修改报文的类
│  │  │      │
│  │  │      ├─proxy  //代理处理代码
│  │  │      │      HttpProxyHandler.java  //http代理
│  │  │      │      HttpsProxyHandler.java  //https代理
│  │  │      │      IProxyHandler.java  //http、https的处理方法接口
│  │  │      │      SocksProxyHandler.java  //socks代理
│  │  │      │
│  │  │      ├─response  //处理response报文
│  │  │      │      HttpProxyResponseHandler.java  //http response处理
│  │  │      │      SocksResponseHandler.java  //socks response处理
│  │  │      │
│  │  │      └─utils  //存放功能代码
│  │  │              HttpsSupport.java  //对https报文解析提供支持
│  │  │              ProxyRequestUtil.java  //获取代理的request请求
│  │  │
│  │  └─resources //资源目录，openssl证书，用于https的数据解析
│  │          ca.crt
│  │          ca.key
│  │          ca_private.der
│  │
│  └─test //测试目录
│      └─java
│              Start.java  //测试代码
│      └─python
│              test.py  //测试代码
```

## 流程

```flow
start=>start: 客户端请求
decode_encode=>operation: 解码、编码
HttpProxyHandler=>operation: HttpProxyHandler
isHttp=>condition: 是否为http报文
httpRequestHandle=>operation: 处理Http请求
httpForward=>end: 转发http报文

HttpsProxyHandler=>operation: HttpsProxyHandler
httpsRequestHandle=>operation: 处理Https请求
isHttps=>condition: 是否为https报文
isHost=>condition: 主机是否为wttr.in
httpsEdit=>end: 篡改https报文并发送
httpsForward=>end: 直接转发https报文

SocksProxyHandler=>operation: SocksProxyHandler
socksHandle=>operation: 处理Socks信息
socksEnd=>end: 转发信息

start->decode_encode->HttpProxyHandler->isHttp
isHttp(yes)->httpRequestHandle->httpForward
isHttp(no)->HttpsProxyHandler->isHttps
isHttps(yes)->isHost
isHttps(no)->httpsForward
isHost(yes)->httpsEdit
isHost(no)->httpsForward
isHttps(no)->SocksProxyHandler->socksHandle->socksEnd->socksEnd

```
