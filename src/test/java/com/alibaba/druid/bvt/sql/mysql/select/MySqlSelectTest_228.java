/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.List;


public class MySqlSelectTest_228 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT *\n" +
                "FROM (\n" +
                "  SELECT pk AS pk, ? AS 1024, varchar_test AS varchar_test\n" +
                "  FROM select_base_one_one_db_one_tb\n" +
                "  ORDER BY pk\n" +
                "  LIMIT ?\n" +
                ") x";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT pk AS pk, ? AS \"1024\", varchar_test AS varchar_test\n" +
                "\tFROM select_base_one_one_db_one_tb\n" +
                "\tORDER BY pk\n" +
                "\tLIMIT ?\n" +
                ") x", stmt.toString());
    }

    public void test_genAlias() throws Exception {
        String sql = "select pk, '1024', varchar_test from select_base_one_one_db_one_tb order by pk limit 10";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql, SQLParserFeature.SelectItemGenerateAlias);
        assertEquals("SELECT pk, '1024', varchar_test\n" +
                "FROM select_base_one_one_db_one_tb\n" +
                "ORDER BY pk\n" +
                "LIMIT 10", stmt.toString());
    }

    public void test_genAlias2() throws Exception {
        String sql = "SELECT json_size('{\"x\": {\"a\": 1, \"b\": 2}}', '$.x')\n";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql, SQLParserFeature.SelectItemGenerateAlias);
        assertEquals("SELECT json_size('{\"x\": {\"a\": 1, \"b\": 2}}', '$.x') AS `json_size('{\"x\": {\"a\": 1, \"b\": 2}}', '$.x')`", stmt.toString());
    }

    public void test_genAlias3() throws Exception {
        String sql = "select cardinality(regexp_extract_all('test', '\"meid\":\"'));";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql, SQLParserFeature.SelectItemGenerateAlias);

        assertEquals("SELECT cardinality(regexp_extract_all('test', '\"meid\":\"')) AS `cardinality(regexp_extract_all('test', '\"meid\":\"'))`;", stmt.toString());
    }

}