package com.alibaba.druid.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.sql.parser.Keywords;

public class KeywordsMySqlTest extends TestCase {

    public void test_sort() throws Exception {
        List<String> list = new ArrayList<String>(Keywords.DEFAULT_KEYWORDS.getKeywords().keySet());

        Collections.sort(list);

        for (int i = 0; i < list.size(); ++i) {
            if (i % 5 == 0) {
                System.out.println();
            }
            String item = list.get(i);
            System.out.println("map.put(\"" + item + "\", Token." + item + ");");
            // map.put("AS", Token.AS);
        }
    }
}
