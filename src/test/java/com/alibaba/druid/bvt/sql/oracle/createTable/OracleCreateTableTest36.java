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
package com.alibaba.druid.bvt.sql.oracle.createTable;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleCreateTableTest36 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE composite_sales"
        + "    ( prod_id        NUMBER(6)"
        + "    , cust_id        NUMBER"
        + "    , time_id        DATE"
        + "    , channel_id     CHAR(1)"
        + "    , promo_id       NUMBER(6)"
        + "    , quantity_sold  NUMBER(3)"
        + "    , amount_sold         NUMBER(10,2)"
        + "    ) "
        + "PARTITION BY RANGE (time_id)"
        + "SUBPARTITION BY HASH (channel_id)"
        + "  (PARTITION SALES_Q1_1998 VALUES LESS THAN (TO_DATE('01-APR-1998','DD-MON-YYYY')),"
        + "   PARTITION SALES_Q2_1998 VALUES LESS THAN (TO_DATE('01-JUL-1998','DD-MON-YYYY')),"
        + "   PARTITION SALES_Q3_1998 VALUES LESS THAN (TO_DATE('01-OCT-1998','DD-MON-YYYY')),"
        + "   PARTITION SALES_Q4_1998 VALUES LESS THAN (TO_DATE('01-JAN-1999','DD-MON-YYYY')),"
        + "   PARTITION SALES_Q1_1999 VALUES LESS THAN (TO_DATE('01-APR-1999','DD-MON-YYYY')),"
        + "   PARTITION SALES_Q2_1999 VALUES LESS THAN (TO_DATE('01-JUL-1999','DD-MON-YYYY')),"
        + "   PARTITION SALES_Q3_1999 VALUES LESS THAN (TO_DATE('01-OCT-1999','DD-MON-YYYY')),"
        + "   PARTITION SALES_Q4_1999 VALUES LESS THAN (TO_DATE('01-JAN-2000','DD-MON-YYYY')),"
        + "   PARTITION SALES_Q1_2000 VALUES LESS THAN (TO_DATE('01-APR-2000','DD-MON-YYYY')),"
        + "   PARTITION SALES_Q2_2000 VALUES LESS THAN (TO_DATE('01-JUL-2000','DD-MON-YYYY'))"
        + "      SUBPARTITIONS 8,"
        + "   PARTITION SALES_Q3_2000 VALUES LESS THAN (TO_DATE('01-OCT-2000','DD-MON-YYYY'))"
        + "     (SUBPARTITION ch_c,"
        + "      SUBPARTITION ch_i,"
        + "      SUBPARTITION ch_p,"
        + "      SUBPARTITION ch_s,"
        + "      SUBPARTITION ch_t),"
        + "   PARTITION SALES_Q4_2000 VALUES LESS THAN (MAXVALUE)"
        + "      SUBPARTITIONS 4)"
        + ";";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE composite_sales ("
                + "\n\tprod_id NUMBER(6),"
                + "\n\tcust_id NUMBER,"
                + "\n\ttime_id DATE,"
                + "\n\tchannel_id CHAR(1),"
                + "\n\tpromo_id NUMBER(6),"
                + "\n\tquantity_sold NUMBER(3),"
                + "\n\tamount_sold NUMBER(10, 2)"
                + "\n)"
                + "\nPARTITION BY RANGE (time_id)"
                + "\nSUBPARTITION BY HASH (channel_id) ("
                + "\n\tPARTITION SALES_Q1_1998 VALUES LESS THAN (TO_DATE('01-APR-1998', 'DD-MON-YYYY')),"
                + "\n\tPARTITION SALES_Q2_1998 VALUES LESS THAN (TO_DATE('01-JUL-1998', 'DD-MON-YYYY')),"
                + "\n\tPARTITION SALES_Q3_1998 VALUES LESS THAN (TO_DATE('01-OCT-1998', 'DD-MON-YYYY')),"
                + "\n\tPARTITION SALES_Q4_1998 VALUES LESS THAN (TO_DATE('01-JAN-1999', 'DD-MON-YYYY')),"
                + "\n\tPARTITION SALES_Q1_1999 VALUES LESS THAN (TO_DATE('01-APR-1999', 'DD-MON-YYYY')),"
                + "\n\tPARTITION SALES_Q2_1999 VALUES LESS THAN (TO_DATE('01-JUL-1999', 'DD-MON-YYYY')),"
                + "\n\tPARTITION SALES_Q3_1999 VALUES LESS THAN (TO_DATE('01-OCT-1999', 'DD-MON-YYYY')),"
                + "\n\tPARTITION SALES_Q4_1999 VALUES LESS THAN (TO_DATE('01-JAN-2000', 'DD-MON-YYYY')),"
                + "\n\tPARTITION SALES_Q1_2000 VALUES LESS THAN (TO_DATE('01-APR-2000', 'DD-MON-YYYY')),"
                + "\n\tPARTITION SALES_Q2_2000 VALUES LESS THAN (TO_DATE('01-JUL-2000', 'DD-MON-YYYY'))"
                + "\n\t\tSUBPARTITIONS 8,"
                + "\n\tPARTITION SALES_Q3_2000 VALUES LESS THAN (TO_DATE('01-OCT-2000', 'DD-MON-YYYY')) ("
                + "\n\t\tSUBPARTITION ch_c,"
                + "\n\t\tSUBPARTITION ch_i,"
                + "\n\t\tSUBPARTITION ch_p,"
                + "\n\t\tSUBPARTITION ch_s,"
                + "\n\t\tSUBPARTITION ch_t"
                + "\n\t),"
                + "\n\tPARTITION SALES_Q4_2000 VALUES LESS THAN (MAXVALUE)"
                + "\n\t\tSUBPARTITIONS 4"
                + "\n);",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(7, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("composite_sales", "prod_id")));
    }
}
