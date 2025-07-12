package com.alibaba.druid.bvt.sql.visitor;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.visitor.SQLTableAliasCollectVisitor;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public class SQLTableAliasCollectVisitorTest {

    @Test
    public void test_1() {
        String sql = "SELECT t1.id, t2.name FROM table1 t1 JOIN table2 t2 ON t1.id = t2.id";
        SQLStatement statement = SQLUtils.parseSingleMysqlStatement(sql);
        SQLTableAliasCollectVisitor visitor = new SQLTableAliasCollectVisitor();
        if (statement instanceof SQLSelectStatement) {
            ((SQLSelectStatement) statement).getSelect().accept(visitor);
        }
        Collection<SQLTableSource> tableSources = visitor.getTableSources();

        // check tableSources size
        Assert.assertEquals(2, tableSources.size());

        // check tableSources
        SQLExprTableSource table1 = (SQLExprTableSource) tableSources.toArray()[0];
        Assert.assertEquals("t1", table1.getAlias());
        Assert.assertEquals("table1", table1.getTableName());
        SQLExprTableSource table2 = (SQLExprTableSource) tableSources.toArray()[1];
        Assert.assertEquals("t2", table2.getAlias());
        Assert.assertEquals("table2", table2.getTableName());
    }

}
