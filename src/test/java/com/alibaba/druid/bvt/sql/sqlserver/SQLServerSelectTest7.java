/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class SQLServerSelectTest7 extends TestCase {

    public void test_isEmpty() throws Exception {
        String sql = "with menu_view as(" +
        		"\n            select t.*,1 level from sec_portal_menu t where t.parent_id = ?" +
        		"\n            union all" +
        		"\n            select t.*,level + 1 from sec_portal_menu t , menu_view x where t.parent_id = x.menu_id" +
        		"\n        )" +
        		"\n        select t.menu_id \"id\"," +
        		"\n               t.menu_name \"name\"," +
        		"\n               t.parent_id \"pId\"," +
        		"\n               case t.level when 1 then 'true' else 'false' end \"open\"," +
        		"\n               t.link_type \"linkType\"" +
        		"\n                from menu_view t" +
        		"\n                where 1=1" +
        		"\n                and t.deleted = 0" +
        		"\n                --菜单权限控制" +
        		"\n                AND t.link_type in ('simple','link')" +
        		"\n                AND (" +
        		"\n                     EXISTS (" +
        		"\n                        select p.entity_code from sec_role_auth p where p.entity_code = t.menu_id" +
        		"\n                        and p.entity_type = 'menu'" +
        		"\n                        and p.role_id in (" +
        		"\n                            select r.role_code from sec_role_member rm ,sec_role r where rm.entity_type = 'user'" +
        		"\n                            and entity_code = ? --用户ID" +
        		"\n                            and r.role_id = rm.role_id" +
        		"\n                            and r.enabled = 1" +
        		"\n                            and r.deleted = 0" +
        		"\n                        )" +
        		"\n                     )" +
        		"\n                     or '1'= ? --超级管理员账户id" +
        		"\n                     or t.need_control = 0" +
        		"\n                )" +
        		"\n                AND (" +
        		"\n                    t.enabled = 1 or '1'= ? --超级管理员账户id" +
        		"\n                    or t.need_control = 0" +
        		"\n                )" +
        		"\n        order by t.sort_order";

        String expect = "WITH" +
        		"\n\tmenu_view" +
        		"\n\tAS" +
        		"\n\t(" +
        		"\n\t\tSELECT t.*, 1 AS level" +
        		"\n\t\tFROM sec_portal_menu t" +
        		"\n\t\tWHERE t.parent_id = ?" +
        		"\n\t\tUNION ALL" +
        		"\n\t\tSELECT t.*, level + 1" +
        		"\n\t\tFROM sec_portal_menu t, menu_view x" +
        		"\n\t\tWHERE t.parent_id = x.menu_id" +
        		"\n\t)" +
        		"\nSELECT t.menu_id AS \"id\", t.menu_name AS \"name\", t.parent_id AS \"pId\", CASE t.level WHEN 1 THEN 'true' ELSE 'false' END AS \"open\", t.link_type AS \"linkType\"" +
        		"\nFROM menu_view t" +
				"\nWHERE 1 = 1" +
				"\n\tAND t.deleted = 0" +
				"\n\tAND t.link_type IN ('simple', 'link')" +
				"\n\tAND (EXISTS (SELECT p.entity_code" +
				"\n\t\t\tFROM sec_role_auth p" +
				"\n\t\t\tWHERE p.entity_code = t.menu_id" +
				"\n\t\t\t\tAND p.entity_type = 'menu'" +
				"\n\t\t\t\tAND p.role_id IN (SELECT r.role_code" +
				"\n\t\t\t\t\tFROM sec_role_member rm, sec_role r" +
				"\n\t\t\t\t\tWHERE rm.entity_type = 'user'" +
				"\n\t\t\t\t\t\tAND entity_code = ?" +
				"\n\t\t\t\t\t\tAND r.role_id = rm.role_id" +
				"\n\t\t\t\t\t\tAND r.enabled = 1" +
				"\n\t\t\t\t\t\tAND r.deleted = 0))" +
				"\n\t\tOR '1' = ?" +
				"\n\t\tOR t.need_control = 0)" +
				"\n\tAND (t.enabled = 1" +
				"\n\t\tOR '1' = ?" +
				"\n\t\tOR t.need_control = 0)" +
				"\nORDER BY t.sort_order";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
