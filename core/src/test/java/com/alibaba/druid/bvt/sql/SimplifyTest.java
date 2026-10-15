package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimplifyTest {
    @Test
    public void test_simplify_column() throws Exception {
        SQLColumnDefinition column = new SQLColumnDefinition();

        column.setName("`a`");

        SQLName name_0 = column.getName();
        column.simplify();

        assertNotSame(name_0, column.getName());
        assertEquals("a", column.getName().getSimpleName());

        name_0 = column.getName();
        column.simplify();
        assertSame(name_0, column.getName());
    }
}
