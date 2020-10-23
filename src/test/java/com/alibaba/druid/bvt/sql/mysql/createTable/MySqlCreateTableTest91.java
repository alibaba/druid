package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.Assert;

import java.util.List;

public class MySqlCreateTableTest91 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE tbl_name(id int, sid int, name varchar(8)) " + //
                "PARTITION BY LINEAR KEY ALGORITHM=2 (id, sid) PARTITIONS 4 (PARTITION p0, PARTITION p1, PARTITION p2, PARTITION p3)";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());
    }

    public void test_1() throws Exception {
        String sql = "CREATE TABLE tbl_name(id int, sid int, name varchar(8)) " + //
                "PARTITION BY LINEAR KEY ALGORITHM=2 (id, sid) PARTITIONS 4 " + //
                "SUBPARTITION BY LINEAR KEY ALGORITHM=2 (id, sid) SUBPARTITIONS 2 " + //
                "(PARTITION p0 (SUBPARTITION s0, SUBPARTITION s1), " + //
                "PARTITION p1 (SUBPARTITION s0, SUBPARTITION s1), " + //
                "PARTITION p2 (SUBPARTITION s0, SUBPARTITION s1), " + //
                "PARTITION p3 (SUBPARTITION s0, SUBPARTITION s1))";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());
    }
}