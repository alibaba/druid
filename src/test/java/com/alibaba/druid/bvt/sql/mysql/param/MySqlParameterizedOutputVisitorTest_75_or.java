package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.ParameterizedVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class MySqlParameterizedOutputVisitorTest_75_or extends TestCase {
    public void test_or() throws Exception {

        String sql = "select * from t1 where id = 1 or id = 2 or id = 3";

        List<Object> outParameters = new ArrayList<Object>(0);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters, VisitorFeature.OutputParameterizedQuesUnMergeOr);
        assertEquals("SELECT *\n" +
                "FROM t1\n" +
                "WHERE id = ?\n" +
                "\tOR id = ?\n" +
                "\tOR id = ?", psql);

        assertEquals("[1,2,3]", JSON.toJSONString(outParameters));
    }

    public void test_or_2() throws Exception {

        String sql = "select * from t1 where id = 1 or id = 2 or id = 3";

        List<Object> outParameters = new ArrayList<Object>(0);

        SQLStatement stmt = SQLUtils.parseStatements(sql, DbType.mysql).get(0);

        StringBuilder out = new StringBuilder(sql.length());
        ParameterizedVisitor visitor = ParameterizedOutputVisitorUtils.createParameterizedOutputVisitor(out, DbType.mysql);
        visitor.config(VisitorFeature.OutputParameterizedQuesUnMergeOr, true);
        if (outParameters != null) {
            visitor.setOutputParameters(outParameters);
        }
        stmt.accept(visitor);

        String psql = out.toString();
        assertEquals("SELECT *\n" +
                "FROM t1\n" +
                "WHERE id = ?\n" +
                "\tOR id = ?\n" +
                "\tOR id = ?", psql);

        assertEquals("[1,2,3]", JSON.toJSONString(outParameters));
    }
}
