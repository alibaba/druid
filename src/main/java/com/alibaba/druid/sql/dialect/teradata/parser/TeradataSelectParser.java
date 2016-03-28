package com.alibaba.druid.sql.dialect.teradata.parser;

import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.teradata.ast.stmt.TeradataSelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;

public class TeradataSelectParser extends SQLSelectParser{

	public TeradataSelectParser(SQLExprParser exprParser) {
		super(exprParser);
	}
	
	public TeradataSelectParser(String sql) {
		this(new TeradataExprParser(sql));
	}
	
	public SQLSelectQuery query() {
		if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select);
        }
        
        TeradataSelectQueryBlock queryBlock = new TeradataSelectQueryBlock();
        
        if (lexer.token() == Token.SEL 
        		|| lexer.token() == Token.SELECT) {

            lexer.nextToken();

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            if (lexer.token() == (Token.DISTINCT)) {
                queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
                lexer.nextToken();
            } else if (lexer.token() == (Token.ALL)) {
                queryBlock.setDistionOption(SQLSetQuantifier.ALL);
                lexer.nextToken();
            }
            parseSelectList(queryBlock);
        }
        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseGroupBy(queryBlock);
        
        queryBlock.setOrderBy(this.exprParser.parseOrderBy());

        return queryRest(queryBlock);
	}

}
