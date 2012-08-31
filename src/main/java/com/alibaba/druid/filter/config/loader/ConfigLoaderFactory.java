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

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 * 如果有扩展的装载器.
 * 可以放到classes目录下的druid-config-loader.properties
 *
 * druid-config-loader.properties 内容如下:
 * ConfigLoader.x=com.xx.xxx.xx.MyConfigLoader
 *
 * </pre>
 * @author Jonas Yang
 */
public class ConfigLoaderFactory {

    private static Log log = LogFactory.getLog(ConfigLoaderFactory.class);

    private static ConcurrentHashMap<String, ConfigLoader> configLoaders = new ConcurrentHashMap<String, ConfigLoader>();

    static {
        //加载默认的装载器
        initConfigLoader("META-INF/druid-config-loader-default.properties", ConfigLoaderFactory.class.getClassLoader());

        //加载扩展的装载器
        initConfigLoader("druid-config-loader.properties", ClassLoader.getSystemClassLoader());

        if (ClassLoader.getSystemClassLoader() != Thread.currentThread().getContextClassLoader()) {
            initConfigLoader("ConfigLoader.properties", Thread.currentThread().getContextClassLoader());
        }
    }

    private ConfigLoaderFactory() {}

    static void initConfigLoader(String resource, ClassLoader classLoader) {
        if (resource == null) {
            return;
        }

        InputStream inStream = null;

        try {
            inStream = classLoader.getResourceAsStream(resource);
            if (inStream == null) {
                return;
            }

            Properties p = new Properties();
            p.load(inStream);

            for (String key : p.stringPropertyNames()) {
                String clazzName = p.getProperty(key);
                System.out.println(clazzName);
                Class<?> clazz = classLoader.loadClass(clazzName);
                ConfigLoader configLoader = (ConfigLoader) clazz.newInstance();

                addConfigLoader(configLoader);
            }
        } catch (Exception e) {
            log.warn("Fail to init config loaders.", e);
        } finally {
            JdbcUtils.close(inStream);
        }
    }

    /**
     * 通过 protocol 获得装载器. 如果没有返回 <code>null</code>.
     * @param protocol
     * @return 如果没有找到返回<code>null</code>
     */
    public static ConfigLoader getConfigLoader(String protocol) {
        for (ConfigLoader configLoader : configLoaders.values()) {
            if (configLoader.isSupported(protocol)) {
                return configLoader;
            }
        }

       return null;
    }

    public static void addConfigLoader(ConfigLoader configLoader) {
        if (configLoaders.putIfAbsent(configLoader.getId(), configLoader) != null) {
            log.warn("Duplicate config loader [" + configLoader.getId() + "]");
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Add config loader [" + configLoader.getId() + " = " + configLoader.getClass() + "].");
            }
        }
    }

    public static ConfigLoader[] getConfigLoaders() {
        return configLoaders.values().toArray(new ConfigLoader[0]);
    }

}
