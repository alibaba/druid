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
package com.alibaba.druid.sql.parser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAggregateOption;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllExpr;
import com.alibaba.druid.sql.ast.expr.SQLAnyExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLCurrentOfCursorExpr;
import com.alibaba.druid.sql.ast.expr.SQLDefaultExpr;
import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
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
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLSomeExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.NotNullConstraint;
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
        if (lexer.token() == Token.CARET) {
            lexer.nextToken();
            SQLExpr rightExp = primary();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BitwiseXor, rightExp, getDbType());
            expr = bitXorRest(expr);
        }

        return expr;
    }

    public final SQLExpr multiplicative() {
        SQLExpr expr = bitXor();
        return multiplicativeRest(expr);
    }

    public SQLExpr multiplicativeRest(SQLExpr expr) {
        if (lexer.token() == Token.STAR) {
            lexer.nextToken();
            SQLExpr rightExp = bitXor();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Multiply, rightExp, getDbType());
            expr = multiplicativeRest(expr);
        } else if (lexer.token() == Token.SLASH) {
            lexer.nextToken();
            SQLExpr rightExp = bitXor();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Divide, rightExp, getDbType());
            expr = multiplicativeRest(expr);
        } else if (lexer.token() == Token.PERCENT) {
            lexer.nextToken();
            SQLExpr rightExp = bitXor();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Modulus, rightExp, getDbType());
            expr = multiplicativeRest(expr);
        }
        return expr;
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
                    listExpr.getItems().add(sqlExpr);
                    do {
                        lexer.nextToken();
                        listExpr.getItems().add(expr());
                    } while (lexer.token() == Token.COMMA);

                    sqlExpr = listExpr;
                }
                accept(Token.RPAREN);
                break;
            case INSERT:
                lexer.nextToken();
                if (lexer.token() != Token.LPAREN) {
                    throw new ParserException("syntax error");
                }
                sqlExpr = new SQLIdentifierExpr("INSERT");
                break;
            case IDENTIFIER:
                sqlExpr = new SQLIdentifierExpr(lexer.stringVal());
                lexer.nextToken();
                break;
            case NEW:
                throw new ParserException("TODO");
            case LITERAL_INT:
                sqlExpr = new SQLIntegerExpr(lexer.integerValue());
                lexer.nextToken();
                break;
            case LITERAL_FLOAT:
                sqlExpr = new SQLNumberExpr(lexer.decimalValue());
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
            case VARIANT:
                SQLVariantRefExpr varRefExpr = new SQLVariantRefExpr(lexer.stringVal());
                lexer.nextToken();
                if (varRefExpr.getName().equals("@") && lexer.token() == Token.LITERAL_CHARS) {
                    varRefExpr.setName("@'" + lexer.stringVal() + "'");
                    lexer.nextToken();
                } else if (varRefExpr.getName().equals("@@") && lexer.token() == Token.LITERAL_CHARS) {
                    varRefExpr.setName("@@'" + lexer.stringVal() + "'");
                    lexer.nextToken();
                }
                sqlExpr = varRefExpr;
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
            case KEEP:
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
                    caseExpr.getItems().add(caseItem);
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
                    notTarget = exprRest(notTarget);

                    sqlExpr = new SQLNotExpr(notTarget);

                    return primaryRest(sqlExpr);
                } else {
                    SQLExpr restExpr = relational();
                    sqlExpr = new SQLNotExpr(restExpr);
                }
                break;
            case SELECT:
                SQLQueryExpr queryExpr = new SQLQueryExpr(createSelectParser().select());
                sqlExpr = queryExpr;
                break;
            case CAST:
                lexer.nextToken();
                accept(Token.LPAREN);
                SQLCastExpr cast = new SQLCastExpr();
                cast.setExpr(expr());
                accept(Token.AS);
                cast.setDataType(parseDataType());
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
                        sqlExpr = new SQLNumberExpr(lexer.decimalValue().negate());
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
                    case QUES:
                        sqlExpr = new SQLUnaryExpr(SQLUnaryOperator.Negative, new SQLVariantRefExpr("?"));
                        lexer.nextToken();
                        break;
                    case LPAREN:
                        lexer.nextToken();
                        sqlExpr = expr();
                        accept(Token.RPAREN);
                        sqlExpr = new SQLUnaryExpr(SQLUnaryOperator.Negative, sqlExpr);
                        break;
                    default:
                        throw new ParserException("TODO : " + lexer.token());
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
                        sqlExpr = new SQLNumberExpr(lexer.decimalValue());
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
                        throw new ParserException("TODO");
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
            default:
                throw new ParserException("ERROR. token : " + tok + ", pos : " + lexer.pos());
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
        throw new ParserException("TODO");
    }

    public SQLSelectParser createSelectParser() {
        return new SQLSelectParser(this);
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        if (expr == null) {
            throw new IllegalArgumentException("expr");
        }

        if (lexer.token() == Token.OF) {
            if (expr instanceof SQLIdentifierExpr) {
                String name = ((SQLIdentifierExpr) expr).getName();
                if ("CURRENT".equalsIgnoreCase(name)) {
                    lexer.nextToken();
                    SQLName cursorName = this.name();
                    return new SQLCurrentOfCursorExpr(cursorName);
                }
            }
        }

        if (lexer.token() == Token.DOT) {
            lexer.nextToken();

            if (expr instanceof SQLCharExpr) {
                String text = ((SQLCharExpr) expr).getText();
                expr = new SQLIdentifierExpr(text);
            }

            expr = dotRest(expr);
            return primaryRest(expr);
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

        if (expr instanceof SQLName || expr instanceof SQLDefaultExpr) {
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

            if (isAggreateFunction(methodName)) {
                SQLAggregateExpr aggregateExpr = parseAggregateExpr(methodName);

                return aggregateExpr;
            }

            if (lexer.token() != Token.RPAREN) {
                exprList(methodInvokeExpr.getParameters(), methodInvokeExpr);
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

        throw new ParserException("not support token:" + lexer.token());
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
                throw new ParserException("error : " + lexer.stringVal());
            }

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

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
                case KEEP:
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
                //binary有很多含义，lexer识别了这个token，实际上应该当做普通IDENTIFIER
                case BINARY:
                    identName = lexer.stringVal();
                    lexer.nextToken();
                    break;
                default:
                    throw new ParserException("error " + lexer.token());
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
                throw new ParserException("error, " + lexer.token());
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
        } else if (identifierEquals("DEDUPLICATION")) { //just for nut
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

            accept(Token.BY);

            orderBy.addItem(parseSelectOrderByItem());

            while (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                orderBy.addItem(parseSelectOrderByItem());
            }

            return orderBy;
        }

        return null;
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
        } else if (lexer.token() == Token.BARBAR) {
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
            if (lexer.token() == Token.AND || lexer.token() == Token.AMPAMP) {
                if (lexer.isKeepComments() && lexer.hasComment()) {
                    expr.addAfterComment(lexer.readAndResetComments());
                }
                
                lexer.nextToken();
                
                SQLExpr rightExp = relational();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BooleanAnd, rightExp, getDbType());
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
        } else {
            throw new ParserException("TODO " + lexer.token());
        }
        return expr;
    }

    public SQLDataType parseDataType() {

        if (lexer.token() == Token.DEFAULT || lexer.token() == Token.NOT || lexer.token() == Token.NULL) {
            return null;
        }

        SQLName typeExpr = name();
        String typeName = typeExpr.toString();

        if (isCharType(typeName)) {
            SQLCharacterDataType charType = new SQLCharacterDataType(typeName);

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                SQLExpr arg = this.expr();
                arg.setParent(charType);
                charType.getArguments().add(arg);
                accept(Token.RPAREN);
            }

            return parseCharTypeRest(charType);
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

        return dataType;
    }

    protected boolean isCharType(String dataTypeName) {
        return "char".equalsIgnoreCase(dataTypeName) //
               || "varchar".equalsIgnoreCase(dataTypeName)
               || "nchar".equalsIgnoreCase(dataTypeName)
               || "nvarchar".equalsIgnoreCase(dataTypeName)
               || "tinytext".equalsIgnoreCase(dataTypeName)
               || "text".equalsIgnoreCase(dataTypeName)
               || "mediumtext".equalsIgnoreCase(dataTypeName)
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
                                      + lexer.stringVal());
        }
    }

    public SQLColumnDefinition parseColumn() {
        SQLColumnDefinition column = createColumnDefinition();
        column.setName(name());
        
        if(lexer.token() != Token.SET //
                && lexer.token() != Token.DROP) {
            column.setDataType(parseDataType());
        }
        return parseColumnRest(column);
    }

    protected SQLColumnDefinition createColumnDefinition() {
        SQLColumnDefinition column = new SQLColumnDefinition();
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
            column.getConstraints().add(new NotNullConstraint());
            return parseColumnRest(column);
        }

        if (lexer.token() == Token.NULL) {
            lexer.nextToken();
            column.setDefaultExpr(new SQLNullExpr());
            return parseColumnRest(column);
        }

        if (lexer.token == Token.PRIMARY) {
            lexer.nextToken();
            accept(Token.KEY);
            column.getConstraints().add(new SQLColumnPrimaryKey());
            return parseColumnRest(column);
        }

        if (lexer.token == Token.UNIQUE) {
            lexer.nextToken();
            if (lexer.token() == Token.KEY) {
                lexer.nextToken();
            }
            column.getConstraints().add(new SQLColumnUniqueKey());
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
                column.getConstraints().add(pk);
                return parseColumnRest(column);
            }

            if (lexer.token() == Token.UNIQUE) {
                lexer.nextToken();
                SQLColumnUniqueKey uk = new SQLColumnUniqueKey();
                uk.setName(name);
                column.getConstraints().add(uk);
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
                column.getConstraints().add(ref);
                return parseColumnRest(column);
            }

            if (lexer.token() == Token.NOT) {
                lexer.nextToken();
                accept(Token.NULL);
                NotNullConstraint notNull = new NotNullConstraint();
                notNull.setName(name);
                column.getConstraints().add(notNull);
                return parseColumnRest(column);
            }

            if (lexer.token == Token.CHECK) {
                SQLColumnCheck check = parseColumnCheck();
                check.setName(name);
                check.setParent(column);
                column.getConstraints().add(check);
                return parseColumnRest(column);
            }

            if (lexer.token == Token.DEFAULT) {
                lexer.nextToken();
                SQLExpr expr = this.expr();
                column.setDefaultExpr(expr);
                return parseColumnRest(column);
            }

            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
        }

        if (lexer.token == Token.CHECK) {
            SQLColumnCheck check = parseColumnCheck();
            column.getConstraints().add(check);
            return parseColumnRest(column);
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            column.setComment(primary());
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
        }
        return check;
    }

    public SQLPrimaryKey parsePrimaryKey() {
        accept(Token.PRIMARY);
        accept(Token.KEY);

        SQLPrimaryKeyImpl pk = new SQLPrimaryKeyImpl();
        accept(Token.LPAREN);
        exprList(pk.getColumns(), pk);
        accept(Token.RPAREN);

        return pk;
    }

    public SQLUnique parseUnique() {
        accept(Token.UNIQUE);

        SQLUnique unique = new SQLUnique();
        accept(Token.LPAREN);
        exprList(unique.getColumns(), unique);
        accept(Token.RPAREN);

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
        } else {
            accept(Token.EQ);
        }
        
        if(lexer.token() == Token.ON) {
            item.setValue(new SQLIdentifierExpr(lexer.stringVal()));
            lexer.nextToken();
        } else {
            if (lexer.token() == Token.ALL) {
                item.setValue(new SQLIdentifierExpr(lexer.stringVal()));
                lexer.nextToken();
            } else {
                item.setValue(expr());
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
            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
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

        SQLForeignKeyConstraint fk = createForeignKey();

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

        return fk;
    }

    protected SQLForeignKeyConstraint createForeignKey() {
        return new SQLForeignKeyImpl();
    }
    
    public SQLSelectItem parseSelectItem() {
        SQLExpr expr;
        boolean connectByRoot = false;
        if (lexer.token() == Token.IDENTIFIER) {
            if (identifierEquals("CONNECT_BY_ROOT")) {
                connectByRoot = true;
                lexer.nextToken();
            }
            expr = new SQLIdentifierExpr(lexer.stringVal());
            lexer.nextTokenComma();

            if (lexer.token() != Token.COMMA) {
                expr = this.primaryRest(expr);
                expr = this.exprRest(expr);
            }
        } else {
            expr = expr();
        }
        final String alias = as();

        return new SQLSelectItem(expr, alias, connectByRoot);
    }
}
