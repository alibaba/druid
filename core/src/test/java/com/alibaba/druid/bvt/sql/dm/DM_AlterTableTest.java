package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import junit.framework.TestCase;

import java.util.List;

public class DM_AlterTableTest extends TestCase {
    private SQLAlterTableStatement parseAlterTable(String sql) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLAlterTableStatement);
        return (SQLAlterTableStatement) stmtList.get(0);
    }

    private void assertTableName(SQLAlterTableStatement s, String t) {
        assertEquals(t, s.getTableName());
    }

    private void assertVisitorTable(SQLStatement s, String t) {
        OracleSchemaStatVisitor v = new OracleSchemaStatVisitor();
        s.accept(v);
        assertTrue(v.getTables().containsKey(new TableStat.Name(t)));
    }

    private void assertRoundTrip(String sql) {
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_alter_modify_column() {
        String sql = "ALTER TABLE users MODIFY name VARCHAR(500)";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertVisitorTable(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_modify_column_not_null() {
        String sql = "ALTER TABLE users MODIFY email VARCHAR(200) NOT NULL";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_add_column() {
        String sql = "ALTER TABLE users ADD phone VARCHAR(20)";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertVisitorTable(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_add_column_keyword() {
        String sql = "ALTER TABLE users ADD COLUMN address VARCHAR(500)";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_add_column_if_not_exists() {
        String sql = "ALTER TABLE users ADD COLUMN IF NOT EXISTS remark TEXT";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
    }

    public void test_alter_add_multiple_columns() {
        String sql = "ALTER TABLE users ADD (col1 INT, col2 VARCHAR(100), col3 DATE)";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_drop_column() {
        String sql = "ALTER TABLE users DROP COLUMN temp_data";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertVisitorTable(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_drop_column_if_exists() {
        String sql = "ALTER TABLE users DROP COLUMN IF EXISTS old_field";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_drop_column_cascade() {
        String sql = "ALTER TABLE users DROP COLUMN ref_id CASCADE";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_drop_column_restrict() {
        String sql = "ALTER TABLE users DROP COLUMN ref_id RESTRICT";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm).contains("RESTRICT"));
        assertRoundTrip(sql);
    }

    public void test_alter_add_constraint_primary_key() {
        String sql = "ALTER TABLE orders ADD CONSTRAINT pk_orders PRIMARY KEY (id)";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertRoundTrip(sql);
    }

    public void test_alter_add_constraint_foreign_key() {
        String sql = "ALTER TABLE orders ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertRoundTrip(sql);
    }

    public void test_alter_add_constraint_unique() {
        String sql = "ALTER TABLE users ADD CONSTRAINT uk_email UNIQUE (email)";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_add_constraint_check() {
        String sql = "ALTER TABLE orders ADD CONSTRAINT chk_amount CHECK (amount > 0)";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertRoundTrip(sql);
    }

    public void test_alter_rename_constraint() {
        String sql = "ALTER TABLE users RENAME CONSTRAINT old_constraint TO new_constraint";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_rename_column() {
        String sql = "ALTER TABLE users RENAME COLUMN old_name TO new_name";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_drop_constraint() {
        String sql = "ALTER TABLE orders DROP CONSTRAINT fk_user";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertVisitorTable(s, "orders");
        assertRoundTrip(sql);
    }

    public void test_alter_drop_constraint_cascade() {
        String sql = "ALTER TABLE orders DROP CONSTRAINT fk_user CASCADE";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertRoundTrip(sql);
    }

    public void test_alter_column_set_default() {
        String sql = "ALTER TABLE users ALTER COLUMN status SET DEFAULT 'active'";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_column_drop_default() {
        String sql = "ALTER TABLE users ALTER COLUMN status DROP DEFAULT";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_column_set_not_null() {
        String sql = "ALTER TABLE users ALTER COLUMN name SET NOT NULL";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_column_set_null() {
        String sql = "ALTER TABLE users ALTER COLUMN remark SET NULL";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
    }

    public void test_alter_rename_table() {
        String sql = "ALTER TABLE old_table RENAME TO new_table";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "old_table");
        assertRoundTrip(sql);
    }

    public void test_alter_enable_all_triggers() {
        String sql = "ALTER TABLE users ENABLE ALL TRIGGERS";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm)
                .contains("ENABLE ALL TRIGGERS"));
        assertRoundTrip(sql);
    }

    public void test_alter_disable_all_triggers() {
        String sql = "ALTER TABLE users DISABLE ALL TRIGGERS";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm)
                .contains("DISABLE ALL TRIGGERS"));
        assertRoundTrip(sql);
    }

    public void test_alter_modify_constraint_enable() {
        String sql = "ALTER TABLE orders MODIFY CONSTRAINT fk_user ENABLE";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertRoundTrip(sql);
    }

    public void test_alter_modify_constraint_disable() {
        String sql = "ALTER TABLE orders MODIFY CONSTRAINT fk_user DISABLE";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertRoundTrip(sql);
    }

    public void test_alter_enable_constraint() {
        String sql = "ALTER TABLE orders ENABLE CONSTRAINT fk_user";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertRoundTrip(sql);
    }

    public void test_alter_disable_constraint() {
        String sql = "ALTER TABLE orders DISABLE CONSTRAINT fk_user";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertRoundTrip(sql);
    }

    public void test_alter_drop_primary_key() {
        String sql = "ALTER TABLE users DROP PRIMARY KEY";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertVisitorTable(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_drop_primary_key_cascade() {
        String sql = "ALTER TABLE users DROP PRIMARY KEY CASCADE";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm)
                .contains("DROP PRIMARY KEY CASCADE"));
        assertRoundTrip(sql);
    }

    public void test_alter_drop_primary_key_restrict() {
        String sql = "ALTER TABLE users DROP PRIMARY KEY RESTRICT";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm)
                .contains("DROP PRIMARY KEY RESTRICT"));
        assertRoundTrip(sql);
    }

    public void test_alter_truncate_partition() {
        String sql = "ALTER TABLE orders TRUNCATE PARTITION p2023";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertRoundTrip(sql);
    }

    public void test_alter_truncate_subpartition() {
        String sql = "ALTER TABLE orders TRUNCATE SUBPARTITION sp_jan";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm)
                .contains("TRUNCATE SUBPARTITION"));
        assertRoundTrip(sql);
    }

    public void test_alter_truncate_partition_drop_storage() {
        String sql = "ALTER TABLE orders TRUNCATE PARTITION p2023 DROP STORAGE";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm).contains("DROP STORAGE"));
        assertRoundTrip(sql);
    }

    public void test_alter_truncate_subpartition_reuse_storage() {
        String sql = "ALTER TABLE orders TRUNCATE SUBPARTITION sp_jan REUSE STORAGE";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm).contains("REUSE STORAGE"));
        assertRoundTrip(sql);
    }

    public void test_alter_move_tablespace() {
        String sql = "ALTER TABLE users MOVE TABLESPACE tbs_archive";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_parallel() {
        String sql = "ALTER TABLE orders PARALLEL";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm).contains("PARALLEL"));
        assertRoundTrip(sql);
    }

    public void test_alter_parallel_degree() {
        String sql = "ALTER TABLE orders PARALLEL 4";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm).contains("PARALLEL 4"));
        assertRoundTrip(sql);
    }

    public void test_alter_noparallel() {
        String sql = "ALTER TABLE orders NOPARALLEL";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "orders");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm).contains("NOPARALLEL"));
        assertRoundTrip(sql);
    }

    public void test_alter_read_only() {
        String sql = "ALTER TABLE archive_data READ ONLY";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "archive_data");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm).contains("READ ONLY"));
        assertRoundTrip(sql);
    }

    public void test_alter_read_write() {
        String sql = "ALTER TABLE archive_data READ WRITE";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "archive_data");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm).contains("READ WRITE"));
        assertRoundTrip(sql);
    }

    public void test_alter_with_schema() {
        String sql = "ALTER TABLE myschema.users ADD COLUMN new_col INT";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertVisitorTable(s, "myschema.users");
        assertRoundTrip(sql);
    }

    public void test_alter_auto_increment() {
        String sql = "ALTER TABLE users AUTO_INCREMENT = 1000";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertTrue(SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.dm), DbType.dm)
                .contains("AUTO_INCREMENT = 1000"));
        assertRoundTrip(sql);
    }

    public void test_alter_drop_auto_increment() {
        String sql = "ALTER TABLE users DROP AUTO_INCREMENT";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
        assertRoundTrip(sql);
    }

    public void test_alter_drop_identity() {
        String sql = "ALTER TABLE users DROP IDENTITY";
        SQLAlterTableStatement s = parseAlterTable(sql);
        assertTableName(s, "users");
    }
}
