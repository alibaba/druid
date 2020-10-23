package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_129_ads extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "/*+ engine= mpp*/     \n" +
                "with  \n" +
                "history_table as (         \n" +
                "\tselect hour_id,minute_id, sum(imp)/1000.0 as history_imp,sum(revenue)/100000.0 as history_revenue         \n" +
                "\tfrom ads_add_rtb_monitor_minute             \n" +
                "\twhere thedate BETWEEN  cast(20180109 as bigint)  and cast(20180109 as bigint)             \n" +
                "\tgroup by hour_id,minute_id     ),     \n" +
                "avg_table as (      \n" +
                "\t\twith sum_table as (             \n" +
                "\t\t\tselect thedate,minute_id, sum(COALESCE(imp,0))/1000.0 as sum_imp,sum(COALESCE(revenue,0))/100000.0 as sum_revenue             \n" +
                "\t\t\tfrom ads_add_rtb_monitor_minute             \n" +
                "\t\t\twhere thedate BETWEEN  cast(20171231 as bigint)  AND cast(20180109 as bigint)             \n" +
                "\t\t\tgroup by thedate,minute_id         \n" +
                "\t\t\t)\n" +
                "\t\t\tselect minute_id, avg(sum_imp\n" +
                "\t\t\t) as avg_imp, avg(sum_revenue) as avg_revenue,stddev(sum_imp\n" +
                "\t\t\t) as stddev_imp,stddev(sum_revenue) as stddev_revenue  from sum_table group by minute_id      )     \n" +
                "select history_table.hour_id,history_table.minute_id , history_imp,history_revenue,avg_imp,avg_revenue,stddev_imp,stddev_revenue,\n" +
                "     avg_imp+stddev_imp as add_stddev_imp,avg_imp-stddev_imp as sub_stddev_imp,avg_revenue+stddev_revenue as add_stddev_rev,avg_revenue-stddev_revenue as sub_stddev_rev     \n" +
                "from history_table     left join avg_table using (minute_id) order by history_table.hour_id\n";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+ engine= mpp*/\n" +
                "WITH history_table AS (\n" +
                "\t\tSELECT hour_id, minute_id, sum(imp) / 1000.0 AS history_imp\n" +
                "\t\t\t, sum(revenue) / 100000.0 AS history_revenue\n" +
                "\t\tFROM ads_add_rtb_monitor_minute\n" +
                "\t\tWHERE thedate BETWEEN CAST(20180109 AS bigint) AND CAST(20180109 AS bigint)\n" +
                "\t\tGROUP BY hour_id, minute_id\n" +
                "\t), \n" +
                "\tavg_table AS (\n" +
                "\t\tWITH sum_table AS (\n" +
                "\t\t\t\tSELECT thedate, minute_id\n" +
                "\t\t\t\t\t, sum(COALESCE(imp, 0)) / 1000.0 AS sum_imp\n" +
                "\t\t\t\t\t, sum(COALESCE(revenue, 0)) / 100000.0 AS sum_revenue\n" +
                "\t\t\t\tFROM ads_add_rtb_monitor_minute\n" +
                "\t\t\t\tWHERE thedate BETWEEN CAST(20171231 AS bigint) AND CAST(20180109 AS bigint)\n" +
                "\t\t\t\tGROUP BY thedate, minute_id\n" +
                "\t\t\t)\n" +
                "\t\tSELECT minute_id, avg(sum_imp) AS avg_imp, avg(sum_revenue) AS avg_revenue\n" +
                "\t\t\t, stddev(sum_imp) AS stddev_imp, stddev(sum_revenue) AS stddev_revenue\n" +
                "\t\tFROM sum_table\n" +
                "\t\tGROUP BY minute_id\n" +
                "\t)\n" +
                "SELECT history_table.hour_id, history_table.minute_id, history_imp, history_revenue, avg_imp\n" +
                "\t, avg_revenue, stddev_imp, stddev_revenue, avg_imp + stddev_imp AS add_stddev_imp\n" +
                "\t, avg_imp - stddev_imp AS sub_stddev_imp, avg_revenue + stddev_revenue AS add_stddev_rev\n" +
                "\t, avg_revenue - stddev_revenue AS sub_stddev_rev\n" +
                "FROM history_table\n" +
                "\tLEFT JOIN avg_table USING (minute_id)\n" +
                "ORDER BY history_table.hour_id", stmt.toString());
    }


}