package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * Created by zyc@byshell.org on 2016/8/16.
 */
public class SqlserverAlterTest extends TestCase {
    public void testAlter1() {
        String sql = "alter table alert_config_detail\n" + "    add age14 int";
        SQLStatementParser parser = new SQLServerStatementParser(sql);
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) parser.parseStatement(); //
        try {
            stmt.toString();
        } catch (ClassCastException e) {
            Assert.fail(e.getMessage());
        }
    }

    public void testAlter2() {
        String sql = "ALTER TABLE \"console\".\"dbo\".\"alert_config_detail\"\n" + "\tADD \"age14\" int";
        SQLStatementParser parser = new SQLServerStatementParser(sql);
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) parser.parseStatement(); //
        try {
            stmt.toString();
        } catch (ClassCastException e) {
            Assert.fail(e.getMessage());
        }
    }
}
