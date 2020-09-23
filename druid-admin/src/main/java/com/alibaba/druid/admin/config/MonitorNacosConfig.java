package com.alibaba.druid.admin.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author linchtech
 * @date 2020-09-20 22:29
 **/
@Configuration
@ConditionalOnProperty(value = "spring.cloud.nacos.discovery.enabled")
public class MonitorNacosConfig {

    @Bean
    public DiscoveryClient nacosDiscoveryClient(NacosDiscoveryProperties nacosDiscoveryProperties) {
        return new NacosDiscoveryClient(nacosDiscoveryProperties);
    }

}
