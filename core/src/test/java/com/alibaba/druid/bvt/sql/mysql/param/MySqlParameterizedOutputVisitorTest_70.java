package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlParameterizedOutputVisitorTest_70 {
    @Test
    public void test_in() throws Exception {
        String sql = "select ((0='x6') & 31) ^ (ROW(76, 4) NOT IN (ROW(1, 2 ),ROW(3, 4)) );";

        List<Object> params = new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params,
                VisitorFeature.OutputParameterizedUnMergeShardingTable,
                VisitorFeature.OutputParameterizedQuesUnMergeInList
        );
        assertEquals("SELECT ((? = ?) & ?) ^ (ROW(?, ?) NOT IN (ROW(?, ?), ROW(?, ?)));", psql);
        assertEquals(9, params.size());
        assertEquals("0", JSON.toJSONString(params.get(0)));

        assertEquals("[0,\"x6\",31,76,4,1,2,3,4]", JSON.toJSONString(params));

        String rsql = ParameterizedOutputVisitorUtils.restore(psql, JdbcConstants.MYSQL, params);
        assertEquals("SELECT ((0 = 'x6') & 31) ^ (ROW(76, 4) NOT IN (ROW(1, 2), ROW(3, 4)));", rsql);
    }
}
