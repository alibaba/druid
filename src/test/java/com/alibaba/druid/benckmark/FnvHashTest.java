package com.alibaba.druid.benckmark;

import com.alibaba.druid.util.FnvHash;
import junit.framework.TestCase;

public class FnvHashTest extends TestCase {
    static String sql = "SELECT id, item_id, rule_id, tag_id, ext , gmt_create, gmt_modified FROM wukong_preview_item_tag WHERE item_id = ? AND rule_id = ?";
    static char[] chars = sql.toCharArray();

    public void test_perf_fnv() throws Exception {
        for (int i = 0; i < 5; ++i) {
//            perf_hashCode64(sql); // 168
            perf_hashCode64(chars); // 169
        }
    }

    public long perf_hashCode64(String sql) {
        long val = 0;
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            val = FnvHash.fnv1a_64(sql);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
        return val;
    }

    public long perf_hashCode64(char[] sql) {
        long val = 0;
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            val = FnvHash.fnv1a_64(sql);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
        return val;
    }

    public void test_fnv_hash_1a() throws Exception {
        assertEquals(FnvHash.fnv1a_64("bcd"), FnvHash.fnv1a_64("abcde", 1, 4));
    }
}
