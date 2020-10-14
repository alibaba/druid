package com.alibaba.druid.admin;

import com.alibaba.druid.admin.config.MonitorProperties;
import com.alibaba.druid.admin.servlet.MonitorViewServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(MonitorProperties.class)
public class DruidAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(DruidAdminApplication.class, args);
    }


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
