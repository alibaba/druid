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
package com.alibaba.druid.sql.dialect.odps.parser;

import com.alibaba.druid.sql.ast.ClusteringType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInputOutputFormat;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class OdpsCreateTableParser extends SQLCreateTableParser {
    public OdpsCreateTableParser(String sql) {
        super(new OdpsExprParser(sql));
    }

    public OdpsCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SQLCreateTableStatement parseCreateTable(boolean acceptCreate) {
        OdpsCreateTableStatement stmt = new OdpsCreateTableStatement();

        if (acceptCreate) {
            accept(Token.CREATE);
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            lexer.nextToken();
            stmt.setExternal(true);
        }

        accept(Token.TABLE);

        if (lexer.token() == Token.IF || lexer.identifierEquals("IF")) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);

            stmt.setIfNotExiists(true);
        }

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            stmt.setComment(this.exprParser.primary());
        }

        if (lexer.token() == Token.SEMI || lexer.token() == Token.EOF) {
            return stmt;
        }

        for (; ; ) {
            if (lexer.identifierEquals(FnvHash.Constants.TBLPROPERTIES)) {
                parseOptions(stmt);

                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.LIFECYCLE)) {
                lexer.nextToken();
                stmt.setLifeCycle(this.exprParser.expr());

                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.STORED)) {
                lexer.nextToken();
                accept(Token.AS);

                if (lexer.identifierEquals(FnvHash.Constants.INPUTFORMAT)) {
                    HiveInputOutputFormat format = new HiveInputOutputFormat();
                    lexer.nextToken();
                    format.setInput(this.exprParser.primary());

                    if (lexer.identifierEquals(FnvHash.Constants.OUTPUTFORMAT)) {
                        lexer.nextToken();
                        format.setOutput(this.exprParser.primary());
                    }
                    stmt.setStoredAs(format);
                } else {
                    SQLName name = this.exprParser.name();
                    stmt.setStoredAs(name);
                }

                continue;
            }

            break;
        }

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLName name = this.exprParser.name();
            stmt.setLike(name);
        } else if (lexer.token() == Token.AS) {
            lexer.nextToken();

            OdpsSelectParser selectParser = new OdpsSelectParser(this.exprParser);
            SQLSelect select = selectParser.select();

            stmt.setSelect(select);
        } else if (lexer.token() != Token.LPAREN && stmt.isExternal()) {
            // skip
        } else {
            accept(Token.LPAREN);

            if (lexer.isKeepComments() && lexer.hasComment()) {
                stmt.addBodyBeforeComment(lexer.readAndResetComments());
            }

            for (; ; ) {
                SQLColumnDefinition column;
                switch (lexer.token()) {
                    case IDENTIFIER:
                    case KEY:
                    case SEQUENCE:
                    case USER:
                    case GROUP:
                    case INDEX:
                    case ENABLE:
                    case DISABLE:
                    case DESC:
                    case ALL:
                    case INTERVAL:
                    case OPEN:
                    case PARTITION:
                    case SCHEMA:
                    case CONSTRAINT:
                    case COMMENT:
                    case VIEW:
                    case SHOW:
                    case ORDER:
                    case LEAVE:
                    case UNIQUE:
                    case DEFAULT:
                    case EXPLAIN:
                    case CHECK:
                    case CLOSE:
                    case IN:
                    case OUT:
                    case INOUT:
                    case LIMIT:
                    case FULL:
                    case MINUS:
                    case VALUES:
                    case TRIGGER:
                    case USE:
                    case LIKE:
                    case DISTRIBUTE:
                    case DELETE:
                    case UPDATE:
                    case IS:
                    case LEFT:
                    case RIGHT:
                    case REPEAT:
                    case COMPUTE:
                    case LOCK:
                    case TABLE:
                    case DO:
                    case WHILE:
                    case LOOP:
                    case FOR:
                    case RLIKE:
                    case PROCEDURE:
                    case GRANT:
                    case EXCEPT:
                    case CREATE:
                    case PARTITIONED:
                    case UNION:
                    case PRIMARY:
                    case INNER:
                    case TO:
                    case DECLARE:
                    case REFERENCES:
                    case FOREIGN:
                    case ESCAPE:
                    case BY:
                    case ALTER:
                    case SOME:
                    case ASC:
                    case NULL:
                    case CURSOR:
                    case FETCH:
                    case OVER:
                    case DATABASE:
                        column = this.exprParser.parseColumn(stmt);
                        break;
                    default:
                        throw new ParserException("expect identifier. " + lexer.info());
                }

                stmt.addColumn(column);

                if (lexer.isKeepComments() && lexer.hasComment()) {
                    column.addAfterComment(lexer.readAndResetComments());
                }

                if (!(lexer.token() == (Token.COMMA))) {
                    break;
                } else {
                    lexer.nextToken();

                    if (lexer.isKeepComments() && lexer.hasComment()) {
                        column.addAfterComment(lexer.readAndResetComments());
                    }
                }
            }
            accept(Token.RPAREN);
        }

        for (; ; ) {
            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                stmt.setComment(this.exprParser.primary());
                continue;
            }

            if (lexer.token() == Token.PARTITIONED) {
                lexer.nextToken();
                accept(Token.BY);
                accept(Token.LPAREN);

                for (; ; ) {
                    switch (lexer.token()) {
                        case INDEX:
                        case KEY:
                        case CHECK:
                        case IDENTIFIER:
                        case GROUP:
                        case INTERVAL:
                        case LOOP:
                        case USER:
                        case TABLE:
                        case PARTITION:
                        case SEQUENCE:
                        case VARIANT:
                            break;
                        default:
                            throw new ParserException("expect identifier. " + lexer.info());
                    }

                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    stmt.addPartitionColumn(column);

                    if (lexer.isKeepComments() && lexer.hasComment()) {
                        column.addAfterComment(lexer.readAndResetComments());
                    }

                    if (lexer.token() != Token.COMMA) {
                        break;
                    } else {
                        lexer.nextToken();
                        if (lexer.isKeepComments() && lexer.hasComment()) {
                            column.addAfterComment(lexer.readAndResetComments());
                        }
                    }
                }

                accept(Token.RPAREN);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.RANGE)) {
                lexer.nextToken();
                if (lexer.identifierEquals(FnvHash.Constants.CLUSTERED)) {
                    stmt.setClusteringType(ClusteringType.Range);
                }

                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.CLUSTERED)) {
                lexer.nextToken();
                accept(Token.BY);
                accept(Token.LPAREN);
                for (; ; ) {
                    SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
                    stmt.addClusteredByItem(item);
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);

                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.ROW)) {
                SQLExternalRecordFormat recordFormat = this.exprParser.parseRowFormat();
                stmt.setRowFormat(recordFormat);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.SORTED)) {
                lexer.nextToken();
                accept(Token.BY);
                accept(Token.LPAREN);
                for (; ; ) {
                    SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
                    stmt.addSortedByItem(item);
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);

                continue;
            }

            if (stmt.getClusteringType() != ClusteringType.Range &&
                    (stmt.getClusteredBy().size() > 0 || stmt.getSortedBy().size() > 0) && lexer.token() == Token.INTO) {
                lexer.nextToken();
                if (lexer.token() == Token.LITERAL_INT) {
                    stmt.setBuckets(lexer.integerValue().intValue());
                    lexer.nextToken();
                } else {
                    throw new ParserException("into buckets must be integer. " + lexer.info());
                }
                acceptIdentifier("BUCKETS");

                if (lexer.token() == Token.INTO) {
                    lexer.nextToken();

                    if (lexer.token() == Token.LITERAL_INT) {
                        stmt.setShards(lexer.integerValue().intValue());
                        lexer.nextToken();
                    } else {
                        throw new ParserException("into shards must be integer. " + lexer.info());
                    }

                    acceptIdentifier("SHARDS");
                }

                continue;
            }

            if (lexer.token() == Token.INTO) {
                lexer.nextToken();

                if (lexer.token() == Token.LITERAL_INT) {
                    stmt.setIntoBuckets(
                            new SQLIntegerExpr(lexer.integerValue().intValue()));
                    lexer.nextToken();
                    acceptIdentifier("BUCKETS");
                } else {
                    throw new ParserException("into shards must be integer. " + lexer.info());
                }
                continue;
            }

            if (lexer.token() == Token.AS && stmt.getSelect() == null) {
                lexer.nextToken();

                OdpsSelectParser selectParser = new OdpsSelectParser(this.exprParser);
                SQLSelect select = selectParser.select();

                stmt.setSelect(select);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.LIFECYCLE)) {
                lexer.nextToken();
                stmt.setLifeCycle(this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.STORED)) {
                lexer.nextToken();
                if (lexer.token() == Token.AS) {
                    lexer.nextToken();

                    if (lexer.identifierEquals(FnvHash.Constants.INPUTFORMAT)) {
                        HiveInputOutputFormat format = new HiveInputOutputFormat();
                        lexer.nextToken();
                        format.setInput(this.exprParser.primary());

                        if (lexer.identifierEquals(FnvHash.Constants.OUTPUTFORMAT)) {
                            lexer.nextToken();
                            format.setOutput(this.exprParser.primary());
                        }
                        stmt.setStoredAs(format);
                    } else {
                        SQLName storedAs = this.exprParser.name();
                        stmt.setStoredAs(storedAs);
                    }
                } else {
                    accept(Token.BY);
                    SQLExpr storedBy = this.exprParser.expr();
                    stmt.setStoredBy(storedBy);
                }
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.LIFECYCLE)) {
                lexer.nextToken();
                stmt.setLifeCycle(this.exprParser.expr());
                continue;
            }

            if (lexer.token() == Token.WITH) {
                lexer.nextToken();
                acceptIdentifier("SERDEPROPERTIES");
                accept(Token.LPAREN);
                this.exprParser.exprList(stmt.getWithSerdeproperties(), stmt);
                accept(Token.RPAREN);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.TBLPROPERTIES)) {
                parseOptions(stmt);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.LOCATION)) {
                lexer.nextToken();
                SQLExpr location = this.exprParser.expr();
                stmt.setLocation(location);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.TBLPROPERTIES)) {
                parseOptions(stmt);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.USING)) {
                lexer.nextToken();
                SQLExpr using = this.exprParser.expr();
                stmt.setUsing(using);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.LIFECYCLE)) {
                lexer.nextToken();
                stmt.setLifeCycle(this.exprParser.expr());
                continue;
            }

            break;
        }

        return stmt;
    }
}
