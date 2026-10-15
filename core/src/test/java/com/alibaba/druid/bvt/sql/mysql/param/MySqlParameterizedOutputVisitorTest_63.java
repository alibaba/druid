package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by wenshao on 16/9/23.
 */
public class MySqlParameterizedOutputVisitorTest_63 {
    @Test
    public void test_for_parameterize() throws Exception {
        String sql = "select * from abc where id in (null)";

        List<Object> params = new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params, VisitorFeature.OutputParameterizedUnMergeShardingTable);
        assertEquals("SELECT *\n" +
                "FROM abc\n" +
                "WHERE id IN (?)", psql);
        assertEquals(1, params.size());
        assertEquals("null", JSON.toJSONString(params.get(0)));

        String rsql = ParameterizedOutputVisitorUtils.restore(sql, JdbcConstants.MYSQL, params);
        assertEquals("SELECT *\n" +
                "FROM abc\n" +
                "WHERE id IN (NULL)", rsql);
    }

    @Test
    public void test_for_parameterize_1() throws Exception {
        String sql = "select * from abc where id in (1)";

        List<Object> params = new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params, VisitorFeature.OutputParameterizedUnMergeShardingTable);
        assertEquals("SELECT *\n" +
                "FROM abc\n" +
                "WHERE id IN (?)", psql);
        assertEquals(1, params.size());
        assertEquals("1", JSON.toJSONString(params.get(0)));

        String rsql = ParameterizedOutputVisitorUtils.restore(sql, JdbcConstants.MYSQL, params);
        assertEquals("SELECT *\n" +
                "FROM abc\n" +
                "WHERE id IN (1)", rsql);
    }

    @Test
    public void test_for_parameterize_2() throws Exception {
        String sql = "select * from abc where id in (null, null)";

        List<Object> params = new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params, VisitorFeature.OutputParameterizedUnMergeShardingTable);
        assertEquals("SELECT *\n" +
                "FROM abc\n" +
                "WHERE id IN (?)", psql);
        assertEquals(2, params.size());
        assertEquals("null", JSON.toJSONString(params.get(0)));
        assertEquals("null", JSON.toJSONString(params.get(1)));

        String rsql = ParameterizedOutputVisitorUtils.restore(sql, JdbcConstants.MYSQL, params);
        assertEquals("SELECT *\n" +
                "FROM abc\n" +
                "WHERE id IN (NULL, NULL)", rsql);
    }
}
