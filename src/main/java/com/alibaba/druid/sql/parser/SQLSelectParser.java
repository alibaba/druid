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
package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionOperator;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;

public class SQLSelectParser extends SQLParser {
    protected SQLExprParser exprParser;

    public SQLSelectParser(String sql){
        super(sql);
    }

    public SQLSelectParser(Lexer lexer){
        super(lexer);
    }
    
    public SQLSelectParser(SQLExprParser exprParser){
        super(exprParser.getLexer());
        this.exprParser = exprParser;
    }

    public SQLSelect select() {
        SQLSelect select = new SQLSelect();

        select.setQuery(query());
        select.setOrderBy(parseOrderBy());

        if (select.getOrderBy() == null) {
            select.setOrderBy(parseOrderBy());
        }

        return select;
    }

    protected SQLUnionQuery createSQLUnionQuery() {
        return new SQLUnionQuery();
    }

    public SQLUnionQuery unionRest(SQLUnionQuery union) {
        if (lexer.token() == Token.ORDER) {
            SQLOrderBy orderBy = this.exprParser.parseOrderBy();
            union.setOrderBy(orderBy);
            return unionRest(union);
        }
        return union;
    }

    public SQLSelectQuery queryRest(SQLSelectQuery selectQuery) {
        if (lexer.token() == Token.UNION) {
            lexer.nextToken();

            SQLUnionQuery union = createSQLUnionQuery();
            union.setLeft(selectQuery);

            if (lexer.token() == Token.ALL) {
                union.setOperator(SQLUnionOperator.UNION_ALL);
                lexer.nextToken();
            } else if (lexer.token() == Token.DISTINCT) {
                union.setOperator(SQLUnionOperator.DISTINCT);
                lexer.nextToken();
            }
            SQLSelectQuery right = this.query();
            union.setRight(right);

            return unionRest(union);
        }

        if (lexer.token() == Token.INTERSECT) {
            lexer.nextToken();

            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            union.setOperator(SQLUnionOperator.INTERSECT);

            SQLSelectQuery right = this.query();
            union.setRight(right);

            return union;
        }

        if (lexer.token() == Token.MINUS) {
            lexer.nextToken();

            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            union.setOperator(SQLUnionOperator.MINUS);

            SQLSelectQuery right = this.query();
            union.setRight(right);

            return union;
        }

        return selectQuery;
    }

