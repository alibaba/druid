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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import junit.framework.TestCase;

public class MapComparatorTest extends TestCase {

    private String orderByKey = "orderby";

    public void test_comparator_date() throws Exception {
        test_comparator_date_0(true);
        test_comparator_date_0(false);
    }

    public void test_comparator_String() throws Exception {
        test_comparator_string_0(true);
        test_comparator_string_0(false);
    }

    public void test_comparator_number() throws Exception {
        test_comparator_number_0(true);
        test_comparator_number_0(false);
    }

    public void test_comparator_array() throws Exception {
        test_comparator_array_0(true);
        test_comparator_array_0(false);
    }

    private void test_comparator_array_0(boolean desc) throws Exception {
        String orderByKey = "orderby";
        long now = System.currentTimeMillis();
        MapComparator<String, Date[]> comparator = new MapComparator<String, Date[]>(orderByKey + "[0]", desc);

        Map<String, Date[]> map1 = new HashMap<String, Date[]>();
        Map<String, Date[]> map2 = new HashMap<String, Date[]>();

        map1.put(orderByKey, new Date[] { new Date(now) });
        map2.put(orderByKey, new Date[] { new Date(now - 1) });
        Assert.assertEquals(!desc, comparator.compare(map1, map2) > 0);

        map2.put(orderByKey, new Date[] { new Date(now) });
        Assert.assertEquals(true, comparator.compare(map1, map2) == 0);

        map2.put(orderByKey, new Date[] { new Date(now + 1) });
        Assert.assertEquals(!desc, comparator.compare(map1, map2) < 0);

        map2.put(orderByKey, null);
        Assert.assertEquals(!desc, comparator.compare(map1, map2) > 0);

        map1.put(orderByKey, null);
        map2.put(orderByKey, new Date[] { new Date(now) });
        Assert.assertEquals(!desc, comparator.compare(map1, map2) < 0);

        map1.put(orderByKey, null);
        map2.put(orderByKey, null);
        Assert.assertEquals(true, comparator.compare(map1, map2) == 0);

    }

    private void test_comparator_date_0(boolean desc) throws Exception {
        long now = System.currentTimeMillis();
        MapComparator<String, Date> comparator = new MapComparator<String, Date>(orderByKey, desc);

        Map<String, Date> map1 = new HashMap<String, Date>();
        Map<String, Date> map2 = new HashMap<String, Date>();

        map1.put(orderByKey, new Date(now));
        map2.put(orderByKey, new Date(now - 1));
        Assert.assertEquals(!desc, comparator.compare(map1, map2) > 0);

        map2.put(orderByKey, new Date(now));
        Assert.assertEquals(true, comparator.compare(map1, map2) == 0);

        map2.put(orderByKey, new Date(now + 1));
        Assert.assertEquals(!desc, comparator.compare(map1, map2) < 0);

        map2.put(orderByKey, null);
        Assert.assertEquals(!desc, comparator.compare(map1, map2) > 0);

        map1.put(orderByKey, null);
        map2.put(orderByKey, new Date(now));
        Assert.assertEquals(!desc, comparator.compare(map1, map2) < 0);

        map1.put(orderByKey, null);
        map2.put(orderByKey, null);
        Assert.assertEquals(true, comparator.compare(map1, map2) == 0);

    }

    private void test_comparator_string_0(boolean desc) throws Exception {
        MapComparator<String, String> comparator = new MapComparator<String, String>(orderByKey, desc);

        Map<String, String> map1 = new HashMap<String, String>();
        Map<String, String> map2 = new HashMap<String, String>();

        map1.put(orderByKey, "opq");
        map2.put(orderByKey, "xyz");
        Assert.assertEquals(desc, comparator.compare(map1, map2) > 0);

        map2.put(orderByKey, "opq");
        Assert.assertEquals(true, comparator.compare(map1, map2) == 0);

        map2.put(orderByKey, "abc");
        Assert.assertEquals(desc, comparator.compare(map1, map2) < 0);

        map2.put(orderByKey, null);
        Assert.assertEquals(!desc, comparator.compare(map1, map2) > 0);

        map1.put(orderByKey, null);
        map2.put(orderByKey, "opq");
        Assert.assertEquals(!desc, comparator.compare(map1, map2) < 0);

        map1.put(orderByKey, null);
        map2.put(orderByKey, null);
        Assert.assertEquals(true, comparator.compare(map1, map2) == 0);
    }

    private void test_comparator_number_0(boolean desc) throws Exception {
        MapComparator<String, Double> comparator = new MapComparator<String, Double>(orderByKey, desc);
        double baseNumber = 100.123;

        Map<String, Double> map1 = new HashMap<String, Double>();
        Map<String, Double> map2 = new HashMap<String, Double>();

        map1.put(orderByKey, baseNumber);
        map2.put(orderByKey, baseNumber - 1);
        Assert.assertEquals(!desc, comparator.compare(map1, map2) > 0);

        map2.put(orderByKey, baseNumber);
        Assert.assertEquals(true, comparator.compare(map1, map2) == 0);

        map2.put(orderByKey, baseNumber + 1);
        Assert.assertEquals(!desc, comparator.compare(map1, map2) < 0);

        map2.put(orderByKey, null);
        Assert.assertEquals(!desc, comparator.compare(map1, map2) > 0);

        map1.put(orderByKey, null);
        map2.put(orderByKey, baseNumber);
        Assert.assertEquals(!desc, comparator.compare(map1, map2) < 0);

        map1.put(orderByKey, null);
        map2.put(orderByKey, null);
        Assert.assertEquals(true, comparator.compare(map1, map2) == 0);
    }
}
