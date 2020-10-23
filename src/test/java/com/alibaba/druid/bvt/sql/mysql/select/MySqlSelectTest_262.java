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
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;


public class MySqlSelectTest_262 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT   s.acctbal,\n" +
                "         s.name,\n" +
                "         n.name,\n" +
                "         p.partkey,\n" +
                "         p.mfgr\n" +
                "FROM     part p,\n" +
                "         supplier s,\n" +
                "         partsupp ps,\n" +
                "         nation n,\n" +
                "         region r\n" +
                "WHERE    p.partkey = ps.partkey/*+dynamicFilter=true*/AND      s.suppkey = ps.suppkey\n" +
                "AND      p.size = 35\n" +
                "AND      s.nationkey = n.nationkey/*+dynamicFilter=true*/AND      n.regionkey = r.regionkey\n" +
                "ORDER BY s.acctbal DESC,\n" +
                "         n.name,\n" +
                "         s.name,\n" +
                "         p.partkey\n" +
                "LIMIT    100";

        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.KeepSourceLocation, SQLParserFeature.EnableSQLBinaryOpExprGroup);

        assertEquals("SELECT s.acctbal, s.name, n.name, p.partkey, p.mfgr\n" +
                "FROM part p, supplier s, partsupp ps, nation n, region r\n" +
                "WHERE p.partkey = ps.partkey/*+dynamicFilter=true*/\n" +
                "\tAND s.suppkey = ps.suppkey\n" +
                "\tAND p.size = 35\n" +
                "\tAND s.nationkey = n.nationkey/*+dynamicFilter=true*/\n" +
                "\tAND n.regionkey = r.regionkey\n" +
                "ORDER BY s.acctbal DESC, n.name, s.name, p.partkey\n" +
                "LIMIT 100", stmt.toString());
    }


}