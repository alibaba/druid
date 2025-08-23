package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import static org.junit.Assert.*;

import java.util.List;

public class MySqlCreateTableTest92 extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "CREATE TABLE `test` (\n"
                + "  `id`  bigint(20) unsigned zerofill NOT NULL AUTO_INCREMENT COMMENT 'id',\n"
                + "  `c_set` set('a','b','c') COMMENT 'set',\n" + "  PRIMARY KEY (`id`)\n"
                + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='10000000';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement statemen = (MySqlCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(3, statemen.getTableElementList().size());
        assertEquals("`c_set` set('a', 'b', 'c') COMMENT 'set'", SQLUtils.toMySqlString(statemen.getTableElementList().get(1)));

    }
}