package com.alibaba.druid.bvt.support.json;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.Utils;
import junit.framework.TestCase;

public class JSONParserTest5 extends TestCase {
    public void test_parse() throws Exception {
        String text = Utils.readFromResource("bvt/x.json");
        JSONUtils.parse(text);
    }
}
