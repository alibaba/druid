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

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        List<SQLCreateTableStatement> createStmtList = new ArrayList<SQLCreateTableStatement>();
        for (SQLStatement item : stmtList) {
            createStmtList.add((SQLCreateTableStatement) item);
        }

        SQLCreateTableStatement.sort(createStmtList);

        assertEquals("t9", createStmtList.get(9).getName().getSimpleName());
        assertEquals("t8", createStmtList.get(8).getName().getSimpleName());
        assertEquals("t7", createStmtList.get(7).getName().getSimpleName());
        assertEquals("t6", createStmtList.get(6).getName().getSimpleName());
        assertEquals("t5", createStmtList.get(5).getName().getSimpleName());


    }
}
