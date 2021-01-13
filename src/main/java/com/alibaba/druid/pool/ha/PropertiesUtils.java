/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.pool.ha;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Utilities for Properties.
 *
 * @author DigitalSonic
 */
public class PropertiesUtils {
    private final static Log LOG = LogFactory.getLog(PropertiesUtils.class);

    /**
     * Load properties from the given file into Properties.
     */
    public static Properties loadProperties(String file) {
        Properties properties = new Properties();
        if (file == null) {
            return properties;
        }

        InputStream is = null;
        try {
            LOG.debug("Trying to load " + file + " from FileSystem.");
            is = new FileInputStream(file);
        } catch(FileNotFoundException e) {
            LOG.debug("Trying to load " + file + " from Classpath.");
            try {
                is = PropertiesUtils.class.getResourceAsStream(file);
            } catch (Exception ex) {
                LOG.warn("Can not load resource " + file, ex);
            }
        }
        if (is != null) {
            try {
                properties.load(is);
            } catch(Exception e) {
                LOG.error("Exception occurred while loading " + file, e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                        LOG.debug("Can not close Inputstream.", e);
                    }
                }
            }
        } else {
            LOG.warn("File " + file + " can't be loaded!");
        }

        return properties;
    }

    /**
     * Pick the name of a JDBC url. Such as xxx.url, xxx is the name.
     */
    public static List<String> loadNameList(Properties properties, String propertyPrefix) {
        List<String> nameList = new ArrayList<String>();

        Set<String> names = new HashSet<String>();
        for (String n : properties.stringPropertyNames()) {
            if (propertyPrefix != null && !propertyPrefix.isEmpty()
                    && !n.startsWith(propertyPrefix)) {
                continue;
            }
            if (n.endsWith(".url")) {
                names.add(n.split("\\.url")[0]);
            }
        }
        if (!names.isEmpty()) {
            nameList.addAll(names);
        }
        return nameList;
    }

    public static Properties filterPrefix(Properties properties, String prefix) {
        if (properties == null || prefix == null || prefix.isEmpty()) {
            return properties;
        }
        Properties result = new Properties();
        for (String n : properties.stringPropertyNames()) {
            if (n.startsWith(prefix)) {
                result.setProperty(n, properties.getProperty(n));
            }
        }
        return result;
    }
}
