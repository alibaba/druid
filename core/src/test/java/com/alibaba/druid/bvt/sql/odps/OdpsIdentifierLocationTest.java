package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;

public class OdpsIdentifierLocationTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "--odps sql\n" +
                "--********************************************************************--\n" +
                "--author:dw_on_emr_qa3_testcloud_com\n" +
                "--create time:2025-08-11 17:08:41\n" +
                "--********************************************************************--\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS partition_table1\n" +
                "(\n" +
                "    a  STRING COMMENT 'FIELD'\n" +
                "    ,b STRING COMMENT 'FIELD'\n" +
                ")\n" +
                "COMMENT 'TABLE COMMENT'\n" +
                "PARTITIONED BY (ds STRING COMMENT '分区')\n" +
                "LIFECYCLE 70;";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(
                sql,
                DbType.odps,
                SQLParserFeature.KeepSourceLocation,
                SQLParserFeature.KeepComments);
        OdpsCreateTableStatement sqlCreateTableStatement = (OdpsCreateTableStatement) parser.parseStatement();
        int column = sqlCreateTableStatement.getTableSource().getExpr().getSourceColumn();
        int line = sqlCreateTableStatement.getTableSource().getExpr().getSourceLine();
        assertEquals(column, 28);
        assertEquals(line, 7);
    }
}
