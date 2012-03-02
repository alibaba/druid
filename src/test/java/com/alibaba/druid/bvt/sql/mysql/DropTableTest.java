package com.alibaba.druid.bvt.sql.mysql;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class DropTableTest extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "DROP TABLE films, distributors;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("films")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("distributors")));
        
        Assert.assertTrue(visitor.getTables().get(new TableStat.Name("films")).getDropCount() == 1);
        Assert.assertTrue(visitor.getTables().get(new TableStat.Name("distributors")).getDropCount() == 1);

        Assert.assertTrue(visitor.getColumns().size() == 0);
    }

   
}
