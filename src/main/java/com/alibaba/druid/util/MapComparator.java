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
package com.alibaba.druid.util;

import java.lang.reflect.Array;
import java.text.Collator;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

/**
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class MapComparator<K extends Object, V extends Object> implements Comparator<Map<K, V>> {

    private boolean isDesc;
    private K       orderByKey;

    public MapComparator(K orderByKey, boolean isDesc){
        this.orderByKey = orderByKey;
        this.isDesc = isDesc;
    }

    private int compare(Number o1, Number o2) {
        return (int) (o1.doubleValue() - o2.doubleValue());
    }

    private int compare(String o1, String o2) {
        return Collator.getInstance().compare(o1, o2);
    }

    private int compare(Date o1, Date o2) {
        return (int) (o1.getTime() - o2.getTime());
    }

    @Override
    public int compare(Map<K, V> o1, Map<K, V> o2) {
        int result = compare_0(o1, o2);

        if (isDesc) {
            result = -result;
        }

        return result;
    }

    private Object getValueByKey(Map<K, V> map, K key) {
        if (key instanceof String) {
            String keyStr = (String) key;

            if (keyStr.matches(".+\\[[0-9]+\\]")) {
                Object value = map.get(keyStr.substring(0, keyStr.indexOf('[')));
                if (value == null) {
                    return null;
                }

                Integer index = StringUtils.subStringToInteger(keyStr, "[", "]");
                if (value.getClass().isArray() && Array.getLength(value) >= index) {
                    return Array.get(value, index);
                }

                return null;
            }
        }
        return map.get(key);
    }

    private int compare_0(Map<K, V> o1, Map<K, V> o2) {
        Object v1 = getValueByKey(o1, orderByKey);
        Object v2 = getValueByKey(o2, orderByKey);

        if (v1 == null && v2 == null) {
            return 0;
        }
        if (v1 == null) {
            return -1;
        }
        if (v2 == null) {
            return 1;
        }

        if (v1 instanceof Number) {
            return compare((Number) v1, (Number) v2);
        }

        if (v1 instanceof String) {
            return compare((String) v1, (String) v2);
        }

        if (v1 instanceof Date) {
            return compare((Date) v1, (Date) v2);
        }

        return 0;
    }
}
