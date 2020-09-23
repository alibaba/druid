package com.alibaba.druid.admin.config;

import com.ecwid.consul.transport.TLSConfig;
import com.ecwid.consul.v1.ConsulClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.ConsulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author linchtech
 * @date 2020-09-20 19:56
 **/
@Configuration
@ConditionalOnConsulEnabled
public class MonitorConsulConfig {

    @Bean
    @ConditionalOnProperty(value = "spring.cloud.consul.enabled")
    public ConsulProperties consulProperties() {
        return new ConsulProperties();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.cloud.consul.enabled")
    public ConsulClient consulClient(ConsulProperties consulProperties) {
        final int agentPort = consulProperties.getPort();
        final String agentHost = !StringUtils.isEmpty(consulProperties.getScheme())
                ? consulProperties.getScheme() + "://" + consulProperties.getHost()
                : consulProperties.getHost();

        if (consulProperties.getTls() != null) {
            ConsulProperties.TLSConfig tls = consulProperties.getTls();
            TLSConfig tlsConfig = new TLSConfig(tls.getKeyStoreInstanceType(),
                    tls.getCertificatePath(), tls.getCertificatePassword(),
                    tls.getKeyStorePath(), tls.getKeyStorePassword());
            return new ConsulClient(agentHost, agentPort, tlsConfig);
        }
        return new ConsulClient(agentHost, agentPort);
    }


}
