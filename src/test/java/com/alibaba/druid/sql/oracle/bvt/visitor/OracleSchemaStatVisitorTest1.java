package com.alibaba.druid.sql.oracle.bvt.visitor;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.stat.TableStat.Column;

public class OracleSchemaStatVisitorTest1 extends TestCase {

    public void test_0() throws Exception {
        String sql = "select a.name, b.name FROM users a, usergroups b on a.groupId = b.id";

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
        Assert.assertEquals(true, visitor.getTables().containsKey("usergroups"));

        Assert.assertEquals(4, visitor.getFields().size());
        Assert.assertEquals(true, visitor.getFields().contains(new Column("users", "groupId")));
        Assert.assertEquals(true, visitor.getFields().contains(new Column("users", "name")));
        Assert.assertEquals(true, visitor.getFields().contains(new Column("usergroups", "id")));
        Assert.assertEquals(true, visitor.getFields().contains(new Column("usergroups", "name")));

    }
    
    public void test_1() throws Exception {
        String sql = "select a.name, b.name FROM users a, usergroups b on a.groupId = b.id where a.groupID = ?";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println(sql);
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getFields());

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(true, visitor.getTables().containsKey("users"));
        Assert.assertEquals(true, visitor.getTables().containsKey("usergroups"));

        Assert.assertEquals(4, visitor.getFields().size());
        Assert.assertEquals(true, visitor.getFields().contains(new Column("users", "groupId")));
        Assert.assertEquals(true, visitor.getFields().contains(new Column("users", "name")));
        Assert.assertEquals(true, visitor.getFields().contains(new Column("usergroups", "id")));
        Assert.assertEquals(true, visitor.getFields().contains(new Column("usergroups", "name")));

    }
}
