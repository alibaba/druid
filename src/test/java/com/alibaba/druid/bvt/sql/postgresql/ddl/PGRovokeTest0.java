package com.alibaba.druid.bvt.sql.postgresql.ddl;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;


public class PGRovokeTest0 extends PGTest {
    public void test_0 () throws Exception {
        String sql = "REVOKE ALL ON accounts FROM PUBLIC;";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("accounts")));
        
        Assert.assertTrue(visitor.getTables().get(new TableStat.Name("accounts")).getDropCount() == 0);
        Assert.assertTrue(visitor.getTables().get(new TableStat.Name("accounts")).getAlterCount() == 0);

        Assert.assertTrue(visitor.getColumns().size() == 0);
    }
}
