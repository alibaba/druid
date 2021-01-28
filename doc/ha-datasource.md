# High Available DataSource 说明

## 1. High Available DataSource 介绍

High Available DataSource（下文简称HA DataSource）即高可用数据源，基于Druid数据源之上进行了二次封装，在无需LVS或HA Proxy的前提下，实现了数据源层面的负载均衡。

例如，如下场景中均可通过HA DataSource配置数据源的连接：

1. 读写分离场景中，有多个数据库的从库提供读服务；
2. 使用了分库分表中间件（例如MyCat）进行分库分表，部署了多台中间件服务；

HA DataSource提供了如下特性：

1. 节点路由 - 根据节点名称指定路由，随机路由，粘性随机路由
2. 节点配置 - 纯手工配置节点，根据配置文件生成节点，根据ZooKeeper信息生成节点
3. 节点健康检查 - 基于ValidConnectionChecker的节点检查机制，检查间隔时间可根据运行情况动态调整

## 2. 节点的配置与路由选择

### 2.1 根据名称路由节点

在Spring配置文件中加入如下DataSoruce配置：

```xml
<bean id="dataSource" class="com.alibaba.druid.pool.ha.HighAvailableDataSource" 
    init-method="init" destroy-method="destroy">
    <property name="dataSourceMap">
        <map>
            <entry key="default" value-ref="fooDataSource" /> <!-- 必须要配置default -->
            <entry key="foo" value-ref="fooDataSource" />
            <entry key="bar" value-ref="barDataSource" />
        </map>
    </property>
    <property name="selector" value="byName" /> <!-- selector默认是random，此处必须要设为byName  -->
</bean>
```

其中，`fooDataSource`和`barDataSource`分别是两个事先配置好的DataSource。

在没有手工进行选择前，默认会使用`default`，如要指定数据源，可使用如下方法：

```java
((HighAvailableDataSource) dataSource).setTargetDataSource("bar");
```

> 注意：指定目标数据源的标志是保存在线程上下文中的，因此在使用完毕后，需要及时将targetDataSource设回default。

### 2.2 根据配置文件生成节点集合

假设有两台MySQL，将JDBC URL、用户名和密码按如下格式写入datasource.properties中：

```properties
ha.db1.url=jdbc:mysql://192.168.0.10:3306/foo?useUnicode=true
ha.db1.username=foo
ha.db1.password=password

ha.db1.url=jdbc:mysql://192.168.0.11:3306/foo?useUnicode=true
ha.db1.username=foo
ha.db1.password=password

# 下面这个由于前缀不同，不会被ha前缀加载
hb.db1.url=jdbc:mysql://192.168.0.12:3306/bar?useUnicode=true
hb.db1.username=bar
hb.db1.password=password
```

此处的`ha`是用于过滤配置项的，在一个配置文件中如存在多个不同前缀，可以通过前缀进行区分。

在Spring配置文件中加入如下DataSoruce配置：

```xml
<bean id="dataSource" class="com.alibaba.druid.pool.ha.HighAvailableDataSource" 
    init-method="init" destroy-method="destroy">
    <property name="dataSourceFile" value="datasource.properties" /> <!-- 默认值是ha-datasource.properties -->
    <property name="propertyPrefix" value="ha" /> <!-- 需要与配置文件中的前缀对应 -->
    <property name="selector" value="random" /> <!-- 还有一种随机是stickyRandom  -->
    <property name="poolPurgeIntervalSeconds" value="60" /> <!-- 删除节点操作的间隔时间 -->
    <property name="allowEmptyPoolWhenUpdate" value="false" /> <!-- 配置文件更新时，是否允许完全清空HA DataSource的节点列表 -->

    <!-- 其他DruidDataSource的常见配置可自行添加 -->
    <!-- ... -->
</bean>
```

> stickyRandom，粘性随机选择，5秒内，同一个线程中多次通过HighAvailableDataSource获取连接时始终会返回同一个DataSource的连接。

每隔60秒，FileNodeListener会扫描一次配置文件，如果文件内容发生变化，则会动态调整节点。如果是删除节点，会将待删除节点先放入一个黑名单，待定PoolUpdater的60秒定时任务执行时一起清理。

配置后，可像使用普通DataSource那样来使用`dataSource` Bean。

### 2.3 根据ZooKeeper生成节点集合

HA DataSource默认基于文件创建随机节点列表，只需提供其他NodeListener的实现类，就可以监听不同的配置源，例如ZookeeperNodeListener就是基于ZooKeeper的。

在Spring配置文件中加入如下DataSoruce配置：

