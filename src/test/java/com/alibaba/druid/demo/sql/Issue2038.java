package com.alibaba.druid.demo.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2ASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class Issue2038 extends TestCase {
    public void test_for_demo() throws Exception {
        String sql = "select * from (select * from t union all select * from t1 union all select * from t3) xx";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.db2);


        SQLASTVisitor visitor = new DB2ASTVisitorAdapter() {
            public boolean visit(SQLUnionQuery x) {
                System.out.println("union");
                return true;
            }

            public boolean visit(DB2SelectQueryBlock x) {
                System.out.println("select");
                return true;
            }
        };

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
    }
}
