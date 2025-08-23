package com.alibaba.druid.bvt.sql.phoenix;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.phoenix.visitor.PhoenixSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Created by wenshao on 16/9/13.
 */
public class PhoenixUpsertTest_std extends TestCase {
    public void test_0() throws Exception {
        String sql = "upsert into t_1 (a,b,c) values (?,?,?)";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, (DbType) null);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = new PhoenixSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("t_1")));

        assertTrue(visitor.getColumns().contains(new TableStat.Column("t_1", "a")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("t_1", "b")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("t_1", "c")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        String output = SQLUtils.toSQLString(stmt, JdbcConstants.PHOENIX);
        assertEquals("UPSERT INTO t_1 (a, b, c)\n" +
                        "VALUES (?, ?, ?)", //
                output);
    }
}
