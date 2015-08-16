/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
