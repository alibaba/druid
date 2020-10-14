package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest105 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE DIMENSION TABLE tpch_junlan.nation (\n" +
                "  n_nationkey int NOT NULL COMMENT '',\n" +
                "  n_name varchar NOT NULL COMMENT '',\n" +
                "  n_regionkey int NOT NULL COMMENT '',\n" +
                "  n_comment varchar COMMENT '',\n" +
                "  PRIMARY KEY (N_NATIONKEY)\n" +
                ")\n" +
                "OPTIONS (UPDATETYPE='realtime')\n" +
                "COMMENT ''";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(5, stmt.getTableElementList().size());

        assertEquals("CREATE DIMENSION TABLE tpch_junlan.nation (\n"
                     + "\tn_nationkey int NOT NULL COMMENT '',\n"
                     + "\tn_name varchar NOT NULL COMMENT '',\n"
                     + "\tn_regionkey int NOT NULL COMMENT '',\n"
                     + "\tn_comment varchar COMMENT '',\n"
                     + "\tPRIMARY KEY (N_NATIONKEY)\n"
                     + ")\n"
                     + "OPTIONS (UPDATETYPE = 'realtime') COMMENT ''", stmt.toString());
    }
}