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
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.hive.parser.HiveCreateTableParser;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOrderingExpr;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.StringUtils;

import java.util.List;

public class SQLSelectParser extends SQLParser {
    protected SQLExprParser exprParser;
    protected SQLSelectListCache selectListCache;

    public SQLSelectParser(String sql) {
        super(sql);
    }

    public SQLSelectParser(Lexer lexer) {
        super(lexer);
    }

    public SQLSelectParser(SQLExprParser exprParser) {
        this(exprParser, null);
    }

    public SQLSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
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
        return new SQLUnionQuery(dbType);
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
                Lexer.SavePoint uninMark = lexer.mark();
                lexer.nextToken();

                switch (lexer.token) {
                    case GROUP:
                    case ORDER:
                    case WHERE:
                    case RPAREN:
                        lexer.reset(uninMark);
                        return selectQuery;
                    default:
                        break;
                }

                if (lexer.token == Token.SEMI && dbType == DbType.odps) {
                    break;
                }

                SQLUnionQuery union = createSQLUnionQuery();
                union.setLeft(selectQuery);

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

                    if (lexer.token == Token.UNION && dbType == DbType.odps) {
                        continue; // skip
                    }

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
                    r.setParenthesized(paren);
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

    private void setToLeft(SQLSelectQuery selectQuery,
                           SQLUnionQuery parentUnion,
                           SQLUnionQuery union,
                           SQLSelectQuery right) {
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

    protected SQLSelectQueryBlock createSelectQueryBlock() {
        return new SQLSelectQueryBlock(dbType);
    }

    protected void querySelectListBefore(SQLSelectQueryBlock x) {
    }

    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        if (lexer.token == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);
            select.setParenthesized(true);

            return queryRest(select, acceptUnion);
        }

        if (lexer.token() == Token.VALUES) {
            return valuesQuery(acceptUnion);
        }

        SQLSelectQueryBlock queryBlock = createSelectQueryBlock();

        if (lexer.hasComment() && lexer.isKeepComments()) {
            queryBlock.addBeforeComment(lexer.readAndResetComments());
        }

        accept(Token.SELECT);

        querySelectListBefore(queryBlock);

        if (lexer.token() == Token.HINT) {
            this.exprParser.parseHints(queryBlock.getHints());
        }

