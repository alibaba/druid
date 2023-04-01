package com.alibaba.druid.sql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorImpl;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.sql.visitor.functions.Function;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.List;

public class PrecomputedFunctionTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "select * from tb1 where dt > '20230320' and dt < TO_DATE2('20230320', 'yyyymmdd')";
        DbType dbType = JdbcConstants.ODPS;
        SQLStatement statement = SQLUtils.parseSingleStatement(sql, dbType);

        // 只考虑查询语句
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) statement;
        SQLEvalVisitor sqlEvalVisitor = new SQLEvalVisitorImpl();

        // 1. 向visitor里面注册函数，一定要【小写】
        sqlEvalVisitor.registerFunction("to_date2", new Function() {
            @Override
            public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
                List<SQLExpr> arguments = x.getArguments();
                if(!arguments.get(0).computeDataType().isString() && !arguments.get(1).computeDataType().isString()){
                    throw new RuntimeException("函数参数类型错误");
                }
                SQLCharExpr dateFormat = (SQLCharExpr) arguments.get(1);
                SimpleDateFormat formatter = new SimpleDateFormat(dateFormat.getText());

                SQLCharExpr date = (SQLCharExpr) arguments.get(0);
                String dateString = date.getText();
                return new SQLCharExpr(dateString);
            }
        });

        // 2. 获得所有函数
        OdpsSchemaStatVisitor visitor = new OdpsSchemaStatVisitor();
        sqlSelectStatement.accept(visitor);
        List<SQLMethodInvokeExpr> functions = visitor.getFunctions();

        // 3. 执行函数并进行替换
        functions.forEach(f -> SQLEvalVisitorUtils.visit(sqlEvalVisitor, f, true));
        System.out.println(SQLUtils.toSQLString(sqlSelectStatement));
    }
}
