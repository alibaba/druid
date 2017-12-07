package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
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
public class MySqlParameterizedOutputVisitorTest_58 extends TestCase {
    final String dbType = JdbcConstants.MYSQL;
    public void test_for_parameterize() throws Exception {

        String sql = "select * from t where id = 101";

        List<Object> params = new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType, params);
        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "WHERE id = ?", psql);
        assertEquals(1, params.size());
        assertEquals(101, params.get(0));
    }

    public void test_for_parameterize_insert() throws Exception {
        String sql = "insert into mytab(fid,fname)values(1001,'wenshao');";

        List<Object> params = new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType, params);
        assertEquals("INSERT INTO mytab(fid, fname)\n" +
                "VALUES (?, ?);", psql);
        assertEquals(2, params.size());
        assertEquals(1001, params.get(0));
        assertEquals("wenshao", params.get(1));
    }
}
