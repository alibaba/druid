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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLPartitionOf;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.template.SQLSelectQueryTemplate;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

import static com.alibaba.druid.sql.parser.DialectFeature.ParserFeature.CreateTableBodySupplemental;
import static com.alibaba.druid.sql.parser.SQLParserFeature.Template;

public class SQLCreateTableParser extends SQLDDLParser {
    public SQLCreateTableParser(String sql) {
        super(sql);
    }

    public SQLCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = exprParser.dbType;
    }

    public SQLCreateTableStatement parseCreateTable() {
        List<String> comments = null;
        if (lexer.isKeepComments() && lexer.hasComment()) {
            comments = lexer.readAndResetComments();
        }

        SQLCreateTableStatement createTable = newCreateStatement();
        if (comments != null) {
            createTable.addBeforeComment(comments);
        }

        createTable.setDbType(dbType);

        if (lexer.hasComment() && lexer.isKeepComments()) {
            createTable.addBeforeComment(lexer.readAndResetComments());
        }

        accept(Token.CREATE);
        createTableBefore(createTable);
        if (lexer.token == Token.TABLE || lexer.identifierEquals(FnvHash.Constants.TABLE)) {
            lexer.nextToken();
        } else {
            setErrorEndPos(lexer.pos());
            printError(lexer.token);
        }
        createTableBeforeName(createTable);
        createTable.setName(
                this.exprParser.name());
        createTableAfterName(createTable);
        createTableBody(createTable);
        parseCreateTableRest(createTable);
        createTableQuery(createTable);

        return createTable;
    }

    protected SQLSelect createTableQueryRest() {
        return this.createSQLSelectParser().select();
    }

    protected void createTableQuery(SQLCreateTableStatement createTable) {
        if (lexer.nextIf(Token.AS)) {
            SQLSelect select;
            if ((lexer.token == Token.IDENTIFIER || lexer.token == Token.VARIANT)
                    && lexer.isEnabled(Template)
                    && lexer.stringVal.startsWith("$")) {
                select = new SQLSelect(
                        new SQLSelectQueryTemplate(lexer.stringVal));
                lexer.nextToken();
            } else {
                select = createTableQueryRest();
            }
            createTable.setSelect(select);
        }
    }

    protected void createTableBody(SQLCreateTableStatement createTable) {
        if (lexer.nextIf(Token.LPAREN)) {
            for (; ; ) {
                createTableBodyItem(createTable);
                if (lexer.nextIf(Token.COMMA)) {
                    if (lexer.token == Token.RPAREN) { // compatible for sql server
                        break;
                    }
                    continue;
                }

                break;
            }

            accept(Token.RPAREN);

            createTableAfter(createTable);
        }
    }

    protected void createTableBodyItem(SQLCreateTableStatement createTable) {
        Token token = lexer.token;
        if (lexer.identifierEquals(FnvHash.Constants.SUPPLEMENTAL)
                && dialectFeatureEnabled(CreateTableBodySupplemental)) {
            SQLTableElement element = this.parseCreateTableSupplementalLoggingProps();
            element.setParent(createTable);
            createTable.getTableElementList().add(element);
        } else if (token == Token.IDENTIFIER //
                || token == Token.LITERAL_ALIAS) {
            SQLColumnDefinition column = this.exprParser.parseColumn(createTable);
            column.setParent(createTable);
            createTable.getTableElementList().add(column);
        } else if (token == Token.PRIMARY //
                || token == Token.UNIQUE //
                || token == Token.CHECK //
                || token == Token.CONSTRAINT
                || token == Token.FOREIGN) {
            SQLConstraint constraint = this.exprParser.parseConstraint();
            constraint.setParent(createTable);
            createTable.getTableElementList().add((SQLTableElement) constraint);
        } else if (token == Token.TABLESPACE) {
            throw new ParserException("TODO " + lexer.info());
        } else if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLTableLike tableLike = new SQLTableLike();
            tableLike.setTable(new SQLExprTableSource(this.exprParser.name()));
            tableLike.setParent(createTable);
            createTable.getTableElementList().add(tableLike);

            if (lexer.identifierEquals(FnvHash.Constants.INCLUDING)) {
                lexer.nextToken();
                acceptIdentifier("PROPERTIES");
                tableLike.setIncludeProperties(true);
            } else if (lexer.identifierEquals(FnvHash.Constants.EXCLUDING)) {
                lexer.nextToken();
                acceptIdentifier("PROPERTIES");
                tableLike.setExcludeProperties(true);
            }
        } else if (lexer.token() == Token.INDEX) {
            parseIndex(createTable);
        } else {
            SQLColumnDefinition column = this.exprParser.parseColumn();
            createTable.getTableElementList().add(column);
        }
    }

    protected void parseIndex(SQLCreateTableStatement createTable) {
        SQLColumnDefinition column = this.exprParser.parseColumn();
        createTable.getTableElementList().add(column);
    }

    protected void createTableBefore(SQLCreateTableStatement createTable) {
        if (lexer.nextIfIdentifier("GLOBAL")) {
            createTable.config(SQLCreateTableStatement.Feature.Global);
            if (lexer.nextIfIdentifier("TEMPORARY") || lexer.nextIfIdentifier("TEMP")) {
                createTable.config(SQLCreateTableStatement.Feature.Temporary);
            } else {
                throw new ParserException("syntax error " + lexer.info());
            }
        } else if (lexer.nextIfIdentifier("LOCAL") || lexer.nextIf(Token.LOCAL)) {
            createTable.config(SQLCreateTableStatement.Feature.Local);
            if (lexer.nextIfIdentifier("TEMPORARY") || lexer.nextIfIdentifier("TEMP")) {
                createTable.config(SQLCreateTableStatement.Feature.Temporary);
            } else {
                throw new ParserException("syntax error. " + lexer.info());
            }
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.DIMENSION)) {
            createTable.config(SQLCreateTableStatement.Feature.Dimension);
        }
    }

    protected void createTableBeforeName(SQLCreateTableStatement createTable) {
        if (lexer.nextIf(Token.IF) || lexer.nextIfIdentifier("IF")) {
            accept(Token.NOT);
            accept(Token.EXISTS);

            createTable.setIfNotExists(true);
        }
    }

    protected void createTableAfterName(SQLCreateTableStatement createTable) {}

    protected void createTableAfter(SQLCreateTableStatement stmt) {
        if (lexer.nextIfIdentifier(FnvHash.Constants.INHERITS)) {
            accept(Token.LPAREN);
            SQLName inherits = this.exprParser.name();
            stmt.setInherits(new SQLExprTableSource(inherits));
            accept(Token.RPAREN);
        }
    }

    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        // For partition of for PG
        if (lexer.token() == Token.PARTITION) {
            Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();
            if (Token.OF.equals(lexer.token())) {
                lexer.reset(mark);
                SQLPartitionOf partitionOf = parsePartitionOf();
                stmt.setPartitionOf(partitionOf);
            } else if (Token.BY.equals(lexer.token())) {
                lexer.reset(mark);
                SQLPartitionBy partitionClause = parsePartitionBy();
                stmt.setPartitionBy(partitionClause);
            }
        }
        // For partition by
        if (lexer.token() == Token.PARTITION) {
            Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();
            if (Token.OF.equals(lexer.token())) {
                lexer.reset(mark);
                SQLPartitionOf partitionOf = parsePartitionOf();
                stmt.setPartitionOf(partitionOf);
            } else if (Token.BY.equals(lexer.token())) {
                lexer.reset(mark);
                SQLPartitionBy partitionClause = parsePartitionBy();
                stmt.setPartitionBy(partitionClause);
            }
        }

        if (lexer.nextIf(Token.TABLESPACE)) {
            stmt.setTablespace(
                    this.exprParser.name()
            );
        }
    }

    public SQLPartitionBy parsePartitionBy() {
        return null;
    }

    public SQLPartitionOf parsePartitionOf() {
        return null;
    }

    protected SQLTableElement parseCreateTableSupplementalLoggingProps() {
        throw new ParserException("TODO " + lexer.info());
    }

    protected SQLCreateTableStatement newCreateStatement() {
        return new SQLCreateTableStatement(getDbType());
    }

    protected void parseOptions(SQLCreateTableStatement stmt) {
        lexer.nextToken();
        accept(Token.LPAREN);

        for (; ; ) {
            String name = lexer.stringVal();
            lexer.nextToken();
            accept(Token.EQ);
            SQLExpr value = this.exprParser.primary();
            stmt.addOption(name, value);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                continue;
            }
            break;
        }

        accept(Token.RPAREN);
    }
}
