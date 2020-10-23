package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_78_nchar extends TestCase {

    public void test_for_parameterize() throws Exception {
        String sql = "select N'1' as `customerid`,N'5004' as `ordersourceid`,'2018-12-13 21:15:30.879' as `creationtime`";
        List<Object> outParameters = new ArrayList<Object>(0);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters, VisitorFeature.OutputParameterizedQuesUnMergeOr);
        assertEquals("SELECT ? AS `customerid`, ? AS `ordersourceid`\n\t, ? AS `creationtime`", psql);

        assertEquals("[\"1\",\"5004\",\"2018-12-13 21:15:30.879\"]", JSON.toJSONString(outParameters));

        String rsql = ParameterizedOutputVisitorUtils.restore(sql, DbType.mysql, outParameters);
        assertEquals("SELECT N'1' AS `customerid`, N'5004' AS `ordersourceid`\n"
            + "\t, '2018-12-13 21:15:30.879' AS `creationtime`", rsql);
    }
}
