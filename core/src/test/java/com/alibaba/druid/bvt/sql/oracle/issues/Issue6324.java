package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Oracle 将 SEGMENT 用作列别名的解析测试。
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6324">issue #6324</a>
 */
public class Issue6324 {
    @Test
    public void segmentAsColumnAlias() {
        String sql = "SELECT bill_segment segment FROM t_billsas";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        List<SQLStatement> stmts = parser.parseStatementList();
        assertEquals(1, stmts.size());

        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) ((SQLSelectStatement) stmts.get(0)).getSelect().getQuery();
        assertEquals("segment", queryBlock.getSelectList().get(0).getAlias());

        String out = SQLUtils.toSQLString(stmts.get(0), DbType.oracle);
        assertTrue(out.contains("AS segment"), () -> "round-trip 应保留别名 segment: " + out);
    }

    @Test
    public void segmentInStorageClauseUnaffected() {
        // SEGMENT 作为 storage 子句关键字（非别名）的解析路径不受影响
        String sql = "CREATE TABLE t_billsas (id NUMBER) SEGMENT CREATION IMMEDIATE";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        List<SQLStatement> stmts = parser.parseStatementList();
        assertEquals(1, stmts.size());
    }
}
