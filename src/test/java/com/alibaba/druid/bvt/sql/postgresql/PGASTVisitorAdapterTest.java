package com.alibaba.druid.bvt.sql.postgresql;

import junit.framework.TestCase;

import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithQuery;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGParameter;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGFunctionTableSource;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.FetchClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.ForClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.WindowClause;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitorAdapter;

public class PGASTVisitorAdapterTest extends TestCase {

    public void test_adapter() throws Exception {
        PGASTVisitorAdapter adapter = new PGASTVisitorAdapter();

        new WindowClause().accept(adapter);
        new FetchClause().accept(adapter);
        new ForClause().accept(adapter);
        new PGWithQuery().accept(adapter);
        new PGWithClause().accept(adapter);
        new PGDeleteStatement().accept(adapter);
        new PGParameter().accept(adapter);
        new PGFunctionTableSource().accept(adapter);
    }
}
