package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_30 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;


        String sql = "/* 0a67bca314863468702364451e/0.3// */select `udata`.`id` as `id`,`udata`.`gmt_create` as `gmtCreate`,`udata`.`gmt_modified` as `gmtModified`,`udata`.`uid` as `userId`,`udata`.`user_nick` as `userNick`,`udata`.`user_type` as `userType`,`udata`.`aps` as `acPeSe`,`udata`.`rn` as `rn`,`udata`.`start_period_time` as `startPeriodTime`,`udata`.`ept` as `adTm`,`udata`.`status` as `status`,`udata`.`charging_period` as `chargingPeriod`,`udata`.`sn` as `sn`,`udata`.`cpd` as `chargingPeriodDesc`,`udata`.`task_total_num` as `taskTotalNum`,`udata`.`tcn` as `taCoNu`,`udata`.`task_type` as `taskType`,`udata`.`ilbu` as `isLaBiUs`" +
                " from `udata_0888` `udata` where ((`udata`.`id` IN (" +
                "   (select MAX(`udata`.`id`) " +
                "   from `udata_0888` `udata` " +
                "   where ((`udata`.`uid` = 1039100792) AND (`udata`.`user_type` = 2) AND (`udata`.`start_period_time` <= '2017-01-01 00:00:00') AND (`udata`.`status` = 10) AND (`udata`.`charging_period` = 1) AND (`udata`.`task_type` = 1) AND (`udata`.`task_total_num` <= `udata`.`tcn`)) group by `udata`.`charging_period`,`udata`.`start_period_time`,`udata`.`ept`))) AND ((`udata`.`uid` = '1039100792') AND (`udata`.`user_type` = 2))) order by `udata`.`start_period_time` desc  limit 0,6";

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        assertEquals("SELECT `udata`.`id` AS `id`, `udata`.`gmt_create` AS `gmtCreate`, `udata`.`gmt_modified` AS `gmtModified`, `udata`.`uid` AS `userId`, `udata`.`user_nick` AS `userNick`\n" +
                "\t, `udata`.`user_type` AS `userType`, `udata`.`aps` AS `acPeSe`, `udata`.`rn` AS `rn`, `udata`.`start_period_time` AS `startPeriodTime`, `udata`.`ept` AS `adTm`\n" +
                "\t, `udata`.`status` AS `status`, `udata`.`charging_period` AS `chargingPeriod`, `udata`.`sn` AS `sn`, `udata`.`cpd` AS `chargingPeriodDesc`, `udata`.`task_total_num` AS `taskTotalNum`\n" +
                "\t, `udata`.`tcn` AS `taCoNu`, `udata`.`task_type` AS `taskType`, `udata`.`ilbu` AS `isLaBiUs`\n" +
                "FROM udata `udata`\n" +
                "WHERE `udata`.`id` IN (\n" +
                "\t\tSELECT MAX(`udata`.`id`)\n" +
                "\t\tFROM udata `udata`\n" +
                "\t\tWHERE `udata`.`uid` = ?\n" +
                "\t\t\tAND `udata`.`user_type` = ?\n" +
                "\t\t\tAND `udata`.`start_period_time` <= ?\n" +
                "\t\t\tAND `udata`.`status` = ?\n" +
                "\t\t\tAND `udata`.`charging_period` = ?\n" +
                "\t\t\tAND `udata`.`task_type` = ?\n" +
                "\t\t\tAND `udata`.`task_total_num` <= `udata`.`tcn`\n" +
                "\t\tGROUP BY `udata`.`charging_period`, `udata`.`start_period_time`, `udata`.`ept`\n" +
                "\t)\n" +
                "\tAND (`udata`.`uid` = ?\n" +
                "\t\tAND `udata`.`user_type` = ?)\n" +
                "ORDER BY `udata`.`start_period_time` DESC\n" +
                "LIMIT ?, ?", psql);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        List<Object> parameters = new ArrayList<Object>();
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        visitor.setParameters(parameters);
        visitor.setExportTables(true);
        /*visitor.setPrettyFormat(false);*/

        SQLStatement stmt = stmtList.get(0);
        stmt.accept(visitor);

        // System.out.println(parameters);
        assertEquals(10, parameters.size());

        //SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(psql, dbType);
        // List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement pstmt = SQLUtils.parseStatements(psql, dbType).get(0);

        StringBuilder buf = new StringBuilder();
        SQLASTOutputVisitor visitor1 = SQLUtils.createOutputVisitor(buf, dbType);
        visitor1.addTableMapping("udata", "udata_0888");
        visitor1.setInputParameters(visitor.getParameters());
        pstmt.accept(visitor1);

        assertEquals("SELECT `udata`.`id` AS `id`, `udata`.`gmt_create` AS `gmtCreate`, `udata`.`gmt_modified` AS `gmtModified`, `udata`.`uid` AS `userId`, `udata`.`user_nick` AS `userNick`\n" +
                "\t, `udata`.`user_type` AS `userType`, `udata`.`aps` AS `acPeSe`, `udata`.`rn` AS `rn`, `udata`.`start_period_time` AS `startPeriodTime`, `udata`.`ept` AS `adTm`\n" +
                "\t, `udata`.`status` AS `status`, `udata`.`charging_period` AS `chargingPeriod`, `udata`.`sn` AS `sn`, `udata`.`cpd` AS `chargingPeriodDesc`, `udata`.`task_total_num` AS `taskTotalNum`\n" +
                "\t, `udata`.`tcn` AS `taCoNu`, `udata`.`task_type` AS `taskType`, `udata`.`ilbu` AS `isLaBiUs`\n" +
                "FROM udata_0888 `udata`\n" +
                "WHERE `udata`.`id` IN (\n" +
                "\t\tSELECT MAX(`udata`.`id`)\n" +
                "\t\tFROM udata_0888 `udata`\n" +
                "\t\tWHERE `udata`.`uid` = 1039100792\n" +
                "\t\t\tAND `udata`.`user_type` = 2\n" +
                "\t\t\tAND `udata`.`start_period_time` <= '2017-01-01 00:00:00'\n" +
                "\t\t\tAND `udata`.`status` = 10\n" +
                "\t\t\tAND `udata`.`charging_period` = 1\n" +
                "\t\t\tAND `udata`.`task_type` = 1\n" +
                "\t\t\tAND `udata`.`task_total_num` <= `udata`.`tcn`\n" +
                "\t\tGROUP BY `udata`.`charging_period`, `udata`.`start_period_time`, `udata`.`ept`\n" +
                "\t)\n" +
                "\tAND (`udata`.`uid` = '1039100792'\n" +
                "\t\tAND `udata`.`user_type` = 2)\n" +
                "ORDER BY `udata`.`start_period_time` DESC\n" +
                "LIMIT 0, 6", buf.toString());
    }
}
