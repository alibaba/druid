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
package com.alibaba.druid.support.spring.boot.autoconfigure;

import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.RegexpMethodPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * The Druid spring aop configuration.
 *
 * @author lihengming<89921218@qq.com>
 * @see <a href="https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE_Druid%E5%92%8CSpring%E5%85%B3%E8%81%94%E7%9B%91%E6%8E%A7%E9%85%8D%E7%BD%AE">Druid和Spring关联监控配置</a>
 */
@ConditionalOnProperty("spring.datasource.druid.aop-patterns")
public class DruidSpringAopConfiguration {
    @Value("${spring.aop.proxy-target-class:false}")
    private boolean proxyTargetClass;

    @Bean
    public Advice advice() {
        return new DruidStatInterceptor();
    }

    @Bean
    public Advisor advisor(DruidStatProperties properties) {
        return new RegexpMethodPointcutAdvisor(properties.getAopPatterns(), advice());
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(proxyTargetClass);
        return advisorAutoProxyCreator;
    }
}
