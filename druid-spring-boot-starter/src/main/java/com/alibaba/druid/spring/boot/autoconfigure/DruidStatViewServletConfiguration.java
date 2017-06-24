/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.spring.boot.autoconfigure;

import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * @author lihengming [89921218@qq.com]
 */
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "spring.datasource.druid.StatViewServlet.enabled", havingValue = "true", matchIfMissing = true)
public class DruidStatViewServletConfiguration {
    @Bean
    public ServletRegistrationBean servletRegistrationBean(DruidStatProperties properties) {
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
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
