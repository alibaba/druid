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
package com.alibaba.druid;

import junit.framework.TestCase;

import com.alibaba.druid.support.json.JSONUtils;


public class Bug_for_alibank extends TestCase {
    public void test_bug() throws Exception {
        String jsonStrz = "{\"addContact\":[{\"address\":\"=\\\\\\\\\\\'\'\\&quot;);|]*{%0d%0a&lt;%00\"}]}";
        System.out.println(jsonStrz);
        Object o = JSONUtils.parse(jsonStrz.replaceAll("\\\\", ""));
        System.out.println(JSONUtils.toJSONString(o));
        System.out.println(System.getProperty("java.vendor"));
    }
}
