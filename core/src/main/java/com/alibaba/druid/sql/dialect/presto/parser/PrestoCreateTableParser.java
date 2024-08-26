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
package com.alibaba.druid.sql.dialect.presto.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

/**
 * presto 的create table解析器
 *
 * @author yanlong
 * @since 2023-03-13
 */
public class PrestoCreateTableParser extends SQLCreateTableParser {
    public PrestoCreateTableParser(String sql) {
        super(sql);
    }

    public PrestoCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    @Override
    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLExpr comment = this.exprParser.expr();
            stmt.setComment(comment);
        }

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            accept(Token.LPAREN);
            parseAssignItems(stmt.getTableOptions(), stmt, false);
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.AS) {
            lexer.nextToken();
            SQLSelect select = this.createSQLSelectParser().select();
            stmt.setSelect(select);
        }
        super.parseCreateTableRest(stmt);
    }

    protected PrestoCreateTableStatement newCreateStatement() {
        return new PrestoCreateTableStatement();
    }
}
