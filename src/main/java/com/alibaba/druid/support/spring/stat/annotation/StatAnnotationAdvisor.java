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
package com.alibaba.druid.support.spring.stat.annotation;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;

@SuppressWarnings("serial")
public class StatAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

	private Advice advice;
	private Pointcut pointcut;
	private DruidStatInterceptor druidStatInterceptor;
	
	public StatAnnotationAdvisor(DruidStatInterceptor druidStatInterceptor) {
		this.druidStatInterceptor = druidStatInterceptor;
		this.advice = buildAdvice();
		this.pointcut = buildPointcut();
	}

	public Pointcut getPointcut() {
		return this.pointcut;
	}

	public Advice getAdvice() {
		return this.advice;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (this.advice instanceof BeanFactoryAware) {
			((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
		}
	}
	
	protected Advice buildAdvice() {
		return druidStatInterceptor;
	}

	protected Pointcut buildPointcut() {
		Pointcut cpc = new AnnotationMatchingPointcut(Stat.class, true);
		Pointcut mpc = AnnotationMatchingPointcut.forMethodAnnotation(Stat.class);
		
		ComposablePointcut result = new ComposablePointcut(cpc).union(mpc);
		
		return result;
	}

}
