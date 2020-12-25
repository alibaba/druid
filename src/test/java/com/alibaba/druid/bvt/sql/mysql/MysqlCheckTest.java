package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MysqlAlterTableAlterCheck;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.Assert;

import java.util.List;

public class MysqlCheckTest extends MysqlTest {

    public void test_create1()  {
        String sql = "CREATE TABLE `t12` (\n" +
                "  `c1` int DEFAULT NULL,\n" +
                "  `c2` int DEFAULT NULL,\n" +
                "  `c3` int DEFAULT NULL,\n" +
                "  CONSTRAINT `c12_positive` CHECK ((`c2` > 0)) /*!80016 NOT ENFORCED */,\n" +
                "  CONSTRAINT `c21_nonzero` CHECK ((`c1` <> 0)),\n" +
                "  CONSTRAINT `t12_chk_1` CHECK ((`c1` <> `c2`)),\n" +
                "  CONSTRAINT `t12_chk_2` CHECK ((`c1` > 10)),\n" +
                "  CONSTRAINT `t12_chk_3` CHECK (((`c3` < 100) and (`c3` > 0))),\n" +
                "  CONSTRAINT `t12_chk_4` CHECK ((`c1` > `c3`))\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());

        MySqlCreateTableStatement statement = (MySqlCreateTableStatement) statementList.get(0);
        Assert.assertEquals(9, statement.getTableElementList().size());
        {
            SQLTableElement element = statement.getTableElementList().get(3);
            Assert.assertTrue(element instanceof SQLCheck);
            SQLCheck sqlCheck = (SQLCheck) element;
            Assert.assertEquals(false, sqlCheck.getEnforced());
            Assert.assertEquals("`c12_positive`", sqlCheck.getName().getSimpleName());
            Assert.assertEquals("`c2` > 0", sqlCheck.getExpr().toString());
        }
        {
            SQLTableElement element = statement.getTableElementList().get(4);
            Assert.assertTrue(element instanceof SQLCheck);
            SQLCheck sqlCheck = (SQLCheck) element;
            Assert.assertNull(sqlCheck.getEnforced());
            Assert.assertEquals("`c21_nonzero`", sqlCheck.getName().getSimpleName());
            Assert.assertEquals("`c1` <> 0", sqlCheck.getExpr().toString());
        }
        {
            SQLTableElement element = statement.getTableElementList().get(5);
            Assert.assertTrue(element instanceof SQLCheck);
            SQLCheck sqlCheck = (SQLCheck) element;
            Assert.assertNull(sqlCheck.getEnforced());
            Assert.assertEquals("`t12_chk_1`", sqlCheck.getName().getSimpleName());
            Assert.assertEquals("`c1` <> `c2`", sqlCheck.getExpr().toString());
        }
        {
            SQLTableElement element = statement.getTableElementList().get(6);
            Assert.assertTrue(element instanceof SQLCheck);
            SQLCheck sqlCheck = (SQLCheck) element;
            Assert.assertNull(sqlCheck.getEnforced());
            Assert.assertEquals("`t12_chk_2`", sqlCheck.getName().getSimpleName());
            Assert.assertEquals("`c1` > 10", sqlCheck.getExpr().toString());
        }
        {
            SQLTableElement element = statement.getTableElementList().get(7);
            Assert.assertTrue(element instanceof SQLCheck);
            SQLCheck sqlCheck = (SQLCheck) element;
            Assert.assertNull(sqlCheck.getEnforced());
            Assert.assertEquals("`t12_chk_3`", sqlCheck.getName().getSimpleName());
            Assert.assertTrue(sqlCheck.getExpr() instanceof SQLBinaryOpExpr);
            Assert.assertEquals(SQLBinaryOperator.BooleanAnd, ((SQLBinaryOpExpr) sqlCheck.getExpr()).getOperator());
            Assert.assertEquals("`c3` < 100", ((SQLBinaryOpExpr) sqlCheck.getExpr()).getLeft().toString());
            Assert.assertEquals("`c3` > 0", ((SQLBinaryOpExpr) sqlCheck.getExpr()).getRight().toString());
        }
        {
            SQLTableElement element = statement.getTableElementList().get(8);
            Assert.assertTrue(element instanceof SQLCheck);
            SQLCheck sqlCheck = (SQLCheck) element;
            Assert.assertNull(sqlCheck.getEnforced());
            Assert.assertEquals("`t12_chk_4`", sqlCheck.getName().getSimpleName());
            Assert.assertEquals("`c1` > `c3`", sqlCheck.getExpr().toString());
        }
    }

