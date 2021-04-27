package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import junit.framework.TestCase;

import java.util.List;

public class Issue3534 extends TestCase {

    public void test0() throws Exception {
        String sql = "select d001,d002 from d_t\n" +
                "PIVOT ( SUM(d001) FOR d002 IN ([2019-06-01],[2019-06-02])) AS PVT";
        parseSql(sql);
    }

    public void test1() throws Exception {
        String sql = "with PIVOT_Table as (select * from ShoppingCart as C " +
                "PIVOT(count(TotalPrice) FOR [Week] IN([1],[2],[3],[4],[5],[6],[7])) AS T)" +
                "select * from PIVOT_Table UNPIVOT([RowCount] for [Week] in ([1],[2],[3],[4],[5],[6],[7])) as T\n";
        parseSql(sql);
    }

    private void parseSql(String sql) {
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.sqlserver);
        StringBuilder stringBuilder = new StringBuilder();
        SQLServerASTVisitor output = new SQLServerOutputVisitor(stringBuilder);
        sqlStatements.forEach(statement -> statement.accept(output));
        System.out.println(stringBuilder.toString());
    }
}
