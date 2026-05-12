package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MysqlDeallocatePrepareStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeallocatePrepareTest {
    @Test
    public void test() {
        String sql = "DEALLOCATE PREPARE stmt1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatement();
        assertEquals(MysqlDeallocatePrepareStatement.class, stmt.getClass());
        MysqlDeallocatePrepareStatement dpStmt = (MysqlDeallocatePrepareStatement) stmt;
        assertEquals("stmt1", dpStmt.getStatementName().getSimpleName());
        assertEquals(sql, dpStmt.toString());
    }
}
