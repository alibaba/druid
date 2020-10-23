package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class MySqlParameterizedOutputVisitorTest_69 extends TestCase {
    public void test_in() throws Exception {

        String sql = "select ((0='x6') & 31) ^ (ROW(76, 4) NOT IN (ROW(1, 2 ),ROW(3, 4)) );";

        List<Object> params =  new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params, VisitorFeature.OutputParameterizedUnMergeShardingTable);
        assertEquals("SELECT ((? = ?) & ?) ^ (ROW(?, ?) NOT IN (ROW(?, ?), ROW(?, ?)));", psql);
        assertEquals(9, params.size());
        assertEquals("0", JSON.toJSONString(params.get(0)));

        String rsql = ParameterizedOutputVisitorUtils.restore(psql, JdbcConstants.MYSQL, params);
        assertEquals("SELECT ((0 = 'x6') & 31) ^ (ROW(76, 4) NOT IN (ROW(1, 2), ROW(3, 4)));", rsql);
    }

    public void test_between() throws Exception {

        String sql = "select ((0='x6') & 31) ^ (76 NOT BETWEEN 3 AND 4) ;";

        List<Object> params =  new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params, VisitorFeature.OutputParameterizedUnMergeShardingTable);
        assertEquals("SELECT ((? = ?) & ?) ^ (? NOT BETWEEN ? AND ?);", psql);
        assertEquals(6, params.size());
        assertEquals("0", JSON.toJSONString(params.get(0)));

        String rsql = ParameterizedOutputVisitorUtils.restore(psql, JdbcConstants.MYSQL, params);
        assertEquals("SELECT ((0 = 'x6') & 31) ^ (76 NOT BETWEEN 3 AND 4);", rsql);
    }
}
