/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.performance;

import java.text.NumberFormat;

import junit.framework.TestCase;

import com.alibaba.druid.sql.parser.SQLStatementParser;

public class TestSelectPerformance extends TestCase {

    private final int COUNT = 1000 * 1;
    private String    sql   = "SELECT F100, F101, F102, F103, F104 FROM T_001 WHERE F100 = ?";

    public void test_simple() throws Exception {
        for (int i = 0; i < 1; ++i) {
            f();
        }
    }

    private void f() throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; ++i) {
            new SQLStatementParser(sql).parseStatementList();
            // stmtList.toString();
        }
        long time = System.currentTimeMillis() - start;
        System.out.println(NumberFormat.getInstance().format(time));
    }
}
