package com.alibaba.druid.demo.sql;

import com.alibaba.druid.sql.dialect.oracle.parser.OracleLexer;
import com.alibaba.druid.sql.parser.Token;
import junit.framework.TestCase;

public class OracleCompatibleTest extends TestCase {
    public void test_compatibleTest() throws Exception {
        String sql = "select * from t where rownum < 10"; //oracle ppas
        OracleLexer lexer = new OracleLexer(sql);
        for (;;) {
            lexer.nextToken();
            Token token = lexer.token();
            if (token == Token.EOF) {
                break;
            }
            if (token == Token.IDENTIFIER) {

                System.out.println(lexer.stringVal());
            } else if (token == Token.LITERAL_CHARS
                    || token == Token.LITERAL_INT
                    || token == Token.LITERAL_ALIAS) {
                // skip
            }
            System.out.println(token);
        }
    }
}
