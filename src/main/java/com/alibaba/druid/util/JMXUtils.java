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
package com.alibaba.druid.util;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public final class JMXUtils {

    public static ObjectName register(String name, Object mbean) {
        try {
            ObjectName objectName = new ObjectName(name);

            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

            try {
                mbeanServer.registerMBean(mbean, objectName);
            } catch (InstanceAlreadyExistsException ex) {
                mbeanServer.unregisterMBean(objectName);
                mbeanServer.registerMBean(mbean, objectName);
            }

            return objectName;
        } catch (JMException e) {
            throw new IllegalArgumentException(name, e);
        }
    }

    public static void unregister(String name) {
        try {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

            mbeanServer.unregisterMBean(new ObjectName(name));
        } catch (JMException e) {
            throw new IllegalArgumentException(name, e);
        }

    }

    private static final String[]      THROWABLE_COMPOSITE_INDEX_NAMES        = { "message", "class", "stackTrace" };
    private static final String[]      THROWABLE_COMPOSITE_INDEX_DESCRIPTIONS = { "message", "class", "stackTrace" };
    private static final OpenType<?>[] THROWABLE_COMPOSITE_INDEX_TYPES        = new OpenType<?>[] { SimpleType.STRING,
            SimpleType.STRING, SimpleType.STRING                             };

    private static CompositeType       THROWABLE_COMPOSITE_TYPE               = null;

    public static CompositeType getThrowableCompositeType() throws JMException {
        if (THROWABLE_COMPOSITE_TYPE == null) {
            THROWABLE_COMPOSITE_TYPE = new CompositeType("Throwable", "Throwable", THROWABLE_COMPOSITE_INDEX_NAMES,
                                                         THROWABLE_COMPOSITE_INDEX_DESCRIPTIONS,
                                                         THROWABLE_COMPOSITE_INDEX_TYPES);
        }

        return THROWABLE_COMPOSITE_TYPE;
    }

    public static CompositeData getErrorCompositeData(Throwable error) throws JMException {
        if (error == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("class", error.getClass().getName());
        map.put("message", error.getMessage());

        map.put("stackTrace", Utils.getStackTrace(error));

        return new CompositeDataSupport(getThrowableCompositeType(), map);
    }
}
