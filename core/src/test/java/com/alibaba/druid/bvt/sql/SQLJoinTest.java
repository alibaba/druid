package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by wenshao on 03/08/2017.
 */
public class SQLJoinTest {
    @Test
    public void test_0() throws Exception {
        SQLSelectStatement stmt = (SQLSelectStatement)
                SQLUtils.parseStatements("select a.* from t_user a inner join t_group b on a.gid = b.id", JdbcConstants.ORACLE)
                        .get(0);

        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();
        assertNotNull(queryBlock);

        SQLJoinTableSource join = (SQLJoinTableSource) queryBlock.getFrom();
        assertTrue(join.match("a", "b"));
    }
}
