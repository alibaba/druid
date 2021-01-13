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
package com.alibaba.druid.bvt.sql.sqlserver;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SQLServerAlterTableTest_13_default extends TestCase {

    public void test_alter_first() throws Exception {
        String sql = "ALTER TABLE N_MerchantBase ADD  CONSTRAINT DF_N_MerchantBase_UnUsedCouponMoney  DEFAULT ((0)) FOR [UnUsedCouponMoney]";
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.sqlserver);
        SQLStatement stmt = sqlStatements.get(0);

        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toSQLString(stmt, JdbcConstants.SQL_SERVER);
            Assert.assertEquals("ALTER TABLE N_MerchantBase\n" +
                    "\tADD CONSTRAINT DF_N_MerchantBase_UnUsedCouponMoney DEFAULT 0 FOR [UnUsedCouponMoney]", output);
        }
        {
            String output = SQLUtils.toSQLString(stmt, JdbcConstants.SQL_SERVER, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("alter table N_MerchantBase\n" +
                    "\tadd constraint DF_N_MerchantBase_UnUsedCouponMoney default 0 for [UnUsedCouponMoney]", output);
        }
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(1, visitor.getColumns().size());
    }

    @Test
    public void test_alter_table_constraint_default() {

        String sql = "ALTER TABLE N_MerchantBase ADD  CONSTRAINT DF_N_MerchantBase_UnUsedCouponMoney  DEFAULT 0 FOR UnUsedCouponMoney;";

        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, com.alibaba.druid.DbType.sqlserver);

        Assert.assertEquals("ALTER TABLE N_MerchantBase\n" +
                "\tADD CONSTRAINT DF_N_MerchantBase_UnUsedCouponMoney DEFAULT 0 FOR UnUsedCouponMoney;", sqlStatements.get(0).toString());
    }

    @Test
    public void test_alter_table_add_default() {

        String sql = "alter table GoingHomeMain  add default(0) for MilesTotal with values;";

        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, com.alibaba.druid.DbType.sqlserver);

        // System.out.println(sqlStatements.get(0).toString());
        Assert.assertEquals("ALTER TABLE GoingHomeMain\n" +
                "\tADD DEFAULT 0 FOR MilesTotal WITH VALUES;", sqlStatements.get(0).toString());

    }


}
