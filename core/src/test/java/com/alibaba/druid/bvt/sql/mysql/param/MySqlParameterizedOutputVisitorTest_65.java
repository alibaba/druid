package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by wenshao on 16/9/23.
 */
public class MySqlParameterizedOutputVisitorTest_65 {
    @Test
    public void test_for_parameterize() throws Exception {
        List<Object> outParams = new ArrayList<Object>();

        String sql = "select * from abc";
        assertEquals("select * from abc", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
        assertEquals("SELECT *\n" +
                "FROM abc", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParams, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));
    }
}
