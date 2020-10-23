package com.alibaba.druid.demo.sql;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.util.JdbcConstants;

import junit.framework.TestCase;

public class ExportParameters extends TestCase {
    public void test_export_parameters() throws Exception {
        String sql = "select * from t where id = 3 and name = 'abc'";
        
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql);
        
        StringBuilder out = new StringBuilder();
        ExportParameterVisitor visitor = new MySqlExportParameterVisitor(out);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        
        String paramteredSql = out.toString();
        System.out.println(paramteredSql);
        
        List<Object> paramters = visitor.getParameters(); // [3, "abc"]
        for (Object param : paramters) {
            System.out.println(param);
        }
    }
}
