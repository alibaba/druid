package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 23/11/2016.
 */
public class ExportParameterShardingTest extends TestCase {
    DbType dbType = JdbcConstants.MYSQL;

    public void test_exportParameter() throws Exception {
        String sql = "select * from t_user_0000 where oid = 1001";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, dbType);
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        List<Object> parameters = visitor.getParameters();
        //visitor.setParameters(parameters);

        stmt.accept(visitor);

        System.out.println(out);
        System.out.println(JSON.toJSONString(parameters));

        String restoredSql = restore(out.toString(), parameters);
        assertEquals("SELECT *\n" +
                "FROM t_user_0000\n" +
                "WHERE oid = 1001", restoredSql);
    }

    public String restore(String sql, List<Object> parameters) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, dbType);
        visitor.setInputParameters(parameters);

        visitor.addTableMapping("t_user", "t_user_0000");

        stmt.accept(visitor);

        return out.toString();
    }
}
