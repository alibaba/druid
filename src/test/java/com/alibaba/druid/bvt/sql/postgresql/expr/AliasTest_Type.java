package com.alibaba.druid.bvt.sql.postgresql.expr;
import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.Assert;
import java.util.List;
/**
 *测试 TYPE作为别名
 */
public class AliasTest_Type extends PGTest {
    public void test_timestamp() throws Exception {
        String sql = "select column1 as TYPE from table1 where xx=1";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);
        Assert.assertEquals(1, statementList.size());
        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);
        String mergedSql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.POSTGRESQL);
        System.out.println(mergedSql);
    }
}
