package com.alibaba.druid.bvt.sql.mysql.alterTable;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * @version 1.0
 * @ClassName MySqlAlterTableTest52_primary_prefix_key
 * @description
 * @Author zzy
 * @Date 2019-05-15 14:37
 */
public class MySqlAlterTableTest52_primary_prefix_key extends TestCase {

    public void test_0() {
        String sql = "alter table test001 add primary key (b (4) asc, c (8) desc);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE test001\n" +
                "\tADD PRIMARY KEY (b(4) ASC, c(8) DESC);", SQLUtils.toMySqlString(stmt));

    }

}
