package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class MySqlParameterizedOutputVisitorTest_67 extends TestCase {
    public void test_for_parameterize() throws Exception {

        String sql = "select dep_id, dep_name, count(1) from t where dep_tpe = 'aa' group by dep_id having count(1) > 10";

        List<Object> params =  new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql);
        assertEquals("SELECT dep_id, dep_name, count(1)\n" +
                "FROM t\n" +
                "WHERE dep_tpe = ?\n" +
                "GROUP BY dep_id\n" +
                "HAVING count(1) > ?", psql);
        assertEquals(2, params.size());
        assertEquals("\"aa\"", JSON.toJSONString(params.get(0)));
        assertEquals("10", JSON.toJSONString(params.get(1)));

        String rsql = ParameterizedOutputVisitorUtils.restore(psql, JdbcConstants.MYSQL, params);
        assertEquals("SELECT dep_id, dep_name, count(1)\n" +
                "FROM t\n" +
                "WHERE dep_tpe = 'aa'\n" +
                "GROUP BY dep_id\n" +
                "HAVING count(1) > 10", rsql);
    }

}
