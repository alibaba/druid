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
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest78 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "WITH resulttable AS (\n" +
                        "    SELECT DISTINCT\n" +
                        "        tcc_cust.get_visit_cust_name(h.visit_id) AS cust_name,\n" +
                        "        h.visit_id,\n" +
                        "        h.visit_code,\n" +
                        "        h.visit_title,\n" +
                        "        h.visit_start_date AS visitstsrtdate,\n" +
                        "        TO_CHAR(\n" +
                        "            h.visit_end_date,\n" +
                        "            'YYYY-MM-DD'\n" +
                        "        ) AS visit_end_date,\n" +
                        "        h.master_dept_id,\n" +
                        "        h.area_id,\n" +
                        "        h.creation_time,\n" +
                        "        h.visit_mode,\n" +
                        "        h.track_flag,\n" +
                        "        h.customer_id,\n" +
                        "        h.visit_state,\n" +
                        "        cust.level_,\n" +
                        "        h.approve_state,\n" +
                        "        h.evamemo,\n" +
                        "        h.approver_leader AS approveby,\n" +
                        "        h.created_by\n" +
                        "    FROM\n" +
                        "        tcc_cust.tcc_cust_visit_header h,\n" +
                        "        tcc_cust.tcc_cust_visit_join custjoin,\n" +
                        "        tcc_cust.tcc_cust_visit_join ourjion,\n" +
                        "        tcc_cust.tcc_cust_person_info person,\n" +
                        "        tcc_cust.tcc_cust_customer cust\n" +
                        "    WHERE\n" +
                        "            h.visit_id = custjoin.visit_id (+)\n" +
                        "        AND\n" +
                        "            custjoin.join_id = person.id (+)\n" +
                        "        AND\n" +
                        "            custjoin.join_type (+) =?\n" +
                        "        AND\n" +
                        "            h.visit_id = ourjion.visit_id (+)\n" +
                        "        AND\n" +
                        "            ourjion.join_type (+) =?\n" +
                        "        AND\n" +
                        "            h.customer_id = cust.id (+)\n" +
                        "        AND\n" +
                        "            h.enable_flag =?\n" +
                        "        AND\n" +
                        "            custjoin.enable_flag (+) =?\n" +
                        "        AND\n" +
                        "            ourjion.enable_flag (+) =?\n" +
                        "        AND\n" +
                        "            h.parent_visit_id =:1\n" +
                        "    UNION\n" +
                        "    SELECT DISTINCT\n" +
                        "        tcc_cust.get_visit_cust_name(h.visit_id) AS cust_name,\n" +
                        "        h.visit_id,\n" +
                        "        h.visit_code,\n" +
                        "        h.visit_title,\n" +
                        "        h.visit_start_date AS visitstsrtdate,\n" +
                        "        TO_CHAR(\n" +
                        "            h.visit_end_date,\n" +
                        "            'YYYY-MM-DD'\n" +
                        "        ) AS visit_end_date,\n" +
                        "        h.master_dept_id,\n" +
                        "        h.area_id,\n" +
                        "        h.creation_time,\n" +
                        "        h.visit_mode,\n" +
                        "        h.track_flag,\n" +
                        "        h.customer_id,\n" +
                        "        h.visit_state,\n" +
                        "        cust.level_,\n" +
                        "        h.approve_state,\n" +
                        "        h.evamemo,\n" +
                        "        h.approver_leader AS approveby,\n" +
                        "        h.created_by\n" +
                        "    FROM\n" +
                        "        tcc_cust.tcc_cust_visit_header h,\n" +
                        "        tcc_cust.tcc_cust_visit_join custjoin,\n" +
                        "        tcc_cust.tcc_cust_visit_join ourjion,\n" +
                        "        tcc_cust.tcc_cust_person_info person,\n" +
                        "        tcc_cust.tcc_cust_customer cust\n" +
                        "    WHERE\n" +
                        "            h.visit_id = custjoin.visit_id (+)\n" +
                        "        AND\n" +
                        "            custjoin.join_id = person.id (+)\n" +
                        "        AND\n" +
                        "            custjoin.join_type (+) =?\n" +
                        "        AND\n" +
                        "            h.visit_id = ourjion.visit_id (+)\n" +
                        "        AND\n" +
                        "            ourjion.join_type (+) =?\n" +
                        "        AND\n" +
                        "            h.customer_id = cust.id (+)\n" +
                        "        AND\n" +
                        "            h.enable_flag =?\n" +
                        "        AND\n" +
                        "            custjoin.enable_flag (+) =?\n" +
                        "        AND\n" +
                        "            ourjion.enable_flag (+) =?\n" +
                        "        AND\n" +
                        "            person.id IN (\n" +
                        "                SELECT\n" +
                        "                    *\n" +
                        "                FROM\n" +
                        "                    (\n" +
                        "                        SELECT DISTINCT\n" +
                        "                            t.id AS personid\n" +
                        "                        FROM\n" +
                        "                            tcc_cust.tcc_cust_person_info t,\n" +
                        "                            tcc_cust.tcc_cust_customer cust\n" +
                        "                        WHERE\n" +
                        "                                t.enable_flag =?\n" +
                        "                            AND\n" +
                        "                                cust.enable_flag =?\n" +
                        "                            AND (\n" +
                        "                                    t.is_temp =?\n" +
                        "                                OR\n" +
                        "                                    t.is_temp IS NULL\n" +
                        "                            ) AND\n" +
                        "                                cust.id = t.customer_id\n" +
                        "                            AND\n" +
                        "                                cust.id IN (\n" +
                        "                                    SELECT DISTINCT\n" +
                        "                                        customer_id\n" +
                        "                                    FROM\n" +
                        "                                        tcc_cust.tcc_cust_customer_power tccp\n" +
                        "                                    WHERE\n" +
                        "                                            tccp.employeeid =?\n" +
                        "                                        AND\n" +
                        "                                            tccp.enable_flag =?\n" +
                        "                                )\n" +
                        "                        UNION\n" +
                        "                        SELECT DISTINCT\n" +
                        "                            t.id AS personid\n" +
                        "                        FROM\n" +
                        "                            tcc_cust.tcc_cust_person_info t,\n" +
                        "                            tcc_cust.tcc_cust_customer cust\n" +
                        "                        WHERE\n" +
                        "                                t.enable_flag =?\n" +
                        "                            AND\n" +
                        "                                cust.enable_flag =?\n" +
                        "                            AND (\n" +
                        "                                    t.is_temp =?\n" +
                        "                                OR\n" +
                        "                                    t.is_temp IS NULL\n" +
                        "                            ) AND\n" +
                        "                                cust.id = t.customer_id\n" +
                        "                            AND\n" +
                        "                                cust.id IN (\n" +
                        "                                    SELECT DISTINCT\n" +
                        "                                        ucoa.cust_org_id\n" +
                        "                                    FROM\n" +
                        "                                        tcc_fnd.tcc_fnd_userorg_cust_org_auth ucoa,\n" +
                        "                                        tcc_fnd.tcc_fnd_emplyees eep\n" +
                        "                                    WHERE\n" +
                        "                                            ucoa.user_org_id = eep.dept_id\n" +
                        "                                        AND\n" +
                        "                                            eep.emplyee_id =?\n" +
                        "                                        AND\n" +
                        "                                            ucoa.enable_flag =?\n" +
                        "                                )\n" +
                        "                        UNION\n" +
                        "                        SELECT DISTINCT\n" +
                        "                            t.id AS personid\n" +
                        "                        FROM\n" +
                        "                            tcc_cust.tcc_cust_person_info t,\n" +
                        "                            tcc_cust.tcc_cust_customer cust\n" +
                        "                        WHERE\n" +
                        "                                t.enable_flag =?\n" +
                        "                            AND\n" +
                        "                                cust.enable_flag =?\n" +
                        "                            AND (\n" +
                        "                                    t.is_temp =?\n" +
                        "                                OR\n" +
                        "                                    t.is_temp IS NULL\n" +
                        "                            ) AND\n" +
                        "                                cust.id = t.customer_id\n" +
                        "                            AND\n" +
                        "                                t.id IN (\n" +
                        "                                    SELECT DISTINCT\n" +
                        "                                        euca.cust_person_id\n" +
                        "                                    FROM\n" +
                        "                                        tcc_fnd.tcc_fnd_user_contact_auth euca\n" +
                        "                                    WHERE\n" +
                        "                                            euca.user_id =?\n" +
                        "                                        AND\n" +
                        "                                            euca.enable_flag =?\n" +
                        "                                )\n" +
                        "                        UNION\n" +
                        "                        SELECT DISTINCT\n" +
                        "                            t.id AS personid\n" +
                        "                        FROM\n" +
                        "                            tcc_cust.tcc_cust_person_info t\n" +
                        "                        WHERE\n" +
                        "                                t.enable_flag =?\n" +
                        "                            AND (\n" +
                        "                                    t.is_temp =?\n" +
                        "                                OR\n" +
                        "                                    t.is_temp IS NULL\n" +
                        "                            ) AND\n" +
                        "                                t.created_by =?\n" +
                        "                    ) temp\n" +
                        "            )\n" +
                        "        AND\n" +
                        "            h.parent_visit_id =:2\n" +
                        "    ORDER BY\n" +
                        "        creation_time DESC,\n" +
                        "        visitstsrtdate\n" +
                        ") SELECT\n" +
                        "    cust_name,\n" +
                        "    visit_id,\n" +
                        "    visit_code,\n" +
                        "    visit_title,\n" +
                        "    visit_start_date,\n" +
                        "    visit_end_date,\n" +
                        "    master_dept_id,\n" +
                        "    area_id,\n" +
                        "    creation_time,\n" +
                        "    visit_mode,\n" +
                        "    track_flag,\n" +
                        "    customer_id,\n" +
                        "    visit_state,\n" +
                        "    tcc_cust.get_join_info(\n" +
                        "        lasttab.visit_id,\n" +
                        "        ?\n" +
                        "    ) AS person_name,\n" +
                        "    level_,\n" +
                        "    approve_state,\n" +
                        "    evamemo,\n" +
                        "    approveby,\n" +
                        "    (\n" +
                        "        SELECT\n" +
                        "            e.nameno\n" +
                        "        FROM\n" +
                        "            tcc_fnd.tcc_fnd_emplyees e\n" +
                        "        WHERE\n" +
                        "            e.emplyee_id = lasttab.created_by\n" +
                        "    ) AS created_by,\n" +
                        "    area_name,\n" +
                        "    master_dept_name,\n" +
                        "    resultcount,\n" +
                        "    resultnums\n" +
                        "FROM\n" +
                        "    (\n" +
                        "        SELECT\n" +
                        "            result.*,\n" +
                        "            TO_CHAR(\n" +
                        "                result.visitstsrtdate,\n" +
                        "                'YYYY-MM-DD'\n" +
                        "            ) AS visit_start_date,\n" +
                        "            ROWNUM AS resultnums\n" +
                        "        FROM\n" +
                        "            (\n" +
                        "                SELECT\n" +
                        "                    *\n" +
                        "                FROM\n" +
                        "                    (\n" +
                        "                        SELECT\n" +
                        "                            resulttable.*,\n" +
                        "                            viewtable.*,\n" +
                        "                            area.full_area_name AS area_name,\n" +
                        "                            d.full_name AS master_dept_name\n" +
                        "                        FROM\n" +
                        "                            resulttable,\n" +
                        "                            (\n" +
                        "                                SELECT\n" +
                        "                                    COUNT(1) AS resultcount\n" +
                        "                                FROM\n" +
                        "                                    resulttable\n" +
                        "                            ) viewtable,\n" +
                        "                            tcc_cust.tcc_cust_area_info_v area,\n" +
                        "                            tcc_fnd.tcc_fnd_depts_v d\n" +
                        "                        WHERE\n" +
                        "                                resulttable.area_id = area.area_id (+)\n" +
                        "                            AND\n" +
                        "                                resulttable.master_dept_id = d.dept_id (+)\n" +
                        "                        ORDER BY\n" +
                        "                            resulttable.creation_time DESC,\n" +
                        "                            resulttable.visitstsrtdate\n" +
                        "                    )\n" +
                        "            ) result\n" +
                        "    ) lasttab\n" +
                        "WHERE\n" +
                        "        resultnums >:3\n" +
                        "    AND\n" +
                        "        resultnums <=:4"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(10, visitor.getTables().size());
        assertEquals(46, visitor.getColumns().size());
        assertEquals(30, visitor.getConditions().size());
        assertEquals(7, visitor.getRelationships().size());
        assertEquals(2, visitor.getOrderByColumns().size());

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("WITH resulttable AS (\n" +
                    "\t\tSELECT DISTINCT tcc_cust.get_visit_cust_name(h.visit_id) AS cust_name, h.visit_id, h.visit_code, h.visit_title\n" +
                    "\t\t\t, h.visit_start_date AS visitstsrtdate, TO_CHAR(h.visit_end_date, 'YYYY-MM-DD') AS visit_end_date, h.master_dept_id\n" +
                    "\t\t\t, h.area_id, h.creation_time, h.visit_mode, h.track_flag, h.customer_id\n" +
                    "\t\t\t, h.visit_state, cust.level_, h.approve_state, h.evamemo, h.approver_leader AS approveby\n" +
                    "\t\t\t, h.created_by\n" +
                    "\t\tFROM tcc_cust.tcc_cust_visit_header h, tcc_cust.tcc_cust_visit_join custjoin, tcc_cust.tcc_cust_visit_join ourjion, tcc_cust.tcc_cust_person_info person, tcc_cust.tcc_cust_customer cust\n" +
                    "\t\tWHERE h.visit_id = custjoin.visit_id(+)\n" +
                    "\t\t\tAND custjoin.join_id = person.id(+)\n" +
                    "\t\t\tAND custjoin.join_type(+) = ?\n" +
                    "\t\t\tAND h.visit_id = ourjion.visit_id(+)\n" +
                    "\t\t\tAND ourjion.join_type(+) = ?\n" +
                    "\t\t\tAND h.customer_id = cust.id(+)\n" +
                    "\t\t\tAND h.enable_flag = ?\n" +
                    "\t\t\tAND custjoin.enable_flag(+) = ?\n" +
                    "\t\t\tAND ourjion.enable_flag(+) = ?\n" +
                    "\t\t\tAND h.parent_visit_id = :1\n" +
                    "\t\tUNION\n" +
                    "\t\tSELECT DISTINCT tcc_cust.get_visit_cust_name(h.visit_id) AS cust_name, h.visit_id, h.visit_code, h.visit_title\n" +
                    "\t\t\t, h.visit_start_date AS visitstsrtdate, TO_CHAR(h.visit_end_date, 'YYYY-MM-DD') AS visit_end_date, h.master_dept_id\n" +
                    "\t\t\t, h.area_id, h.creation_time, h.visit_mode, h.track_flag, h.customer_id\n" +
                    "\t\t\t, h.visit_state, cust.level_, h.approve_state, h.evamemo, h.approver_leader AS approveby\n" +
                    "\t\t\t, h.created_by\n" +
                    "\t\tFROM tcc_cust.tcc_cust_visit_header h, tcc_cust.tcc_cust_visit_join custjoin, tcc_cust.tcc_cust_visit_join ourjion, tcc_cust.tcc_cust_person_info person, tcc_cust.tcc_cust_customer cust\n" +
                    "\t\tWHERE h.visit_id = custjoin.visit_id(+)\n" +
                    "\t\t\tAND custjoin.join_id = person.id(+)\n" +
                    "\t\t\tAND custjoin.join_type(+) = ?\n" +
                    "\t\t\tAND h.visit_id = ourjion.visit_id(+)\n" +
                    "\t\t\tAND ourjion.join_type(+) = ?\n" +
                    "\t\t\tAND h.customer_id = cust.id(+)\n" +
                    "\t\t\tAND h.enable_flag = ?\n" +
                    "\t\t\tAND custjoin.enable_flag(+) = ?\n" +
                    "\t\t\tAND ourjion.enable_flag(+) = ?\n" +
                    "\t\t\tAND person.id IN (\n" +
                    "\t\t\t\tSELECT *\n" +
                    "\t\t\t\tFROM (\n" +
                    "\t\t\t\t\tSELECT DISTINCT t.id AS personid\n" +
                    "\t\t\t\t\tFROM tcc_cust.tcc_cust_person_info t, tcc_cust.tcc_cust_customer cust\n" +
                    "\t\t\t\t\tWHERE t.enable_flag = ?\n" +
                    "\t\t\t\t\t\tAND cust.enable_flag = ?\n" +
                    "\t\t\t\t\t\tAND (t.is_temp = ?\n" +
                    "\t\t\t\t\t\t\tOR t.is_temp IS NULL)\n" +
                    "\t\t\t\t\t\tAND cust.id = t.customer_id\n" +
                    "\t\t\t\t\t\tAND cust.id IN (\n" +
                    "\t\t\t\t\t\t\tSELECT DISTINCT customer_id\n" +
                    "\t\t\t\t\t\t\tFROM tcc_cust.tcc_cust_customer_power tccp\n" +
                    "\t\t\t\t\t\t\tWHERE tccp.employeeid = ?\n" +
                    "\t\t\t\t\t\t\t\tAND tccp.enable_flag = ?\n" +
                    "\t\t\t\t\t\t)\n" +
                    "\t\t\t\t\tUNION\n" +
                    "\t\t\t\t\tSELECT DISTINCT t.id AS personid\n" +
                    "\t\t\t\t\tFROM tcc_cust.tcc_cust_person_info t, tcc_cust.tcc_cust_customer cust\n" +
                    "\t\t\t\t\tWHERE t.enable_flag = ?\n" +
                    "\t\t\t\t\t\tAND cust.enable_flag = ?\n" +
                    "\t\t\t\t\t\tAND (t.is_temp = ?\n" +
                    "\t\t\t\t\t\t\tOR t.is_temp IS NULL)\n" +
                    "\t\t\t\t\t\tAND cust.id = t.customer_id\n" +
                    "\t\t\t\t\t\tAND cust.id IN (\n" +
                    "\t\t\t\t\t\t\tSELECT DISTINCT ucoa.cust_org_id\n" +
                    "\t\t\t\t\t\t\tFROM tcc_fnd.tcc_fnd_userorg_cust_org_auth ucoa, tcc_fnd.tcc_fnd_emplyees eep\n" +
                    "\t\t\t\t\t\t\tWHERE ucoa.user_org_id = eep.dept_id\n" +
                    "\t\t\t\t\t\t\t\tAND eep.emplyee_id = ?\n" +
                    "\t\t\t\t\t\t\t\tAND ucoa.enable_flag = ?\n" +
                    "\t\t\t\t\t\t)\n" +
                    "\t\t\t\t\tUNION\n" +
                    "\t\t\t\t\tSELECT DISTINCT t.id AS personid\n" +
                    "\t\t\t\t\tFROM tcc_cust.tcc_cust_person_info t, tcc_cust.tcc_cust_customer cust\n" +
                    "\t\t\t\t\tWHERE t.enable_flag = ?\n" +
                    "\t\t\t\t\t\tAND cust.enable_flag = ?\n" +
                    "\t\t\t\t\t\tAND (t.is_temp = ?\n" +
                    "\t\t\t\t\t\t\tOR t.is_temp IS NULL)\n" +
                    "\t\t\t\t\t\tAND cust.id = t.customer_id\n" +
                    "\t\t\t\t\t\tAND t.id IN (\n" +
                    "\t\t\t\t\t\t\tSELECT DISTINCT euca.cust_person_id\n" +
                    "\t\t\t\t\t\t\tFROM tcc_fnd.tcc_fnd_user_contact_auth euca\n" +
                    "\t\t\t\t\t\t\tWHERE euca.user_id = ?\n" +
                    "\t\t\t\t\t\t\t\tAND euca.enable_flag = ?\n" +
                    "\t\t\t\t\t\t)\n" +
                    "\t\t\t\t\tUNION\n" +
                    "\t\t\t\t\tSELECT DISTINCT t.id AS personid\n" +
                    "\t\t\t\t\tFROM tcc_cust.tcc_cust_person_info t\n" +
                    "\t\t\t\t\tWHERE t.enable_flag = ?\n" +
                    "\t\t\t\t\t\tAND (t.is_temp = ?\n" +
                    "\t\t\t\t\t\t\tOR t.is_temp IS NULL)\n" +
                    "\t\t\t\t\t\tAND t.created_by = ?\n" +
                    "\t\t\t\t) temp\n" +
                    "\t\t\t)\n" +
                    "\t\t\tAND h.parent_visit_id = :2\n" +
                    "\t\tORDER BY creation_time DESC, visitstsrtdate\n" +
                    "\t)\n" +
                    "SELECT cust_name, visit_id, visit_code, visit_title, visit_start_date\n" +
                    "\t, visit_end_date, master_dept_id, area_id, creation_time, visit_mode\n" +
                    "\t, track_flag, customer_id, visit_state\n" +
                    "\t, tcc_cust.get_join_info(lasttab.visit_id, ?) AS person_name, level_\n" +
                    "\t, approve_state, evamemo, approveby\n" +
                    "\t, (\n" +
                    "\t\tSELECT e.nameno\n" +
                    "\t\tFROM tcc_fnd.tcc_fnd_emplyees e\n" +
                    "\t\tWHERE e.emplyee_id = lasttab.created_by\n" +
                    "\t) AS created_by, area_name, master_dept_name, resultcount, resultnums\n" +
                    "FROM (\n" +
                    "\tSELECT result.*, TO_CHAR(result.visitstsrtdate, 'YYYY-MM-DD') AS visit_start_date, ROWNUM AS resultnums\n" +
                    "\tFROM (\n" +
                    "\t\tSELECT *\n" +
                    "\t\tFROM (\n" +
                    "\t\t\tSELECT resulttable.*, viewtable.*, area.full_area_name AS area_name, d.full_name AS master_dept_name\n" +
                    "\t\t\tFROM resulttable, (\n" +
                    "\t\t\t\tSELECT COUNT(1) AS resultcount\n" +
                    "\t\t\t\tFROM resulttable\n" +
                    "\t\t\t) viewtable, tcc_cust.tcc_cust_area_info_v area, tcc_fnd.tcc_fnd_depts_v d\n" +
                    "\t\t\tWHERE resulttable.area_id = area.area_id(+)\n" +
                    "\t\t\t\tAND resulttable.master_dept_id = d.dept_id(+)\n" +
                    "\t\t\tORDER BY resulttable.creation_time DESC, resulttable.visitstsrtdate\n" +
                    "\t\t)\n" +
                    "\t) result\n" +
                    ") lasttab\n" +
                    "WHERE resultnums > :3\n" +
                    "\tAND resultnums <= :4", text);
        }
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
