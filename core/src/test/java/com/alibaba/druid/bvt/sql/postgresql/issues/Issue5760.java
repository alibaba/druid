package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5760">Issue来源</a>
 */
public class Issue5760 {

    @Test
    public void test_parse_error_sql() {
            Assert.assertThrows(ParserException.class,
                () -> SQLUtils.parseStatements("Vacuum verbose", DbType.postgresql));
            Assert.assertThrows(ParserException.class,
                () -> SQLUtils.parseStatements("Vacuum verbose;", DbType.postgresql));
            Assert.assertThrows(ParserException.class,
                () -> SQLUtils.parseStatements("Vacuum verbose full", DbType.postgresql));

            Assert.assertThrows(ParserException.class,
                () -> SQLUtils.parseStatements("Vacuum verbose full;", DbType.postgresql));

            Assert.assertThrows(ParserException.class,
                () -> SQLUtils.parseStatements("Vacuum verbose; select a from b", DbType.postgresql));

            Assert.assertThrows(ParserException.class,
                () -> {
                    List<SQLStatement> list = SQLUtils.parseStatements("Vacuum verbose full"
                        + ";"
                        + "Vacuum verbose bbbb;", DbType.postgresql);
                    System.out.println("list = " + list);
                    Assert.assertEquals(1, list.size());
                });
    }
}
