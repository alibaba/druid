package com.alibaba.druid.spring.boot.autoconfigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author lihengming<89921218@qq.com>
 *
 * <p>druid-spring-boot-autoconfigure 测试例子</p>
 * <p>1.按需配置application.properties，配置项请参考</p>
 * <p>2.run Application</p>
 * <p>3.访问http://127.0.0.1:8080/druid</p>
 * <p>4.访问/user/{id}接口，查看监控效果，如：http://127.0.0.1:8080/user/1</p>
 *
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
