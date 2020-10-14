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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleCreateTableTest87 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE \"HX_DJ\".\"DJ_JZYXMDJ_THJJFGQK\" \n" +
                        "   (\t\"THJUUID\" VARCHAR2(32) NOT NULL ENABLE, \n" +
                        "\t\"GCXMBH\" VARCHAR2(40) NOT NULL ENABLE, \n" +
                        "\t\"THJRQ\" DATE NOT NULL ENABLE, \n" +
                        "\t\"THJYY\" VARCHAR2(3000) NOT NULL ENABLE, \n" +
                        "\t\"GCJD\" NUMBER(4,2) NOT NULL ENABLE, \n" +
                        "\t\"YJGJK\" NUMBER(18,2) NOT NULL ENABLE, \n" +
                        "\t\"YNSKYYS\" NUMBER(18,2) NOT NULL ENABLE, \n" +
                        "\t\"YNSKCJS\" NUMBER(18,2), \n" +
                        "\t\"YNSKJYFFJ\" NUMBER(18,2), \n" +
                        "\t\"FGRQ\" DATE, \n" +
                        "\t\"YXBZ\" CHAR(1) DEFAULT 'Y' NOT NULL ENABLE, \n" +
                        "\t\"LRR_DM\" CHAR(11) NOT NULL ENABLE, \n" +
                        "\t\"LRRQ\" DATE NOT NULL ENABLE, \n" +
                        "\t\"XGR_DM\" CHAR(11), \n" +
                        "\t\"XGRQ\" DATE, \n" +
                        "\t\"SJGSDQ\" CHAR(11) NOT NULL ENABLE, \n" +
                        "\t\"JZYGCXMUUID\" CHAR(32), \n" +
                        "\t\"SJTB_SJ\" TIMESTAMP (6), \n" +
                        "\t CONSTRAINT \"PK_DJ_JZYXMDJ_THJJFGQK\" PRIMARY KEY (\"THJUUID\")\n" +
                        "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                        "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"HXZG_IDX\"  ENABLE, \n" +
                        "\t CONSTRAINT \"FK_DJ_JZYXMDJ_THJJFGQK_UUID\" FOREIGN KEY (\"JZYGCXMUUID\")\n" +
                        "\t  REFERENCES \"HX_DJ\".\"DJ_JZYGCXMQKDJXXB\" (\"JZYGCXMUUID\") DISABLE\n" +
                        "   ) SEGMENT CREATION IMMEDIATE \n" +
                        "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                        " NOCOMPRESS LOGGING\n" +
                        "  STORAGE(INITIAL 16384 NEXT 8192 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"HXZG_DAT\";";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

//        Assert.assertTrue(visitor.containsTable("\"HX_DJ\".\"DJ_JZYXMDJ_THJJFGQK\""));
//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("mid_users_restore_account_598")));
//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("subtotal_bill")));
//
//        Assert.assertEquals(8, visitor.getColumns().size());

    }
}
