package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class MySqlParameterizedOutputVisitorTest_73 extends TestCase {
    public void test_in() throws Exception {

        String sql = "replace into t1(pk, integer_test, varchar_test) values(9223372036854775808,-1,'feed32feed')";

        List<Object> outParameters = new ArrayList<Object>(0);

//        String parameterize = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters,
//                                                                           VisitorFeature.OutputParameterizedQuesUnMergeInList,
//                                                                           VisitorFeature.OutputParameterizedUnMergeShardingTable);

        List<Object> params =  new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters, VisitorFeature.OutputParameterizedQuesUnMergeInList,
                                                                   VisitorFeature.OutputParameterizedUnMergeShardingTable);
        assertEquals("REPLACE INTO t1 (pk, integer_test, varchar_test)\n" + "VALUES (?, ?, ?)", psql);
    }


}
