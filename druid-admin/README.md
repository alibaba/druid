# druid-admin
我们知道druid只针对单个应用做监控,如果应用部署在集群环境下,内置的监控页面将难以被外界访问.

所以druid-admin是对集群环境下的druid的监控数据进行采集汇总

如何使用:

```yml
server:
  port: 19999
spring:
  application:
    name: druid-admin
  main:
    allow-bean-definition-overriding: true
  cloud:
    consul:
      enabled: false
      host: localhost
      port: 8500
      discovery:
        enable: true
        hostname: ${spring.cloud.client.ip-address}
        instance-id: ${spring.application.name}:${vcap.application.instance_id:${spring.application.instance_id:${random.value}}}
        instance-zone: zone1
    nacos:
      discovery:
        enabled: true
        server-addr: localhost:8848
eureka:
  instance:
    preferIpAddress: true
    ipAddress: localhost
    instance-id: ${eureka.instance.ipAddress}:${server.port}:${spring.application.name}
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
    enabled: false
monitor:
  applications:   #需要监控的服务名spring.application.name
  login-username: admin #监控页面的登录用户名和密码
  login-password: 123456



```

druid-admin支持三种注册中心nacos,consul,eureka.可在bootstrap.yml文件中启用不同的注册中心(只能启用一种注册中心)

例如,你的项目中采用的是consul作为注册中心,则druid-admin的配置如下:

```yml
server:
  port: 19999
spring:
  application:
    name: druid-admin
  main:
    allow-bean-definition-overriding: true
  cloud:
    consul:
      enabled: true # 只需修改这里为true
      host: localhost #这里修改为环境中consul的正确地址
      port: 8500
      discovery:
        enable: true
        hostname: ${spring.cloud.client.ip-address}
        instance-id: ${spring.application.name}:${vcap.application.instance_id:${spring.application.instance_id:${random.value}}}
        instance-zone: zone1
    nacos:
      discovery:
        enabled: false
        server-addr: localhost:8848
eureka:
  instance:
    preferIpAddress: true
    ipAddress: localhost
    instance-id: ${eureka.instance.ipAddress}:${server.port}:${spring.application.name}
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
    enabled: false
monitor:
  applications:   #需要监控的服务名spring.application.name
  login-username: admin
  login-password: 123456
```

然后访问:

http://localhost:19999/druid/sql.html