package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.Map;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 验证 Postgresql 没有解析行号的问题 #5287
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5287">增强 #5287</a>
 */
public class Issue5287 {

    @Test
    public void test_get_source_location() throws Exception {
        for (DbType dbType : new DbType[]{DbType.postgresql}) {
            String sql =
                "select * from t1 \n"
                    + " where a=1 \n"
                    + " and b =2 \n"
                    + " order by c desc;";
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
            PGSelectStatement statement = (PGSelectStatement) parser.parseStatement();
            SQLOrderBy orderBy = statement.getSelect().getQueryBlock().getOrderBy();
            SQLExpr cExpr = orderBy.getItems().get(0).getExpr();
            SQLIdentifierExpr si = (SQLIdentifierExpr) cExpr;
            int sourceLine1 = si.getSourceLine();
            System.out.println(" statement.getSourceLine()===" + orderBy.getSourceLine() + "||" + sourceLine1);
            assertEquals(0, sourceLine1);
            parser = SQLParserUtils.createSQLStatementParser(sql, dbType, SQLParserFeature.KeepSourceLocation);
            statement = (PGSelectStatement) parser.parseStatement();
            orderBy = statement.getSelect().getQueryBlock().getOrderBy();
            cExpr = orderBy.getItems().get(0).getExpr();
            si = (SQLIdentifierExpr) cExpr;
            int sourceLine2 = si.getSourceLine();
            System.out.println(" statement.getSourceLine()===" + orderBy.getSourceLine() + "||" + sourceLine2);
            assertEquals(4, sourceLine2);

        }
    }
}
