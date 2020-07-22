package com.alibaba.druid.spring.boot.demo.configurer;

import com.alibaba.druid.spring.boot.demo.util.MyDruidPasswordCallback;
import com.alibaba.druid.util.DruidPasswordCallback;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Druid数据源配置
 *
 * @author Created by 思伟 on 2020/7/22
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class DataSourceConfig {

    /**
     * 数据库密码回调解密
     *
     * @return MyDruidPasswordCallback
     */
    @Primary
    @Bean
    @ConditionalOnMissingBean
    public DruidPasswordCallback myDruidPasswordCallback() {
        return new MyDruidPasswordCallback();
    }

}
