package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlCreateTableTest110_drds extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table a (id int(10)) dbpartition by hash(id) dbpartitionS 2 tbpartition by hash(id) tbpartitions 2 ";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(1, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE a (\n" +
                "\tid int(10)\n" +
                ")\n" +
                "DBPARTITION BY hash(id) DBPARTITIONS 2\n" +
                "TBPARTITION BY hash(id) TBPARTITIONS 2", stmt.toString());
    }

}