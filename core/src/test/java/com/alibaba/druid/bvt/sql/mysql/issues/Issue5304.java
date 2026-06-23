package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GROUP_CONCAT(expr SEPARATOR '...') with a multi-byte separator.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/5304">Issue #5304</a>
 */
public class Issue5304 {
    @Test
    public void test_group_concat_separator() {
        String sql = "SELECT GROUP_CONCAT(name SEPARATOR '、') FROM t";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.mysql).contains("SEPARATOR '、'"));
    }
}
