package com.alibaba.druid.bvt.support.json;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.Utils;
import org.junit.jupiter.api.Test;

public class JSONParserTest5 {
    @Test
    public void test_parse() throws Exception {
        String text = Utils.readFromResource("bvt/x.json");
        JSONUtils.parse(text);
    }
}
