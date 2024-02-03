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
package com.alibaba.druid.filter;

import com.alibaba.druid.proxy.jdbc.DataSourceProxy;

import java.util.List;
import java.util.ServiceLoader;

/**
 * FilterChain Factory SPI.
 *
 *  通过实现此SPI来扩展默认能力并其配置在META-INF/services/com.alibaba.druid.filter.FilterChainFactory.
 *
 */
public interface FilterChainFactory {
    /**
     * 获取SPI实现类, 目的是功能扩展提供切换入点.
     */
    FilterChainFactory ME = ServiceLoader.load(FilterChainFactory.class).iterator().next();

    /**
     * 创建FilterChain.
     * @param dataSource - DataSourceProxy
     * @param filterList - Filter列表, 为null表示取默认的.
     * @param fromObj - 方调createFilterChain方法的来源对象, 用于实现时根据不同来源可按需实现不同的策略.
     * @return
     */
    FilterChain createFilterChain(DataSourceProxy dataSource, List<Filter> filterList, Object fromObj);
}