    public SQLSelectQuery query() {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select);
        }

        accept(Token.SELECT);

        SQLSelectQueryBlock queryBlock = new SQLSelectQueryBlock();

        if (lexer.token() == Token.DISTINCT) {
            queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
            lexer.nextToken();
        } else if (lexer.token() == Token.UNIQUE) {
            queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
            lexer.nextToken();
        } else if (lexer.token() == Token.ALL) {
            queryBlock.setDistionOption(SQLSetQuantifier.ALL);
            lexer.nextToken();
        }

        parseSelectList(queryBlock);

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseGroupBy(queryBlock);

        return queryRest(queryBlock);
    }

    protected void parseWhere(SQLSelectQueryBlock queryBlock) {
        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();

            queryBlock.setWhere(expr());
        }
    }

    protected void parseGroupBy(SQLSelectQueryBlock queryBlock) {
        if (lexer.token() == Token.GROUP) {
            lexer.nextToken();
            accept(Token.BY);

            SQLSelectGroupByClause groupBy = new SQLSelectGroupByClause();
            while (true) {
                groupBy.getItems().add(expr());
                if (lexer.token() != Token.COMMA) {
                    break;
                }

                lexer.nextToken();
            }

            if (lexer.token() == Token.HAVING) {
                lexer.nextToken();

                groupBy.setHaving(expr());
            }

            queryBlock.setGroupBy(groupBy);
        } else if (lexer.token() == (Token.HAVING)) {
            lexer.nextToken();

            SQLSelectGroupByClause groupBy = new SQLSelectGroupByClause();
            groupBy.setHaving(this.expr());
            queryBlock.setGroupBy(groupBy);
        }
    }

    protected void parseSelectList(SQLSelectQueryBlock queryBlock) {
        queryBlock.getSelectList().add(new SQLSelectItem(expr(), as()));

        while (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            queryBlock.getSelectList().add(new SQLSelectItem(expr(), as()));
        }
    }

    public void parseFrom(SQLSelectQueryBlock queryBlock) {
        if (lexer.token() != Token.FROM) {
            return;
        }

        lexer.nextToken();

        queryBlock.setFrom(parseTableSource());
    }

    public SQLTableSource parseTableSource() {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            SQLTableSource tableSource;
            if (lexer.token() == Token.SELECT) {
                SQLSelect select = select();
                accept(Token.RPAREN);
                queryRest(select.getQuery());
                tableSource = new SQLSubqueryTableSource(select);
            } else if (lexer.token() == Token.LPAREN) {
                tableSource = parseTableSource();
                accept(Token.RPAREN);
            } else {
                tableSource = parseTableSource();
                accept(Token.RPAREN);
            }

            return parseTableSourceRest(tableSource);
        }

        if (lexer.token() == Token.SELECT) {
            throw new ParserException("TODO");
        }

        SQLExprTableSource tableReference = new SQLExprTableSource();

        parseTableSourceQueryTableExpr(tableReference);

        return parseTableSourceRest(tableReference);
    }

    private void parseTableSourceQueryTableExpr(SQLExprTableSource tableReference) {
        if (lexer.token() == Token.LITERAL_ALIAS || lexer.token() == Token.IDENTIFIED
            || lexer.token() == Token.LITERAL_CHARS) {
            tableReference.setExpr(this.exprParser.name());
            return;
        }

        tableReference.setExpr(expr());
    }

    protected SQLTableSource parseTableSourceRest(SQLTableSource tableSource) {
        if ((tableSource.getAlias() == null) || (tableSource.getAlias().length() == 0)) {
            if (lexer.token() != Token.LEFT && lexer.token() != Token.RIGHT && lexer.token() != Token.FULL) {
                String alias = as();
                if (alias != null) {
                    tableSource.setAlias(alias);
                    return parseTableSourceRest(tableSource);
                }
            }
        }

        SQLJoinTableSource.JoinType joinType = null;

        if (lexer.token() == Token.LEFT) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }

            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.LEFT_OUTER_JOIN;
        } else if (lexer.token() == Token.RIGHT) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.RIGHT_OUTER_JOIN;
        } else if (lexer.token() == Token.FULL) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.FULL_OUTER_JOIN;
        } else if (lexer.token() == Token.INNER) {
            lexer.nextToken();
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.INNER_JOIN;
        } else if (lexer.token() == Token.JOIN) {
            lexer.nextToken();
            joinType = SQLJoinTableSource.JoinType.JOIN;
        } else if (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            joinType = SQLJoinTableSource.JoinType.COMMA;
        } else if (identifierEquals("STRAIGHT_JOIN")) {
            lexer.nextToken();
            joinType = SQLJoinTableSource.JoinType.STRAIGHT_JOIN;
        }

        if (joinType != null) {
            SQLJoinTableSource join = new SQLJoinTableSource();
            join.setLeft(tableSource);
            join.setJoinType(joinType);
            join.setRight(parseTableSource());

            if (lexer.token() == Token.ON) {
                lexer.nextToken();
                join.setCondition(expr());
            }

            return parseTableSourceRest(join);
        }

        return tableSource;
    }

    public SQLExpr expr() {
        return this.exprParser.expr();
    }

    public SQLOrderBy parseOrderBy() {
        return this.exprParser.parseOrderBy();
    }

    public void acceptKeyword(String ident) {
        if (lexer.token() == Token.IDENTIFIER && ident.equalsIgnoreCase(lexer.stringVal())) {
            lexer.nextToken();
        } else {
            setErrorEndPos(lexer.pos());
            throw new SQLParseException("syntax error, expect " + ident + ", actual " + lexer.token());
        }
    }

}
