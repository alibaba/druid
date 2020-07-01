package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_18 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;

        String sql = "insert into `t_n_0021` ( " +
                "`f0`, `f1`, `f2`, `f3`, `f4`" +
                ", `f5`, `f6`, `f7`, `f8`, `f9`" +
                ", `f10`, `f11`, `f12`, `f13`, `f14`" +
                ", `f15`) " +
                "values ( NOW(), NOW(), 123, 'abc', 'abd'" +
                ", 'tair:ldbcount:808', 0.0, 2.0, 0, 251, 0, '172.29.60.62', 2, 1483686655818, 12, 0);";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        assertEquals("INSERT INTO t_n (`f0`, `f1`, `f2`, `f3`, `f4`\n" +
                "\t, `f5`, `f6`, `f7`, `f8`, `f9`\n" +
                "\t, `f10`, `f11`, `f12`, `f13`, `f14`\n" +
                "\t, `f15`)\n" +
                "VALUES (NOW(), NOW(), ?, ?, ?\n" +
                "\t\t, ?, ?, ?, ?, ?\n" +
                "\t\t, ?, ?, ?, ?, ?\n" +
                "\t\t, ?);", psql);

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
        assertEquals(14, parameters.size());

        StringBuilder buf = new StringBuilder();
        SQLASTOutputVisitor visitor1 = SQLUtils.createOutputVisitor(buf, dbType);
        visitor1.setParameters(visitor.getParameters());
        stmt.accept(visitor1);

        assertEquals("INSERT INTO `t_n_0021` (`f0`, `f1`, `f2`, `f3`, `f4`\n" +
                "\t, `f5`, `f6`, `f7`, `f8`, `f9`\n" +
                "\t, `f10`, `f11`, `f12`, `f13`, `f14`\n" +
                "\t, `f15`)\n" +
                "VALUES (NOW(), NOW(), 123, 'abc', 'abd'\n" +
                "\t, 'tair:ldbcount:808', 0.0, 2.0, 0, 251\n" +
                "\t, 0, '172.29.60.62', 2, 1483686655818, 12\n" +
                "\t, 0);", buf.toString());
    }
}
