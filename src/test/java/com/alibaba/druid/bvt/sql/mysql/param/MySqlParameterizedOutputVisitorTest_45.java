package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_45 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;
        String sql = "(SELECT `t_like_count`.`id`, `t_like_count`.`target_id`, `t_like_count`.`target_type`, `t_like_count`.`like_type`, `t_like_count`.`like_count` , `t_like_count`.`like_optimalize_count`, `t_like_count`.`create_time`, `t_like_count`.`update_time` FROM `t_like_count0062` `t_like_count` WHERE `t_like_count`.`target_type` = ? AND `t_like_count`.`target_id` IN (?)) UNION ALL (SELECT `t_like_count`.`id`, `t_like_count`.`target_id`, `t_like_count`.`target_type`, `t_like_count`.`like_type`, `t_like_count`.`like_count` , `t_like_count`.`like_optimalize_count`, `t_like_count`.`create_time`, `t_like_count`.`update_time` FROM `t_like_count0057` `t_like_count` WHERE `t_like_count`.`target_type` = ? AND `t_like_count`.`target_id` IN (?)) UNION ALL (SELECT `t_like_count`.`id`, `t_like_count`.`target_id`, `t_like_count`.`target_type`, `t_like_count`.`like_type`, `t_like_count`.`like_count` , `t_like_count`.`like_optimalize_count`, `t_like_count`.`create_time`, `t_like_count`.`update_time` FROM `t_like_count0050` `t_like_count` WHERE `t_like_count`.`target_type` = ? AND `t_like_count`.`target_id` IN (?)) UNION ALL (SELECT `t_like_count`.`id`, `t_like_count`.`target_id`, `t_like_count`.`target_type`, `t_like_count`.`like_type`, `t_like_count`.`like_count` , `t_like_count`.`like_optimalize_count`, `t_like_count`.`create_time`, `t_like_count`.`update_time` FROM `t_like_count0048` `t_like_count` WHERE `t_like_count`.`target_type` = ? AND `t_like_count`.`target_id` IN (?))";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement statement = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        List<Object> parameters = new ArrayList<Object>();
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        visitor.setParameters(parameters);
        /*visitor.setPrettyFormat(false);*/
        statement.accept(visitor);
       /* JSONArray array = new JSONArray();
        for(String table : visitor.getTables()){
            array.add(table.replaceAll("`",""));
        }*/

        String psql = out.toString();

        System.out.println(psql);


        assertEquals("(SELECT `t_like_count`.`id`, `t_like_count`.`target_id`, `t_like_count`.`target_type`, `t_like_count`.`like_type`, `t_like_count`.`like_count`\n" +
                "\t, `t_like_count`.`like_optimalize_count`, `t_like_count`.`create_time`, `t_like_count`.`update_time`\n" +
                "FROM t_like_count `t_like_count`\n" +
                "WHERE `t_like_count`.`target_type` = ?\n" +
                "\tAND `t_like_count`.`target_id` IN (?))\n" +
                "UNION ALL\n" +
                "(SELECT `t_like_count`.`id`, `t_like_count`.`target_id`, `t_like_count`.`target_type`, `t_like_count`.`like_type`, `t_like_count`.`like_count`\n" +
                "\t, `t_like_count`.`like_optimalize_count`, `t_like_count`.`create_time`, `t_like_count`.`update_time`\n" +
                "FROM t_like_count `t_like_count`\n" +
                "WHERE `t_like_count`.`target_type` = ?\n" +
                "\tAND `t_like_count`.`target_id` IN (?))\n" +
                "UNION ALL\n" +
                "(SELECT `t_like_count`.`id`, `t_like_count`.`target_id`, `t_like_count`.`target_type`, `t_like_count`.`like_type`, `t_like_count`.`like_count`\n" +
                "\t, `t_like_count`.`like_optimalize_count`, `t_like_count`.`create_time`, `t_like_count`.`update_time`\n" +
                "FROM t_like_count `t_like_count`\n" +
                "WHERE `t_like_count`.`target_type` = ?\n" +
                "\tAND `t_like_count`.`target_id` IN (?))\n" +
                "UNION ALL\n" +
                "(SELECT `t_like_count`.`id`, `t_like_count`.`target_id`, `t_like_count`.`target_type`, `t_like_count`.`like_type`, `t_like_count`.`like_count`\n" +
                "\t, `t_like_count`.`like_optimalize_count`, `t_like_count`.`create_time`, `t_like_count`.`update_time`\n" +
                "FROM t_like_count `t_like_count`\n" +
                "WHERE `t_like_count`.`target_type` = ?\n" +
                "\tAND `t_like_count`.`target_id` IN (?))", psql);
    }
}
