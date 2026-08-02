package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLGrantStatement;
import com.alibaba.druid.sql.ast.statement.SQLObjectType;
import com.alibaba.druid.sql.ast.statement.SQLRevokeStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PostgreSQL GRANT/REVOKE ... ON DATABASE / ON SCHEMA 解析报错。
 *
 * @author fudianchn
 * @see <a href="https://github.com/alibaba/druid/issues/5964">Issue #5964</a>
 */
public class Issue5964Test {
    @Test
    public void pgGrantOnSchema() {
        String sql = "GRANT ALL PRIVILEGES ON SCHEMA public TO your_username";
        SQLGrantStatement stmt = (SQLGrantStatement) SQLUtils.parseStatements(sql, "postgresql").get(0);
        assertEquals(SQLObjectType.SCHEMA, stmt.getResourceType());
    }

    @Test
    public void pgRevokeOnDatabase() {
        String sql = "REVOKE ALL PRIVILEGES ON DATABASE mydb FROM username";
        SQLRevokeStatement stmt = (SQLRevokeStatement) SQLUtils.parseStatements(sql, "postgresql").get(0);
        assertEquals(SQLObjectType.DATABASE, stmt.getResourceType());
    }

    @Test
    public void pgRevokeOnSchema() {
        String sql = "REVOKE ALL PRIVILEGES ON SCHEMA public FROM your_username";
        SQLRevokeStatement stmt = (SQLRevokeStatement) SQLUtils.parseStatements(sql, "postgresql").get(0);
        assertEquals(SQLObjectType.SCHEMA, stmt.getResourceType());
    }
}
