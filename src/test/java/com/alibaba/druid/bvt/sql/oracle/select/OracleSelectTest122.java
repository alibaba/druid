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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;


public class OracleSelectTest122 extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "select A.gnmk_ljmc , A.mklx_dm , A.gnmb_dm , B.jbdm , B.sdate , B.edate \n" +
                "from ( \n" +
                "\tselect model.gnmk_ljmc as gnmk_ljmc , model.mklx_dm as mklx_dm , role_model.gnmb_dm as gnmb_dm , model.gnmk_dm as gnmk_dm \n" +
                "\tfrom qx_gnmk model \n" +
                "\t\tleft join qx_gnmb_gnmk role_model on role_model.gnmk_dm = model.gnmk_dm \n" +
                "\twhere model.mklx_dm in ( ? , ? ) \n" +
                ") A \n" +
                "left join ( \n" +
                "\tselect distinct org.jbdm as jbdm , org_model.sdate as sdate , org_model.edate as edate , model.gnmk_dm as gnmk_dm \n" +
                "\tfrom qx_gnmk model , DM_SWJG org , qx_jg_gnmk org_model \n" +
                "\twhere model.mklx_dm in ( ? , ? ) and org_model.gnmk_dm = model.gnmk_dm and org.SWJG_DM = org_model.jg_dm \n" +
                ") B on A.gnmk_dm = B.gnmk_dm";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT A.gnmk_ljmc, A.mklx_dm, A.gnmb_dm, B.jbdm, B.sdate\n" +
                "\t, B.edate\n" +
                "FROM (\n" +
                "\tSELECT model.gnmk_ljmc AS gnmk_ljmc, model.mklx_dm AS mklx_dm, role_model.gnmb_dm AS gnmb_dm, model.gnmk_dm AS gnmk_dm\n" +
                "\tFROM qx_gnmk model\n" +
                "\t\tLEFT JOIN qx_gnmb_gnmk role_model ON role_model.gnmk_dm = model.gnmk_dm \n" +
                "\tWHERE model.mklx_dm IN (?, ?)\n" +
                ") A\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT DISTINCT org.jbdm AS jbdm, org_model.sdate AS sdate, org_model.edate AS edate, model.gnmk_dm AS gnmk_dm\n" +
                "\t\tFROM qx_gnmk model, DM_SWJG org, qx_jg_gnmk org_model\n" +
                "\t\tWHERE model.mklx_dm IN (?, ?)\n" +
                "\t\t\tAND org_model.gnmk_dm = model.gnmk_dm\n" +
                "\t\t\tAND org.SWJG_DM = org_model.jg_dm\n" +
                "\t) B ON A.gnmk_dm = B.gnmk_dm ", stmt.toString());
    }

}