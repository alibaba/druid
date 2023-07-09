package com.alibaba.druid.bvt.sql.mysql.select;

import java.util.List;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

public class MySqlSelectTest_partition extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "select\n"
            + "        p.id as id,\n"
            + "        c.template_no as templateNo\n"
            + "    from\n"
            + "      contract_relation_party partition(p0) p\n"
            + "    left join\n"
            + "      contract partition(p0) c\n"
            + "    on\n"
            + "      p.contract_sn = c.contract_sn\n"
            + "      and p.contract_id_no = c.id_no\n"
            + "    where p.seal_type is null\n"
            + "    order by p.update_time asc\n"
            + "    limit 5";

        //System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals(1, statementList.size());
        //System.out.println(stmt.toString());
        assertEquals("SELECT p.id AS id, c.template_no AS templateNo\n"
            + "FROM contract_relation_party PARTITION (p0) p\n"
            + "\tLEFT JOIN contract PARTITION (p0) c\n"
            + "\tON p.contract_sn = c.contract_sn\n"
            + "\t\tAND p.contract_id_no = c.id_no\n"
            + "WHERE p.seal_type IS NULL\n"
            + "ORDER BY p.update_time ASC\n"
            + "LIMIT 5", stmt.toString());
    }
}