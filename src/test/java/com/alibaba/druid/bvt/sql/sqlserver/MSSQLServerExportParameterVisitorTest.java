package com.alibaba.druid.bvt.sql.sqlserver;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.MSSQLServerExportParameterVisitor;

public class MSSQLServerExportParameterVisitorTest extends TestCase {

    public void test_sqlserver() throws Exception {
        String sql = "select fname, count(*) from t where fid = 1 group by fname order by 1";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        List<Object> parameters = new ArrayList<Object>();
        MSSQLServerExportParameterVisitor visitor = new MSSQLServerExportParameterVisitor(parameters);
        stmt.accept(visitor);
        
        Assert.assertEquals(1, parameters.size());
        Assert.assertEquals(1, parameters.get(0));
    }
}
