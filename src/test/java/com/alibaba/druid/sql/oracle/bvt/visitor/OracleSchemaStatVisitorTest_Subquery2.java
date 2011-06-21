package com.alibaba.druid.sql.oracle.bvt.visitor;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.stat.TableStat.Column;

public class OracleSchemaStatVisitorTest_Subquery2 extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT a.id, a.name, b.name groupName FROM (select id, name, groupId from users WHERE ROWNUM < 10) a inner join groups b on a.groupId = b.id";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println(sql);
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getFields());

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(true, visitor.getTables().containsKey("users"));
        Assert.assertEquals(true, visitor.getTables().containsKey("groups"));

        Assert.assertEquals(5, visitor.getFields().size());
        Assert.assertEquals(true, visitor.getFields().contains(new Column("users", "id")));
        Assert.assertEquals(true, visitor.getFields().contains(new Column("users", "groupId")));
        Assert.assertEquals(true, visitor.getFields().contains(new Column("users", "name")));
        Assert.assertEquals(true, visitor.getFields().contains(new Column("groups", "id")));
        Assert.assertEquals(true, visitor.getFields().contains(new Column("groups", "name")));

    }

}
