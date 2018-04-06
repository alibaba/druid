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
package com.alibaba.druid.support.ibatis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;

@SuppressWarnings({ "serial", "deprecation" })
public class SpringIbatisBeanNameAutoProxyCreator extends BeanNameAutoProxyCreator implements SpringIbatisBeanNameAutoProxyCreatorMBean {

    private final static Log LOG            = LogFactory.getLog(SpringIbatisBeanNameAutoProxyCreator.class);

    private List<String>     proxyBeanNames = new ArrayList<String>();

    public List<String> getProxyBeanNames() {
        return proxyBeanNames;
    }

    public void setProxyBeanNames(List<String> proxyBeanNames) {
        this.proxyBeanNames = proxyBeanNames;
    }

    @SuppressWarnings("rawtypes")
    protected Object createProxy(Class beanClass, String beanName, Object[] specificInterceptors,
                                 TargetSource targetSource) {
        try {
            Object target = targetSource.getTarget();

            if (target instanceof SqlMapClientWrapper) {
                proxyBeanNames.add(beanName);
                return target;
            }

            if (target instanceof SqlMapClient) {
                proxyBeanNames.add(beanName);

                return new SqlMapClientWrapper((ExtendedSqlMapClient) target);
            }

            return super.createProxy(beanClass, beanName, specificInterceptors, targetSource);
        } catch (Throwable ex) {
            LOG.error(ex.getMessage(), ex);
            return super.createProxy(beanClass, beanName, specificInterceptors, targetSource);
        }
    }

}
