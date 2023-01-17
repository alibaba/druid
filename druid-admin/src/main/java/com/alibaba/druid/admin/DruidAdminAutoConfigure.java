package com.alibaba.druid.admin;

import com.alibaba.druid.admin.config.MonitorProperties;
import com.alibaba.druid.admin.servlet.MonitorViewServlet;
import com.alibaba.druid.util.StringUtils;
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
        registrationBean.addUrlMappings(getUrlMappings(properties));
        if (properties.getLoginUsername() != null) {
            registrationBean.addInitParameter("loginUsername", properties.getLoginUsername());
        }
        if (properties.getLoginPassword() != null) {
            registrationBean.addInitParameter("loginPassword", properties.getLoginPassword());
        }
        return registrationBean;
    }


    private String getUrlMappings(MonitorProperties properties) {
        String urlMapping = "/druid/*";
        if(StringUtils.isEmpty(properties.getContextPath())) {
            return urlMapping;
        }

        if(!properties.getContextPath().startsWith("/") || properties.getContextPath().endsWith("/")) {
            throw new IllegalArgumentException("Druid ContextPath must start with '/' and not end with '/'");
        }
        return properties.getContextPath() + "/*";
    }

}
