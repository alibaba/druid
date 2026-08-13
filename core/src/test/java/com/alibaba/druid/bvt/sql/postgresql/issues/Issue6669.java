package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PostgreSQL {@code CREATE TEMPORARY TABLE} / {@code CREATE TEMP TABLE}（无 GLOBAL 前缀）的解析测试。
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6669">issue #6669</a>
 */
public class Issue6669 {
    private static List<SQLStatement> parsePg(String sql) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        return parser.parseStatementList();
    }

    private static void assertTemporary(String sql) {
        List<SQLStatement> stmts = parsePg(sql);
        assertEquals(1, stmts.size(), () -> "应解析为 1 条语句: " + sql);
        assertTrue(((SQLCreateTableStatement) stmts.get(0)).isTemporary(), () -> "应识别为 temporary: " + sql);
    }

    @Test
    public void createTemporaryTable_noPrefix() {
        // issue #6669：GLOBAL/LOCAL 前缀省略的 TEMPORARY 表
        String sql = "CREATE TEMPORARY TABLE tbl (id INT)";
        assertTemporary(sql);
        String out = SQLUtils.toSQLString(parsePg(sql).get(0), DbType.postgresql);
        assertTrue(out.contains("TEMPORARY TABLE"), () -> "输出应含 TEMPORARY TABLE: " + out);
    }

    @Test
    public void createTempTable_pgShorthand() {
        // PG 的 TEMP 简写
        assertTemporary("CREATE TEMP TABLE t (a INT)");
    }

    @Test
    public void createGlobalTemporaryTable_unchanged() {
        // 既有 GLOBAL TEMPORARY 形式不受影响（回归保护）
        assertTemporary("CREATE GLOBAL TEMPORARY TABLE t (a INT)");
    }

    @Test
    public void bareTemp_rejectedByOracle() {
        // Oracle 要求 GLOBAL/LOCAL TEMPORARY，不接受裸 TEMP 形式。
        // TEMPORARY/TEMP 处理置于 PGCreateTableParser 而非基类，确保 Oracle 等方言不受影响。
        String sql = "CREATE TEMP TABLE t (id NUMBER)";
        SQLStatementParser oracleParser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        assertThrows(ParserException.class, oracleParser::parseStatementList,
                "Oracle 应拒绝裸 CREATE TEMP TABLE");
    }
}
