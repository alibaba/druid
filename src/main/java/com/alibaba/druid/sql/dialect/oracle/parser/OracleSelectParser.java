/*
 * Copyright 2011 Alibaba Group. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAggregateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelect;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectForUpdate;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectHierachicalQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectJoin;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;

public class OracleSelectParser extends SQLSelectParser {

    public OracleSelectParser(String sql){
        super(sql);
    }

    public OracleSelectParser(Lexer lexer){
        super(lexer);
    }

    public OracleSelect select() {
        OracleSelect select = new OracleSelect();

        select.setQuery(query());
        select.setOrderBy(this.parseOrderBy());

        if (lexer.token() == (Token.FOR)) {
            lexer.nextToken();
            accept(Token.UPDATE);

            OracleSelectForUpdate forUpdate = new OracleSelectForUpdate();

            if (identifierEquals("OF")) {
                lexer.nextToken();
                this.createExprParser().exprList(forUpdate.getOf());

                if (identifierEquals("WAIT")) throw new ParserException("TODO");
                if (identifierEquals("NOWAIT")) throw new ParserException("TODO");
                if (identifierEquals("SKIP")) {
                    throw new ParserException("TODO");
                }
            }

            select.setForUpdate(forUpdate);
        }

        if (select.getOrderBy() == null) {
            select.setOrderBy(this.createExprParser().parseOrderBy());
        }

        if (identifierEquals("WITH")) {
            lexer.nextToken();

            if (identifierEquals("READ")) {
                lexer.nextToken();

                if (identifierEquals("ONLY")) lexer.nextToken();
                else {
                    throw new ParserException("syntax error");
                }

                select.setRestriction(new OracleSelectRestriction.ReadOnly());
            } else if (lexer.token() == (Token.CHECK)) {
                lexer.nextToken();

                if (identifierEquals("OPTION")) lexer.nextToken();
                else {
                    throw new ParserException("syntax error");
                }

                OracleSelectRestriction.CheckOption checkOption = new OracleSelectRestriction.CheckOption();

                if (lexer.token() == Token.CONSTRAINT) {
                    lexer.nextToken();
                    throw new ParserException("TODO");
                }

                select.setRestriction(checkOption);
            } else {
                throw new ParserException("syntax error");
            }
        }

        return select;
    }

    protected SQLSelectQuery query() {
        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select);
        }

        accept(Token.SELECT);

        OracleSelectQueryBlock queryBlock = new OracleSelectQueryBlock();
        parseHints(queryBlock);

        if (lexer.token() == (Token.DISTINCT)) queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
        else if (lexer.token() == (Token.UNIQUE)) queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
        else if (lexer.token() == (Token.ALL)) {
            queryBlock.setDistionOption(SQLSetQuantifier.ALL);
        }

        parseSelectList(queryBlock);

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseHierachical(queryBlock);

        parseGroupBy(queryBlock);

        return queryRest(queryBlock);
    }

    public SQLSelectQuery queryRest(SQLSelectQuery selectQuery) {
        if (lexer.token() == (Token.UNION)) {
            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            lexer.nextToken();

            if (lexer.token() == (Token.ALL)) {
                union.setAll(true);
                lexer.nextToken();
            }

            SQLSelectQuery right = query();

            union.setRight(right);

            return queryRest(union);
        }

        if (lexer.token() == (Token.INTERSECT)) {
            throw new ParserException("TODO");
        }

        if (lexer.token() == (Token.MINUS)) {
            throw new ParserException("TODO");
        }

        return selectQuery;
    }

    private void parseGroupBy(OracleSelectQueryBlock queryBlock) {
        if (lexer.token() == (Token.GROUP)) {
            lexer.nextToken();
            accept(Token.BY);

            SQLSelectGroupByClause groupBy = new SQLSelectGroupByClause();
            while (true) {
                groupBy.getItems().add(this.createExprParser().expr());
                if (!(lexer.token() == (Token.COMMA))) break;
                lexer.nextToken();
            }

            if (lexer.token() == (Token.HAVING)) {
                lexer.nextToken();

                groupBy.setHaving(this.createExprParser().expr());
            }

            queryBlock.setGroupBy(groupBy);
        }
    }

    private void parseHierachical(OracleSelectQueryBlock queryBlock) {
        OracleSelectHierachicalQueryClause hierachical = null;

        if (identifierEquals("CONNECT")) {
            hierachical = new OracleSelectHierachicalQueryClause();
            lexer.nextToken();
            accept(Token.BY);

            if (identifierEquals("NOCYCLE")) {
                hierachical.setNoCycle(true);
            }
            hierachical.setConnectBy(this.createExprParser().expr());
        }

        if (identifierEquals("START")) {
            lexer.nextToken();
            if (hierachical == null) {
                hierachical = new OracleSelectHierachicalQueryClause();
            }
            acceptIdentifier("WITH");

            hierachical.setStartWith(this.createExprParser().expr());
        }

        if (identifierEquals("CONNECT")) {
            if (hierachical == null) {
                hierachical = new OracleSelectHierachicalQueryClause();
            }

            lexer.nextToken();
            accept(Token.BY);

            if (identifierEquals("NOCYCLE")) {
                hierachical.setNoCycle(true);
            }
            hierachical.setConnectBy(this.createExprParser().expr());
        }

        if (hierachical != null) {
            queryBlock.setHierachicalQueryClause(hierachical);
        }
    }

    @Override
    public SQLTableSource parseTableSource() {
        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            OracleSelectSubqueryTableSource tableSource;
            if (lexer.token() == (Token.SELECT)) tableSource = new OracleSelectSubqueryTableSource(select());
            else {
                throw new ParserException("TODO");
            }
            accept(Token.RPAREN);

            parsePivot(tableSource);

            return parseTableSourceRest(tableSource);
        }

        if (lexer.token() == (Token.SELECT)) {
            throw new ParserException("TODO");
        }

        OracleSelectTableReference tableReference = new OracleSelectTableReference();

        if (identifierEquals("ONLY")) {
            lexer.nextToken();
            accept(Token.LPAREN);
            parseTableSourceQueryTableExpr(tableReference);
            accept(Token.RPAREN);
        } else {
            parseTableSourceQueryTableExpr(tableReference);
            parsePivot(tableReference);
        }

        return parseTableSourceRest(tableReference);
    }

    private void parseTableSourceQueryTableExpr(OracleSelectTableReference tableReference) {
        tableReference.setExpr(this.createExprParser().expr());

        if (identifierEquals("PARTITION")) {
            lexer.nextToken();
            throw new ParserException("TODO");
        }
        if (identifierEquals("SUBPARTITION")) {
            throw new ParserException("TODO");
        }
    }

    protected SQLTableSource parseTableSourceRest(SQLTableSource tableSource) {
        if ((tableSource.getAlias() == null) || (tableSource.getAlias().length() == 0)) {
            if (lexer.token() != Token.LEFT && lexer.token() != Token.RIGHT && lexer.token() != Token.FULL) {
                tableSource.setAlias(as());
            }
        }

        OracleSelectJoin.JoinType joinType = null;

        if (identifierEquals("LEFT")) {
            lexer.nextToken();
            if (identifierEquals("OUTER")) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = OracleSelectJoin.JoinType.LEFT_OUTER_JOIN;
        }

        if (identifierEquals("RIGHT")) {
            lexer.nextToken();
            if (identifierEquals("OUTER")) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = OracleSelectJoin.JoinType.RIGHT_OUTER_JOIN;
        }

        if (identifierEquals("FULL")) {
            lexer.nextToken();
            if (identifierEquals("OUTER")) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = OracleSelectJoin.JoinType.FULL_OUTER_JOIN;
        }

        if (identifierEquals("INNER")) {
            lexer.nextToken();
            accept(Token.JOIN);
            joinType = OracleSelectJoin.JoinType.INNER_JOIN;
        }

        if (lexer.token() == (Token.JOIN)) {
            lexer.nextToken();
            joinType = OracleSelectJoin.JoinType.JOIN;
        }

        if (joinType != null) {
            OracleSelectJoin join = new OracleSelectJoin();
            join.setLeft(tableSource);
            join.setJoinType(joinType);
            join.setRight(parseTableSource());

            if (identifierEquals("ON")) {
                lexer.nextToken();
                join.setCondition(this.createExprParser().expr());
            } else if (identifierEquals("USING")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                this.createExprParser().exprList(join.getUsing());
                accept(Token.RPAREN);
            }

            return join;
        }

        return tableSource;
    }

    private void parsePivot(OracleSelectTableSource tableSource) {
        OracleSelectPivot.Item item;
        if (identifierEquals("PIVOT")) {
            lexer.nextToken();

            OracleSelectPivot pivot = new OracleSelectPivot();

            if (identifierEquals("XML")) {
                lexer.nextToken();
                pivot.setXml(true);
            }

            accept(Token.LPAREN);
            while (true) {
                item = new OracleSelectPivot.Item();
                item.setExpr((OracleAggregateExpr) this.createExprParser().expr());
                item.setAlias(as());
                pivot.getItems().add(item);

                if (!(lexer.token() == (Token.COMMA))) break;
                lexer.nextToken();
            }

            acceptIdentifier("FOR");

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                while (true) {
                    pivot.getPivotFor().add(new SQLIdentifierExpr(lexer.stringVal()));
                    lexer.nextToken();

                    if (!(lexer.token() == (Token.COMMA))) break;
                    lexer.nextToken();
                }

                accept(Token.RPAREN);
            } else {
                pivot.getPivotFor().add(new SQLIdentifierExpr(lexer.stringVal()));
                lexer.nextToken();
            }

            acceptIdentifier("IN");
            accept(Token.LPAREN);
            if (lexer.token() == (Token.LPAREN)) throw new ParserException("TODO");
            if (lexer.token() == (Token.SELECT)) throw new ParserException("TODO");
            while (true) {
                item = new OracleSelectPivot.Item();
                item.setExpr(this.createExprParser().expr());
                item.setAlias(as());
                pivot.getPivotIn().add(item);

                if (!(lexer.token() == (Token.COMMA))) break;
                lexer.nextToken();
            }

            accept(Token.RPAREN);

            accept(Token.RPAREN);

            tableSource.setPivot(pivot);
        } else if (identifierEquals("UNPIVOT")) {
            lexer.nextToken();

            OracleSelectUnPivot unPivot = new OracleSelectUnPivot();
            if (identifierEquals("INCLUDE")) {
                lexer.nextToken();
                acceptIdentifier("NULLS");
                unPivot.setNullsIncludeType(OracleSelectUnPivot.NullsIncludeType.INCLUDE_NULLS);
            } else if (identifierEquals("EXCLUDE")) {
                lexer.nextToken();
                acceptIdentifier("NULLS");
                unPivot.setNullsIncludeType(OracleSelectUnPivot.NullsIncludeType.EXCLUDE_NULLS);
            }

            accept(Token.LPAREN);

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                this.createExprParser().exprList(unPivot.getItems());
                accept(Token.RPAREN);
            } else {
                unPivot.getItems().add(this.createExprParser().expr());
            }

            acceptIdentifier("FOR");

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                while (true) {
                    unPivot.getPivotFor().add(new SQLIdentifierExpr(lexer.stringVal()));
                    lexer.nextToken();

                    if (!(lexer.token() == (Token.COMMA))) break;
                    lexer.nextToken();
                }

                accept(Token.RPAREN);
            } else {
                unPivot.getPivotFor().add(new SQLIdentifierExpr(lexer.stringVal()));
                lexer.nextToken();
            }

            acceptIdentifier("IN");
            accept(Token.LPAREN);
            if (lexer.token() == (Token.LPAREN)) throw new ParserException("TODO");
            if (lexer.token() == (Token.SELECT)) throw new ParserException("TODO");
            while (true) {
                item = new OracleSelectPivot.Item();
                item.setExpr(this.createExprParser().expr());
                item.setAlias(as());
                unPivot.getPivotIn().add(item);

                if (!(lexer.token() == (Token.COMMA))) break;
                lexer.nextToken();
            }

            accept(Token.RPAREN);

            accept(Token.RPAREN);

            tableSource.setPivot(unPivot);
        }
    }

    private void parseHints(OracleSelectQueryBlock queryBlock) {
        if (lexer.token() == Token.HINT) throw new ParserException("TODO");
    }
}
