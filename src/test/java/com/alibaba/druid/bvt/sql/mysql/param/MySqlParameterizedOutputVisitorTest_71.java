package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class MySqlParameterizedOutputVisitorTest_71 extends TestCase {
    public void test_in() throws Exception {

        String sql = "SELECT (3, 4) IN ((1, 2), (3, 4)) FROM dual";

        List<Object> params =  new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params
                , VisitorFeature.OutputParameterizedUnMergeShardingTable
                ,VisitorFeature.OutputParameterizedQuesUnMergeInList
        );
        assertEquals("SELECT (?, ?) IN ((?, ?), (?, ?))\n" +
                "FROM dual", psql);
        assertEquals("[3,4,1,2,3,4]", JSON.toJSONString(params));


        String rsql = ParameterizedOutputVisitorUtils.restore(psql, JdbcConstants.MYSQL, params);
        assertEquals("SELECT (3, 4) IN ((1, 2), (3, 4))\n" +
                "FROM dual", rsql);
    }


}
