package com.alibaba.druid.demo.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import java.io.File;
import java.util.List;
import java.util.Set;

public class SchemaStatTest_odps extends TestCase {

    public void test_schemaStat() throws Exception {
        File file = new File("/Users/wenshao/Downloads/odps_sql_1.txt");
        String sql = FileUtils.readFileToString(file);

        DbType dbType = DbType.odps;
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();

        System.out.println("stmtList size : " + stmtList.size());

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(statVisitor);
        }

        Set<TableStat.Relationship> relationships = statVisitor.getRelationships();
        for (TableStat.Relationship relationship : relationships) {
            System.out.println(relationship); // table1.id = table2.id
        }

//        System.out.println(statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println(statVisitor.getConditions());
//        assertEquals(3, relationships.size());
//
//        Assert.assertEquals(21, statVisitor.getColumns().size());
//        Assert.assertEquals(20, statVisitor.getConditions().size());
//        assertEquals(1, statVisitor.getFunctions().size());
    }
}
