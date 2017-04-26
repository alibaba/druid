package com.alibaba.druid.spring.boot.autoconfigure;

import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * The Druid stat view servlet configuration.
 *
 * @author lihengming< qq.com>
 * @see <a href="https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE_StatViewServlet%E9%85%8D%E7%BD%AE">StatViewServlet配置</a>
 */
@ConditionalOnProperty(name = "spring.datasource.druid.StatViewServlet.enabled", havingValue = "true", matchIfMissing = true)
public class DruidStatViewServletConfiguration {
    @Bean
    public ServletRegistrationBean servletRegistrationBean(DruidProperties properties) {
        DruidProperties.StatViewServlet config = properties.getStatViewServlet();
        ServletRegistrationBean registration = new ServletRegistrationBean();
        registration.setServlet(new StatViewServlet());
        registration.addUrlMappings(config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*");
        if (config.getAllow() != null) {
            registration.addInitParameter("allow", config.getAllow());
        }
        if (config.getDeny() != null) {
            registration.addInitParameter("deny", config.getDeny());
        }
        if (config.getLoginUsername() != null) {
            registration.addInitParameter("loginUsername", config.getLoginUsername());
        }
        if (config.getLoginPassword() != null) {
            registration.addInitParameter("loginPassword", config.getLoginPassword());
        }
        if (config.getResetEnable() != null) {
            registration.addInitParameter("resetEnable", config.getResetEnable());
        }
        return registration;
    }
}
