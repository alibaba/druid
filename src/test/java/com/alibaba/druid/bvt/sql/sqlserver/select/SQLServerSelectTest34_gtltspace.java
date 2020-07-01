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
package com.alibaba.druid.bvt.sql.sqlserver.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class SQLServerSelectTest34_gtltspace extends TestCase {

    public void test_simple() throws Exception {
        String sql = //
                "select count(1) From wms.pack_box_material a,wms.pack_box b where a.pb_id=b.pb_id\n" +
                        "and b.state< >2\n" +
                        "and not exists( select 1 From wms.box_pack_detail c where c.pm_id=a.pm_id )\n" +
                        "and b.box_cid=? "; //

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.SQL_SERVER);

        {
            String text = SQLUtils.toSQLString(stmtList, JdbcConstants.SQL_SERVER);

            assertEquals("SELECT count(1)\n" +
                    "FROM wms.pack_box_material a, wms.pack_box b\n" +
                    "WHERE a.pb_id = b.pb_id\n" +
                    "\tAND b.state <> 2\n" +
                    "\tAND NOT EXISTS (\n" +
                    "\t\tSELECT 1\n" +
                    "\t\tFROM wms.box_pack_detail c\n" +
                    "\t\tWHERE c.pm_id = a.pm_id\n" +
                    "\t)\n" +
                    "\tAND b.box_cid = ?", text);
        }
        {
            String text = SQLUtils.toSQLString(stmtList, JdbcConstants.SQL_SERVER, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);

            assertEquals("select count(1)\n" +
                    "from wms.pack_box_material a, wms.pack_box b\n" +
                    "where a.pb_id = b.pb_id\n" +
                    "\tand b.state <> 2\n" +
                    "\tand not exists (\n" +
                    "\t\tselect 1\n" +
                    "\t\tfrom wms.box_pack_detail c\n" +
                    "\t\twhere c.pm_id = a.pm_id\n" +
                    "\t)\n" +
                    "\tand b.box_cid = ?", text);
        }
    }
}
