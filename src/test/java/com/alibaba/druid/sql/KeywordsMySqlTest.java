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
