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
package com.alibaba.druid;

import java.util.LinkedHashMap;

import junit.framework.TestCase;

public class TestLRU extends TestCase {

    public void test_lru() throws Exception {
        LinkedHashMap<Integer, Object> cache = new LinkedHashMap<Integer, Object>(100, 0.75f, true);

        cache.put(2, "22");
        cache.put(3, "33");

        System.out.println(cache);

        cache.put(2, "22");

        System.out.println(cache);
        cache.get(3);
        System.out.println(cache);

    }
}
