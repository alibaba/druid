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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLDataTypeRefExpr;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.ast.SQLPartition;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLPartitionValue;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAggregateOption;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllExpr;
import com.alibaba.druid.sql.ast.expr.SQLAnyExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExprGroup;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLContainsExpr;
import com.alibaba.druid.sql.ast.expr.SQLCurrentOfCursorExpr;
import com.alibaba.druid.sql.ast.expr.SQLDateExpr;
import com.alibaba.druid.sql.ast.expr.SQLDefaultExpr;
import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.ast.expr.SQLGroupingSetExpr;
import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLSequenceExpr;
import com.alibaba.druid.sql.ast.expr.SQLSomeExpr;
import com.alibaba.druid.sql.ast.expr.SQLTimestampExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;
import com.alibaba.druid.sql.ast.statement.SQLCheck;
import com.alibaba.druid.sql.ast.statement.SQLColumnCheck;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLColumnPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLColumnReference;
import com.alibaba.druid.sql.ast.statement.SQLColumnUniqueKey;
import com.alibaba.druid.sql.ast.statement.SQLConstraint;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyConstraint;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLUnique;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleArgumentExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGTypeCastExpr;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;

public class SQLExprParser extends SQLParser {

    public final static String[] AGGREGATE_FUNCTIONS;

    public final static long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = { "AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM" };
        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    protected String[]           aggregateFunctions  = AGGREGATE_FUNCTIONS;

    protected long[]             aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;

    protected boolean allowIdentifierMethod = true;

    public SQLExprParser(String sql){
        super(sql);
    }

    public SQLExprParser(String sql, String dbType){
        super(sql, dbType);
    }

    public SQLExprParser(Lexer lexer){
        super(lexer);
    }

    public SQLExprParser(Lexer lexer, String dbType){
        super(lexer, dbType);
    }

    public SQLExpr expr() {
        if (lexer.token == Token.STAR) {
            lexer.nextToken();

            SQLExpr expr = new SQLAllColumnExpr();

            if (lexer.token == Token.DOT) {
                lexer.nextToken();
                accept(Token.STAR);
                return new SQLPropertyExpr(expr, "*");
            }

            return expr;
        }

        SQLExpr expr = primary();

        Token token = lexer.token;
        if (token == Token.COMMA) {
            return expr;
        } else if (token == Token.EQ) {
            expr = relationalRest(expr);
            expr = andRest(expr);
            expr = xorRest(expr);
            expr = orRest(expr);
            return expr;
        } else {
            return exprRest(expr);
        }
    }

    public SQLExpr exprRest(SQLExpr expr) {
        expr = bitXorRest(expr);
        expr = multiplicativeRest(expr);
        expr = additiveRest(expr);
        expr = shiftRest(expr);
        expr = bitAndRest(expr);
        expr = bitOrRest(expr);
        expr = inRest(expr);
        expr = relationalRest(expr);
//        expr = equalityRest(expr);
        expr = andRest(expr);
        expr = xorRest(expr);
        expr = orRest(expr);

        return expr;
    }

    public final SQLExpr bitXor() {
        SQLExpr expr = primary();
        return bitXorRest(expr);
    }

