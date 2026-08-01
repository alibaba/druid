package com.alibaba.druid.bvt.sql.doris.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Doris {@code INSERT OVERWRITE TABLE ... PARTITION(*)} 解析测试。
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6350">issue #6350</a>
 */
public class Issue6350 {
    @Test
    public void insertOverwritePartitionStar() {
        String sql = "INSERT OVERWRITE TABLE t PARTITION(*) SELECT * FROM t";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.doris);
        List<SQLStatement> stmts = parser.parseStatementList();
        assertEquals(1, stmts.size());

        SQLInsertStatement insert = (SQLInsertStatement) stmts.get(0);
        assertEquals(1, insert.getPartitions().size());

        // AST 级断言：PARTITION(*) 的 target 应为通配符节点 SQLAllColumnExpr，
        // 而非依赖 round-trip 输出里是否含 "*"（SELECT * 也会满足后者，无法区分）。
        SQLAssignItem partition = insert.getPartitions().get(0);
        assertInstanceOf(SQLAllColumnExpr.class, partition.getTarget(),
                () -> "PARTITION(*) target 应为 SQLAllColumnExpr，实际为: "
                        + partition.getTarget().getClass().getSimpleName());
    }
}
