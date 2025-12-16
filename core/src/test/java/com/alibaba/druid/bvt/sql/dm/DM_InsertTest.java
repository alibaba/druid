package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

import java.util.List;

public class DM_InsertTest extends TestCase {
    public void test_simple_insert() throws Exception {
        String sql = "INSERT INTO users (id, name, email) VALUES (1, '张三', 'zhangsan@example.com')";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_insert_with_select() throws Exception {
        String sql = "INSERT INTO users_backup SELECT * FROM users WHERE status = 'active'";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_insert_multiple_values() throws Exception {
        String sql = "INSERT INTO users (id, name) VALUES (1, 'Alice'), (2, 'Bob'), (3, 'Charlie')";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
    }

    public void test_insert_with_returning() throws Exception {
        String sql = "INSERT INTO users (name, email) VALUES ('李四', 'lisi@example.com') RETURNING id INTO :new_id";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
    }
}
