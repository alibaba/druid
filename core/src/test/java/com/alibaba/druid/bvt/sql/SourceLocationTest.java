package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SourceLocationTest {
    @Test
    public void test_0() throws Exception {
        String sql = "\nselect getdate()";
        DbType[] dbTypes = new DbType[]{DbType.mysql, DbType.oracle, DbType.db2, DbType.odps};
        for (DbType dbType : dbTypes) {
            SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils
                    .parseSingleStatement(
                            sql,
                            dbType,
                            SQLParserFeature.KeepSourceLocation
                    );

            SQLExpr expr = stmt.getSelect().getQueryBlock().getSelectList().get(0).getExpr();
            assertEquals(2, expr.getSourceLine());
            assertEquals(dbType.name(), 8, expr.getSourceColumn());
        }
    }
}
