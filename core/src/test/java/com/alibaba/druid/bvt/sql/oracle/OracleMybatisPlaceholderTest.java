package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression for issue #5172: Oracle's lexer did not allow {@code .} inside MyBatis
 * placeholders, so {@code ${a.b}} raised {@code ParserException: syntax error} while
 * mysql/postgresql already accepted it.
 */
public class OracleMybatisPlaceholderTest {
    @Test
    public void mybatisPlaceholderWithDotParses() {
        String sql = "select * from table_a where b.b1 = ${a.basdf}";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "oracle");
        assertEquals(1, stmts.size());

        String formatted = SQLUtils.formatOracle(sql);
        assertEquals("SELECT *\nFROM table_a\nWHERE b.b1 = ${a.basdf}", formatted);
    }

    @Test
    public void mybatisPlaceholderNestedDotParses() {
        String sql = "select * from t where b = ${a.b.c}";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "oracle");
        assertEquals(1, stmts.size());
    }

    @Test
    public void hashPlaceholderWithDotParses() {
        // The same scan path covers #{...} placeholders.
        String sql = "select * from t where b = #{a.b}";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "oracle");
        assertEquals(1, stmts.size());
    }

    @Test
    public void simplePlaceholderStillWorks() {
        // Regression guard: a placeholder without a dot must keep working.
        String sql = "select * from t where b = ${a}";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "oracle");
        assertEquals(1, stmts.size());
    }
}
