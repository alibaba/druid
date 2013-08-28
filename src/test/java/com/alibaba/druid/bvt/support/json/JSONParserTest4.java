package com.alibaba.druid.bvt.support.json;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.Utils;

public class JSONParserTest4 extends TestCase {
    public void test_parse() throws Exception {
        String text ="{\"\\u0006\":\"123\"}";
      JSONUtils.parse(text);

    }
}
