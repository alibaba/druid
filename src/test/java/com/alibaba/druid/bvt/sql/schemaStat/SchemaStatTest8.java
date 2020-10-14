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

public class SchemaStatTest8 extends TestCase {

    public void test_schemaStat() throws Exception {
        String sql = "SELECT * FROM AQLQCAT " +
                "JOIN AQLQCATB " +
                "   ON AQLQCAT.ZZ = AQLQCATB.RR " +
                "       AND trim(AQLQCAT.BB) = AQLQCATB.DD " +
                "WHERE AQLQCAT.MANDT = 'A0' " +
                "   AND AQLQCATB.NUM = 'A1'";

        DbType dbType = JdbcConstants.ORACLE;
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatementList().get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(statVisitor);

//        System.out.println(statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by

        Assert.assertEquals(8, statVisitor.getColumns().size());
        Assert.assertEquals(6, statVisitor.getConditions().size());
    }
}
