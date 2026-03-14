package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLConstraint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePrimaryKey;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUsingIndexClause;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.druid.sql.parser.ParserException;
import java.util.NoSuchElementException;

public class OracleUsingIndexTest {


    @Test
    public void testUsingLiteralIndex() {
        String sql = "ALTER TABLE \"SC\".\"TB1\"\n" +
                "\tADD CONSTRAINT \"PK_XXX\" PRIMARY KEY (\"ID\")\n" +
                "\t\tUSING INDEX \"SC\".\"PK_XXX\"";
        SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle,false);
        System.out.println(stat);
        Assert.assertTrue(stat instanceof SQLAlterTableStatement);
        SQLAlterTableItem sqlAlterTableItem = ((SQLAlterTableStatement) stat).getItems().stream().findAny()
                .orElseThrow(NoSuchElementException::new);
        Assert.assertTrue(sqlAlterTableItem instanceof SQLAlterTableAddConstraint);
        SQLConstraint constraint = ((SQLAlterTableAddConstraint) sqlAlterTableItem).getConstraint();
        Assert.assertTrue(constraint instanceof OraclePrimaryKey);
        OracleUsingIndexClause usingIndexClause = ((OraclePrimaryKey) constraint).getUsing();
        Assert.assertEquals("\"SC\".\"PK_XXX\"", usingIndexClause.getIndex().toString());
        Assert.assertEquals(sql, stat.toString());
        System.out.println("=============");
    }

    @Test
    public void testUsingIndexWithoutSchema() {
        String sql = "ALTER TABLE \"SC\".\"TB1\"\n" +
                "\tADD CONSTRAINT \"PK_XXX\" PRIMARY KEY (\"ID\")\n" +
                "\t\tUSING INDEX \"PK_XXX\"";
        SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle,false);
        System.out.println(stat);
        Assert.assertTrue(stat instanceof SQLAlterTableStatement);
        SQLAlterTableItem sqlAlterTableItem = ((SQLAlterTableStatement) stat).getItems().stream().findAny()
                .orElseThrow(NoSuchElementException::new);
        Assert.assertTrue(sqlAlterTableItem instanceof SQLAlterTableAddConstraint);
        SQLConstraint constraint = ((SQLAlterTableAddConstraint) sqlAlterTableItem).getConstraint();
        Assert.assertTrue(constraint instanceof OraclePrimaryKey);
        OracleUsingIndexClause usingIndexClause = ((OraclePrimaryKey) constraint).getUsing();
        Assert.assertEquals("\"PK_XXX\"", usingIndexClause.getIndex().toString());
        Assert.assertEquals(sql, stat.toString());
        System.out.println("=============");
    }

    @Test
    public void testUsingIdentifierIndex() {
        String sql = "ALTER TABLE \"SC\".\"TB1\"\n" +
                "\tADD CONSTRAINT \"PK_XXX\" PRIMARY KEY (\"ID\")\n" +
                "\t\tUSING INDEX SC.PK_XXX";
        SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle,false);
        System.out.println(stat);
        Assert.assertTrue(stat instanceof SQLAlterTableStatement);
        SQLAlterTableItem sqlAlterTableItem = ((SQLAlterTableStatement) stat).getItems().stream().findAny()
                .orElseThrow(NoSuchElementException::new);
        Assert.assertTrue(sqlAlterTableItem instanceof SQLAlterTableAddConstraint);
        SQLConstraint constraint = ((SQLAlterTableAddConstraint) sqlAlterTableItem).getConstraint();
        Assert.assertTrue(constraint instanceof OraclePrimaryKey);
        OracleUsingIndexClause usingIndexClause = ((OraclePrimaryKey) constraint).getUsing();
        Assert.assertEquals("SC.PK_XXX", usingIndexClause.getIndex().toString());
        Assert.assertEquals(sql, stat.toString());
        System.out.println("=============");
    }

    @Test
    public void testUsingIdentifierIndexWithoutSchema() {
        String sql = "ALTER TABLE \"SC\".\"TB1\"\n" +
                "\tADD CONSTRAINT \"PK_XXX\" PRIMARY KEY (\"ID\")\n" +
                "\t\tUSING INDEX PK_XXX";
        SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle,false);
        System.out.println(stat);
        Assert.assertTrue(stat instanceof SQLAlterTableStatement);
        SQLAlterTableItem sqlAlterTableItem = ((SQLAlterTableStatement) stat).getItems().stream().findAny()
                .orElseThrow(NoSuchElementException::new);
        Assert.assertTrue(sqlAlterTableItem instanceof SQLAlterTableAddConstraint);
        SQLConstraint constraint = ((SQLAlterTableAddConstraint) sqlAlterTableItem).getConstraint();
        Assert.assertTrue(constraint instanceof OraclePrimaryKey);
        OracleUsingIndexClause usingIndexClause = ((OraclePrimaryKey) constraint).getUsing();
        Assert.assertEquals("PK_XXX", usingIndexClause.getIndex().toString());
        Assert.assertEquals(sql, stat.toString());
        System.out.println("=============");
    }

    @Test
    public void testIncompleteUsingIndex() {
        String sql = "ALTER TABLE \"SC\".\"TB1\"\n" +
                "\tADD CONSTRAINT \"PK_XXX\" PRIMARY KEY (\"ID\")\n" +
                "\t\tUSING INDEX";
        try {
            SQLUtils.parseSingleStatement(sql, DbType.oracle,false);
            throw new RuntimeException("not ParserException");
        }catch (Throwable e){
            Assert.assertTrue(e instanceof ParserException);
            Assert.assertEquals(e.getMessage(),"syntax error, expect '('/LITERAL_ALIAS/IDENTIFIER, actual EOF pos 80, line 3, column 14, token EOF");
        }
    }

    @Test
    public void testWrongUsingIndex() {
        String sql = "ALTER TABLE \"SC\".\"TB1\"\n" +
                "\tADD CONSTRAINT \"PK_XXX\" PRIMARY KEY (\"ID\")\n" +
                "\t\tUSING INDEX ENABLE";
        try {
            SQLUtils.parseSingleStatement(sql, DbType.oracle,false);
            throw new RuntimeException("not ParserException");
        }catch (Throwable e){
            Assert.assertTrue(e instanceof ParserException);
            Assert.assertEquals(e.getMessage(),"syntax error, expect '('/LITERAL_ALIAS/IDENTIFIER, actual ENABLE pos 87, line 3, column 15, token ENABLE");
        }
    }
}
