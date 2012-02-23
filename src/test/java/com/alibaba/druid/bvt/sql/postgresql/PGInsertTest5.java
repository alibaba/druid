package com.alibaba.druid.bvt.sql.postgresql;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class PGInsertTest5 extends PGTest {

    public void test_0() throws Exception {
        String sql = "WITH upd AS (" + //
                     "  UPDATE employees SET sales_count = sales_count + 1 WHERE id =" + //
                     "    (SELECT sales_person FROM accounts WHERE name = 'Acme Corporation')" + //
                     "    RETURNING *" + //
                     ")" + //
                     "INSERT INTO employees_log SELECT *, current_timestamp FROM upd;";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        System.out.println(output(statementList));

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getFields());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("distributors")));

        Assert.assertEquals(2, visitor.getFields().size());

        Assert.assertTrue(visitor.getFields().contains(new TableStat.Column("distributors", "did")));
        Assert.assertTrue(visitor.getFields().contains(new TableStat.Column("distributors", "dname")));
    }

}
