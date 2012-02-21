package com.alibaba.druid.sql.dialect.postgresql.parser;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;

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
}
