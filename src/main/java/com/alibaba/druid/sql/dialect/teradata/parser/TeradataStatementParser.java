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
package com.alibaba.druid.sql.dialect.teradata.parser;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleSelectParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;

public class TeradataStatementParser extends SQLStatementParser {

    public TeradataStatementParser(String sql){
        super(new TeradataExprParser(sql));
    }

    public TeradataStatementParser(Lexer lexer){
        super(new TeradataExprParser(lexer));
    }
    
    public TeradataExprParser getExprParser() {
    	return (TeradataExprParser) exprParser;
    }
    
    public TeradataSelectParser createSQLSelectParser() {
    	return new TeradataSelectParser(this.exprParser);
    }
    
    public SQLSelectStatement parseSelect() {
    	TeradataSelectParser selectParser = new TeradataSelectParser(this.exprParser);
    	return new SQLSelectStatement(selectParser.select(), JdbcConstants.TERADATA);
    }
    
    public void parseStatementList(List<SQLStatement> statementList, int max) {
    	for (;;) {
            if (max != -1) {
                if (statementList.size() >= max) {
                    return;
                }
            }

            if (lexer.token() == Token.EOF) {
                return;
            }
            if (lexer.token() == Token.END) {
                return;
            }
            if (lexer.token() == Token.ELSE) {
                return;
            }

            if (lexer.token() == (Token.SEMI)) {
                lexer.nextToken();
                continue;
            }

            if (lexer.token() == (Token.SELECT)) {
                statementList.add(parseSelect());
                continue;
            } else {
                super.parseStatementList(statementList, max);	
            }
    	}
    }
    
    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.SEL) {
        	statementList.add(parseSelect());
            return true;
        }
        return false;
    }

}
