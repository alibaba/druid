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
public class MySqlParameterizedOutputVisitorTest_22 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;

        String sql = "/* 0bba613214845441110397435e/0.4.6.25// */select `f`.`id`,`f`.`biz_id`,`f`.`user_id`,`f`.`file_name`,`f`.`parent_id`,`f`.`length`,`f`.`type`,`f`.`stream_key`,`f`.`biz_status`,`f`.`mark`,`f`.`content_modified`,`f`.`status`,`f`.`gmt_create`,`f`.`gmt_modified`,`f`.`md5`,`f`.`extra_str1`,`f`.`extra_str2`,`f`.`extra_str3`,`f`.`extra_num1`,`f`.`extra_num2`,`f`.`extra_num3`,`f`.`safe`,`f`.`open_status`,`f`.`inner_mark`,`f`.`sys_extra`,`f`.`feature`,`f`.`domain_option`,`f`.`version`,`f`.`reference_type`,`f`.`dentry_type`,`f`.`space_id`,`f`.`extension`,`f`.`creator_id`,`f`.`modifier_id`,`f`.`store_type`,`f`.`link_mark`,`f`.`content_type` from  ( select `vfs_dentry_2664`.`id` from `vfs_dentry_2664` FORCE INDEX (idx_gmt) where ((`vfs_dentry_2664`.`extra_str1` = '97d45a25df387b4460e5b4151daeb452') AND (`vfs_dentry_2664`.`biz_id` = 62) AND (`vfs_dentry_2664`.`status` = 0) AND (`vfs_dentry_2664`.`user_id` = '11168360') AND (`vfs_dentry_2664`.`dentry_type` = 1)) limit 0,50 )  `t`  join `vfs_dentry_2664` `f` on `t`.`id` = `f`.`id` where ((`t`.`id` = `f`.`id`) AND (`f`.`user_id` = 11168360))";

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        assertEquals("SELECT `f`.`id`, `f`.`biz_id`, `f`.`user_id`, `f`.`file_name`, `f`.`parent_id`\n" +
                "\t, `f`.`length`, `f`.`type`, `f`.`stream_key`, `f`.`biz_status`, `f`.`mark`\n" +
                "\t, `f`.`content_modified`, `f`.`status`, `f`.`gmt_create`, `f`.`gmt_modified`, `f`.`md5`\n" +
                "\t, `f`.`extra_str1`, `f`.`extra_str2`, `f`.`extra_str3`, `f`.`extra_num1`, `f`.`extra_num2`\n" +
                "\t, `f`.`extra_num3`, `f`.`safe`, `f`.`open_status`, `f`.`inner_mark`, `f`.`sys_extra`\n" +
                "\t, `f`.`feature`, `f`.`domain_option`, `f`.`version`, `f`.`reference_type`, `f`.`dentry_type`\n" +
                "\t, `f`.`space_id`, `f`.`extension`, `f`.`creator_id`, `f`.`modifier_id`, `f`.`store_type`\n" +
                "\t, `f`.`link_mark`, `f`.`content_type`\n" +
                "FROM (\n" +
                "\tSELECT vfs_dentry.`id`\n" +
                "\tFROM vfs_dentry FORCE INDEX (idx_gmt)\n" +
                "\tWHERE vfs_dentry.`extra_str1` = ?\n" +
                "\t\tAND vfs_dentry.`biz_id` = ?\n" +
                "\t\tAND vfs_dentry.`status` = ?\n" +
                "\t\tAND vfs_dentry.`user_id` = ?\n" +
                "\t\tAND vfs_dentry.`dentry_type` = ?\n" +
                "\tLIMIT ?, ?\n" +
                ") `t`\n" +
                "\tJOIN vfs_dentry `f` ON `t`.`id` = `f`.`id`\n" +
                "WHERE `t`.`id` = `f`.`id`\n" +
                "\tAND `f`.`user_id` = ?", psql);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(psql, dbType);
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
        assertEquals(0, parameters.size());

        StringBuilder buf = new StringBuilder();
        SQLASTOutputVisitor visitor1 = SQLUtils.createOutputVisitor(buf, dbType);
        visitor1.addTableMapping("vfs_dentry", "vfs_dentry_001");
        visitor1.setParameters(visitor.getParameters());
        stmt.accept(visitor1);

        assertEquals("SELECT `f`.`id`, `f`.`biz_id`, `f`.`user_id`, `f`.`file_name`, `f`.`parent_id`\n" +
                "\t, `f`.`length`, `f`.`type`, `f`.`stream_key`, `f`.`biz_status`, `f`.`mark`\n" +
                "\t, `f`.`content_modified`, `f`.`status`, `f`.`gmt_create`, `f`.`gmt_modified`, `f`.`md5`\n" +
                "\t, `f`.`extra_str1`, `f`.`extra_str2`, `f`.`extra_str3`, `f`.`extra_num1`, `f`.`extra_num2`\n" +
                "\t, `f`.`extra_num3`, `f`.`safe`, `f`.`open_status`, `f`.`inner_mark`, `f`.`sys_extra`\n" +
                "\t, `f`.`feature`, `f`.`domain_option`, `f`.`version`, `f`.`reference_type`, `f`.`dentry_type`\n" +
                "\t, `f`.`space_id`, `f`.`extension`, `f`.`creator_id`, `f`.`modifier_id`, `f`.`store_type`\n" +
                "\t, `f`.`link_mark`, `f`.`content_type`\n" +
                "FROM (\n" +
                "\tSELECT vfs_dentry_001.`id`\n" +
                "\tFROM vfs_dentry_001 FORCE INDEX (idx_gmt)\n" +
                "\tWHERE vfs_dentry_001.`extra_str1` = ?\n" +
                "\t\tAND vfs_dentry_001.`biz_id` = ?\n" +
                "\t\tAND vfs_dentry_001.`status` = ?\n" +
                "\t\tAND vfs_dentry_001.`user_id` = ?\n" +
                "\t\tAND vfs_dentry_001.`dentry_type` = ?\n" +
                "\tLIMIT ?, ?\n" +
                ") `t`\n" +
                "\tJOIN vfs_dentry_001 `f` ON `t`.`id` = `f`.`id`\n" +
                "WHERE `t`.`id` = `f`.`id`\n" +
                "\tAND `f`.`user_id` = ?", buf.toString());
    }
}
