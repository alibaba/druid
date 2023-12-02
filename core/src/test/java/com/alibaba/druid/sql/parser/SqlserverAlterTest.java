package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropColumnItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

/**
 * Created by zyc@byshell.org on 2016/8/16.
 */
public class SqlserverAlterTest extends TestCase {
    public void testAlter1() {
        String sql = "alter table alert_config_detail\n" + "    add age14 int";
        SQLStatementParser parser = new SQLServerStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) parser.parseStatement(); //
        try {
            stmt.toString();
        } catch (ClassCastException e) {
            Assert.fail(e.getMessage());
        }

        assert stmt.getTableName().equals("alert_config_detail");
        assert stmt.getItems().size() == 1;
        assert stmt.getItems().get(0) instanceof SQLAlterTableAddColumn;
        List<SQLColumnDefinition> columns = ((SQLAlterTableAddColumn) stmt.getItems().get(0)).getColumns();
        assert columns.size() == 1;
        assert columns.get(0).getName().getSimpleName().equals("age14");
        assert columns.get(0).getDataType().getName().equals("int");
    }

    public void testAlter2() {
        String sql = "ALTER TABLE \"console\".\"dbo\".\"alert_config_detail\"\n" + "\tADD \"age14\" int";
        SQLStatementParser parser = new SQLServerStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) parser.parseStatement(); //
        try {
            stmt.toString();
        } catch (ClassCastException e) {
            Assert.fail(e.getMessage());
        }

        assert stmt.getTableName().equals("alert_config_detail");
        assert stmt.getItems().size() == 1;
        assert stmt.getItems().get(0) instanceof SQLAlterTableAddColumn;
        List<SQLColumnDefinition> columns = ((SQLAlterTableAddColumn) stmt.getItems().get(0)).getColumns();
        assert columns.size() == 1;
        assert columns.get(0).getName().getSimpleName().equals("age14");
        assert columns.get(0).getDataType().getName().equals("int");
    }

    public void testAlter3() {
        String sql = "ALTER TABLE [console].[dbo].[alert_config_detail]\n" + "\tADD [age14] int";
        SQLStatementParser parser = new SQLServerStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) parser.parseStatement(); //
        try {
            stmt.toString();
        } catch (ClassCastException e) {
            Assert.fail(e.getMessage());
        }

        assert stmt.getTableName().equals("alert_config_detail");
        assert stmt.getItems().size() == 1;
        assert stmt.getItems().get(0) instanceof SQLAlterTableAddColumn;
        List<SQLColumnDefinition> columns = ((SQLAlterTableAddColumn) stmt.getItems().get(0)).getColumns();
        assert columns.size() == 1;
        assert columns.get(0).getName().getSimpleName().equals("age14");
        assert columns.get(0).getDataType().getName().equals("int");
    }

    public void testAlter4() {
        String sql = "ALTER TABLE [dbo].[alert_config_detail] DROP COLUMN [a2], COLUMN [a3], COLUMN [a4]";
        SQLStatementParser parser = new SQLServerStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) parser.parseStatement(); //
        try {
            stmt.toString();
        } catch (ClassCastException e) {
            Assert.fail(e.getMessage());
        }

        assert stmt.getTableName().equals("alert_config_detail");
        assert stmt.getItems().size() == 1;
        assert stmt.getItems().get(0) instanceof SQLAlterTableDropColumnItem;
        List<SQLName> columns = ((SQLAlterTableDropColumnItem) stmt.getItems().get(0)).getColumns();
        assert columns.size() == 3;
        assert columns.get(0).getSimpleName().equals("a2");
        assert columns.get(1).getSimpleName().equals("a3");
        assert columns.get(2).getSimpleName().equals("a4");
    }
}
