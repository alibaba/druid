package com.alibaba.druid.bvt.sql.mysql;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

import junit.framework.TestCase;


public class MySql57Test extends TestCase {
    public void test_0() throws Exception {
        String sql = "ALTER TABLE t1 ALGORITHM=INPLACE, CHANGE COLUMN c1 c1 VARCHAR(255);";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        
        assertEquals("ALTER TABLE t1" //
                + "\n\tALGORITHM = INPLACE," //
                + "\n\tCHANGE COLUMN c1 c1 VARCHAR(255);", SQLUtils.toMySqlString(stmt));
        
        assertEquals("alter table t1" //
                            + "\n\tALGORITHM = INPLACE," //
                            + "\n\tchange column c1 c1 VARCHAR(255);", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
