package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlParameterizedOutputVisitorTest_66 {
    @Test
    public void test_for_parameterize() throws Exception {
        String sql = "select * from abc where 1=1 and id = trim(' abc ')";

        List<Object> params = new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params, VisitorFeature.OutputParameterizedUnMergeShardingTable);
        assertEquals("SELECT *\n" +
                "FROM abc\n" +
                "WHERE 1 = 1\n" +
                "\tAND id = ?", psql);
        assertEquals(1, params.size());
        assertEquals("\"abc\"", JSON.toJSONString(params.get(0)));

        String rsql = ParameterizedOutputVisitorUtils.restore(psql, JdbcConstants.MYSQL, params);
        assertEquals("SELECT *\n" +
                "FROM abc\n" +
                "WHERE 1 = 1\n" +
                "\tAND id = 'abc'", rsql);
    }
}
