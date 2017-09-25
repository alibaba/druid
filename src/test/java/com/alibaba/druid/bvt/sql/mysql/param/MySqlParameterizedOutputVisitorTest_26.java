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
public class MySqlParameterizedOutputVisitorTest_26 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;

        String sql = "SELECT `xxx_reverse_od`.`id` from xxx_reverse_od_0446 `xxx_reverse_od` where `xxx_reverse_od`.name=1 limit 1;";

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        assertEquals("SELECT `xxx_reverse_od`.`id`\n" +
                "FROM xxx_reverse_od `xxx_reverse_od`\n" +
                "WHERE `xxx_reverse_od`.name = ?\n" +
                "LIMIT ?;", psql);

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
        visitor1.addTableMapping("xxx_reverse_od", "xxx_reverse_od_0446");
        visitor1.setParameters(visitor.getParameters());
        stmt.accept(visitor1);

        assertEquals("SELECT `xxx_reverse_od`.`id`\n" +
                "FROM xxx_reverse_od_0446 `xxx_reverse_od`\n" +
                "WHERE `xxx_reverse_od`.name = ?\n" +
                "LIMIT ?;", buf.toString());
    }
}
