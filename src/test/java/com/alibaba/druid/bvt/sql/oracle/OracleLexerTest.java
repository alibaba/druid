/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.oracle;

import junit.framework.TestCase;

import com.alibaba.druid.sql.dialect.oracle.parser.OracleLexer;
import com.alibaba.druid.sql.parser.Token;

public class OracleLexerTest extends TestCase {

    public void test_hint() throws Exception {
        String sql = "SELECT /*+FIRST_ROWS*/ * FROM T WHERE F1 = ? ORDER BY F2";
        OracleLexer lexer = new OracleLexer(sql);
        for (;;) {
            lexer.nextToken();
            Token tok = lexer.token();

            switch (tok) {
                case IDENTIFIER:
                    System.out.println(tok.name() + "\t\t" + lexer.stringVal());
                    break;
                case HINT:
                    System.out.println(tok.name() + "\t\t\t" + lexer.stringVal());
                    break;
                default:
                    System.out.println(tok.name() + "\t\t\t" + tok.name);
                    break;
            }

            if (tok == Token.EOF) {
                break;
            }
        }
    }
}
