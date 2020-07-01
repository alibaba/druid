package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class Issue1994 extends TestCase {

    public void test_for_issue() throws Exception {
        String sql = "INSERT INTO MKTG_H_EXEC_RESULT_FACT\n" +
                "(THE_DATE, AREA_ID, SCENE_ID, MKTG_CNT, MKTG_SUC_CNT\n" +
                ", TASK_CNT, TASK_F_CNT, TASK_F_SUC_CNT, CON_CNT, CON_SUC_CNT)\n" +
                "SELECT TRUNC(SYSDATE), T1.AREA_ID\n" +
                ", RTRIM(TO_CHAR(T2.PID))\n" +
                ", SUM(T1.MKTG_CNT), SUM(T1.MKTG_SUC_CNT)\n" +
                ", SUM(T1.TASK_CNT), SUM(T1.TASK_F_CNT)\n" +
                ", SUM(T1.TASK_F_SUC_CNT), SUM(T1.CON_CNT)\n" +
                ", SUM(T1.CON_SUC_CNT)\n" +
                "FROM MKTG_H_EXEC_RESULT_FACT T1, (\n" +
                "SELECT DISTINCT MKTG_PLAN_LVL1_ID AS PID, MKTG_PLAN_LVL4_ID AS SCENE_ID\n" +
                "FROM DMN_MKTG_PLAN_TYPE\n" +
                "UNION ALL\n" +
                "SELECT DISTINCT MKTG_PLAN_LVL2_ID AS PID, MKTG_PLAN_LVL4_ID AS SCENE_ID\n" +
                "FROM DMN_MKTG_PLAN_TYPE_TWO\n" +
                "WHERE MKTG_PLAN_LVL2_ID <> MKTG_PLAN_LVL4_ID\n" +
                "UNION ALL\n" +
                "SELECT DISTINCT MKTG_PLAN_LVL3_ID AS PID, MKTG_PLAN_LVL4_ID AS SCENE_ID\n" +
                "FROM DMN_MKTG_PLAN_TYPE\n" +
                "WHERE MKTG_PLAN_LVL3_ID <> MKTG_PLAN_LVL4_ID\n" +
                ") T2\n" +
                "WHERE T1.THE_DATE = TRUNC(SYSDATE)\n" +
                "AND T1.SCENE_ID = T2.SCENE_ID\n" +
                "GROUP BY T1.AREA_ID, RTRIM(TO_CHAR(T2.PID))";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        assertEquals(1,  stmtList.size());

        SQLStatement stmt = stmtList.get(0);

        System.out.println(stmt);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ORACLE);
        stmt.accept(statVisitor);

        System.out.println("columns : " + statVisitor.getColumns());

        assertTrue(statVisitor.containsColumn("MKTG_H_EXEC_RESULT_FACT", "THE_DATE"));
        assertTrue(statVisitor.containsColumn("MKTG_H_EXEC_RESULT_FACT", "AREA_ID"));
        assertTrue(statVisitor.containsColumn("MKTG_H_EXEC_RESULT_FACT", "SCENE_ID"));
        assertTrue(statVisitor.containsColumn("MKTG_H_EXEC_RESULT_FACT", "MKTG_CNT"));
        assertTrue(statVisitor.containsColumn("MKTG_H_EXEC_RESULT_FACT", "MKTG_SUC_CNT"));

        assertTrue(statVisitor.containsColumn("MKTG_H_EXEC_RESULT_FACT", "TASK_CNT"));
        assertTrue(statVisitor.containsColumn("MKTG_H_EXEC_RESULT_FACT", "TASK_F_CNT"));
        assertTrue(statVisitor.containsColumn("MKTG_H_EXEC_RESULT_FACT", "TASK_F_SUC_CNT"));
        assertTrue(statVisitor.containsColumn("MKTG_H_EXEC_RESULT_FACT", "CON_CNT"));
        assertTrue(statVisitor.containsColumn("MKTG_H_EXEC_RESULT_FACT", "CON_SUC_CNT"));

        assertTrue(statVisitor.containsColumn("DMN_MKTG_PLAN_TYPE", "MKTG_PLAN_LVL1_ID"));
        assertTrue(statVisitor.containsColumn("DMN_MKTG_PLAN_TYPE", "MKTG_PLAN_LVL4_ID"));
        assertTrue(statVisitor.containsColumn("DMN_MKTG_PLAN_TYPE_TWO", "MKTG_PLAN_LVL2_ID"));
        assertTrue(statVisitor.containsColumn("DMN_MKTG_PLAN_TYPE_TWO", "MKTG_PLAN_LVL4_ID"));
        assertTrue(statVisitor.containsColumn("DMN_MKTG_PLAN_TYPE", "MKTG_PLAN_LVL3_ID"));

        OracleInsertStatement insertStmt = (OracleInsertStatement) stmt;
        OracleSelectQueryBlock queryBlock = (OracleSelectQueryBlock) insertStmt.getQuery().getQueryBlock();

        SQLSelectItem selectItem = queryBlock.getSelectList().get(0);
        assertEquals("TRUNC(SYSDATE)", selectItem.toString());
    }
}
