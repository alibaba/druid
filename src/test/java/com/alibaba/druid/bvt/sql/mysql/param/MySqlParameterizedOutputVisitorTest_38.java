package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_38 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "SELECT lower(hex(file_md5)) as file_md5,\n" +
                "        lower(hex(thumb)) as thumb,st\n" +
                "        FROM t_f_p_thumb\n" +
                "        WHERE file_md5 = x'84C1F969587F5FD1942148EE9D36A0FB'";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement statement = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        //  List<Object> parameters = new ArrayList<Object>();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        //   visitor.setParameters(parameters);
        visitor.setExportTables(true);
        visitor.setPrettyFormat(true);
        statement.accept(visitor);
        assertEquals("SELECT lower(hex(file_md5)) AS file_md5\n" +
                "\t, lower(hex(thumb)) AS thumb, st\n" +
                "FROM t_f_p_thumb\n" +
                "WHERE file_md5 = ?", out.toString());
    }
}
