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
package com.alibaba.druid.bvt.bug;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;

public class Bug_for_weizhi extends TestCase {

    public void test_for_issue() throws Exception {
        String sql = "insert into aaa values(1,2,'这是个反斜杠\\\\');";

        String expected = "INSERT INTO aaa\nVALUES (1, 2, '这是个反斜杠\\\\');";

        Assert.assertEquals(expected, SQLUtils.formatMySql(sql));
    }
}
