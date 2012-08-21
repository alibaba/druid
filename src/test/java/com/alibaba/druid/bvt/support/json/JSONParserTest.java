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
