package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import junit.framework.TestCase;

public class UnsignedBigIntTest extends TestCase {

    public void test_mysqlUnsignedBitInt() throws Exception {
        String sql = "SELECT a from b where c <> 1 LIMIT 18446744073709551615 OFFSET 0";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();
        StringBuilder sb = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(sb);
        visitor.setPrettyFormat(false);
        sqlStatement.accept(visitor);
        assertEquals("SELECT a FROM b WHERE c <> 1 LIMIT 0, 18446744073709551615", sb.toString());
    }

    public void test_postgresqlUnsignedBitInt() {
        String sql = "SELECT a from b where c <> 1 LIMIT 18446744073709551615 OFFSET 1";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();
        StringBuilder sb = new StringBuilder();
        PGOutputVisitor visitor = new PGOutputVisitor(sb);
        visitor.setPrettyFormat(false);
        sqlStatement.accept(visitor);
        assertEquals("SELECT a FROM b WHERE c <> 1 LIMIT 18446744073709551615 OFFSET 1", sb.toString());
    }
}
