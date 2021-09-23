# druid-admin
我们知道druid只针对单个应用做监控,如果应用部署在集群环境下,内置的监控页面将难以被外界访问.

所以druid-admin是对集群环境下的druid的监控数据进行采集汇总

如何使用:

新建一个SpringBoot工程,添加以下maven依赖:

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-admin</artifactId>
    <version>${druid-version}</version>
</dependency>
<!--添加注册中心,如果集群环境使用的是consul或其他注册中心,请替换为对应版本依赖-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    <version>${nacos-version}</version>
</dependency>
```

application.yml:

```yml
server:
  port: 8888
spring:
  application:
    name: druid-admin
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        username: nacos
        password: nacos
monitor:
  applications:   #需要监控的服务名(其他微服务的spring.application.name)以逗号隔开
  login-username: admin #监控页面的登录用户名和密码
  login-password: 123456

```



然后访问:

http://localhost:8888/druid/sql.html

注意事项:
如果集群中微服务使用了权限框架或拦截器,记得放行/druid/**