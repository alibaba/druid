package com.alibaba.druid.bvt.sql.schemaStat;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import junit.framework.TestCase;

public class SchemaStatTest6 extends TestCase {
    public void test_schemaStat() throws Exception {
String sql = "select count(1), name from tg_rpc_user where id < 5 group by name order by id desc";


String dbType = JdbcConstants.MYSQL;
SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
SQLStatement stmt = parser.parseStatementList().get(0);

SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
stmt.accept(statVisitor);

System.out.println(statVisitor.getColumns());
System.out.println(statVisitor.getGroupByColumns()); // group by
        
        Assert.assertEquals(2, statVisitor.getColumns().size());
    }
}
