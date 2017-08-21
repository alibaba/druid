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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLNullConstraint;
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
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLUnique;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGPolygonExpr;
import com.alibaba.druid.util.JdbcConstants;

public class SQLExprParser extends SQLParser {

    public final static String[] AGGREGATE_FUNCTIONS = { "AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM" };

    protected String[]           aggregateFunctions  = AGGREGATE_FUNCTIONS;

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
        if (lexer.token() == Token.STAR) {
            lexer.nextToken();

            SQLExpr expr = new SQLAllColumnExpr();

            if (lexer.token() == Token.DOT) {
                lexer.nextToken();
                accept(Token.STAR);
                return new SQLPropertyExpr(expr, "*");
            }

            return expr;
        }

        SQLExpr expr = primary();

        if (lexer.token() == Token.COMMA) {
            return expr;
        }

        return exprRest(expr);
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
        expr = equalityRest(expr);
        expr = andRest(expr);
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
                SQLExpr rightExp = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BitwiseXor, rightExp, dbType);
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
        final Token token = lexer.token();
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
        }
        return expr;
    }
    
    public SQLIntegerExpr integerExpr() {
        SQLIntegerExpr intExpr = new SQLIntegerExpr(lexer.integerValue());
        accept(Token.LITERAL_INT);
        return intExpr;
    }

    public int parseIntValue() {
        if (lexer.token() == Token.LITERAL_INT) {
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

        final Token tok = lexer.token();

        switch (tok) {
            case LPAREN:
                lexer.nextToken();
 
                sqlExpr = expr();
                if (lexer.token() == Token.COMMA) {
                    SQLListExpr listExpr = new SQLListExpr();
                    listExpr.addItem(sqlExpr);
                    do {
                        lexer.nextToken();
                        listExpr.addItem(expr());
                    } while (lexer.token() == Token.COMMA);

                    sqlExpr = listExpr;
                }

                if (sqlExpr instanceof SQLBinaryOpExpr) {
                    ((SQLBinaryOpExpr) sqlExpr).setBracket(true);
                }
                
                accept(Token.RPAREN);
                break;
            case INSERT:
                lexer.nextToken();
                if (lexer.token() != Token.LPAREN) {
                    throw new ParserException("syntax error. " + lexer.info());
                }
                sqlExpr = new SQLIdentifierExpr("INSERT");
                break;
            case IDENTIFIER:
                String ident = lexer.stringVal();
                lexer.nextToken();

                if ("DATE".equalsIgnoreCase(ident)
                        && lexer.token() == Token.LITERAL_CHARS
                        && (JdbcConstants.ORACLE.equals(getDbType())
                            || JdbcConstants.POSTGRESQL.equals(getDbType()))) {
                    String literal = lexer.stringVal();
                    lexer.nextToken();

                    SQLDateExpr dateExpr = new SQLDateExpr();
                    dateExpr.setLiteral(literal);
                    sqlExpr = dateExpr;
                } else if (JdbcConstants.MYSQL.equalsIgnoreCase(dbType) && ident.startsWith("0x") && (ident.length() % 2) == 0) {
                    sqlExpr = new SQLHexExpr(ident.substring(2));
                } else {
                    sqlExpr = new SQLIdentifierExpr(ident);
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
            case LITERAL_CHARS:
                sqlExpr = new SQLCharExpr(lexer.stringVal());
                lexer.nextToken();
                break;
            case LITERAL_NCHARS:
                sqlExpr = new SQLNCharExpr(lexer.stringVal());
                lexer.nextToken();
                break;
            case VARIANT: {
                String varName = lexer.stringVal();
                lexer.nextToken();

                if (varName.equals(":") && lexer.token() == Token.IDENTIFIER && JdbcConstants.ORACLE.equals(dbType)) {
                    String part2 = lexer.stringVal();
                    lexer.nextToken();
                    varName += part2;
                }

                SQLVariantRefExpr varRefExpr = new SQLVariantRefExpr(varName);
                if (varRefExpr.getName().equals("@") && lexer.token() == Token.LITERAL_CHARS) {
                    varRefExpr.setName("@'" + lexer.stringVal() + "'");
                    lexer.nextToken();
                } else if (varRefExpr.getName().equals("@@") && lexer.token() == Token.LITERAL_CHARS) {
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
            case DISTINCT:
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
                sqlExpr = new SQLIdentifierExpr(lexer.stringVal());
                lexer.nextToken();
                break;
            case CASE:
                SQLCaseExpr caseExpr = new SQLCaseExpr();
                lexer.nextToken();
                if (lexer.token() != Token.WHEN) {
                    caseExpr.setValueExpr(expr());
                }

                accept(Token.WHEN);
                SQLExpr testExpr = expr();
                accept(Token.THEN);
                SQLExpr valueExpr = expr();
                SQLCaseExpr.Item caseItem = new SQLCaseExpr.Item(testExpr, valueExpr);
                caseExpr.addItem(caseItem);

                while (lexer.token() == Token.WHEN) {
                    lexer.nextToken();
                    testExpr = expr();
                    accept(Token.THEN);
                    valueExpr = expr();
                    caseItem = new SQLCaseExpr.Item(testExpr, valueExpr);
                    caseExpr.addItem(caseItem);
                }

                if (lexer.token() == Token.ELSE) {
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
                if (lexer.token() == Token.EXISTS) {
                    lexer.nextToken();
                    accept(Token.LPAREN);
                    sqlExpr = new SQLExistsExpr(createSelectParser().select(), true);
                    accept(Token.RPAREN);
                } else if (lexer.token() == Token.LPAREN) {
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
                switch (lexer.token()) {
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
                        sqlExpr = new SQLIdentifierExpr(lexer.stringVal());
                        lexer.nextToken();

                        if (lexer.token() == Token.LPAREN || lexer.token() == Token.LBRACKET) {
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
                    default:
                        throw new ParserException("TODO : " + lexer.info());
                }
                break;
            case PLUS:
                lexer.nextToken();
                switch (lexer.token()) {
                    case LITERAL_INT:
                        sqlExpr = new SQLIntegerExpr(lexer.integerValue());
                        lexer.nextToken();
                        break;
                    case LITERAL_FLOAT:
                        sqlExpr = lexer.numberExpr();
                        lexer.nextToken();
                        break;
                    case IDENTIFIER: // 当+号后面为字段的情况
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
                    default:
                        throw new ParserException("TODO " + lexer.info());
                }
                break;
            case TILDE:
                lexer.nextToken();
                SQLExpr unaryValueExpr = expr();
                SQLUnaryExpr unary = new SQLUnaryExpr(SQLUnaryOperator.Compl, unaryValueExpr);
                sqlExpr = unary;
                break;
            case QUES:
                lexer.nextToken();
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
            default:
                throw new ParserException("ERROR" + lexer.info());
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
        if (lexer.token() == Token.LPAREN) {
            accept(Token.LPAREN);

            if (lexer.token() == Token.IDENTIFIER) {
                SQLExpr expr = this.expr();
                SQLMethodInvokeExpr methodInvokeExpr = new SQLMethodInvokeExpr("ANY");
                methodInvokeExpr.addParameter(expr);
                accept(Token.RPAREN);
                return methodInvokeExpr;
            }

            SQLAnyExpr anyExpr = new SQLAnyExpr();
            SQLSelect anySubQuery = createSelectParser().select();
            anyExpr.setSubQuery(anySubQuery);
            accept(Token.RPAREN);

            anySubQuery.setParent(anyExpr);

            sqlExpr = anyExpr;
        } else {
            sqlExpr = new SQLIdentifierExpr("ANY");
        }
        return sqlExpr;
    }

    protected SQLExpr parseAliasExpr(String alias) {
        return new SQLIdentifierExpr('"' + alias + '"');
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
                String name = ((SQLIdentifierExpr) expr).getName();
                if ("CURRENT".equalsIgnoreCase(name)) {
                    lexer.nextToken();
                    SQLName cursorName = this.name();
                    return new SQLCurrentOfCursorExpr(cursorName);
                }
            }
        } else if (token == Token.FOR) {
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr idenExpr = (SQLIdentifierExpr) expr;
                String name = idenExpr.getName();
                if ("NEXTVAL".equalsIgnoreCase(name)) {
                    lexer.nextToken();
                    SQLName seqName = this.name();
                    SQLSequenceExpr seqExpr = new SQLSequenceExpr(seqName, SQLSequenceExpr.Function.NextVal);
                    return seqExpr;
                } else if ("CURRVAL".equalsIgnoreCase(name)) {
                    lexer.nextToken();
                    SQLName seqName = this.name();
                    SQLSequenceExpr seqExpr = new SQLSequenceExpr(seqName, SQLSequenceExpr.Function.CurrVal);
                    return seqExpr;
                } else if ("PREVVAL".equalsIgnoreCase(name)) {
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
        } else if (identifierEquals("SETS") //
                && expr.getClass() == SQLIdentifierExpr.class // 
                && "GROUPING".equalsIgnoreCase(((SQLIdentifierExpr) expr).getName())) {
            SQLGroupingSetExpr groupingSets = new SQLGroupingSetExpr();
            lexer.nextToken();
            
            accept(Token.LPAREN);
            
            for (;;) {
                SQLExpr item;
                if (lexer.token() == Token.LPAREN) {
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
                
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                
                accept(Token.COMMA);
            }

            this.exprList(groupingSets.getParameters(), groupingSets);

            accept(Token.RPAREN);
            
            return groupingSets;
        } else {
            if (lexer.token() == Token.LPAREN) {
                return methodRest(expr, true);
            }
        }

        return expr;
    }

    protected SQLExpr methodRest(SQLExpr expr, boolean acceptLPAREN) {
        if (acceptLPAREN) {
            accept(Token.LPAREN);
        }

        if (expr instanceof SQLName || expr instanceof SQLDefaultExpr || expr instanceof SQLCharExpr) {
            boolean distinct = false;
            if (lexer.token() == Token.DISTINCT) {
                lexer.nextToken();
                distinct = true;
            }

            String methodName;

            SQLMethodInvokeExpr methodInvokeExpr;
            if (expr instanceof SQLPropertyExpr) {
                methodName = ((SQLPropertyExpr) expr).getName();
                methodInvokeExpr = new SQLMethodInvokeExpr(methodName);
                methodInvokeExpr.setOwner(((SQLPropertyExpr) expr).getOwner());
            } else {
                methodName = expr.toString();
                methodInvokeExpr = new SQLMethodInvokeExpr(methodName);
            }

            String aggMethodName = SQLUtils.normalize(methodName);

            if (isAggreateFunction(aggMethodName)) {
                SQLAggregateExpr aggregateExpr = parseAggregateExpr(aggMethodName);
                if (distinct) {
                    aggregateExpr.setOption(SQLAggregateOption.DISTINCT);
                }

                return aggregateExpr;
            }

            if (lexer.token() != Token.RPAREN) {
                exprList(methodInvokeExpr.getParameters(), methodInvokeExpr);
            }

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                SQLExpr from = this.expr();
                methodInvokeExpr.setFrom(from);
            }

            accept(Token.RPAREN);

            if (lexer.token() == Token.OVER) {
                SQLAggregateExpr aggregateExpr = new SQLAggregateExpr(methodName);
                aggregateExpr.getArguments().addAll(methodInvokeExpr.getParameters());
                over(aggregateExpr);

                return primaryRest(aggregateExpr);
            }

            return primaryRest(methodInvokeExpr);
        }

        throw new ParserException("not support token:" + lexer.token() + ", " + lexer.info());
    }

    protected SQLExpr dotRest(SQLExpr expr) {
        if (lexer.token() == Token.STAR) {
            lexer.nextToken();
            expr = new SQLPropertyExpr(expr, "*");
        } else {
            String name;

            if (lexer.token() == Token.IDENTIFIER || lexer.token() == Token.LITERAL_CHARS
                || lexer.token() == Token.LITERAL_ALIAS) {
                name = lexer.stringVal();
                lexer.nextToken();
            } else if (lexer.getKeywods().containsValue(lexer.token())) {
                name = lexer.stringVal();
                lexer.nextToken();
            } else {
                throw new ParserException("error : " + lexer.info());
            }

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                if (lexer.token() == Token.DISTINCT) {
                    lexer.nextToken();

                    String aggreateMethodName = expr.toString() + "." + name;
                    SQLAggregateExpr aggregateExpr = new SQLAggregateExpr(aggreateMethodName, SQLAggregateOption.DISTINCT);

                    if (lexer.token() == Token.RPAREN) {
                        lexer.nextToken();
                    } else {
                        if (lexer.token() == Token.PLUS) {
                            aggregateExpr.getArguments().add(new SQLIdentifierExpr("+"));
                            lexer.nextToken();
                        } else {
                            exprList(aggregateExpr.getArguments(), aggregateExpr);
                        }
                        accept(Token.RPAREN);
                    }
                    expr = aggregateExpr;
                } else {
                    SQLMethodInvokeExpr methodInvokeExpr = new SQLMethodInvokeExpr(name);
                    methodInvokeExpr.setOwner(expr);
                    if (lexer.token() == Token.RPAREN) {
                        lexer.nextToken();
                    } else {
                        if (lexer.token() == Token.PLUS) {
                            methodInvokeExpr.addParameter(new SQLIdentifierExpr("+"));
                            lexer.nextToken();
                        } else {
                            exprList(methodInvokeExpr.getParameters(), methodInvokeExpr);
                        }
                        accept(Token.RPAREN);
                    }
                    expr = methodInvokeExpr;
                }
            } else {
                expr = new SQLPropertyExpr(expr, name);
            }
        }

        expr = primaryRest(expr);
        return expr;
    }

    public final SQLExpr groupComparisionRest(SQLExpr expr) {
        return expr;
    }

    public final void names(Collection<SQLName> exprCol) {
        names(exprCol, null);
    }

    public final void names(Collection<SQLName> exprCol, SQLObject parent) {
        if (lexer.token() == Token.RBRACE) {
            return;
        }

        if (lexer.token() == Token.EOF) {
            return;
        }

        SQLName name = name();
        name.setParent(parent);
        exprCol.add(name);

        while (lexer.token() == Token.COMMA) {
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
        if (lexer.token() == Token.RPAREN || lexer.token() == Token.RBRACKET) {
            return;
        }

        if (lexer.token() == Token.EOF) {
            return;
        }

        SQLExpr expr = expr();
        expr.setParent(parent);
        exprCol.add(expr);

        while (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            expr = expr();
            expr.setParent(parent);
            exprCol.add(expr);
        }
    }

    public SQLName name() {
        String identName;
        if (lexer.token() == Token.LITERAL_ALIAS) {
            identName = '"' + lexer.stringVal() + '"';
            lexer.nextToken();
        } else if (lexer.token() == Token.IDENTIFIER) {
            identName = lexer.stringVal();

            lexer.nextToken();
        } else if (lexer.token() == Token.LITERAL_CHARS) {
            identName = '\'' + lexer.stringVal() + '\'';
            lexer.nextToken();
        } else if (lexer.token() == Token.VARIANT) {
            identName = lexer.stringVal();
            lexer.nextToken();
        } else {
            switch (lexer.token()) {
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
                    identName = lexer.stringVal();
                    lexer.nextToken();
                    break;
                default:
                    throw new ParserException("error " + lexer.info());
            }
        }

        SQLName name = new SQLIdentifierExpr(identName);

        name = nameRest(name);

        return name;
    }

    public SQLName nameRest(SQLName name) {
        if (lexer.token() == Token.DOT) {
            lexer.nextToken();

            if (lexer.token() == Token.KEY) {
                name = new SQLPropertyExpr(name, "KEY");
                lexer.nextToken();
                return name;
            }

            if (lexer.token() != Token.LITERAL_ALIAS && lexer.token() != Token.IDENTIFIER
                && (!lexer.getKeywods().containsValue(lexer.token()))) {
                throw new ParserException("error, " + lexer.info());
            }

            if (lexer.token() == Token.LITERAL_ALIAS) {
                name = new SQLPropertyExpr(name, '"' + lexer.stringVal() + '"');
            } else {
                name = new SQLPropertyExpr(name, lexer.stringVal());
            }
            lexer.nextToken();
            name = nameRest(name);
        }

        return name;
    }

    public boolean isAggreateFunction(String word) {
        for (int i = 0; i < aggregateFunctions.length; ++i) {
            if (aggregateFunctions[i].compareToIgnoreCase(word) == 0) {
                return true;
            }
        }

        return false;
    }

    protected SQLAggregateExpr parseAggregateExpr(String methodName) {
        methodName = methodName.toUpperCase();

        SQLAggregateExpr aggregateExpr;
        if (lexer.token() == Token.ALL) {
            aggregateExpr = new SQLAggregateExpr(methodName, SQLAggregateOption.ALL);
            lexer.nextToken();
        } else if (lexer.token() == Token.DISTINCT) {
            aggregateExpr = new SQLAggregateExpr(methodName, SQLAggregateOption.DISTINCT);
            lexer.nextToken();
        } else if (identifierEquals("DEDUPLICATION")) { // just for nut
            aggregateExpr = new SQLAggregateExpr(methodName, SQLAggregateOption.DEDUPLICATION);
            lexer.nextToken();
        } else {
            aggregateExpr = new SQLAggregateExpr(methodName);
        }

        exprList(aggregateExpr.getArguments(), aggregateExpr);

        parseAggregateExprRest(aggregateExpr);

        accept(Token.RPAREN);

        if (lexer.token() == Token.OVER) {
            over(aggregateExpr);

        }

        return aggregateExpr;
    }

    protected void over(SQLAggregateExpr aggregateExpr) {
        lexer.nextToken();
        SQLOver over = new SQLOver();
        accept(Token.LPAREN);

        if (lexer.token() == Token.PARTITION || identifierEquals("PARTITION")) {
            lexer.nextToken();
            accept(Token.BY);

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                exprList(over.getPartitionBy(), over);
                accept(Token.RPAREN);
            } else {
                exprList(over.getPartitionBy(), over);
            }
        }

        over.setOrderBy(parseOrderBy());
        
        if (lexer.token() == Token.OF) {
            lexer.nextToken();
            SQLExpr of = this.expr();
            over.setOf(of);
        }

        SQLOver.WindowingType windowingType = null;
        if (identifierEquals("ROWS")) {
            windowingType = SQLOver.WindowingType.ROWS;
        } else if (identifierEquals("RANGE")) {
            windowingType = SQLOver.WindowingType.RANGE;
        }

        if (windowingType != null) {
            over.setWindowingType(windowingType);
            lexer.nextToken();

            if (lexer.token == Token.BETWEEN) {
                lexer.nextToken();
                SQLIntegerExpr rowsBegin = (SQLIntegerExpr) this.primary();
                over.setWindowingBetweenBegin(rowsBegin);

                if (identifierEquals("PRECEDING")) {
                    over.setWindowingBetweenBeginPreceding(true);
                    lexer.nextToken();
                } else if (identifierEquals("FOLLOWING")) {
                    over.setWindowingBetweenBeginFollowing(true);
                    lexer.nextToken();
                }

                accept(Token.AND);

                SQLIntegerExpr betweenEnd = (SQLIntegerExpr) this.primary();
                over.setWindowingBetweenEnd(betweenEnd);

                if (identifierEquals("PRECEDING")) {
                    over.setWindowingBetweenEndPreceding(true);
                    lexer.nextToken();
                } else if (identifierEquals("FOLLOWING")) {
                    over.setWindowingBetweenEndFollowing(true);
                    lexer.nextToken();
                }

            } else {

                if (identifierEquals("CURRENT")) {
                    lexer.nextToken();
                    acceptIdentifier("ROW");
                    over.setWindowing(new SQLIdentifierExpr("CURRENT ROW"));
                } else  if (identifierEquals("UNBOUNDED")) {
                    lexer.nextToken();
                    over.setWindowing(new SQLIdentifierExpr("UNBOUNDED"));

                    if (identifierEquals("PRECEDING")) {
                        over.setWindowingPreceding(true);
                        lexer.nextToken();
                    } else if (identifierEquals("FOLLOWING")) {
                        over.setWindowingFollowing(true);
                        lexer.nextToken();
                    }
                } else {
                    SQLIntegerExpr rowsExpr = (SQLIntegerExpr) this.primary();
                    over.setWindowing(rowsExpr);

                    if (identifierEquals("PRECEDING")) {
                        over.setWindowingPreceding(true);
                        lexer.nextToken();
                    } else if (identifierEquals("FOLLOWING")) {
                        over.setWindowingFollowing(true);
                        lexer.nextToken();
                    }
                }
            }
        }

        accept(Token.RPAREN);
        aggregateExpr.setOver(over);
    }

    protected SQLAggregateExpr parseAggregateExprRest(SQLAggregateExpr aggregateExpr) {
        return aggregateExpr;
    }

    public SQLOrderBy parseOrderBy() {
        if (lexer.token() == Token.ORDER) {
            SQLOrderBy orderBy = new SQLOrderBy();

            lexer.nextToken();
            
            if (identifierEquals("SIBLINGS")) {
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
        while (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            item = parseSelectOrderByItem();
            item.setParent(parent);
            items.add(item);
        }
    }

    public SQLSelectOrderByItem parseSelectOrderByItem() {
        SQLSelectOrderByItem item = new SQLSelectOrderByItem();

        item.setExpr(expr());

        if (lexer.token() == Token.ASC) {
            lexer.nextToken();
            item.setType(SQLOrderingSpecification.ASC);
        } else if (lexer.token() == Token.DESC) {
            lexer.nextToken();
            item.setType(SQLOrderingSpecification.DESC);
        }

        if (identifierEquals("NULLS")) {
            lexer.nextToken();
            if (identifierEquals("FIRST")) {
                lexer.nextToken();
                item.setNullsOrderType(SQLSelectOrderByItem.NullsOrderType.NullsFirst);
            } else if (identifierEquals("LAST")) {
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

        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            SQLListExpr list = new SQLListExpr();
            this.exprList(list.getItems(), list);
            accept(Token.RPAREN);
            item.setColumn(list);
        } else {
            item.setColumn(this.primary());
        }
        if (lexer.token() == Token.COLONEQ) {
            lexer.nextToken();
        } else {
            accept(Token.EQ);
        }

        item.setValue(this.expr());
        return item;
    }

    public final SQLExpr bitAnd() {
        SQLExpr expr = shift();
        return bitAndRest(expr);
    }

    public final SQLExpr bitAndRest(SQLExpr expr) {
        while (lexer.token() == Token.AMP) {
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
        while (lexer.token() == Token.BAR) {
            lexer.nextToken();
            SQLExpr rightExp = bitAnd();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BitwiseOr, rightExp, getDbType());
            expr = bitAndRest(expr);
        }
        return expr;
    }

    public final SQLExpr equality() {
        SQLExpr expr = bitOr();
        return equalityRest(expr);
    }

    public SQLExpr equalityRest(SQLExpr expr) {
        SQLExpr rightExp;
        if (lexer.token() == Token.EQ) {
            lexer.nextToken();
            try {
                rightExp = bitOr();
            } catch (EOFParserException e) {
                throw new ParserException("EOF, " + expr + "=", e);
            }
            rightExp = equalityRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Equality, rightExp, getDbType());
        } else if (lexer.token() == Token.BANGEQ) {
            lexer.nextToken();
            rightExp = bitOr();

            rightExp = equalityRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotEqual, rightExp, getDbType());
        } else if (lexer.token() == Token.BANG) {
            lexer.nextToken();
            accept(Token.EQ);
            rightExp = bitOr();

            rightExp = equalityRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotEqual, rightExp, getDbType());
        } else if (lexer.token() == Token.COLONEQ) {
            lexer.nextToken();
            rightExp = expr();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Assignment, rightExp, getDbType());
        }

        return expr;
    }

    public final SQLExpr inRest(SQLExpr expr) {
        if (lexer.token() == Token.IN) {
            lexer.nextToken();

            SQLInListExpr inListExpr = new SQLInListExpr(expr);
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                if (lexer.token() == Token.WITH) {
                    SQLSelect select = this.createSelectParser().select();
                    SQLInSubQueryExpr queryExpr = new SQLInSubQueryExpr(select);
                    queryExpr.setExpr(expr);
                    accept(Token.RPAREN);
                    return queryExpr;
                }

                exprList(inListExpr.getTargetList(), inListExpr);
                accept(Token.RPAREN);
                expr = inListExpr;
            } else {
                SQLExpr itemExpr = primary();
                itemExpr.setParent(inListExpr);
                inListExpr.getTargetList().add(itemExpr);
            }

            expr = inListExpr;

            if (inListExpr.getTargetList().size() == 1) {
                SQLExpr targetExpr = inListExpr.getTargetList().get(0);
                if (targetExpr instanceof SQLQueryExpr) {
                    SQLInSubQueryExpr inSubQueryExpr = new SQLInSubQueryExpr();
                    inSubQueryExpr.setExpr(inListExpr.getExpr());
                    inSubQueryExpr.setSubQuery(((SQLQueryExpr) targetExpr).getSubQuery());
                    expr = inSubQueryExpr;
                }
            }
        }

        return expr;
    }

    public final SQLExpr additive() {
        SQLExpr expr = multiplicative();
        return additiveRest(expr);
    }

    public SQLExpr additiveRest(SQLExpr expr) {
        if (lexer.token() == Token.PLUS) {
            lexer.nextToken();
            SQLExpr rightExp = multiplicative();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Add, rightExp, getDbType());
            expr = additiveRest(expr);
        } else if (lexer.token() == Token.BARBAR || lexer.token() == Token.CONCAT) {
            lexer.nextToken();
            SQLExpr rightExp = multiplicative();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Concat, rightExp, getDbType());
            expr = additiveRest(expr);
        } else if (lexer.token() == Token.SUB) {
            lexer.nextToken();
            SQLExpr rightExp = multiplicative();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Subtract, rightExp, getDbType());
            expr = additiveRest(expr);
        }

        return expr;
    }

    public final SQLExpr shift() {
        SQLExpr expr = additive();
        return shiftRest(expr);
    }

    public SQLExpr shiftRest(SQLExpr expr) {
        if (lexer.token() == Token.LTLT) {
            lexer.nextToken();
            SQLExpr rightExp = additive();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LeftShift, rightExp, getDbType());
            expr = shiftRest(expr);
        } else if (lexer.token() == Token.GTGT) {
            lexer.nextToken();
            SQLExpr rightExp = additive();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.RightShift, rightExp, getDbType());
            expr = shiftRest(expr);
        }

        return expr;
    }

    public SQLExpr and() {
        SQLExpr expr = relational();
        return andRest(expr);
    }

    public SQLExpr andRest(SQLExpr expr) {
        for (;;) {
            Token token = lexer.token();
            if (token == Token.AND || token == Token.AMPAMP) {
                if (lexer.isKeepComments() && lexer.hasComment()) {
                    expr.addAfterComment(lexer.readAndResetComments());
                }

                lexer.nextToken();

                SQLExpr rightExp = relational();

                SQLBinaryOperator operator = token == Token.AMPAMP && JdbcConstants.POSTGRESQL.equals(getDbType()) ?
                        SQLBinaryOperator.PG_And
                        : SQLBinaryOperator.BooleanAnd;

                expr = new SQLBinaryOpExpr(expr, operator, rightExp, getDbType());
            } else {
                break;
            }
        }

        return expr;
    }

    public SQLExpr or() {
        SQLExpr expr = and();
        return orRest(expr);
    }

    public SQLExpr orRest(SQLExpr expr) {

        for (;;) {
            if (lexer.token() == Token.OR) {
                lexer.nextToken();
                SQLExpr rightExp = and();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BooleanOr, rightExp, getDbType());
            } else if (lexer.token() == Token.XOR) {
                lexer.nextToken();
                SQLExpr rightExp = and();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BooleanXor, rightExp, getDbType());
            } else {
                break;
            }
        }

        return expr;
    }

    public SQLExpr relational() {
        SQLExpr expr = equality();

        return relationalRest(expr);
    }

    public SQLExpr relationalRest(SQLExpr expr) {
        SQLExpr rightExp;

        if (lexer.token() == Token.LT) {
            SQLBinaryOperator op = SQLBinaryOperator.LessThan;

            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
                op = SQLBinaryOperator.LessThanOrEqual;
            }

            rightExp = bitOr();
            expr = new SQLBinaryOpExpr(expr, op, rightExp, getDbType());
            // expr = relationalRest(expr);
        } else if (lexer.token() == Token.LTEQ) {
            lexer.nextToken();
            rightExp = bitOr();

            // rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThanOrEqual, rightExp, getDbType());
        } else if (lexer.token() == Token.LTEQGT) {
            lexer.nextToken();
            rightExp = bitOr();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThanOrEqualOrGreaterThan, rightExp, getDbType());
        } else if (lexer.token() == Token.GT) {
            SQLBinaryOperator op = SQLBinaryOperator.GreaterThan;

            lexer.nextToken();

            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
                op = SQLBinaryOperator.GreaterThanOrEqual;
            }

            rightExp = bitOr();

            expr = new SQLBinaryOpExpr(expr, op, rightExp, getDbType());
        } else if (lexer.token() == Token.GTEQ) {
            lexer.nextToken();
            rightExp = bitOr();

            // rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.GreaterThanOrEqual, rightExp, getDbType());
        } else if (lexer.token() == Token.BANGLT) {
            lexer.nextToken();
            rightExp = bitOr();

            // rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotLessThan, rightExp, getDbType());
        } else if (lexer.token() == Token.BANGGT) {
            lexer.nextToken();
            rightExp = bitOr();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotGreaterThan, rightExp, getDbType());
        } else if (lexer.token() == Token.LTGT) {
            lexer.nextToken();
            rightExp = bitOr();

            // rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThanOrGreater, rightExp, getDbType());
        } else if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            rightExp = bitOr();

            // rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Like, rightExp, getDbType());

            if (lexer.token() == Token.ESCAPE) {
                lexer.nextToken();
                rightExp = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Escape, rightExp, getDbType());
            }
        } else if (identifierEquals("RLIKE")) {
            lexer.nextToken();
            rightExp = equality();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.RLike, rightExp, getDbType());
        } else if (lexer.token() == Token.ILIKE) {
            lexer.nextToken();
            rightExp = equality();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.ILike, rightExp, getDbType());
        } else if (lexer.token() == Token.MONKEYS_AT_AT) {
            lexer.nextToken();
            rightExp = equality();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.AT_AT, rightExp, getDbType());
        } else if (lexer.token() == Token.MONKEYS_AT_GT) {
            lexer.nextToken();
            rightExp = equality();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Array_Contains, rightExp, getDbType());
        } else if (lexer.token() == Token.LT_MONKEYS_AT) {
            lexer.nextToken();
            rightExp = equality();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Array_ContainedBy, rightExp, getDbType());
        } else if (lexer.token() == (Token.NOT)) {
            lexer.nextToken();
            expr = notRationalRest(expr);
        } else if (lexer.token() == (Token.BETWEEN)) {
            lexer.nextToken();
            SQLExpr beginExpr = bitOr();
            accept(Token.AND);
            SQLExpr endExpr = bitOr();
            expr = new SQLBetweenExpr(expr, beginExpr, endExpr);
        } else if (lexer.token() == (Token.IS)) {
            lexer.nextToken();

            if (lexer.token() == (Token.NOT)) {
                lexer.nextToken();
                SQLExpr rightExpr = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.IsNot, rightExpr, getDbType());
            } else {
                SQLExpr rightExpr = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Is, rightExpr, getDbType());
            }
        } else if (lexer.token() == Token.IN) {
            expr = inRest(expr);
        } else if (JdbcConstants.POSTGRESQL.equals(lexer.dbType)) {
            if (identifierEquals("SIMILAR")) {
                lexer.nextToken();
                accept(Token.TO);

                rightExp = equality();

                rightExp = relationalRest(rightExp);

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.SIMILAR_TO, rightExp, getDbType());
            } else if (lexer.token() == Token.TILDE) {
                lexer.nextToken();

                rightExp = equality();

                rightExp = relationalRest(rightExp);

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.POSIX_Regular_Match, rightExp, getDbType());
            } else if (lexer.token() == Token.TILDE_STAR) {
                lexer.nextToken();

                rightExp = equality();

                rightExp = relationalRest(rightExp);

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.POSIX_Regular_Match_Insensitive, rightExp, getDbType());
            } else if (lexer.token() == Token.BANG_TILDE) {
                lexer.nextToken();

                rightExp = equality();

                rightExp = relationalRest(rightExp);

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.POSIX_Regular_Not_Match, rightExp, getDbType());
            } else if (lexer.token() == Token.BANG_TILDE_STAR) {
                lexer.nextToken();

                rightExp = equality();

                rightExp = relationalRest(rightExp);

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.POSIX_Regular_Not_Match_POSIX_Regular_Match_Insensitive, rightExp, getDbType());
            } else if (lexer.token() == Token.TILDE_EQ) {
                lexer.nextToken();

                rightExp = equality();

                rightExp = relationalRest(rightExp);

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.SAME_AS, rightExp, getDbType());
            }
        }

        return expr;
    }

    public SQLExpr notRationalRest(SQLExpr expr) {
        if (lexer.token() == (Token.LIKE)) {
            lexer.nextToken();
            SQLExpr rightExp = equality();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotLike, rightExp, getDbType());

            if (lexer.token() == Token.ESCAPE) {
                lexer.nextToken();
                rightExp = expr();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Escape, rightExp, getDbType());
            }
        } else if (lexer.token() == Token.IN) {
            lexer.nextToken();
            accept(Token.LPAREN);

            SQLInListExpr inListExpr = new SQLInListExpr(expr, true);
            exprList(inListExpr.getTargetList(), inListExpr);
            expr = inListExpr;

            accept(Token.RPAREN);

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

            expr = relationalRest(expr);
            return expr;
        } else if (lexer.token() == (Token.BETWEEN)) {
            lexer.nextToken();
            SQLExpr beginExpr = bitOr();
            accept(Token.AND);
            SQLExpr endExpr = bitOr();

            expr = new SQLBetweenExpr(expr, true, beginExpr, endExpr);

            return expr;
        } else if (identifierEquals("RLIKE")) {
            lexer.nextToken();
            SQLExpr rightExp = primary();

            rightExp = relationalRest(rightExp);

            return new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotRLike, rightExp, getDbType());
        } else if (lexer.token() == Token.ILIKE) {
            lexer.nextToken();
            SQLExpr rightExp = primary();

            rightExp = relationalRest(rightExp);

            return new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotILike, rightExp, getDbType());
        } else {
            throw new ParserException("TODO " + lexer.info());
        }
        return expr;
    }

    public SQLDataType parseDataType() {
        return parseDataType(true);
    }

    public SQLDataType parseDataType(boolean restrict) {
        Token token = lexer.token();
        if (token == Token.DEFAULT || token == Token.NOT || token == Token.NULL) {
            return null;
        }

        SQLName typeExpr = name();
        String typeName = typeExpr.toString();
        
        if ("long".equalsIgnoreCase(typeName) // 
                && identifierEquals("byte") //
                && JdbcConstants.MYSQL.equals(getDbType()) //
                ) {
            typeName += (' ' + lexer.stringVal());
            lexer.nextToken();
        } else if ("double".equalsIgnoreCase(typeName)
                && JdbcConstants.POSTGRESQL.equals(getDbType()) //
                ) {
            typeName += (' ' + lexer.stringVal());
            lexer.nextToken();
        }

        if (isCharType(typeName)) {
            SQLCharacterDataType charType = new SQLCharacterDataType(typeName);

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                SQLExpr arg = this.expr();
                arg.setParent(charType);
                charType.addArgument(arg);
                accept(Token.RPAREN);
            }

            charType = (SQLCharacterDataType) parseCharTypeRest(charType);

            if (lexer.token() == Token.HINT) {
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


        return parseDataTypeRest(dataType);
    }

    protected SQLDataType parseDataTypeRest(SQLDataType dataType) {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            exprList(dataType.getArguments(), dataType);
            accept(Token.RPAREN);
        }
        
        if (identifierEquals("PRECISION") && dataType.getName().equalsIgnoreCase("DOUBLE")) {
            lexer.nextToken();
            dataType.setName("DOUBLE PRECISION");
        }

        if ("timestamp".equalsIgnoreCase(dataType.getName())) {
            if (identifierEquals("WITHOUT")) {
                lexer.nextToken();
                acceptIdentifier("TIME");
                acceptIdentifier("ZONE");
                dataType.setWithTimeZone(false);
            } else if (lexer.token() == Token.WITH) {
                lexer.nextToken();
                acceptIdentifier("TIME");
                acceptIdentifier("ZONE");
                dataType.setWithTimeZone(true);
            }
        }

        return dataType;
    }

    protected boolean isCharType(String dataTypeName) {
        return "char".equalsIgnoreCase(dataTypeName) //
               || "varchar".equalsIgnoreCase(dataTypeName) || "nchar".equalsIgnoreCase(dataTypeName)
               || "nvarchar".equalsIgnoreCase(dataTypeName) || "tinytext".equalsIgnoreCase(dataTypeName)
               || "text".equalsIgnoreCase(dataTypeName) || "mediumtext".equalsIgnoreCase(dataTypeName)
               || "longtext".equalsIgnoreCase(dataTypeName)
               //
               ;
    }

    protected SQLDataType parseCharTypeRest(SQLCharacterDataType charType) {
        if (lexer.token() == Token.BINARY) {
            charType.setHasBinary(true);
            lexer.nextToken();
        }

        if (identifierEquals("CHARACTER")) {
            lexer.nextToken();

            accept(Token.SET);

            if (lexer.token() != Token.IDENTIFIER && lexer.token() != Token.LITERAL_CHARS) {
                throw new ParserException();
            }
            charType.setCharSetName(lexer.stringVal());
            lexer.nextToken();
        }

        if (lexer.token() == Token.BINARY) {
            charType.setHasBinary(true);
            lexer.nextToken();
        }

        if (lexer.token() == Token.IDENTIFIER) {
            if (lexer.stringVal().equalsIgnoreCase("COLLATE")) {
                lexer.nextToken();

                if (lexer.token() != Token.IDENTIFIER) {
                    throw new ParserException();
                }
                charType.setCollate(lexer.stringVal());
                lexer.nextToken();
            }
        }
        return charType;
    }

    public void accept(Token token) {
        if (lexer.token() == token) {
            lexer.nextToken();
        } else {
            throw new ParserException("syntax error, expect " + token + ", actual " + lexer.token() + " "
                                      + lexer.info());
        }
    }

    public SQLColumnDefinition parseColumn() {
        SQLColumnDefinition column = createColumnDefinition();
        column.setName(name());

        final Token token = lexer.token();
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
        if (lexer.token() == Token.DEFAULT) {
            lexer.nextToken();
            column.setDefaultExpr(bitOr());
            return parseColumnRest(column);
        }

        if (lexer.token() == Token.NOT) {
            lexer.nextToken();
            accept(Token.NULL);
            SQLNotNullConstraint notNull = new SQLNotNullConstraint();
            if (lexer.token() == Token.HINT) {
                List<SQLCommentHint> hints = this.parseHints();
                notNull.setHints(hints);
            }
            column.addConstraint(notNull);
            return parseColumnRest(column);
        }

        if (lexer.token() == Token.NULL) {
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
            if (lexer.token() == Token.KEY) {
                lexer.nextToken();
            }
            column.addConstraint(new SQLColumnUniqueKey());
            return parseColumnRest(column);
        }

        if (lexer.token == Token.CONSTRAINT) {
            lexer.nextToken();

            SQLName name = this.name();

            if (lexer.token() == Token.PRIMARY) {
                lexer.nextToken();
                accept(Token.KEY);
                SQLColumnPrimaryKey pk = new SQLColumnPrimaryKey();
                pk.setName(name);
                column.addConstraint(pk);
                return parseColumnRest(column);
            }

            if (lexer.token() == Token.UNIQUE) {
                lexer.nextToken();
                SQLColumnUniqueKey uk = new SQLColumnUniqueKey();
                uk.setName(name);

                column.addConstraint(uk);
                return parseColumnRest(column);
            }

            if (lexer.token() == Token.REFERENCES) {
                lexer.nextToken();
                SQLColumnReference ref = new SQLColumnReference();
                ref.setName(name);
                ref.setTable(this.name());
                accept(Token.LPAREN);
                this.names(ref.getColumns(), ref);
                accept(Token.RPAREN);
                column.addConstraint(ref);
                return parseColumnRest(column);
            }

            if (lexer.token() == Token.NOT) {
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

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            column.setComment(primary());
            return parseColumnRest(column);
        }

        return column;
    }

    protected SQLColumnCheck parseColumnCheck() {
        lexer.nextToken();
        SQLExpr expr = this.expr();
        SQLColumnCheck check = new SQLColumnCheck(expr);

        if (lexer.token() == Token.DISABLE) {
            lexer.nextToken();
            check.setEnable(false);
        } else if (lexer.token() == Token.ENABLE) {
            lexer.nextToken();
            check.setEnable(true);
        } else if (identifierEquals("VALIDATE")) {
            lexer.nextToken();
            check.setValidate(Boolean.TRUE);
        } else if (identifierEquals("NOVALIDATE")) {
            lexer.nextToken();
            check.setValidate(Boolean.FALSE);
        } else if (identifierEquals("RELY")) {
            lexer.nextToken();
            check.setRely(Boolean.TRUE);
        } else if (identifierEquals("NORELY")) {
            lexer.nextToken();
            check.setRely(Boolean.FALSE);
        }
        return check;
    }

    public SQLPrimaryKey parsePrimaryKey() {
        accept(Token.PRIMARY);
        accept(Token.KEY);

        SQLPrimaryKeyImpl pk = new SQLPrimaryKeyImpl();
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

        if (lexer.token() == Token.DISABLE) {
            lexer.nextToken();
            unique.setEnable(false);
        } else if (lexer.token() == Token.ENABLE) {
            lexer.nextToken();
            unique.setEnable(true);
        } else if (identifierEquals("VALIDATE")) {
            lexer.nextToken();
            unique.setValidate(Boolean.TRUE);
        } else if (identifierEquals("NOVALIDATE")) {
            lexer.nextToken();
            unique.setValidate(Boolean.FALSE);
        } else if (identifierEquals("RELY")) {
            lexer.nextToken();
            unique.setRely(Boolean.TRUE);
        } else if (identifierEquals("NORELY")) {
            lexer.nextToken();
            unique.setRely(Boolean.FALSE);
        }

        return unique;
    }

    public SQLAssignItem parseAssignItem() {
        SQLAssignItem item = new SQLAssignItem();

        SQLExpr var = primary();

        if (var instanceof SQLIdentifierExpr) {
            var = new SQLVariantRefExpr(((SQLIdentifierExpr) var).getName());
        }
        item.setTarget(var);
        if (lexer.token() == Token.COLONEQ) {
            lexer.nextToken();
        } else if (lexer.token() == Token.TRUE || identifierEquals("TRUE")) {
            lexer.nextToken();
            item.setValue(new SQLBooleanExpr(true));
            return item;
        } else {
            accept(Token.EQ);
        }

        if (lexer.token() == Token.ON) {
            item.setValue(new SQLIdentifierExpr(lexer.stringVal()));
            lexer.nextToken();
        } else {
            if (lexer.token() == Token.ALL) {
                item.setValue(new SQLIdentifierExpr(lexer.stringVal()));
                lexer.nextToken();
            } else {
                SQLExpr expr = expr();

                if (lexer.token() == Token.COMMA && JdbcConstants.POSTGRESQL.equals(dbType)) {
                    SQLListExpr listExpr = new SQLListExpr();
                    listExpr.addItem(expr);
                    expr.setParent(listExpr);
                    do {
                        lexer.nextToken();
                        SQLExpr listItem = this.expr();
                        listItem.setParent(listExpr);
                        listExpr.addItem(listItem);
                    } while (lexer.token() == Token.COMMA);
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
        if (lexer.token() == Token.HINT) {
            hints.add(new SQLCommentHint(lexer.stringVal()));
            lexer.nextToken();
        }
    }

    public SQLConstraint parseConstaint() {
        SQLName name = null;

        if (lexer.token() == Token.CONSTRAINT) {
            lexer.nextToken();
            name = this.name();
        }

        SQLConstraint constraint;
        if (lexer.token() == Token.PRIMARY) {
            constraint = parsePrimaryKey();
        } else if (lexer.token() == Token.UNIQUE) {
            constraint = parseUnique();
        } else if (lexer.token() == Token.FOREIGN) {
            constraint = parseForeignKey();
        } else if (lexer.token() == Token.CHECK) {
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
        this.names(fk.getReferencingColumns());
        accept(Token.RPAREN);

        accept(Token.REFERENCES);

        fk.setReferencedTableName(this.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            this.names(fk.getReferencedColumns(), fk);
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            accept(Token.DELETE);
            if (identifierEquals("CASCADE")) {
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
        if (lexer.token() == Token.IDENTIFIER) {
            String ident = lexer.stringVal();
            lexer.nextTokenComma();

            if ("CONNECT_BY_ROOT".equalsIgnoreCase(ident)) {
                connectByRoot = true;
                expr = new SQLIdentifierExpr(lexer.stringVal());
                lexer.nextToken();
            } else if ("DATE".equalsIgnoreCase(ident)
                    && lexer.token() == Token.LITERAL_CHARS
                    && (JdbcConstants.ORACLE.equals(getDbType())
                    || JdbcConstants.POSTGRESQL.equals(getDbType()))) {
                String literal = lexer.stringVal();
                lexer.nextToken();

                SQLDateExpr dateExpr = new SQLDateExpr();
                dateExpr.setLiteral(literal);

                expr = dateExpr;
            } else {
                expr = new SQLIdentifierExpr(ident);
            }

            if (lexer.token() != Token.COMMA) {
                expr = this.primaryRest(expr);
                expr = this.exprRest(expr);
            }
        } else if (lexer.token() == Token.DO || lexer.token() == Token.JOIN) {
            expr = this.name();
            expr = this.exprRest(expr);
        } else {
            expr = expr();
        }

        final String alias;
        switch (lexer.token()) {
            case FULL:
                alias = lexer.stringVal();
                lexer.nextToken();
                break;
            default:
                alias = as();
                break;
        }

        return new SQLSelectItem(expr, alias, connectByRoot);
    }

    public SQLExpr parseGroupingSet() {
        String tmp = lexer.stringVal();
        acceptIdentifier("GROUPING");
        
        SQLGroupingSetExpr expr = new SQLGroupingSetExpr();
        
        if (identifierEquals("SET")) {
            lexer.nextToken();
        } else {
            return new SQLIdentifierExpr(tmp);
        }

        accept(Token.LPAREN);

        this.exprList(expr.getParameters(), expr);

        accept(Token.RPAREN);

        return expr;
    }
    
    public SQLPartitionValue parsePartitionValues() {
        if (lexer.token() != Token.VALUES) {
            return null;
        }
        lexer.nextToken();

        SQLPartitionValue values = null;

        if (lexer.token() == Token.IN) {
            lexer.nextToken();
            values = new SQLPartitionValue(SQLPartitionValue.Operator.In);

            accept(Token.LPAREN);
            this.exprList(values.getItems(), values);
            accept(Token.RPAREN);
        } else if (identifierEquals("LESS")) {
            lexer.nextToken();
            acceptIdentifier("THAN");

            values = new SQLPartitionValue(SQLPartitionValue.Operator.LessThan);

            if (identifierEquals("MAXVALUE")) {
                SQLIdentifierExpr maxValue = new SQLIdentifierExpr(lexer.stringVal());
                lexer.nextToken();
                maxValue.setParent(values);
                values.addItem(maxValue);
            } else {
                accept(Token.LPAREN);
                this.exprList(values.getItems(), values);
                accept(Token.RPAREN);
            }
        } else if (lexer.token() == Token.LPAREN) {
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
        if (lexer.token() == Token.LIMIT) {
            lexer.nextToken();

            SQLLimit limit = new SQLLimit();

            SQLExpr temp = this.expr();
            if (lexer.token() == (Token.COMMA)) {
                limit.setOffset(temp);
                lexer.nextToken();
                limit.setRowCount(this.expr());
            } else if (identifierEquals("OFFSET")) {
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
