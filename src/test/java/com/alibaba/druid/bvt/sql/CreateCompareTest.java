package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wenshao on 18/07/2017.
 */
public class CreateCompareTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "create table t7 (\n" +
                "\tint bigint,\n" +
                "\tFOREIGN KEY (id) REFERENCES t6 (id)\n" +
                ");\n" +
                "\n" +
                "create table t2 (\n" +
                "\tint bigint\n" +
                ");\n" +
                "\n" +
                "create table t9 (\n" +
                "\tint bigint,\n" +
                "\tFOREIGN KEY (id) REFERENCES t8 (id)\n" +
                ");\n" +
                "\n" +
                "create table t6 (\n" +
                "\tint bigint,\n" +
                "\tFOREIGN KEY (id) REFERENCES t5 (id)\n" +
                ");\n" +
                "\n" +
                "create table t8 (\n" +
                "\tint bigint,\n" +
                "\tFOREIGN KEY (id) REFERENCES t7 (id)\n" +
                ");\n" +
                "\n" +
                "create table t4 (\n" +
                "\tint bigint,\n" +
                "\tFOREIGN KEY (id) REFERENCES t2 (id)\n" +
                ");\n" +
                "\n" +
                "create table t5 (\n" +
                "\tint bigint,\n" +
                "\tFOREIGN KEY (id) REFERENCES t4 (id)\n" +
                ");\n" +
                "\n" +
                "create table t3 (\n" +
                "\tint bigint,\n" +
                "\tFOREIGN KEY (id) REFERENCES t2 (id)\n" +
                ");\n" +
                "\n" +
                "create table t0 (\n" +
                "\tint bigint\n" +
                ");\n" +
                "\n" +
                "create table t1 (\n" +
                "\tint bigint\n" +
                ");";

        List stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);


        SQLCreateTableStatement.sort(stmtList);

        assertEquals("t9", ((SQLCreateTableStatement)stmtList.get(9)).getName().getSimpleName());
        assertEquals("t3", ((SQLCreateTableStatement)stmtList.get(8)).getName().getSimpleName());
        assertEquals("t0", ((SQLCreateTableStatement)stmtList.get(7)).getName().getSimpleName());
        assertEquals("t1", ((SQLCreateTableStatement)stmtList.get(6)).getName().getSimpleName());
        assertEquals("t8", ((SQLCreateTableStatement)stmtList.get(5)).getName().getSimpleName());
        assertEquals("t7", ((SQLCreateTableStatement)stmtList.get(4)).getName().getSimpleName());
        assertEquals("t6", ((SQLCreateTableStatement)stmtList.get(3)).getName().getSimpleName());
        assertEquals("t5", ((SQLCreateTableStatement)stmtList.get(2)).getName().getSimpleName());
        assertEquals("t4", ((SQLCreateTableStatement)stmtList.get(1)).getName().getSimpleName());
        assertEquals("t2", ((SQLCreateTableStatement)stmtList.get(0)).getName().getSimpleName());


        String sortedSql = SQLUtils.toSQLString(stmtList, JdbcConstants.ORACLE);
        System.out.println(sortedSql);
    }
}
