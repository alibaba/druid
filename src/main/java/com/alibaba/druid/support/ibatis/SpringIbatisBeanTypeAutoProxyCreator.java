/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.PatternMatchUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类BeanTypeAutoProxyCreator.java的实现描述：使用配置类型代替Springframework中配置名称的实现
 * 
 * @author hualiang.lihl 2011-12-31 上午10:48:20
 */
@SuppressWarnings("deprecation")
public class SpringIbatisBeanTypeAutoProxyCreator extends AbstractAutoProxyCreator implements SpringIbatisBeanTypeAutoProxyCreatorMBean {

    private final static Log   LOG              = LogFactory.getLog(SpringIbatisBeanTypeAutoProxyCreator.class);

    private static final long  serialVersionUID = -9094985530794052264L;

    private List<String>       beanNames        = new ArrayList<String>();
    private final List<String> proxyBeanNames   = new ArrayList<String>();

    /**
     * Identify as bean to proxy if the bean name is in the configured list of names.
     */
    @SuppressWarnings("rawtypes")
    protected Object[] getAdvicesAndAdvisorsForBean(Class beanClass, String beanName, TargetSource targetSource) {
        for (String mappedName : this.beanNames) {
            if (FactoryBean.class.isAssignableFrom(beanClass)) {
                if (!mappedName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
                    continue;
                }
                mappedName = mappedName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
            }
            if (isMatch(beanName, mappedName)) {
                return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
            }
        }
        return DO_NOT_PROXY;
    }

    @SuppressWarnings({ "rawtypes" })
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

    /**
     * Return if the given bean name matches the mapped name.
     * <p>
     * The default implementation checks for "xxx*", "*xxx" and "*xxx*" matches, as well as direct equality. Can be
     * overridden in subclasses.
     * 
     * @param beanName the bean name to check
     * @param mappedName the name in the configured list of names
     * @return if the names match
     * @see org.springframework.util.PatternMatchUtils#simpleMatch(String, String)
     */
    protected boolean isMatch(String beanName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, beanName);
    }

    public List<String> getBeanNames() {
        return beanNames;
    }

    public List<String> getProxyBeanNames() {
        return proxyBeanNames;
    }

}
