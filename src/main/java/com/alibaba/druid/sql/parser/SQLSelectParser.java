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
package com.alibaba.druid.sql.parser;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLWindow;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLDateExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLRealExpr;
import com.alibaba.druid.sql.ast.expr.SQLTimestampExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprHint;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLLateralViewTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionOperator;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLValuesTableSource;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOrderingExpr;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;

public class SQLSelectParser extends SQLParser {
    protected SQLExprParser      exprParser;
    protected SQLSelectListCache selectListCache;

    public SQLSelectParser(String sql){
        super(sql);
    }

    public SQLSelectParser(Lexer lexer){
        super(lexer);
    }

    public SQLSelectParser(SQLExprParser exprParser){
        this(exprParser, null);
    }

    public SQLSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache){
        super(exprParser.getLexer(), exprParser.getDbType());
        this.exprParser = exprParser;
        this.selectListCache = selectListCache;
    }

    public SQLSelect select() {
        SQLSelect select = new SQLSelect();

        if (lexer.token == Token.WITH) {
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

        while (lexer.token == Token.HINT) {
            this.exprParser.parseHints(select.getHints());
        }

        return select;
    }

    protected SQLUnionQuery createSQLUnionQuery() {
        SQLUnionQuery union = new SQLUnionQuery();
        union.setDbType(getDbType());
        return union;
    }

    public SQLUnionQuery unionRest(SQLUnionQuery union) {
        if (lexer.token == Token.ORDER) {
            SQLOrderBy orderBy = this.exprParser.parseOrderBy();
            union.setOrderBy(orderBy);
            return unionRest(union);
        }
        return union;
    }

    public SQLSelectQuery queryRest(SQLSelectQuery selectQuery) {
        return queryRest(selectQuery, true);
    }

    public SQLSelectQuery queryRest(SQLSelectQuery selectQuery, boolean acceptUnion) {
        if (!acceptUnion) {
            return selectQuery;
        }

        if (lexer.token == Token.UNION) {
            do {
                lexer.nextToken();

                SQLUnionQuery union = createSQLUnionQuery();
                if (union.getLeft() == null) {
                    union.setLeft(selectQuery);
                }

                boolean paren = lexer.token == Token.LPAREN;

                if (lexer.token == Token.ALL) {
                    union.setOperator(SQLUnionOperator.UNION_ALL);
                    lexer.nextToken();
                } else if (lexer.token == Token.DISTINCT) {
                    union.setOperator(SQLUnionOperator.DISTINCT);
                    lexer.nextToken();
                }
                SQLSelectQuery right = this.query(paren ? null : union, false);
                union.setRight(right);

                if (!paren) {
                    if (right instanceof SQLSelectQueryBlock) {
                        SQLSelectQueryBlock rightQuery = (SQLSelectQueryBlock) right;
                        SQLOrderBy orderBy = rightQuery.getOrderBy();
                        SQLLimit limit = rightQuery.getLimit();
                        if (orderBy != null && limit == null) {
                            union.setOrderBy(orderBy);
                            rightQuery.setOrderBy(null);
                        }
                    } else if (right instanceof SQLUnionQuery) {
                        SQLUnionQuery rightUnion = (SQLUnionQuery) right;
                        if (rightUnion.getOrderBy() != null) {
                            union.setOrderBy(rightUnion.getOrderBy());
                            rightUnion.setOrderBy(null);
                        }
                    }
                }

                union = unionRest(union);

                selectQuery = union;

            } while (lexer.token() == Token.UNION);

            selectQuery = queryRest(selectQuery, true);

            return selectQuery;
        }

        if (lexer.token == Token.EXCEPT) {
            lexer.nextToken();

            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            union.setOperator(SQLUnionOperator.EXCEPT);

            SQLSelectQuery right = this.query(union, false);
            union.setRight(right);

            return queryRest(union, true);
        }

        if (lexer.token == Token.INTERSECT) {
            lexer.nextToken();

            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            union.setOperator(SQLUnionOperator.INTERSECT);

            SQLSelectQuery right = this.query(union, false);
            union.setRight(right);

            return queryRest(union, true);
        }

        if (acceptUnion && lexer.token == Token.MINUS) {
            lexer.nextToken();

            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            union.setOperator(SQLUnionOperator.MINUS);

            SQLSelectQuery right = this.query(union, false);
            union.setRight(right);

            return queryRest(union, true);
        }

        return selectQuery;
    }

    public SQLSelectQuery query() {
        return query(null, true);
    }

    public SQLSelectQuery query(SQLObject parent) {
        return query(parent, true);
    }

    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        if (lexer.token == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            select.setBracket(true);
            accept(Token.RPAREN);

            return queryRest(select, acceptUnion);
        }

        SQLSelectQueryBlock queryBlock = new SQLSelectQueryBlock();

        if (lexer.hasComment() && lexer.isKeepComments()) {
            queryBlock.addBeforeComment(lexer.readAndResetComments());
        }

        accept(Token.SELECT);

        if (lexer.token() == Token.HINT) {
            this.exprParser.parseHints(queryBlock.getHints());
        }

        if (lexer.token == Token.COMMENT) {
            lexer.nextToken();
        }

        if (JdbcConstants.INFORMIX.equals(dbType)) {
            if (lexer.identifierEquals(FnvHash.Constants.SKIP)) {
                lexer.nextToken();
                SQLExpr offset = this.exprParser.primary();
                queryBlock.setOffset(offset);
            }

            if (lexer.identifierEquals(FnvHash.Constants.FIRST)) {
                lexer.nextToken();
                SQLExpr first = this.exprParser.primary();
                queryBlock.setFirst(first);
            }
        }

        if (lexer.token == Token.DISTINCT) {
            queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
            lexer.nextToken();
        } else if (lexer.token == Token.UNIQUE) {
            queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
            lexer.nextToken();
        } else if (lexer.token == Token.ALL) {
            queryBlock.setDistionOption(SQLSetQuantifier.ALL);
            lexer.nextToken();
        }

        parseSelectList(queryBlock);

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseGroupBy(queryBlock);

        parseSortBy(queryBlock);

        parseFetchClause(queryBlock);

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();
            accept(Token.UPDATE);

            queryBlock.setForUpdate(true);

            if (lexer.identifierEquals(FnvHash.Constants.NO_WAIT) || lexer.identifierEquals(FnvHash.Constants.NOWAIT)) {
                lexer.nextToken();
                queryBlock.setNoWait(true);
            } else if (lexer.identifierEquals(FnvHash.Constants.WAIT)) {
                lexer.nextToken();
                SQLExpr waitTime = this.exprParser.primary();
                queryBlock.setWaitTime(waitTime);
            }
        }

        return queryRest(queryBlock, acceptUnion);
    }

    protected void parseSortBy(SQLSelectQueryBlock queryBlock) {

    }

    protected void withSubquery(SQLSelect select) {
        if (lexer.token == Token.WITH) {
            lexer.nextToken();

            SQLWithSubqueryClause withQueryClause = new SQLWithSubqueryClause();

            if (lexer.token == Token.RECURSIVE || lexer.identifierEquals(FnvHash.Constants.RECURSIVE)) {
                lexer.nextToken();
                withQueryClause.setRecursive(true);
            }

            for (;;) {
                SQLWithSubqueryClause.Entry entry = new SQLWithSubqueryClause.Entry();
                entry.setParent(withQueryClause);

                String alias = this.lexer.stringVal();
                lexer.nextToken();
                entry.setAlias(alias);

                if (lexer.token == Token.LPAREN) {
                    lexer.nextToken();
                    exprParser.names(entry.getColumns());
                    accept(Token.RPAREN);
                }

                accept(Token.AS);
                accept(Token.LPAREN);
                entry.setSubQuery(select());
                accept(Token.RPAREN);

                withQueryClause.addEntry(entry);

                if (lexer.token == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }

            select.setWithSubQuery(withQueryClause);
        }
    }

    public SQLWithSubqueryClause parseWith() {
        accept(Token.WITH);

        SQLWithSubqueryClause withQueryClause = new SQLWithSubqueryClause();

        if (lexer.token == Token.RECURSIVE || lexer.identifierEquals(FnvHash.Constants.RECURSIVE)) {
            lexer.nextToken();
            withQueryClause.setRecursive(true);
        }

        for (;;) {
            SQLWithSubqueryClause.Entry entry = new SQLWithSubqueryClause.Entry();
            entry.setParent(withQueryClause);

            String alias = this.lexer.stringVal();
            lexer.nextToken();
            entry.setAlias(alias);

            if (lexer.token == Token.LPAREN) {
                lexer.nextToken();
                exprParser.names(entry.getColumns());
                accept(Token.RPAREN);
            }

            accept(Token.AS);
            accept(Token.LPAREN);

            switch (lexer.token) {
                case SELECT:
                    entry.setSubQuery(select());
                    break;
                default:
                    break;
            }

            accept(Token.RPAREN);

            withQueryClause.addEntry(entry);

            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        return withQueryClause;
    }

    public void parseWhere(SQLSelectQueryBlock queryBlock) {
        if (lexer.token == Token.WHERE) {
            lexer.nextTokenIdent();

            List<String> beforeComments = null;
            if (lexer.hasComment() && lexer.isKeepComments()) {
                beforeComments = lexer.readAndResetComments();
            }

            SQLExpr where;

            if (lexer.token == Token.IDENTIFIER) {
                String ident = lexer.stringVal();
                long hash_lower = lexer.hash_lower();
                lexer.nextTokenEq();

                SQLExpr identExpr;
                if (lexer.token == Token.LITERAL_CHARS) {
                    String literal = lexer.stringVal;
                    if (hash_lower == FnvHash.Constants.TIMESTAMP) {
                        identExpr = new SQLTimestampExpr(literal);
                        lexer.nextToken();
                    } else if (hash_lower == FnvHash.Constants.DATE) {
                        identExpr = new SQLDateExpr(literal);
                        lexer.nextToken();
                    } else if (hash_lower == FnvHash.Constants.REAL) {
                        identExpr = new SQLRealExpr(Float.parseFloat(literal));
                        lexer.nextToken();
                    } else {
                        identExpr = new SQLIdentifierExpr(ident, hash_lower);
                    }
                } else {
                    identExpr = new SQLIdentifierExpr(ident, hash_lower);
                }

                if (lexer.token == Token.DOT) {
                    identExpr = this.exprParser.primaryRest(identExpr);
                }

                if (lexer.token == Token.EQ) {
                    SQLExpr rightExp;

                    lexer.nextToken();

                    try {
                        rightExp = this.exprParser.bitOr();
                    } catch (EOFParserException e) {
                        throw new ParserException("EOF, " + ident + "=", e);
                    }

                    where = new SQLBinaryOpExpr(identExpr, SQLBinaryOperator.Equality, rightExp, dbType);
                    switch (lexer.token) {
                        case BETWEEN:
                        case IS:
                        case EQ:
                        case IN:
                        case CONTAINS:
                        case BANG_TILDE_STAR:
                        case TILDE_EQ:
                        case LT:
                        case LTEQ:
                        case LTEQGT:
                        case GT:
                        case GTEQ:
                        case LTGT:
                        case BANGEQ:
                        case LIKE:
                        case NOT:
                            where = this.exprParser.relationalRest(where);
                            break;
                        default:
                            break;
                    }

                    where = this.exprParser.andRest(where);
                    where = this.exprParser.xorRest(where);
                    where = this.exprParser.orRest(where);
                } else {
                    identExpr = this.exprParser.primaryRest(identExpr);
                    where = this.exprParser.exprRest(identExpr);
                }
            } else {
                where = this.exprParser.expr();
            }
//            where = this.exprParser.expr();

            if (beforeComments != null) {
                where.addBeforeComment(beforeComments);
            }

            if (lexer.hasComment() && lexer.isKeepComments() //
                    && lexer.token != Token.INSERT // odps multi-insert
                    ) {
                where.addAfterComment(lexer.readAndResetComments());
            }

            queryBlock.setWhere(where);
        }
    }

    protected void parseWindow(SQLSelectQueryBlock queryBlock) {
        if (!(lexer.identifierEquals(FnvHash.Constants.WINDOW) || lexer.token == Token.WINDOW)) {
            return;
        }

        lexer.nextToken();

        for (;;) {
            SQLName name = this.exprParser.name();
            accept(Token.AS);
            SQLOver over = new SQLOver();
            this.exprParser.over(over);
            queryBlock.addWindow(new SQLWindow(name, over));

            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }
    }
    
    protected void parseGroupBy(SQLSelectQueryBlock queryBlock) {
        if (lexer.token == (Token.GROUP)) {
            lexer.nextTokenBy();
            accept(Token.BY);

            SQLSelectGroupByClause groupBy = new SQLSelectGroupByClause();
            if (lexer.identifierEquals(FnvHash.Constants.ROLLUP)) {
                lexer.nextToken();
                accept(Token.LPAREN);
                groupBy.setWithRollUp(true);
            }
            if (lexer.identifierEquals(FnvHash.Constants.CUBE)) {
                lexer.nextToken();
                accept(Token.LPAREN);
                groupBy.setWithCube(true);
            }

            for (;;) {
                SQLExpr item = parseGroupByItem();
                
                item.setParent(groupBy);
                groupBy.addItem(item);

                if (!(lexer.token == (Token.COMMA))) {
                    break;
                }

                lexer.nextToken();
            }
            if (groupBy.isWithRollUp() || groupBy.isWithCube()) {
                accept(Token.RPAREN);
            }

            if (lexer.token == (Token.HAVING)) {
                lexer.nextToken();

                SQLExpr having = this.exprParser.expr();
                groupBy.setHaving(having);
            }
            
            if (lexer.token == Token.WITH) {
                lexer.nextToken();
                
                if (lexer.identifierEquals(FnvHash.Constants.CUBE)) {
                    lexer.nextToken();
                    groupBy.setWithCube(true);
                } else if(lexer.identifierEquals(FnvHash.Constants.ROLLUP)) {
                    lexer.nextToken();
                    groupBy.setWithRollUp(true);
                } else if (lexer.identifierEquals(FnvHash.Constants.RS)
                        && JdbcConstants.DB2.equals(dbType)) {
                    lexer.nextToken();
                    ((DB2SelectQueryBlock) queryBlock).setIsolation(DB2SelectQueryBlock.Isolation.RS);
                } else if (lexer.identifierEquals(FnvHash.Constants.RR)
                        && JdbcConstants.DB2.equals(dbType)) {
                    lexer.nextToken();
                    ((DB2SelectQueryBlock) queryBlock).setIsolation(DB2SelectQueryBlock.Isolation.RR);
                } else if (lexer.identifierEquals(FnvHash.Constants.CS)
                        && JdbcConstants.DB2.equals(dbType)) {
                    lexer.nextToken();
                    ((DB2SelectQueryBlock) queryBlock).setIsolation(DB2SelectQueryBlock.Isolation.CS);
                } else if (lexer.identifierEquals(FnvHash.Constants.UR)
                        && JdbcConstants.DB2.equals(dbType)) {
                    lexer.nextToken();
                    ((DB2SelectQueryBlock) queryBlock).setIsolation(DB2SelectQueryBlock.Isolation.UR);
                } else {
                    throw new ParserException("TODO " + lexer.info());
                }
            }
            
            queryBlock.setGroupBy(groupBy);
        } else if (lexer.token == (Token.HAVING)) {
            lexer.nextToken();

            SQLSelectGroupByClause groupBy = new SQLSelectGroupByClause();
            groupBy.setHaving(this.exprParser.expr());

            if (lexer.token == (Token.GROUP)) {
                lexer.nextToken();
                accept(Token.BY);

                for (;;) {
                    SQLExpr item = parseGroupByItem();
                    
                    item.setParent(groupBy);
                    groupBy.addItem(item);

                    if (!(lexer.token == (Token.COMMA))) {
                        break;
                    }

                    lexer.nextToken();
                }
            }
            
            if (lexer.token == Token.WITH) {
                lexer.nextToken();
                acceptIdentifier("ROLLUP");

                groupBy.setWithRollUp(true);
            }
            
            if(JdbcConstants.MYSQL.equals(getDbType())
                    && lexer.token == Token.DESC) {
                lexer.nextToken(); // skip
            }

            queryBlock.setGroupBy(groupBy);
        }
    }

    protected SQLExpr parseGroupByItem() {
        SQLExpr item = this.exprParser.expr();
        
        if(JdbcConstants.MYSQL.equals(getDbType())) {
            if (lexer.token == Token.DESC) {
                lexer.nextToken(); // skip
                item =new MySqlOrderingExpr(item, SQLOrderingSpecification.DESC);
            } else if (lexer.token == Token.ASC) {
                lexer.nextToken(); // skip
                item =new MySqlOrderingExpr(item, SQLOrderingSpecification.ASC);
            }
        }
        return item;
    }

    protected void parseSelectList(SQLSelectQueryBlock queryBlock) {
        final List<SQLSelectItem> selectList = queryBlock.getSelectList();
        for (;;) {
            final SQLSelectItem selectItem = this.exprParser.parseSelectItem();
            selectList.add(selectItem);
            selectItem.setParent(queryBlock);

            if (lexer.token != Token.COMMA) {
                break;
            }

            lexer.nextToken();
        }
    }

    public void parseFrom(SQLSelectQueryBlock queryBlock) {
        if (lexer.token != Token.FROM) {
            return;
        }
        
        lexer.nextToken();
        
        queryBlock.setFrom(
                parseTableSource());
    }

    public SQLTableSource parseTableSource() {
        if (lexer.token == Token.LPAREN) {
            lexer.nextToken();
            SQLTableSource tableSource;
            if (lexer.token == Token.SELECT || lexer.token == Token.WITH
                    || lexer.token == Token.SEL) {
                SQLSelect select = select();
                accept(Token.RPAREN);
                SQLSelectQuery query = queryRest(select.getQuery(), true);
                if (query instanceof SQLUnionQuery) {
                    tableSource = new SQLUnionQueryTableSource((SQLUnionQuery) query);
                } else {
                    tableSource = new SQLSubqueryTableSource(select);
                }
            } else if (lexer.token == Token.LPAREN) {
                tableSource = parseTableSource();
                accept(Token.RPAREN);
            } else {
                tableSource = parseTableSource();
                accept(Token.RPAREN);
            }

            if (lexer.token == Token.AS
                    && tableSource instanceof SQLValuesTableSource
                    && ((SQLValuesTableSource) tableSource).getColumns().size() == 0)
            {
                lexer.nextToken();

                String alias = this.tableAlias();
                tableSource.setAlias(alias);

                SQLValuesTableSource values = (SQLValuesTableSource) tableSource;
                accept(Token.LPAREN);
                this.exprParser.names(values.getColumns(), values);
                accept(Token.RPAREN);
            }

            return parseTableSourceRest(tableSource);
        }

        if (lexer.token() == Token.VALUES) {
            lexer.nextToken();
            SQLValuesTableSource tableSource = new SQLValuesTableSource();

            for (;;) {
                accept(Token.LPAREN);
                SQLListExpr listExpr = new SQLListExpr();
                this.exprParser.exprList(listExpr.getItems(), listExpr);
                accept(Token.RPAREN);

                listExpr.setParent(tableSource);

                tableSource.getValues().add(listExpr);

                if (lexer.token == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }

            if (lexer.token == Token.RPAREN) {
                return tableSource;
            }

            String alias = this.tableAlias();
            if (alias != null) {
                tableSource.setAlias(alias);
            }

            accept(Token.LPAREN);
            this.exprParser.names(tableSource.getColumns(), tableSource);
            accept(Token.RPAREN);

            return tableSource;
        }

        if (lexer.token == Token.SELECT) {
            throw new ParserException("TODO " + lexer.info());
        }

        SQLExprTableSource tableReference = new SQLExprTableSource();

        parseTableSourceQueryTableExpr(tableReference);

        SQLTableSource tableSrc = parseTableSourceRest(tableReference);

        if (lexer.hasComment() && lexer.isKeepComments()) {
            tableSrc.addAfterComment(lexer.readAndResetComments());
        }

        return tableSrc;
    }

    protected void parseTableSourceQueryTableExpr(SQLExprTableSource tableReference) {
        if (lexer.token == Token.LITERAL_ALIAS || lexer.token == Token.IDENTIFIED
            || lexer.token == Token.LITERAL_CHARS) {
            tableReference.setExpr(this.exprParser.name());
            return;
        }

        tableReference.setExpr(expr());
    }

    protected SQLTableSource primaryTableSourceRest(SQLTableSource tableSource) {
        return tableSource;
    }

    protected SQLTableSource parseTableSourceRest(SQLTableSource tableSource) {
        if (tableSource.getAlias() == null || tableSource.getAlias().length() == 0) {
            Token token = lexer.token;
            long hash;
            if (token != Token.LEFT
                    && token != Token.RIGHT
                    && token != Token.FULL
                    && token != Token.OUTER
                    && !(token == Token.IDENTIFIER
                        && ((hash = lexer.hash_lower()) == FnvHash.Constants.STRAIGHT_JOIN
                            || hash == FnvHash.Constants.CROSS)))
            {
                String alias = tableAlias();
                if (alias != null) {
                    tableSource.setAlias(alias);

                    if (lexer.token == Token.WHERE) {
                        return tableSource;
                    }

                    return parseTableSourceRest(tableSource);
                }
            }
        }

        SQLJoinTableSource.JoinType joinType = null;

        boolean natural = lexer.identifierEquals(FnvHash.Constants.NATURAL) && JdbcConstants.MYSQL.equals(dbType);
        if (natural) {
            lexer.nextToken();
        }

        if (lexer.token == Token.LEFT) {
            lexer.nextToken();

            if (lexer.identifierEquals(FnvHash.Constants.SEMI)) {
                lexer.nextToken();
                joinType = SQLJoinTableSource.JoinType.LEFT_SEMI_JOIN;
            } else if (lexer.identifierEquals(FnvHash.Constants.ANTI)) {
                lexer.nextToken();
                joinType = SQLJoinTableSource.JoinType.LEFT_ANTI_JOIN;
            } else if (lexer.token == Token.OUTER) {
                lexer.nextToken();
                joinType = SQLJoinTableSource.JoinType.LEFT_OUTER_JOIN;
            } else {
                joinType = SQLJoinTableSource.JoinType.LEFT_OUTER_JOIN;
            }

            accept(Token.JOIN);

        } else if (lexer.token == Token.RIGHT) {
            lexer.nextToken();
            if (lexer.token == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.RIGHT_OUTER_JOIN;
        } else if (lexer.token == Token.FULL) {
            lexer.nextToken();
            if (lexer.token == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.FULL_OUTER_JOIN;
        } else if (lexer.token == Token.INNER) {
            lexer.nextToken();
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.INNER_JOIN;
        } else if (lexer.token == Token.JOIN) {
            lexer.nextToken();
            joinType = SQLJoinTableSource.JoinType.JOIN;
        } else if (lexer.token == Token.COMMA) {
            lexer.nextToken();
            joinType = SQLJoinTableSource.JoinType.COMMA;
        } else if (lexer.identifierEquals(FnvHash.Constants.STRAIGHT_JOIN)) {
            lexer.nextToken();
            joinType = SQLJoinTableSource.JoinType.STRAIGHT_JOIN;
        } else if (lexer.identifierEquals(FnvHash.Constants.CROSS)) {
            lexer.nextToken();
            if (lexer.token == Token.JOIN) {
                lexer.nextToken();
                joinType = SQLJoinTableSource.JoinType.CROSS_JOIN;
            } else if (lexer.identifierEquals(FnvHash.Constants.APPLY)) {
                lexer.nextToken();
                joinType = SQLJoinTableSource.JoinType.CROSS_APPLY;
            }
        } else if (lexer.token == Token.OUTER) {
            lexer.nextToken();
            if (lexer.identifierEquals(FnvHash.Constants.APPLY)) {
                lexer.nextToken();
                joinType = SQLJoinTableSource.JoinType.OUTER_APPLY;
            }
        }

        if (joinType != null) {
            SQLJoinTableSource join = new SQLJoinTableSource();
            join.setLeft(tableSource);
            join.setJoinType(joinType);


            SQLTableSource rightTableSource;
            if (lexer.token == Token.LPAREN) {
                lexer.nextToken();
                if (lexer.token == Token.SELECT) {
                    SQLSelect select = this.select();
                    rightTableSource = new SQLSubqueryTableSource(select);
                } else  {
                    rightTableSource = this.parseTableSource();
                }
                accept(Token.RPAREN);
            } else {
                SQLExpr expr = this.expr();
                rightTableSource = new SQLExprTableSource(expr);
                primaryTableSourceRest(rightTableSource);
            }

            if (lexer.token == Token.USING
                ||lexer.identifierEquals(FnvHash.Constants.USING))
            {
                Lexer.SavePoint savePoint = lexer.mark();
                lexer.nextToken();

                if (lexer.token == Token.LPAREN) {
                    lexer.nextToken();
                    join.setRight(rightTableSource);
                    this.exprParser.exprList(join.getUsing(), join);
                    accept(Token.RPAREN);
                } else if (lexer.token == Token.IDENTIFIER) {
                    lexer.reset(savePoint);
                    join.setRight(rightTableSource);
                    return join;
                } else {
                    join.setAlias(this.tableAlias());
                }
            } else {
                rightTableSource.setAlias(this.tableAlias());

                primaryTableSourceRest(rightTableSource);
            }

            if (lexer.token == Token.WITH) {
                lexer.nextToken();
                accept(Token.LPAREN);

                for (;;) {
                    SQLExpr hintExpr = this.expr();
                    SQLExprHint hint = new SQLExprHint(hintExpr);
                    hint.setParent(tableSource);
                    rightTableSource.getHints().add(hint);
                    if (lexer.token == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    } else {
                        break;
                    }
                }

                accept(Token.RPAREN);
            }

            join.setRight(rightTableSource);

            if (!natural) {
                if (tableSource.aliasHashCode64() == FnvHash.Constants.NATURAL && JdbcConstants.MYSQL.equals(dbType)) {
                    tableSource.setAlias(null);
                    natural = true;
                }
            }
            join.setNatural(natural);

            if (lexer.token == Token.ON) {
                lexer.nextToken();
                join.setCondition(expr());
            } else if (lexer.token == Token.USING
                    || lexer.identifierEquals(FnvHash.Constants.USING)) {
                Lexer.SavePoint savePoint = lexer.mark();
                lexer.nextToken();
                if (lexer.token == Token.LPAREN) {
                    lexer.nextToken();
                    this.exprParser.exprList(join.getUsing(), join);
                    accept(Token.RPAREN);
                } else {
                    lexer.reset(savePoint);
                }
            }

            return parseTableSourceRest(join);
        }

        if (tableSource.aliasHashCode64() == FnvHash.Constants.LATERAL
                && lexer.token() == Token.VIEW) {
            return parseLateralView(tableSource);
        }

        if (lexer.identifierEquals(FnvHash.Constants.LATERAL)) {
            lexer.nextToken();
            return parseLateralView(tableSource);
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
        if (lexer.token == Token.IDENTIFIER && ident.equalsIgnoreCase(lexer.stringVal())) {
            lexer.nextToken();
        } else {
            setErrorEndPos(lexer.pos());
            throw new ParserException("syntax error, expect " + ident + ", actual " + lexer.token + ", " + lexer.info());
        }
    }

    public void parseFetchClause(SQLSelectQueryBlock queryBlock) {
        if (lexer.token == Token.LIMIT) {
            SQLLimit limit = this.exprParser.parseLimit();
            queryBlock.setLimit(limit);
            return;
        }

        if (lexer.identifierEquals(FnvHash.Constants.OFFSET) || lexer.token == Token.OFFSET) {
            lexer.nextToken();
            SQLExpr offset = this.exprParser.primary();
            queryBlock.setOffset(offset);
            if (lexer.identifierEquals(FnvHash.Constants.ROW) || lexer.identifierEquals(FnvHash.Constants.ROWS)) {
                lexer.nextToken();
            }
        }

        if (lexer.token == Token.FETCH) {
            lexer.nextToken();
            if (lexer.token == Token.FIRST
                    || lexer.token == Token.NEXT
                    || lexer.identifierEquals(FnvHash.Constants.NEXT)) {
                lexer.nextToken();
            } else {
                acceptIdentifier("FIRST");
            }
            SQLExpr first = this.exprParser.primary();
            queryBlock.setFirst(first);
            if (lexer.identifierEquals(FnvHash.Constants.ROW) || lexer.identifierEquals(FnvHash.Constants.ROWS)) {
                lexer.nextToken();
            }

            if (lexer.token == Token.ONLY) {
                lexer.nextToken();
            } else {
                acceptIdentifier("ONLY");
            }
        }
    }

    protected void parseHierachical(SQLSelectQueryBlock queryBlock) {
        if (lexer.token == Token.CONNECT || lexer.identifierEquals(FnvHash.Constants.CONNECT)) {
            lexer.nextToken();
            accept(Token.BY);

            if (lexer.token == Token.PRIOR || lexer.identifierEquals(FnvHash.Constants.PRIOR)) {
                lexer.nextToken();
                queryBlock.setPrior(true);
            }

            if (lexer.identifierEquals(FnvHash.Constants.NOCYCLE)) {
                queryBlock.setNoCycle(true);
                lexer.nextToken();

                if (lexer.token == Token.PRIOR) {
                    lexer.nextToken();
                    queryBlock.setPrior(true);
                }
            }
            queryBlock.setConnectBy(this.exprParser.expr());
        }

        if (lexer.token == Token.START || lexer.identifierEquals(FnvHash.Constants.START)) {
            lexer.nextToken();
            accept(Token.WITH);

            queryBlock.setStartWith(this.exprParser.expr());
        }

        if (lexer.token == Token.CONNECT || lexer.identifierEquals(FnvHash.Constants.CONNECT)) {
            lexer.nextToken();
            accept(Token.BY);

            if (lexer.token == Token.PRIOR || lexer.identifierEquals(FnvHash.Constants.PRIOR)) {
                lexer.nextToken();
                queryBlock.setPrior(true);
            }

            if (lexer.identifierEquals(FnvHash.Constants.NOCYCLE)) {
                queryBlock.setNoCycle(true);
                lexer.nextToken();

                if (lexer.token == Token.PRIOR || lexer.identifierEquals(FnvHash.Constants.PRIOR)) {
                    lexer.nextToken();
                    queryBlock.setPrior(true);
                }
            }
            queryBlock.setConnectBy(this.exprParser.expr());
        }
    }

    protected SQLTableSource parseLateralView(SQLTableSource tableSource) {
        accept(Token.VIEW);
        if ("LATERAL".equalsIgnoreCase(tableSource.getAlias())) {
            tableSource.setAlias(null);
        }
        SQLLateralViewTableSource lateralViewTabSrc = new SQLLateralViewTableSource();
        lateralViewTabSrc.setTableSource(tableSource);

        SQLMethodInvokeExpr udtf = (SQLMethodInvokeExpr) this.exprParser.expr();
        lateralViewTabSrc.setMethod(udtf);

        String alias = as();
        lateralViewTabSrc.setAlias(alias);

        accept(Token.AS);

        this.exprParser.names(lateralViewTabSrc.getColumns());

        return parseTableSourceRest(lateralViewTabSrc);
    }
}
