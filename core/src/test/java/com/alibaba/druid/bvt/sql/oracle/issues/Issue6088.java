package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Oracle LISTAGG(...) WITHIN GROUP (...) OVER (PARTITION BY ...) must round-trip with balanced
 * parentheses (the reported symptom was a missing right parenthesis).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6088">Issue #6088</a>
 */
public class Issue6088 {
    @Test
    public void test_listagg_within_group_over() {
        String sql = "select listagg(nvl(a.orderid,0)*100,',') within group(order by t.time) "
                + "over(partition by t.create_time) as ids from a";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
        String out = SQLUtils.toSQLString(stmts.get(0), DbType.oracle);
        assertTrue(out.contains("WITHIN GROUP (ORDER BY t.time)"), out);
        assertTrue(out.contains("OVER (PARTITION BY t.create_time)"), out);
        long open = out.chars().filter(c -> c == '(').count();
        long close = out.chars().filter(c -> c == ')').count();
        assertEquals(open, close, "parentheses must be balanced:\n" + out);
    }
}
