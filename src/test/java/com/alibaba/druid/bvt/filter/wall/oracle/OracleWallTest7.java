/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.filter.wall.oracle;

import com.alibaba.druid.wall.WallUtils;
import junit.framework.TestCase;
import org.junit.Assert;

public class OracleWallTest7 extends TestCase {

    public void test_true() throws Exception {
        String sql = //
        "begin\n"
                + "end";
        Assert.assertTrue(WallUtils.isValidateOracle(sql));
    }

    public void test_insert_all() throws Exception {
        String sql = //
        "INSERT ALL\n" +
                "  INTO mytable (column1, column2, column_n) VALUES (expr1, expr2, expr_n)\n" +
                "  INTO mytable (column1, column2, column_n) VALUES (expr1, expr2, expr_n)\n" +
                "  INTO mytable (column1, column2, column_n) VALUES (expr1, expr2, expr_n)\n" +
                "SELECT * FROM dual;";

        assertTrue(WallUtils.isValidateOracle(sql));
    }
}
