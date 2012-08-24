/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveShowTablesStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class HiveStatementParser extends SQLStatementParser {

    public HiveStatementParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public HiveStatementParser(String sql){
        super(new SQLExprParser(sql));
    }

    public HiveCreateTableParser getSQLCreateTableParser() {
        return new HiveCreateTableParser(this.exprParser);
    }

    public SQLStatement parseShow() {
        acceptIdentifier("SHOW");

        if (identifierEquals("TABLES")) {
            lexer.nextToken();

            HiveShowTablesStatement stmt = new HiveShowTablesStatement();

            if (lexer.token() == Token.LITERAL_CHARS) {
                stmt.setPattern((SQLCharExpr) exprParser.primary());
            }

            return stmt;
        }

        throw new ParserException("TODO " + lexer.info());
    }
}
