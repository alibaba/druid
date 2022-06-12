package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest86
        extends TestCase {
    public void test_0() throws Exception {
        String sql = "select * from xxx_point point inner join xxx_cc cc on point.id = cc.point_id";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        assertEquals("SELECT *\n" +
                        "FROM xxx_point point\n" +
                        "\tINNER JOIN xxx_cc cc ON point.id = cc.point_id"
                , stmt.toString());

        assertEquals("select *\n" +
                "from xxx_point point\n" +
                "\tinner join xxx_cc cc on point.id = cc.point_id", stmt.toLowerCaseString());
    }

    public void test_1() throws Exception {
        String sql = "select\n" +
                "        COUNT(1) res\n" +
                "        from TB_EXPERIENCE exp\n" +
                "        where exp.creatdate>DATE_SUB(CURDATE(), interval 1 MONTH)\n" +
                "        and exp.cstatus = 1";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        assertEquals("SELECT COUNT(1) AS res\n" +
                        "FROM TB_EXPERIENCE exp\n" +
                        "WHERE exp.creatdate > DATE_SUB(CURDATE(), INTERVAL '1' MONTH)\n" +
                        "\tAND exp.cstatus = 1"
                , stmt.toString());

        assertEquals("select count(1) as res\n" +
                "from TB_EXPERIENCE exp\n" +
                "where exp.creatdate > DATE_SUB(CURDATE(), interval '1' month)\n" +
                "\tand exp.cstatus = 1", stmt.toLowerCaseString());
    }
}
