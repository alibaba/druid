package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Issue4071 {
    @Test
    public void test_for_issue() throws Exception {
        assertEquals("", new SQLSelectStatement().toString());
    }
}
