package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MySQL 8 JSON predicate: value MEMBER OF (json_array).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6534">Issue #6534</a>
 */
public class Issue6534 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.mysql).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_member_of_param() {
        assertTrue(rt("select * from project p where ? member of(p.participants -> '$.*[*]') order by p.id desc")
                .contains("? MEMBER OF (p.participants -> '$.*[*]')"));
    }

    @Test
    public void test_member_of_column() {
        assertEquals("SELECT * FROM t WHERE x MEMBER OF (arr)",
                rt("select * from t where x member of (arr)"));
    }

    @Test
    public void test_member_of_literal_in_and() {
        assertTrue(rt("select * from t where a = 1 and 5 member of (data)")
                .contains("5 MEMBER OF (data)"));
    }
}
