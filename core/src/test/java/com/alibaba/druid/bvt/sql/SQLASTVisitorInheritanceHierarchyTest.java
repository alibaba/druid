package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SQLASTVisitorInheritanceHierarchyTest {
    @Test
    public void test_withEntryDelegatesToTableSourceHooks() {
        SQLStatement stmt = SQLUtils.parseSingleStatement(
                "with cte as (select 1 as id) select * from cte",
                DbType.mysql
        );

        WithEntryDelegationVisitor visitor = new WithEntryDelegationVisitor();
        stmt.accept(visitor);

        assertEquals(1, visitor.withEntryVisitBySpecificMethod);
        assertEquals(1, visitor.withEntryVisitByTableSourceHook);
        assertEquals(1, visitor.withEntryEndVisitByTableSourceHook);
    }

    private static class WithEntryDelegationVisitor extends SQLASTVisitorAdapter {
        int withEntryVisitBySpecificMethod;
        int withEntryVisitByTableSourceHook;
        int withEntryEndVisitByTableSourceHook;

        @Override
        public boolean visit(SQLWithSubqueryClause.Entry x) {
            withEntryVisitBySpecificMethod++;
            return super.visit(x);
        }

        @Override
        public boolean visitTableSource(SQLTableSource x) {
            if (x instanceof SQLWithSubqueryClause.Entry) {
                withEntryVisitByTableSourceHook++;
            }
            return true;
        }

        @Override
        public void endVisitTableSource(SQLTableSource x) {
            if (x instanceof SQLWithSubqueryClause.Entry) {
                withEntryEndVisitByTableSourceHook++;
            }
        }
    }
}
