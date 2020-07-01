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
package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest51 extends TestCase {
    private final String dbType = JdbcConstants.POSTGRESQL;

    public void test_0() throws Exception {
        String sql = "SELECT TMP_PAGE.*, ROWNUM ROW_ID FROM ( SELECT M.*, A.*\n" +
                "\t\tFROM T_EW_MERCHANT M LEFT JOIN LP_ADDRESS A\n" +
                "\t\tON\n" +
                "\t\tM.ADDRESS_KEY = A.KEY\n" +
                "\t\t \n" +
                "\t\t\tWHERE M.MERCHANT_CODE LIKE '%'||?||'%'\n" +
                "\t\t\tOR\n" +
                "\t\t\tM.MERCHANT_NAME LIKE '%'||?||'%'\n" +
                "\t\t\tOR M.MERCHANT_NAME LIKE\n" +
                "\t\t\t'%'||?||'%'\n" +
                "\t\t\tOR M.CERTIFICATE LIKE\n" +
                "\t\t\t'%'||?||'%'\n" +
                "\t\t\tOR M.OWNER\n" +
                "\t\t\tLIKE\n" +
                "\t\t\t'%'||?||'%'\n" +
                "\t\t\tOR A.COUNTRY LIKE\n" +
                "\t\t\t'%'||?||'%'\n" +
                "\t\t\tOR\n" +
                "\t\t\tA.PROVINCE LIKE\n" +
                "\t\t\t'%'||?||'%'\n" +
                "\t\t\tOR A.CITY LIKE\n" +
                "\t\t\t'%'||?||'%'\n" +
                "\t\t\tOR A.COUNTY LIKE\n" +
                "\t\t\t'%'||?||'%'\n" +
                "\t\t\tOR A.STREET_AREA LIKE\n" +
                "\t\t\t'%'||?||'%'\n" +
                "\t\t\tOR A.DETAILE LIKE\n" +
                "\t\t\t'%'||?||'%'\n" +
                "\t\t\tOR A.ZIPCODE\n" +
                "\t\t\tLIKE\n" +
                "\t\t\t'%'||?||'%' ) TMP_PAGE WHERE ROWNUM <= 10";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        assertEquals("SELECT TMP_PAGE.*, ROWNUM AS ROW_ID\n" +
                "FROM (\n" +
                "\t(SELECT M.*, A.*\n" +
                "\tFROM T_EW_MERCHANT M\n" +
                "\t\tLEFT JOIN LP_ADDRESS A ON M.ADDRESS_KEY = A.KEY\n" +
                "\tWHERE M.MERCHANT_CODE LIKE ('%' || ? || '%')\n" +
                "\t\tOR M.MERCHANT_NAME LIKE ('%' || ? || '%')\n" +
                "\t\tOR M.MERCHANT_NAME LIKE ('%' || ? || '%')\n" +
                "\t\tOR M.CERTIFICATE LIKE ('%' || ? || '%')\n" +
                "\t\tOR M.OWNER LIKE ('%' || ? || '%')\n" +
                "\t\tOR A.COUNTRY LIKE ('%' || ? || '%')\n" +
                "\t\tOR A.PROVINCE LIKE ('%' || ? || '%')\n" +
                "\t\tOR A.CITY LIKE ('%' || ? || '%')\n" +
                "\t\tOR A.COUNTY LIKE ('%' || ? || '%')\n" +
                "\t\tOR A.STREET_AREA LIKE ('%' || ? || '%')\n" +
                "\t\tOR A.DETAILE LIKE ('%' || ? || '%')\n" +
                "\t\tOR A.ZIPCODE LIKE ('%' || ? || '%'))\n" +
                ") TMP_PAGE\n" +
                "WHERE ROWNUM <= 10", SQLUtils.toPGString(stmt));
        
        assertEquals("select TMP_PAGE.*, ROWNUM as ROW_ID\n" +
                "from (\n" +
                "\t(select M.*, A.*\n" +
                "\tfrom T_EW_MERCHANT M\n" +
                "\t\tleft join LP_ADDRESS A on M.ADDRESS_KEY = A.KEY\n" +
                "\twhere M.MERCHANT_CODE like ('%' || ? || '%')\n" +
                "\t\tor M.MERCHANT_NAME like ('%' || ? || '%')\n" +
                "\t\tor M.MERCHANT_NAME like ('%' || ? || '%')\n" +
                "\t\tor M.CERTIFICATE like ('%' || ? || '%')\n" +
                "\t\tor M.OWNER like ('%' || ? || '%')\n" +
                "\t\tor A.COUNTRY like ('%' || ? || '%')\n" +
                "\t\tor A.PROVINCE like ('%' || ? || '%')\n" +
                "\t\tor A.CITY like ('%' || ? || '%')\n" +
                "\t\tor A.COUNTY like ('%' || ? || '%')\n" +
                "\t\tor A.STREET_AREA like ('%' || ? || '%')\n" +
                "\t\tor A.DETAILE like ('%' || ? || '%')\n" +
                "\t\tor A.ZIPCODE like ('%' || ? || '%'))\n" +
                ") TMP_PAGE\n" +
                "where ROWNUM <= 10", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());

        assertEquals(15, visitor.getColumns().size());
        assertEquals(2, visitor.getTables().size());
    }
}
