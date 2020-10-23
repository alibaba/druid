package com.alibaba.druid.bvt.sql.schemaStat;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

public class SchemaStatTest7 extends TestCase {

    public void test_schemaStat() throws Exception {
        String sql = "select t.a,t.b from t where cast(t.statdate as decimal) >=20160626";

        DbType dbType = JdbcConstants.MYSQL;
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatementList().get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(statVisitor);

//        System.out.println(statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by

        Assert.assertEquals(3, statVisitor.getColumns().size());
        Assert.assertEquals(1, statVisitor.getConditions().size());
    }
}
