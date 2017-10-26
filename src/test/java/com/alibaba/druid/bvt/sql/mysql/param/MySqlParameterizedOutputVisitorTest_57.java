package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
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
public class MySqlParameterizedOutputVisitorTest_57 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "select `ktv_resource`.`VERSION` from `ktv_resource_0118` `ktv_resource` " +
                "where ((`ktv_resource`.`BUYER_ID` = 736485494) " +
                "   AND (`ktv_resource`.`STATUS` = 1) " +
                "   AND (`ktv_resource`.`START_TIME` <= '2017-10-24 00:27:21.839') " +
                "   AND (`ktv_resource`.`END_TIME` >= '2017-10-24 00:27:21.839') " +
                "   AND (`ktv_resource`.`seller_id` = 2933220011) " +
                "   AND (`ktv_resource`.`AVAILABLE_COUNT` IS NULL " +
                "       OR (`ktv_resource`.`AVAILABLE_COUNT` > 0) " +
                "       OR (`ktv_resource`.`AVAILABLE_COUNT` = -1))" +
                ") limit 0,20";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, SQLParserFeature.EnableSQLBinaryOpExprGroup);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement stmt = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        List<Object> parameters = new ArrayList<Object>();
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        visitor.setParameters(parameters);
        /*visitor.setPrettyFormat(false);*/
        stmt.accept(visitor);
       /* JSONArray array = new JSONArray();
        for(String table : visitor.getTables()){
            array.add(table.replaceAll("`",""));
        }*/

        String psql = out.toString();

        System.out.println(psql);




        assertEquals("SELECT `ktv_resource`.`VERSION`\n" +
                "FROM ktv_resource `ktv_resource`\n" +
                "WHERE `ktv_resource`.`BUYER_ID` = ?\n" +
                "\tAND `ktv_resource`.`STATUS` = ?\n" +
                "\tAND `ktv_resource`.`START_TIME` <= ?\n" +
                "\tAND `ktv_resource`.`END_TIME` >= ?\n" +
                "\tAND `ktv_resource`.`seller_id` = ?\n" +
                "\tAND (`ktv_resource`.`AVAILABLE_COUNT` IS NULL\n" +
                "\t\tOR `ktv_resource`.`AVAILABLE_COUNT` > ?\n" +
                "\t\tOR `ktv_resource`.`AVAILABLE_COUNT` = ?)\n" +
                "LIMIT ?, ?", psql);

        String rsql = SQLUtils.format(psql, JdbcConstants.MYSQL, parameters);
        System.out.println(rsql);
    }
}
