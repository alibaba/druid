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
public class MySqlParameterizedOutputVisitorTest_53_or extends TestCase {
    public void test_for_parameterize() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;
        String sql = "SELECT p.id as \"id\", p.rule_id as \"ruleId\", p.name as \"name\", p.param_type as \"type\", p.default_value as \"defaultValue\", p.description as \"description\" FROM rules_parameters p WHERE (( p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=? or p.rule_id=?))";

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


        assertEquals("SELECT p.id AS \"id\", p.rule_id AS \"ruleId\", p.name AS \"name\", p.param_type AS \"type\", p.default_value AS \"defaultValue\"\n" +
                "\t, p.description AS \"description\"\n" +
                "FROM rules_parameters p\n" +
                "WHERE p.rule_id = ?", psql);
    }
}
