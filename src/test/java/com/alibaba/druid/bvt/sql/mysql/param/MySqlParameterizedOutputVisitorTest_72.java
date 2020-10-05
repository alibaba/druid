package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class MySqlParameterizedOutputVisitorTest_72 extends TestCase {
    public void test_in() throws Exception {

        String sql = "create table t1(pk bigint(20) primary key, integer_test int, varchar_test varchar(20));\n";

        List<Object> params =  new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params
                , VisitorFeature.OutputParameterizedUnMergeShardingTable
                ,VisitorFeature.OutputParameterizedQuesUnMergeInList
        );
        assertEquals("CREATE TABLE t1 (\n" +
                "\tpk bigint(20) PRIMARY KEY,\n" +
                "\tinteger_test int,\n" +
                "\tvarchar_test varchar(20)\n" +
                ");", psql);
        assertEquals("[]", JSON.toJSONString(params));


        String rsql = ParameterizedOutputVisitorUtils.restore(psql, JdbcConstants.MYSQL, params);
        assertEquals("CREATE TABLE t1 (\n" +
                "\tpk bigint(20) PRIMARY KEY,\n" +
                "\tinteger_test int,\n" +
                "\tvarchar_test varchar(20)\n" +
                ");", rsql);
    }


}
