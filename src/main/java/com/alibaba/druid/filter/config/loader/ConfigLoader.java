/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.filter.config.loader;

import java.util.Properties;

/**
 * <pre>
 * 配置文件装载器.
 * </pre>
 *
 * @see com.alibaba.druid.filter.config.ConfigFilter
 * @see ConfigLoaderFactory
 *
 * @author Jonas Yang
 */
public interface ConfigLoader {

    /**
     * 唯一的ID
     * @return
     */
    public String getId();

    /**
     * 获得配置内容, 如果没有, 返回<code>null</code>.
     * @param protocol
     * @return
     */
    public Properties loadConfig(String protocol);

    /**
     * 是否支持该协议, 如果支持, 将会使用该装载器获取配置文件
     * @param protocol
     * @return
     */
    public boolean isSupported(String protocol);
}
