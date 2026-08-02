package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterUserStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Regression for issue #4306: {@code ALTER USER ... IDENTIFIED WITH plugin AS 'hash'}
 * (and {@code BY 'auth_string'}) failed to parse — only {@code IDENTIFIED BY 'pass'}
 * was accepted, while {@code CREATE USER} already supported the WITH form.
 */
public class MySqlAlterUserIdentifiedWithTest {
    @Test
    public void identifiedWithAs() {
        String sql = "ALTER USER 'root'@'%' IDENTIFIED WITH 'mysql_native_password' AS '*F3A2A51A9B0F2BE2468926B4132313728C250DBF'";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "mysql");
        assertEquals(1, stmts.size());

        MySqlAlterUserStatement stmt = (MySqlAlterUserStatement) stmts.get(0);
        MySqlAlterUserStatement.AuthOption auth = stmt.getAlterUsers().get(0).getAuthOption();
        assertNotNull(auth.getAuthPlugin(), "auth plugin must be captured");
        assertNotNull(auth.getPassword(), "hash must be captured");
        assertEquals(true, auth.isPluginAs(), "AS form must be flagged");

        String formatted = SQLUtils.toSQLString(stmt, com.alibaba.druid.DbType.mysql);
        assertEquals("ALTER USER 'root'@'%' IDENTIFIED WITH 'mysql_native_password' AS '*F3A2A51A9B0F2BE2468926B4132313728C250DBF'", formatted);
    }

    @Test
    public void identifiedWithBy() {
        String sql = "ALTER USER 'u'@'%' IDENTIFIED WITH 'plugin' BY 'secret'";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "mysql");
        MySqlAlterUserStatement.AuthOption auth = ((MySqlAlterUserStatement) stmts.get(0))
                .getAlterUsers().get(0).getAuthOption();
        assertNotNull(auth.getAuthPlugin());
        assertNotNull(auth.getPassword());
        assertEquals(false, auth.isPluginAs());
    }

    @Test
    public void identifiedByStillWorks() {
        // Regression guard: the original IDENTIFIED BY 'pass' form must keep working.
        String sql = "ALTER USER u IDENTIFIED BY 'secret'";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "mysql");
        assertEquals(1, stmts.size());

        MySqlAlterUserStatement.AuthOption auth = ((MySqlAlterUserStatement) stmts.get(0))
                .getAlterUsers().get(0).getAuthOption();
        assertNotNull(auth.getAuthString());
    }
}
