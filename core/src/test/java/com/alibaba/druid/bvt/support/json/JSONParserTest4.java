package com.alibaba.druid.bvt.support.json;

import com.alibaba.druid.support.json.JSONUtils;
import org.junit.jupiter.api.Test;

public class JSONParserTest4 {
    @Test
    public void test_parse() throws Exception {
        String text = "{\"\\u0006\":\"123\"}";
        System.out.println(JSONUtils.parse(text));
    }
}
