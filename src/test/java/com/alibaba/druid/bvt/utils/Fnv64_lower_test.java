package com.alibaba.druid.bvt.utils;

import com.alibaba.druid.util.Utils;
import junit.framework.TestCase;

/**
 * Created by wenshao on 19/06/2017.
 */
public class Fnv64_lower_test extends TestCase {
    public void test_fnv_lower() throws Exception {
        assertEquals(Utils.fnv_64_lower("Id"), Utils.fnv_64_lower("ID"));
        assertEquals(Utils.fnv_64_lower("id"), Utils.fnv_64_lower("ID"));

        System.out.println(Long.toHexString(Utils.fnv_64("druid")));
    }
}
