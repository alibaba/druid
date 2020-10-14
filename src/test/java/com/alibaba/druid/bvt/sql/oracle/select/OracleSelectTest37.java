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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest37 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "select resource_value,count(resource_value) nums,http_method "
                + "from ( "
                + "       select * from audit_url_log "
                + "       where project_id = ? and to_char(begin_time,'yyyy-MM-dd') > = ? and to_char(begin_time,'yyyy-MM-dd') < = ? ) "
                + "       group by resource_value,http_method having count(resource_value) > = ?"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        
        Assert.assertEquals("SELECT resource_value, count(resource_value) AS nums, http_method"
                + "\nFROM ("
                + "\n\tSELECT *"
                + "\n\tFROM audit_url_log"
                + "\n\tWHERE project_id = ?"
                + "\n\t\tAND to_char(begin_time, 'yyyy-MM-dd') >= ?"
                + "\n\t\tAND to_char(begin_time, 'yyyy-MM-dd') <= ?"
                + "\n)"
                + "\nGROUP BY resource_value, http_method"
                + "\nHAVING count(resource_value) >= ?", SQLUtils.toOracleString(stmt));

        Assert.assertEquals("select resource_value, count(resource_value) as nums, http_method"
                + "\nfrom ("
                + "\n\tselect *"
                + "\n\tfrom audit_url_log"
                + "\n\twhere project_id = ?"
                + "\n\t\tand to_char(begin_time, 'yyyy-MM-dd') >= ?"
                + "\n\t\tand to_char(begin_time, 'yyyy-MM-dd') <= ?"
                + "\n)"
                + "\ngroup by resource_value, http_method"
                + "\nhaving count(resource_value) >= ?", SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
        
        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("audit_url_log")));

        Assert.assertEquals(5, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("audit_url_log", "project_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("audit_url_log", "begin_time")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
