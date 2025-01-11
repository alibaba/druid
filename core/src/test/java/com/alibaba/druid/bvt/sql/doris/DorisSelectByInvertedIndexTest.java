package com.alibaba.druid.bvt.sql.doris;

import java.util.List;

import com.alibaba.druid.sql.DorisTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.doris.parser.DorisStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import org.junit.Assert;

public class DorisSelectByInvertedIndexTest extends DorisTest {

    public void test_0() throws Exception {
        String sql = "SELECT t.name, t.age FROM employee t WHERE t.name MATCH_ANY '张三 李四' and t.age > 10;";

        DorisStatementParser parser = new DorisStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(2, visitor.getColumns().size());
        //Assert.assertEquals(2, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employee")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("employee", "name")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("employee", "age")));

        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor outVisitor = new MySqlOutputVisitor(out);

        for (SQLStatement stmt : statementList) {
            stmt.accept(outVisitor);
        }

        System.out.println(out.toString());
    }


}
