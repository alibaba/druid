package com.alibaba.druid;

import junit.framework.TestCase;

import com.alibaba.fastjson.JSON;


public class Bug_for_alibank extends TestCase {
    public void test_bug() throws Exception {
        String jsonStrz = "{addContact:[{\"address\":\"=\\\\\\\\\\\'\'\\&quot;);|]*{%0d%0a&lt;%00\"}]}";
        System.out.println(jsonStrz);
        Object o = JSON.parseObject(jsonStrz.replaceAll("\\\\", ""));
        System.out.println(JSON.toJSONString(o));
    }
}
