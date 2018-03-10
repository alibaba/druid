/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.spring.boot.autoconfigure.stat;

import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * @author lihengming [89921218@qq.com]
 */
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "spring.datasource.druid.web-stat-filter.enabled", havingValue = "true", matchIfMissing = true)
public class DruidWebStatFilterConfiguration {
    @Bean
    public FilterRegistrationBean webStatFilterRegistrationBean(DruidStatProperties properties) {
        DruidStatProperties.WebStatFilter config = properties.getWebStatFilter();
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        WebStatFilter filter = new WebStatFilter();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(config.getUrlPattern() != null ? config.getUrlPattern() : "/*");
        registrationBean.addInitParameter("exclusions", config.getExclusions() != null ? config.getExclusions() : "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        if (config.getSessionStatEnable() != null) {
            registrationBean.addInitParameter("sessionStatEnable", config.getSessionStatEnable());
        }
        if (config.getSessionStatMaxCount() != null) {
            registrationBean.addInitParameter("sessionStatMaxCount", config.getSessionStatMaxCount());
        }
        if (config.getPrincipalSessionName() != null) {
            registrationBean.addInitParameter("principalSessionName", config.getPrincipalSessionName());
        }
        if (config.getPrincipalCookieName() != null) {
            registrationBean.addInitParameter("principalCookieName", config.getPrincipalCookieName());
        }
        if (config.getProfileEnable() != null) {
            registrationBean.addInitParameter("profileEnable", config.getProfileEnable());
        }
        return registrationBean;
    }
}
