package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

import java.util.List;

public class DM_AlterTableTest extends TestCase {
    // =============== MODIFY ===============
    public void test_alter_modify_column() {
        String sql = "ALTER TABLE users MODIFY name VARCHAR(500)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_modify_column_not_null() {
        String sql = "ALTER TABLE users MODIFY email VARCHAR(200) NOT NULL";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== ADD COLUMN ===============
    public void test_alter_add_column() {
        String sql = "ALTER TABLE users ADD phone VARCHAR(20)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_add_column_keyword() {
        String sql = "ALTER TABLE users ADD COLUMN address VARCHAR(500)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_add_column_if_not_exists() {
        String sql = "ALTER TABLE users ADD COLUMN IF NOT EXISTS remark TEXT";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_add_multiple_columns() {
        String sql = "ALTER TABLE users ADD (col1 INT, col2 VARCHAR(100), col3 DATE)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== DROP COLUMN ===============
    public void test_alter_drop_column() {
        String sql = "ALTER TABLE users DROP COLUMN temp_data";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_drop_column_if_exists() {
        String sql = "ALTER TABLE users DROP COLUMN IF EXISTS old_field";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_drop_column_cascade() {
        String sql = "ALTER TABLE users DROP COLUMN ref_id CASCADE";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_drop_column_restrict() {
        String sql = "ALTER TABLE users DROP COLUMN ref_id RESTRICT";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== ADD CONSTRAINT ===============
    public void test_alter_add_constraint_primary_key() {
        String sql = "ALTER TABLE orders ADD CONSTRAINT pk_orders PRIMARY KEY (id)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_add_constraint_foreign_key() {
        String sql = "ALTER TABLE orders ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_add_constraint_unique() {
        String sql = "ALTER TABLE users ADD CONSTRAINT uk_email UNIQUE (email)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_add_constraint_check() {
        String sql = "ALTER TABLE orders ADD CONSTRAINT chk_amount CHECK (amount > 0)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== RENAME CONSTRAINT ===============
    public void test_alter_rename_constraint() {
        String sql = "ALTER TABLE users RENAME CONSTRAINT old_constraint TO new_constraint";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== RENAME COLUMN ===============
    public void test_alter_rename_column() {
        String sql = "ALTER TABLE users RENAME COLUMN old_name TO new_name";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== DROP CONSTRAINT ===============
    public void test_alter_drop_constraint() {
        String sql = "ALTER TABLE orders DROP CONSTRAINT fk_user";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_drop_constraint_cascade() {
        String sql = "ALTER TABLE orders DROP CONSTRAINT fk_user CASCADE";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== ALTER COLUMN SET DEFAULT ===============
    public void test_alter_column_set_default() {
        String sql = "ALTER TABLE users ALTER COLUMN status SET DEFAULT 'active'";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_column_drop_default() {
        String sql = "ALTER TABLE users ALTER COLUMN status DROP DEFAULT";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== ALTER COLUMN SET NULL/NOT NULL ===============
    public void test_alter_column_set_not_null() {
        String sql = "ALTER TABLE users ALTER COLUMN name SET NOT NULL";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_column_set_null() {
        String sql = "ALTER TABLE users ALTER COLUMN remark SET NULL";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== RENAME TO ===============
    public void test_alter_rename_table() {
        String sql = "ALTER TABLE old_table RENAME TO new_table";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== ENABLE/DISABLE TRIGGERS ===============
    public void test_alter_enable_all_triggers() {
        String sql = "ALTER TABLE users ENABLE ALL TRIGGERS";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_disable_all_triggers() {
        String sql = "ALTER TABLE users DISABLE ALL TRIGGERS";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== MODIFY CONSTRAINT ===============
    public void test_alter_modify_constraint_enable() {
        String sql = "ALTER TABLE orders MODIFY CONSTRAINT fk_user ENABLE";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_modify_constraint_disable() {
        String sql = "ALTER TABLE orders MODIFY CONSTRAINT fk_user DISABLE";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== ENABLE/DISABLE CONSTRAINT ===============
    public void test_alter_enable_constraint() {
        String sql = "ALTER TABLE orders ENABLE CONSTRAINT fk_user";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_disable_constraint() {
        String sql = "ALTER TABLE orders DISABLE CONSTRAINT fk_user";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== DROP PRIMARY KEY ===============
    public void test_alter_drop_primary_key() {
        String sql = "ALTER TABLE users DROP PRIMARY KEY";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_drop_primary_key_cascade() {
        String sql = "ALTER TABLE users DROP PRIMARY KEY CASCADE";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== TRUNCATE PARTITION ===============
    public void test_alter_truncate_partition() {
        String sql = "ALTER TABLE orders TRUNCATE PARTITION p2023";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_truncate_subpartition() {
        String sql = "ALTER TABLE orders TRUNCATE SUBPARTITION sp_jan";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== MOVE TABLESPACE ===============
    public void test_alter_move_tablespace() {
        String sql = "ALTER TABLE users MOVE TABLESPACE tbs_archive";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== PARALLEL ===============
    public void test_alter_parallel() {
        String sql = "ALTER TABLE orders PARALLEL";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_parallel_degree() {
        String sql = "ALTER TABLE orders PARALLEL 4";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_noparallel() {
        String sql = "ALTER TABLE orders NOPARALLEL";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== READ ONLY / READ WRITE ===============
    public void test_alter_read_only() {
        String sql = "ALTER TABLE archive_data READ ONLY";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_read_write() {
        String sql = "ALTER TABLE archive_data READ WRITE";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_with_schema() {
        String sql = "ALTER TABLE myschema.users ADD COLUMN new_col INT";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_auto_increment() {
        String sql = "ALTER TABLE users AUTO_INCREMENT = 1000";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    public void test_alter_drop_auto_increment() {
        String sql = "ALTER TABLE users DROP AUTO_INCREMENT";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    // =============== DROP IDENTITY ===============
    public void test_alter_drop_identity() {
        String sql = "ALTER TABLE users DROP IDENTITY";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }
}
