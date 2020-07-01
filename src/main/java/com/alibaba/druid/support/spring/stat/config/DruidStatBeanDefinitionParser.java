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
package com.alibaba.druid.support.spring.stat.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class DruidStatBeanDefinitionParser implements BeanDefinitionParser {

	public static final String STAT_ANNOTATION_PROCESSOR_BEAN_NAME = "com.alibaba.druid.support.spring.stat.annotation.internalStatAnnotationBeanPostProcessor";
	public static final String STAT_ANNOTATION_PROCESSOR_BEAN_CLASS = "com.alibaba.druid.support.spring.stat.annotation.StatAnnotationBeanPostProcessor";
	public static final String STAT_ANNOTATION_ADVICE_BEAN_NAME = "druid-stat-interceptor";
	public static final String STAT_ANNOTATION_ADVICE_BEAN_CLASS = "com.alibaba.druid.support.spring.stat.DruidStatInterceptor";
	
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);

		CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
		parserContext.pushContainingComponent(compDefinition);

		BeanDefinitionRegistry registry = parserContext.getRegistry();

		if (registry.containsBeanDefinition(STAT_ANNOTATION_PROCESSOR_BEAN_NAME)) {
			parserContext.getReaderContext().error(
					"Only one DruidStatBeanDefinitionParser may exist within the context.", source);
		} else {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder
					.genericBeanDefinition(STAT_ANNOTATION_PROCESSOR_BEAN_CLASS);
			builder.getRawBeanDefinition().setSource(source);
			registerComponent(parserContext, builder, STAT_ANNOTATION_PROCESSOR_BEAN_NAME);
		}
		
		if (!registry.containsBeanDefinition(STAT_ANNOTATION_ADVICE_BEAN_NAME)) {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder
					.genericBeanDefinition(STAT_ANNOTATION_ADVICE_BEAN_CLASS);
			builder.getRawBeanDefinition().setSource(source);
			registerComponent(parserContext, builder, STAT_ANNOTATION_ADVICE_BEAN_NAME);
		}
		
		parserContext.popAndRegisterContainingComponent();

		return null;
	}

	private static void registerComponent(ParserContext parserContext, BeanDefinitionBuilder builder,
			String beanName) {

		builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		parserContext.getRegistry().registerBeanDefinition(beanName, builder.getBeanDefinition());
		BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getBeanDefinition(), beanName);
		parserContext.registerComponent(new BeanComponentDefinition(holder));
	}

}