    public SQLExpr bitXorRest(SQLExpr expr) {
        Token token = lexer.token;
        switch (token) {
            case CARET: {
                lexer.nextToken();
                SQLBinaryOperator op;
                if (lexer.token == Token.EQ) {
                    lexer.nextToken();
                    op = SQLBinaryOperator.BitwiseXorEQ;
                } else {
                    op = SQLBinaryOperator.BitwiseXor;
                }
                SQLExpr rightExp = primary();
                expr = new SQLBinaryOpExpr(expr, op, rightExp, dbType);
                expr = bitXorRest(expr);
                break;
            }
            case SUBGT:{
                lexer.nextToken();
                SQLExpr rightExp = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.SubGt, rightExp, dbType);
                expr = bitXorRest(expr);
                break;
            }
            case LT_SUB_GT: {
                lexer.nextToken();
                SQLExpr rightExp = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.PG_ST_DISTANCE, rightExp, dbType);
                expr = bitXorRest(expr);
                break;
            }
            case SUBGTGT:{
                lexer.nextToken();
                SQLExpr rightExp = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.SubGtGt, rightExp, dbType);
                expr = bitXorRest(expr);
                break;
            }
            case POUNDGT: {
                lexer.nextToken();
                SQLExpr rightExp = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.PoundGt, rightExp, dbType);
                expr = bitXorRest(expr);
                break;
            }
            case POUNDGTGT: {
                lexer.nextToken();
                SQLExpr rightExp = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.PoundGtGt, rightExp, dbType);
                expr = bitXorRest(expr);
                break;
            }
            case QUESQUES: {
                lexer.nextToken();
                SQLExpr rightExp = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.QuesQues, rightExp, dbType);
                expr = bitXorRest(expr);
                break;
            }
            case QUESBAR: {
                lexer.nextToken();
                SQLExpr rightExp = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.QuesBar, rightExp, dbType);
                expr = bitXorRest(expr);
                break;
            }
            case QUESAMP: {
                lexer.nextToken();
                SQLExpr rightExp = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.QuesAmp, rightExp, dbType);
                expr = bitXorRest(expr);
                break;
            }

            default:
                break;
        }


        return expr;
    }

    public final SQLExpr multiplicative() {
        SQLExpr expr = bitXor();
        return multiplicativeRest(expr);
    }

    public SQLExpr multiplicativeRest(SQLExpr expr) {
        final Token token = lexer.token;
        if (token == Token.STAR) {
            lexer.nextToken();
            SQLExpr rightExp = bitXor();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Multiply, rightExp, getDbType());
            expr = multiplicativeRest(expr);
        } else if (token == Token.SLASH) {
            lexer.nextToken();
            SQLExpr rightExp = bitXor();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Divide, rightExp, getDbType());
            expr = multiplicativeRest(expr);
        } else if (token == Token.PERCENT) {
            lexer.nextToken();
            SQLExpr rightExp = bitXor();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Modulus, rightExp, getDbType());
            expr = multiplicativeRest(expr);
        } else if (token == Token.DIV) {
            lexer.nextToken();
            SQLExpr rightExp = bitXor();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.DIV, rightExp, getDbType());
            expr = multiplicativeRest(expr);
        } else if (lexer.identifierEquals(FnvHash.Constants.MOD) || lexer.token == Token.MOD) {
            Lexer.SavePoint savePoint = lexer.mark();
            lexer.nextToken();

            if (lexer.token == Token.COMMA || lexer.token == Token.EOF) {
                lexer.reset(savePoint);
                return expr;
            }

            SQLExpr rightExp = bitXor();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Modulus, rightExp, dbType);

            expr = multiplicativeRest(expr);
        }
        return expr;
    }
    
    public SQLIntegerExpr integerExpr() {
        SQLIntegerExpr intExpr = new SQLIntegerExpr(lexer.integerValue());
        accept(Token.LITERAL_INT);
        return intExpr;
    }

    public int parseIntValue() {
        if (lexer.token == Token.LITERAL_INT) {
            Number number = this.lexer.integerValue();
            int intVal = ((Integer) number).intValue();
            lexer.nextToken();
            return intVal;
        } else {
            throw new ParserException("not int. " + lexer.info());
        }
    }

    public SQLExpr primary() {
        List<String> beforeComments = null;
        if (lexer.isKeepComments() && lexer.hasComment()) {
            beforeComments = lexer.readAndResetComments();
        }

        SQLExpr sqlExpr = null;

        final Token tok = lexer.token;

        switch (tok) {
            case LPAREN:
                lexer.nextToken();
 
                sqlExpr = expr();
                if (lexer.token == Token.COMMA) {
                    SQLListExpr listExpr = new SQLListExpr();
                    listExpr.addItem(sqlExpr);
                    do {
                        lexer.nextToken();
                        listExpr.addItem(expr());
                    } while (lexer.token == Token.COMMA);

                    sqlExpr = listExpr;
                }

                if (sqlExpr instanceof SQLBinaryOpExpr) {
                    ((SQLBinaryOpExpr) sqlExpr).setBracket(true);
                }

                accept(Token.RPAREN);

                if (lexer.token == Token.UNION && sqlExpr instanceof SQLQueryExpr) {
                    SQLQueryExpr queryExpr = (SQLQueryExpr) sqlExpr;

                    SQLSelectQuery query = this.createSelectParser().queryRest(queryExpr.getSubQuery().getQuery());
                    queryExpr.getSubQuery().setQuery(query);
                }

                //accept(Token.RPAREN) 在处理UNION 之前，导致 以下类型的语句解析异常
                //select * from test_a where id=(select id from test_b where id = 1) union all select * from test_c .....
                //会解析为 select * from select * from test_a where id=(select id from test_b where id = 1 union all select * from test_c .....
                //所以需要将accept(Token.RPAREN) 移动到 处理 Union all 以及 子查询之后


                break;
            case INSERT:
                lexer.nextToken();
                if (lexer.token != Token.LPAREN) {
                    throw new ParserException("syntax error. " + lexer.info());
                }
                sqlExpr = new SQLIdentifierExpr("INSERT");
                break;
            case IDENTIFIER:
                String ident = lexer.stringVal();
                long hash_lower = lexer.hash_lower;
                lexer.nextToken();

                if (hash_lower == FnvHash.Constants.DATE
                        && (lexer.token == Token.LITERAL_CHARS || lexer.token == Token.VARIANT)
                        && (JdbcConstants.ORACLE.equals(dbType)
                            || JdbcConstants.POSTGRESQL.equals(dbType)
                            || JdbcConstants.MYSQL.equals(dbType))) {
                    SQLExpr literal = this.primary();
                    SQLDateExpr dateExpr = new SQLDateExpr();
                    dateExpr.setLiteral(literal);
                    sqlExpr = dateExpr;
                } else if (hash_lower == FnvHash.Constants.TIMESTAMP
                        && (lexer.token == Token.LITERAL_CHARS || lexer.token == Token.VARIANT)
                        && !JdbcConstants.ORACLE.equals(dbType)) {
                    SQLTimestampExpr dateExpr = new SQLTimestampExpr(lexer.stringVal());
                    lexer.nextToken();
                    sqlExpr = dateExpr;
                } else if (JdbcConstants.MYSQL.equalsIgnoreCase(dbType) && ident.startsWith("0x") && (ident.length() % 2) == 0) {
                    sqlExpr = new SQLHexExpr(ident.substring(2));
                } else {
                    if (ident.charAt(0) == '`') {
                        if (lexer.isEnabled(SQLParserFeature.IgnoreNameQuotes)) {
                            ident = ident.substring(1, ident.length() - 1);
                        }
                        hash_lower = FnvHash.hashCode64(ident);
                    }
                    sqlExpr = new SQLIdentifierExpr(ident, hash_lower);
                }
                break;
            case NEW:
                throw new ParserException("TODO " + lexer.info());
            case LITERAL_INT:
                sqlExpr = new SQLIntegerExpr(lexer.integerValue());
                lexer.nextToken();
                break;
            case LITERAL_FLOAT:
                sqlExpr = lexer.numberExpr();
                lexer.nextToken();
                break;
            case LITERAL_CHARS: {
                sqlExpr = new SQLCharExpr(lexer.stringVal());

                if (JdbcConstants.MYSQL.equals(dbType)) {
                    lexer.nextTokenValue();

                    for (; ; ) {
                        if (lexer.token == Token.LITERAL_ALIAS) {
                            String concat = ((SQLCharExpr) sqlExpr).getText();
                            concat += lexer.stringVal();
                            lexer.nextTokenValue();
                            sqlExpr = new SQLCharExpr(concat);
                        } else if (lexer.token == Token.LITERAL_CHARS || lexer.token == Token.LITERAL_NCHARS) {
                            String concat = ((SQLCharExpr) sqlExpr).getText();
                            concat += lexer.stringVal();
                            lexer.nextTokenValue();
                            sqlExpr = new SQLCharExpr(concat);
                        } else {
                            break;
                        }
                    }
                } else {
                    lexer.nextToken();
                }
                break;
            } case LITERAL_NCHARS:
                sqlExpr = new SQLNCharExpr(lexer.stringVal());
                lexer.nextToken();

                if (JdbcConstants.MYSQL.equals(dbType)) {
                    SQLMethodInvokeExpr concat = null;
                    for (; ; ) {
                        if (lexer.token == Token.LITERAL_ALIAS) {
                            if (concat == null) {
                                concat = new SQLMethodInvokeExpr("CONCAT");
                                concat.addParameter(sqlExpr);
                                sqlExpr = concat;
                            }
                            String alias = lexer.stringVal();
                            lexer.nextToken();
                            SQLCharExpr concat_right = new SQLCharExpr(alias.substring(1, alias.length() - 1));
                            concat.addParameter(concat_right);
                        } else if (lexer.token == Token.LITERAL_CHARS || lexer.token == Token.LITERAL_NCHARS) {
                            if (concat == null) {
                                concat = new SQLMethodInvokeExpr("CONCAT");
                                concat.addParameter(sqlExpr);
                                sqlExpr = concat;
                            }

                            String chars = lexer.stringVal();
                            lexer.nextToken();
                            SQLCharExpr concat_right = new SQLCharExpr(chars);
                            concat.addParameter(concat_right);
                        } else {
                            break;
                        }
                    }
                }
                break;
            case VARIANT: {
                String varName = lexer.stringVal();
                lexer.nextToken();

                if (varName.equals(":") && lexer.token == Token.IDENTIFIER && JdbcConstants.ORACLE.equals(dbType)) {
                    String part2 = lexer.stringVal();
                    lexer.nextToken();
                    varName += part2;
                }

                SQLVariantRefExpr varRefExpr = new SQLVariantRefExpr(varName);
                if (varName.startsWith(":")) {
                    varRefExpr.setIndex(lexer.nextVarIndex());
                }
                if (varRefExpr.getName().equals("@") && lexer.token == Token.LITERAL_CHARS) {
                    varRefExpr.setName("@'" + lexer.stringVal() + "'");
                    lexer.nextToken();
                } else if (varRefExpr.getName().equals("@@") && lexer.token == Token.LITERAL_CHARS) {
                    varRefExpr.setName("@@'" + lexer.stringVal() + "'");
                    lexer.nextToken();
                }
                sqlExpr = varRefExpr;
            }
                break;
            case DEFAULT:
                sqlExpr = new SQLDefaultExpr();
                lexer.nextToken();
                break;
            case DUAL:
            case KEY:
            case LIMIT:
            case SCHEMA:
            case COLUMN:
            case IF:
            case END:
            case COMMENT:
            case COMPUTE:
            case ENABLE:
            case DISABLE:
            case INITIALLY:
            case SEQUENCE:
            case USER:
            case EXPLAIN:
            case WITH:
            case GRANT:
            case REPLACE:
            case INDEX:
            case MODEL:
            case PCTFREE:
            case INITRANS:
            case MAXTRANS:
            case SEGMENT:
            case CREATION:
            case IMMEDIATE:
            case DEFERRED:
            case STORAGE:
            case NEXT:
            case MINEXTENTS:
            case MAXEXTENTS:
            case MAXSIZE:
            case PCTINCREASE:
            case FLASH_CACHE:
            case CELL_FLASH_CACHE:
            case NONE:
            case LOB:
            case STORE:
            case ROW:
            case CHUNK:
            case CACHE:
            case NOCACHE:
            case LOGGING:
            case NOCOMPRESS:
            case KEEP_DUPLICATES:
            case EXCEPTIONS:
            case PURGE:
            case FULL:
            case TO:
            case IDENTIFIED:
            case PASSWORD:
            case BINARY:
            case WINDOW:
            case OFFSET:
            case SHARE:
            case START:
            case CONNECT:
            case MATCHED:
            case ERRORS:
            case REJECT:
            case UNLIMITED:
            case BEGIN:
            case EXCLUSIVE:
            case MODE:
            case ADVISE:
            case VIEW:
            case ESCAPE:
            case OVER:
            case ORDER:
            case CONSTRAINT:
            case TYPE:
            case OPEN:
            case REPEAT:
            case TABLE:
            case TRUNCATE:
            case EXCEPTION:
            case FUNCTION:
            case IDENTITY:
            case EXTRACT:
            case DESC:
            case DO:
            case GROUP:
            case MOD:
            case CONCAT:
                sqlExpr = new SQLIdentifierExpr(lexer.stringVal());
                lexer.nextToken();
                break;
            case CASE:
                SQLCaseExpr caseExpr = new SQLCaseExpr();
                lexer.nextToken();
                if (lexer.token != Token.WHEN) {
                    caseExpr.setValueExpr(expr());
                }

                accept(Token.WHEN);
                SQLExpr testExpr = expr();
                accept(Token.THEN);
                SQLExpr valueExpr = expr();
                SQLCaseExpr.Item caseItem = new SQLCaseExpr.Item(testExpr, valueExpr);
                caseExpr.addItem(caseItem);

                while (lexer.token == Token.WHEN) {
                    lexer.nextToken();
                    testExpr = expr();
                    accept(Token.THEN);
                    valueExpr = expr();
                    caseItem = new SQLCaseExpr.Item(testExpr, valueExpr);
                    caseExpr.addItem(caseItem);
                }

                if (lexer.token == Token.ELSE) {
                    lexer.nextToken();
                    caseExpr.setElseExpr(expr());
                }

                accept(Token.END);

                sqlExpr = caseExpr;
                break;
            case EXISTS:
                lexer.nextToken();
                accept(Token.LPAREN);
                sqlExpr = new SQLExistsExpr(createSelectParser().select());
                accept(Token.RPAREN);
                break;
            case NOT:
                lexer.nextToken();
                if (lexer.token == Token.EXISTS) {
                    lexer.nextToken();
                    accept(Token.LPAREN);
                    sqlExpr = new SQLExistsExpr(createSelectParser().select(), true);
                    accept(Token.RPAREN);
                } else if (lexer.token == Token.LPAREN) {
                    lexer.nextToken();

                    SQLExpr notTarget = expr();

                    accept(Token.RPAREN);
                    notTarget = relationalRest(notTarget);
                    sqlExpr = new SQLNotExpr(notTarget);
                    
                    return primaryRest(sqlExpr);
                } else {
                    SQLExpr restExpr = relational();
                    sqlExpr = new SQLNotExpr(restExpr);
                }
                break;
            case SELECT:
                SQLQueryExpr queryExpr = new SQLQueryExpr(
                        createSelectParser()
                                .select());
                sqlExpr = queryExpr;
                break;
            case CAST:
                lexer.nextToken();
                accept(Token.LPAREN);
                SQLCastExpr cast = new SQLCastExpr();
                cast.setExpr(expr());
                accept(Token.AS);
                cast.setDataType(parseDataType(false));
                accept(Token.RPAREN);

                sqlExpr = cast;
                break;
            case SUB:
                lexer.nextToken();
                switch (lexer.token) {
                    case LITERAL_INT:
                        Number integerValue = lexer.integerValue();
                        if (integerValue instanceof Integer) {
                            int intVal = ((Integer) integerValue).intValue();
                            if (intVal == Integer.MIN_VALUE) {
                                integerValue = Long.valueOf(((long) intVal) * -1);
                            } else {
                                integerValue = Integer.valueOf(intVal * -1);
                            }
                        } else if (integerValue instanceof Long) {
                            long longVal = ((Long) integerValue).longValue();
                            if (longVal == 2147483648L) {
                                integerValue = Integer.valueOf((int) (((long) longVal) * -1));
                            } else {
                                integerValue = Long.valueOf(longVal * -1);
                            }
                        } else {
                            integerValue = ((BigInteger) integerValue).negate();
                        }
                        sqlExpr = new SQLIntegerExpr(integerValue);
                        lexer.nextToken();
                        break;
                    case LITERAL_FLOAT:
                        sqlExpr = lexer.numberExpr(true);
                        lexer.nextToken();
                        break;
                    case IDENTIFIER: // 当负号后面为字段的情况
                    case LITERAL_CHARS:
                    case LITERAL_ALIAS:
                        sqlExpr = new SQLIdentifierExpr(lexer.stringVal());
                        lexer.nextToken();

                        if (lexer.token == Token.LPAREN || lexer.token == Token.LBRACKET) {
                            sqlExpr = primaryRest(sqlExpr);
                        }
                        sqlExpr = new SQLUnaryExpr(SQLUnaryOperator.Negative, sqlExpr);

                        break;
                    case QUES: {
                        SQLVariantRefExpr variantRefExpr = new SQLVariantRefExpr("?");
                        variantRefExpr.setIndex(lexer.nextVarIndex());
                        sqlExpr = new SQLUnaryExpr(SQLUnaryOperator.Negative, variantRefExpr);
                        lexer.nextToken();
                        break;
                    }
                    case LPAREN:
                        lexer.nextToken();
                        sqlExpr = expr();
                        accept(Token.RPAREN);
                        sqlExpr = new SQLUnaryExpr(SQLUnaryOperator.Negative, sqlExpr);
                        break;
                    case BANG:
                    case CAST:
                        sqlExpr = expr();
                        sqlExpr = new SQLUnaryExpr(SQLUnaryOperator.Negative, sqlExpr);
                        break;
                    default:
                        throw new ParserException("TODO : " + lexer.info());
                }
                break;
            case PLUS:
                lexer.nextToken();
                switch (lexer.token) {
                    case LITERAL_INT:
                        sqlExpr = new SQLIntegerExpr(lexer.integerValue());
                        lexer.nextToken();
                        break;
                    case LITERAL_FLOAT:
                        sqlExpr = lexer.numberExpr();
                        lexer.nextToken();
                        break;
                    case IDENTIFIER: // 当+号后面为字段的情况
                    case LITERAL_CHARS:
                    case LITERAL_ALIAS:
                        sqlExpr = new SQLIdentifierExpr(lexer.stringVal());
                        sqlExpr = new SQLUnaryExpr(SQLUnaryOperator.Plus, sqlExpr);
                        lexer.nextToken();
                        break;
                    case LPAREN:
                        lexer.nextToken();
                        sqlExpr = expr();
                        accept(Token.RPAREN);
                        sqlExpr = new SQLUnaryExpr(SQLUnaryOperator.Plus, sqlExpr);
                        break;
                    case SUB:
                    case CAST:
                        sqlExpr = expr();
                        sqlExpr = new SQLUnaryExpr(SQLUnaryOperator.Plus, sqlExpr);
                        break;
                    default:
                        throw new ParserException("TODO " + lexer.info());
                }
                break;
            case TILDE:
                lexer.nextToken();
                SQLExpr unaryValueExpr = primary();
                SQLUnaryExpr unary = new SQLUnaryExpr(SQLUnaryOperator.Compl, unaryValueExpr);
                sqlExpr = unary;
                break;
            case QUES:
                if (JdbcConstants.MYSQL.equals(dbType)) {
                    lexer.nextTokenValue();
                } else {
                    lexer.nextToken();
                }
                SQLVariantRefExpr quesVarRefExpr = new SQLVariantRefExpr("?");
                quesVarRefExpr.setIndex(lexer.nextVarIndex());
                sqlExpr = quesVarRefExpr;
                break;
            case LEFT:
                sqlExpr = new SQLIdentifierExpr("LEFT");
                lexer.nextToken();
                break;
            case RIGHT:
                sqlExpr = new SQLIdentifierExpr("RIGHT");
                lexer.nextToken();
                break;
            case DATABASE:
                sqlExpr = new SQLIdentifierExpr("DATABASE");
                lexer.nextToken();
                break;
            case LOCK:
                sqlExpr = new SQLIdentifierExpr("LOCK");
                lexer.nextToken();
                break;
            case NULL:
                sqlExpr = new SQLNullExpr();
                lexer.nextToken();
                break;
            case BANG:
                lexer.nextToken();
                SQLExpr bangExpr = primary();
                sqlExpr = new SQLUnaryExpr(SQLUnaryOperator.Not, bangExpr);
                break;
            case LITERAL_HEX:
                String hex = lexer.hexString();
                sqlExpr = new SQLHexExpr(hex);
                lexer.nextToken();
                break;
            case INTERVAL:
                sqlExpr = parseInterval();
                break;
            case COLON:
                lexer.nextToken();
                if (lexer.token == Token.LITERAL_ALIAS) {
                    sqlExpr = new SQLVariantRefExpr(":\"" + lexer.stringVal() + "\"");
                    lexer.nextToken();
                }
                break;
            case ANY:
                sqlExpr = parseAny();
                break;
            case SOME:
                sqlExpr = parseSome();
                break;
            case ALL:
                sqlExpr = parseAll();
                break;
            case LITERAL_ALIAS:
                sqlExpr = parseAliasExpr(lexer.stringVal());
                lexer.nextToken();
                break;
            case EOF:
                throw new EOFParserException();
            case TRUE:
                lexer.nextToken();
                sqlExpr = new SQLBooleanExpr(true);
                break;
            case FALSE:
                lexer.nextToken();
                sqlExpr = new SQLBooleanExpr(false);
                break;
            case BITS: {
                String strVal = lexer.stringVal();
                lexer.nextToken();
                sqlExpr = new SQLBinaryExpr(strVal);
                break;
            }
            case CONTAINS:
                sqlExpr = inRest(null);
                break;
            case SET: {
                Lexer.SavePoint savePoint = lexer.mark();
                lexer.nextToken();
                if (lexer.token() == Token.LPAREN) {
                    sqlExpr = new SQLIdentifierExpr("SET");
                } else {
                    lexer.reset(savePoint);
                    throw new ParserException("ERROR. " + lexer.info());
                }
                break;
            }

            default:
                throw new ParserException("ERROR. " + lexer.info());
        }

        SQLExpr expr = primaryRest(sqlExpr);

        if (beforeComments != null) {
            expr.addBeforeComment(beforeComments);
        }

        return expr;
    }

    protected SQLExpr parseAll() {
        SQLExpr sqlExpr;
        lexer.nextToken();
        SQLAllExpr allExpr = new SQLAllExpr();

        accept(Token.LPAREN);
        SQLSelect allSubQuery = createSelectParser().select();
        allExpr.setSubQuery(allSubQuery);
        accept(Token.RPAREN);

        allSubQuery.setParent(allExpr);

        sqlExpr = allExpr;
        return sqlExpr;
    }

    protected SQLExpr parseSome() {
        SQLExpr sqlExpr;
        lexer.nextToken();
        SQLSomeExpr someExpr = new SQLSomeExpr();

        accept(Token.LPAREN);
        SQLSelect someSubQuery = createSelectParser().select();
        someExpr.setSubQuery(someSubQuery);
        accept(Token.RPAREN);

        someSubQuery.setParent(someExpr);

        sqlExpr = someExpr;
        return sqlExpr;
    }

    protected SQLExpr parseAny() {
        SQLExpr sqlExpr;
        lexer.nextToken();
        if (lexer.token == Token.LPAREN) {
            accept(Token.LPAREN);

            if (lexer.token == Token.ARRAY || lexer.token == Token.IDENTIFIER) {
                SQLExpr expr = this.expr();
                SQLMethodInvokeExpr methodInvokeExpr = new SQLMethodInvokeExpr("ANY");
                methodInvokeExpr.addParameter(expr);
                accept(Token.RPAREN);
                return methodInvokeExpr;
            }

            SQLSelect anySubQuery = createSelectParser().select();
            SQLAnyExpr anyExpr = new SQLAnyExpr(anySubQuery);
            accept(Token.RPAREN);

            sqlExpr = anyExpr;
        } else {
            sqlExpr = new SQLIdentifierExpr("ANY");
        }
        return sqlExpr;
    }

    protected SQLExpr parseAliasExpr(String alias) {
        return new SQLIdentifierExpr(alias);
    }

    protected SQLExpr parseInterval() {
        throw new ParserException("TODO. " + lexer.info());
    }

    public SQLSelectParser createSelectParser() {
        return new SQLSelectParser(this);
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        if (expr == null) {
            throw new IllegalArgumentException("expr");
        }

        Token token = lexer.token;
        if (token == Token.OF) {
            if (expr instanceof SQLIdentifierExpr) {
                long hashCode64 = ((SQLIdentifierExpr) expr).hashCode64();
                if (hashCode64 == FnvHash.Constants.CURRENT) {
                    lexer.nextToken();
                    SQLName cursorName = this.name();
                    return new SQLCurrentOfCursorExpr(cursorName);
                }
            }
        } else if (token == Token.FOR) {
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr idenExpr = (SQLIdentifierExpr) expr;
                if (idenExpr.hashCode64() == FnvHash.Constants.NEXTVAL) {
                    lexer.nextToken();
                    SQLName seqName = this.name();
                    SQLSequenceExpr seqExpr = new SQLSequenceExpr(seqName, SQLSequenceExpr.Function.NextVal);
                    return seqExpr;
                } else if (idenExpr.hashCode64() == FnvHash.Constants.CURRVAL) {
                    lexer.nextToken();
                    SQLName seqName = this.name();
                    SQLSequenceExpr seqExpr = new SQLSequenceExpr(seqName, SQLSequenceExpr.Function.CurrVal);
                    return seqExpr;
                } else if (idenExpr.hashCode64() == FnvHash.Constants.PREVVAL) {
                    lexer.nextToken();
                    SQLName seqName = this.name();
                    SQLSequenceExpr seqExpr = new SQLSequenceExpr(seqName, SQLSequenceExpr.Function.PrevVal);
                    return seqExpr;
                }
            }
        }

        if (token == Token.DOT) {
            lexer.nextToken();

            if (expr instanceof SQLCharExpr) {
                String text = ((SQLCharExpr) expr).getText();
                expr = new SQLIdentifierExpr(text);
            }

            expr = dotRest(expr);
            return primaryRest(expr);
        } else if (lexer.identifierEquals(FnvHash.Constants.SETS) //
                && expr.getClass() == SQLIdentifierExpr.class // 
                && "GROUPING".equalsIgnoreCase(((SQLIdentifierExpr) expr).getName())) {
            SQLGroupingSetExpr groupingSets = new SQLGroupingSetExpr();
            lexer.nextToken();

            accept(Token.LPAREN);

            for (; ; ) {
                SQLExpr item;
                if (lexer.token == Token.LPAREN) {
                    lexer.nextToken();

                    SQLListExpr listExpr = new SQLListExpr();
                    this.exprList(listExpr.getItems(), listExpr);
                    item = listExpr;

                    accept(Token.RPAREN);
                } else {
                    item = this.expr();
                }

                item.setParent(groupingSets);
                groupingSets.addParameter(item);

                if (lexer.token == Token.RPAREN) {
                    break;
                }

                accept(Token.COMMA);
            }

            this.exprList(groupingSets.getParameters(), groupingSets);

            accept(Token.RPAREN);

            return groupingSets;
        } else {
            if (lexer.token == Token.LPAREN) {
                return methodRest(expr, true);
            }
        }

        return expr;
    }

    protected SQLExpr parseExtract() {
        throw new ParserException("not supported.");
    }

    protected SQLExpr parsePosition() {
        throw new ParserException("not supported.");
    }

    protected SQLExpr parseMatch() {
        throw new ParserException("not supported.");
    }

    protected SQLExpr methodRest(SQLExpr expr, boolean acceptLPAREN) {
        if (acceptLPAREN) {
            accept(Token.LPAREN);
        }

        boolean distinct = false;
        if (lexer.token == Token.DISTINCT) {
            lexer.nextToken();
            distinct = true;

            if (lexer.token == Token.RPAREN || lexer.token == Token.COMMA) {
                throw new ParserException(lexer.info());
            }
        }

        String methodName = null;
        String aggMethodName = null;
        SQLMethodInvokeExpr methodInvokeExpr;
        SQLExpr owner = null;
        String trimOption = null;

        long hash_lower = 0L;
        if (expr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) expr;
            methodName = identifierExpr.getName();
            hash_lower = identifierExpr.nameHashCode64();

            if (allowIdentifierMethod) {
                if (hash_lower == FnvHash.Constants.TRIM) {
                    if (lexer.identifierEquals(FnvHash.Constants.LEADING)) {
                        trimOption = lexer.stringVal();
                        lexer.nextToken();
                    } else if (lexer.identifierEquals(FnvHash.Constants.BOTH)) {
                        trimOption = lexer.stringVal();
                        lexer.nextToken();
                    } else if (lexer.identifierEquals(FnvHash.Constants.TRAILING)) {
                        trimOption = lexer.stringVal();
                        lexer.nextToken();
                    }
                } else if (hash_lower == FnvHash.Constants.MATCH
                        && (JdbcConstants.MYSQL.equals(dbType) || JdbcConstants.ALIYUN_ADS.equals(dbType))) {
                    return parseMatch();
                } else if (hash_lower == FnvHash.Constants.EXTRACT
                        && JdbcConstants.MYSQL.equals(dbType)) {
                    return parseExtract();
                } else if (hash_lower == FnvHash.Constants.POSITION
                        && JdbcConstants.MYSQL.equals(dbType)) {
                    return parsePosition();
                }else if (hash_lower == FnvHash.Constants.INT4 && JdbcConstants.POSTGRESQL.equals(dbType)) {
                    PGTypeCastExpr castExpr = new PGTypeCastExpr();
                    castExpr.setExpr(this.expr());
                    castExpr.setDataType(new SQLDataTypeImpl(methodName));
                    accept(Token.RPAREN);
                    return castExpr;
                } else if (hash_lower == FnvHash.Constants.VARBIT && JdbcConstants.POSTGRESQL.equals(dbType)) {
                    PGTypeCastExpr castExpr = new PGTypeCastExpr();
                    SQLExpr len = this.primary();
                    castExpr.setDataType(new SQLDataTypeImpl(methodName, len));
                    accept(Token.RPAREN);
                    castExpr.setExpr(this.expr());
                    return castExpr;
                } else if (hash_lower == FnvHash.Constants.CONVERT && JdbcConstants.MYSQL.equals(dbType)) {
                    methodInvokeExpr = new SQLMethodInvokeExpr(methodName, hash_lower);
                    SQLExpr arg0 = this.expr();
                    // Fix for using.
                    Object exprUsing = arg0.getAttributes().get("USING");
                    if (exprUsing instanceof String) {
                        String charset = (String) exprUsing;
                        methodInvokeExpr.setUsing(new SQLIdentifierExpr(charset));
                        arg0.getAttributes().remove("USING");
                    }
                    methodInvokeExpr.addArgument(arg0);

                    if (lexer.token == Token.COMMA) {
                        lexer.nextToken();
                        SQLDataType dataType = this.parseDataType();
                        SQLDataTypeRefExpr dataTypeRefExpr = new SQLDataTypeRefExpr(dataType);
                        methodInvokeExpr.addArgument(dataTypeRefExpr);
                    }

                    if (lexer.token == Token.USING || lexer.identifierEquals(FnvHash.Constants.USING)) {
                        lexer.nextToken();
                        SQLExpr using;
                        if (lexer.token == Token.STAR) {
                            lexer.nextToken();
                            using = new SQLAllColumnExpr();
                        } else if (lexer.token == Token.BINARY) {
                            using = new SQLIdentifierExpr(lexer.stringVal());
                            lexer.nextToken();
                        } else {
                            using = this.primary();
                        }
                        methodInvokeExpr.setUsing(using);
                    }

                    accept(Token.RPAREN);

                    return primaryRest(methodInvokeExpr);
                }
            }

            if (distinct) {
                aggMethodName = methodName;
            } else {
                aggMethodName = getAggreateFunction(hash_lower);
            }
        } else if (expr instanceof SQLPropertyExpr) {
            methodName = ((SQLPropertyExpr) expr).getSimpleName();
            aggMethodName = SQLUtils.normalize(methodName);
            hash_lower = FnvHash.fnv1a_64_lower(aggMethodName);
            aggMethodName = getAggreateFunction(hash_lower);

            owner = ((SQLPropertyExpr) expr).getOwner();
        } else if (expr instanceof SQLDefaultExpr) {
            methodName = "DEFAULT";
        } else if (expr instanceof SQLCharExpr) {
            methodName = ((SQLCharExpr) expr).getText();
            if (isAggreateFunction(methodName)) {
                aggMethodName = methodName;
            }
        }

        if (aggMethodName != null) {
            SQLAggregateExpr aggregateExpr = parseAggregateExpr(methodName);
            if (distinct) {
                aggregateExpr.setOption(SQLAggregateOption.DISTINCT);
            }


            return aggregateExpr;
        }

        methodInvokeExpr = new SQLMethodInvokeExpr(methodName, hash_lower);
        if (owner != null) {
            methodInvokeExpr.setOwner(owner);
        }
        if (trimOption != null) {
            methodInvokeExpr.setTrimOption(trimOption);
        }

        Token token = lexer.token;
        if (token != Token.RPAREN && token != Token.FROM) {
            exprList(methodInvokeExpr.getArguments(), methodInvokeExpr);
        }

        if (hash_lower == FnvHash.Constants.EXIST
                && methodInvokeExpr.getArguments().size() == 1
                && methodInvokeExpr.getArguments().get(0) instanceof SQLQueryExpr) {
            throw new ParserException("exists syntax error.");
        }

        if (lexer.token == Token.FROM) {
            lexer.nextToken();
            SQLExpr from = this.expr();
            methodInvokeExpr.setFrom(from);

            if (lexer.token == Token.FOR) {
                lexer.nextToken();
                SQLExpr forExpr = expr();
                methodInvokeExpr.setFor(forExpr);
            }
        }

        if (lexer.token == Token.USING || lexer.identifierEquals(FnvHash.Constants.USING)) {
            lexer.nextToken();
            SQLExpr using;
            if (lexer.token == Token.STAR) {
                lexer.nextToken();
                using = new SQLAllColumnExpr();
            } else if (lexer.token == Token.BINARY) {
                using = new SQLIdentifierExpr(lexer.stringVal());
                lexer.nextToken();
            } else {
                using = this.primary();
            }
            methodInvokeExpr.setUsing(using);
        }

        // mysql
        if (hash_lower == FnvHash.Constants.WEIGHT_STRING) {
            if (lexer.token == Token.AS) {
                lexer.nextToken();
                SQLDataType as = this.parseDataType();
                methodInvokeExpr.putAttribute("as", as);
            }

            if (lexer.identifierEquals(FnvHash.Constants.LEVEL)) {
                lexer.nextToken();
                List<SQLSelectOrderByItem> levels = new ArrayList<SQLSelectOrderByItem>();
                for (;;) {
                    SQLSelectOrderByItem level = this.parseSelectOrderByItem();
                    levels.add(level);
                    if (lexer.token == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                methodInvokeExpr.putAttribute("levels", levels);
            }

            if (lexer.identifierEquals(FnvHash.Constants.REVERSE)) {
                lexer.nextToken();
                methodInvokeExpr.putAttribute("reverse", true);
            }
        }

        SQLAggregateExpr aggregateExpr = null;
        if (lexer.token == Token.ORDER) {
            lexer.nextToken();
            accept(Token.BY);

            aggregateExpr = new SQLAggregateExpr(methodName);
            aggregateExpr.getArguments().addAll(methodInvokeExpr.getArguments());

            SQLOrderBy orderBy = new SQLOrderBy();
            this.orderBy(orderBy.getItems(), orderBy);
            aggregateExpr.setWithinGroup(orderBy);
        }

        accept(Token.RPAREN);


        if (lexer.identifierEquals(FnvHash.Constants.FILTER)) {
            if (aggregateExpr == null) {
                Lexer.SavePoint mark = lexer.mark();
                lexer.nextToken();
                Token nextToken = lexer.token;
                lexer.reset(mark);

                if (nextToken == Token.LPAREN) {
                    aggregateExpr = new SQLAggregateExpr(methodName);
                    aggregateExpr.getArguments().addAll(methodInvokeExpr.getArguments());
                    filter(aggregateExpr);
                }
            } else {
                filter(aggregateExpr);
            }
        }

        if (lexer.token == Token.OVER) {
            if (aggregateExpr == null) {
                aggregateExpr = new SQLAggregateExpr(methodName);
                aggregateExpr.getArguments().addAll(methodInvokeExpr.getArguments());
            }
            over(aggregateExpr);
        }

        if (aggregateExpr != null) {
            return primaryRest(aggregateExpr);
        }

        if (lexer.token == Token.LPAREN) {
            return methodInvokeExpr;
        }

        return primaryRest(methodInvokeExpr);

        //throw new ParserException("not support token:" + lexer.token + ", " + lexer.info());
    }

    protected SQLExpr dotRest(SQLExpr expr) {
        if (lexer.token == Token.STAR) {
            lexer.nextToken();
            expr = new SQLPropertyExpr(expr, "*");
        } else {
            String name;
            long hash_lower = 0L;

            if (lexer.token == Token.IDENTIFIER) {
                name = lexer.stringVal();
                hash_lower = lexer.hash_lower;
                lexer.nextToken();
            } else if (lexer.token == Token.LITERAL_CHARS
                    || lexer.token == Token.LITERAL_ALIAS) {
                name = lexer.stringVal();
                lexer.nextToken();
            } else if (lexer.getKeywods().containsValue(lexer.token)) {
                name = lexer.stringVal();
                lexer.nextToken();
            } else {
                throw new ParserException("error : " + lexer.info());
            }

            if (lexer.token == Token.LPAREN) {
                boolean aggregate = hash_lower == FnvHash.Constants.WM_CONCAT
                        && expr instanceof SQLIdentifierExpr
                        && ((SQLIdentifierExpr) expr).nameHashCode64() == FnvHash.Constants.WMSYS;
                expr = methodRest(expr, name, aggregate);
            } else {
                expr = new SQLPropertyExpr(expr, name, hash_lower);
            }
        }

        expr = primaryRest(expr);
        return expr;
    }

    private SQLExpr methodRest(SQLExpr expr, String name, boolean aggregate) {
        lexer.nextToken();

        if (lexer.token == Token.DISTINCT) {
            lexer.nextToken();

            String aggreateMethodName = expr.toString() + "." + name;
            SQLAggregateExpr aggregateExpr = new SQLAggregateExpr(aggreateMethodName, SQLAggregateOption.DISTINCT);

            if (lexer.token == Token.RPAREN) {
                lexer.nextToken();
            } else {
                if (lexer.token == Token.PLUS) {
                    aggregateExpr.getArguments().add(new SQLIdentifierExpr("+"));
                    lexer.nextToken();
                } else {
                    exprList(aggregateExpr.getArguments(), aggregateExpr);
                }
                accept(Token.RPAREN);
            }
            expr = aggregateExpr;
        } else if (aggregate) {
            SQLAggregateExpr methodInvokeExpr = new SQLAggregateExpr(name);
            methodInvokeExpr.setMethodName(expr.toString() + "." + name);
            if (lexer.token == Token.RPAREN) {
                lexer.nextToken();
            } else {
                if (lexer.token == Token.PLUS) {
                    methodInvokeExpr.addArgument(new SQLIdentifierExpr("+"));
                    lexer.nextToken();
                } else {
                    exprList(methodInvokeExpr.getArguments(), methodInvokeExpr);
                }
                accept(Token.RPAREN);
            }

            if (lexer.token == Token.OVER) {
                over(methodInvokeExpr);
            }

            expr = methodInvokeExpr;
        } else {
            SQLMethodInvokeExpr methodInvokeExpr = new SQLMethodInvokeExpr(name);
            methodInvokeExpr.setOwner(expr);
            if (lexer.token == Token.RPAREN) {
                lexer.nextToken();
            } else {
                if (lexer.token == Token.PLUS) {
                    methodInvokeExpr.addParameter(new SQLIdentifierExpr("+"));
                    lexer.nextToken();
                } else {
                    exprList(methodInvokeExpr.getParameters(), methodInvokeExpr);
                }
                accept(Token.RPAREN);
            }
            expr = methodInvokeExpr;
        }
        return expr;
    }

    public final SQLExpr groupComparisionRest(SQLExpr expr) {
        return expr;
    }

    public final void names(Collection<SQLName> exprCol) {
        names(exprCol, null);
    }

    public final void names(Collection<SQLName> exprCol, SQLObject parent) {
        if (lexer.token == Token.RBRACE) {
            return;
        }

        if (lexer.token == Token.EOF) {
            return;
        }

        SQLName name = name();
        name.setParent(parent);
        exprCol.add(name);

        while (lexer.token == Token.COMMA) {
            lexer.nextToken();

            name = name();
            name.setParent(parent);
            exprCol.add(name);
        }
    }

    @Deprecated
    public final void exprList(Collection<SQLExpr> exprCol) {
        exprList(exprCol, null);
    }

    public final void exprList(Collection<SQLExpr> exprCol, SQLObject parent) {
        if (lexer.token == Token.RPAREN || lexer.token == Token.RBRACKET) {
            return;
        }

        if (lexer.token == Token.EOF) {
            return;
        }

        SQLExpr expr = expr();
        expr.setParent(parent);
        exprCol.add(expr);

        while (lexer.token == Token.COMMA) {
            lexer.nextToken();
            expr = expr();
            expr.setParent(parent);
            exprCol.add(expr);
        }
    }

    public SQLName name() {
        String identName;
        long hash = 0;
        if (lexer.token == Token.LITERAL_ALIAS) {
            identName = lexer.stringVal();
            lexer.nextToken();
        } else if (lexer.token == Token.IDENTIFIER) {
            identName = lexer.stringVal();

            char c0 = identName.charAt(0);
            if (c0 != '[') {
                hash = lexer.hash_lower();
            }
            lexer.nextToken();
        } else if (lexer.token == Token.LITERAL_CHARS) {
            identName = '\'' + lexer.stringVal() + '\'';
            lexer.nextToken();
        } else if (lexer.token == Token.VARIANT) {
            identName = lexer.stringVal();
            lexer.nextToken();
        } else {
            switch (lexer.token) {
                case MODEL:
                case PCTFREE:
                case INITRANS:
                case MAXTRANS:
                case SEGMENT:
                case CREATION:
                case IMMEDIATE:
                case DEFERRED:
                case STORAGE:
                case NEXT:
                case MINEXTENTS:
                case MAXEXTENTS:
                case MAXSIZE:
                case PCTINCREASE:
                case FLASH_CACHE:
                case CELL_FLASH_CACHE:
                case NONE:
                case LOB:
                case STORE:
                case ROW:
                case CHUNK:
                case CACHE:
                case NOCACHE:
                case LOGGING:
                case NOCOMPRESS:
                case KEEP_DUPLICATES:
                case EXCEPTIONS:
                case PURGE:
                case INITIALLY:
                case END:
                case COMMENT:
                case ENABLE:
                case DISABLE:
                case SEQUENCE:
                case USER:
                case ANALYZE:
                case OPTIMIZE:
                case GRANT:
                case REVOKE:
                    // binary有很多含义，lexer识别了这个token，实际上应该当做普通IDENTIFIER
                case BINARY:
                case OVER:
                case ORDER:
                case DO:
                case JOIN:
                case TYPE:
                case FUNCTION:
                case KEY:
                case SCHEMA:
                case INTERVAL:
                case EXPLAIN:
                case PARTITION:
                case SET:
                case DEFAULT:
                    identName = lexer.stringVal();
                    lexer.nextToken();
                    break;
                default:
                    throw new ParserException("error " + lexer.info());
            }
        }

        SQLName name = new SQLIdentifierExpr(identName, hash);

        name = nameRest(name);

        return name;
    }

    public SQLName nameRest(SQLName name) {
        if (lexer.token == Token.DOT) {
            lexer.nextToken();

            if (lexer.token == Token.KEY) {
                name = new SQLPropertyExpr(name, "KEY");
                lexer.nextToken();
                return name;
            }

            if (lexer.token != Token.LITERAL_ALIAS && lexer.token != Token.IDENTIFIER
                && (!lexer.getKeywods().containsValue(lexer.token))) {
                throw new ParserException("error, " + lexer.info());
            }

            if (lexer.token == Token.LITERAL_ALIAS) {
                name = new SQLPropertyExpr(name, lexer.stringVal());
            } else {
                name = new SQLPropertyExpr(name, lexer.stringVal());
            }
            lexer.nextToken();
            name = nameRest(name);
        }

        return name;
    }

    public boolean isAggreateFunction(String word) {
        long hash_lower = FnvHash.fnv1a_64_lower(word);
        return isAggreateFunction(hash_lower);
    }

    protected boolean isAggreateFunction(long hash_lower) {
        return Arrays.binarySearch(aggregateFunctionHashCodes, hash_lower) >= 0;
    }

    protected String getAggreateFunction(long hash_lower) {
        int index = Arrays.binarySearch(aggregateFunctionHashCodes, hash_lower);
        if (index < 0) {
            return null;
        }
        return aggregateFunctions[index];
    }

    protected SQLAggregateExpr parseAggregateExpr(String methodName) {
        SQLAggregateExpr aggregateExpr;
        if (lexer.token == Token.ALL) {
            aggregateExpr = new SQLAggregateExpr(methodName, SQLAggregateOption.ALL);
            lexer.nextToken();
        } else if (lexer.token == Token.DISTINCT) {
            aggregateExpr = new SQLAggregateExpr(methodName, SQLAggregateOption.DISTINCT);
            lexer.nextToken();
        } else if (lexer.identifierEquals(FnvHash.Constants.DEDUPLICATION)) { // just for nut
            aggregateExpr = new SQLAggregateExpr(methodName, SQLAggregateOption.DEDUPLICATION);
            lexer.nextToken();
        } else {
            aggregateExpr = new SQLAggregateExpr(methodName);
        }

        exprList(aggregateExpr.getArguments(), aggregateExpr);

        if (lexer.token != Token.RPAREN) {
            parseAggregateExprRest(aggregateExpr);
        }

        accept(Token.RPAREN);

        if (lexer.identifierEquals(FnvHash.Constants.FILTER)) {
            filter(aggregateExpr);
        }

        if (lexer.token == Token.OVER) {
            over(aggregateExpr);
        }

        return aggregateExpr;
    }

    protected void filter(SQLAggregateExpr aggregateExpr) {

    }

    protected void over(SQLAggregateExpr aggregateExpr) {
        lexer.nextToken();

        if (lexer.token != Token.LPAREN) {
            SQLName overRef = this.name();
            aggregateExpr.setOverRef(overRef);
            return;
        }

        SQLOver over = new SQLOver();
        over(over);
        aggregateExpr.setOver(over);
    }

    protected void over(SQLOver over) {
        lexer.nextToken();

        if (lexer.token == Token.PARTITION || lexer.identifierEquals("PARTITION")) {
            lexer.nextToken();
            accept(Token.BY);

            if (lexer.token == (Token.LPAREN)) {
                lexer.nextToken();
                exprList(over.getPartitionBy(), over);
                accept(Token.RPAREN);
            } else {
                exprList(over.getPartitionBy(), over);
            }
        }

        over.setOrderBy(parseOrderBy());

        if (lexer.token == Token.OF) {
            lexer.nextToken();
            SQLName of = this.name();
            over.setOf(of);
        }

        SQLOver.WindowingType windowingType = null;
        if (lexer.identifierEquals(FnvHash.Constants.ROWS) || lexer.token == Token.ROWS) {
            windowingType = SQLOver.WindowingType.ROWS;

        } else if (lexer.identifierEquals(FnvHash.Constants.RANGE)) {
            windowingType = SQLOver.WindowingType.RANGE;
        }

        if (windowingType != null) {
            over.setWindowingType(windowingType);
            lexer.nextToken();

            if (lexer.token == Token.BETWEEN) {
                lexer.nextToken();
                SQLExpr rowsBegin = this.primary();
                over.setWindowingBetweenBegin(rowsBegin);

                if (lexer.identifierEquals(FnvHash.Constants.PRECEDING)) {
                    over.setWindowingBetweenBeginPreceding(true);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.FOLLOWING)) {
                    over.setWindowingBetweenBeginFollowing(true);
                    lexer.nextToken();
                }

                accept(Token.AND);

                SQLExpr betweenEnd;
                if (lexer.identifierEquals(FnvHash.Constants.CURRENT) || lexer.token == Token.CURRENT) {
                    lexer.nextToken();
                    if (lexer.identifierEquals(FnvHash.Constants.ROW)) {
                        lexer.nextToken();
                    } else {
                        accept(Token.ROW);
                    }
                    betweenEnd = new SQLIdentifierExpr("CURRENT ROW");
                } else {
                    betweenEnd = this.primary();
                }
                over.setWindowingBetweenEnd(betweenEnd);

                if (lexer.identifierEquals(FnvHash.Constants.PRECEDING)) {
                    over.setWindowingBetweenEndPreceding(true);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.FOLLOWING)) {
                    over.setWindowingBetweenEndFollowing(true);
                    lexer.nextToken();
                }

            } else {

                if (lexer.identifierEquals(FnvHash.Constants.CURRENT)) {
                    lexer.nextToken();
                    if (lexer.identifierEquals(FnvHash.Constants.ROW)) {
                        lexer.nextToken();
                    } else {
                        accept(Token.ROW);
                    }
                    over.setWindowing(new SQLIdentifierExpr("CURRENT ROW"));
                } else  if (lexer.identifierEquals(FnvHash.Constants.UNBOUNDED)) {
                    lexer.nextToken();
                    over.setWindowing(new SQLIdentifierExpr("UNBOUNDED"));

                    if (lexer.identifierEquals(FnvHash.Constants.PRECEDING)) {
                        over.setWindowingPreceding(true);
                        lexer.nextToken();
                    } else if (lexer.identifierEquals("FOLLOWING")) {
                        over.setWindowingFollowing(true);
                        lexer.nextToken();
                    }
                } else {
                    SQLIntegerExpr rowsExpr = (SQLIntegerExpr) this.primary();
                    over.setWindowing(rowsExpr);

                    if (lexer.identifierEquals(FnvHash.Constants.PRECEDING)) {
                        over.setWindowingPreceding(true);
                        lexer.nextToken();
                    } else if (lexer.identifierEquals(FnvHash.Constants.FOLLOWING)) {
                        over.setWindowingFollowing(true);
                        lexer.nextToken();
                    }
                }
            }
        }

        accept(Token.RPAREN);
    }

    protected SQLAggregateExpr parseAggregateExprRest(SQLAggregateExpr aggregateExpr) {
        return aggregateExpr;
    }

    public SQLOrderBy parseOrderBy() {
        if (lexer.token == Token.ORDER) {
            SQLOrderBy orderBy = new SQLOrderBy();

            lexer.nextToken();
            
            if (lexer.identifierEquals(FnvHash.Constants.SIBLINGS)) {
                lexer.nextToken();
                orderBy.setSibings(true);
            }

            accept(Token.BY);

            orderBy(orderBy.getItems(), orderBy);

            return orderBy;
        }

        return null;
    }

    public void orderBy(List<SQLSelectOrderByItem> items, SQLObject parent) {
        SQLSelectOrderByItem item = parseSelectOrderByItem();
        item.setParent(parent);
        items.add(item);
        while (lexer.token == Token.COMMA) {
            lexer.nextToken();
            item = parseSelectOrderByItem();
            item.setParent(parent);
            items.add(item);
        }
    }

    public SQLSelectOrderByItem parseSelectOrderByItem() {
        SQLSelectOrderByItem item = new SQLSelectOrderByItem();

        item.setExpr(expr());

        if (lexer.token == Token.ASC) {
            lexer.nextToken();
            item.setType(SQLOrderingSpecification.ASC);
        } else if (lexer.token == Token.DESC) {
            lexer.nextToken();
            item.setType(SQLOrderingSpecification.DESC);
        }

        if (lexer.identifierEquals(FnvHash.Constants.NULLS)) {
            lexer.nextToken();
            if (lexer.identifierEquals(FnvHash.Constants.FIRST)) {
                lexer.nextToken();
                item.setNullsOrderType(SQLSelectOrderByItem.NullsOrderType.NullsFirst);
            } else if (lexer.identifierEquals(FnvHash.Constants.LAST)) {
                lexer.nextToken();
                item.setNullsOrderType(SQLSelectOrderByItem.NullsOrderType.NullsLast);
            } else {
                throw new ParserException("TODO " + lexer.info());
            }
        }

        return item;
    }

    public SQLUpdateSetItem parseUpdateSetItem() {
        SQLUpdateSetItem item = new SQLUpdateSetItem();

        if (lexer.token == (Token.LPAREN)) {
            lexer.nextToken();
            SQLListExpr list = new SQLListExpr();
            this.exprList(list.getItems(), list);
            accept(Token.RPAREN);
            item.setColumn(list);
        } else {
            String identName;
            long hash;

            Token token = lexer.token();
            if (token == Token.IDENTIFIER) {
                identName = lexer.stringVal();
                hash = lexer.hash_lower();
            } else if (token == Token.LITERAL_CHARS) {
                identName = '\'' + lexer.stringVal() + '\'';
                hash = 0;
            } else {
                identName = lexer.stringVal();
                hash = 0;
            }
            lexer.nextTokenEq();
            SQLExpr expr = new SQLIdentifierExpr(identName, hash);
            while (lexer.token() == Token.DOT) {
                lexer.nextToken();
                String propertyName = lexer.stringVal();
                lexer.nextTokenEq();
                expr = new SQLPropertyExpr(expr, propertyName);
            }

            item.setColumn(expr);
        }
        if (lexer.token == Token.COLONEQ) {
            lexer.nextTokenValue();
        } else if (lexer.token == Token.EQ) {
            lexer.nextTokenValue();
        } else {
            throw new ParserException("syntax error, expect EQ, actual " + lexer.token + " "
                    + lexer.info());
        }

        item.setValue(this.expr());
        return item;
    }

    public final SQLExpr bitAnd() {
        SQLExpr expr = shift();
        return bitAndRest(expr);
    }

    public final SQLExpr bitAndRest(SQLExpr expr) {
        while (lexer.token == Token.AMP) {
            lexer.nextToken();
            SQLExpr rightExp = shift();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BitwiseAnd, rightExp, getDbType());
        }
        return expr;
    }

    public final SQLExpr bitOr() {
        SQLExpr expr = bitAnd();
        return bitOrRest(expr);
    }

    public final SQLExpr bitOrRest(SQLExpr expr) {
        while (lexer.token == Token.BAR) {
            lexer.nextToken();
            SQLBinaryOperator op = SQLBinaryOperator.BitwiseOr;
            if (lexer.token == Token.BAR) {
                lexer.nextToken();
                op = SQLBinaryOperator.Concat;
            }
            SQLExpr rightExp = bitAnd();
            expr = new SQLBinaryOpExpr(expr, op, rightExp, getDbType());
            expr = bitAndRest(expr);
        }
        return expr;
    }

    public final SQLExpr inRest(SQLExpr expr) {
        if (lexer.token == Token.IN) {
            lexer.nextTokenLParen();

            SQLInListExpr inListExpr = new SQLInListExpr(expr);
            List<SQLExpr> targetList = inListExpr.getTargetList();
            if (lexer.token == Token.LPAREN) {
                lexer.nextTokenValue();

                if (lexer.token == Token.WITH) {
                    SQLSelect select = this.createSelectParser().select();
                    SQLInSubQueryExpr queryExpr = new SQLInSubQueryExpr(select);
                    queryExpr.setExpr(expr);
                    accept(Token.RPAREN);
                    return queryExpr;
                }

                for (;;) {
                    SQLExpr item;
                    if (lexer.token == Token.LITERAL_INT) {
                        item = new SQLIntegerExpr(lexer.integerValue());
                        lexer.nextToken();
                        if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                            item = this.primaryRest(item);
                            item = this.exprRest(item);
                        }
                    } else {
                        item = this.expr();
                    }

                    item.setParent(inListExpr);
                    targetList.add(item);
                    if (lexer.token == Token.COMMA) {
                        lexer.nextTokenValue();
                        continue;
                    }
                    break;
                }

                accept(Token.RPAREN);
            } else {
                SQLExpr itemExpr = primary();
                itemExpr.setParent(inListExpr);
                targetList.add(itemExpr);
            }

            expr = inListExpr;

            if (targetList.size() == 1) {
                SQLExpr targetExpr = targetList.get(0);
                if (targetExpr instanceof SQLQueryExpr) {
                    SQLInSubQueryExpr inSubQueryExpr = new SQLInSubQueryExpr();
                    inSubQueryExpr.setExpr(inListExpr.getExpr());
                    inSubQueryExpr.setSubQuery(((SQLQueryExpr) targetExpr).getSubQuery());
                    expr = inSubQueryExpr;
                }
            }
        } else if (lexer.token == Token.CONTAINS) {
            lexer.nextTokenLParen();

            SQLContainsExpr containsExpr = new SQLContainsExpr(expr);
            List<SQLExpr> targetList = containsExpr.getTargetList();
            if (lexer.token == Token.LPAREN) {
                lexer.nextTokenValue();

                if (lexer.token == Token.WITH) {
                    SQLSelect select = this.createSelectParser().select();
                    SQLInSubQueryExpr queryExpr = new SQLInSubQueryExpr(select);
                    queryExpr.setExpr(expr);
                    accept(Token.RPAREN);
                    return queryExpr;
                }

                for (;;) {
                    SQLExpr item;
                    if (lexer.token == Token.LITERAL_INT) {
                        item = new SQLIntegerExpr(lexer.integerValue());
                        lexer.nextToken();
                        if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                            item = this.primaryRest(item);
                            item = this.exprRest(item);
                        }
                    } else {
                        item = this.expr();
                    }

                    item.setParent(containsExpr);
                    targetList.add(item);
                    if (lexer.token == Token.COMMA) {
                        lexer.nextTokenValue();
                        continue;
                    }
                    break;
                }

                accept(Token.RPAREN);
            } else {
                SQLExpr itemExpr = primary();
                itemExpr.setParent(containsExpr);
                targetList.add(itemExpr);
            }

            expr = containsExpr;
        }

        return expr;
    }

    public final SQLExpr additive() {
        SQLExpr expr = multiplicative();

        if (lexer.token == Token.PLUS
                || lexer.token == Token.BARBAR
                || lexer.token == Token.CONCAT
                || lexer.token == Token.SUB) {
            expr = additiveRest(expr);
        }

        return expr;
    }

    public SQLExpr additiveRest(SQLExpr expr) {
        Token token = lexer.token;
        if (token == Token.PLUS) {
            lexer.nextToken();
            SQLExpr rightExp = multiplicative();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Add, rightExp, dbType);
            expr = additiveRest(expr);
        } else if ((token == Token.BARBAR || token == Token.CONCAT)
                && (isEnabled(SQLParserFeature.PipesAsConcat) || !JdbcConstants.MYSQL.equals(dbType))) {
            lexer.nextToken();
            SQLExpr rightExp = multiplicative();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Concat, rightExp, dbType);
            expr = additiveRest(expr);
        } else if (token == Token.SUB) {
            lexer.nextToken();
            SQLExpr rightExp = multiplicative();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Subtract, rightExp, dbType);
            expr = additiveRest(expr);
        }

        return expr;
    }

    public final SQLExpr shift() {
        SQLExpr expr = additive();
        return shiftRest(expr);
    }

    public SQLExpr shiftRest(SQLExpr expr) {
        if (lexer.token == Token.LTLT) {
            lexer.nextToken();
            SQLExpr rightExp = additive();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LeftShift, rightExp, dbType);
            expr = shiftRest(expr);
        } else if (lexer.token == Token.GTGT) {
            lexer.nextToken();
            SQLExpr rightExp = additive();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.RightShift, rightExp, dbType);
            expr = shiftRest(expr);
        }

        return expr;
    }

    public SQLExpr and() {
        SQLExpr expr = relational();
        if (lexer.token == Token.AND || lexer.token == Token.AMPAMP) {
            expr = andRest(expr);
        }
        return expr;
    }

    public SQLExpr andRest(SQLExpr expr) {
        for (;;) {
            Token token = lexer.token;
            if (token == Token.AND) {
                if (lexer.isKeepComments() && lexer.hasComment()) {
                    expr.addAfterComment(lexer.readAndResetComments());
                }

                lexer.nextToken();

                SQLExpr rightExp = relational();

                if (lexer.token == Token.AND
                        && lexer.isEnabled(SQLParserFeature.EnableSQLBinaryOpExprGroup)) {

                    SQLBinaryOpExprGroup group = new SQLBinaryOpExprGroup(SQLBinaryOperator.BooleanAnd, dbType);
                    group.add(expr);
                    group.add(rightExp);

                    if (lexer.isKeepComments() && lexer.hasComment()) {
                        rightExp.addAfterComment(lexer.readAndResetComments());
                    }

                    for (;;) {
                        lexer.nextToken();
                        SQLExpr more = relational();
                        group.add(more);

                        if (lexer.token == Token.AND) {
                            if (lexer.isKeepComments() && lexer.hasComment()) {
                                more.addAfterComment(lexer.readAndResetComments());
                            }

                            continue;
                        }
                        break;
                    }

                    expr = group;
                } else {
                    expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BooleanAnd, rightExp, dbType);
                }
            } else if (token == Token.AMPAMP) {
                if (lexer.isKeepComments() && lexer.hasComment()) {
                    expr.addAfterComment(lexer.readAndResetComments());
                }

                lexer.nextToken();

                SQLExpr rightExp = relational();

                SQLBinaryOperator operator = JdbcConstants.POSTGRESQL.equals(dbType)
                        ? SQLBinaryOperator.PG_And
                        : SQLBinaryOperator.BooleanAnd;

                expr = new SQLBinaryOpExpr(expr, operator, rightExp, dbType);
            } else {
                break;
            }
        }

        return expr;
    }


    public SQLExpr xor() {
        SQLExpr expr = and();
        if (lexer.token == Token.XOR) {
            expr = xorRest(expr);
        }
        return expr;
    }

    public SQLExpr xorRest(SQLExpr expr) {
        for (;;) {
            if (lexer.token == Token.XOR) {
                lexer.nextToken();
                SQLExpr rightExp = and();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BooleanXor, rightExp, dbType);
            } else {
                break;
            }
        }

        return expr;
    }

    public SQLExpr or() {
        SQLExpr expr = xor();
        if (lexer.token == Token.OR || lexer.token == Token.BARBAR) {
            expr = orRest(expr);
        }
        return expr;
    }

    public SQLExpr orRest(SQLExpr expr) {
        for (;;) {
            if (lexer.token == Token.OR) {
                lexer.nextToken();
                SQLExpr rightExp = xor();

                if (lexer.token == Token.OR
                        && lexer.isEnabled(SQLParserFeature.EnableSQLBinaryOpExprGroup)) {

                    SQLBinaryOpExprGroup group = new SQLBinaryOpExprGroup(SQLBinaryOperator.BooleanOr, dbType);
                    group.add(expr);
                    group.add(rightExp);

                    if (lexer.isKeepComments() && lexer.hasComment()) {
                        rightExp.addAfterComment(lexer.readAndResetComments());
                    }

                    for (;;) {
                        lexer.nextToken();
                        SQLExpr more = xor();
                        group.add(more);
                        if (lexer.token == Token.OR) {
                            if (lexer.isKeepComments() && lexer.hasComment()) {
                                more.addAfterComment(lexer.readAndResetComments());
                            }

                            continue;
                        }
                        break;
                    }

                    expr = group;
                } else {
                    expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BooleanOr, rightExp, dbType);
                }
            } else  if (lexer.token == Token.BARBAR) {
                lexer.nextToken();
                SQLExpr rightExp = xor();

                SQLBinaryOperator op = JdbcConstants.MYSQL.equals(dbType) && !isEnabled(SQLParserFeature.PipesAsConcat)
                        ? SQLBinaryOperator.BooleanOr
                        : SQLBinaryOperator.Concat;

                expr = new SQLBinaryOpExpr(expr, op, rightExp, dbType);
            } else {
                break;
            }
        }

        return expr;
    }

    public SQLExpr relational() {
        SQLExpr expr = bitOr();

        return relationalRest(expr);
    }

    public SQLExpr relationalRest(SQLExpr expr) {
        SQLExpr rightExp;

        Token token = lexer.token;

        switch (token) {
            case EQ:{
                lexer.nextToken();
                try {
                    rightExp = bitOr();
                } catch (EOFParserException e) {
                    throw new ParserException("EOF, " + expr + "=", e);
                }

                if (lexer.token == Token.COLONEQ) {
                    lexer.nextToken();
                    SQLExpr colonExpr = expr();
                    rightExp = new SQLBinaryOpExpr(rightExp, SQLBinaryOperator.Assignment, colonExpr, dbType);
                }

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Equality, rightExp, dbType);
            }
            break;
            case IS: {
                lexer.nextTokenNotOrNull();

                SQLBinaryOperator op;
                if (lexer.token == Token.NOT) {
                    op = SQLBinaryOperator.IsNot;
                    lexer.nextTokenNotOrNull();
                } else {
                    op = SQLBinaryOperator.Is;
                }
                rightExp = primary();
                expr = new SQLBinaryOpExpr(expr, op, rightExp, dbType);
            }
            break;
            case EQGT: {
                lexer.nextToken();
                rightExp = expr();
                String argumentName = ((SQLIdentifierExpr) expr).getName();
                expr = new OracleArgumentExpr(argumentName, rightExp);
            }
            break;
            case BANGEQ:
            case CARETEQ: {
                lexer.nextToken();
                rightExp = bitOr();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotEqual, rightExp, dbType);
            }
            break;
            case COLONEQ:{
                lexer.nextToken();
                rightExp = expr();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Assignment, rightExp, dbType);
            }
            break;
            case LT:{
                SQLBinaryOperator op = SQLBinaryOperator.LessThan;

                lexer.nextToken();
                if (lexer.token == Token.EQ) {
                    lexer.nextToken();
                    op = SQLBinaryOperator.LessThanOrEqual;
                }

                rightExp = bitOr();
                expr = new SQLBinaryOpExpr(expr, op, rightExp, getDbType());
            }
            break;
            case LTEQ: {
                lexer.nextToken();
                rightExp = bitOr();

                // rightExp = relationalRest(rightExp);

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThanOrEqual, rightExp, getDbType());
            }
            break;
            case LTEQGT: {
                lexer.nextToken();
                rightExp = bitOr();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThanOrEqualOrGreaterThan, rightExp, getDbType());
            }
            break;
            case GT: {
                SQLBinaryOperator op = SQLBinaryOperator.GreaterThan;

                lexer.nextToken();

                if (lexer.token == Token.EQ) {
                    lexer.nextToken();
                    op = SQLBinaryOperator.GreaterThanOrEqual;
                }

                rightExp = bitOr();

                expr = new SQLBinaryOpExpr(expr, op, rightExp, getDbType());
            }
            break;
            case GTEQ:{
                lexer.nextToken();
                rightExp = bitOr();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.GreaterThanOrEqual, rightExp, getDbType());
            }
            break;
            case BANGLT:{
                lexer.nextToken();
                rightExp = bitOr();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotLessThan, rightExp, getDbType());
            }
            break;
            case BANGGT:
                lexer.nextToken();
                rightExp = bitOr();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotGreaterThan, rightExp, getDbType());
                break;
            case LTGT:
                lexer.nextToken();
                rightExp = bitOr();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThanOrGreater, rightExp, getDbType());
                break;
            case LIKE:
                lexer.nextTokenValue();
                rightExp = bitOr();

                if (rightExp.getClass() == SQLIdentifierExpr.class) {
                    String name = ((SQLIdentifierExpr) rightExp).getName();
                    int length = name.length();
                    if(length > 1 && name.charAt(0) == name.charAt(length -1 )) {
                        rightExp = new SQLCharExpr(name.substring(1, length - 1));
                    }
                }

                // rightExp = relationalRest(rightExp);

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Like, rightExp, getDbType());

                if (lexer.token == Token.ESCAPE) {
                    lexer.nextToken();
                    rightExp = primary();
                    expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Escape, rightExp, getDbType());
                }
                break;
            case ILIKE:
                lexer.nextToken();
                rightExp = bitOr();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.ILike, rightExp, getDbType());
                break;
            case MONKEYS_AT_AT:
                lexer.nextToken();
                rightExp = bitOr();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.AT_AT, rightExp, getDbType());
                break;
            case MONKEYS_AT_GT:
                lexer.nextToken();
                rightExp = bitOr();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Array_Contains, rightExp, getDbType());
                break;
            case LT_MONKEYS_AT:
                lexer.nextToken();
                rightExp = bitOr();

                rightExp = relationalRest(rightExp);

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Array_ContainedBy, rightExp, getDbType());
                break;
            case NOT:
                lexer.nextToken();
                expr = notRationalRest(expr);
                break;
            case BETWEEN:
                lexer.nextToken();
                SQLExpr beginExpr = relational();
                accept(Token.AND);
                SQLExpr endExpr = relational();
                expr = new SQLBetweenExpr(expr, beginExpr, endExpr);
                break;
            case IN:
            case CONTAINS:
                expr = inRest(expr);
                break;
            case EQEQ:
                if (JdbcConstants.ODPS.equals(dbType)) {
                    lexer.nextToken();
                    try {
                        rightExp = bitOr();
                    } catch (EOFParserException e) {
                        throw new ParserException("EOF, " + expr + "=", e);
                    }

                    if (lexer.token == Token.COLONEQ) {
                        lexer.nextToken();
                        SQLExpr colonExpr = expr();
                        rightExp = new SQLBinaryOpExpr(rightExp, SQLBinaryOperator.Assignment, colonExpr, dbType);
                    }

                    expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Equality, rightExp, dbType);
                } else {
                    return expr;
                }
                break;
            case TILDE:
                if (JdbcConstants.POSTGRESQL.equals(lexer.dbType)) {
                    lexer.nextToken();

                    rightExp = relational();

                    rightExp = relationalRest(rightExp);

                    expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.POSIX_Regular_Match, rightExp, getDbType());
                } else {
                    return expr;
                }
                break;
            case TILDE_STAR:
                if (JdbcConstants.POSTGRESQL.equals(lexer.dbType)) {
                    lexer.nextToken();
                    rightExp = relational();
                    expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.POSIX_Regular_Match_Insensitive, rightExp, getDbType());
                } else {
                    return expr;
                }
                break;
            case BANG_TILDE:
                if (JdbcConstants.POSTGRESQL.equals(lexer.dbType)) {
                    lexer.nextToken();
                    rightExp = relational();
                    expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.POSIX_Regular_Not_Match, rightExp, getDbType());
                } else {
                    return expr;
                }
                break;
            case BANG_TILDE_STAR:
                if (JdbcConstants.POSTGRESQL.equals(lexer.dbType)) {
                    lexer.nextToken();
                    rightExp = relational();
                    expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.POSIX_Regular_Not_Match_POSIX_Regular_Match_Insensitive, rightExp, getDbType());
                } else {
                    return expr;
                }
                break;
            case TILDE_EQ:
                if (JdbcConstants.POSTGRESQL.equals(lexer.dbType)) {
                    lexer.nextToken();
                    rightExp = relational();
                    expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.SAME_AS, rightExp, getDbType());
                } else {
                    return expr;
                }
                break;
            case RLIKE:
                lexer.nextToken();
                rightExp = relational();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.RLike, rightExp, getDbType());
                break;
            case IDENTIFIER:
                long hash = lexer.hash_lower;
                if (hash == FnvHash.Constants.SOUNDS) {
                    lexer.nextToken();
                    accept(Token.LIKE);

                    rightExp = relational();

                    expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.SoudsLike, rightExp, getDbType());
                } else if (hash == FnvHash.Constants.REGEXP) {
                    lexer.nextToken();
                    rightExp = relational();

                    return new SQLBinaryOpExpr(expr, SQLBinaryOperator.RegExp, rightExp, JdbcConstants.MYSQL);

                } else if (hash == FnvHash.Constants.SIMILAR && JdbcConstants.POSTGRESQL.equals(lexer.dbType)) {
                    lexer.nextToken();
                    accept(Token.TO);

                    rightExp = relational();

                    expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.SIMILAR_TO, rightExp, getDbType());
                } else {
                    return expr;
                }
                break;
            default:
                break;
        }

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
                expr = relationalRest(expr);
                break;
            default:
                break;
        }

        return expr;
    }

    public SQLExpr notRationalRest(SQLExpr expr) {
        switch (lexer.token) {
            case LIKE:
                lexer.nextTokenValue();
                SQLExpr rightExp = bitOr();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotLike, rightExp, getDbType());

                if (lexer.token == Token.ESCAPE) {
                    lexer.nextToken();
                    rightExp = bitOr();
                    expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Escape, rightExp, getDbType());
                }
                break;
            case IN:
                lexer.nextToken();

                SQLInListExpr inListExpr = new SQLInListExpr(expr, true);
                if (lexer.token == Token.LPAREN) {
                    lexer.nextToken();

                    exprList(inListExpr.getTargetList(), inListExpr);
                    expr = inListExpr;

                    accept(Token.RPAREN);
                } else {
                    SQLExpr valueExpr = this.primary();
                    valueExpr.setParent(inListExpr);
                    inListExpr.getTargetList().add(valueExpr);
                    expr = inListExpr;
                }

                if (inListExpr.getTargetList().size() == 1) {
                    SQLExpr targetExpr = inListExpr.getTargetList().get(0);
                    if (targetExpr instanceof SQLQueryExpr) {
                        SQLInSubQueryExpr inSubQueryExpr = new SQLInSubQueryExpr();
                        inSubQueryExpr.setNot(true);
                        inSubQueryExpr.setExpr(inListExpr.getExpr());
                        inSubQueryExpr.setSubQuery(((SQLQueryExpr) targetExpr).getSubQuery());
                        expr = inSubQueryExpr;
                    }
                }

                break;
            case CONTAINS:
                lexer.nextToken();

                SQLContainsExpr containsExpr = new SQLContainsExpr(expr, true);
                if (lexer.token == Token.LPAREN) {
                    lexer.nextToken();

                    exprList(containsExpr.getTargetList(), containsExpr);
                    expr = containsExpr;

                    accept(Token.RPAREN);
                } else {
                    SQLExpr valueExpr = this.primary();
                    valueExpr.setParent(containsExpr);
                    containsExpr.getTargetList().add(valueExpr);
                    expr = containsExpr;
                }

                if (containsExpr.getTargetList().size() == 1) {
                    SQLExpr targetExpr = containsExpr.getTargetList().get(0);
                    if (targetExpr instanceof SQLQueryExpr) {
                        SQLInSubQueryExpr inSubQueryExpr = new SQLInSubQueryExpr();
                        inSubQueryExpr.setNot(true);
                        inSubQueryExpr.setExpr(containsExpr.getExpr());
                        inSubQueryExpr.setSubQuery(((SQLQueryExpr) targetExpr).getSubQuery());
                        expr = inSubQueryExpr;
                    }
                }

                break;
            case BETWEEN:
                lexer.nextToken();
                SQLExpr beginExpr = relational();
                accept(Token.AND);
                SQLExpr endExpr = relational();

                expr = new SQLBetweenExpr(expr, true, beginExpr, endExpr);
                break;
            case ILIKE:
                lexer.nextToken();
                rightExp = bitOr();

                return new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotILike, rightExp, getDbType());
            case LPAREN:
                expr = this.primary();
                break;
            case RLIKE:
                lexer.nextToken();
                rightExp = bitOr();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotRLike, rightExp, getDbType());
                break;
            case IDENTIFIER:
                long hash = lexer.hash_lower;
                if (hash == FnvHash.Constants.REGEXP) {
                    lexer.nextToken();
                    rightExp = bitOr();
                    expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotRegExp, rightExp, getDbType());
                }
                break;
            default:
                throw new ParserException("TODO " + lexer.info());
        }

        return expr;
    }

    public SQLDataType parseDataType() {
        return parseDataType(true);
    }

    public SQLDataType parseDataType(boolean restrict) {
        Token token = lexer.token;
        if (token == Token.DEFAULT || token == Token.NOT || token == Token.NULL) {
            return null;
        }

        SQLName typeExpr = name();
        final long typeNameHashCode = typeExpr.nameHashCode64();
        String typeName = typeExpr.toString();

        if (typeNameHashCode == FnvHash.Constants.LONG
                && lexer.identifierEquals(FnvHash.Constants.BYTE)
                && JdbcConstants.MYSQL.equals(getDbType()) //
                ) {
            typeName += (' ' + lexer.stringVal());
            lexer.nextToken();
        } else if (typeNameHashCode == FnvHash.Constants.DOUBLE
                && JdbcConstants.POSTGRESQL.equals(getDbType()) //
                ) {
            typeName += (' ' + lexer.stringVal());
            lexer.nextToken();
        }

        if (typeNameHashCode == FnvHash.Constants.UNSIGNED) {
            if (lexer.token == Token.IDENTIFIER) {
                typeName += (' ' + lexer.stringVal());
                lexer.nextToken();
            }
        }

        if (isCharType(typeName)) {
            SQLCharacterDataType charType = new SQLCharacterDataType(typeName);

            if (lexer.token == Token.LPAREN) {
                lexer.nextToken();
                SQLExpr arg = this.expr();
                arg.setParent(charType);
                charType.addArgument(arg);
                accept(Token.RPAREN);
            }

            charType = (SQLCharacterDataType) parseCharTypeRest(charType);

            if (lexer.token == Token.HINT) {
                List<SQLCommentHint> hints = this.parseHints();
                charType.setHints(hints);
            }

            return charType;
        }

        if ("character".equalsIgnoreCase(typeName) && "varying".equalsIgnoreCase(lexer.stringVal())) {
            typeName += ' ' + lexer.stringVal();
            lexer.nextToken();
        }

        SQLDataType dataType = new SQLDataTypeImpl(typeName);
        dataType.setDbType(dbType);

        return parseDataTypeRest(dataType);
    }

    protected SQLDataType parseDataTypeRest(SQLDataType dataType) {
        if (lexer.token == Token.LPAREN) {
            lexer.nextToken();
            exprList(dataType.getArguments(), dataType);
            accept(Token.RPAREN);
        }
        
        if (lexer.identifierEquals(FnvHash.Constants.PRECISION)
                && dataType.nameHashCode64() == FnvHash.Constants.DOUBLE) {
            lexer.nextToken();
            dataType.setName("DOUBLE PRECISION");
        }

        if (FnvHash.Constants.TIMESTAMP == dataType.nameHashCode64()) {
            if (lexer.identifierEquals(FnvHash.Constants.WITHOUT)) {
                lexer.nextToken();
                acceptIdentifier("TIME");
                acceptIdentifier("ZONE");
                dataType.setWithTimeZone(false);
            } else if (lexer.token == Token.WITH) {
                lexer.nextToken();
                acceptIdentifier("TIME");
                acceptIdentifier("ZONE");
                dataType.setWithTimeZone(true);
            }
        }

        return dataType;
    }

    protected boolean isCharType(String dataTypeName) {
        long hash = FnvHash.hashCode64(dataTypeName);
        return isCharType(hash);
    }


    protected boolean isCharType(long hash) {
        return hash == FnvHash.Constants.CHAR
                || hash == FnvHash.Constants.VARCHAR
                || hash == FnvHash.Constants.NCHAR
                || hash == FnvHash.Constants.NVARCHAR
                || hash == FnvHash.Constants.TINYTEXT
                || hash == FnvHash.Constants.TEXT
                || hash == FnvHash.Constants.MEDIUMTEXT
                || hash == FnvHash.Constants.LONGTEXT
                ;
    }

    protected SQLDataType parseCharTypeRest(SQLCharacterDataType charType) {
        if (lexer.token == Token.BINARY) {
            charType.setHasBinary(true);
            lexer.nextToken();
        }

        if (lexer.identifierEquals(FnvHash.Constants.CHARACTER)) {
            lexer.nextToken();

            accept(Token.SET);

            if (lexer.token != Token.IDENTIFIER
                    && lexer.token != Token.LITERAL_CHARS
                    && lexer.token != Token.BINARY) {
                throw new ParserException(lexer.info());
            }
            charType.setCharSetName(lexer.stringVal());
            lexer.nextToken();
        } else  if (lexer.identifierEquals(FnvHash.Constants.CHARSET)) {
            lexer.nextToken();

            if (lexer.token != Token.IDENTIFIER
                    && lexer.token != Token.LITERAL_CHARS
                    && lexer.token != Token.BINARY) {
                throw new ParserException(lexer.info());
            }
            charType.setCharSetName(lexer.stringVal());
            lexer.nextToken();
        }

        if (lexer.token == Token.BINARY) {
            charType.setHasBinary(true);
            lexer.nextToken();
        }

        if (lexer.identifierEquals(FnvHash.Constants.COLLATE)) {
            lexer.nextToken();

            if (lexer.token == Token.LITERAL_ALIAS) {
                charType.setCollate(lexer.stringVal());
            } else if (lexer.token == Token.IDENTIFIER) {
                charType.setCollate(lexer.stringVal());
            } else {
                throw new ParserException();
            }

            lexer.nextToken();
        }

        return charType;
    }

    public void accept(Token token) {
        if (lexer.token == token) {
            lexer.nextToken();
        } else {
            throw new ParserException("syntax error, expect " + token + ", actual " + lexer.token + " "
                                      + lexer.info());
        }
    }

    public SQLColumnDefinition parseColumn() {
        SQLColumnDefinition column = createColumnDefinition();
        column.setName(name());

        final Token token = lexer.token;
        if (token != Token.SET //
                && token != Token.DROP
                && token != Token.PRIMARY
                && token != Token.RPAREN) {
            column.setDataType(parseDataType());
        }
        return parseColumnRest(column);
    }

    public SQLColumnDefinition createColumnDefinition() {
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setDbType(dbType);
        return column;
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        if (lexer.token == Token.DEFAULT) {
            lexer.nextToken();
            column.setDefaultExpr(bitOr());
            return parseColumnRest(column);
        }

        if (lexer.token == Token.NOT) {
            lexer.nextToken();
            accept(Token.NULL);
            SQLNotNullConstraint notNull = new SQLNotNullConstraint();
            if (lexer.token == Token.HINT) {
                List<SQLCommentHint> hints = this.parseHints();
                notNull.setHints(hints);
            }
            column.addConstraint(notNull);
            return parseColumnRest(column);
        }

        if (lexer.token == Token.NULL) {
            lexer.nextToken();
            column.getConstraints().add(new SQLNullConstraint());
            return parseColumnRest(column);
        }

        if (lexer.token == Token.PRIMARY) {
            lexer.nextToken();
            accept(Token.KEY);
            column.addConstraint(new SQLColumnPrimaryKey());
            return parseColumnRest(column);
        }

        if (lexer.token == Token.UNIQUE) {
            lexer.nextToken();
            if (lexer.token == Token.KEY) {
                lexer.nextToken();
            }
            column.addConstraint(new SQLColumnUniqueKey());
            return parseColumnRest(column);
        }

        if (lexer.token == Token.KEY) {
            lexer.nextToken();
            column.addConstraint(new SQLColumnUniqueKey());
            return parseColumnRest(column);
        }

        if (lexer.token == Token.REFERENCES) {
            SQLColumnReference ref = parseReference();
            column.addConstraint(ref);
            return parseColumnRest(column);
        }

        if (lexer.token == Token.CONSTRAINT) {
            lexer.nextToken();

            SQLName name = this.name();

            if (lexer.token == Token.PRIMARY) {
                lexer.nextToken();
                accept(Token.KEY);
                SQLColumnPrimaryKey pk = new SQLColumnPrimaryKey();
                pk.setName(name);
                column.addConstraint(pk);
                return parseColumnRest(column);
            }

            if (lexer.token == Token.UNIQUE) {
                lexer.nextToken();
                SQLColumnUniqueKey uk = new SQLColumnUniqueKey();
                uk.setName(name);

                column.addConstraint(uk);
                return parseColumnRest(column);
            }

            if (lexer.token == Token.REFERENCES) {
                SQLColumnReference ref = parseReference();
                ref.setName(name);
                column.addConstraint(ref);
                return parseColumnRest(column);
            }

            if (lexer.token == Token.NOT) {
                lexer.nextToken();
                accept(Token.NULL);
                SQLNotNullConstraint notNull = new SQLNotNullConstraint();
                notNull.setName(name);
                column.addConstraint(notNull);
                return parseColumnRest(column);
            }

            if (lexer.token == Token.CHECK) {
                SQLColumnCheck check = parseColumnCheck();
                check.setName(name);
                check.setParent(column);
                column.addConstraint(check);
                return parseColumnRest(column);
            }

            if (lexer.token == Token.DEFAULT) {
                lexer.nextToken();
                SQLExpr expr = this.expr();
                column.setDefaultExpr(expr);
                return parseColumnRest(column);
            }

            throw new ParserException("TODO : " + lexer.info());
        }

        if (lexer.token == Token.CHECK) {
            SQLColumnCheck check = parseColumnCheck();
            column.addConstraint(check);
            return parseColumnRest(column);
        }

        if (lexer.token == Token.COMMENT) {
            lexer.nextToken();

            if (lexer.token == Token.LITERAL_ALIAS) {
                String alias = lexer.stringVal();
                if (alias.length() > 2 && alias.charAt(0) == '"' && alias.charAt(alias.length() - 1) == '"') {
                    alias = alias.substring(1, alias.length() - 1);
                }
                column.setComment(alias);
                lexer.nextToken();
            } else {
                column.setComment(primary());
            }
            return parseColumnRest(column);
        }

        if (lexer.identifierEquals(FnvHash.Constants.AUTO_INCREMENT)) {
            lexer.nextToken();
            column.setAutoIncrement(true);
            return parseColumnRest(column);
        }

        return column;
    }

    private SQLColumnReference parseReference() {
        SQLColumnReference fk = new SQLColumnReference();

        lexer.nextToken();
        fk.setTable(this.name());
        accept(Token.LPAREN);
        this.names(fk.getColumns(), fk);
        accept(Token.RPAREN);

        if (lexer.identifierEquals(FnvHash.Constants.MATCH)) {
            lexer.nextToken();
            if (lexer.identifierEquals("FULL") || lexer.token() == Token.FULL) {
                fk.setReferenceMatch(SQLForeignKeyImpl.Match.FULL);
                lexer.nextToken();
            } else if (lexer.identifierEquals(FnvHash.Constants.PARTIAL)) {
                fk.setReferenceMatch(SQLForeignKeyImpl.Match.PARTIAL);
                lexer.nextToken();
            } else if (lexer.identifierEquals(FnvHash.Constants.SIMPLE)) {
                fk.setReferenceMatch(SQLForeignKeyImpl.Match.SIMPLE);
                lexer.nextToken();
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }
        }

        while (lexer.token() == Token.ON) {
            lexer.nextToken();

            if (lexer.token() == Token.DELETE) {
                lexer.nextToken();

                SQLForeignKeyImpl.Option option = parseReferenceOption();
                fk.setOnDelete(option);
            } else if (lexer.token() == Token.UPDATE) {
                lexer.nextToken();

                SQLForeignKeyImpl.Option option = parseReferenceOption();
                fk.setOnUpdate(option);
            } else {
                throw new ParserException("syntax error, expect DELETE or UPDATE, actual " + lexer.token() + " "
                        + lexer.info());
            }
        }

        return fk;
    }

    protected SQLForeignKeyImpl.Option parseReferenceOption() {
        SQLForeignKeyImpl.Option option;
        if (lexer.token() == Token.RESTRICT || lexer.identifierEquals(FnvHash.Constants.RESTRICT)) {
            option = SQLForeignKeyImpl.Option.RESTRICT;
            lexer.nextToken();
        } else if (lexer.identifierEquals(FnvHash.Constants.CASCADE)) {
            option = SQLForeignKeyImpl.Option.CASCADE;
            lexer.nextToken();
        } else if (lexer.token() == Token.SET) {
            lexer.nextToken();
            accept(Token.NULL);
            option = SQLForeignKeyImpl.Option.SET_NULL;
        } else if (lexer.identifierEquals(FnvHash.Constants.NO)) {
            lexer.nextToken();
            if (lexer.identifierEquals(FnvHash.Constants.ACTION)) {
                option = SQLForeignKeyImpl.Option.NO_ACTION;
                lexer.nextToken();
            } else {
                throw new ParserException("syntax error, expect ACTION, actual " + lexer.token() + " "
                        + lexer.info());
            }
        } else {
            throw new ParserException("syntax error, expect ACTION, actual " + lexer.token() + " "
                    + lexer.info());
        }

        return option;
    }

    protected SQLColumnCheck parseColumnCheck() {
        lexer.nextToken();
        SQLExpr expr = this.expr();
        SQLColumnCheck check = new SQLColumnCheck(expr);

        if (lexer.token == Token.DISABLE) {
            lexer.nextToken();
            check.setEnable(false);
        } else if (lexer.token == Token.ENABLE) {
            lexer.nextToken();
            check.setEnable(true);
        } else if (lexer.identifierEquals(FnvHash.Constants.VALIDATE)) {
            lexer.nextToken();
            check.setValidate(Boolean.TRUE);
        } else if (lexer.identifierEquals(FnvHash.Constants.NOVALIDATE)) {
            lexer.nextToken();
            check.setValidate(Boolean.FALSE);
        } else if (lexer.identifierEquals(FnvHash.Constants.RELY)) {
            lexer.nextToken();
            check.setRely(Boolean.TRUE);
        } else if (lexer.identifierEquals(FnvHash.Constants.NORELY)) {
            lexer.nextToken();
            check.setRely(Boolean.FALSE);
        }
        return check;
    }

    public SQLPrimaryKey parsePrimaryKey() {
        accept(Token.PRIMARY);
        accept(Token.KEY);

        SQLPrimaryKeyImpl pk = new SQLPrimaryKeyImpl();

        if (lexer.identifierEquals(FnvHash.Constants.CLUSTERED)) {
            lexer.nextToken();
            pk.setClustered(true);
        }

        accept(Token.LPAREN);
        orderBy(pk.getColumns(), pk);
        accept(Token.RPAREN);

        return pk;
    }

    public SQLUnique parseUnique() {
        accept(Token.UNIQUE);

        SQLUnique unique = new SQLUnique();
        accept(Token.LPAREN);
        orderBy(unique.getColumns(), unique);
        accept(Token.RPAREN);

        if (lexer.token == Token.DISABLE) {
            lexer.nextToken();
            unique.setEnable(false);
        } else if (lexer.token == Token.ENABLE) {
            lexer.nextToken();
            unique.setEnable(true);
        } else if (lexer.identifierEquals(FnvHash.Constants.VALIDATE)) {
            lexer.nextToken();
            unique.setValidate(Boolean.TRUE);
        } else if (lexer.identifierEquals(FnvHash.Constants.NOVALIDATE)) {
            lexer.nextToken();
            unique.setValidate(Boolean.FALSE);
        } else if (lexer.identifierEquals(FnvHash.Constants.RELY)) {
            lexer.nextToken();
            unique.setRely(Boolean.TRUE);
        } else if (lexer.identifierEquals(FnvHash.Constants.NORELY)) {
            lexer.nextToken();
            unique.setRely(Boolean.FALSE);
        }

        return unique;
    }

    public void parseAssignItem(List<SQLAssignItem> outList, SQLObject parent) {
        accept(Token.LPAREN);
        for (;;) {
            SQLAssignItem item = this.parseAssignItem();
            item.setParent(parent);
            outList.add(item);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);
    }

    public SQLAssignItem parseAssignItem() {
        SQLAssignItem item = new SQLAssignItem();

        SQLExpr var = primary();

        if (var instanceof SQLIdentifierExpr) {
            var = new SQLVariantRefExpr(((SQLIdentifierExpr) var).getName());
        }
        item.setTarget(var);
        if (lexer.token == Token.COLONEQ) {
            lexer.nextToken();
        } else if (lexer.token == Token.TRUE || lexer.identifierEquals(FnvHash.Constants.TRUE)) {
            lexer.nextToken();
            item.setValue(new SQLBooleanExpr(true));
            return item;
        } else if (lexer.token == Token.ON) {
            lexer.nextToken();
            item.setValue(new SQLIdentifierExpr("ON"));
            return item;
        } else {
            if (lexer.token == Token.EQ) {
                lexer.nextToken();
            } else if (JdbcConstants.DB2.equals(dbType)) {
                // skip
            } else {
                accept(Token.EQ);
            }
        }

        if (lexer.token == Token.ON) {
            item.setValue(new SQLIdentifierExpr(lexer.stringVal()));
            lexer.nextToken();
        } else {
            if (lexer.token == Token.ALL) {
                item.setValue(new SQLIdentifierExpr(lexer.stringVal()));
                lexer.nextToken();
            } else {
                SQLExpr expr = expr();

                if (lexer.token == Token.COMMA && JdbcConstants.POSTGRESQL.equals(dbType)) {
                    SQLListExpr listExpr = new SQLListExpr();
                    listExpr.addItem(expr);
                    expr.setParent(listExpr);
                    do {
                        lexer.nextToken();
                        SQLExpr listItem = this.expr();
                        listItem.setParent(listExpr);
                        listExpr.addItem(listItem);
                    } while (lexer.token == Token.COMMA);
                    item.setValue(listExpr);
                } else {
                    item.setValue(expr);
                }
            }
        }

        return item;
    }

    public List<SQLCommentHint> parseHints() {
        List<SQLCommentHint> hints = new ArrayList<SQLCommentHint>();
        parseHints(hints);
        return hints;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void parseHints(List hints) {
        if (lexer.token == Token.HINT) {
            SQLCommentHint hint = new SQLCommentHint(lexer.stringVal());

            if (lexer.commentCount > 0) {
                hint.addBeforeComment(lexer.comments);
            }

            hints.add(hint);
            lexer.nextToken();
        }
    }

    public SQLConstraint parseConstaint() {
        SQLName name = null;

        if (lexer.token == Token.CONSTRAINT) {
            lexer.nextToken();
            name = this.name();
        }

        SQLConstraint constraint;
        if (lexer.token == Token.PRIMARY) {
            constraint = parsePrimaryKey();
        } else if (lexer.token == Token.UNIQUE) {
            constraint = parseUnique();
        } else if (lexer.token == Token.KEY) {
            constraint = parseUnique();
        } else if (lexer.token == Token.FOREIGN) {
            constraint = parseForeignKey();
        } else if (lexer.token == Token.CHECK) {
            constraint = parseCheck();
        } else {
            throw new ParserException("TODO : " + lexer.info());
        }

        constraint.setName(name);

        return constraint;
    }

    public SQLCheck parseCheck() {
        accept(Token.CHECK);
        SQLCheck check = createCheck();
        accept(Token.LPAREN);
        check.setExpr(this.expr());
        accept(Token.RPAREN);
        return check;
    }

    protected SQLCheck createCheck() {
        return new SQLCheck();
    }

    public SQLForeignKeyConstraint parseForeignKey() {
        accept(Token.FOREIGN);
        accept(Token.KEY);

        SQLForeignKeyImpl fk = createForeignKey();

        accept(Token.LPAREN);
        this.names(fk.getReferencingColumns(), fk);
        accept(Token.RPAREN);

        accept(Token.REFERENCES);

        fk.setReferencedTableName(this.name());

        if (lexer.token == Token.LPAREN) {
            lexer.nextToken();
            this.names(fk.getReferencedColumns(), fk);
            accept(Token.RPAREN);
        }

        if (lexer.token == Token.ON) {
            lexer.nextToken();
            accept(Token.DELETE);
            if (lexer.identifierEquals(FnvHash.Constants.CASCADE)) {
                lexer.nextToken();
                fk.setOnDeleteCascade(true);
            } else {
                accept(Token.SET);
                accept(Token.NULL);
                fk.setOnDeleteSetNull(true);
            }
        }

        return fk;
    }

    protected SQLForeignKeyImpl createForeignKey() {
        return new SQLForeignKeyImpl();
    }

    public SQLSelectItem parseSelectItem() {
        SQLExpr expr;
        boolean connectByRoot = false;
        Token token = lexer.token;
        if (token == Token.IDENTIFIER) {
            String ident = lexer.stringVal();
            long hash_lower = lexer.hash_lower();
            lexer.nextTokenComma();

            if (hash_lower == FnvHash.Constants.CONNECT_BY_ROOT) {
                connectByRoot = lexer.token != Token.LPAREN;
                if (connectByRoot) {
                    expr = new SQLIdentifierExpr(lexer.stringVal());
                    lexer.nextToken();
                } else {
                    expr = new SQLIdentifierExpr(ident);
                }
            } else if (FnvHash.Constants.DATE == hash_lower
                    && lexer.token == Token.LITERAL_CHARS
                    && (JdbcConstants.ORACLE.equals(getDbType())
                    || JdbcConstants.POSTGRESQL.equals(getDbType()))) {
                String literal = lexer.stringVal();
                lexer.nextToken();

                SQLDateExpr dateExpr = new SQLDateExpr();
                dateExpr.setLiteral(literal);

                expr = dateExpr;
            } else {
                expr = new SQLIdentifierExpr(ident, hash_lower);
            }

            token = lexer.token;

            if (token == Token.DOT) {
                lexer.nextTokenIdent();
                String name;
                long name_hash_lower;

                if (lexer.token == Token.STAR) {
                    name = "*";
                    name_hash_lower = FnvHash.Constants.STAR;
                } else {
                    name = lexer.stringVal();
                    name_hash_lower = lexer.hash_lower();
                }

                lexer.nextTokenComma();

                token = lexer.token;
                if (token == Token.LPAREN) {
                    boolean aggregate = hash_lower == FnvHash.Constants.WMSYS && name_hash_lower == FnvHash.Constants.WM_CONCAT;
                    expr = methodRest(expr, name, aggregate);
                    token = lexer.token;
                } else {
                    if (name_hash_lower == FnvHash.Constants.NEXTVAL) {
                        expr = new SQLSequenceExpr((SQLIdentifierExpr) expr, SQLSequenceExpr.Function.NextVal);
                    } else if (name_hash_lower == FnvHash.Constants.CURRVAL) {
                        expr = new SQLSequenceExpr((SQLIdentifierExpr) expr, SQLSequenceExpr.Function.CurrVal);
                    } else if (name_hash_lower == FnvHash.Constants.PREVVAL) {
                        expr = new SQLSequenceExpr((SQLIdentifierExpr) expr, SQLSequenceExpr.Function.PrevVal);
                    } else {
                        expr = new SQLPropertyExpr(expr, name, name_hash_lower);
                    }
                }
            }

            if (token == Token.COMMA) {
                return new SQLSelectItem(expr, null, connectByRoot);
            }

            if (token == Token.AS) {
                lexer.nextToken();
                String as = null;
                if (lexer.token != Token.COMMA && lexer.token != Token.FROM) {
                    as = lexer.stringVal();

                    lexer.nextTokenComma();

                    if (lexer.token == Token.DOT) {
                        lexer.nextToken();
                        as += '.' + lexer.stringVal();
                        lexer.nextToken();
                    }
                }

                return new SQLSelectItem(expr, as, connectByRoot);
            }

            if (token == Token.LITERAL_ALIAS) {
                String as = lexer.stringVal();
                lexer.nextTokenComma();
                return new SQLSelectItem(expr, as, connectByRoot);
            }

            if ((token == Token.IDENTIFIER && hash_lower != FnvHash.Constants.CURRENT)
                    || token == Token.MODEL) {
                String as;
                if (lexer.hash_lower == FnvHash.Constants.FORCE && JdbcConstants.MYSQL.equals(dbType)) {
                    String force = lexer.stringVal();

                    Lexer.SavePoint savePoint = lexer.mark();
                    lexer.nextToken();

                    if (lexer.token == Token.PARTITION) {
                        lexer.reset(savePoint);
                        as = null;
                    } else {
                        as = force;
                        lexer.nextTokenComma();
                    }
                } else {
                    as = lexer.stringVal();
                    lexer.nextTokenComma();
                }
                return new SQLSelectItem(expr, as, connectByRoot);
            }

            if (token == Token.LPAREN) {
                lexer.nextToken();
                expr = this.methodRest(expr, false);
            } else {
                expr = this.primaryRest(expr);
            }
            expr = this.exprRest(expr);
        } else if (token == Token.STAR) {
            expr = new SQLAllColumnExpr();
            lexer.nextToken();
            return new SQLSelectItem(expr, null, connectByRoot);
        } else if (token == Token.DO || token == Token.JOIN) {
            expr = this.name();
            expr = this.exprRest(expr);
        } else {
            expr = expr();
        }

        final String alias;
        switch (lexer.token) {
            case FULL:
            case MODEL:
            case TABLESPACE:
                alias = lexer.stringVal();
                lexer.nextToken();
                break;
            default:
                alias = as();
                break;
        }

        SQLSelectItem selectItem = new SQLSelectItem(expr, alias, connectByRoot);
        if (lexer.token == Token.HINT && !lexer.isEnabled(SQLParserFeature.StrictForWall)) {
            String comment = "/*" + lexer.stringVal() + "*/";
            selectItem.addAfterComment(comment);
            lexer.nextToken();
        }

        return selectItem;
    }

    public SQLExpr parseGroupingSet() {
        String tmp = lexer.stringVal();
        acceptIdentifier("GROUPING");
        
        SQLGroupingSetExpr expr = new SQLGroupingSetExpr();
        
        if (lexer.token == Token.SET || lexer.identifierEquals(FnvHash.Constants.SET)) {
            lexer.nextToken();
        } else {
            return new SQLIdentifierExpr(tmp);
        }

        accept(Token.LPAREN);

        this.exprList(expr.getParameters(), expr);

        accept(Token.RPAREN);

        return expr;
    }

    protected SQLPartition parsePartition() {
        throw new ParserException("TODO");
    }

    protected SQLPartitionBy parsePartitionBy() {
        throw new ParserException("TODO");
    }
    
    public SQLPartitionValue parsePartitionValues() {
        if (lexer.token != Token.VALUES) {
            return null;
        }
        lexer.nextToken();

        SQLPartitionValue values = null;

        if (lexer.token == Token.IN) {
            lexer.nextToken();
            values = new SQLPartitionValue(SQLPartitionValue.Operator.In);

            accept(Token.LPAREN);
            this.exprList(values.getItems(), values);
            accept(Token.RPAREN);
        } else if (lexer.identifierEquals(FnvHash.Constants.LESS)) {
            lexer.nextToken();
            acceptIdentifier("THAN");

            values = new SQLPartitionValue(SQLPartitionValue.Operator.LessThan);

            if (lexer.identifierEquals(FnvHash.Constants.MAXVALUE)) {
                SQLIdentifierExpr maxValue = new SQLIdentifierExpr(lexer.stringVal());
                lexer.nextToken();
                maxValue.setParent(values);
                values.addItem(maxValue);
            } else {
                accept(Token.LPAREN);
                this.exprList(values.getItems(), values);
                accept(Token.RPAREN);
            }
        } else if (lexer.token == Token.LPAREN) {
            values = new SQLPartitionValue(SQLPartitionValue.Operator.List);
            lexer.nextToken();
            this.exprList(values.getItems(), values);
            accept(Token.RPAREN);
        }

        return values;
    }
    
    protected static boolean isIdent(SQLExpr expr, String name) {
        if (expr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
            return identExpr.getName().equalsIgnoreCase(name);
        }
        return false;
    }

    public SQLLimit parseLimit() {
        if (lexer.token == Token.LIMIT) {
            lexer.nextTokenValue();

            SQLLimit limit = new SQLLimit();

            SQLExpr temp;
            if (lexer.token == Token.LITERAL_INT) {
                temp = new SQLIntegerExpr(lexer.integerValue());
                lexer.nextTokenComma();
                if (lexer.token != Token.COMMA && lexer.token != Token.EOF && lexer.token != Token.IDENTIFIER) {
                    temp = this.primaryRest(temp);
                    temp = this.exprRest(temp);
                }
            } else {
                temp = this.expr();
            }

            if (lexer.token == (Token.COMMA)) {
                limit.setOffset(temp);
                lexer.nextTokenValue();

                SQLExpr rowCount;
                if (lexer.token == Token.LITERAL_INT) {
                    rowCount = new SQLIntegerExpr(lexer.integerValue());
                    lexer.nextToken();
                    if (lexer.token != Token.EOF && lexer.token != Token.IDENTIFIER) {
                        rowCount = this.primaryRest(rowCount);
                        rowCount = this.exprRest(rowCount);
                    }
                } else {
                    rowCount = this.expr();
                }

                limit.setRowCount(rowCount);
            } else if (lexer.identifierEquals(FnvHash.Constants.OFFSET)) {
                limit.setRowCount(temp);
                lexer.nextToken();
                limit.setOffset(this.expr());
            } else {
                limit.setRowCount(temp);
            }
            return limit;
        }

        return null;
    }
}
