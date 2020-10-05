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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.stat.TableStat;

public class MySqlSelectTest_ads_keywords_1 extends MysqlTest {
    String[] keywords = new String[] {
            "any",
            "begin",
            "cast",
            "compute",
            "escape",
            "except",
            "full",
            "identified",
            "intersect",
            "merge",
            "minus",
            "open",
            "some",
            "truncate",
            "until",
            "view",
            "lable",
            "status",
            "option",
            "restrict",
            "connection",
            "character",
            "offset"
    };

    public void test_create() throws Exception {
        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];

            String sql = "create table t (" + keyword + " bigint)";

            try {
                SQLUtils.parseSingleMysqlStatement(sql);
            } catch (ParserException ex) {
                System.out.println(keyword);
                throw ex;
            }
        }
    }

    public void test_select() throws Exception {
        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];

            String sql = "select " + keyword + " from t where label = 1";

            try {
                SQLUtils.parseSingleMysqlStatement(sql);
            } catch (ParserException ex) {
                System.out.println(keyword);
                throw ex;
            }
        }
    }

    public void test_select_alais() throws Exception {
        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];

            String sql = "select " + keyword + " " + keyword + " from t where label = 1";

            try {
                SQLUtils.parseSingleMysqlStatement(sql);
            } catch (ParserException ex) {
                System.out.println(keyword);
                throw ex;
            }
        }
    }

    public void test_select_alais_2() throws Exception {
        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];

            String sql = "select " + keyword + " AS " + keyword + " from t where label = 1";

            try {
                SQLUtils.parseSingleMysqlStatement(sql);
            } catch (ParserException ex) {
                System.out.println(keyword);
                throw ex;
            }
        }
    }

    public void test_where() throws Exception {
        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];

            String sql = "select 1 from t where " + keyword + " = 1";

            try {
                SQLUtils.parseSingleMysqlStatement(sql);
            } catch (ParserException ex) {
                System.out.println(keyword);
                throw ex;
            }
        }
    }

    public void test_where_2() throws Exception {
        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];

            String sql = "select 1 from t where max(" + keyword + ") = 1";

            try {
                SQLUtils.parseSingleMysqlStatement(sql);
            } catch (ParserException ex) {
                System.out.println(keyword);
                throw ex;
            }
        }
    }
}
