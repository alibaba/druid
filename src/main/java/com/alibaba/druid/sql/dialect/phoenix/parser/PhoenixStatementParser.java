/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.phoenix.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

/**
 * Created by wenshao on 16/9/13.
 */
public class PhoenixStatementParser extends SQLStatementParser {
    public PhoenixStatementParser(String sql) {
        super (new PhoenixExprParser(sql));
    }

    public PhoenixStatementParser(Lexer lexer){
        super(new PhoenixExprParser(lexer));
    }

    @Override
    protected void parseInsertColumns(SQLInsertInto insert) {
        if (lexer.token() == Token.RPAREN ) {
            return;
        }

        for (;;) {
            SQLName expr = this.exprParser.name();
            expr.setParent(insert);
            insert.getColumns().add(expr);

            if (lexer.token() == Token.IDENTIFIER) {
                String text = lexer.stringVal();
                if (text.equalsIgnoreCase("TINYINT")
                    || text.equalsIgnoreCase("BIGINT")
                    || text.equalsIgnoreCase("INTEGER")
                    || text.equalsIgnoreCase("DOUBLE")
                    || text.equalsIgnoreCase("DATE")
                    || text.equalsIgnoreCase("VARCHAR")) {
                    expr.getAttributes().put("dataType", text);
                    lexer.nextToken();
                } else if (text.equalsIgnoreCase("CHAR")) {
                    String dataType = text;
                    lexer.nextToken();
                    accept(Token.LPAREN);
                    SQLExpr char_len = this.exprParser.primary();
                    accept(Token.RPAREN);
                    dataType += ("(" + char_len.toString() + ")");
                    expr.getAttributes().put("dataType", dataType);
                }
            }

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }
    }
}
