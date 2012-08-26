package com.alibaba.druid.bvt.support.json;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.IOUtils;

public class JSONParserTest2 extends TestCase {
    public void test_parse() throws Exception {
        String text = IOUtils.readFromResource("bvt/sql.json");
        Map<String, Object> result = (Map<String, Object>)JSONUtils.parse(text);
        List<Map<String, Object>> sqlList = (List<Map<String, Object>>) result.get("Content");
        Assert.assertEquals(82, sqlList.size());
    }
}