```xml
<bean id="dataSource" class="com.alibaba.druid.pool.ha.HighAvailableDataSource" 
    init-method="init" destroy-method="destroy">
    <property name="nodeListener" value-ref="zkNodeListener" />
    <property name="propertyPrefix" value="ha" />
    <property name="selector" value="random" />
    <property name="poolPurgeIntervalSeconds" value="60" /> <!-- 删除节点操作的间隔时间 -->
    <property name="allowEmptyPoolWhenUpdate" value="false" /> <!-- 配置文件更新时，是否允许完全清空HA DataSource的节点列表 -->
    
    <!-- 其他DruidDataSource的常见配置可自行添加 -->
    <!-- ... -->
</bean>

<bean id="zkNodeListener" class="com.alibaba.druid.pool.ha.node.ZookeeperNodeListener">
    <property name="zkConnectString" value="192.168.0.2:2181" />
    <property name="path" value="/ha-druid-datasources" />
    <property name="urlTemplate" value="jdbc:mysql://${host}:${port}/${database}?useUnicode=true" />
</bean>
```

假设ZooKeeper的/ha-druid-datasources目录下有NodeFoo和NodeBar两个节点，NodeFoo内容如下：

```
ha.host=192.168.0.10
ha.port=3306
ha.database=foo
ha.username=foo
ha.password=password
```

urlTemplate用于根据ZooKeeper数据创建JDBC URL，其中的占位符会替换为具体的值。

```
jdbc:mysql://192.168.0.10:3306/foo?useUnicode=true
```

可以手动在ZooKeeper上添加节点配置，也可以通过代码进行注册，假设对MyCAT服务端代码进行了调整，在服务启动后进行ZooKeeper注册，可以添加如下代码：

```java
// 注册节点
ZookeeperNodeRegister register = new ZookeeperNodeRegister();
register.setZkConnectString("192.168.0.2:2181");
register.setPath("/ha-druid-datasources");
register.init();

List<ZookeeperNodeInfo> payload = new ArrayList<ZookeeperNodeInfo>();
ZookeeperNodeInfo node = new ZookeeperNodeInfo();
node.setPrefix("ha");
node.setHost("192.168.0.10");
node.setPort(3306);
node.setDatabase("foo");
node.setUsername("foo");
node.setPassword("password");
payload.add(node);
register.register("NodeFoo", payload);

// 此处创建的是临时节点，Java进程停止后该节点就会消失，以此实现节点发现和下线。
// 也可以通过register.destroy()从ZooKeeper上删除该节点。
```

ZookeeperNodeListener会监听ZooKeeper的节点内容变更，PoolUpdater每隔60秒会清理已下线节点。

## 3. 随机节点的健康检查

在使用随机节点选择（random）或粘性随机节点选择（stickyRandom）时，HA DataSource会去检查后端节点的健康状态。

### 3.1 检查策略

* 根据 `druid.ha.random.checkingIntervalSeconds` 设定的间隔时间异步进行检测
* 针对每个节点，使用数据源的配置信息新建后端节点的数据库连接，调用`dataSource.validateConnection()` 方法检查连接状态
* 完成检查后关闭新建的连接
* 某个节点连续`druid.ha.random.blacklistThreshold`次检查失败后，会被加入黑名单
* 黑名单会有另外一个线程进行探活检查，每隔`druid.ha.random.recoveryIntervalSeconds`秒进行一次探活，如果探活成功，则会将其从黑名单中移除

### 3.2 策略优化

#### 3.2.1 快速发现失败节点

为了快速发现有问题的节点，HA DataSource做了一系列的优化：

* 节点正常时，`druid.ha.random.checkingIntervalSeconds`进行一次节点检查
* 有节点出现异常后，间隔时间会快速缩短，在短时间内进行第二次检查

#### 3.2.2 减少检查次数

为了保证节点出问题时能快速发现，势必需要频繁进行检查，这样就加重了后端数据库或中间件节点的负担。`RandomDataSourceValidateThread`中记录了每个节点的上次检查成功时间，可以将正常执行的SQL操作也视为检查的一部分，只要SQL正常执行，也算检查成功。

为此，HA DataSource提供了一个`RandomDataSourceValidateFilter`，在SQL执行成功后修改对应DataSource的上次检查成功时间。在为`DruidDataSource`配置`filters`时，只需简单增加一个`haRandomValidator`即可实现上述功能。

### 3.3 参数配置

关于健康检查，HA DataSource提供了4个参数可供配置：

| 配置项                                  | 默认值 | 说明                                         |
| --------------------------------------- | ------ | -------------------------------------------- |
| druid.ha.random.checkingIntervalSeconds | 10秒   | 健康检查间隔时间                             |
| druid.ha.random.validationSleepSeconds  | 0秒    | 健康检查建立连接，等待多少秒后再进行连接校验 |
| druid.ha.random.recoveryIntervalSeconds | 120秒  | 黑名单中DataSource恢复检测的间隔时间         |
| druid.ha.random.blacklistThreshold      | 3次    | 健康检查失败多少次后放入黑名单               |

这些参数配置在Druid的`ConnectProperties`中即可。