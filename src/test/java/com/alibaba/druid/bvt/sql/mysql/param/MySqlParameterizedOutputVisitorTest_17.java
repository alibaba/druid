package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_17 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;

        String sql = "replace into `mytable_0228` " +
                "( `user_id`, `c_level`, `l_level`, `t_level`, `v_level`, `tag`) " +
                "values ( 2272895716, 'C1', null, 'T1', 'V0', '0') ";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        assertEquals("REPLACE INTO mytable (`user_id`, `c_level`, `l_level`, `t_level`, `v_level`, `tag`)\n" +
                "VALUES (?, ?, ?, ?, ?\n" +
                "\t\t, ?)", psql);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();

        StringBuilder out = new StringBuilder();
        ExportParameterVisitor visitor = new MySqlExportParameterVisitor(out);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        assertEquals(6, visitor.getParameters().size());
    }
}
