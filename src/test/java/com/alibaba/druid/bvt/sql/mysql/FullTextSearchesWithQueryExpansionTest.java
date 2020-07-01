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
package com.alibaba.druid.bvt.sql.mysql;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import junit.framework.TestCase;

public class FullTextSearchesWithQueryExpansionTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM articles WHERE MATCH (title,body) AGAINST ('database' IN NATURAL LANGUAGE MODE)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement stmt = stmtList.get(0);

        {
            String text = SQLUtils.toMySqlString(stmt);
    
            Assert.assertEquals("SELECT *" //
                    + "\nFROM articles" //
                    + "\nWHERE MATCH (title, body) AGAINST ('database' IN NATURAL LANGUAGE MODE)",
                                text);
        }
        {
            String text = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
    
            Assert.assertEquals("select *" //
                    + "\nfrom articles" //
                    + "\nwhere match (title, body) against ('database' in natural language mode)",
                                text);
        }
    }

}
