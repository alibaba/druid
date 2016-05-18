package com.alibaba.druid.bvt.sql.schemaStat;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import junit.framework.TestCase;

public class SchemaStatTest4 extends TestCase {

    public void test_schemaStat() throws Exception {
        String sql = "select name, age from t_user where id = 1";

        String dbType = JdbcConstants.MYSQL;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(statVisitor);

        System.out.println(statVisitor.getColumns());
        System.out.println(statVisitor.getTables());
        System.out.println(statVisitor.getConditions());

        Assert.assertEquals(3, statVisitor.getColumns().size());
    }
}
