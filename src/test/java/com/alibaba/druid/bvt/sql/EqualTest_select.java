package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLAnyExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class EqualTest_select extends TestCase {

    public void test_eq_select() throws Exception {
        List stmtsA = SQLUtils.parseStatements("select * from a", JdbcConstants.ODPS);
        List stmtsB = SQLUtils.parseStatements("select * from b", JdbcConstants.ODPS);
        SQLSelect selectA = ((SQLSelectStatement) stmtsA.get(0)).getSelect();
        SQLSelect selectB = ((SQLSelectStatement) stmtsB.get(0)).getSelect();
        boolean eq = selectA.equals(selectB);
        assertFalse(eq);
    }
}
