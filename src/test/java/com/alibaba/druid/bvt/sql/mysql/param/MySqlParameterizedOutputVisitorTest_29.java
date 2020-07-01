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
public class MySqlParameterizedOutputVisitorTest_29 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;

        String sql = "select `a1`.`id`,`a1`.`gmt_create`,`a1`.`gmt_modified`,`a1`.`push_date`,`a1`.`parent_task_id`" +
                "   ,`a1`.`parent_task_type`,`a1`.`action_type`,`a1`.`schedule_no`,`a1`.`type`,`a1`.`md5`" +
                "   ,`a1`.`message_content`,`a1`.`retry_count`,`a1`.`level`,`a1`.`extra`,`a1`.`status`" +
                "   ,`a1`.`is_exist_relation`,`a1`.`begin_time`,`a1`.`end_time`,`a1`.`orig`,`a1`.`dest`" +
                "   ,`a1`.`airline`,`a1`.`params_stat_id`,`a1`.`total_num`,`a1`.`finish_num`,`a1`.`type_idx_key`" +
                "   ,`a1`.`seqno`,`a1`.`task_flag`,`a1`.`tariff` " +
                "from `xx_abcde_ta_0018` `a1` " +
                "where ((`a1`.`push_date` = '2017-01-19 00:00:00') AND (`a1`.`schedule_no` <= '201701181201') AND (`a1`.`type` IN (1,4,2,3,7,8,11,12,13,14,15,16)) AND (`a1`.`retry_count` < 3) AND (`a1`.`status` IN (3,6)) AND (`a1`.`gmt_modified` <= DATE_ADD(NOW(),INTERVAL -(5) MINUTE))) limit 0,2000";

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        assertEquals("SELECT `a1`.`id`, `a1`.`gmt_create`, `a1`.`gmt_modified`, `a1`.`push_date`, `a1`.`parent_task_id`\n" +
                "\t, `a1`.`parent_task_type`, `a1`.`action_type`, `a1`.`schedule_no`, `a1`.`type`, `a1`.`md5`\n" +
                "\t, `a1`.`message_content`, `a1`.`retry_count`, `a1`.`level`, `a1`.`extra`, `a1`.`status`\n" +
                "\t, `a1`.`is_exist_relation`, `a1`.`begin_time`, `a1`.`end_time`, `a1`.`orig`, `a1`.`dest`\n" +
                "\t, `a1`.`airline`, `a1`.`params_stat_id`, `a1`.`total_num`, `a1`.`finish_num`, `a1`.`type_idx_key`\n" +
                "\t, `a1`.`seqno`, `a1`.`task_flag`, `a1`.`tariff`\n" +
                "FROM xx_abcde_ta `a1`\n" +
                "WHERE `a1`.`push_date` = ?\n" +
                "\tAND `a1`.`schedule_no` <= ?\n" +
                "\tAND `a1`.`type` IN (?)\n" +
                "\tAND `a1`.`retry_count` < ?\n" +
                "\tAND `a1`.`status` IN (?)\n" +
                "\tAND `a1`.`gmt_modified` <= DATE_ADD(NOW(), INTERVAL -? MINUTE)\n" +
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
        assertEquals(8, parameters.size());

        //SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(psql, dbType);
        // List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement pstmt = SQLUtils.parseStatements(psql, dbType).get(0);

        StringBuilder buf = new StringBuilder();
        SQLASTOutputVisitor visitor1 = SQLUtils.createOutputVisitor(buf, dbType);
        visitor1.addTableMapping("xx_abcde_ta", "xx_abcde_ta_0018");
        visitor1.setInputParameters(visitor.getParameters());
        pstmt.accept(visitor1);

        assertEquals("SELECT `a1`.`id`, `a1`.`gmt_create`, `a1`.`gmt_modified`, `a1`.`push_date`, `a1`.`parent_task_id`\n" +
                "\t, `a1`.`parent_task_type`, `a1`.`action_type`, `a1`.`schedule_no`, `a1`.`type`, `a1`.`md5`\n" +
                "\t, `a1`.`message_content`, `a1`.`retry_count`, `a1`.`level`, `a1`.`extra`, `a1`.`status`\n" +
                "\t, `a1`.`is_exist_relation`, `a1`.`begin_time`, `a1`.`end_time`, `a1`.`orig`, `a1`.`dest`\n" +
                "\t, `a1`.`airline`, `a1`.`params_stat_id`, `a1`.`total_num`, `a1`.`finish_num`, `a1`.`type_idx_key`\n" +
                "\t, `a1`.`seqno`, `a1`.`task_flag`, `a1`.`tariff`\n" +
                "FROM xx_abcde_ta_0018 `a1`\n" +
                "WHERE `a1`.`push_date` = '2017-01-19 00:00:00'\n" +
                "\tAND `a1`.`schedule_no` <= '201701181201'\n" +
                "\tAND `a1`.`type` IN (1, 4, 2, 3, 7, 8, 11, 12, 13, 14, 15, 16)\n" +
                "\tAND `a1`.`retry_count` < 3\n" +
                "\tAND `a1`.`status` IN (3, 6)\n" +
                "\tAND `a1`.`gmt_modified` <= DATE_ADD(NOW(), INTERVAL -5 MINUTE)\n" +
                "LIMIT 0, 2000", buf.toString());
    }
}
