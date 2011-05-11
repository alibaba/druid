package com.alibaba.druid.sql.mysql.bvt;

import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlSelectParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySql2OracleOutputVisitor;
import com.alibaba.druid.sql.parser.SQLSelectParser;

public class SQLParserTest extends TestCase {
    public void test_select() throws Exception {
        String sql =
                "   SELECT COUNT(*) FROM close_plan WHERE 1=1          AND close_type = ?             AND target_type = ?             AND target_id = ?         AND(    mi_name=?   )               AND end_time >= ?         ";
        SQLSelectParser parser = new MySqlSelectParser(sql);
        SQLSelect select = parser.select();

        StringBuilder out = new StringBuilder();
        MySql2OracleOutputVisitor visitor = new MySql2OracleOutputVisitor(out);

        select.accept(visitor);

        System.out.println(out);
    }

}
