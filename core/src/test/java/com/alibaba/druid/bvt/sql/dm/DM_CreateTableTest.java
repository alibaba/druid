package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

import java.util.List;

public class DM_CreateTableTest extends TestCase {
    public void test_simple_create_table() throws Exception {
        String sql = "CREATE TABLE users (" +
                "id INT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(200), " +
                "created_at TIMESTAMP DEFAULT SYSDATE" +
                ")";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
    }

    public void test_create_table_with_constraints() throws Exception {
        String sql = "CREATE TABLE orders (" +
                "id INT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "amount DECIMAL(10,2), " +
                "status VARCHAR(20) DEFAULT 'pending', " +
                "CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
    }

    public void test_create_table_as_select() throws Exception {
        String sql = "CREATE TABLE users_backup AS SELECT * FROM users WHERE status = 'active'";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
    }

    public void test_drop_table() throws Exception {
        String sql = "DROP TABLE users CASCADE CONSTRAINTS";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
    }

    public void test_create_index() throws Exception {
        String sql = "CREATE INDEX idx_users_email ON users(email)";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
    }

    public void test_create_sequence() throws Exception {
        String sql = "CREATE SEQUENCE seq_users START WITH 1 INCREMENT BY 1";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
    }
}
