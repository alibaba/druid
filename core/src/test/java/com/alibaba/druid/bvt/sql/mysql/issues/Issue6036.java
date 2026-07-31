package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证 ALTER TABLE 多个 ALTER INDEX ... VISIBLE/INVISIBLE 子句用逗号分隔时可正确解析
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6036">Issue来源</a>
 */
public class Issue6036 {
    @Test
    public void test_alter_index_multiple_clauses() {
        String sql = "ALTER TABLE t1 ALTER INDEX idx_1 INVISIBLE, ALTER INDEX idx_2 INVISIBLE";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());
        // 两个 ALTER INDEX 子句都应被解析为独立的 table item
        String result = statementList.get(0).toString();
        System.out.println("解析结果===" + result);
        assertEquals(2, ((com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement) statementList.get(0))
                .getItems().size());
        assertEquals("ALTER TABLE t1\n\tALTER INDEX idx_1  INVISIBLE,\n\tALTER INDEX idx_2  INVISIBLE", result);
    }

    @Test
    public void test_alter_index_mixed_clauses() {
        // 第一个 INVISIBLE、第二个 VISIBLE,确认两条子句都被解析且可见性标识正确
        String sql = "ALTER TABLE t1 ALTER INDEX idx_1 INVISIBLE, ALTER INDEX idx_2 VISIBLE";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());
        String result = statementList.get(0).toString();
        System.out.println("解析结果===" + result);
        assertEquals("ALTER TABLE t1\n\tALTER INDEX idx_1  INVISIBLE,\n\tALTER INDEX idx_2  VISIBLE", result);
    }

    @Test
    public void test_single_clause_still_works() {
        // 单条子句的解析行为不能因修复而回退
        String sql = "ALTER TABLE t1 ALTER INDEX idx_1 INVISIBLE";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());
        assertEquals("ALTER TABLE t1\n\tALTER INDEX idx_1  INVISIBLE", statementList.get(0).toString());
    }
}
