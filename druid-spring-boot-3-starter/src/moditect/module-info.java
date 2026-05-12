open module druid.spring.boot3.starter {
    exports com.alibaba.druid.spring.boot3.autoconfigure;
    exports com.alibaba.druid.spring.boot3.autoconfigure.stat;
    exports com.alibaba.druid.spring.boot3.autoconfigure.properties;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires spring.aop;
    requires spring.beans;
    requires druid;
    requires org.slf4j;
    requires java.sql;
    requires java.naming;
    requires java.management;
}
