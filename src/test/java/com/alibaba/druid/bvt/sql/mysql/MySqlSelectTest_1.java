package com.alibaba.druid.bvt.sql.mysql;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlSelectTest_1 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT t1.name, t2.salary FROM employee t1, info t2  WHERE t1.name = t2.name;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employee")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("info")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("employee", "name")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("info", "name")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("info", "salary")));
    }
}
