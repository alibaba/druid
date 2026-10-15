package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MySQL / MariaDB ALTER TABLE ... ADD COLUMN IF NOT EXISTS.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6067">Issue #6067</a>
 */
public class Issue6067 {
    @Test
    public void test_add_column_if_not_exists_mysql_and_mariadb() {
        String sql = "alter table aaa add column if not exists cc_count int(1) DEFAULT '5'";
        for (DbType dbType : new DbType[]{DbType.mysql, DbType.mariadb}) {
            List<SQLStatement> stmts = SQLUtils.parseStatements(sql, dbType);
            assertEquals(1, stmts.size());

            SQLAlterTableStatement alter = (SQLAlterTableStatement) stmts.get(0);
            SQLAlterTableAddColumn add = (SQLAlterTableAddColumn) alter.getItems().get(0);
            SQLColumnDefinition col = add.getColumns().get(0);
            assertTrue(col.isIfNotExists(), "AST flag must be set for " + dbType);

            assertTrue(SQLUtils.toSQLString(stmts.get(0), dbType).contains("ADD COLUMN IF NOT EXISTS cc_count"));
        }
    }

    @Test
    public void test_add_multiple_columns_if_not_exists() {
        String sql = "alter table t add column if not exists a int, add column if not exists b varchar(10)";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        String out = SQLUtils.toSQLString(stmts.get(0), DbType.mysql);
        assertTrue(out.contains("ADD COLUMN IF NOT EXISTS a"), out);
        assertTrue(out.contains("ADD COLUMN IF NOT EXISTS b"), out);
    }

    @Test
    public void test_add_column_without_if_not_exists_unchanged() {
        String sql = "alter table aaa add column cc_count int(1) DEFAULT '5'";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        SQLAlterTableStatement alter = (SQLAlterTableStatement) stmts.get(0);
        SQLColumnDefinition col = ((SQLAlterTableAddColumn) alter.getItems().get(0)).getColumns().get(0);
        assertEquals(false, col.isIfNotExists());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.mysql).contains("ADD COLUMN cc_count"));
    }

    @Test
    public void test_if_without_not_exists_is_error() {
        // IF must be followed by NOT EXISTS; otherwise it is a syntax error
        assertThrows(Exception.class,
                () -> SQLUtils.parseStatements("alter table t add column if foo int", DbType.mysql));
    }
}
