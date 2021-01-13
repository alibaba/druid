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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateViewTest14 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE OR REPLACE FORCE VIEW \"CITSONLINE\".\"VIEW_SK_ORDER_APPLY\" (\"ORDER_ID\", \"PRODUCT_TYPE\", \"ORDER_AGENT\", \"COLLECT_MONEY\", \"CUSTOMER_ID\", \"ADD_USER\", \"ADD_DATE\", \"TEAM_NAME\", \"TEAM_NO\", \"CONTACT\", \"ORDER_ACCOUNT\") AS \n" +
                "  select id as order_id,\n" +
                "       'PW' product_type,\n" +
                "       order_owner as order_agent,\n" +
                "       account_receivable as collect_money,\n" +
                "       customer_group as customer_id,\n" +
                "       operator_id as add_user,\n" +
                "       order_date as add_date,\n" +
                "       '' as team_name,\n" +
                "       '' as team_no,\n" +
                "       customer_name as contact,\n" +
                "       (select t.name from operator t where t.account = at.ouser_id) order_account\n" +
                "  from ticket_product_order pw_order_info, allot_track at\n" +
                " where at.order_id like '%' || pw_order_info.id || '%'\n" +
                "   and at.produce_id = 'PW'\n" +
                "   and at.end_date = to_date('1900-01-01', 'yyyy-mm-dd') --hn --766ߡh (\n" +
                "   and pw_order_info.status = '4'\n" +
                "union\n" +
                "select insurance_order. order_id as order_id,\n" +
                "       'BX' product_type,\n" +
                "       insurance_order.agent_id as order_agent,\n" +
                "       insurance_order. all_price as collect_money,\n" +
                "       insurance_order. custom_id as customer_id,\n" +
                "       insurance_order. add_user,\n" +
                "       insurance_order. add_date,\n" +
                "       '' as team_name,\n" +
                "       '' as team_no,\n" +
                "       insurance_order. contact_name as contact,\n" +
                "       (select t.name from operator t where t.account = at.ouser_id) order_account\n" +
                "  from insurance_order insurance_order, allot_track at\n" +
                " where insurance_order.status = '3' --iU\n" +
                "   and at.order_id_s = insurance_order.order_id\n" +
                "   and at.produce_id = 'BX'\n" +
                "   and at.end_date = to_date('1900-01-01', 'yyyy-mm-dd')\n" +
                "union\n" +
                "select t.order_id as order_id,\n" +
                "       tt.product_type product_type,\n" +
                "       tt.true_agent_id as order_agent,\n" +
                "       t.price as collect_money,\n" +
                "       t.custom_id as customer_id,\n" +
                "       t.add_user,\n" +
                "       t.add_date,\n" +
                "       tb.name as team_name,\n" +
                "       tb.team_no as team_no,\n" +
                "       t.person as contact,\n" +
                "       (select t.name from operator t where t.account = tt.od_user) order_account\n" +
                "  from order_info t, order_flow_info tt, team_baseinfo tb\n" +
                " where t.product_type in ('0', '7')\n" +
                "   and t.order_id = tt.order_id\n" +
                "   and tt.user_type = '0'\n" +
                "   and (tt.status = '3' or tt.status = '4' or tt.status = '5')\n" +
                "   and tt.product_id = tb.team_id\n" +
                "   --3n,4c,5c*6>\n" +
                "\n" +
                "  union\n" +
                "/*  hU*/\n" +
                "\n" +
                "select t.order_id as order_id,\n" +
                "       'GN' product_type,\n" +
                "       t.agent_id as order_agent,\n" +
                "       t.all_price as collect_money,\n" +
                "       t.olduserid as customer_id,\n" +
                "       t.add_user as add_user,\n" +
                "       t.add_date as add_date,\n" +
                "       '' as team_name,\n" +
                "       '' as team_no,\n" +
                "       t.client_name as contact,\n" +
                "       (select o.name\n" +
                "          from operator o, allot_track a\n" +
                "         where o.account = a.ouser_id\n" +
                "           and a.order_id_s = t.order_id\n" +
                "           and a.agent_id_s = o.agent_id\n" +
                "           and a.agent_id_s = t.agent_id\n" +
                "           and a.end_date =\n" +
                "               to_timestamp('1900-1-1', 'yyyy-mm-dd hh24:mi:ssxff')) order_account\n" +
                "  from ticket_order_info t\n" +
                " where t.product_type = 'GN'\n" +
                "   and (t.order_status = '6' or t.ticket_status = '2')\n" +
                "\n" +
                "union\n" +
                "\n" +
                "/* Eh*/\n" +
                "select t.order_id as order_id,\n" +
                "       'GJ' product_type,\n" +
                "       t.agent_id as order_agent,\n" +
                "       t.all_price as collect_money,\n" +
                "       t.olduserid as customer_id,\n" +
                "       t.add_user as add_user,\n" +
                "       t.add_date as add_date,\n" +
                "       '' as team_name,\n" +
                "       '' as team_no,\n" +
                "       t.client_name as contact,\n" +
                "       (select o.name\n" +
                "          from operator o, allot_track a\n" +
                "         where o.account = a.ouser_id\n" +
                "           and a.order_id_s = t.order_id\n" +
                "           and a.agent_id_s = t.agent_id\n" +
                "           and a.agent_id_s = o.agent_id\n" +
                "           and a.end_date =\n" +
                "               to_timestamp('1900-1-1', 'yyyy-mm-dd hh24:mi:ssxff')) order_account\n" +
                "  from ticket_order_info t\n" +
                " where t.product_type = 'GJ'\n" +
                "   and t.order_id like '02%'\n" +
                "   and (t.order_status = '3' or t.ticket_status = '2')\n" +
                "  union\n" +
                "--~\"5\">~,6\">ӗ\n" +
                "select voi.order_id as order_id,\n" +
                "       'QZ' product_type,\n" +
                "       ofi.true_agent_id as order_agent,\n" +
                "       ofi.sell_sum,\n" +
                "       voi.custom_id as customer_id,\n" +
                "       voi.add_user,\n" +
                "       voi.add_date,\n" +
                "       ofi.bal_team_id as team_name,\n" +
                "       ofi.bal_team_no as team_no,\n" +
                "       voi.user_name as contact,\n" +
                "       (select op.name\n" +
                "          from operator op, allot_track al\n" +
                "         where op.account = al.ouser_id\n" +
                "           and al.order_id_s = ofi.order_id\n" +
                "           and al.supply_id_s = ofi.supply_id\n" +
                "           and al.agent_id_s = ofi.agent_id\n" +
                "           and al.end_date = to_timestamp('1900-01-01', 'yyyy-MM-dd')\n" +
                "           and al.produce_Id = 'QZ') order_account\n" +
                "  from order_flow_info ofi, visa_order_info voi\n" +
                " where ofi.product_type = 'QZ'\n" +
                "   and voi.order_id = ofi.order_id\n" +
                "   and ofi.user_type = '0'\n" +
                "   and (ofi.status = '5' or ofi.status = '6')\n" +
                "\n" +
                "\n" +
                "  union\n" +
                "--R3an,3bn*,7B2C6>n\n" +
                "select res.reservation_id,\n" +
                "       'FD' product_type,\n" +
                "       res.agent_id,\n" +
                "       res.total_sale_amt sale_amt,\n" +
                "       bus.customer_id,\n" +
                "       bus.operator,\n" +
                "       bus.create_date,\n" +
                "       '' as team_name,\n" +
                "       '' as team_no,\n" +
                "       res.contact_person as contact,\n" +
                "       (select t.name\n" +
                "          from operator t, allot_track a\n" +
                "         where t.account = a.ouser_id\n" +
                "           and bus.reservation_id = a.order_id\n" +
                "           and bus.agent_id = a.agent_id\n" +
                "           and a.end_date =\n" +
                "               to_timestamp('1900-1-1', 'yyyy-mm-dd hh24:mi:ssxff')) order_account\n" +
                "  from resaccount res, businessres bus\n" +
                " where bus.reservation_id = res.reservation_id\n" +
                "   and (bus.confirm_status = '3' or bus.confirm_status = '7')"
               ;

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE VIEW \"CITSONLINE\".\"VIEW_SK_ORDER_APPLY\" (\n" +
                        "\t\"ORDER_ID\", \n" +
                        "\t\"PRODUCT_TYPE\", \n" +
                        "\t\"ORDER_AGENT\", \n" +
                        "\t\"COLLECT_MONEY\", \n" +
                        "\t\"CUSTOMER_ID\", \n" +
                        "\t\"ADD_USER\", \n" +
                        "\t\"ADD_DATE\", \n" +
                        "\t\"TEAM_NAME\", \n" +
                        "\t\"TEAM_NO\", \n" +
                        "\t\"CONTACT\", \n" +
                        "\t\"ORDER_ACCOUNT\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT id AS order_id, 'PW' AS product_type, order_owner AS order_agent, account_receivable AS collect_money, customer_group AS customer_id\n" +
                        "\t, operator_id AS add_user, order_date AS add_date, NULL AS team_name, NULL AS team_no, customer_name AS contact\n" +
                        "\t, (\n" +
                        "\t\tSELECT t.name\n" +
                        "\t\tFROM operator t\n" +
                        "\t\tWHERE t.account = at.ouser_id\n" +
                        "\t) AS order_account\n" +
                        "FROM ticket_product_order pw_order_info, allot_track at\n" +
                        "WHERE at.order_id LIKE ('%' || pw_order_info.id || '%')\n" +
                        "\tAND at.produce_id = 'PW'\n" +
                        "\tAND at.end_date = to_date('1900-01-01', 'yyyy-mm-dd') -- hn --766ߡh (\n" +
                        "\tAND pw_order_info.status = '4'\n" +
                        "UNION\n" +
                        "SELECT insurance_order.order_id AS order_id, 'BX' AS product_type, insurance_order.agent_id AS order_agent, insurance_order.all_price AS collect_money, insurance_order.custom_id AS customer_id\n" +
                        "\t, insurance_order.add_user, insurance_order.add_date, NULL AS team_name, NULL AS team_no, insurance_order.contact_name AS contact\n" +
                        "\t, (\n" +
                        "\t\tSELECT t.name\n" +
                        "\t\tFROM operator t\n" +
                        "\t\tWHERE t.account = at.ouser_id\n" +
                        "\t) AS order_account\n" +
                        "FROM insurance_order insurance_order, allot_track at\n" +
                        "WHERE insurance_order.status = '3' -- iU\n" +
                        "\tAND at.order_id_s = insurance_order.order_id\n" +
                        "\tAND at.produce_id = 'BX'\n" +
                        "\tAND at.end_date = to_date('1900-01-01', 'yyyy-mm-dd')\n" +
                        "UNION\n" +
                        "SELECT t.order_id AS order_id, tt.product_type AS product_type, tt.true_agent_id AS order_agent, t.price AS collect_money, t.custom_id AS customer_id\n" +
                        "\t, t.add_user, t.add_date, tb.name AS team_name, tb.team_no AS team_no, t.person AS contact\n" +
                        "\t, (\n" +
                        "\t\tSELECT t.name\n" +
                        "\t\tFROM operator t\n" +
                        "\t\tWHERE t.account = tt.od_user\n" +
                        "\t) AS order_account\n" +
                        "FROM order_info t, order_flow_info tt, team_baseinfo tb\n" +
                        "WHERE t.product_type IN ('0', '7')\n" +
                        "\tAND t.order_id = tt.order_id\n" +
                        "\tAND tt.user_type = '0'\n" +
                        "\tAND (tt.status = '3'\n" +
                        "\t\tOR tt.status = '4'\n" +
                        "\t\tOR tt.status = '5')\n" +
                        "\tAND tt.product_id = tb.team_id\n" +
                        "UNION\n" +
                        "/*  hU*/\n" +
                        "SELECT t.order_id AS order_id, 'GN' AS product_type, t.agent_id AS order_agent, t.all_price AS collect_money, t.olduserid AS customer_id\n" +
                        "\t, t.add_user AS add_user, t.add_date AS add_date, NULL AS team_name, NULL AS team_no, t.client_name AS contact\n" +
                        "\t, (\n" +
                        "\t\tSELECT o.name\n" +
                        "\t\tFROM operator o, allot_track a\n" +
                        "\t\tWHERE o.account = a.ouser_id\n" +
                        "\t\t\tAND a.order_id_s = t.order_id\n" +
                        "\t\t\tAND a.agent_id_s = o.agent_id\n" +
                        "\t\t\tAND a.agent_id_s = t.agent_id\n" +
                        "\t\t\tAND a.end_date = to_timestamp('1900-1-1', 'yyyy-mm-dd hh24:mi:ssxff')\n" +
                        "\t) AS order_account\n" +
                        "FROM ticket_order_info t\n" +
                        "WHERE t.product_type = 'GN'\n" +
                        "\tAND (t.order_status = '6'\n" +
                        "\t\tOR t.ticket_status = '2')\n" +
                        "UNION\n" +
                        "/* Eh*/\n" +
                        "SELECT t.order_id AS order_id, 'GJ' AS product_type, t.agent_id AS order_agent, t.all_price AS collect_money, t.olduserid AS customer_id\n" +
                        "\t, t.add_user AS add_user, t.add_date AS add_date, NULL AS team_name, NULL AS team_no, t.client_name AS contact\n" +
                        "\t, (\n" +
                        "\t\tSELECT o.name\n" +
                        "\t\tFROM operator o, allot_track a\n" +
                        "\t\tWHERE o.account = a.ouser_id\n" +
                        "\t\t\tAND a.order_id_s = t.order_id\n" +
                        "\t\t\tAND a.agent_id_s = t.agent_id\n" +
                        "\t\t\tAND a.agent_id_s = o.agent_id\n" +
                        "\t\t\tAND a.end_date = to_timestamp('1900-1-1', 'yyyy-mm-dd hh24:mi:ssxff')\n" +
                        "\t) AS order_account\n" +
                        "FROM ticket_order_info t\n" +
                        "WHERE t.product_type = 'GJ'\n" +
                        "\tAND t.order_id LIKE '02%'\n" +
                        "\tAND (t.order_status = '3'\n" +
                        "\t\tOR t.ticket_status = '2')\n" +
                        "UNION\n" +
                        "-- ~\"5\">~,6\">ӗ\n" +
                        "SELECT voi.order_id AS order_id, 'QZ' AS product_type, ofi.true_agent_id AS order_agent, ofi.sell_sum, voi.custom_id AS customer_id\n" +
                        "\t, voi.add_user, voi.add_date, ofi.bal_team_id AS team_name, ofi.bal_team_no AS team_no, voi.user_name AS contact\n" +
                        "\t, (\n" +
                        "\t\tSELECT op.name\n" +
                        "\t\tFROM operator op, allot_track al\n" +
                        "\t\tWHERE op.account = al.ouser_id\n" +
                        "\t\t\tAND al.order_id_s = ofi.order_id\n" +
                        "\t\t\tAND al.supply_id_s = ofi.supply_id\n" +
                        "\t\t\tAND al.agent_id_s = ofi.agent_id\n" +
                        "\t\t\tAND al.end_date = to_timestamp('1900-01-01', 'yyyy-MM-dd')\n" +
                        "\t\t\tAND al.produce_Id = 'QZ'\n" +
                        "\t) AS order_account\n" +
                        "FROM order_flow_info ofi, visa_order_info voi\n" +
                        "WHERE ofi.product_type = 'QZ'\n" +
                        "\tAND voi.order_id = ofi.order_id\n" +
                        "\tAND ofi.user_type = '0'\n" +
                        "\tAND (ofi.status = '5'\n" +
                        "\t\tOR ofi.status = '6')\n" +
                        "UNION\n" +
                        "-- R3an,3bn*,7B2C6>n\n" +
                        "SELECT res.reservation_id, 'FD' AS product_type, res.agent_id, res.total_sale_amt AS sale_amt, bus.customer_id\n" +
                        "\t, bus.operator, bus.create_date, NULL AS team_name, NULL AS team_no, res.contact_person AS contact\n" +
                        "\t, (\n" +
                        "\t\tSELECT t.name\n" +
                        "\t\tFROM operator t, allot_track a\n" +
                        "\t\tWHERE t.account = a.ouser_id\n" +
                        "\t\t\tAND bus.reservation_id = a.order_id\n" +
                        "\t\t\tAND bus.agent_id = a.agent_id\n" +
                        "\t\t\tAND a.end_date = to_timestamp('1900-1-1', 'yyyy-mm-dd hh24:mi:ssxff')\n" +
                        "\t) AS order_account\n" +
                        "FROM resaccount res, businessres bus\n" +
                        "WHERE bus.reservation_id = res.reservation_id\n" +
                        "\tAND (bus.confirm_status = '3'\n" +
                        "\t\tOR bus.confirm_status = '7')",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(11, visitor.getTables().size());

        assertEquals(75, visitor.getColumns().size());

        assertTrue(visitor.getColumns().contains(new TableStat.Column("ticket_product_order", "status")));
    }
}
