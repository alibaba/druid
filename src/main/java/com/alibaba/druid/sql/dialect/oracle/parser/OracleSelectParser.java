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
package com.alibaba.druid.sql.dialect.oracle.parser;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.CycleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.CellAssignment;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.CellAssignmentItem;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.CellReferenceOption;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.MainModelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelColumn;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelColumnClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelRuleOption;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ModelRulesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.QueryPartitionClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ReferenceModelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause.ReturnRowsClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleWithSubqueryEntry;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SampleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SearchClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectJoin;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

public class OracleSelectParser extends SQLSelectParser {

    public OracleSelectParser(String sql){
        super(new OracleExprParser(sql));
    }

    public OracleSelectParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public OracleSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache){
        super(exprParser, selectListCache);
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

        OracleSelectQueryBlock queryBlock = null;
        if (query instanceof SQLSelectQueryBlock) {
            queryBlock = (OracleSelectQueryBlock) query;

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

        if (lexer.token() == (Token.FOR)) {
            if (queryBlock == null) {
                throw new ParserException("TODO. " + lexer.info());
            }

            lexer.nextToken();
            accept(Token.UPDATE);

            queryBlock.setForUpdate(true);

            // OracleSelectForUpdate forUpdate = new OracleSelectForUpdate();

            if (lexer.token() == Token.OF) {
                lexer.nextToken();
                this.exprParser.exprList(queryBlock.getForUpdateOf(), queryBlock);
            } else if (lexer.token() == Token.LPAREN
                    && queryBlock.isForUpdate()) {
                this.exprParser.exprList(queryBlock.getForUpdateOf(), queryBlock);
            }

            if (lexer.token() == Token.NOWAIT) {
                lexer.nextToken();
                queryBlock.setNoWait(true);
            } else if (lexer.token() == Token.WAIT) {
                lexer.nextToken();
                queryBlock.setWaitTime(this.exprParser.primary());
            } else if (lexer.identifierEquals("SKIP")) {
                lexer.nextToken();
                acceptIdentifier("LOCKED");
                queryBlock.setSkipLocked(true);
            }
        }

        if (lexer.token() == Token.ORDER) {
            orderBy = this.exprParser.parseOrderBy();
            if (queryBlock != null && queryBlock.getOrderBy() == null) {
                queryBlock.setOrderBy(orderBy);
            } else if (select.getOrderBy() == null){
                select.setOrderBy(orderBy);
            } else {
                throw new ParserException("illegal state.");
            }
        }

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();

            if (lexer.identifierEquals("READ")) {
                lexer.nextToken();

                if (lexer.identifierEquals("ONLY")) {
                    lexer.nextToken();
                } else {
                    throw new ParserException("syntax error. " + lexer.info());
                }

                select.setRestriction(new OracleSelectRestriction.ReadOnly());
            } else if (lexer.token() == (Token.CHECK)) {
                lexer.nextToken();

                if (lexer.identifierEquals("OPTION")) {
                    lexer.nextToken();
                } else {
                    throw new ParserException("syntax error. " + lexer.info());
                }

                OracleSelectRestriction.CheckOption checkOption = new OracleSelectRestriction.CheckOption();

                if (lexer.token() == Token.CONSTRAINT) {
                    lexer.nextToken();
                    throw new ParserException("TODO. " + lexer.info());
                }

                select.setRestriction(checkOption);
            } else {
                throw new ParserException("syntax error. " + lexer.info());
            }
        }

        return select;
    }

    public SQLWithSubqueryClause parseWith() {
        accept(Token.WITH);
        SQLWithSubqueryClause subqueryFactoringClause = new SQLWithSubqueryClause();
        for (;;) {
            OracleWithSubqueryEntry entry = new OracleWithSubqueryEntry();

            String alias = lexer.stringVal();
            lexer.nextToken();
            entry.setAlias(alias);

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                exprParser.names(entry.getColumns());
                accept(Token.RPAREN);
            }

            accept(Token.AS);
            accept(Token.LPAREN);
            entry.setSubQuery(select());
            accept(Token.RPAREN);

            if (lexer.identifierEquals("SEARCH")) {
                lexer.nextToken();
                SearchClause searchClause = new SearchClause();

                if (lexer.token() != Token.IDENTIFIER) {
                    throw new ParserException("syntax erorr : " + lexer.token());
                }

                if (lexer.identifierEquals(FnvHash.Constants.DEPTH)) {
                    lexer.nextToken();
                    searchClause.setType(SearchClause.Type.DEPTH);
                } else if (lexer.identifierEquals(FnvHash.Constants.BREADTH)) {
                    lexer.nextToken();
                    searchClause.setType(SearchClause.Type.BREADTH);
                } else {
                    searchClause.setType(SearchClause.Type.valueOf(lexer.stringVal().toUpperCase()));
                    lexer.nextToken();
                }

                acceptIdentifier("FIRST");
                accept(Token.BY);

                searchClause.addItem(exprParser.parseSelectOrderByItem());

                while (lexer.token() == (Token.COMMA)) {
                    lexer.nextToken();
                    searchClause.addItem(exprParser.parseSelectOrderByItem());
                }

                accept(Token.SET);

                searchClause.setOrderingColumn((SQLIdentifierExpr) exprParser.name());

                entry.setSearchClause(searchClause);
            }

            if (lexer.identifierEquals("CYCLE")) {
                lexer.nextToken();
                CycleClause cycleClause = new CycleClause();
                exprParser.exprList(cycleClause.getAliases(), cycleClause);
                accept(Token.SET);
                cycleClause.setMark(exprParser.expr());
                accept(Token.TO);
                cycleClause.setValue(exprParser.expr());
                accept(Token.DEFAULT);
                cycleClause.setDefaultValue(exprParser.expr());
                entry.setCycleClause(cycleClause);
            }

            subqueryFactoringClause.addEntry(entry);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        return subqueryFactoringClause;
    }

    public SQLSelectQuery query() {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select);
        }

        OracleSelectQueryBlock queryBlock = new OracleSelectQueryBlock();
        if (lexer.hasComment() && lexer.isKeepComments()) {
            queryBlock.addBeforeComment(lexer.readAndResetComments());
        }

        if (lexer.token() == Token.SELECT) {
            lexer.nextToken();

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            parseHints(queryBlock);

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

            this.exprParser.parseHints(queryBlock.getHints());

            parseSelectList(queryBlock);
        }

        parseInto(queryBlock);

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseHierachical(queryBlock);

        parseGroupBy(queryBlock);

        parseModelClause(queryBlock);

        parseFetchClause(queryBlock);

        return queryRest(queryBlock);
    }

    public SQLSelectQuery queryRest(SQLSelectQuery selectQuery) {
        if (lexer.token() == Token.UNION) {
            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            lexer.nextToken();

            if (lexer.token() == Token.ALL) {
                union.setOperator(SQLUnionOperator.UNION_ALL);
                lexer.nextToken();
            } else if (lexer.token() == Token.DISTINCT) {
                union.setOperator(SQLUnionOperator.DISTINCT);
                lexer.nextToken();
            }

            SQLSelectQuery right = query();

            union.setRight(right);

            return queryRest(union);
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

    private void parseModelClause(OracleSelectQueryBlock queryBlock) {
        if (lexer.token() != Token.MODEL) {
            return;
        }

        lexer.nextToken();

        ModelClause model = new ModelClause();
        parseCellReferenceOptions(model.getCellReferenceOptions());

        if (lexer.identifierEquals("RETURN")) {
            lexer.nextToken();
            ReturnRowsClause returnRowsClause = new ReturnRowsClause();
            if (lexer.token() == Token.ALL) {
                lexer.nextToken();
                returnRowsClause.setAll(true);
            } else {
                acceptIdentifier("UPDATED");
            }
            acceptIdentifier("ROWS");

            model.setReturnRowsClause(returnRowsClause);
        }

        while (lexer.identifierEquals("REFERENCE")) {
            ReferenceModelClause referenceModelClause = new ReferenceModelClause();
            lexer.nextToken();

            SQLExpr name = expr();
            referenceModelClause.setName(name);

            accept(Token.ON);
            accept(Token.LPAREN);
            SQLSelect subQuery = this.select();
            accept(Token.RPAREN);
            referenceModelClause.setSubQuery(subQuery);

            parseModelColumnClause(referenceModelClause);

            parseCellReferenceOptions(referenceModelClause.getCellReferenceOptions());

            model.getReferenceModelClauses().add(referenceModelClause);
        }

        parseMainModelClause(model);

        queryBlock.setModelClause(model);
    }

    private void parseMainModelClause(ModelClause modelClause) {
        MainModelClause mainModel = new MainModelClause();

        if (lexer.identifierEquals("MAIN")) {
            lexer.nextToken();
            mainModel.setMainModelName(expr());
        }

        ModelColumnClause modelColumnClause = new ModelColumnClause();
        parseQueryPartitionClause(modelColumnClause);
        mainModel.setModelColumnClause(modelColumnClause);

        acceptIdentifier("DIMENSION");
        accept(Token.BY);
        accept(Token.LPAREN);
        for (;;) {
            if (lexer.token() == Token.RPAREN) {
                lexer.nextToken();
                break;
            }

            ModelColumn column = new ModelColumn();
            column.setExpr(expr());
            column.setAlias(as());
            modelColumnClause.getDimensionByColumns().add(column);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
        }

        acceptIdentifier("MEASURES");
        accept(Token.LPAREN);
        for (;;) {
            if (lexer.token() == Token.RPAREN) {
                lexer.nextToken();
                break;
            }

            ModelColumn column = new ModelColumn();
            column.setExpr(expr());
            column.setAlias(as());
            modelColumnClause.getMeasuresColumns().add(column);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
        }
        mainModel.setModelColumnClause(modelColumnClause);

        parseCellReferenceOptions(mainModel.getCellReferenceOptions());

        parseModelRulesClause(mainModel);

        modelClause.setMainModel(mainModel);
    }

    private void parseModelRulesClause(MainModelClause mainModel) {
        ModelRulesClause modelRulesClause = new ModelRulesClause();
        if (lexer.identifierEquals("RULES")) {
            lexer.nextToken();
            if (lexer.token() == Token.UPDATE) {
                modelRulesClause.getOptions().add(ModelRuleOption.UPDATE);
                lexer.nextToken();
            } else if (lexer.identifierEquals("UPSERT")) {
                modelRulesClause.getOptions().add(ModelRuleOption.UPSERT);
                lexer.nextToken();
            }

            if (lexer.identifierEquals("AUTOMATIC")) {
                lexer.nextToken();
                accept(Token.ORDER);
                modelRulesClause.getOptions().add(ModelRuleOption.AUTOMATIC_ORDER);
            } else if (lexer.identifierEquals("SEQUENTIAL")) {
                lexer.nextToken();
                accept(Token.ORDER);
                modelRulesClause.getOptions().add(ModelRuleOption.SEQUENTIAL_ORDER);
            }
        }

        if (lexer.identifierEquals("ITERATE")) {
            lexer.nextToken();
            accept(Token.LPAREN);
            modelRulesClause.setIterate(expr());
            accept(Token.RPAREN);

            if (lexer.identifierEquals("UNTIL")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                modelRulesClause.setUntil(expr());
                accept(Token.RPAREN);
            }
        }

        accept(Token.LPAREN);
        for (;;) {
            if (lexer.token() == Token.RPAREN) {
                lexer.nextToken();
                break;
            }

            CellAssignmentItem item = new CellAssignmentItem();
            if (lexer.token() == Token.UPDATE) {
                item.setOption(ModelRuleOption.UPDATE);
            } else if (lexer.identifierEquals("UPSERT")) {
                item.setOption(ModelRuleOption.UPSERT);
            }

            item.setCellAssignment(parseCellAssignment());
            item.setOrderBy(this.parseOrderBy());
            accept(Token.EQ);

            SQLExpr expr = this.expr();
            item.setExpr(expr);

            modelRulesClause.getCellAssignmentItems().add(item);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
        }

        mainModel.setModelRulesClause(modelRulesClause);
    }

    private CellAssignment parseCellAssignment() {
        CellAssignment cellAssignment = new CellAssignment();

        cellAssignment.setMeasureColumn(this.exprParser.name());
        accept(Token.LBRACKET);
        this.exprParser.exprList(cellAssignment.getConditions(), cellAssignment);
        accept(Token.RBRACKET);

        return cellAssignment;
    }

    private void parseQueryPartitionClause(ModelColumnClause modelColumnClause) {
        if (lexer.token() == Token.PARTITION) {
            QueryPartitionClause queryPartitionClause = new QueryPartitionClause();

            lexer.nextToken();
            accept(Token.BY);
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                exprParser.exprList(queryPartitionClause.getExprList(), queryPartitionClause);
                accept(Token.RPAREN);
            } else {
                exprParser.exprList(queryPartitionClause.getExprList(), queryPartitionClause);
            }
            modelColumnClause.setQueryPartitionClause(queryPartitionClause);
        }
    }

    private void parseModelColumnClause(ReferenceModelClause referenceModelClause) {
        throw new ParserException();
    }

    private void parseCellReferenceOptions(List<CellReferenceOption> options) {
        if (lexer.identifierEquals("IGNORE")) {
            lexer.nextToken();
            acceptIdentifier("NAV");
            options.add(CellReferenceOption.IgnoreNav);
        } else if (lexer.identifierEquals("KEEP")) {
            lexer.nextToken();
            acceptIdentifier("NAV");
            options.add(CellReferenceOption.KeepNav);
        }

        if (lexer.token() == Token.UNIQUE) {
            lexer.nextToken();
            if (lexer.identifierEquals("DIMENSION")) {
                lexer.nextToken();
                options.add(CellReferenceOption.UniqueDimension);
            } else {
                acceptIdentifier("SINGLE");
                acceptIdentifier("REFERENCE");
                options.add(CellReferenceOption.UniqueDimension);
            }
        }
    }

    @Override
    public SQLTableSource parseTableSource() {
        SQLTableSource tableSource = parseTableSourcePrimary();

        if (tableSource instanceof OracleSelectTableSource) {
            return parseTableSourceRest((OracleSelectTableSource) tableSource);
        }

        return parseTableSourceRest(tableSource);
    }

    public SQLTableSource parseTableSourcePrimary() {
        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();

            OracleSelectTableSource tableSource;
            if (lexer.token() == Token.SELECT || lexer.token() == Token.WITH) {
                tableSource = new OracleSelectSubqueryTableSource(select());
            } else if (lexer.token() == Token.LPAREN) {
                tableSource = (OracleSelectTableSource) parseTableSource();
            } else if (lexer.token() == Token.IDENTIFIER) {
                SQLTableSource identTable = parseTableSource();
                accept(Token.RPAREN);
                parsePivot((OracleSelectTableSource) identTable);
                return identTable;
            } else {
                throw new ParserException("TODO :" + lexer.info());
            }

            accept(Token.RPAREN);

            if (lexer.token() == Token.UNION && tableSource instanceof OracleSelectSubqueryTableSource) {
                OracleSelectSubqueryTableSource selectSubqueryTableSource = (OracleSelectSubqueryTableSource) tableSource;
                SQLSelect select = selectSubqueryTableSource.getSelect();
                SQLSelectQuery selectQuery = this.queryRest(select.getQuery());
                select.setQuery(selectQuery);
            }

            parsePivot(tableSource);

            return tableSource;
        }

        if (lexer.token() == (Token.SELECT)) {
            throw new ParserException("TODO. " + lexer.info());
        }

        OracleSelectTableReference tableReference = new OracleSelectTableReference();

        if (lexer.identifierEquals("ONLY")) {
            lexer.nextToken();
            tableReference.setOnly(true);
            accept(Token.LPAREN);
            parseTableSourceQueryTableExpr(tableReference);
            accept(Token.RPAREN);
        } else {
            parseTableSourceQueryTableExpr(tableReference);
            parsePivot(tableReference);
        }

        return tableReference;
    }

    private void parseTableSourceQueryTableExpr(OracleSelectTableReference tableReference) {
        tableReference.setExpr(this.exprParser.expr());

//        {
//            FlashbackQueryClause clause = flashback();
//            tableReference.setFlashback(clause);
//        }

        if (lexer.identifierEquals("SAMPLE")) {
            lexer.nextToken();

            SampleClause sample = new SampleClause();

            if (lexer.identifierEquals("BLOCK")) {
                sample.setBlock(true);
                lexer.nextToken();
            }

            accept(Token.LPAREN);
            this.exprParser.exprList(sample.getPercent(), sample);
            accept(Token.RPAREN);

            if (lexer.identifierEquals("SEED")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                sample.setSeedValue(expr());
                accept(Token.RPAREN);
            }

            tableReference.setSampleClause(sample);
        }

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            PartitionExtensionClause partition = new PartitionExtensionClause();

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                partition.setPartition(exprParser.name());
                accept(Token.RPAREN);
            } else if (lexer.token() == Token.BY) {
                lexer.nextToken();
                accept(Token.LPAREN);
                partition.setPartition(exprParser.name());
                accept(Token.RPAREN);
            } else {
                accept(Token.FOR);
                accept(Token.LPAREN);
                exprParser.names(partition.getFor());
                accept(Token.RPAREN);
            }

            tableReference.setPartition(partition);
        }

        if (lexer.identifierEquals("SUBPARTITION")) {
            lexer.nextToken();
            PartitionExtensionClause partition = new PartitionExtensionClause();
            partition.setSubPartition(true);

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                partition.setPartition(exprParser.name());
                accept(Token.RPAREN);
            } else {
                accept(Token.FOR);
                accept(Token.LPAREN);
                exprParser.names(partition.getFor());
                accept(Token.RPAREN);
            }

            tableReference.setPartition(partition);
        }

        if (lexer.identifierEquals("VERSIONS")) {
            SQLBetweenExpr betweenExpr = new SQLBetweenExpr();
            betweenExpr.setTestExpr(new SQLIdentifierExpr("VERSIONS"));
            lexer.nextToken();

            accept(Token.BETWEEN);


            SQLFlashbackExpr start = new SQLFlashbackExpr();
            if (lexer.identifierEquals("SCN")) {
                lexer.nextToken();
                start.setType(SQLFlashbackExpr.Type.SCN);
            } else {
                acceptIdentifier("TIMESTAMP");
                start.setType(SQLFlashbackExpr.Type.TIMESTAMP);
            }

            SQLBinaryOpExpr binaryExpr = (SQLBinaryOpExpr) exprParser.expr();
            if (binaryExpr.getOperator() != SQLBinaryOperator.BooleanAnd) {
                throw new ParserException("syntax error : " + binaryExpr.getOperator() + ", " + lexer.info());
            }

            start.setExpr(binaryExpr.getLeft());

            betweenExpr.setBeginExpr(start);
            betweenExpr.setEndExpr(binaryExpr.getRight());

            tableReference.setFlashback(betweenExpr);
        }

    }

    private SQLExpr flashback() {
        accept(Token.OF);
        if (lexer.identifierEquals("SCN")) {
            lexer.nextToken();
            return new SQLFlashbackExpr(SQLFlashbackExpr.Type.SCN, this.expr());
        } else if (lexer.identifierEquals("SNAPSHOT")) {
            return this.expr();
        } else {
            lexer.nextToken();
            return new SQLFlashbackExpr(SQLFlashbackExpr.Type.TIMESTAMP, this.expr());
        }
    }

    protected SQLTableSource primaryTableSourceRest(SQLTableSource tableSource) {
        if (tableSource instanceof OracleSelectTableSource) {
            if (lexer.token() == Token.AS) {
                lexer.nextToken();

                if (lexer.token() == Token.OF) {
                    ((OracleSelectTableSource)tableSource).setFlashback(flashback());
                }

                tableSource.setAlias(tableAlias());
            }
        }

        return tableSource;
    }

    protected SQLTableSource parseTableSourceRest(OracleSelectTableSource tableSource) {
        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            if (lexer.token() == Token.OF) {
                tableSource.setFlashback(flashback());
                return parseTableSourceRest(tableSource);
            }

            tableSource.setAlias(tableAlias(true));
        } else if ((tableSource.getAlias() == null) || (tableSource.getAlias().length() == 0)) {
            if (lexer.token() != Token.LEFT && lexer.token() != Token.RIGHT && lexer.token() != Token.FULL) {
                tableSource.setAlias(tableAlias());
            }
        }

        if (lexer.token() == Token.HINT) {
            this.exprParser.parseHints(tableSource.getHints());
        }

        SQLJoinTableSource.JoinType joinType = null;

        if (lexer.token() == Token.LEFT) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.LEFT_OUTER_JOIN;
        }

        if (lexer.token() == Token.RIGHT) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.RIGHT_OUTER_JOIN;
        }

        if (lexer.token() == Token.FULL) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.FULL_OUTER_JOIN;
        }

        if (lexer.token() == Token.INNER) {
            lexer.nextToken();
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.INNER_JOIN;
        }
        if (lexer.token() == Token.CROSS) {
            lexer.nextToken();
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.CROSS_JOIN;
        }

        if (lexer.token() == Token.JOIN) {
            lexer.nextToken();
            joinType = SQLJoinTableSource.JoinType.JOIN;
        }

        if (lexer.token() == (Token.COMMA)) {
            lexer.nextToken();
            joinType = SQLJoinTableSource.JoinType.COMMA;
        }

        if (joinType != null) {
            OracleSelectJoin join = new OracleSelectJoin();
            join.setLeft(tableSource);
            join.setJoinType(joinType);

            SQLTableSource right;
            right = parseTableSourcePrimary();
            right.setAlias(this.tableAlias());
            join.setRight(right);

            if (lexer.token() == Token.ON) {
                lexer.nextToken();
                join.setCondition(this.exprParser.expr());

                if (lexer.token() == Token.ON
                        && tableSource instanceof SQLJoinTableSource
                        && ((SQLJoinTableSource) tableSource).getCondition() == null) {
                    lexer.nextToken();
                    SQLExpr leftCondidition = this.exprParser.expr();
                    ((SQLJoinTableSource) tableSource).setCondition(leftCondidition);
                }
            } else if (lexer.token() == Token.USING) {
                lexer.nextToken();
                accept(Token.LPAREN);
                this.exprParser.exprList(join.getUsing(), join);
                accept(Token.RPAREN);
            }

            parsePivot(join);

            return parseTableSourceRest(join);
        }

        return tableSource;
    }

    private void parsePivot(OracleSelectTableSource tableSource) {
        OracleSelectPivot.Item item;
        if (lexer.identifierEquals("PIVOT")) {
            lexer.nextToken();

            OracleSelectPivot pivot = new OracleSelectPivot();

            if (lexer.identifierEquals("XML")) {
                lexer.nextToken();
                pivot.setXml(true);
            }

            accept(Token.LPAREN);
            while (true) {
                item = new OracleSelectPivot.Item();
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
                item = new OracleSelectPivot.Item();
                item.setExpr(expr);
                pivot.getPivotIn().add(item);
            } else {
                for (; ; ) {
                    item = new OracleSelectPivot.Item();
                    item.setExpr(this.exprParser.expr());
                    item.setAlias(as());
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

            OracleSelectUnPivot unPivot = new OracleSelectUnPivot();
            if (lexer.identifierEquals("INCLUDE")) {
                lexer.nextToken();
                acceptIdentifier("NULLS");
                unPivot.setNullsIncludeType(OracleSelectUnPivot.NullsIncludeType.INCLUDE_NULLS);
            } else if (lexer.identifierEquals("EXCLUDE")) {
                lexer.nextToken();
                acceptIdentifier("NULLS");
                unPivot.setNullsIncludeType(OracleSelectUnPivot.NullsIncludeType.EXCLUDE_NULLS);
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

            for (;;) {
                item = new OracleSelectPivot.Item();
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

            tableSource.setPivot(unPivot);
        }
    }

    protected void parseInto(OracleSelectQueryBlock x) {
        if (lexer.token() == Token.INTO) {
            lexer.nextToken();

            if (lexer.token() == Token.FROM) {
                return;
            }

            SQLExpr expr = expr();
            if (lexer.token() != Token.COMMA) {
                x.setInto(expr);
                return;
            }
            SQLListExpr list = new SQLListExpr();
            list.addItem(expr);
            while (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                list.addItem(expr());
            }
            x.setInto(list);
        }
    }

    private void parseHints(OracleSelectQueryBlock queryBlock) {
        this.exprParser.parseHints(queryBlock.getHints());
    }
}
