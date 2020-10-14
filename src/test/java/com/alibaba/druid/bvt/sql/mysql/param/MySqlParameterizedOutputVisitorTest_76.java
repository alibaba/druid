package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class MySqlParameterizedOutputVisitorTest_76 extends TestCase {
    public void test_or() throws Exception {

        String sql = "select * from select_base_one_one_db_multi_tb where pk>=7 and pk>4 and pk <=49 and pk<18 order by pk limit 1";

        List<Object> outParameters = new ArrayList<Object>(0);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters, VisitorFeature.OutputParameterizedQuesUnMergeOr);
        assertEquals("SELECT *\n" +
                "FROM select_base_one_one_db_multi_tb\n" +
                "WHERE pk >= ?\n" +
                "\tAND pk > ?\n" +
                "\tAND pk <= ?\n" +
                "\tAND pk < ?\n" +
                "ORDER BY pk\n" +
                "LIMIT ?", psql);

        assertEquals("[7,4,49,18,1]", JSON.toJSONString(outParameters));
    }

    public void test_and() throws Exception {
        String sql = "select * from select_base_one_one_db_multi_tb where pk=1 and pk=4 and pk=49 and pk=18 order by pk limit 1";

        List<Object> outParameters = new ArrayList<Object>(0);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters, VisitorFeature.OutputParameterizedQuesUnMergeAnd);
        assertEquals("SELECT *\n" +
                "FROM select_base_one_one_db_multi_tb\n" +
                "WHERE pk = ?\n" +
                "\tAND pk = ?\n" +
                "\tAND pk = ?\n" +
                "\tAND pk = ?\n" +
                "ORDER BY pk\n" +
                "LIMIT ?", psql);

        assertEquals("[1,4,49,18,1]", JSON.toJSONString(outParameters));
    }

}
