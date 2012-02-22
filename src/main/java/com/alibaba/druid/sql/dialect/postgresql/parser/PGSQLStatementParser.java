package com.alibaba.druid.sql.dialect.postgresql.parser;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class PGSQLStatementParser extends SQLStatementParser {
	public PGSQLStatementParser(String sql) throws ParserException {
		this(new PGLexer(sql));
		this.lexer.nextToken();
	}

	public PGSQLStatementParser(Lexer lexer) {
		super(lexer);
	}

	public PGSelectParser createSQLSelectParser() {
		return new PGSelectParser(this.lexer);
	}
	
	public boolean parseStatementListDialect(List<SQLStatement> statementList) {
		if (lexer.token() == Token.WITH) {
			SQLStatement stmt = parseSelect();
			statementList.add(stmt);
			return true;
		}
		
		return false;
	}
}
