package com.alibaba.druid.bvt.sql.clickhouse;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.clickhouse.parser.CKStatementParser;
import com.alibaba.druid.sql.dialect.clickhouse.visitor.CKStatVisitor;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;

import junit.framework.TestCase;

public class CK_update_0 extends TestCase {

    public void test_0() throws Exception {
        String sql = "alter table tb1 ON CLUSTER 'cluster' update A=1,B=1,C=2  where ID = 1;"
            + "alter table tb1 ON CLUSTER cluster update A=3,B=4,C=5  where ID = 2;";
        CKStatementParser parser=new CKStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        System.out.println(statementList);
        assertEquals(2, statementList.size());

        CKStatVisitor visitor = new CKStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsTable("tb1"));

        // assertTrue(visitor.getColumns().contains(new Column("mytable", "last_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        String output = SQLUtils.toSQLString(stmt, DbType.clickhouse);
        System.out.println("output : " + output);
        assertEquals("ALTER TABLE tb1 ON CLUSTER 'cluster' UPDATE A = 1, B = 1, C = 2 WHERE ID = 1;", //
            output);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, DbType.clickhouse);
        System.out.println("psql : " + psql);
        assertEquals("ALTER TABLE tb1 ON CLUSTER ? UPDATE A = ?, B = ?, C = ? WHERE ID = ?;", psql);
    }


    public void test_1() throws Exception {
        String sql = "alter table tb1 update A=1,B=1,C=2  where ID = 1;"
            + "alter table tb1 update A=3,B=4,C=5  where ID = 2;";
        CKStatementParser parser=new CKStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        System.out.println(statementList);
        assertEquals(2, statementList.size());

        CKStatVisitor visitor = new CKStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsTable("tb1"));

        // assertTrue(visitor.getColumns().contains(new Column("mytable", "last_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        String output = SQLUtils.toSQLString(stmt, DbType.clickhouse);
        System.out.println("output : " + output);
        assertEquals("ALTER TABLE tb1 UPDATE A = 1, B = 1, C = 2 WHERE ID = 1;", //
            output);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, DbType.clickhouse);
        System.out.println("psql : " + psql);
        assertEquals("ALTER TABLE tb1 UPDATE A = ?, B = ?, C = ? WHERE ID = ?;", psql);
    }


    public void test_3() throws Exception {
            String sql = "alter table tb1 ON CLUSTER cluster update A=1,B=1,C=2 IN PARTITION partition_id where ID = 1;"
            + "alter table tb1 ON CLUSTER cluster update A=3,B=4,C=5 IN PARTITION partition_id where ID = 2;";
        CKStatementParser parser=new CKStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        assertEquals(2, statementList.size());

        CKStatVisitor visitor = new CKStatVisitor();
        stmt.accept(visitor);

        System.out.println("test_3.Tables : " + visitor.getTables());
        System.out.println("test_3.fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsTable("tb1"));

        // assertTrue(visitor.getColumns().contains(new Column("mytable", "last_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        String output = SQLUtils.toSQLString(stmt, DbType.clickhouse);
        System.out.println("output : " + output);
        assertEquals("ALTER TABLE tb1 ON CLUSTER cluster UPDATE A = 1, B = 1, C = 2 IN PARTITION partition_id WHERE ID = 1;", //
            output);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, DbType.clickhouse);
        System.out.println("psql : " + psql);
        assertEquals("ALTER TABLE tb1 ON CLUSTER ? UPDATE A = ?, B = ?, C = ? IN PARTITION ? WHERE ID = ?;", psql);
    }

    public void test_4() throws Exception {
        String sql="update tb1 set A=1,B=1,C=2 IN aaa where ID = 1;";
        MySqlStatementParser mp=new MySqlStatementParser(sql);
        List<SQLStatement> statementList = mp.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        System.out.println(statementList);

        String output = SQLUtils.toSQLString(stmt, DbType.mysql);
        System.out.println("output : " + output);
        assertEquals("UPDATE tb1\n"
                + "SET A = 1, B = 1, C = 2 IN (aaa)\n"
                + "WHERE ID = 1;", //
            output);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, DbType.mysql);
        System.out.println("psql : " + psql);
        assertEquals("UPDATE tb1\n"
            + "SET A = ?, B = ?, C = ? IN (aaa)\n"
            + "WHERE ID = ?;", psql);

    }
}
