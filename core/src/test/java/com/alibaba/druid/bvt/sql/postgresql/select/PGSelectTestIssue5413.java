package com.alibaba.druid.bvt.sql.postgresql.select;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;

import junit.framework.TestCase;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5413">修复bug，加上GREENPLUM的支持</a>
 */
public class PGSelectTestIssue5413 extends TestCase {
    public void test_0() throws Exception {
        String sql = "update  WORK.TABLE1 t\n" + "set open_date = cast(to_char(a.d_opentimestamp, 'yyyMMdd') as int4)\n"
            + "from WORK.TABLE2 a\n" + "where t.acco=a.acco\n" + "\tand t.sys='KEY';";

        List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        statements = SQLUtils.parseStatements(sql, JdbcConstants.GREENPLUM);
        assertEquals(1, statements.size());
    }

}
