package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SQLASTVisitorInterfaceOptimizationTest {
    @Test
    public void test_tableSourceDelegation_forJoinTraversal() {
        SQLStatement stmt = SQLUtils.parseSingleStatement(
                "select * from t1 join t2 on t1.id = t2.id",
                DbType.mysql
        );

        TableSourceDelegationVisitor visitor = new TableSourceDelegationVisitor();
        stmt.accept(visitor);

        assertTrue(visitor.tableSourceVisitCount >= 3);
        assertEquals(visitor.tableSourceVisitCount, visitor.tableSourceEndVisitCount);
    }

    @Test
    public void test_specificVisitOverride_keepsCompatibility() {
        SQLStatement stmt = SQLUtils.parseSingleStatement("select * from t", DbType.mysql);

        SpecificVisitCompatibilityVisitor visitor = new SpecificVisitCompatibilityVisitor();
        stmt.accept(visitor);

        assertEquals(1, visitor.exprTableSourceVisitCount);
        assertTrue(visitor.tableSourceVisitCount <= 1);
    }

    private static class TableSourceDelegationVisitor extends SQLASTVisitorAdapter {
        int tableSourceVisitCount;
        int tableSourceEndVisitCount;

        @Override
        public boolean visitTableSource(SQLTableSource x) {
            tableSourceVisitCount++;
            return true;
        }

        @Override
        public void endVisitTableSource(SQLTableSource x) {
            tableSourceEndVisitCount++;
        }
    }

    private static class SpecificVisitCompatibilityVisitor extends SQLASTVisitorAdapter {
        int exprTableSourceVisitCount;
        int tableSourceVisitCount;

        @Override
        public boolean visit(SQLExprTableSource x) {
            exprTableSourceVisitCount++;
            return true;
        }

        @Override
        public boolean visitTableSource(SQLTableSource x) {
            tableSourceVisitCount++;
            return true;
        }
    }
}
