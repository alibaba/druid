package com.alibaba.druid.bvt.sql;

import java.util.List;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

public class OverTest extends TestCase {

    public void test_over() throws Exception {
        String sql = "SELECT SalesOrderID, ProductID, OrderQty" + //
                     "    ,SUM(OrderQty) OVER(PARTITION BY SalesOrderID) AS 'Total'" + //
                     "    ,AVG(OrderQty) OVER(PARTITION BY SalesOrderID) AS 'Avg'" + //
                     "    ,COUNT(OrderQty) OVER(PARTITION BY SalesOrderID) AS 'Count'" + //
                     "    ,MIN(OrderQty) OVER(PARTITION BY SalesOrderID) AS 'Min'" + //
                     "    ,MAX(OrderQty) OVER(PARTITION BY SalesOrderID) AS 'Max' " + //
                     "FROM Sales.SalesOrderDetail " + //
                     "WHERE SalesOrderID IN(43659,43664);";

        SQLStatementParser parser = new SQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = new SchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());

        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getTables().size());
    }
}
