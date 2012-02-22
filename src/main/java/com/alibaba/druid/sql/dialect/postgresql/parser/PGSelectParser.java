package com.alibaba.druid.sql.dialect.postgresql.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.IntoClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.WithQuery;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;

public class PGSelectParser extends SQLSelectParser {

	public PGSelectParser(Lexer lexer) {
		super(lexer);
	}

	public PGSelectParser(String sql) throws ParserException {
		this(new PGLexer(sql));
		this.lexer.nextToken();
	}

	@Override
	protected SQLSelectQuery query() throws ParserException {
		PGSelectQueryBlock queryBlock = new PGSelectQueryBlock();

		if (lexer.token() == Token.WITH) {
			lexer.nextToken();

			PGSelectQueryBlock.WithClause withClause = new PGSelectQueryBlock.WithClause();

			if (lexer.token() == Token.RECURSIVE) {
				lexer.nextToken();
				withClause.setRecursive(true);
			}

			for (;;) {
				WithQuery withQuery = withQuery();
				withClause.getWithQuery().add(withQuery);
				if (lexer.token() == Token.COMMA) {
					lexer.nextToken();
					continue;
				} else {
					break;
				}
			}

			queryBlock.setWith(withClause);
		}

		accept(Token.SELECT);

		if (lexer.token() == Token.DISTINCT) {
			queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
			lexer.nextToken();

			if (lexer.token() == Token.ON) {
				lexer.nextToken();

				for (;;) {
					SQLExpr expr = this.createExprParser().expr();
					queryBlock.getDistinctOn().add(expr);
					if (lexer.token() == Token.COMMA) {
						lexer.nextToken();
						continue;
					} else {
						break;
					}
				}
			}
		} else if (lexer.token() == Token.ALL) {
			queryBlock.setDistionOption(SQLSetQuantifier.ALL);
			lexer.nextToken();
		}
		
		parseSelectList(queryBlock);
		
		if (lexer.token() == Token.INTO) {
			IntoClause into = new IntoClause();
			lexer.nextToken();
			
			if (lexer.token() == Token.TEMPORARY) {
				lexer.nextToken();
				into.setOption(IntoClause.Option.TEMPORARY);
			} else if (lexer.token() == Token.TEMP) {
				lexer.nextToken();
				into.setOption(IntoClause.Option.TEMP);
			} else if (lexer.token() == Token.UNLOGGED) {
				lexer.nextToken();
				into.setOption(IntoClause.Option.UNLOGGED);
			}
			
			if (lexer.token() == Token.TABLE) {
				lexer.nextToken();
			}
			
			SQLExpr name = this.createExprParser().name();
			into.setTable(name);
			
			queryBlock.setInto(into);
		}

		parseFrom(queryBlock);

		parseWhere(queryBlock);

		parseGroupBy(queryBlock);

		if (lexer.token() == Token.WINDOW) {
			lexer.nextToken();
			PGSelectQueryBlock.WindowClause window = new PGSelectQueryBlock.WindowClause();
			window.setName(this.expr());
			accept(Token.AS);

			for (;;) {
				SQLExpr expr = this.createExprParser().expr();
				window.getDefinition().add(expr);
				if (lexer.token() == Token.COMMA) {
					lexer.nextToken();
					continue;
				} else {
					break;
				}
			}
			queryBlock.setWindow(window);
		}

		queryBlock.setOrderBy(this.createExprParser().parseOrderBy());

		if (lexer.token() == Token.LIMIT) {
			lexer.nextToken();
			if (lexer.token() == Token.ALL) {
				queryBlock.setLimit(new SQLIdentifierExpr("ALL"));
				lexer.nextToken();
			} else {
				SQLExpr limit = expr();
				queryBlock.setLimit(limit);
			}
		}

		if (lexer.token() == Token.OFFSET) {
			lexer.nextToken();
			SQLExpr offset = expr();
			queryBlock.setOffset(offset);

			if (lexer.token() == Token.ROW || lexer.token() == Token.ROWS) {
				lexer.nextToken();
			} else {
				throw new ParserException("expect 'ROW' or 'ROWS'");
			}
		}

		if (lexer.token() == Token.FETCH) {
			lexer.nextToken();
			PGSelectQueryBlock.FetchClause fetch = new PGSelectQueryBlock.FetchClause();

			if (lexer.token() == Token.FIRST) {
				fetch.setOption(PGSelectQueryBlock.FetchClause.Option.FIRST);
			} else if (lexer.token() == Token.NEXT) {
				fetch.setOption(PGSelectQueryBlock.FetchClause.Option.NEXT);
			} else {
				throw new ParserException("expect 'FIRST' or 'NEXT'");
			}

			SQLExpr count = expr();
			fetch.setCount(count);

			if (lexer.token() == Token.ROW || lexer.token() == Token.ROWS) {
				lexer.nextToken();
			} else {
				throw new ParserException("expect 'ROW' or 'ROWS'");
			}

			if (lexer.token() == Token.ONLY) {
				lexer.nextToken();
			} else {
				throw new ParserException("expect 'ONLY'");
			}
			
			queryBlock.setFetch(fetch);
		}

		if (lexer.token() == Token.FOR) {
			lexer.nextToken();

			PGSelectQueryBlock.ForClause forClause = new PGSelectQueryBlock.ForClause();

			if (lexer.token() == Token.UPDATE) {
				forClause.setOption(PGSelectQueryBlock.ForClause.Option.UPDATE);
			} else if (lexer.token() == Token.SHARE) {
				forClause.setOption(PGSelectQueryBlock.ForClause.Option.SHARE);
			} else {
				throw new ParserException("expect 'FIRST' or 'NEXT'");
			}

			accept(Token.OF);

			for (;;) {
				SQLExpr expr = this.createExprParser().expr();
				forClause.getOf().add(expr);
				if (lexer.token() == Token.COMMA) {
					lexer.nextToken();
					continue;
				} else {
					break;
				}
			}

			if (lexer.token() == Token.NOWAIT) {
				lexer.nextToken();
				forClause.setNoWait(true);
			}
			
			queryBlock.setForClause(forClause);
		}
		
		return queryRest(queryBlock);
	}

	private WithQuery withQuery() {
		WithQuery withQuery = new WithQuery();
		withQuery.setName(expr());
		
		if (lexer.token() == Token.LPAREN) {
			lexer.nextToken();

			for (;;) {
				SQLExpr expr = this.createExprParser().expr();
				withQuery.getColumns().add(expr);
				if (lexer.token() == Token.COMMA) {
					lexer.nextToken();
					continue;
				} else {
					break;
				}
			}

			accept(Token.RPAREN);
		}

		accept(Token.AS);

		if (lexer.token() == Token.LPAREN) {
			lexer.nextToken();

			SQLSelectQuery subQuery = query();
			withQuery.setSubQuery(subQuery);
			accept(Token.RPAREN);
		}
		
		return withQuery;
	}
}
