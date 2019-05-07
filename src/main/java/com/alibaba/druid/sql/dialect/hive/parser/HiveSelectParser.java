/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.dialect.hive.ast.HiveClusterBy;
import com.alibaba.druid.sql.dialect.hive.ast.HiveDistributeBy;
import com.alibaba.druid.sql.dialect.hive.ast.HiveSortBy;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveSelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;

public class HiveSelectParser extends SQLSelectParser {

	public HiveSelectParser(SQLExprParser exprParser) {
		super(exprParser);
	}

	public HiveSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
		super(exprParser, selectListCache);
	}

	public HiveSelectParser(String sql) {
		this(new HiveExprParser(sql));
	}

	protected SQLExprParser createExprParser() {
		return new HiveExprParser(lexer);
	}

	protected void parseSortBy(SQLSelectQueryBlock queryBlock) {
		if (lexer.token() == Token.SORT) {
			lexer.nextToken();
			accept(Token.BY);
			for (;;) {
				SQLExpr expr = this.expr();

				SQLSelectOrderByItem sortByItem = new SQLSelectOrderByItem(expr);

				if (lexer.token() == Token.ASC) {
					sortByItem.setType(SQLOrderingSpecification.ASC);
					lexer.nextToken();
				} else if (lexer.token() == Token.DESC) {
					sortByItem.setType(SQLOrderingSpecification.DESC);
					lexer.nextToken();
				}

				queryBlock.addSortBy(sortByItem);

				if (lexer.token() == Token.COMMA) {
					lexer.nextToken();
				} else {
					break;
				}
			}
		}
	}

	public SQLSelect select() {
		SQLSelect select = new SQLSelect();

		if (lexer.token() == Token.WITH) {
			SQLWithSubqueryClause with = this.parseWith();
			select.setWithSubQuery(with);
		}

		SQLSelectQuery query = query();
		select.setQuery(query);

		SQLOrderBy orderBy = this.parseOrderBy();

		if (query instanceof SQLSelectQueryBlock) {
			SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;

			if (queryBlock.getOrderBy() == null) {
				queryBlock.setOrderBy(orderBy);
			} else {
				select.setOrderBy(orderBy);
			}

			if (orderBy != null) {
				parseFetchClause(queryBlock);
			}
		} else {
			select.setOrderBy(orderBy);
		}

		while (lexer.token() == Token.HINT) {
			this.exprParser.parseHints(select.getHints());
		}

		return select;
	}

	public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
		if (lexer.token() == Token.LPAREN) {
			lexer.nextToken();
			SQLSelectQuery select = query();
			accept(Token.RPAREN);
			return queryRest(select, acceptUnion);
		}

		HiveSelectQueryBlock queryBlock = new HiveSelectQueryBlock();

		if (lexer.hasComment() && lexer.isKeepComments()) {
			queryBlock.addBeforeComment(lexer.readAndResetComments());
		}

		accept(Token.SELECT);

		if (lexer.token() == Token.HINT) {
			this.exprParser.parseHints(queryBlock.getHints());
		}

		if (lexer.token() == Token.COMMENT) {
			lexer.nextToken();
		}

		if (lexer.token() == Token.DISTINCT) {
			queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
			lexer.nextToken();
		} else if (lexer.token() == Token.ALL) {
			queryBlock.setDistionOption(SQLSetQuantifier.ALL);
			lexer.nextToken();
		}

		parseSelectList(queryBlock);

		parseFrom(queryBlock);

		parseWhere(queryBlock);

		parseGroupBy(queryBlock);
		
		parseHiveClusterBy(queryBlock);
		
		parseHiveDistributeBy(queryBlock);

		parseHiveSortBy(queryBlock);

		parseFetchClause(queryBlock);

		return queryRest(queryBlock, acceptUnion);
	}


	protected void parseHiveSortBy(HiveSelectQueryBlock queryBlock) {
		if (this.exprParser instanceof HiveExprParser) {
			HiveExprParser hiveExprParser = (HiveExprParser) this.exprParser;
			HiveSortBy hiveSortBy = hiveExprParser.parseHiveSortBy();
			if (queryBlock != null) {
				queryBlock.setHiveSortBy(hiveSortBy);
			}
		}
	}

	protected void parseHiveDistributeBy(HiveSelectQueryBlock queryBlock) {
		if (this.exprParser instanceof HiveExprParser) {
			HiveExprParser hiveExprParser = (HiveExprParser) this.exprParser;
			HiveDistributeBy hiveDistributeBy = hiveExprParser.parseHiveDistributeBy();
			if (queryBlock != null) {
				queryBlock.setHiveDistributeBy(hiveDistributeBy);
			}
		}
	}
	
	protected void parseHiveClusterBy(HiveSelectQueryBlock queryBlock) {
		if (this.exprParser instanceof HiveExprParser) {
			HiveExprParser hiveExprParser = (HiveExprParser) this.exprParser;
			HiveClusterBy hiveClusterBy = hiveExprParser.parseHiveClusterBy();
			if (queryBlock != null) {
				queryBlock.setHiveClusterBy(hiveClusterBy);
			}
		}
	}
}
