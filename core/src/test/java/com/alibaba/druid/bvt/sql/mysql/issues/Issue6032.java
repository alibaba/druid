package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A back-quoted table name containing the word "check" must parse (the CHECK keyword inside a
 * quoted identifier is not a constraint).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6032">Issue #6032</a>
 */
public class Issue6032 {
    @Test
    public void test_backquoted_table_name_with_check() {
        String sql = "SELECT `email` FROM `hr`.`background-check-info`";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.mysql).contains("`background-check-info`"));
    }
}
