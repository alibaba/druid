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
package com.alibaba.druid.sql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.hive.parser.HiveCreateTableParser;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOrderingExpr;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.StringUtils;

import java.util.List;

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

        SQLSelectQuery query = query(select, true);
        select.setQuery(query);

        SQLOrderBy orderBy = this.parseOrderBy();

        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;

            if (queryBlock.getOrderBy() == null) {
                queryBlock.setOrderBy(orderBy);
                if (lexer.token == Token.LIMIT) {
                    SQLLimit limit = this.exprParser.parseLimit();
                    queryBlock.setLimit(limit);
                }
            } else {
                select.setOrderBy(orderBy);
                if (lexer.token == Token.LIMIT) {
                    SQLLimit limit = this.exprParser.parseLimit();
                    select.setLimit(limit);
                }
            }

            if (orderBy != null) {
                parseFetchClause(queryBlock);
            }
        } else {
            select.setOrderBy(orderBy);
        }

        if (lexer.token == Token.LIMIT) {
            SQLLimit limit = this.exprParser.parseLimit();
            select.setLimit(limit);
        }

        while (lexer.token == Token.HINT) {
            this.exprParser.parseHints(select.getHints());
        }

        return select;
    }

    protected SQLUnionQuery createSQLUnionQuery() {
        SQLUnionQuery union = new SQLUnionQuery();
        union.setDbType(dbType);
        return union;
    }

    public SQLUnionQuery unionRest(SQLUnionQuery union) {
        if (lexer.token == Token.ORDER) {
            SQLOrderBy orderBy = this.exprParser.parseOrderBy();
            union.setOrderBy(orderBy);
            return unionRest(union);
        }

        if (lexer.token == Token.LIMIT) {
            SQLLimit limit = this.exprParser.parseLimit();
            union.setLimit(limit);
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
                if (union.getRelations().isEmpty()) {
                    union.setLeft(selectQuery);
                }

                if (lexer.token == Token.ALL) {
                    union.setOperator(SQLUnionOperator.UNION_ALL);
                    lexer.nextToken();
                } else if (lexer.token == Token.DISTINCT) {
                    union.setOperator(SQLUnionOperator.DISTINCT);
                    lexer.nextToken();
                }

                boolean paren = lexer.token == Token.LPAREN;
                SQLSelectQuery right = this.query(paren ? null : union, false);
                union.setRight(right);

                while (lexer.isEnabled(SQLParserFeature.EnableMultiUnion)
                        && lexer.token == Token.UNION
                ) {
                    Lexer.SavePoint mark = lexer.mark();
                    lexer.nextToken();

                    if (lexer.token == Token.ALL) {
                        if (union.getOperator() == SQLUnionOperator.UNION_ALL) {
                            lexer.nextToken();
                        } else {
                            lexer.reset(mark);
                            break;
                        }
                    } else if (lexer.token == Token.DISTINCT) {
                        if (union.getOperator() == SQLUnionOperator.DISTINCT) {
                            lexer.nextToken();
                        } else {
                            lexer.reset(mark);
                            break;
                        }
                    } else if (union.getOperator() == SQLUnionOperator.UNION) {
                        // skip
                    } else {
                        lexer.reset(mark);
                        break;
                    }

                    paren = lexer.token == Token.LPAREN;
                    SQLSelectQuery r = this.query(paren ? null : union, false);
                    union.addRelation(r);
                    right = r;
                }

                if (!paren) {
                    if (right instanceof SQLSelectQueryBlock) {
                        SQLSelectQueryBlock rightQuery = (SQLSelectQueryBlock) right;
                        SQLOrderBy orderBy = rightQuery.getOrderBy();
                        if (orderBy != null) {
                            union.setOrderBy(orderBy);
                            rightQuery.setOrderBy(null);
                        }

                        SQLLimit limit = rightQuery.getLimit();
                        if (limit != null) {
                            union.setLimit(limit);
                            rightQuery.setLimit(null);
                        }
                    } else if (right instanceof SQLUnionQuery) {
                        SQLUnionQuery rightUnion = (SQLUnionQuery) right;
                        final SQLOrderBy orderBy = rightUnion.getOrderBy();
                        if (orderBy != null) {
                            union.setOrderBy(orderBy);
                            rightUnion.setOrderBy(null);
                        }

                        SQLLimit limit = rightUnion.getLimit();
                        if (limit != null) {
                            union.setLimit(limit);
                            rightUnion.setLimit(null);
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

            if (lexer.token == Token.ALL) {
                lexer.nextToken();
                union.setOperator(SQLUnionOperator.EXCEPT_ALL);
            } else if (lexer.token == Token.DISTINCT) {
                lexer.nextToken();
                union.setOperator(SQLUnionOperator.EXCEPT_DISTINCT);
            } else {
                union.setOperator(SQLUnionOperator.EXCEPT);
            }

            boolean paren = lexer.token == Token.LPAREN;

            SQLSelectQuery right = this.query(union, false);
            union.setRight(right);

            if (!paren) {
                if (right instanceof SQLSelectQueryBlock) {
                    SQLSelectQueryBlock rightQuery = (SQLSelectQueryBlock) right;
                    SQLOrderBy orderBy = rightQuery.getOrderBy();
                    if (orderBy != null) {
                        union.setOrderBy(orderBy);
                        rightQuery.setOrderBy(null);
                    }

                    SQLLimit limit = rightQuery.getLimit();
                    if (limit != null) {
                        union.setLimit(limit);
                        rightQuery.setLimit(null);
                    }
                } else if (right instanceof SQLUnionQuery) {
                    SQLUnionQuery rightUnion = (SQLUnionQuery) right;
                    final SQLOrderBy orderBy = rightUnion.getOrderBy();
                    if (orderBy != null) {
                        union.setOrderBy(orderBy);
                        rightUnion.setOrderBy(null);
                    }

                    SQLLimit limit = rightUnion.getLimit();
                    if (limit != null) {
                        union.setLimit(limit);
                        rightUnion.setLimit(null);
                    }
                }
            }

            return queryRest(union, true);
        }

        if (lexer.token == Token.INTERSECT) {
            lexer.nextToken();

            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            if (lexer.token() == Token.DISTINCT) {
                lexer.nextToken();
                union.setOperator(SQLUnionOperator.INTERSECT_DISTINCT);
            } else if (lexer.token == Token.ALL) {
                lexer.nextToken();
                union.setOperator(SQLUnionOperator.INTERSECT_ALL);
            } else {
                union.setOperator(SQLUnionOperator.INTERSECT);
            }

            boolean paren = lexer.token == Token.LPAREN;
            SQLSelectQuery right = this.query(union, false);
            union.setRight(right);
            if (!paren) {
                if (right instanceof SQLSelectQueryBlock) {
                    SQLSelectQueryBlock rightQuery = (SQLSelectQueryBlock) right;
                    SQLOrderBy orderBy = rightQuery.getOrderBy();
                    if (orderBy != null) {
                        union.setOrderBy(orderBy);
                        rightQuery.setOrderBy(null);
                    }

                    SQLLimit limit = rightQuery.getLimit();
                    if (limit != null) {
                        union.setLimit(limit);
                        rightQuery.setLimit(null);
                    }
                } else if (right instanceof SQLUnionQuery) {
                    SQLUnionQuery rightUnion = (SQLUnionQuery) right;
                    final SQLOrderBy orderBy = rightUnion.getOrderBy();
                    if (orderBy != null) {
                        union.setOrderBy(orderBy);
                        rightUnion.setOrderBy(null);
                    }

                    SQLLimit limit = rightUnion.getLimit();
                    if (limit != null) {
                        union.setLimit(limit);
                        rightUnion.setLimit(null);
                    }
                }
            }

            return queryRest(union, true);
        }

        if (acceptUnion && lexer.token == Token.MINUS) {
            lexer.nextToken();

            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            union.setOperator(SQLUnionOperator.MINUS);
            if (lexer.token == Token.DISTINCT) {
                union.setOperator(SQLUnionOperator.MINUS_DISTINCT);
                lexer.nextToken();
            } else if (lexer.token == Token.ALL) {
                union.setOperator(SQLUnionOperator.MINUS_ALL);
                lexer.nextToken();
            }

            SQLSelectQuery right = this.query(union, false);
            union.setRight(right);

            return queryRest(union, true);
        }

        return selectQuery;
    }

    private void setToLeft(SQLSelectQuery selectQuery, SQLUnionQuery parentUnion, SQLUnionQuery union, SQLSelectQuery right) {
        SQLUnionOperator operator = union.getOperator();

        if (union.getLeft() instanceof SQLUnionQuery) {
            SQLUnionQuery left = (SQLUnionQuery) union.getLeft();
            while (left.getLeft() instanceof SQLUnionQuery) {
                left = (SQLUnionQuery) left.getLeft();
            }

            left.setLeft(new SQLUnionQuery(parentUnion.getLeft(), parentUnion.getOperator(), left.getLeft()));

            parentUnion.setLeft(union.getLeft());
            parentUnion.setRight(union.getRight());
        } else {
            parentUnion.setRight(right);
            union.setLeft(parentUnion.getLeft());
            parentUnion.setLeft(union);
            union.setRight(selectQuery);
            union.setOperator(parentUnion.getOperator());
            parentUnion.setOperator(operator);
        }

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
            accept(Token.RPAREN);

            return queryRest(select, acceptUnion);
        }

        if (lexer.token() == Token.VALUES) {
            return valuesQuery(acceptUnion);
        }
        
        SQLSelectQueryBlock queryBlock = new SQLSelectQueryBlock(dbType);
        
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

        if (DbType.informix == dbType) {
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

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();

            SQLExpr expr = expr();
            if (lexer.token() != Token.COMMA) {
                queryBlock.setInto(expr);
            }
        }

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseGroupBy(queryBlock);

        if (lexer.identifierEquals(FnvHash.Constants.WINDOW)) {
            parseWindow(queryBlock);
        }

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

    protected SQLSelectQuery valuesQuery(boolean acceptUnion) {
        lexer.nextToken();
        SQLValuesQuery valuesQuery = new SQLValuesQuery();

        for (;;) {
            if (lexer.token == Token.LPAREN) {
                lexer.nextToken();
                SQLListExpr listExpr = new SQLListExpr();
                this.exprParser.exprList(listExpr.getItems(), listExpr);
                accept(Token.RPAREN);
                valuesQuery.addValue(listExpr);
            } else {
                this.exprParser.exprList(valuesQuery.getValues(), valuesQuery);
            }

            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                continue;
            } else {
                break;
            }
        }

        return queryRest(valuesQuery, acceptUnion);
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
        SQLWithSubqueryClause withQueryClause = new SQLWithSubqueryClause();
        if (lexer.hasComment() && lexer.isKeepComments()) {
            withQueryClause.addBeforeComment(lexer.readAndResetComments());
        }

        accept(Token.WITH);

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
                case WITH:
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
        if (lexer.token != Token.WHERE) {
            return;
        }

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
            while (lexer.token == Token.HINT) {
                lexer.nextToken();
            }

            where = this.exprParser.expr();

            while (lexer.token == Token.HINT) {
                lexer.nextToken();
            }
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

    protected void parseSortBy(SQLSelectQueryBlock queryBlock) {
        if (lexer.token() == Token.ORDER) {
            SQLOrderBy orderBy = parseOrderBy();
            queryBlock.setOrderBy(orderBy);
        }

        if (lexer.identifierEquals(FnvHash.Constants.DISTRIBUTE)) {
            lexer.nextToken();
            accept(Token.BY);

            for (;;) {
                SQLSelectOrderByItem distributeByItem = this.exprParser.parseSelectOrderByItem();
                queryBlock.addDistributeBy(distributeByItem);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                } else {
                    break;
                }
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.SORT)) {
            lexer.nextToken();
            accept(Token.BY);

            for (;;) {
                SQLSelectOrderByItem sortByItem = this.exprParser.parseSelectOrderByItem();
                queryBlock.addSortBy(sortByItem);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                } else {
                    break;
                }
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.CLUSTER)) {
            lexer.nextToken();
            accept(Token.BY);

            for (;;) {
                SQLSelectOrderByItem clusterByItem = this.exprParser.parseSelectOrderByItem();
                queryBlock.addClusterBy(clusterByItem);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                } else {
                    break;
                }
            }
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
        if (lexer.token == Token.GROUP) {
            lexer.nextTokenBy();
            accept(Token.BY);

            SQLSelectGroupByClause groupBy = new SQLSelectGroupByClause();

            if (lexer.token == Token.HINT) {
                groupBy.setHint(this.exprParser.parseHint());
            }

            if (lexer.token == Token.ALL) {
                Lexer.SavePoint mark = lexer.mark();
                lexer.nextToken();
                if (!lexer.identifierEquals(FnvHash.Constants.GROUPING)) {
                    if (dbType == DbType.odps) {
                        lexer.reset(mark);
                    } else {
                        throw new ParserException("group by all syntax error. " + lexer.info());
                    }
                }
            } else if (lexer.token == Token.DISTINCT) {
                lexer.nextToken();
                groupBy.setDistinct(true);
            }

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

                if (lexer.token == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else if(lexer.identifierEquals(FnvHash.Constants.GROUPING)) {
                    continue;
                } else {
                    break;
                }
            }
            if (groupBy.isWithRollUp() || groupBy.isWithCube()) {
                accept(Token.RPAREN);
                groupBy.setParen(true);

                if (lexer.token == Token.COMMA && dbType == DbType.odps) {
                    lexer.nextToken();
                    SQLMethodInvokeExpr func = new SQLMethodInvokeExpr(groupBy.isWithCube() ? "CUBE" : "ROLLUP");
                    func.getArguments().addAll(groupBy.getItems());
                    groupBy.getItems().clear();
                    groupBy.setWithCube(false);
                    groupBy.setWithRollUp(false);
                    for (SQLExpr arg : func.getArguments()) {
                        arg.setParent(func);
                    }
                    groupBy.addItem(func);
                    this.exprParser.exprList(groupBy.getItems(), groupBy);
                }
            }

            if (lexer.token == (Token.HAVING)) {
                lexer.nextToken();

                SQLExpr having = this.exprParser.expr();
                groupBy.setHaving(having);
            }
            
            if (lexer.token == Token.WITH) {
                Lexer.SavePoint mark = lexer.mark();
                lexer.nextToken();
                
                if (lexer.identifierEquals(FnvHash.Constants.CUBE)) {
                    lexer.nextToken();
                    groupBy.setWithCube(true);
                } else if(lexer.identifierEquals(FnvHash.Constants.ROLLUP)) {
                    lexer.nextToken();
                    groupBy.setWithRollUp(true);
                } else if (lexer.identifierEquals(FnvHash.Constants.RS)
                        && DbType.db2 == dbType) {
                    lexer.nextToken();
                    ((DB2SelectQueryBlock) queryBlock).setIsolation(DB2SelectQueryBlock.Isolation.RS);
                } else if (lexer.identifierEquals(FnvHash.Constants.RR)
                        && DbType.db2 == dbType) {
                    lexer.nextToken();
                    ((DB2SelectQueryBlock) queryBlock).setIsolation(DB2SelectQueryBlock.Isolation.RR);
                } else if (lexer.identifierEquals(FnvHash.Constants.CS)
                        && DbType.db2 == dbType) {
                    lexer.nextToken();
                    ((DB2SelectQueryBlock) queryBlock).setIsolation(DB2SelectQueryBlock.Isolation.CS);
                } else if (lexer.identifierEquals(FnvHash.Constants.UR)
                        && DbType.db2 == dbType) {
                    lexer.nextToken();
                    ((DB2SelectQueryBlock) queryBlock).setIsolation(DB2SelectQueryBlock.Isolation.UR);
                } else {
                    lexer.reset(mark);
                }
            }

            if (groupBy.getHaving() == null && lexer.token == Token.HAVING) {
                lexer.nextToken();

                SQLExpr having = this.exprParser.expr();
                groupBy.setHaving(having);
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
            
            if(DbType.mysql == dbType
                    && lexer.token == Token.DESC) {
                lexer.nextToken(); // skip
            }

            queryBlock.setGroupBy(groupBy);
        }
    }

    protected SQLExpr parseGroupByItem() {
        if (lexer.token == Token.LPAREN) {
            Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();

            if (lexer.token == Token.RPAREN) {
                lexer.nextToken();
                return new SQLListExpr();
            }

            lexer.reset(mark);
        }
        SQLExpr item = this.exprParser.expr();
        
        if(DbType.mysql == dbType) {
            if (lexer.token == Token.DESC) {
                lexer.nextToken(); // skip
                item =new MySqlOrderingExpr(item, SQLOrderingSpecification.DESC);
            } else if (lexer.token == Token.ASC) {
                lexer.nextToken(); // skip
                item =new MySqlOrderingExpr(item, SQLOrderingSpecification.ASC);
            }
        }

        if (lexer.token == Token.HINT) {
            SQLCommentHint hint = this.exprParser.parseHint();// skip
            if (item instanceof SQLObjectImpl) {
                ((SQLExprImpl) item).setHint(hint);
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
                if (select.getQuery() instanceof SQLSelectQueryBlock) {
                    ((SQLSelectQueryBlock) select.getQuery()).setParenthesized(true);
                }

                SQLSelectQuery query = queryRest(select.getQuery(), true);
                if (query instanceof SQLUnionQuery) {
                    tableSource = new SQLUnionQueryTableSource((SQLUnionQuery) query);
                } else {
                    tableSource = new SQLSubqueryTableSource(select);
                }
            } else if (lexer.token == Token.LPAREN) {
                tableSource = parseTableSource();

                while ((lexer.token == Token.UNION || lexer.token == Token.EXCEPT || lexer.token == Token.INTERSECT)
                        && tableSource instanceof SQLUnionQueryTableSource) {
                    SQLUnionQueryTableSource unionQueryTableSource = (SQLUnionQueryTableSource) tableSource;
                    SQLUnionQuery union = unionQueryTableSource.getUnion();
                    unionQueryTableSource.setUnion(
                            (SQLUnionQuery) queryRest(union)
                    );
                }
                accept(Token.RPAREN);
            } else {
                tableSource = parseTableSource();
                accept(Token.RPAREN);
            }

            if (lexer.token == Token.AS) {
                lexer.nextToken();
                String alias = this.tableAlias(true);
                tableSource.setAlias(alias);

                if (tableSource instanceof SQLValuesTableSource
                        && ((SQLValuesTableSource) tableSource).getColumns().size() == 0) {
                    SQLValuesTableSource values = (SQLValuesTableSource) tableSource;
                    accept(Token.LPAREN);
                    this.exprParser.names(values.getColumns(), values);
                    accept(Token.RPAREN);
                } else  if (tableSource instanceof SQLSubqueryTableSource) {
                    SQLSubqueryTableSource values = (SQLSubqueryTableSource) tableSource;
                    if (lexer.token == Token.LPAREN) {
                        lexer.nextToken();
                        this.exprParser.names(values.getColumns(), values);
                        accept(Token.RPAREN);
                    }
                }
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

            return parseTableSourceRest(tableSource);
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
        if (lexer.token == Token.LITERAL_ALIAS || lexer.identifierEquals(FnvHash.Constants.IDENTIFIED)
            || lexer.token == Token.LITERAL_CHARS) {
            tableReference.setExpr(this.exprParser.name());
            return;
        }

        if (lexer.token == Token.HINT) {
            SQLCommentHint hint = this.exprParser.parseHint();
            tableReference.setHint(hint);
        }

        SQLExpr expr = expr();

        if (expr instanceof SQLBinaryOpExpr) {
            throw new ParserException("Invalid from clause : " + expr.toString().replace("\n", " "));
        }

        tableReference.setExpr(expr);
    }

    protected SQLTableSource primaryTableSourceRest(SQLTableSource tableSource) {
        return tableSource;
    }

    public void parseTableSourceSample(SQLTableSource tableSource) {

    }

    public void parseTableSourceSampleHive(SQLTableSource tableSource) {
        if (lexer.identifierEquals(FnvHash.Constants.TABLESAMPLE) && tableSource instanceof SQLExprTableSource) {
            Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                SQLTableSampling sampling = new SQLTableSampling();

                if (lexer.identifierEquals(FnvHash.Constants.BUCKET)) {
                    lexer.nextToken();
                    SQLExpr bucket = this.exprParser.primary();
                    sampling.setBucket(bucket);

                    if (lexer.token() == Token.OUT) {
                        lexer.nextToken();
                        accept(Token.OF);
                        SQLExpr outOf = this.exprParser.primary();
                        sampling.setOutOf(outOf);
                    }

                    if (lexer.token() == Token.ON) {
                        lexer.nextToken();
                        SQLExpr on = this.exprParser.expr();
                        sampling.setOn(on);
                    }
                }

                if (lexer.token() == Token.LITERAL_INT || lexer.token() == Token.LITERAL_FLOAT) {
                    SQLExpr val = this.exprParser.primary();

                    if (lexer.identifierEquals(FnvHash.Constants.ROWS)) {
                        lexer.nextToken();
                        sampling.setRows(val);
                    } else {
                        acceptIdentifier("PERCENT");
                        sampling.setPercent(val);
                    }
                }

                if (lexer.token() == Token.IDENTIFIER) {
                    String strVal = lexer.stringVal();
                    char first = strVal.charAt(0);
                    char last = strVal.charAt(strVal.length() - 1);
                    if (last >= 'a' && last <= 'z') {
                        last -= 32; // to upper
                    }

                    boolean match = false;
                    if ((first == '.' || (first >= '0' && first <= '9'))) {
                        switch (last) {
                            case 'B':
                            case 'K':
                            case 'M':
                            case 'G':
                            case 'T':
                            case 'P':
                                match = true;
                                break;
                            default:
                                break;
                        }
                    }
                    SQLSizeExpr size = new SQLSizeExpr(strVal.substring(0, strVal.length() - 2), last);
                    sampling.setByteLength(size);
                    lexer.nextToken();
                }

                final SQLExprTableSource table = (SQLExprTableSource) tableSource;
                table.setSampling(sampling);

                accept(Token.RPAREN);
            } else {
                lexer.reset(mark);
            }
        }
    }

    public SQLTableSource parseTableSourceRest(SQLTableSource tableSource) {
        parseTableSourceSample(tableSource);

        if (lexer.hasComment()
                && lexer.isKeepComments()
                && !(tableSource instanceof SQLSubqueryTableSource)) {
            tableSource.addAfterComment(lexer.readAndResetComments());
        }

        if (tableSource.getAlias() == null || tableSource.getAlias().length() == 0) {
            Token token = lexer.token;
            long hash;

            switch (token) {
                case LEFT:
                case RIGHT:
                case FULL: {
                    Lexer.SavePoint mark = lexer.mark();
                    String strVal = lexer.stringVal();
                    lexer.nextToken();
                    if (lexer.token == Token.OUTER
                            || lexer.token == Token.JOIN
                            || lexer.identifierEquals(FnvHash.Constants.ANTI)
                            || lexer.identifierEquals(FnvHash.Constants.SEMI)) {
                        lexer.reset(mark);
                    } else {
                        tableSource.setAlias(strVal);
                    }
                }
                    break;
                case OUTER:
                    break;
                default:
                    if (!(token == Token.IDENTIFIER
                            && ((hash = lexer.hash_lower()) == FnvHash.Constants.STRAIGHT_JOIN
                            || hash == FnvHash.Constants.CROSS)))
                    {
                        String alias = tableAlias(false);
                        if (alias != null) {
                            if (isEnabled(SQLParserFeature.IgnoreNameQuotes) && alias.length() > 1) {
                                alias = StringUtils.removeNameQuotes(alias);
                            }
                            tableSource.setAlias(alias);

                            if (tableSource instanceof SQLValuesTableSource
                                    && ((SQLValuesTableSource) tableSource).getColumns().size() == 0) {
                                SQLValuesTableSource values = (SQLValuesTableSource) tableSource;
                                accept(Token.LPAREN);
                                this.exprParser.names(values.getColumns(), values);
                                accept(Token.RPAREN);
                            } else  if (tableSource instanceof SQLSubqueryTableSource) {
                                SQLSubqueryTableSource values = (SQLSubqueryTableSource) tableSource;
                                if (lexer.token == Token.LPAREN) {
                                    lexer.nextToken();
                                    this.exprParser.names(values.getColumns(), values);
                                    accept(Token.RPAREN);
                                }
                            }

                            if (lexer.token == Token.WHERE) {
                                return tableSource;
                            }

                            return parseTableSourceRest(tableSource);
                        }
                    }
                    break;
            }

        }

        SQLJoinTableSource.JoinType joinType = null;

        boolean natural = lexer.identifierEquals(FnvHash.Constants.NATURAL);
        if (natural) {
            lexer.nextToken();
        }

        boolean asof = false;
        if (lexer.identifierEquals(FnvHash.Constants.ASOF) && dbType == DbType.clickhouse) {
            lexer.nextToken();
            asof = true;
        }

        if (lexer.token == Token.OUTER) {
            Lexer.SavePoint mark = lexer.mark();
            String str = lexer.stringVal();
            lexer.nextToken();
            if (tableSource.getAlias() == null &&
                    !lexer.identifierEquals(FnvHash.Constants.APPLY)) {
                tableSource.setAlias(str);
            } else {
                lexer.reset(mark);
            }
        }

        switch (lexer.token) {
            case LEFT:
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
                break;
            case RIGHT:
                lexer.nextToken();
                if (lexer.token == Token.OUTER) {
                    lexer.nextToken();
                }
                accept(Token.JOIN);
                joinType = SQLJoinTableSource.JoinType.RIGHT_OUTER_JOIN;
                break;
            case FULL:
                lexer.nextToken();
                if (lexer.token == Token.OUTER) {
                    lexer.nextToken();
                }
                accept(Token.JOIN);
                joinType = SQLJoinTableSource.JoinType.FULL_OUTER_JOIN;
                break;
            case INNER:
                lexer.nextToken();
                accept(Token.JOIN);
                joinType = SQLJoinTableSource.JoinType.INNER_JOIN;
                break;
            case JOIN:
                lexer.nextToken();
                joinType = natural ? SQLJoinTableSource.JoinType.NATURAL_JOIN : SQLJoinTableSource.JoinType.JOIN;
                break;
            case COMMA:
                lexer.nextToken();
                joinType = SQLJoinTableSource.JoinType.COMMA;
                break;
            case OUTER:
                lexer.nextToken();
                if (lexer.identifierEquals(FnvHash.Constants.APPLY)) {
                    lexer.nextToken();
                    joinType = SQLJoinTableSource.JoinType.OUTER_APPLY;
                }
                break;
            case STRAIGHT_JOIN:
            case IDENTIFIER:
                final long hash = lexer.hash_lower;
                if (hash == FnvHash.Constants.STRAIGHT_JOIN) {
                    lexer.nextToken();
                    joinType = SQLJoinTableSource.JoinType.STRAIGHT_JOIN;
                } else if (hash == FnvHash.Constants.STRAIGHT) {
                    lexer.nextToken();
                    accept(Token.JOIN);
                    joinType = SQLJoinTableSource.JoinType.STRAIGHT_JOIN;
                } else if (hash == FnvHash.Constants.CROSS) {
                    lexer.nextToken();
                    if (lexer.token == Token.JOIN) {
                        lexer.nextToken();
                        joinType = natural ? SQLJoinTableSource.JoinType.NATURAL_CROSS_JOIN : SQLJoinTableSource.JoinType.CROSS_JOIN;
                    } else if (lexer.identifierEquals(FnvHash.Constants.APPLY)) {
                        lexer.nextToken();
                        joinType = SQLJoinTableSource.JoinType.CROSS_APPLY;
                    }
                }
                break;
            default:
                break;
        }

        if (joinType != null) {
            SQLJoinTableSource join = new SQLJoinTableSource();
            join.setLeft(tableSource);
            join.setJoinType(joinType);
            if (asof) {
                join.setAsof(true);
            }

            boolean isBrace = false;
            if (SQLJoinTableSource.JoinType.COMMA == joinType) {
                if (lexer.token == Token.LBRACE) {
                    lexer.nextToken();
                    acceptIdentifier("OJ");
                    isBrace = true;
                }
            }

            SQLTableSource rightTableSource = null;
            if (lexer.token == Token.LPAREN) {
                lexer.nextToken();
                if (lexer.token == Token.SELECT
                        || (lexer.token == Token.FROM && (dbType == DbType.odps || dbType == DbType.hive))) {
                    SQLSelect select = this.select();
                    rightTableSource = new SQLSubqueryTableSource(select);
                } else {
                    rightTableSource = this.parseTableSource();
                }

                if (lexer.token == Token.UNION && rightTableSource instanceof SQLSubqueryTableSource) {
                    SQLSelect select = ((SQLSubqueryTableSource) rightTableSource).getSelect();
                    SQLSelectQuery query = queryRest(select.getQuery(), true);
                    select.setQuery(query);
                }

                accept(Token.RPAREN);

                if (rightTableSource instanceof SQLValuesTableSource
                        && (lexer.token == Token.AS || lexer.token == Token.IDENTIFIER)
                        && rightTableSource.getAlias() == null
                        && ((SQLValuesTableSource) rightTableSource).getColumns().size() == 0
                ) {
                    rightTableSource.setAlias(tableAlias(true));

                    if (lexer.token == Token.LPAREN) {
                        lexer.nextToken();
                        this.exprParser.names(((SQLValuesTableSource) rightTableSource).getColumns(), rightTableSource);
                        accept(Token.RPAREN);
                    }
                }
            } else if (lexer.token() == Token.TABLE) {
                HiveCreateTableParser createTableParser = new HiveCreateTableParser(lexer);
                HiveCreateTableStatement stmt = (HiveCreateTableStatement) createTableParser
                        .parseCreateTable(false);
                rightTableSource = new SQLAdhocTableSource(stmt);
                primaryTableSourceRest(rightTableSource);
            } else {
                if (lexer.identifierEquals(FnvHash.Constants.UNNEST)) {
                    Lexer.SavePoint mark = lexer.mark();
                    lexer.nextToken();

                    if (lexer.token() == Token.LPAREN){
                        lexer.nextToken();
                        SQLUnnestTableSource unnest = new SQLUnnestTableSource();
                        this.exprParser.exprList(unnest.getItems(), unnest);
                        accept(Token.RPAREN);

                        if (lexer.token() == Token.WITH) {
                            lexer.nextToken();
                            acceptIdentifier("ORDINALITY");
                            unnest.setOrdinality(true);
                        }

                        String alias = this.tableAlias();
                        unnest.setAlias(alias);

                        if (lexer.token() == Token.LPAREN) {
                            lexer.nextToken();
                            this.exprParser.names(unnest.getColumns(), unnest);
                            accept(Token.RPAREN);
                        }

                        SQLTableSource tableSrc = parseTableSourceRest(unnest);
                        rightTableSource = tableSrc;
                    } else {
                        lexer.reset(mark);
                    }
                } else if (lexer.token == Token.VALUES) {
                    rightTableSource = this.parseValues();
                }

                if (rightTableSource == null) {
                    boolean aliasToken = lexer.token == Token.LITERAL_ALIAS;
                    SQLExpr expr = this.expr();
                    if (aliasToken && expr instanceof SQLCharExpr) {
                        expr = new SQLIdentifierExpr(((SQLCharExpr) expr).getText());
                    }
                    SQLExprTableSource exprTableSource = new SQLExprTableSource(expr);

                    if (expr instanceof SQLMethodInvokeExpr && lexer.token == Token.AS) {
                        lexer.nextToken();
                        String alias = this.tableAlias(true);
                        exprTableSource.setAlias(alias);

                        if (lexer.token == Token.LPAREN) {
                            lexer.nextToken();

                            this.exprParser.names(exprTableSource.getColumns(), exprTableSource);
                            accept(Token.RPAREN);
                        }
                    }

                    rightTableSource = exprTableSource;
                }
                rightTableSource = primaryTableSourceRest(rightTableSource);
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
            } else if (lexer.token == Token.STRAIGHT_JOIN || lexer.identifierEquals(FnvHash.Constants.STRAIGHT_JOIN)) {
                primaryTableSourceRest(rightTableSource);

            } else if (rightTableSource.getAlias() == null && !(rightTableSource instanceof SQLValuesTableSource)) {
                String tableAlias = this.tableAlias(false);
                if (tableAlias != null) {
                    rightTableSource.setAlias(tableAlias);

                    if (lexer.token == Token.LPAREN) {
                        if (rightTableSource instanceof SQLSubqueryTableSource) {
                            lexer.nextToken();
                            List<SQLName> columns = ((SQLSubqueryTableSource) rightTableSource).getColumns();
                            this.exprParser.names(columns, rightTableSource);
                            accept(Token.RPAREN);
                        } else if (rightTableSource instanceof SQLExprTableSource
                                && ((SQLExprTableSource) rightTableSource).getExpr() instanceof SQLMethodInvokeExpr) {
                            List<SQLName> columns = ((SQLExprTableSource) rightTableSource).getColumns();
                            if (columns.size() == 0) {
                                lexer.nextToken();
                                this.exprParser.names(columns, rightTableSource);
                                accept(Token.RPAREN);
                            }
                        }
                    }
                }

                rightTableSource = primaryTableSourceRest(rightTableSource);
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
                if (!StringUtils.isEmpty(tableSource.getAlias())
                        && tableSource.aliasHashCode64() == FnvHash.Constants.NATURAL && DbType.mysql == dbType) {
                    tableSource.setAlias(null);
                    natural = true;
                }
            }
            join.setNatural(natural);

            if (lexer.token == Token.ON) {
                lexer.nextToken();
                SQLExpr joinOn = expr();
                join.setCondition(joinOn);

                while (lexer.token == Token.ON) {
                    lexer.nextToken();

                    SQLExpr joinOn2 = expr();
                    join.addCondition(joinOn2);
                }

                if (dbType == DbType.odps && lexer.identifierEquals(FnvHash.Constants.USING)) {
                    SQLJoinTableSource.UDJ udj = new SQLJoinTableSource.UDJ();
                    lexer.nextToken();
                    udj.setFunction(lexer.stringVal());
                    accept(Token.IDENTIFIER);
                    accept(Token.LPAREN);
                    this.exprParser.exprList(udj.getArguments(), udj);
                    accept(Token.RPAREN);

                    udj.setAlias(alias());

                    accept(Token.AS);
                    accept(Token.LPAREN);
                    this.exprParser.names(udj.getColumns(), udj);
                    accept(Token.RPAREN);

                    join.setUdj(udj);
                }
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

            SQLTableSource tableSourceReturn = parseTableSourceRest(join);

            if (isBrace) {
                accept(Token.RBRACE);
            }

            return parseTableSourceRest(tableSourceReturn);
        }

        if ((tableSource.aliasHashCode64() == FnvHash.Constants.LATERAL || lexer.token == Token.LATERAL)
                && lexer.token() == Token.VIEW) {
            return parseLateralView(tableSource);
        }

        if (lexer.identifierEquals(FnvHash.Constants.LATERAL) || lexer.token == Token.LATERAL) {
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
            SQLExpr offset = this.exprParser.expr();
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
        if (tableSource != null && "LATERAL".equalsIgnoreCase(tableSource.getAlias())) {
            tableSource.setAlias(null);
        }
        SQLLateralViewTableSource lateralViewTabSrc = new SQLLateralViewTableSource();
        lateralViewTabSrc.setTableSource(tableSource);

        if (lexer.token == Token.OUTER) {
            lateralViewTabSrc.setOuter(true);
            lexer.nextToken();
        }

        SQLMethodInvokeExpr udtf = (SQLMethodInvokeExpr) this.exprParser.expr();
        lateralViewTabSrc.setMethod(udtf);

        String alias = as();
        lateralViewTabSrc.setAlias(alias);

        if (lexer.token == Token.AS) {
            lexer.nextToken();
            this.exprParser.names(lateralViewTabSrc.getColumns());
        }

        return parseTableSourceRest(lateralViewTabSrc);
    }

    public SQLValuesTableSource parseValues() {
        accept(Token.VALUES);
        SQLValuesTableSource tableSource = new SQLValuesTableSource();

        for (;;) {

            // compatible (VALUES 1,2,3) and (VALUES (1), (2), (3)) for ads
            boolean isSingleValue = true;
            if (lexer.token == Token.ROW) {
                lexer.nextToken();
            }
            if(lexer.token() == Token.LPAREN) {
                accept(Token.LPAREN);
                isSingleValue = false;
            }

            SQLListExpr listExpr = new SQLListExpr();

            if (isSingleValue) {
                SQLExpr expr = expr();
                expr.setParent(listExpr);
                listExpr.getItems().add(expr);
            } else {
                this.exprParser.exprList(listExpr.getItems(), listExpr);
                accept(Token.RPAREN);
            }

            listExpr.setParent(tableSource);

            tableSource.getValues().add(listExpr);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }

        String alias = this.tableAlias();
        if (alias != null) {
            tableSource.setAlias(alias);
        }

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            this.exprParser.names(tableSource.getColumns(), tableSource);
            accept(Token.RPAREN);
        }


        return tableSource;
    }
}
