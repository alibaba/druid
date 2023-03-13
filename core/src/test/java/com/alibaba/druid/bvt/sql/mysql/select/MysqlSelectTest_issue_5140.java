package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Test;

public class MysqlSelectTest_issue_5140 {

    @Test
    public void test_error_sql() {
        String sql = "select col1, from table1";

        try {
            SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);
            System.out.println(stmt.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
