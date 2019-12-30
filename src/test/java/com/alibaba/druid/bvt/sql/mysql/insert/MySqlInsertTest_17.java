package com.alibaba.druid.bvt.sql.mysql.insert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.Assert;

import java.util.List;

public class MySqlInsertTest_17 extends MysqlTest {

    public void test_insert_rollback_on_fail() throws Exception {
        String sql = "/*+engine=MPP, mppNativeInsertFromSelect=true*/\n"
                     + "INSERT INTO dashboard_crowd_analysis(cname,cvalue,orders,users,dt,tagid)\n"
                     + "SELECT x.cname as cname,\n" + "       x.cvalue as cvalue,\n" + "       x.orders as orders,\n"
                     + "       x.users as users,\n" + "       20171211 as dt,\n" + "       91 as tagid\n"
                     + "FROM (WITH h AS\n" + "        (SELECT a.userid,\n" + "                a.col_1,\n"
                     + "                a.col_2,\n" + "                a.col_3,\n" + "                a.col_4,\n"
                     + "                a.col_5,\n" + "                a.col_6,\n" + "                c.orders_week\n"
                     + "         FROM\n" + "           (SELECT userid,\n" + "                   col_1,\n"
                     + "                   col_2,\n" + "                   col_3,\n" + "                   col_4,\n"
                     + "                   col_5,\n" + "                   col_6\n"
                     + "            FROM ofo_personas_dimensions\n" + "            WHERE dt=20171211)a\n"
                     + "         JOIN\n" + "           (SELECT userid\n" + "            FROM personas_user_tag\n"
                     + "            WHERE tagid=91)b ON a.userid=b.userid\n" + "         JOIN\n"
                     + "           (SELECT userid,\n" + "                   orders_week\n"
                     + "            FROM ofo_personas_metrics\n"
                     + "            WHERE dt=20171211)c ON a.userid=c.userid)\n" + "      SELECT 'col_1' as cname,\n"
                     + "             col_1 as cvalue,\n" + "             sum(orders_week) AS orders,\n"
                     + "             count(userid) AS users\n" + "      FROM h\n" + "      GROUP BY col_1\n"
                     + "      UNION ALL\n" + "      SELECT 'col_2' as cname,\n" + "             col_2 as cvalue,\n"
                     + "             sum(orders_week) AS orders,\n" + "             count(userid) AS users\n"
                     + "      FROM h\n" + "      GROUP BY col_2)x";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        MySqlInsertStatement insertStmt = (MySqlInsertStatement) stmt;
        //        System.out.println(SQLUtils.toMySqlString(insertStmt));

        String formatSql = "/*+engine=MPP, mppNativeInsertFromSelect=true*/\n" +
                "INSERT INTO dashboard_crowd_analysis (cname, cvalue, orders, users, dt\n" +
                "\t, tagid)\n" +
                "SELECT x.cname AS cname, x.cvalue AS cvalue, x.orders AS orders, x.users AS users, 20171211 AS dt\n" +
                "\t, 91 AS tagid\n" +
                "FROM (\n" +
                "\tWITH h AS (\n" +
                "\t\t\tSELECT a.userid, a.col_1, a.col_2, a.col_3, a.col_4\n" +
                "\t\t\t\t, a.col_5, a.col_6, c.orders_week\n" +
                "\t\t\tFROM (\n" +
                "\t\t\t\tSELECT userid, col_1, col_2, col_3, col_4\n" +
                "\t\t\t\t\t, col_5, col_6\n" +
                "\t\t\t\tFROM ofo_personas_dimensions\n" +
                "\t\t\t\tWHERE dt = 20171211\n" +
                "\t\t\t) a\n" +
                "\t\t\t\tJOIN (\n" +
                "\t\t\t\t\tSELECT userid\n" +
                "\t\t\t\t\tFROM personas_user_tag\n" +
                "\t\t\t\t\tWHERE tagid = 91\n" +
                "\t\t\t\t) b\n" +
                "\t\t\t\tON a.userid = b.userid\n" +
                "\t\t\t\tJOIN (\n" +
                "\t\t\t\t\tSELECT userid, orders_week\n" +
                "\t\t\t\t\tFROM ofo_personas_metrics\n" +
                "\t\t\t\t\tWHERE dt = 20171211\n" +
                "\t\t\t\t) c\n" +
                "\t\t\t\tON a.userid = c.userid\n" +
                "\t\t)\n" +
                "\tSELECT 'col_1' AS cname, col_1 AS cvalue, sum(orders_week) AS orders\n" +
                "\t\t, count(userid) AS users\n" +
                "\tFROM h\n" +
                "\tGROUP BY col_1\n" +
                "\tUNION ALL\n" +
                "\tSELECT 'col_2' AS cname, col_2 AS cvalue, sum(orders_week) AS orders\n" +
                "\t\t, count(userid) AS users\n" +
                "\tFROM h\n" +
                "\tGROUP BY col_2\n" +
                ") x";
        Assert.assertEquals(formatSql, SQLUtils.toMySqlString(insertStmt));
    }

}
