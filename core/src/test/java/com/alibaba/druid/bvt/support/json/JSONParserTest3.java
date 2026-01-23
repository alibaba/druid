package com.alibaba.druid.bvt.support.json;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.Utils;
import junit.framework.TestCase;

import java.util.LinkedHashMap;
import java.util.Map;

public class JSONParserTest3 extends TestCase {
    public void test_parse() throws Exception {
        String text = Utils.readFromResource("bvt/sonar-sql.json");
        Map<String, Object> result = (Map<String, Object>) JSONUtils.parse(text);
        LinkedHashMap wallStats = (LinkedHashMap) result.get("Content");
        System.out.println(wallStats.get("blackList"));
    }
}
