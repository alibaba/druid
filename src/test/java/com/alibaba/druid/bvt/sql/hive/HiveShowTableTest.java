package com.alibaba.druid.bvt.sql.hive;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;
import com.alibaba.druid.sql.hive.HiveTest;
import com.alibaba.druid.stat.TableStat;

public class HiveShowTableTest extends HiveTest {

    public void test_hive() throws Exception {
        String sql = "SHOW TABLES";

        HiveStatementParser parser = new HiveStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        System.out.println(output(statementList));

        Assert.assertEquals(1, statementList.size());

        HiveSchemaStatVisitor visitor = new HiveSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("invites")));

        Assert.assertEquals(0, visitor.getColumns().size());
        
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("invites", "foo")));
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("invites", "bar")));
    }
}
