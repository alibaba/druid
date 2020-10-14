package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallUtils;

import java.util.List;

public class MySqlSelectTest_141_bigunion extends MysqlTest {

    public void test_small_10() throws Exception {
        StringBuilder buf = new StringBuilder();
        {
            buf.append("select * from (\n");

            for (int i = 0; i < 10; ++i) {
                if (i != 0) {
                    buf.append("union all\n");
                }
                buf.append("select :").append(i).append(" as sid from dual\n");
            }

            buf.append("\n) ux_x");
        }
        String sql = buf.toString();

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        System.out.println(stmt);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT :0 AS sid\n" +
                "\tFROM dual\n" +
                "\tUNION ALL\n" +
                "\tSELECT :1 AS sid\n" +
                "\tFROM dual\n" +
                "\tUNION ALL\n" +
                "\tSELECT :2 AS sid\n" +
                "\tFROM dual\n" +
                "\tUNION ALL\n" +
                "\tSELECT :3 AS sid\n" +
                "\tFROM dual\n" +
                "\tUNION ALL\n" +
                "\tSELECT :4 AS sid\n" +
                "\tFROM dual\n" +
                "\tUNION ALL\n" +
                "\tSELECT :5 AS sid\n" +
                "\tFROM dual\n" +
                "\tUNION ALL\n" +
                "\tSELECT :6 AS sid\n" +
                "\tFROM dual\n" +
                "\tUNION ALL\n" +
                "\tSELECT :7 AS sid\n" +
                "\tFROM dual\n" +
                "\tUNION ALL\n" +
                "\tSELECT :8 AS sid\n" +
                "\tFROM dual\n" +
                "\tUNION ALL\n" +
                "\tSELECT :9 AS sid\n" +
                "\tFROM dual\n" +
                ") ux_x", stmt.toString());
    }


    public void test_big_100000() throws Exception {
        StringBuilder buf = new StringBuilder();
        {
            buf.append("select * from (\n");

            for (int i = 0; i < 1000 * 100; ++i) {
                if (i != 0) {
                    buf.append("union all\n");
                }
                buf.append("select :").append(i).append(" as sid from dual\n");
            }

            buf.append("\n) ux_x");
        }
        String sql = buf.toString();

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        stmt.toString();

        WallUtils.isValidateMySql(sql);
        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(statVisitor);
    }

}