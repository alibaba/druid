package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLStructExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression guard for the end-of-line-comment bug: when a trailing {@code -- xxx} comment is
 * re-emitted by the output visitor, the following token must be pushed onto a new line, otherwise
 * it is swallowed into the comment and lost when the formatted SQL is parsed again.
 */
public class EndLineCommentRoundTripTest {
    @Test
    public void lineComment_doesNotSwallowNextColumn() {
        String sql = "select 1 -- c\n, b from t";

        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql, SQLParserFeature.KeepComments);
        assertEquals(1, stmts.size());
        String formatted = stmts.get(0).toString();

        // Re-parse WITHOUT keeping comments: the projection must still expose both columns. If the
        // line comment had swallowed ", b", the re-parsed query block would have a single item.
        SQLStatement reparsed = SQLUtils.parseStatements(formatted, DbType.mysql).get(0);
        SQLSelectQueryBlock queryBlock = ((SQLSelectStatement) reparsed).getSelect().getQueryBlock();
        assertEquals(2, queryBlock.getSelectList().size(),
                "column 'b' was swallowed by the line comment; round-trip lost a column:\n" + formatted);
    }

    @Test
    public void lineComment_inMultilineStructList_doesNotSwallowComma() {
        // The multi-line element separator goes through the char print(',') path; a trailing line
        // comment on a non-last element must not glue the separator comma onto the comment line.
        // >5 items forces SQLStructExpr into the multi-line (needPrintLine) branch.
        String sql = "select struct(a -- cmt\n, b, c, d, e, f) from t";

        String formatted = SQLUtils.parseStatements(sql, DbType.bigquery, SQLParserFeature.KeepComments)
                .get(0).toString();

        // Re-parse the formatted output and count the struct fields. If the separator comma had been
        // glued onto the "-- cmt" line, re-parsing would merge two fields and the count would drop.
        SQLStatement reparsed = SQLUtils.parseStatements(formatted, DbType.bigquery).get(0);
        SQLSelectQueryBlock queryBlock = ((SQLSelectStatement) reparsed).getSelect().getQueryBlock();
        SQLStructExpr struct = (SQLStructExpr) queryBlock.getSelectList().get(0).getExpr();
        assertEquals(6, struct.getItems().size(),
                "a struct field was swallowed by the line comment on the multi-line comma path:\n" + formatted);
    }
}
