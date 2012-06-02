package com.alibaba.druid.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerLexer;
import com.alibaba.druid.sql.parser.Keywords;

public class KeywordsTest extends TestCase {

    public void test_sort() throws Exception {
        List<String> list = new ArrayList<String>(SQLServerLexer.DEFAULT_SQL_SERVER_KEYWORDS.getKeywords().keySet());

        Collections.sort(list);

        int i = 0;
        for (String item : list) {
            if (Keywords.DEFAULT_KEYWORDS.getKeywords().containsKey(item)) {
                continue;
            }
            if (i % 5 == 0) {
                System.out.println();
            }
            System.out.println("map.put(\"" + item + "\", Token." + item + ");");
            ++i;
        }
    }
}
