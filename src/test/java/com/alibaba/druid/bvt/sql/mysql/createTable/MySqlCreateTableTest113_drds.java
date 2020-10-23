package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlCreateTableTest113_drds extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table if not exists test_table(\n" +
                "  id INT,name VARCHAR(30) DEFAULT NULL,\n" +
                "  create_time DATETIME DEFAULT NULL\n" +
                ")ENGINE = InnoDB DEFAULT CHARSET = utf8 \n" +
                "dbpartition BY YYYYMM_NOLOOP (create_time) \n" +
                "tbpartition BY YYYYMM_NOLOOP (create_time) \n" +
                "  STARTWITH 20160108 ENDWITH 20170108;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(3, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE IF NOT EXISTS test_table (\n" +
                "\tid INT,\n" +
                "\tname VARCHAR(30) DEFAULT NULL,\n" +
                "\tcreate_time DATETIME DEFAULT NULL\n" +
                ") ENGINE = InnoDB CHARSET = utf8\n" +
                "DBPARTITION BY YYYYMM_NOLOOP(create_time)\n" +
                "TBPARTITION BY YYYYMM_NOLOOP(create_time) BETWEEN 20160108 AND 20170108;", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "create table if not exists test_table(\n" +
                "  id INT,name VARCHAR(30) DEFAULT NULL,\n" +
                "  create_time DATETIME DEFAULT NULL\n" +
                ")ENGINE = InnoDB DEFAULT CHARSET = utf8 \n" +
                "dbpartition BY YYYYMM_NOLOOP (create_time) \n" +
                "tbpartition BY YYYYMM_NOLOOP (create_time) \n" +
                "  BETWEEN 20160108 AND 20170108;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(3, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE IF NOT EXISTS test_table (\n" +
                "\tid INT,\n" +
                "\tname VARCHAR(30) DEFAULT NULL,\n" +
                "\tcreate_time DATETIME DEFAULT NULL\n" +
                ") ENGINE = InnoDB CHARSET = utf8\n" +
                "DBPARTITION BY YYYYMM_NOLOOP(create_time)\n" +
                "TBPARTITION BY YYYYMM_NOLOOP(create_time) BETWEEN 20160108 AND 20170108;", stmt.toString());
    }
}