        if (lexer.token == Token.COMMENT) {
            lexer.nextToken();
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

        for (; ; ) {
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

            for (; ; ) {
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

        for (; ; ) {
            SQLWithSubqueryClause.Entry entry = new SQLWithSubqueryClause.Entry();
            entry.setParent(withQueryClause);

            String alias = this.lexer.stringVal();
            lexer.nextToken();
            if (lexer.nextIf(Token.LPAREN)) {
                exprParser.names(entry.getColumns(), entry);
                accept(Token.RPAREN);
            }
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
                case LPAREN:
                case WITH:
                case FROM:
                case VALUES:
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
            long hash_lower = lexer.hashLCase();
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
            } else if (lexer.identifierEquals("COLLATE")) {
                acceptIdentifier("COLLATE");
                String collateValue = lexer.stringVal();
                if (lexer.token == Token.IDENTIFIER || lexer.token == Token.LITERAL_ALIAS || lexer.token == Token.LITERAL_CHARS) {
                    identExpr = new SQLIdentifierExpr(ident);
                    ((SQLIdentifierExpr) identExpr).setCollate(collateValue);
                    lexer.nextToken();
                } else {
                    throw new ParserException("syntax error. " + lexer.info());
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

            for (; ; ) {
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

            for (; ; ) {
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

            for (; ; ) {
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

        for (; ; ) {
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

    public void parseGroupBy(SQLSelectQueryBlock queryBlock) {
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
                    if (dbType == DbType.odps || dbType == DbType.hive || dbType == DbType.spark) {
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

            for (; ; ) {
                List<String> comments = null;
                if (lexer.hasComment()) {
                    comments = lexer.readAndResetComments();
                }
                SQLExpr item = parseGroupByItem();
                if (comments != null) {
                    item.addBeforeComment(comments);
                }

                item.setParent(groupBy);
                groupBy.addItem(item);

                if (lexer.token == Token.COMMA) {
                    int line = lexer.line;
                    lexer.nextToken();

                    if (lexer.hasComment()
                            && lexer.isKeepComments()
                            && lexer.getComments().size() == 1
                            && lexer.getComments().get(0).startsWith("--")
                            && lexer.line == line + 1) {
                        item.addAfterComment(lexer.readAndResetComments());
                    }
                    continue;
                } else if (lexer.identifierEquals(FnvHash.Constants.GROUPING)) {
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
                } else if (lexer.identifierEquals(FnvHash.Constants.ROLLUP)) {
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

                for (; ; ) {
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

            if (DbType.mysql == dbType
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
        SQLExpr item;
        if (lexer.identifierEquals(FnvHash.Constants.ROLLUP)) {
            SQLMethodInvokeExpr rollup = new SQLMethodInvokeExpr(lexer.stringVal());
            lexer.nextToken();
            if (lexer.token == Token.LPAREN) {
                lexer.nextToken();
                for (; ; ) {
                    if (lexer.token == Token.RPAREN) {
                        break;
                    }

                    SQLExpr expr;
                    if (lexer.token == Token.LPAREN) {
                        accept(Token.LPAREN);
                        SQLListExpr list = new SQLListExpr();
                        if (lexer.token == Token.COMMA) {
                            lexer.nextToken();
                        }
                        this.exprParser.exprList(list.getItems(), list);
                        accept(Token.RPAREN);
                        expr = list;
                    } else {
                        expr = this.exprParser.expr();
                    }
                    rollup.addArgument(expr);

                    if (lexer.token == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            }
            item = rollup;
        } else {
            item = this.exprParser.expr();
        }

        if (DbType.mysql == dbType) {
            if (lexer.token == Token.DESC) {
                lexer.nextToken(); // skip
                item = new MySqlOrderingExpr(item, SQLOrderingSpecification.DESC);
            } else if (lexer.token == Token.ASC) {
                lexer.nextToken(); // skip
                item = new MySqlOrderingExpr(item, SQLOrderingSpecification.ASC);
            }
        }

        if (lexer.token == Token.HINT) {
            SQLCommentHint hint = this.exprParser.parseHint(); // skip
            if (item instanceof SQLObjectImpl) {
                ((SQLExprImpl) item).setHint(hint);
            }
        }

        return item;
    }

    protected void parseSelectList(SQLSelectQueryBlock queryBlock) {
        final List<SQLSelectItem> selectList = queryBlock.getSelectList();
        for (; ; ) {
            final SQLSelectItem selectItem = this.exprParser.parseSelectItem();
            selectList.add(selectItem);
            selectItem.setParent(queryBlock);

            //https://github.com/alibaba/druid/issues/5708
            if (lexer.hasComment()
                    && lexer.isKeepComments()
                    && lexer.getComments().size() == 1
                    && lexer.getComments().get(0).startsWith("--")) {
                selectItem.addAfterComment(lexer.readAndResetComments());
            }

            if (lexer.token != Token.COMMA) {
                break;
            }

            int line = lexer.line;
            lexer.nextToken();
            if (lexer.hasComment()
                    && lexer.isKeepComments()
                    && lexer.getComments().size() == 1
                    && lexer.getComments().get(0).startsWith("--")
                    && lexer.line == line + 1) {
                selectItem.addAfterComment(lexer.readAndResetComments());
            }

            // https://github.com/alibaba/druid/issues/5140
            if (lexer.token == Token.FROM) {
                throw new ParserException("syntax error, expect is not TOKEN:from " + lexer.info());
            }
        }
    }

    public void parseFrom(SQLSelectQueryBlock queryBlock) {
        if (lexer.token != Token.FROM) {
            return;
        }

        lexer.nextToken();
        if (lexer.hasComment()) {
            queryBlock.setCommentsAfaterFrom(lexer.readAndResetComments());
        }
        queryBlock.setFrom(
                parseTableSource(true));

    }

    public SQLTableSource parseTableSource() {
        return parseTableSource(false);
    }
    public SQLTableSource parseTableSource(boolean forFrom) {
        if (lexer.token == Token.LPAREN) {
            lexer.nextToken();
            SQLTableSource tableSource;
            if (lexer.token == Token.SELECT || lexer.token == Token.WITH
                    || lexer.token == Token.SEL) {
                SQLSelect select = select();
                accept(Token.RPAREN);
                SQLSelectQuery selectQuery = select.getQuery();
                selectQuery.setParenthesized(true);

                boolean acceptUnion = !(selectQuery instanceof SQLUnionQuery) && dbType != DbType.odps;
                SQLSelectQuery query = queryRest(selectQuery, acceptUnion);
                if (query instanceof SQLUnionQuery) {
                    tableSource = new SQLUnionQueryTableSource((SQLUnionQuery) query);
                } else {
                    tableSource = SQLSubqueryTableSource.fixParenthesized(new SQLSubqueryTableSource(select));
                }
            } else if (lexer.token == Token.LPAREN) {
                tableSource = parseTableSource();

                while ((lexer.token == Token.UNION
                        || lexer.token == Token.EXCEPT
                        || lexer.token == Token.INTERSECT
                        || lexer.token == Token.MINUS)) {
                    if (tableSource instanceof SQLUnionQueryTableSource) {
                        SQLUnionQueryTableSource unionQueryTableSource = (SQLUnionQueryTableSource) tableSource;
                        SQLUnionQuery union = unionQueryTableSource.getUnion();
                        unionQueryTableSource.setUnion(
                                (SQLUnionQuery) queryRest(union)
                        );
                    } else if (tableSource instanceof SQLSubqueryTableSource) {
                        SQLSelect select = ((SQLSubqueryTableSource) tableSource).getSelect();
                        if (select != null) {
                            SQLSelectQuery query = select.getQuery();
                            SQLSelectQuery queryRest = queryRest(query, true);
                            select.setQuery(queryRest);
                        }
                    } else {
                        break;
                    }
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
                        && ((SQLValuesTableSource) tableSource).getColumns().isEmpty()) {
                    SQLValuesTableSource values = (SQLValuesTableSource) tableSource;
                    accept(Token.LPAREN);
                    this.exprParser.names(values.getColumns(), values);
                    accept(Token.RPAREN);
                } else if (tableSource instanceof SQLSubqueryTableSource) {
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
            SQLValuesTableSource tableSource = new SQLValuesTableSource();
            lexer.computeRowAndColumn(tableSource);
            lexer.nextToken();

            for (; ; ) {
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

            if (lexer.token == Token.RPAREN || lexer.token == Token.SEMI || lexer.token == Token.EOF) {
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

        SQLExpr expr;
        switch (lexer.token) {
            case ALL:
            case SET:
                expr = this.exprParser.name();
                break;
            default:
                expr = expr();
                break;
        }

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
                            || lexer.identifierEquals(FnvHash.Constants.ARRAY)
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
                    if (identifierEquals("ARRAY")) {
                        Lexer.SavePoint mark = lexer.mark();
                        String strVal = lexer.stringVal();
                        lexer.nextToken();
                        if (lexer.token == Token.JOIN) {
                            lexer.reset(mark);
                        } else {
                            tableSource.setAlias(strVal);
                        }
                        break;
                    }
                    if (identifierEquals("PIVOT") || identifierEquals("UNPIVOT")) {
                        parsePivot(tableSource);
                    } else if (!(token == Token.IDENTIFIER
                            && ((hash = lexer.hashLCase()) == FnvHash.Constants.STRAIGHT_JOIN
                            || hash == FnvHash.Constants.CROSS))) {
                        boolean must = false;
                        if (lexer.token == Token.AS) {
                            lexer.nextToken();
                            must = true;
                        }
                        String alias = tableAlias(must);
                        if (alias != null) {
                            if (isEnabled(SQLParserFeature.IgnoreNameQuotes) && alias.length() > 1) {
                                alias = StringUtils.removeNameQuotes(alias);
                            }
                            tableSource.setAlias(alias);

                            if (lexer.token == Token.HINT) {
                                tableSource.addAfterComment("/*" + lexer.stringVal + "*/");
                                lexer.nextToken();
                            }

                            if ((tableSource instanceof SQLValuesTableSource)
                                    && ((SQLValuesTableSource) tableSource).getColumns().isEmpty()) {
                                SQLValuesTableSource values = (SQLValuesTableSource) tableSource;
                                accept(Token.LPAREN);
                                this.exprParser.names(values.getColumns(), values);
                                accept(Token.RPAREN);
                            } else if (tableSource instanceof SQLSubqueryTableSource) {
                                SQLSubqueryTableSource subQuery = (SQLSubqueryTableSource) tableSource;
                                if (lexer.token == Token.LPAREN) {
                                    lexer.nextToken();
                                    this.exprParser.names(subQuery.getColumns(), subQuery);
                                    accept(Token.RPAREN);
                                }
                            } else if (tableSource instanceof SQLUnionQueryTableSource) {
                                SQLUnionQueryTableSource union = (SQLUnionQueryTableSource) tableSource;
                                if (lexer.token == Token.LPAREN) {
                                    lexer.nextToken();
                                    this.exprParser.names(union.getColumns(), union);
                                    accept(Token.RPAREN);
                                }
                            } else if (lexer.token == Token.LPAREN
                                    && tableSource instanceof SQLExprTableSource
                                    && (((SQLExprTableSource) tableSource).getExpr() instanceof SQLVariantRefExpr
                                    || ((SQLExprTableSource) tableSource).getExpr() instanceof SQLIdentifierExpr
                            )
                            ) {
                                lexer.nextToken();
                                SQLExprTableSource exprTableSource = (SQLExprTableSource) tableSource;
                                this.exprParser.names(exprTableSource.getColumns(), exprTableSource);
                                accept(Token.RPAREN);
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

        boolean global = false;
        if (dbType == DbType.clickhouse) {
            if (lexer.token == Token.GLOBAL) {
                lexer.nextToken();
                global = true;
            }
        }
        if (identifierEquals("ARRAY")) {
            lexer.nextToken();
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.ARRAY_JOIN;
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
                } else if (lexer.identifierEquals(FnvHash.Constants.ARRAY)) {
                    lexer.nextToken();
                    joinType = SQLJoinTableSource.JoinType.LEFT_ARRAY_JOIN;
                } else if (lexer.token == Token.OUTER) {
                    lexer.nextToken();
                    joinType = natural ? SQLJoinTableSource.JoinType.NATURAL_LEFT_JOIN : SQLJoinTableSource.JoinType.LEFT_OUTER_JOIN;
                } else {
                    joinType = natural ? SQLJoinTableSource.JoinType.NATURAL_LEFT_JOIN : SQLJoinTableSource.JoinType.LEFT_OUTER_JOIN;
                }

                if (dbType == DbType.odps && lexer.token == Token.IDENTIFIER && lexer.stringVal().startsWith("join@")) {
                    lexer.stringVal = lexer.stringVal().substring(5);
                    break;
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
                final long hash = lexer.hashLCase;
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
            join.setGlobal(global);
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
                        || (lexer.token == Token.WITH && dbType == DbType.mysql)
                        || (lexer.token == Token.FROM && (dbType == DbType.odps || dbType == DbType.hive))) {
                    SQLSelect select = this.select();
                    rightTableSource = SQLSubqueryTableSource.fixParenthesized(new SQLSubqueryTableSource(select));
                } else {
                    rightTableSource = this.parseTableSource();
                    if (rightTableSource instanceof SQLExprTableSource) {
                        SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) rightTableSource;
                        if (sqlExprTableSource.getExpr() instanceof SQLQueryExpr) {
                            SQLQueryExpr expr = (SQLQueryExpr) sqlExprTableSource.getExpr();
                            expr.setParenthesized(true);
                        }
                    }
                }
                if (lexer.token == Token.UNION
                        || lexer.token == Token.EXCEPT
                        || lexer.token == Token.MINUS
                        || lexer.token == Token.INTERSECT) {
                    if (rightTableSource instanceof SQLSubqueryTableSource) {
                        SQLSelect select = ((SQLSubqueryTableSource) rightTableSource).getSelect();
                        SQLSelectQuery query = queryRest(select.getQuery(), true);
                        select.setQuery(query);
                    } else if (rightTableSource instanceof SQLUnionQueryTableSource) {
                        SQLUnionQueryTableSource unionTableSrc = (SQLUnionQueryTableSource) rightTableSource;
                        unionTableSrc.setUnion((SQLUnionQuery)
                                queryRest(
                                        unionTableSrc.getUnion()
                                )
                        );
                    }
                }

                accept(Token.RPAREN);

                if (rightTableSource instanceof SQLValuesTableSource
                        && (lexer.token == Token.AS || lexer.token == Token.IDENTIFIER)
                        && rightTableSource.getAlias() == null
                        && ((SQLValuesTableSource) rightTableSource).getColumns().isEmpty()
                ) {
                    if (lexer.token == Token.AS) {
                        lexer.nextToken();
                    }
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

                    if (lexer.token() == Token.LPAREN) {
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

                        if (lexer.identifierEquals(FnvHash.Constants.CROSS)) {
                            rightTableSource = unnest;
                        } else {
                            rightTableSource = parseTableSourceRest(unnest);
                        }
                    } else {
                        lexer.reset(mark);
                    }
                } else if (lexer.token == Token.VALUES) {
                    rightTableSource = this.parseValues();
                }

                if (rightTableSource == null) {
                    boolean aliasToken = lexer.token == Token.LITERAL_ALIAS;
                    SQLExpr expr;
                    switch (lexer.token) {
                        case ALL:
                            expr = this.exprParser.name();
                            break;
                        default:
                            expr = this.expr();
                            break;
                    }

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
                    || lexer.identifierEquals(FnvHash.Constants.USING)) {
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
                int line = lexer.line;
                String tableAlias;
                if (lexer.token == Token.AS) {
                    lexer.nextToken();

                    if (lexer.token != Token.ON) {
                        if (dbType == DbType.clickhouse && rightTableSource instanceof SQLExprTableSource) {
                            SQLExprTableSource exprTableSource = (SQLExprTableSource) rightTableSource;
                            exprTableSource.setNeedAsTokenForAlias(true);
                        }
                        tableAlias = this.tableAlias(true);
                    } else {
                        tableAlias = null;
                    }
                } else {
                    tableAlias = this.tableAlias(false);
                }

                if (tableAlias != null) {
                    rightTableSource.setAlias(tableAlias);

                    if (line + 1 == lexer.line
                            && lexer.hasComment()
                            && lexer.getComments().get(0).startsWith("--")) {
                        rightTableSource.addAfterComment(lexer.readAndResetComments());
                    }

                    if (lexer.token == Token.LPAREN) {
                        if (rightTableSource instanceof SQLSubqueryTableSource) {
                            lexer.nextToken();
                            List<SQLName> columns = ((SQLSubqueryTableSource) rightTableSource).getColumns();
                            this.exprParser.names(columns, rightTableSource);
                            accept(Token.RPAREN);
                        } else if (rightTableSource instanceof SQLExprTableSource
                                && ((SQLExprTableSource) rightTableSource).getExpr() instanceof SQLMethodInvokeExpr) {
                            List<SQLName> columns = ((SQLExprTableSource) rightTableSource).getColumns();
                            if (columns.isEmpty()) {
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

                for (; ; ) {
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
                    if (natural && join.getJoinType() == SQLJoinTableSource.JoinType.LEFT_OUTER_JOIN) {
                        join.setJoinType(SQLJoinTableSource.JoinType.NATURAL_LEFT_JOIN);
                    }
                    if (natural && join.getJoinType() == JoinType.RIGHT_OUTER_JOIN) {
                        join.setJoinType(SQLJoinTableSource.JoinType.NATURAL_RIGHT_JOIN);
                    }
                    if (natural && join.getJoinType() == JoinType.INNER_JOIN) {
                        join.setJoinType(SQLJoinTableSource.JoinType.NATURAL_INNER_JOIN);
                    }
                }
            }
            join.setNatural(natural);

            if (lexer.token == Token.ON) {
                lexer.nextToken();
                SQLExpr joinOn = expr();
                join.setCondition(joinOn);

                while (lexer.token == Token.ON
                        && dbType == DbType.mysql) {
                    lexer.nextToken();

                    SQLExpr joinOn2 = expr();
                    join.addCondition(joinOn2);
                }

                if (dbType == DbType.odps && lexer.identifierEquals(FnvHash.Constants.USING)) {
                    SQLJoinTableSource.UDJ udj = new SQLJoinTableSource.UDJ();
                    lexer.nextToken();
                    udj.setFunction(this.exprParser.name());
                    accept(Token.LPAREN);
                    this.exprParser.exprList(udj.getArguments(), udj);
                    accept(Token.RPAREN);

                    if (lexer.token != Token.AS) {
                        udj.setAlias(alias());
                    }

                    accept(Token.AS);
                    accept(Token.LPAREN);
                    this.exprParser.names(udj.getColumns(), udj);
                    accept(Token.RPAREN);

                    if (lexer.identifierEquals(FnvHash.Constants.SORT)) {
                        lexer.nextToken();
                        accept(Token.BY);
                        this.exprParser.orderBy(udj.getSortBy(), udj);
                    }

                    if (lexer.token == Token.WITH) {
                        lexer.nextToken();
                        acceptIdentifier("UDFPROPERTIES");
                        this.exprParser.parseAssignItem(udj.getProperties(), udj);
                    }

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

        SQLMethodInvokeExpr udtf = (SQLMethodInvokeExpr) this.exprParser.primary();
        lateralViewTabSrc.setMethod(udtf);

        String alias = null;
        if (lexer.token != Token.AS) {
            alias = alias();
        }
        if (alias != null) {
            lateralViewTabSrc.setAlias(alias);
        }

        if (lexer.token == Token.AS) {
            parseLateralViewAs(lateralViewTabSrc);
        }

        if (lexer.token == Token.ON) {
            lexer.nextToken();
            lateralViewTabSrc.setOn(
                    this.exprParser.expr()
            );
        }

        return parseTableSourceRest(lateralViewTabSrc);
    }

    public void parseLateralViewAs(SQLLateralViewTableSource lateralViewTabSrc) {
        accept(Token.AS);

        Lexer.SavePoint mark = null;
        for (; ; ) {
            SQLName name;
            if (lexer.token == Token.NULL) {
                name = new SQLIdentifierExpr(lexer.stringVal());
                lexer.nextToken();
            } else {
                name = this.exprParser.name();
                if (name instanceof SQLPropertyExpr) {
                    lexer.reset(mark);
                    break;
                }
            }
            name.setParent(lateralViewTabSrc);
            lateralViewTabSrc.getColumns().add(name);
            if (lexer.token == Token.COMMA) {
                mark = lexer.mark();
                lexer.nextToken();
                continue;
            }
            break;
        }
    }

    public SQLValuesTableSource parseValues() {
        SQLValuesTableSource tableSource = new SQLValuesTableSource();
        lexer.computeRowAndColumn(tableSource);
        accept(Token.VALUES);

        for (; ; ) {
            // compatible (VALUES 1,2,3) and (VALUES (1), (2), (3)) for ads
            boolean isSingleValue = true;
            if (lexer.token == Token.ROW) {
                lexer.nextToken();
            }
            if (lexer.token() == Token.LPAREN) {
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

    protected void parsePivot(SQLTableSource tableSource) {
        SQLSelectItem item;
        if (lexer.identifierEquals(FnvHash.Constants.PIVOT)) {
            lexer.nextToken();

            SQLPivot pivot = new SQLPivot();

            if (lexer.identifierEquals("XML")) {
                lexer.nextToken();
                pivot.setXml(true);
            }

            accept(Token.LPAREN);
            while (true) {
                item = new SQLSelectItem();
                item.setExpr((SQLAggregateExpr) this.exprParser.expr());
                item.setAlias(as());
                pivot.addItem(item);

                if (!(lexer.token() == (Token.COMMA))) {
                    break;
                }
                lexer.nextToken();
            }

            accept(Token.FOR);

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                while (true) {
                    pivot.getPivotFor().add(new SQLIdentifierExpr(lexer.stringVal()));
                    lexer.nextToken();

                    if (!(lexer.token() == (Token.COMMA))) {
                        break;
                    }
                    lexer.nextToken();
                }

                accept(Token.RPAREN);
            } else {
                pivot.getPivotFor().add(new SQLIdentifierExpr(lexer.stringVal()));
                lexer.nextToken();
            }

            accept(Token.IN);
            accept(Token.LPAREN);
//            if (lexer.token() == (Token.LPAREN)) {
//                throw new ParserException("TODO. " + lexer.info());
//            }

            if (lexer.token() == (Token.SELECT)) {
                SQLExpr expr = this.exprParser.expr();
                item = new SQLSelectItem();
                item.setExpr(expr);
                item.setParent(pivot);
                pivot.getPivotIn().add(item);
            } else {
                for (; ; ) {
                    item = new SQLSelectItem();
                    item.setExpr(this.exprParser.expr());
                    item.setAlias(as());
                    item.setParent(pivot);
                    pivot.getPivotIn().add(item);

                    if (lexer.token() != Token.COMMA) {
                        break;
                    }

                    lexer.nextToken();
                }
            }

            accept(Token.RPAREN);

            accept(Token.RPAREN);

            tableSource.setPivot(pivot);
        } else if (lexer.identifierEquals("UNPIVOT")) {
            lexer.nextToken();

            SQLUnpivot unPivot = new SQLUnpivot();
            if (lexer.identifierEquals("INCLUDE")) {
                lexer.nextToken();
                acceptIdentifier("NULLS");
                unPivot.setNullsIncludeType(SQLUnpivot.NullsIncludeType.INCLUDE_NULLS);
            } else if (lexer.identifierEquals("EXCLUDE")) {
                lexer.nextToken();
                acceptIdentifier("NULLS");
                unPivot.setNullsIncludeType(SQLUnpivot.NullsIncludeType.EXCLUDE_NULLS);
            }

            accept(Token.LPAREN);

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                this.exprParser.exprList(unPivot.getItems(), unPivot);
                accept(Token.RPAREN);
            } else {
                unPivot.addItem(this.exprParser.expr());
            }

            accept(Token.FOR);

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                while (true) {
                    unPivot.getPivotFor().add(new SQLIdentifierExpr(lexer.stringVal()));
                    lexer.nextToken();

                    if (!(lexer.token() == (Token.COMMA))) {
                        break;
                    }
                    lexer.nextToken();
                }

                accept(Token.RPAREN);
            } else {
                unPivot.getPivotFor().add(new SQLIdentifierExpr(lexer.stringVal()));
                lexer.nextToken();
            }

            accept(Token.IN);
            accept(Token.LPAREN);
            if (lexer.token() == (Token.LPAREN)) {
                throw new ParserException("TODO. " + lexer.info());
            }

            if (lexer.token() == (Token.SELECT)) {
                throw new ParserException("TODO. " + lexer.info());
            }

            for (; ; ) {
                item = new SQLSelectItem();
                item.setExpr(this.exprParser.expr());
                item.setAlias(as());
                unPivot.getPivotIn().add(item);

                if (lexer.token() != Token.COMMA) {
                    break;
                }

                lexer.nextToken();
            }

            accept(Token.RPAREN);

            accept(Token.RPAREN);

            tableSource.setUnpivot(unPivot);
        }
    }
}
