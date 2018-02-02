package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by wenshao on 18/07/2017.
 */
public class CreateCompareTest_1 extends TestCase {
    public void test_0() throws Exception {
        String sql = "CREATE TABLE XT_DJ_XT (\n" +
                "\tXTBH varchar(20) NOT NULL,\n" +
                "\tXTDM varchar(20) NOT NULL,\n" +
                "\tXTMC varchar(200) NOT NULL,\n" +
                "\tXTBB varchar(20),\n" +
                "\tKQSJ datetime NOT NULL,\n" +
                "\tYXSJQ datetime NOT NULL,\n" +
                "\tYXSJZ datetime,\n" +
                "\tYXBZ char(1),\n" +
                "\tXTMS varchar(200),\n" +
                "\tCONSTRAINT PK_XT_DJ_XT PRIMARY KEY (XTBH)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE XT_DM_GN (\n" +
                "\tGNDM varchar(20) NOT NULL,\n" +
                "\tGNQQH varchar(20) NOT NULL,\n" +
                "\tGNMC varchar(200) NOT NULL,\n" +
                "\tSJGNDM varchar(20),\n" +
                "\tCDBZ char(1),\n" +
                "\tPLXH bigint,\n" +
                "\tLJDZ varchar(200),\n" +
                "\tYXBZ char(1),\n" +
                "\tGNMS varchar(200),\n" +
                "\tDYZB varchar(20),\n" +
                "\tCDTB varchar(100),\n" +
                "\tDXBZ char(1),\n" +
                "\tCONSTRAINT PK_XT_DM_GN PRIMARY KEY (GNDM)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE XT_DJ_YHCD (\n" +
                "\tCDBH varchar(20) NOT NULL,\n" +
                "\tYHBH varchar(20),\n" +
                "\tGNBH varchar(20),\n" +
                "\tPLXH bigint,\n" +
                "\tXTBH varchar(20),\n" +
                "\tCONSTRAINT PK_XT_DJ_YHCD PRIMARY KEY (CDBH),\n" +
                "\tFOREIGN KEY (GNBH) REFERENCES XT_DJ_GN (GNBH),\n" +
                "\tFOREIGN KEY (YHBH) REFERENCES XT_DJ_YH (YHBH)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE XT_DJ_YH (\n" +
                "\tYHBH varchar(20) NOT NULL,\n" +
                "\tXTBH varchar(20) NOT NULL,\n" +
                "\tYHDM varchar(20) NOT NULL,\n" +
                "\tDLMM varchar(20) NOT NULL,\n" +
                "\tYHMC varchar(200) NOT NULL,\n" +
                "\tCJYH varchar(20),\n" +
                "\tCJSJ datetime,\n" +
                "\tYHBZ char(1) DEFAULT '1',\n" +
                "\tYXBZ char(1),\n" +
                "\tYHMS varchar(200),\n" +
                "\tTSBZ char(1),\n" +
                "\tCONSTRAINT PK_XT_DJ_YH PRIMARY KEY (YHBH),\n" +
                "\tFOREIGN KEY (XTBH) REFERENCES XT_DJ_XT (XTBH)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE XT_DJ_GN (\n" +
                "\tGNBH varchar(20) NOT NULL,\n" +
                "\tXTBH varchar(20),\n" +
                "\tGNDM varchar(20) NOT NULL,\n" +
                "\tGNMC varchar(200) NOT NULL,\n" +
                "\tSJGNBH varchar(20),\n" +
                "\tCDBZ char(1),\n" +
                "\tPLXH bigint,\n" +
                "\tFWKZBZ char(1),\n" +
                "\tYXBZ char(1),\n" +
                "\tLJDZ varchar(200),\n" +
                "\tCONSTRAINT PK_XT_DJ_GN PRIMARY KEY (GNBH),\n" +
                "\tFOREIGN KEY (XTBH) REFERENCES XT_DJ_XT (XTBH),\n" +
                "\tFOREIGN KEY (GNDM) REFERENCES XT_DM_GN (GNDM)\n" +
                ");\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n";

        List stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);


        SQLCreateTableStatement.sort(stmtList);

        assertEquals("XT_DJ_XT", ((SQLCreateTableStatement)stmtList.get(0)).getName().getSimpleName());
        assertEquals("XT_DM_GN", ((SQLCreateTableStatement)stmtList.get(1)).getName().getSimpleName());
        assertEquals("XT_DJ_YH", ((SQLCreateTableStatement)stmtList.get(2)).getName().getSimpleName());
        assertEquals("XT_DJ_GN", ((SQLCreateTableStatement)stmtList.get(3)).getName().getSimpleName());
        assertEquals("XT_DJ_YHCD", ((SQLCreateTableStatement)stmtList.get(4)).getName().getSimpleName());

        String sortedSql = SQLUtils.toSQLString(stmtList, JdbcConstants.ORACLE);
        System.out.println(sortedSql);
    }
}
