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
package com.alibaba.druid.bvt.support.json;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.support.json.JSONParser;

public class JSONParserTest extends TestCase {

    public void test_parse() throws Exception {
        JSONParser parser = new JSONParser("{ \"id\":33,\"name\":\"jobs\",\"values\":[1,2,3,4], \"f1\":true, \"f2\":false,\"f3\":-234,\"f4\":3.5}");
        Map<String, Object> map = (Map<String, Object>) parser.parse();
        Assert.assertEquals(33, map.get("id"));
        Assert.assertEquals("jobs", map.get("name"));
        Assert.assertEquals(4, ((List) map.get("values")).size());
        Assert.assertEquals(1, ((List) map.get("values")).get(0));
        Assert.assertEquals(2, ((List) map.get("values")).get(1));
        Assert.assertEquals(3, ((List) map.get("values")).get(2));
        Assert.assertEquals(4, ((List) map.get("values")).get(3));
        Assert.assertEquals(true, map.get("f1"));
        Assert.assertEquals(false, map.get("f2"));
        Assert.assertEquals(-234, map.get("f3"));
        Assert.assertEquals(3.5D, map.get("f4"));
    }
}
