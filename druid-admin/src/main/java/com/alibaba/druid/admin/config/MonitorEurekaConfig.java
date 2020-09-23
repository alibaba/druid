package com.alibaba.druid.admin.config;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author linchtech
 * @date 2020-09-22 12:02
 **/
@Configuration
@ConditionalOnProperty(value = "eureka.client.enabled")
public class MonitorEurekaConfig {

    private final EurekaClient eurekaClient;

    private final EurekaClientConfig clientConfig;

    public MonitorEurekaConfig(EurekaClient eurekaClient,
                                 EurekaClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.eurekaClient = eurekaClient;
    }

    @Bean
    public DiscoveryClient discoveryClient(EurekaClient client,
                                           EurekaClientConfig clientConfig) {
        return new EurekaDiscoveryClient(client, clientConfig);
    }
}
