package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.spi.PGWallProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Visiting a PostgreSQL-specific expression (EXTRACT, ::cast, ...) with a non-PG visitor used to
 * throw ClassCastException (SQLEvalVisitorImpl / SchemaResolveVisitor cannot be cast to PGASTVisitor),
 * e.g. during WallFilter eval or schema resolve. PGExprImpl now checks instanceof before casting.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6633">Issue #6633</a>
 * @see <a href="https://github.com/alibaba/druid/issues/6158">Issue #6158</a>
 * @see <a href="https://github.com/alibaba/druid/issues/5201">Issue #5201</a>
 */
public class Issue6633 {
    private static final String[] SQLS = {
            "select * from a where extract(epoch from current_date)::bigint > 1",
            "select EXTRACT(EPOCH FROM COALESCE(a, now())) - EXTRACT(EPOCH FROM b) as s from t",
    };

    @Test
    public void test_resolve_does_not_throw_cce() {
        for (String sql : SQLS) {
            SchemaRepository repo = new SchemaRepository(DbType.postgresql);
            List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
            for (SQLStatement stmt : stmts) {
                repo.resolve(stmt);
            }
        }
    }

    @Test
    public void test_wall_eval_does_not_throw_cce() {
        PGWallProvider provider = new PGWallProvider();
        for (String sql : SQLS) {
            WallCheckResult result = provider.check(sql);
            assertNotNull(result);
        }
    }

    @Test
    public void test_select_after_extract_cast_parses() {
        List<SQLStatement> stmts = SQLUtils.parseStatements(SQLS[0], DbType.postgresql);
        assertEquals(1, stmts.size());
    }
}
