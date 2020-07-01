package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallUtils;
import org.junit.Assert;

import java.util.List;

public class OracleListAggTest1 extends OracleTest {
    private final String dbType = JdbcConstants.ORACLE;

    public void test_0() throws Exception {
        String sql = "SELECT count(0) " +
                "FROM WEP_USER T " +
                "LEFT JOIN (" +
                "   SELECT T.ID, LISTAGG(T2.ROLE_NAME, ',') WITHIN GROUP (ORDER BY T.ID) AS ROLENAMES FROM WEP_USER T " +
                "   LEFT JOIN WEP_USER_ROLE T1 ON T.ID = T1.USER_ID " +
                "   LEFT JOIN WEP_ROLE T2 ON T1.ROLE_ID = T2.ID " +
                "   WHERE t.IS_DELETED = 0 " +
                "       AND T1.IS_DELETED = 0 " +
                "       AND T2.IS_DELETED = 0 " +
                "   GROUP BY T.ID" +
                ") T1 ON T.ID = T1.ID " +
                "WHERE t.IS_DELETED = 0";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = statementList.get(0);
        // print(statementList);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("SELECT count(0)\n" +
                        "FROM WEP_USER T\n" +
                        "\tLEFT JOIN (\n" +
                        "\t\tSELECT T.ID, LISTAGG(T2.ROLE_NAME, ',') WITHIN GROUP (ORDER BY T.ID) AS ROLENAMES\n" +
                        "\t\tFROM WEP_USER T\n" +
                        "\t\tLEFT JOIN WEP_USER_ROLE T1 ON T.ID = T1.USER_ID \n" +
                        "\t\t\tLEFT JOIN WEP_ROLE T2 ON T1.ROLE_ID = T2.ID \n" +
                        "\t\tWHERE t.IS_DELETED = 0\n" +
                        "\t\t\tAND T1.IS_DELETED = 0\n" +
                        "\t\t\tAND T2.IS_DELETED = 0\n" +
                        "\t\tGROUP BY T.ID\n" +
                        "\t) T1 ON T.ID = T1.ID \n" +
                        "WHERE t.IS_DELETED = 0",//
                            SQLUtils.toSQLString(stmt, dbType));

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

        Assert.assertEquals(3, visitor.getTables().size());
        Assert.assertEquals(8, visitor.getColumns().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("WEP_USER_ROLE")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("WEP_ROLE")));

        Assert.assertTrue(visitor.containsColumn("WEP_USER_ROLE", "IS_DELETED"));
        Assert.assertTrue(visitor.containsColumn("WEP_ROLE", "IS_DELETED"));

        WallUtils.isValidateOracle(sql);
    }

}
