package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlParameterizedOutputVisitorTest_71 {
    @Test
    public void test_in() throws Exception {
        String sql = "SELECT (3, 4) IN ((1, 2), (3, 4)) FROM dual";

        List<Object> params = new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params,
                VisitorFeature.OutputParameterizedUnMergeShardingTable,
                VisitorFeature.OutputParameterizedQuesUnMergeInList
        );
        assertEquals("SELECT (?, ?) IN ((?, ?), (?, ?))\n" +
                "FROM dual", psql);
        assertEquals("[3,4,1,2,3,4]", JSON.toJSONString(params));

        String rsql = ParameterizedOutputVisitorUtils.restore(psql, JdbcConstants.MYSQL, params);
        assertEquals("SELECT (3, 4) IN ((1, 2), (3, 4))\n" +
                "FROM dual", rsql);
    }
}
