package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @see <a href="https://github.com/alibaba/druid/issues/6572">Issue来源</a>
 */
public class Issue6572 {

    @Test
    public void test_union_column_alias() {
        String sql = "select * from (select 1 union all select 2) AS serise_table(time)";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        String output = stmtList.get(0).toString();
        // Verify column alias is preserved
        assert output.contains("serise_table");
    }

    @Test
    public void test_union_multiple_column_aliases() {
        String sql = "select * from (select 1, 'a' union all select 2, 'b') AS t(id, name)";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }
}
