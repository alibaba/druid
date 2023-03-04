package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by wenshao on 12/07/2017.
 */
public class Issue2049 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select * from emp a,dmp b;";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);

        SQLSelectStatement stmt = (SQLSelectStatement) stmtList.get(0);
        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();
        SQLJoinTableSource joinTableSource = (SQLJoinTableSource) queryBlock.getFrom();

        assertEquals("a", joinTableSource.getLeft().getAlias());
        assertEquals("b", joinTableSource.getRight().getAlias());
    }
}