    public void test_create2() {
        String sql = "CREATE TABLE `t12` (\n" +
                "\t`c1` int DEFAULT NULL,\n" +
                "\t`c2` int DEFAULT NULL,\n" +
                "\t`c3` int DEFAULT NULL,\n" +
                "\tCONSTRAINT `c12_positive` CHECK (`c2` > 0) NOT ENFORCED,\n" +
                "\tCONSTRAINT `c21_nonzero` CHECK (`c1` <> 0),\n" +
                "\tCONSTRAINT `t12_chk_1` CHECK (`c1` <> `c2`),\n" +
                "\tCONSTRAINT `t12_chk_2` CHECK (`c1` > 10),\n" +
                "\tCONSTRAINT `t12_chk_3` CHECK (`c3` < 100\n" +
                "\t\tAND `c3` > 0),\n" +
                "\tCONSTRAINT `t12_chk_4` CHECK (`c1` > `c3`)\n" +
                ") ENGINE = InnoDB CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;\n";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());

        MySqlCreateTableStatement statement = (MySqlCreateTableStatement) statementList.get(0);
        Assert.assertEquals(9, statement.getTableElementList().size());
        {
            SQLTableElement element = statement.getTableElementList().get(3);
            Assert.assertTrue(element instanceof SQLCheck);
            SQLCheck sqlCheck = (SQLCheck) element;
            Assert.assertEquals(false, sqlCheck.getEnforced());
            Assert.assertEquals("`c12_positive`", sqlCheck.getName().getSimpleName());
            Assert.assertEquals("`c2` > 0", sqlCheck.getExpr().toString());
        }
        {
            SQLTableElement element = statement.getTableElementList().get(4);
            Assert.assertTrue(element instanceof SQLCheck);
            SQLCheck sqlCheck = (SQLCheck) element;
            Assert.assertNull(sqlCheck.getEnforced());
            Assert.assertEquals("`c21_nonzero`", sqlCheck.getName().getSimpleName());
            Assert.assertEquals("`c1` <> 0", sqlCheck.getExpr().toString());
        }
        {
            SQLTableElement element = statement.getTableElementList().get(5);
            Assert.assertTrue(element instanceof SQLCheck);
            SQLCheck sqlCheck = (SQLCheck) element;
            Assert.assertNull(sqlCheck.getEnforced());
            Assert.assertEquals("`t12_chk_1`", sqlCheck.getName().getSimpleName());
            Assert.assertEquals("`c1` <> `c2`", sqlCheck.getExpr().toString());
        }
        {
            SQLTableElement element = statement.getTableElementList().get(6);
            Assert.assertTrue(element instanceof SQLCheck);
            SQLCheck sqlCheck = (SQLCheck) element;
            Assert.assertNull(sqlCheck.getEnforced());
            Assert.assertEquals("`t12_chk_2`", sqlCheck.getName().getSimpleName());
            Assert.assertEquals("`c1` > 10", sqlCheck.getExpr().toString());
        }
        {
            SQLTableElement element = statement.getTableElementList().get(7);
            Assert.assertTrue(element instanceof SQLCheck);
            SQLCheck sqlCheck = (SQLCheck) element;
            Assert.assertNull(sqlCheck.getEnforced());
            Assert.assertEquals("`t12_chk_3`", sqlCheck.getName().getSimpleName());
            Assert.assertTrue(sqlCheck.getExpr() instanceof SQLBinaryOpExpr);
            Assert.assertEquals(SQLBinaryOperator.BooleanAnd, ((SQLBinaryOpExpr) sqlCheck.getExpr()).getOperator());
            Assert.assertEquals("`c3` < 100", ((SQLBinaryOpExpr) sqlCheck.getExpr()).getLeft().toString());
            Assert.assertEquals("`c3` > 0", ((SQLBinaryOpExpr) sqlCheck.getExpr()).getRight().toString());
        }
        {
            SQLTableElement element = statement.getTableElementList().get(8);
            Assert.assertTrue(element instanceof SQLCheck);
            SQLCheck sqlCheck = (SQLCheck) element;
            Assert.assertNull(sqlCheck.getEnforced());
            Assert.assertEquals("`t12_chk_4`", sqlCheck.getName().getSimpleName());
            Assert.assertEquals("`c1` > `c3`", sqlCheck.getExpr().toString());
        }
    }

    public void test_alter_add1() {
        String sql = "ALTER TABLE t1 ADD CONSTRAINT chk1 CHECK((a>1));";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());
        Assert.assertTrue(statementList.get(0) instanceof SQLAlterTableStatement);
        SQLAlterTableStatement statement = (SQLAlterTableStatement) statementList.get(0);
        Assert.assertEquals(1, statement.getItems().size());

        Assert.assertTrue(statement.getItems().get(0) instanceof SQLAlterTableAddConstraint);
        SQLAlterTableAddConstraint constraint = (SQLAlterTableAddConstraint) statement.getItems().get(0);

        Assert.assertTrue(constraint.getConstraint() instanceof SQLCheck);

        SQLCheck sqlCheck = (SQLCheck) constraint.getConstraint();
        Assert.assertNull(sqlCheck.getEnforced());
        Assert.assertEquals("chk1", sqlCheck.getName().getSimpleName());
        Assert.assertEquals("a > 1", sqlCheck.getExpr().toString());
    }

    public void test_alter_add2() {
        String sql = "ALTER TABLE t1 ADD CONSTRAINT chk1 CHECK((a>1)) NOT ENFORCED; ";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());
        Assert.assertTrue(statementList.get(0) instanceof SQLAlterTableStatement);
        SQLAlterTableStatement statement = (SQLAlterTableStatement) statementList.get(0);
        Assert.assertEquals(1, statement.getItems().size());

        Assert.assertTrue(statement.getItems().get(0) instanceof SQLAlterTableAddConstraint);
        SQLAlterTableAddConstraint constraint = (SQLAlterTableAddConstraint) statement.getItems().get(0);

        Assert.assertTrue(constraint.getConstraint() instanceof SQLCheck);

        SQLCheck sqlCheck = (SQLCheck) constraint.getConstraint();
        Assert.assertEquals(false, sqlCheck.getEnforced());
        Assert.assertEquals("chk1", sqlCheck.getName().getSimpleName());
        Assert.assertEquals("a > 1", sqlCheck.getExpr().toString());
    }

    public void test_alter_drop() {
        String sql = "ALTER TABLE t1 DROP CONSTRAINT t1_check ;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());

        SQLAlterTableStatement statement = (SQLAlterTableStatement) statementList.get(0);
        Assert.assertEquals(1, statement.getItems().size());

        Assert.assertTrue(statement.getItems().get(0) instanceof SQLAlterTableDropConstraint);
        SQLAlterTableDropConstraint constraint = (SQLAlterTableDropConstraint) statement.getItems().get(0);

        Assert.assertEquals("t1_check", constraint.getConstraintName().getSimpleName());
    }

    public void test_alter_alter1() {
        String sql = "alter table t1 ALTER CHECK  t1_check  ENFORCED;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());

        SQLAlterTableStatement statement = (SQLAlterTableStatement) statementList.get(0);
        Assert.assertEquals(1, statement.getItems().size());

        Assert.assertTrue(statement.getItems().get(0) instanceof MysqlAlterTableAlterCheck);
        MysqlAlterTableAlterCheck constraint = (MysqlAlterTableAlterCheck) statement.getItems().get(0);

        Assert.assertEquals("t1_check", constraint.getName().getSimpleName());
        Assert.assertEquals(true, constraint.getEnforced());
    }

    public void test_alter_alter2() {
        String sql = "alter table t1 ALTER CHECK  t1_check  NOT ENFORCED;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());

        SQLAlterTableStatement statement = (SQLAlterTableStatement) statementList.get(0);
        Assert.assertEquals(1, statement.getItems().size());

        Assert.assertTrue(statement.getItems().get(0) instanceof MysqlAlterTableAlterCheck);
        MysqlAlterTableAlterCheck constraint = (MysqlAlterTableAlterCheck) statement.getItems().get(0);

        Assert.assertEquals("t1_check", constraint.getName().getSimpleName());
        Assert.assertEquals(false, constraint.getEnforced());
    }
}
