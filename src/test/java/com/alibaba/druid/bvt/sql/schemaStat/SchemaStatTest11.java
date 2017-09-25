package com.alibaba.druid.bvt.sql.schemaStat;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Set;

public class SchemaStatTest11 extends TestCase {

    public void test_schemaStat() throws Exception {
        String sql = "select a.id, b.name from (select * from table1) a inner join table2 b on a.id = b.id";

        String dbType = JdbcConstants.ORACLE;
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatementList().get(0);

        System.out.println(stmt);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(statVisitor);

        Set<TableStat.Relationship> relationships = statVisitor.getRelationships();
        for (TableStat.Relationship relationship : relationships) {
            System.out.println(relationship); // table1.id = table2.id
        }

        System.out.println(statVisitor.getColumns());
        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println(statVisitor.getConditions());
        assertEquals(1, relationships.size());

        Assert.assertEquals(3, statVisitor.getColumns().size());
        Assert.assertEquals(2, statVisitor.getConditions().size());
        assertEquals(0, statVisitor.getFunctions().size());
    }
}
