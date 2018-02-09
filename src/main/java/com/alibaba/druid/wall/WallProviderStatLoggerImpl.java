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
package com.alibaba.druid.wall;

import java.util.Map;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSourceStatLoggerImpl;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class WallProviderStatLoggerImpl extends WallProviderStatLoggerAdapter implements WallProviderStatLogger {

    private static Log LOG    = LogFactory.getLog(WallProviderStatLoggerImpl.class);

    private Log        logger = LOG;

    @Override
    public void configFromProperties(Properties properties) {
        String property = properties.getProperty("druid.stat.loggerName");
        if (property != null && property.length() > 0) {
            setLoggerName(property);
        }
    }

    public void setLoggerName(String loggerName) {
        logger = LogFactory.getLog(loggerName);
    }

    public void setLogger(Log logger) {
        if (logger == null) {
            throw new IllegalArgumentException("logger can not be null");
        }
        this.logger = logger;
    }

    public boolean isLogEnable() {
        return logger.isInfoEnabled();
    }

    public void log(String value) {
        logger.info(value);
    }

    @Override
    public void log(WallProviderStatValue statValue) {
        if (!isLogEnable()) {
            return;
        }
        
        Map<String, Object> map = statValue.toMap();
        String text = JSONUtils.toJSONString(map);

        log(text);
    }
}
