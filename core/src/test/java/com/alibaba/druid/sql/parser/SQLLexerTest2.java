/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class SQLLexerTest2 extends TestCase {
    public void test_lexer() throws Exception {
        String sql = "SELECT * FROM T WHERE F1 = ? ORDER BY F2";
        Lexer lexer = new Lexer(sql);
        for (; ; ) {
            lexer.nextToken();
            Token tok = lexer.token();

            if (tok == Token.IDENTIFIER) {
                System.out.println(tok.name() + "\t\t" + lexer.stringVal());
            } else if (tok == Token.LITERAL_INT) {
                System.out.println(tok.name() + "\t\t" + lexer.numberString());
            } else {
                System.out.println(tok.name() + "\t\t\t" + tok.name);
            }

            if (tok == Token.WHERE) {
                System.out.println("where pos : " + lexer.pos());
            }

            if (tok == Token.EOF) {
                break;
            }
        }
    }

    public void test_lexer2() throws Exception {
        String sql = "SELECT substr('''a''bc',0,3) FROM dual";
        Lexer lexer = new Lexer(sql);
        for (; ; ) {
            lexer.nextToken();
            Token tok = lexer.token();

            if (tok == Token.IDENTIFIER) {
                System.out.println(tok.name() + "\t\t" + lexer.stringVal());
            } else if (tok == Token.LITERAL_INT) {
                System.out.println(tok.name() + "\t\t" + lexer.numberString());
            } else if (tok == Token.LITERAL_CHARS) {
                System.out.println(tok.name() + "\t\t" + lexer.stringVal());
            } else {
                System.out.println(tok.name() + "\t\t\t" + tok.name);
            }

            if (tok == Token.EOF) {
                break;
            }
        }
    }

    public void test_lexer_error_info() {
        String line1 = "SELECT *";
        String line2 = "FORM a";
        String sql = line1 + "\n" + line2;
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        Exception exception = null;
        try {
            parser.parseStatementList();
        } catch (Exception e) {
            exception = e;
        }
        assert exception != null;
        Assert.assertEquals("not supported.pos 13, line 2, column 2, token IDENTIFIER FORM", exception.getMessage());
    }

    public void test_lexer_computePos() {
        String sql = "\nSELECT;";
        Lexer lexer = new Lexer(sql);
        lexer.nextToken();
        lexer.nextToken();
        lexer.computeRowAndColumn();
        int posLine = lexer.getPosLine();
        int column = lexer.getPosColumn();
        Assert.assertEquals(posLine, 2);
        Assert.assertEquals(column, 7);
    }

}
