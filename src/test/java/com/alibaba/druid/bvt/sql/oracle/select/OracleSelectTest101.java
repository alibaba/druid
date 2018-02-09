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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest101 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "SELECT e.area_name 区域,\n" +
                        "  e.department_user_id 店铺代码,\n" +
                        "  e.department_name 店铺名称,\n" +
                        "  a.card_id 会员卡号,\n" +
                        "  a.vip_name 姓名,\n" +
                        "  （case WHEN a.vip_sex='1' THEN '男' WHEN a.vip_sex='2' THEN '女' ELSE '保密' end）性别,\n" +
                        "  a.vip_birthday_year\n" +
                        "  ||'-'\n" +
                        "  ||a.vip_birthday_month\n" +
                        "  ||'-'\n" +
                        "  ||a.vip_birthday_day 出生日期,\n" +
                        "  a.vip_create_date 会员注册日期,\n" +
                        "  a.vip_mobile 手机号,\n" +
                        "  a.vip_job 职业,\n" +
                        "  a.wechat 微信号,\n" +
                        "  a.vip_email 邮箱,\n" +
                        "  d.viptype_name 会员等级\n" +
                        "FROM D0210 a\n" +
                        "JOIN D0180 b\n" +
                        "ON a.vip_id = b.vip_id\n" +
                        "JOIN D0160 c\n" +
                        "ON c.viptype_id = b.viptype_id\n" +
                        "JOIN D0169 d\n" +
                        "ON d.viptype_id   = c.viptype_id\n" +
                        "AND d.language_id = 'zh-cn'\n" +
                        "JOIN area_store_hn e\n" +
                        "ON a.department_id=e.department_id\n" +
                        "WHERE a.vip_create_date BETWEEN TRUNC(SYSDATE)- 4 - 10 / 24 AND TRUNC(SYSDATE) - 10 / 24 ----注册日期\n" +
                        "AND (a.vip_state          = '0'\n" +
                        "OR a.vip_state            = '1')\n" +
                        "AND e.department_user_id IN ('44011','44012','44021','44026','44027','44028','44056','44062','44081','44083','44086','44095','44097','44118','44122','44126','44161','44182','44204','44209','44211','44247','44252','44254','44282','44284','44288','44298','44058','44068','44084','44162')\n" +
                        "ORDER BY a.vip_create_date";

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

//        SQLMethodInvokeExpr expr = (SQLMethodInvokeExpr) stmt.getSelect().getQueryBlock().getSelectList().get(0).getExpr();
//        SQLMethodInvokeExpr param0 = (SQLMethodInvokeExpr) expr.getParameters().get(0);
//        assertTrue(param0.getParameters().get(0)
//                instanceof SQLAggregateExpr);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT e.area_name AS 区域, e.department_user_id AS 店铺代码, e.department_name AS 店铺名称, a.card_id AS 会员卡号, a.vip_name AS 姓名\n" +
                    "\t, CASE \n" +
                    "\t\tWHEN a.vip_sex = '1' THEN '男'\n" +
                    "\t\tWHEN a.vip_sex = '2' THEN '女'\n" +
                    "\t\tELSE '保密'\n" +
                    "\tEND AS 性别\n" +
                    "\t, a.vip_birthday_year || '-' || a.vip_birthday_month || '-' || a.vip_birthday_day AS 出生日期\n" +
                    "\t, a.vip_create_date AS 会员注册日期, a.vip_mobile AS 手机号, a.vip_job AS 职业, a.wechat AS 微信号, a.vip_email AS 邮箱\n" +
                    "\t, d.viptype_name AS 会员等级\n" +
                    "FROM D0210 a\n" +
                    "JOIN D0180 b ON a.vip_id = b.vip_id \n" +
                    "JOIN D0160 c ON c.viptype_id = b.viptype_id \n" +
                    "JOIN D0169 d ON d.viptype_id = c.viptype_id\n" +
                    "AND d.language_id = 'zh-cn' \n" +
                    "\tJOIN area_store_hn e ON a.department_id = e.department_id \n" +
                    "WHERE a.vip_create_date BETWEEN TRUNC(SYSDATE) - 4 - 10 / 24 AND TRUNC(SYSDATE) - 10 / 24 -- --注册日期\n" +
                    "\tAND (a.vip_state = '0'\n" +
                    "\t\tOR a.vip_state = '1')\n" +
                    "\tAND e.department_user_id IN (\n" +
                    "\t\t'44011', \n" +
                    "\t\t'44012', \n" +
                    "\t\t'44021', \n" +
                    "\t\t'44026', \n" +
                    "\t\t'44027', \n" +
                    "\t\t'44028', \n" +
                    "\t\t'44056', \n" +
                    "\t\t'44062', \n" +
                    "\t\t'44081', \n" +
                    "\t\t'44083', \n" +
                    "\t\t'44086', \n" +
                    "\t\t'44095', \n" +
                    "\t\t'44097', \n" +
                    "\t\t'44118', \n" +
                    "\t\t'44122', \n" +
                    "\t\t'44126', \n" +
                    "\t\t'44161', \n" +
                    "\t\t'44182', \n" +
                    "\t\t'44204', \n" +
                    "\t\t'44209', \n" +
                    "\t\t'44211', \n" +
                    "\t\t'44247', \n" +
                    "\t\t'44252', \n" +
                    "\t\t'44254', \n" +
                    "\t\t'44282', \n" +
                    "\t\t'44284', \n" +
                    "\t\t'44288', \n" +
                    "\t\t'44298', \n" +
                    "\t\t'44058', \n" +
                    "\t\t'44068', \n" +
                    "\t\t'44084', \n" +
                    "\t\t'44162'\n" +
                    "\t)\n" +
                    "ORDER BY a.vip_create_date", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(5, visitor.getTables().size());
        assertEquals(24, visitor.getColumns().size());
        assertEquals(12, visitor.getConditions().size());
        assertEquals(4, visitor.getRelationships().size());
        assertEquals(1, visitor.getOrderByColumns().size());
    }

   
}
