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
package com.alibaba.druid.support.spring.stat.annotation;

import javax.annotation.Resource;

import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;

@SuppressWarnings("serial")
public class StatAnnotationBeanPostProcessor extends AbstractAdvisingBeanPostProcessor implements BeanFactoryAware {

	@Resource(name="druid-stat-interceptor")
	private DruidStatInterceptor druidStatInterceptor;
	
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		setBeforeExistingAdvisors(true);
		StatAnnotationAdvisor advisor = new StatAnnotationAdvisor(druidStatInterceptor);
		advisor.setBeanFactory(beanFactory);
		this.advisor = advisor;
	}

}
