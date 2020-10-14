package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.DbType;
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
public class MySqlParameterizedOutputVisitorTest_16 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;

        String sql = "/* 0bfacfa414829200086238910e/0.3// */" +
                "insert into `t1` (" +
                " `f0`, `f1`, `f2`, `f3`, `f4`, " +
                "`f5`, `f6`, `f7`, `f8`, `f9`, " +
                "`destination`, `start_standard`, `start_fee`, `add_standard`, `add_fee`, " +
                "`region_fee_standard`, `region_fee_add`, `cell_fee`, `way_day`, `version`)" +
                " values ( 1, 2, 2, 3, 0, -4, 1, null, '2016-12-28 18:13:28.825', '2016-12-28 18:13:28.825', 1, 1, 0, 1, 0, null, null, null, null, 0)\n";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals("INSERT INTO `t1` (`f0`, `f1`, `f2`, `f3`, `f4`\n" +
                "\t, `f5`, `f6`, `f7`, `f8`, `f9`\n" +
                "\t, `destination`, `start_standard`, `start_fee`, `add_standard`, `add_fee`\n" +
                "\t, `region_fee_standard`, `region_fee_add`, `cell_fee`, `way_day`, `version`)\n" +
                "VALUES (?, ?, ?, ?, ?\n" +
                "\t, ?, ?, ?, ?, ?\n" +
                "\t, ?, ?, ?, ?, ?\n" +
                "\t, ?, ?, ?, ?, ?)", psql);



        StringBuilder out = new StringBuilder();
        ExportParameterVisitor visitor = new MySqlExportParameterVisitor(out);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        assertEquals(20, visitor.getParameters().size());
    }
}
