package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGExportParameterVisitor;
import junit.framework.TestCase;

import java.util.List;

public class PGExportParameterVisitorTest1 extends TestCase {
    public void test_pg() throws Exception {
        String sql = "select fname, count(*) from t where fid = 1 group by fname order by 1";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        PGExportParameterVisitor visitor = new PGExportParameterVisitor();
        stmt.accept(visitor);

        List<Object> parameters = visitor.getParameters();

        assertEquals(1, parameters.size());
        assertEquals(1, parameters.get(0));
    }
}
