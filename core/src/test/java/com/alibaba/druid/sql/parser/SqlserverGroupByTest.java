package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by magicdoom on 2016/8/16.
 */
public class SqlserverGroupByTest {
    @Test
    public void testGroupBy() throws Exception {
        String sql = "SELECT a.workflowid, COUNT(1) FROM workflow_base a GROUP BY a.workflowid";
        SQLStatementParser parser = new SQLServerStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatement();
        try {
            stmt.toString();
        } catch (ClassCastException e) {
            fail(e.getMessage());
        }

    }
}
