package com.alibaba.druid.admin;

import com.alibaba.druid.admin.config.MonitorProperties;
import com.alibaba.druid.admin.servlet.MonitorViewServlet;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MonitorProperties.class)
public class DruidAdminAutoConfigure {

    @Bean
    public ServletRegistrationBean statViewServletRegistrationBean(MonitorProperties properties) {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(new MonitorViewServlet());
        registrationBean.addUrlMappings("/druid/*");
        if (properties.getLoginUsername() != null) {
            registrationBean.addInitParameter("loginUsername", properties.getLoginUsername());
        }
        if (properties.getLoginPassword() != null) {
            registrationBean.addInitParameter("loginPassword", properties.getLoginPassword());
        }
        return registrationBean;
    }
}
