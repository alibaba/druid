package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by wenshao on 23/11/2016.
 */
public class ExportParameterInTest {
    DbType dbType = JdbcConstants.MYSQL;

    @Test
    public void test_exportParameter() throws Exception {
        String sql = "select * from t_user where oid = '102' and uid in (1, 2, 3)";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        List<Object> parameters = new ArrayList<Object>();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, dbType);
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        visitor.setParameters(parameters);

        stmt.accept(visitor);

        System.out.println(out);
        System.out.println(JSON.toJSONString(parameters));

        restore(out.toString(), parameters);
    }

    public void restore(String sql, List<Object> parameters) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, dbType);
        visitor.setParameters(parameters);
        stmt.accept(visitor);

        System.out.println(out);
    }
}
