package com.alibaba.druid.bvt.support.json;

import com.alibaba.druid.support.json.JSONUtils;
import junit.framework.TestCase;

public class JSONParserTest4 extends TestCase {
    
    public void test_parse() throws Exception {
        String text ="{\"\\u0006\":\"123\"}";
        System.out.println(JSONUtils.parse(text));
    }
}
