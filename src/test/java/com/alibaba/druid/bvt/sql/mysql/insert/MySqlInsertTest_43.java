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
package com.alibaba.druid.bvt.sql.mysql.insert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class MySqlInsertTest_43 extends TestCase {

    public void test_insert_0() throws Exception {
        String sql = "insert into adl_indication_ums_warehouse_ebax_a (sys_pk,sys_ds,sys_biztime,sys_gmt_modified,business_type,parcelled_order_num,warehouse_id)\n" +
                "select\n" +
                "    concat(20190115,'_',ifnull(businessType, '#null#') ,'_',ifnull(warehouseId, '#null#') ) as sys_pk,\n" +
                "    cast(20190115 as bigint) as sys_ds,\n" +
                "    str_to_date('2019-01-15 21:51:00', '%Y-%m-%d %H:%i:%s') as sys_biztime,\n" +
                "    now() as sys_gmt_modified,\n" +
                "    cast(businessType as varchar) as business_type,\n" +
                "    cast(parcelledOrderNum as bigint) as parcelled_order_num,\n" +
                "    cast(warehouseId as varchar) as warehouse_id\n" +
                "from (\n" +
                "\n" +
                "\n" +
                "select  cast(t1.warehouse_id as varchar) as warehouseId\n" +
                "      , t2.businessType\n" +
                "      , count(1) as parcelledOrderNum\n" +
                "from sales_order t1\n" +
                "join (\n" +
                "           select t2_1.warehouse_id\n" +
                "                , t2_1.external_batch_code\n" +
                "                ,(case\n" +
                "                   when t2_2.operation_type = 2 or t2_2.operation_type is null then '2'\n" +
                "                   when t2_2.operation_type = 3 then '800'\n" +
                "                   when t2_2.operation_type = 1 then '900'\n" +
                "                  end) as businessType\n" +
                "             from batch_order t2_1\n" +
                "             join wave_order t2_2\n" +
                "               on t2_1.wave_id = t2_2.id\n" +
                "              and t2_1.warehouse_id = t2_2.warehouse_id\n" +
                "              and t2_2.wave_status in (5,6)\n" +
                "              and t2_1.ds >= (201901 - 1)\n" +
                "              and t2_2.ds >= (201901 - 1)\n" +
                "              and t2_1.gmt_create >= date_add(now(), interval -3 day)\n" +
                "              and t2_1.gmt_create <= now()\n" +
                "              and t2_2.gmt_create >= date_add(now(), interval -3 day)\n" +
                "              and t2_2.gmt_create <= now()\n" +
                "\n" +
                ") t2 on t1.out_batch_code = t2.external_batch_code\n" +
                " and t1.warehouse_id = t2.warehouse_id\n" +
                " and t1.out_operation_mode = 3\n" +
                " and t1.gmt_create >= CURDATE()\n" +
                " and t1.gmt_create <= now()\n" +
                " and t1.ds >= 201901\n" +
                " and t2.businessType is not null\n" +
                "group by t1.warehouse_id, t2.businessType\n" +
                ") a\n";

        MySqlStatementParser parser = new MySqlStatementParser(sql, false, true);
        parser.config(SQLParserFeature.KeepInsertValueClauseOriginalString, true);

        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        MySqlInsertStatement insertStmt = (MySqlInsertStatement) stmt;
        assertEquals("INSERT INTO adl_indication_ums_warehouse_ebax_a (sys_pk, sys_ds, sys_biztime, sys_gmt_modified, business_type\n" +
                "\t, parcelled_order_num, warehouse_id)\n" +
                "SELECT concat(20190115, '_', ifnull(businessType, '#null#'), '_', ifnull(warehouseId, '#null#')) AS sys_pk\n" +
                "\t, CAST(20190115 AS bigint) AS sys_ds, str_to_date('2019-01-15 21:51:00', '%Y-%m-%d %H:%i:%s') AS sys_biztime, now() AS sys_gmt_modified\n" +
                "\t, CAST(businessType AS varchar) AS business_type, CAST(parcelledOrderNum AS bigint) AS parcelled_order_num, CAST(warehouseId AS varchar) AS warehouse_id\n" +
                "FROM (\n" +
                "\tSELECT CAST(t1.warehouse_id AS varchar) AS warehouseId, t2.businessType, count(1) AS parcelledOrderNum\n" +
                "\tFROM sales_order t1\n" +
                "\t\tJOIN (\n" +
                "\t\t\tSELECT t2_1.warehouse_id, t2_1.external_batch_code\n" +
                "\t\t\t\t, CASE \n" +
                "\t\t\t\t\tWHEN t2_2.operation_type = 2\n" +
                "\t\t\t\t\tOR t2_2.operation_type IS NULL THEN '2'\n" +
                "\t\t\t\t\tWHEN t2_2.operation_type = 3 THEN '800'\n" +
                "\t\t\t\t\tWHEN t2_2.operation_type = 1 THEN '900'\n" +
                "\t\t\t\tEND AS businessType\n" +
                "\t\t\tFROM batch_order t2_1\n" +
                "\t\t\t\tJOIN wave_order t2_2\n" +
                "\t\t\t\tON t2_1.wave_id = t2_2.id\n" +
                "\t\t\t\t\tAND t2_1.warehouse_id = t2_2.warehouse_id\n" +
                "\t\t\t\t\tAND t2_2.wave_status IN (5, 6)\n" +
                "\t\t\t\t\tAND t2_1.ds >= 201901 - 1\n" +
                "\t\t\t\t\tAND t2_2.ds >= 201901 - 1\n" +
                "\t\t\t\t\tAND t2_1.gmt_create >= date_add(now(), INTERVAL -3 DAY)\n" +
                "\t\t\t\t\tAND t2_1.gmt_create <= now()\n" +
                "\t\t\t\t\tAND t2_2.gmt_create >= date_add(now(), INTERVAL -3 DAY)\n" +
                "\t\t\t\t\tAND t2_2.gmt_create <= now()\n" +
                "\t\t) t2\n" +
                "\t\tON t1.out_batch_code = t2.external_batch_code\n" +
                "\t\t\tAND t1.warehouse_id = t2.warehouse_id\n" +
                "\t\t\tAND t1.out_operation_mode = 3\n" +
                "\t\t\tAND t1.gmt_create >= CURDATE()\n" +
                "\t\t\tAND t1.gmt_create <= now()\n" +
                "\t\t\tAND t1.ds >= 201901\n" +
                "\t\t\tAND t2.businessType IS NOT NULL\n" +
                "\tGROUP BY t1.warehouse_id, t2.businessType\n" +
                ") a", insertStmt.toString());

        ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, null, null);
    }

}
