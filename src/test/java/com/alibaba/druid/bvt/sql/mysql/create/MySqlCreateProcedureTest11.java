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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlCreateProcedureTest11 extends MysqlTest {

    public void test_0() throws Exception {
    	String sql = "CREATE PROCEDURE curdemo()\n" +
                "BEGIN\n" +
                "  DECLARE done INT DEFAULT FALSE;\n" +
                "  DECLARE a CHAR(16);\n" +
                "  DECLARE b, c INT;\n" +
                "  DECLARE cur1 CURSOR FOR SELECT id,data FROM test.t1;\n" +
                "  DECLARE cur2 CURSOR FOR SELECT i FROM test.t2;\n" +
                "  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;\n" +
                "\n" +
                "  OPEN cur1;\n" +
                "  OPEN cur2;\n" +
                "\n" +
                "  read_loop: LOOP\n" +
                "    FETCH cur1 INTO a, b;\n" +
                "    FETCH cur2 INTO c;\n" +
                "    IF done THEN\n" +
                "      LEAVE read_loop;\n" +
                "    END IF;\n" +
                "    IF b < c THEN\n" +
                "      INSERT INTO test.t3 VALUES (a,b);\n" +
                "    ELSE\n" +
                "      INSERT INTO test.t3 VALUES (a,c);\n" +
                "    END IF;\n" +
                "  END LOOP;\n" +
                "\n" +
                "  CLOSE cur1;\n" +
                "  CLOSE cur2;\n" +
                "END;";

    	List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
    	SQLStatement stmt = statementList.get(0);
//    	print(statementList);
//        assertEquals(1, statementList.size());

        System.out.println(SQLUtils.toMySqlString(stmt));

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(3, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    
}
