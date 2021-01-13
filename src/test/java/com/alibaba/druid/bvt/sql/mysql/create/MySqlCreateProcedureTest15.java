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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLParameter;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateFunctionStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateProcedureStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlCreateProcedureTest15 extends MysqlTest {

    public void test_0() throws Exception {
    	String sql = "     create function `test1`.`proc1`(`a` enum('1','2') charset utf8)\n" +
                "               returns int(10)\n" +
                "               DETERMINISTIC \n" +
                "     BEGIN\n" +
                "              return 0;\n" +
                "     END ";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL, false);
        SQLCreateFunctionStatement stmt = parser.parseCreateFunction();
        assertEquals(1, stmt.getParameters().size());

        assertEquals("CREATE FUNCTION `test1`.`proc1` (\n" +
                "\t`a` enum('1', '2') CHARACTER SET utf8\n" +
                ")\n" +
                "RETURNS int(10) DETERMINISTIC\n" +
                "BEGIN\n" +
                "\tRETURN 0;\n" +
                "END", stmt.toString());
    }

    
}
