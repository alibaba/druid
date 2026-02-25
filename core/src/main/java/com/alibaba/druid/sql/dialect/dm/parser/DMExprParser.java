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
package com.alibaba.druid.sql.dialect.dm.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

public class DMExprParser extends OracleExprParser {
    public DMExprParser(String sql) {
        this(new DMLexer(sql));
        this.lexer.nextToken();
        this.dbType = DbType.dm;
    }

    public DMExprParser(String sql, SQLParserFeature... features) {
        this(new DMLexer(sql, features));
        this.lexer.nextToken();
        this.dbType = DbType.dm;
    }

    public DMExprParser(Lexer lexer) {
        super(lexer);
        this.dbType = DbType.dm;
    }

    @Override
    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        if (lexer.token() == Token.IDENTITY) {
            lexer.nextToken();

            SQLColumnDefinition.Identity identity = new SQLColumnDefinition.Identity();
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                SQLIntegerExpr seed = (SQLIntegerExpr) this.primary();
                accept(Token.COMMA);
                SQLIntegerExpr increment = (SQLIntegerExpr) this.primary();
                accept(Token.RPAREN);

                identity.setSeed((Integer) seed.getNumber());
                identity.setIncrement((Integer) increment.getNumber());
            }

            column.setIdentity(identity);
        }

        return super.parseColumnRest(column);
    }
}
