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

public class OracleCreateViewTest16 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE OR REPLACE FORCE VIEW \"APPS\".\"PMI_REP_SALES_DETAILS_V\" (\n" +
                "\t\"ITEM_ID\", \"ORDER_QUANTITY\", \"WAREHOUSE_SHIPPED_FROM\", \"EXTENDED_PRICE_BASE_CURRENCY\", \"CHARGES_BASE_CURRENCY\", \"SALES_COMPANY\", \"SALES_ORGANIZATION\", \"PERIOD_START_DATE\", \"OPERATING_UNIT_ID\", \"YEAR_ID\", \"QUARTER_ID\", \"PERIOD_ID\"\n" +
                ") AS \n" +
                "SELECT ITEM_ID ,SUM(ORDER_QUANTITY) ORDER_QUANTITY ,WAREHOUSE_SHIPPED_FROM ,SUM(EXTENDED_PRICE_BASE_CURRENCY) EXTENDED_PRICE_BASE_CURRENCY \n" +
                "\t,SUM(CHARGES_BASE_CURRENCY) CHARGES_BASE_CURRENCY \n" +
                "\t,SALES_COMPANY ,SALES_ORGANIZATION ,PERIOD_START_DATE ,OPERATING_UNIT_ID ,YEAR_ID ,QUARTER_ID ,PERIOD_ID \n" +
                "FROM (\n" +
                "\tSELECT orderdetail.ITEM_ID ITEM_ID\n" +
                "\t\t, gmicuom.i2uom_cv(orderdetail.ITEM_ID,0,orderdetail.ORDER_UM1,orderdetail.ORDER_QTY1, itemmaster.ITEM_UM) ORDER_QUANTITY\n" +
                "\t\t, orderdetail.FROM_WHSE WAREHOUSE_SHIPPED_FROM\n" +
                "\t\t, DECODE(orderdetail.BASE_CURRENCY,orderdetail.BILLING_CURRENCY, orderdetail.EXTENDED_PRICE\n" +
                "\t\t\t, Decode(orderdetail.mul_div_sign,0,orderdetail.EXTENDED_PRICE*orderdetail.EXCHANGE_RATE, orderdetail.EXTENDED_PRICE/orderdetail.EXCHANGE_RATE)\n" +
                "\t\t\t) EXTENDED_PRICE_BASE_CURRENCY\n" +
                "\t\t, pmi_sales_pkg.pmisa_get_charge(orderdetail.order_id,orderdetail.line_id,orderdetail.extended_price\n" +
                "\t\t\t, orderdetail.billing_currency,orderdetail.BASE_CURRENCY, orderdetail.EXCHANGE_RATE,orderdetail.mul_div_sign) CHARGES_BASE_CURRENCY\n" +
                "\t\t\t, salesorg.CO_CODE SALES_COMPANY, orderhdr.ORGN_CODE SALES_ORGANIZATION, glcldr.period_start_date PERIOD_START_DATE\n" +
                "\t\t\t, ou.organization_id operating_unit_id, glcldr.year_id, glcldr.quarter_id, glcldr.period_id \n" +
                "\tFROM PMI_GL_TIME_V glcldr, GL_PLCY_MST glplcy, OP_ORDR_HDR orderhdr, OP_ORDR_DTL orderdetail, ic_item_mst_b itemmaster, IC_WHSE_MST shippingwarehouse, SY_ORGN_MST salesorg, HR_OPERATING_UNITS ou \n" +
                "\tWHERE orderhdr.order_id = orderdetail.order_id \n" +
                "\t\tAND orderhdr.orgn_code = salesorg.orgn_code \n" +
                "\t\tAND orderdetail.from_whse=shippingwarehouse.whse_code \n" +
                "\t\tAND glplcy.co_code = salesorg.co_code \n" +
                "\t\tAND glcldr.set_of_books_name=glplcy.set_of_books_name \n" +
                "\t\tAND trunc(orderdetail.ACTUAL_SHIPDATE) between glcldr.period_start_date and glcldr.period_end_date \n" +
                "\t\tAND orderdetail.LINE_STATUS>=20 \n" +
                "\t\tAND ou.organization_id = glplcy.org_id \n" +
                "\t\tAND itemmaster.item_id = orderdetail.item_id \n" +
                "\t\tAND 'TRUE' = PMI_SECURITY_PKG.show_record(salesorg.orgn_code) \n" +
                "\n" +
                "\tUNION ALL \n" +
                "\n" +
                "\tSELECT itemmaster.ITEM_ID ITEM_ID, gmicuom.i2uom_cv(itemmaster.ITEM_ID, 0, orderdetail.ORDER_QUANTITY_UOM, orderdetail.ORDERED_QUANTITY, itemmaster.ITEM_UM) ORDER_QUANTITY\n" +
                "\t\t, shippingwarehouse.whse_code WAREHOUSE_SHIPPED_FROM\n" +
                "\t\t, DECODE(SOB.CURRENCY_CODE, orderhdr.TRANSACTIONAL_CURR_CODE, orderdetail.UNIT_LIST_PRICE * ORDERED_QUANTITY, orderdetail.UNIT_LIST_PRICE * NVL(orderhdr.CONVERSION_RATE, PMI_COMMON_PKG.PMICO_GET_MULCURR_AMT(orderhdr.TRANSACTIONAL_CURR_CODE, sob.CURRENCY_CODE,orderdetail.ACTUAL_SHIPMENT_DATE, 1)) * ORDERED_QUANTITY) EXTENDED_PRICE_BASE_CURRENCY, (orderdetail.UNIT_LIST_PRICE - orderdetail.UNIT_SELLING_PRICE) * orderdetail.ORDERED_QUANTITY CHARGES_BASE_CURRENCY, NULL SALES_COMPANY, NULL SALES_ORGANIZATION, glcldr.period_start_date PERIOD_START_DATE, ou.organization_id OPERATING_UNIT_ID, glcldr.YEAR_ID, glcldr.QUARTER_ID, glcldr.PERIOD_ID \n" +
                "\tFROM oe_order_headers_all orderhdr, oe_order_lines_all orderdetail, oe_system_parameters_all params, mtl_system_items_b msi, ic_item_mst_b itemmaster, hr_operating_units ou, gl_sets_of_books sob, ic_whse_mst shippingwarehouse, PMI_GL_TIME_V glcldr \n" +
                "\tWHERE orderdetail.header_id = orderhdr.header_id \n" +
                "\t\tAND params.org_id = orderhdr.org_id \n" +
                "\t\tAND msi.organization_id = params.master_organization_id \n" +
                "\t\tAND msi.inventory_item_id = orderdetail.inventory_item_id \n" +
                "\t\tAND itemmaster.item_no(+) = msi.segment1 \n" +
                "\t\tAND ou.organization_id = orderhdr.org_id AND ou.organization_id = orderdetail.org_id \n" +
                "\t\tAND sob.set_of_books_id = ou.set_of_books_id \n" +
                "\t\tAND shippingwarehouse.mtl_organization_id = orderdetail.ship_from_org_id \n" +
                "\t\tAND trunc(orderdetail.ACTUAL_SHIPMENT_DATE) between glcldr.period_start_date and glcldr.period_end_date \n" +
                "\t\tAND sob.period_set_name = glcldr.period_set_name \n" +
                "\t\tand SOB.NAME = glcldr.SET_OF_BOOKS_NAME \n" +
                "\t\tAND HR_SECURITY.SHOW_BIS_RECORD( orderhdr.org_id ) = 'TRUE' \n" +
                ") \n" +
                "GROUP BY ITEM_ID ,WAREHOUSE_SHIPPED_FROM ,SALES_COMPANY ,SALES_ORGANIZATION ,PERIOD_START_DATE ,OPERATING_UNIT_ID ,YEAR_ID ,QUARTER_ID ,PERIOD_ID with read only"
               ;

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE VIEW \"APPS\".\"PMI_REP_SALES_DETAILS_V\" (\n" +
                        "\t\"ITEM_ID\", \n" +
                        "\t\"ORDER_QUANTITY\", \n" +
                        "\t\"WAREHOUSE_SHIPPED_FROM\", \n" +
                        "\t\"EXTENDED_PRICE_BASE_CURRENCY\", \n" +
                        "\t\"CHARGES_BASE_CURRENCY\", \n" +
                        "\t\"SALES_COMPANY\", \n" +
                        "\t\"SALES_ORGANIZATION\", \n" +
                        "\t\"PERIOD_START_DATE\", \n" +
                        "\t\"OPERATING_UNIT_ID\", \n" +
                        "\t\"YEAR_ID\", \n" +
                        "\t\"QUARTER_ID\", \n" +
                        "\t\"PERIOD_ID\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT ITEM_ID, SUM(ORDER_QUANTITY) AS ORDER_QUANTITY, WAREHOUSE_SHIPPED_FROM\n" +
                        "\t, SUM(EXTENDED_PRICE_BASE_CURRENCY) AS EXTENDED_PRICE_BASE_CURRENCY, SUM(CHARGES_BASE_CURRENCY) AS CHARGES_BASE_CURRENCY\n" +
                        "\t, SALES_COMPANY, SALES_ORGANIZATION, PERIOD_START_DATE, OPERATING_UNIT_ID, YEAR_ID\n" +
                        "\t, QUARTER_ID, PERIOD_ID\n" +
                        "FROM (\n" +
                        "\tSELECT orderdetail.ITEM_ID AS ITEM_ID\n" +
                        "\t\t, gmicuom.i2uom_cv(orderdetail.ITEM_ID, 0, orderdetail.ORDER_UM1, orderdetail.ORDER_QTY1, itemmaster.ITEM_UM) AS ORDER_QUANTITY\n" +
                        "\t\t, orderdetail.FROM_WHSE AS WAREHOUSE_SHIPPED_FROM\n" +
                        "\t\t, DECODE(orderdetail.BASE_CURRENCY, orderdetail.BILLING_CURRENCY, orderdetail.EXTENDED_PRICE, Decode(orderdetail.mul_div_sign, 0, orderdetail.EXTENDED_PRICE * orderdetail.EXCHANGE_RATE, orderdetail.EXTENDED_PRICE / orderdetail.EXCHANGE_RATE)) AS EXTENDED_PRICE_BASE_CURRENCY\n" +
                        "\t\t, pmi_sales_pkg.pmisa_get_charge(orderdetail.order_id, orderdetail.line_id, orderdetail.extended_price, orderdetail.billing_currency, orderdetail.BASE_CURRENCY, orderdetail.EXCHANGE_RATE, orderdetail.mul_div_sign) AS CHARGES_BASE_CURRENCY\n" +
                        "\t\t, salesorg.CO_CODE AS SALES_COMPANY, orderhdr.ORGN_CODE AS SALES_ORGANIZATION, glcldr.period_start_date AS PERIOD_START_DATE, ou.organization_id AS operating_unit_id, glcldr.year_id\n" +
                        "\t\t, glcldr.quarter_id, glcldr.period_id\n" +
                        "\tFROM PMI_GL_TIME_V glcldr, GL_PLCY_MST glplcy, OP_ORDR_HDR orderhdr, OP_ORDR_DTL orderdetail, ic_item_mst_b itemmaster, IC_WHSE_MST shippingwarehouse, SY_ORGN_MST salesorg, HR_OPERATING_UNITS ou\n" +
                        "\tWHERE orderhdr.order_id = orderdetail.order_id\n" +
                        "\t\tAND orderhdr.orgn_code = salesorg.orgn_code\n" +
                        "\t\tAND orderdetail.from_whse = shippingwarehouse.whse_code\n" +
                        "\t\tAND glplcy.co_code = salesorg.co_code\n" +
                        "\t\tAND glcldr.set_of_books_name = glplcy.set_of_books_name\n" +
                        "\t\tAND trunc(orderdetail.ACTUAL_SHIPDATE) BETWEEN glcldr.period_start_date AND glcldr.period_end_date\n" +
                        "\t\tAND orderdetail.LINE_STATUS >= 20\n" +
                        "\t\tAND ou.organization_id = glplcy.org_id\n" +
                        "\t\tAND itemmaster.item_id = orderdetail.item_id\n" +
                        "\t\tAND 'TRUE' = PMI_SECURITY_PKG.show_record(salesorg.orgn_code)\n" +
                        "\tUNION ALL\n" +
                        "\tSELECT itemmaster.ITEM_ID AS ITEM_ID\n" +
                        "\t\t, gmicuom.i2uom_cv(itemmaster.ITEM_ID, 0, orderdetail.ORDER_QUANTITY_UOM, orderdetail.ORDERED_QUANTITY, itemmaster.ITEM_UM) AS ORDER_QUANTITY\n" +
                        "\t\t, shippingwarehouse.whse_code AS WAREHOUSE_SHIPPED_FROM\n" +
                        "\t\t, DECODE(SOB.CURRENCY_CODE, orderhdr.TRANSACTIONAL_CURR_CODE, orderdetail.UNIT_LIST_PRICE * ORDERED_QUANTITY, orderdetail.UNIT_LIST_PRICE * NVL(orderhdr.CONVERSION_RATE, PMI_COMMON_PKG.PMICO_GET_MULCURR_AMT(orderhdr.TRANSACTIONAL_CURR_CODE, sob.CURRENCY_CODE, orderdetail.ACTUAL_SHIPMENT_DATE, 1)) * ORDERED_QUANTITY) AS EXTENDED_PRICE_BASE_CURRENCY\n" +
                        "\t\t, (orderdetail.UNIT_LIST_PRICE - orderdetail.UNIT_SELLING_PRICE) * orderdetail.ORDERED_QUANTITY AS CHARGES_BASE_CURRENCY, NULL AS SALES_COMPANY\n" +
                        "\t\t, NULL AS SALES_ORGANIZATION, glcldr.period_start_date AS PERIOD_START_DATE, ou.organization_id AS OPERATING_UNIT_ID, glcldr.YEAR_ID, glcldr.QUARTER_ID\n" +
                        "\t\t, glcldr.PERIOD_ID\n" +
                        "\tFROM oe_order_headers_all orderhdr, oe_order_lines_all orderdetail, oe_system_parameters_all params, mtl_system_items_b msi, ic_item_mst_b itemmaster, hr_operating_units ou, gl_sets_of_books sob, ic_whse_mst shippingwarehouse, PMI_GL_TIME_V glcldr\n" +
                        "\tWHERE orderdetail.header_id = orderhdr.header_id\n" +
                        "\t\tAND params.org_id = orderhdr.org_id\n" +
                        "\t\tAND msi.organization_id = params.master_organization_id\n" +
                        "\t\tAND msi.inventory_item_id = orderdetail.inventory_item_id\n" +
                        "\t\tAND itemmaster.item_no(+) = msi.segment1\n" +
                        "\t\tAND ou.organization_id = orderhdr.org_id\n" +
                        "\t\tAND ou.organization_id = orderdetail.org_id\n" +
                        "\t\tAND sob.set_of_books_id = ou.set_of_books_id\n" +
                        "\t\tAND shippingwarehouse.mtl_organization_id = orderdetail.ship_from_org_id\n" +
                        "\t\tAND trunc(orderdetail.ACTUAL_SHIPMENT_DATE) BETWEEN glcldr.period_start_date AND glcldr.period_end_date\n" +
                        "\t\tAND sob.period_set_name = glcldr.period_set_name\n" +
                        "\t\tAND SOB.NAME = glcldr.SET_OF_BOOKS_NAME\n" +
                        "\t\tAND HR_SECURITY.SHOW_BIS_RECORD(orderhdr.org_id) = 'TRUE'\n" +
                        ")\n" +
                        "GROUP BY ITEM_ID, WAREHOUSE_SHIPPED_FROM, SALES_COMPANY, SALES_ORGANIZATION, PERIOD_START_DATE, OPERATING_UNIT_ID, YEAR_ID, QUARTER_ID, PERIOD_ID\n" +
                        "WITH READ ONLY",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(13, visitor.getTables().size());

        assertEquals(57, visitor.getColumns().size());

        assertTrue(visitor.getColumns().contains(new TableStat.Column("OP_ORDR_DTL", "ITEM_ID")));
    }
}
