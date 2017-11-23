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
package com.alibaba.druid.pool;

import java.util.Properties;

import com.alibaba.druid.support.logging.Log;

/**
 * @author wenshao [szujobs@hotmail.com]
 * @since 0.2.19
 */
public interface DruidDataSourceStatLogger {

    void log(DruidDataSourceStatValue statValue);

    /**
     * @param properties
     * @since 0.2.21
     */
    void configFromProperties(Properties properties);
    
    void setLogger(Log logger);
    
    void setLoggerName(String loggerName);
}
