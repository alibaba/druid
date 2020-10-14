package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class MySqlParameterizedOutputVisitorTest_68 extends TestCase {
    public void test_for_parameterize() throws Exception {

        String sql = "select id , name from xxx group by 1,2 order by 1;";

        List<Object> params =  new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql);
        assertEquals("SELECT id, name\n" +
                "FROM xxx\n" +
                "GROUP BY 1, 2\n" +
                "ORDER BY 1;", psql);
        assertEquals(0, params.size());
    }

}
