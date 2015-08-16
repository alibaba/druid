/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionOperator;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.CycleClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause.AsOfFlashbackQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause.AsOfSnapshotClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause.VersionsFlashbackQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.GroupingSetExpr;
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
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleOrderByItem;
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
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;

public class OracleSelectParser extends SQLSelectParser {

    public OracleSelectParser(String sql){
        super(new OracleExprParser(sql));
    }

    public OracleSelectParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public OracleSelect select() {
        OracleSelect select = new OracleSelect();

        withSubquery(select);

        select.setQuery(query());
        select.setOrderBy(this.parseOrderBy());

        if (lexer.token() == (Token.FOR)) {
            lexer.nextToken();
            accept(Token.UPDATE);

            OracleSelectForUpdate forUpdate = new OracleSelectForUpdate();

            if (lexer.token() == Token.OF) {
                lexer.nextToken();
                this.exprParser.exprList(forUpdate.getOf(), forUpdate);
            }

            if (lexer.token() == Token.NOWAIT) {
                lexer.nextToken();
                forUpdate.setNotWait(true);
            } else if (lexer.token() == Token.WAIT) {
                lexer.nextToken();
                forUpdate.setWait(this.exprParser.primary());
            } else if (identifierEquals("SKIP")) {
                lexer.nextToken();
                acceptIdentifier("LOCKED");
                forUpdate.setSkipLocked(true);
            }

            select.setForUpdate(forUpdate);
        }

        if (select.getOrderBy() == null) {
            select.setOrderBy(this.exprParser.parseOrderBy());
        }

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();

            if (identifierEquals("READ")) {
                lexer.nextToken();

                if (identifierEquals("ONLY")) {
                    lexer.nextToken();
                } else {
                    throw new ParserException("syntax error");
                }

                select.setRestriction(new OracleSelectRestriction.ReadOnly());
            } else if (lexer.token() == (Token.CHECK)) {
                lexer.nextToken();

                if (identifierEquals("OPTION")) {
                    lexer.nextToken();
                } else {
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

    protected void withSubquery(SQLSelect select) {
        if (lexer.token() == Token.WITH) {
            lexer.nextToken();

            SQLWithSubqueryClause subqueryFactoringClause = new SQLWithSubqueryClause();
            for (;;) {
                OracleWithSubqueryEntry entry = new OracleWithSubqueryEntry();
                entry.setName((SQLIdentifierExpr) this.exprParser.name());

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    exprParser.names(entry.getColumns());
                    accept(Token.RPAREN);
                }

                accept(Token.AS);
                accept(Token.LPAREN);
                entry.setSubQuery(select());
                accept(Token.RPAREN);

                if (identifierEquals("SEARCH")) {
                    lexer.nextToken();
                    SearchClause searchClause = new SearchClause();

                    if (lexer.token() != Token.IDENTIFIER) {
                        throw new ParserException("syntax erorr : " + lexer.token());
                    }

                    searchClause.setType(SearchClause.Type.valueOf(lexer.stringVal()));
                    lexer.nextToken();

                    acceptIdentifier("FIRST");
                    accept(Token.BY);

                    searchClause.getItems().add((OracleOrderByItem) exprParser.parseSelectOrderByItem());

                    while (lexer.token() == (Token.COMMA)) {
                        lexer.nextToken();
                        searchClause.getItems().add((OracleOrderByItem) exprParser.parseSelectOrderByItem());
                    }

                    accept(Token.SET);

                    searchClause.setOrderingColumn((SQLIdentifierExpr) exprParser.name());

                    entry.setSearchClause(searchClause);
                }

                if (identifierEquals("CYCLE")) {
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

                subqueryFactoringClause.getEntries().add(entry);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }

            select.setWithSubQuery(subqueryFactoringClause);
        }
    }

    public SQLSelectQuery query() {
        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select);
        }

        OracleSelectQueryBlock queryBlock = new OracleSelectQueryBlock();
        if (lexer.token() == Token.SELECT) {
            lexer.nextToken();

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            parseHints(queryBlock);

            if (lexer.token() == (Token.DISTINCT)) {
                queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
                lexer.nextToken();
            } else if (lexer.token() == (Token.UNIQUE)) {
                queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
                lexer.nextToken();
            } else if (lexer.token() == (Token.ALL)) {
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

        return queryRest(queryBlock);
    }

    public SQLSelectQuery queryRest(SQLSelectQuery selectQuery) {
        if (lexer.token() == (Token.UNION)) {
            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            lexer.nextToken();

            if (lexer.token() == (Token.ALL)) {
                union.setOperator(SQLUnionOperator.UNION_ALL);
                lexer.nextToken();
            } else if (lexer.token() == (Token.DISTINCT)) {
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

        if (identifierEquals("RETURN")) {
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

        while (identifierEquals("REFERENCE")) {
            ReferenceModelClause referenceModelClause = new ReferenceModelClause();
            lexer.nextToken();

            SQLExpr name = expr();
            referenceModelClause.setName(name);

            accept(Token.ON);
            accept(Token.LPAREN);
            OracleSelect subQuery = this.select();
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

        if (identifierEquals("MAIN")) {
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
        if (identifierEquals("RULES")) {
            lexer.nextToken();
            if (lexer.token() == Token.UPDATE) {
                modelRulesClause.getOptions().add(ModelRuleOption.UPDATE);
                lexer.nextToken();
            } else if (identifierEquals("UPSERT")) {
                modelRulesClause.getOptions().add(ModelRuleOption.UPSERT);
                lexer.nextToken();
            }

            if (identifierEquals("AUTOMATIC")) {
                lexer.nextToken();
                accept(Token.ORDER);
                modelRulesClause.getOptions().add(ModelRuleOption.AUTOMATIC_ORDER);
            } else if (identifierEquals("SEQUENTIAL")) {
                lexer.nextToken();
                accept(Token.ORDER);
                modelRulesClause.getOptions().add(ModelRuleOption.SEQUENTIAL_ORDER);
            }
        }

        if (identifierEquals("ITERATE")) {
            lexer.nextToken();
            accept(Token.LPAREN);
            modelRulesClause.setIterate(expr());
            accept(Token.RPAREN);

            if (identifierEquals("UNTIL")) {
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
            } else if (identifierEquals("UPSERT")) {
                item.setOption(ModelRuleOption.UPSERT);
            }

            item.setCellAssignment(parseCellAssignment());
            item.setOrderBy(this.parseOrderBy());
            accept(Token.EQ);
            item.setExpr(expr());

            modelRulesClause.getCellAssignmentItems().add(item);
        }

        mainModel.setModelRulesClause(modelRulesClause);
    }

    private CellAssignment parseCellAssignment() {
        CellAssignment cellAssignment = new CellAssignment();

        cellAssignment.setMeasureColumn(expr());
        accept(Token.LBRACKET);
        this.exprParser.exprList(cellAssignment.getConditions(), cellAssignment);
        accept(Token.RBRACKET);

        return cellAssignment;
    }

    private void parseQueryPartitionClause(ModelColumnClause modelColumnClause) {
        if (identifierEquals("PARTITION")) {
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
        if (identifierEquals("IGNORE")) {
            lexer.nextToken();
            acceptIdentifier("NAV");
            options.add(CellReferenceOption.IgnoreNav);
        } else if (identifierEquals("KEEP")) {
            lexer.nextToken();
            acceptIdentifier("NAV");
            options.add(CellReferenceOption.KeepNav);
        }

        if (lexer.token() == Token.UNIQUE) {
            lexer.nextToken();
            if (identifierEquals("DIMENSION")) {
                lexer.nextToken();
                options.add(CellReferenceOption.UniqueDimension);
            } else {
                acceptIdentifier("SINGLE");
                acceptIdentifier("REFERENCE");
                options.add(CellReferenceOption.UniqueDimension);
            }
        }
    }

    private void parseGroupBy(OracleSelectQueryBlock queryBlock) {
        if (lexer.token() == (Token.GROUP)) {
            lexer.nextToken();
            accept(Token.BY);

            SQLSelectGroupByClause groupBy = new SQLSelectGroupByClause();
            for (;;) {
                if (identifierEquals("GROUPING")) {
                    GroupingSetExpr groupingSet = new GroupingSetExpr();
                    lexer.nextToken();
                    acceptIdentifier("SETS");
                    accept(Token.LPAREN);
                    exprParser.exprList(groupingSet.getParameters(), groupingSet);
                    accept(Token.RPAREN);
                    groupBy.addItem(groupingSet);
                } else {
                    groupBy.addItem(this.exprParser.expr());
                }

                if (!(lexer.token() == (Token.COMMA))) {
                    break;
                }

                lexer.nextToken();
            }

            if (lexer.token() == (Token.HAVING)) {
                lexer.nextToken();

                groupBy.setHaving(this.exprParser.expr());
            }

            queryBlock.setGroupBy(groupBy);
        } else if (lexer.token() == (Token.HAVING)) {
            lexer.nextToken();

            SQLSelectGroupByClause groupBy = new SQLSelectGroupByClause();
            groupBy.setHaving(this.exprParser.expr());

            if (lexer.token() == (Token.GROUP)) {
                lexer.nextToken();
                accept(Token.BY);

                for (;;) {
                    if (identifierEquals("GROUPING")) {
                        GroupingSetExpr groupingSet = new GroupingSetExpr();
                        lexer.nextToken();
                        acceptIdentifier("SETS");
                        accept(Token.LPAREN);
                        exprParser.exprList(groupingSet.getParameters(), groupingSet);
                        accept(Token.RPAREN);
                        groupBy.addItem(groupingSet);
                    } else {
                        groupBy.addItem(this.exprParser.expr());
                    }

                    if (!(lexer.token() == (Token.COMMA))) {
                        break;
                    }

                    lexer.nextToken();
                }
            }

            queryBlock.setGroupBy(groupBy);
        }
    }

    protected String as() {
        if (lexer.token() == Token.CONNECT) {
            return null;
        }

        return super.as();
    }

    private void parseHierachical(OracleSelectQueryBlock queryBlock) {
        OracleSelectHierachicalQueryClause hierachical = null;

        if (lexer.token() == Token.CONNECT) {
            hierachical = new OracleSelectHierachicalQueryClause();
            lexer.nextToken();
            accept(Token.BY);

            if (lexer.token() == Token.PRIOR) {
                lexer.nextToken();
                hierachical.setPrior(true);
            }

            if (identifierEquals("NOCYCLE")) {
                hierachical.setNoCycle(true);
                lexer.nextToken();

                if (lexer.token() == Token.PRIOR) {
                    lexer.nextToken();
                    hierachical.setPrior(true);
                }
            }
            hierachical.setConnectBy(this.exprParser.expr());
        }

        if (lexer.token() == Token.START) {
            lexer.nextToken();
            if (hierachical == null) {
                hierachical = new OracleSelectHierachicalQueryClause();
            }
            accept(Token.WITH);

            hierachical.setStartWith(this.exprParser.expr());
        }

        if (lexer.token() == Token.CONNECT) {
            if (hierachical == null) {
                hierachical = new OracleSelectHierachicalQueryClause();
            }

            lexer.nextToken();
            accept(Token.BY);

            if (lexer.token() == Token.PRIOR) {
                lexer.nextToken();
                hierachical.setPrior(true);
            }

            if (identifierEquals("NOCYCLE")) {
                hierachical.setNoCycle(true);
                lexer.nextToken();

                if (lexer.token() == Token.PRIOR) {
                    lexer.nextToken();
                    hierachical.setPrior(true);
                }
            }
            hierachical.setConnectBy(this.exprParser.expr());
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
            if (lexer.token() == Token.SELECT || lexer.token() == Token.WITH) {
                tableSource = new OracleSelectSubqueryTableSource(select());
            } else if (lexer.token() == (Token.LPAREN)) {
                tableSource = new OracleSelectSubqueryTableSource(select());
            } else {
                throw new ParserException("TODO :" + lexer.token());
            }
            accept(Token.RPAREN);

            parsePivot((OracleSelectTableSource) tableSource);

            return parseTableSourceRest(tableSource);
        }

        if (lexer.token() == (Token.SELECT)) {
            throw new ParserException("TODO");
        }

        OracleSelectTableReference tableReference = new OracleSelectTableReference();

        if (identifierEquals("ONLY")) {
            lexer.nextToken();
            tableReference.setOnly(true);
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
        tableReference.setExpr(this.exprParser.expr());

        {
            FlashbackQueryClause clause = flashback();
            tableReference.setFlashback(clause);
        }

        if (identifierEquals("SAMPLE")) {
            lexer.nextToken();

            SampleClause sample = new SampleClause();

            if (identifierEquals("BLOCK")) {
                sample.setBlock(true);
                lexer.nextToken();
            }

            accept(Token.LPAREN);
            this.exprParser.exprList(sample.getPercent(), sample);
            accept(Token.RPAREN);

            if (identifierEquals("SEED")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                sample.setSeedValue(expr());
                accept(Token.RPAREN);
            }

            tableReference.setSampleClause(sample);
        }

        if (identifierEquals("PARTITION")) {
            lexer.nextToken();
            PartitionExtensionClause partition = new PartitionExtensionClause();

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

        if (identifierEquals("SUBPARTITION")) {
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

        if (identifierEquals("VERSIONS")) {
            lexer.nextToken();

            if (lexer.token() == Token.BETWEEN) {
                lexer.nextToken();

                VersionsFlashbackQueryClause clause = new VersionsFlashbackQueryClause();
                if (identifierEquals("SCN")) {
                    clause.setType(AsOfFlashbackQueryClause.Type.SCN);
                    lexer.nextToken();
                } else {
                    acceptIdentifier("TIMESTAMP");
                    clause.setType(AsOfFlashbackQueryClause.Type.TIMESTAMP);
                }

                SQLBinaryOpExpr binaryExpr = (SQLBinaryOpExpr) exprParser.expr();
                if (binaryExpr.getOperator() != SQLBinaryOperator.BooleanAnd) {
                    throw new ParserException("syntax error : " + binaryExpr.getOperator());
                }

                clause.setBegin(binaryExpr.getLeft());
                clause.setEnd(binaryExpr.getRight());

                tableReference.setFlashback(clause);
            } else {
                throw new ParserException("TODO");
            }
        }

    }

    private FlashbackQueryClause flashback() {
        if (lexer.token() == Token.AS) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.OF) {
            lexer.nextToken();

            if (identifierEquals("SCN")) {
                AsOfFlashbackQueryClause clause = new AsOfFlashbackQueryClause();
                clause.setType(AsOfFlashbackQueryClause.Type.SCN);
                lexer.nextToken();
                clause.setExpr(exprParser.expr());

                return clause;
            } else if (identifierEquals("SNAPSHOT")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                AsOfSnapshotClause clause = new AsOfSnapshotClause();
                clause.setExpr(this.expr());
                accept(Token.RPAREN);

                return clause;
            } else {
                AsOfFlashbackQueryClause clause = new AsOfFlashbackQueryClause();
                acceptIdentifier("TIMESTAMP");
                clause.setType(AsOfFlashbackQueryClause.Type.TIMESTAMP);
                clause.setExpr(exprParser.expr());

                return clause;
            }

        }

        return null;
    }

    protected SQLTableSource parseTableSourceRest(OracleSelectTableSource tableSource) {
        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            if (lexer.token() == Token.OF) {
                tableSource.setFlashback(flashback());
            }

            tableSource.setAlias(as());
        } else if ((tableSource.getAlias() == null) || (tableSource.getAlias().length() == 0)) {
            if (lexer.token() != Token.LEFT && lexer.token() != Token.RIGHT && lexer.token() != Token.FULL) {
                tableSource.setAlias(as());
            }
        }

        if (lexer.token() == Token.HINT) {
            this.exprParser.parseHints(tableSource.getHints());
        }

        OracleSelectJoin.JoinType joinType = null;

        if (lexer.token() == Token.LEFT) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = OracleSelectJoin.JoinType.LEFT_OUTER_JOIN;
        }

        if (lexer.token() == Token.RIGHT) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = OracleSelectJoin.JoinType.RIGHT_OUTER_JOIN;
        }

        if (lexer.token() == Token.FULL) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = OracleSelectJoin.JoinType.FULL_OUTER_JOIN;
        }

        if (lexer.token() == Token.INNER) {
            lexer.nextToken();
            accept(Token.JOIN);
            joinType = OracleSelectJoin.JoinType.INNER_JOIN;
        }
        if (lexer.token() == Token.CROSS) {
            lexer.nextToken();
            accept(Token.JOIN);
            joinType = OracleSelectJoin.JoinType.CROSS_JOIN;
        }

        if (lexer.token() == Token.JOIN) {
            lexer.nextToken();
            joinType = OracleSelectJoin.JoinType.JOIN;
        }

        if (lexer.token() == (Token.COMMA)) {
            lexer.nextToken();
            joinType = OracleSelectJoin.JoinType.COMMA;
        }

        if (joinType != null) {
            OracleSelectJoin join = new OracleSelectJoin();
            join.setLeft(tableSource);
            join.setJoinType(joinType);
            join.setRight(parseTableSource());

            if (lexer.token() == Token.ON) {
                lexer.nextToken();
                join.setCondition(this.exprParser.expr());
            } else if (lexer.token() == Token.USING) {
                lexer.nextToken();
                accept(Token.LPAREN);
                this.exprParser.exprList(join.getUsing(), join);
                accept(Token.RPAREN);
            }

            return parseTableSourceRest(join);
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
                item.setExpr((SQLAggregateExpr) this.exprParser.expr());
                item.setAlias(as());
                pivot.getItems().add(item);

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
            if (lexer.token() == (Token.LPAREN)) {
                throw new ParserException("TODO");
            }

            if (lexer.token() == (Token.SELECT)) {
                throw new ParserException("TODO");
            }

            for (;;) {
                item = new OracleSelectPivot.Item();
                item.setExpr(this.exprParser.expr());
                item.setAlias(as());
                pivot.getPivotIn().add(item);

                if (lexer.token() != Token.COMMA) {
                    break;
                }

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
                this.exprParser.exprList(unPivot.getItems(), unPivot);
                accept(Token.RPAREN);
            } else {
                unPivot.getItems().add(this.exprParser.expr());
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
                throw new ParserException("TODO");
            }

            if (lexer.token() == (Token.SELECT)) {
                throw new ParserException("TODO");
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
            SQLExpr expr = expr();
            if (lexer.token() != Token.COMMA) {
                x.setInto(expr);
                return;
            }
            SQLListExpr list = new SQLListExpr();
            list.getItems().add(expr);
            while (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                list.getItems().add(expr());
            }
            x.setInto(list);
        }
    }

    private void parseHints(OracleSelectQueryBlock queryBlock) {
        this.exprParser.parseHints(queryBlock.getHints());
    }
}
