/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;

import java.util.List;

public class MySqlSelectTest_313
        extends MysqlTest {

    public void test1() {
        String sql = "SELECT 1 FROM t1, t3 RIGHT JOIN t2 ON t2.b=t3.b ";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql);
        SQLStatement stmt = stmtList.get(0);

        System.out.println(stmt);
    }

    public void test2() {
        String sql = "select row(1,2,row(3,4)) IN (row(3,2,row(3,4)), row(1,2,row(3,NULL)));";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql);
        SQLStatement stmt = stmtList.get(0);

        System.out.println(stmt);
    }

    public void test3() {
        String sql = "replace into student(id,name,unit) value(?,?,?)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql);
        SQLStatement stmt = stmtList.get(0);

        System.out.println(stmt);
    }
}