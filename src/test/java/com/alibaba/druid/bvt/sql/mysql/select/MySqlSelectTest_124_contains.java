package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_124_contains extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select shid from alitrip_htl_realtime.hotel_real_time_inventory where rate_sale_status = 1 and rate_tags contains ('520') " +
                "group by shid,start_time_daily,end_time_daily";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT shid\n" +
                "FROM alitrip_htl_realtime.hotel_real_time_inventory\n" +
                "WHERE rate_sale_status = 1\n" +
                "\tAND rate_tags CONTAINS ('520')\n" +
                "GROUP BY shid, start_time_daily, end_time_daily", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "select shid from alitrip_htl_realtime.hotel_real_time_inventory where rate_sale_status = 1 and contains (rate_tags, '520') " +
                "group by shid,start_time_daily,end_time_daily";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT shid\n" +
                "FROM alitrip_htl_realtime.hotel_real_time_inventory\n" +
                "WHERE rate_sale_status = 1\n" +
                "\tAND CONTAINS (rate_tags, '520')\n" +
                "GROUP BY shid, start_time_daily, end_time_daily", stmt.toString());
    }
}