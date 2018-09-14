package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_56 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "/* 0b802ab415058233338893940e1882/0.1.2.23//abd5b625/ */select `ktv_resource`.`RESOURCE_ID`,`ktv_resource`.`RESOURCE_PROVIDER`,`ktv_resource`.`KTV_ID`,`ktv_resource`.`RESOURCE_TYPE`,`ktv_resource`.`SUB_RESOURCE_TYPE`,`ktv_resource`.`STATUS`,`ktv_resource`.`START_TIME`,`ktv_resource`.`END_TIME`,`ktv_resource`.`FEATURE`,`ktv_resource`.`GMT_CREATED`,`ktv_resource`.`GMT_MODIFIED`,`ktv_resource`.`source`,`ktv_resource`.`seller_id`,`ktv_resource`.`original_Resource_Id`,`ktv_resource`.`business_unit`,`ktv_resource`.`resource_code`,`ktv_resource`.`OPTIONS`,`ktv_resource`.`AVAILABLE_COUNT`,`ktv_resource`.`TOTAL_COUNT`,`ktv_resource`.`OUT_INSTANCE_ID`,`ktv_resource`.`CONSUME_ID`,`ktv_resource`.`GROUP_ID`,`ktv_resource`.`BUSINESS_ID`,`ktv_resource`.`rule`,`ktv_resource`.`market_place`,`ktv_resource`.`VERSION` from `ktv_resource_0062` `ktv_resource` where ((`ktv_resource`.`KTV_ID` = 880693310) AND (`ktv_resource`.`STATUS` = 1) AND (`ktv_resource`.`START_TIME` <= '2017-09-19 20:15:34.199') AND (`ktv_resource`.`END_TIME` >= '2017-09-19 20:15:34.199') AND (`ktv_resource`.`seller_id` IN (2680068332)) AND (`ktv_resource`.`AVAILABLE_COUNT` IS NULL OR (`ktv_resource`.`AVAILABLE_COUNT` > 0) OR (`ktv_resource`.`AVAILABLE_COUNT` = -1))) limit 0,30";

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


        assertEquals("SELECT `ktv_resource`.`RESOURCE_ID`, `ktv_resource`.`RESOURCE_PROVIDER`, `ktv_resource`.`KTV_ID`, `ktv_resource`.`RESOURCE_TYPE`, `ktv_resource`.`SUB_RESOURCE_TYPE`\n" +
                "\t, `ktv_resource`.`STATUS`, `ktv_resource`.`START_TIME`, `ktv_resource`.`END_TIME`, `ktv_resource`.`FEATURE`, `ktv_resource`.`GMT_CREATED`\n" +
                "\t, `ktv_resource`.`GMT_MODIFIED`, `ktv_resource`.`source`, `ktv_resource`.`seller_id`, `ktv_resource`.`original_Resource_Id`, `ktv_resource`.`business_unit`\n" +
                "\t, `ktv_resource`.`resource_code`, `ktv_resource`.`OPTIONS`, `ktv_resource`.`AVAILABLE_COUNT`, `ktv_resource`.`TOTAL_COUNT`, `ktv_resource`.`OUT_INSTANCE_ID`\n" +
                "\t, `ktv_resource`.`CONSUME_ID`, `ktv_resource`.`GROUP_ID`, `ktv_resource`.`BUSINESS_ID`, `ktv_resource`.`rule`, `ktv_resource`.`market_place`\n" +
                "\t, `ktv_resource`.`VERSION`\n" +
                "FROM ktv_resource `ktv_resource`\n" +
                "WHERE `ktv_resource`.`KTV_ID` = ?\n" +
                "\tAND `ktv_resource`.`STATUS` = ?\n" +
                "\tAND `ktv_resource`.`START_TIME` <= ?\n" +
                "\tAND `ktv_resource`.`END_TIME` >= ?\n" +
                "\tAND `ktv_resource`.`seller_id` IN (?)\n" +
                "\tAND (`ktv_resource`.`AVAILABLE_COUNT` IS NULL\n" +
                "\t\tOR `ktv_resource`.`AVAILABLE_COUNT` > ?\n" +
                "\t\tOR `ktv_resource`.`AVAILABLE_COUNT` = ?)\n" +
                "LIMIT ?, ?", psql);

        String rsql = SQLUtils.format(psql, JdbcConstants.MYSQL, parameters);
        System.out.println(rsql);
    }

    public void test_for_parameter_char() throws Exception {

        String sql = "insert into t values(?,?,?)";

        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);

        Assert.assertTrue(sqlStatements.size() == 1);

        StringBuilder out = new StringBuilder(sql.length());
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);
        List<Object> parameters = new ArrayList<Object>(Arrays.asList('a', 'b', 'c'));
        visitor.setInputParameters(parameters);
        SQLStatement sqlStatement = sqlStatements.get(0);
        sqlStatement.accept(visitor);
        Assert.assertEquals("INSERT INTO t\n" +
                "VALUES ('a', 'b', 'c')", out.toString());
    }

}
