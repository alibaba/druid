package com.alibaba.druid.sql.dialect.dm.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalType;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

public class DmExprParser extends SQLExprParser {
    public static final String[] AGGREGATE_FUNCTIONS;
    public static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = {
                "AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM",
                "ROW_NUMBER", "RANK", "DENSE_RANK", "PERCENT_RANK",
                "CUME_DIST", "NTILE", "LAG", "LEAD",
                "FIRST_VALUE", "LAST_VALUE", "NTH_VALUE",
                "LISTAGG", "WM_CONCAT"
        };

        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public DmExprParser(String sql) {
        this(new DmLexer(sql));
        this.lexer.nextToken();
        this.dbType = DbType.dm;
    }

    public DmExprParser(String sql, SQLParserFeature... features) {
        this(new DmLexer(sql, features));
        this.lexer.nextToken();
        this.dbType = DbType.dm;
    }

    public DmExprParser(Lexer lexer) {
        super(lexer);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
        this.dbType = DbType.dm;
    }

    @Override
    public SQLDataType parseDataType() {
        if (lexer.token() == Token.TYPE) {
            lexer.nextToken();
        }
        return super.parseDataType();
    }

    public DmSelectParser createSelectParser() {
        return new DmSelectParser(this);
    }

    @Override
    public SQLExpr primary() {
        if (lexer.token() == Token.VALUES) {
            lexer.nextToken();

            SQLValuesExpr values = new SQLValuesExpr();
            for (;;) {
                accept(Token.LPAREN);
                SQLListExpr listExpr = new SQLListExpr();
                exprList(listExpr.getItems(), listExpr);
                accept(Token.RPAREN);

                listExpr.setParent(values);
                values.getValues().add(listExpr);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            return values;
        } else if (lexer.token() == Token.WITH) {
            SQLQueryExpr queryExpr = new SQLQueryExpr(
                    createSelectParser().select());
            return queryExpr;
        }

        return super.primary();
    }

    @Override
    protected SQLExpr parseInterval() {
        accept(Token.INTERVAL);

        OracleIntervalExpr interval = new OracleIntervalExpr();

        if (lexer.token() == Token.LITERAL_CHARS) {
            interval.setValue(new SQLCharExpr(lexer.stringVal()));
        } else if (lexer.token() == Token.VARIANT) {
            interval.setValue(new SQLVariantRefExpr(lexer.stringVal()));
        } else if (lexer.token() == Token.QUES) {
            interval.setValue(new SQLVariantRefExpr("?"));
        } else {
            return new SQLIdentifierExpr("INTERVAL");
        }

        lexer.nextToken();

        OracleIntervalType type;
        if (lexer.identifierEquals(FnvHash.Constants.YEAR)) {
            lexer.nextToken();
            type = OracleIntervalType.YEAR;
        } else if (lexer.identifierEquals(FnvHash.Constants.MONTH)) {
            lexer.nextToken();
            type = OracleIntervalType.MONTH;
        } else if (lexer.identifierEquals(FnvHash.Constants.DAY)) {
            lexer.nextToken();
            type = OracleIntervalType.DAY;
        } else if (lexer.identifierEquals(FnvHash.Constants.HOUR)) {
            lexer.nextToken();
            type = OracleIntervalType.HOUR;
        } else if (lexer.identifierEquals(FnvHash.Constants.MINUTE)) {
            lexer.nextToken();
            type = OracleIntervalType.MINUTE;
        } else if (lexer.identifierEquals(FnvHash.Constants.SECOND)) {
            lexer.nextToken();
            type = OracleIntervalType.SECOND;
        } else {
            throw new ParserException("illegal interval type. " + lexer.info());
        }

        interval.setType(type);

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            if (lexer.token() != Token.LITERAL_INT && lexer.token() != Token.VARIANT) {
                throw new ParserException("syntax error. " + lexer.info());
            }
            interval.setPrecision(this.primary());

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                if (lexer.token() != Token.LITERAL_INT) {
                    throw new ParserException("syntax error. " + lexer.info());
                }
                interval.setFactionalSecondsPrecision(lexer.integerValue().intValue());
                lexer.nextToken();
            }
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.TO) {
            lexer.nextToken();
            if (lexer.identifierEquals("SECOND")) {
                lexer.nextToken();
                interval.setToType(OracleIntervalType.SECOND);
                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    if (lexer.token() != Token.LITERAL_INT && lexer.token() != Token.VARIANT) {
                        throw new ParserException("syntax error. " + lexer.info());
                    }
                    interval.setToFactionalSecondsPrecision(primary());
                    accept(Token.RPAREN);
                }
            } else {
                interval.setToType(OracleIntervalType.MONTH);
                lexer.nextToken();
            }
        }

        return interval;
    }

    @Override
    public SQLExpr primaryRest(SQLExpr expr) {
        if (expr.getClass() == SQLIdentifierExpr.class) {
            SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) expr;
            String ident = identifierExpr.getName();
            long hash = identifierExpr.nameHashCode64();

            if (lexer.token() == Token.COMMA || lexer.token() == Token.RPAREN) {
                return super.primaryRest(expr);
            }

            if (FnvHash.Constants.TIMESTAMP == hash) {
                if (lexer.token() != Token.LITERAL_ALIAS
                        && lexer.token() != Token.LITERAL_CHARS
                        && lexer.token() != Token.WITH) {
                    return super.primaryRest(new SQLIdentifierExpr(ident));
                }

                SQLTimestampExpr timestamp = new SQLTimestampExpr();

                if (lexer.token() == Token.WITH) {
                    lexer.nextToken();
                    acceptIdentifier("TIME");
                    acceptIdentifier("ZONE");
                    timestamp.setWithTimeZone(true);
                }

                String literal = lexer.stringVal();
                timestamp.setValue(literal);
                accept(Token.LITERAL_CHARS);

                if (lexer.identifierEquals("AT")) {
                    lexer.nextToken();
                    acceptIdentifier("TIME");
                    acceptIdentifier("ZONE");

                    String timezone = lexer.stringVal();
                    timestamp.setTimeZone(timezone);
                    accept(Token.LITERAL_CHARS);
                }

                return primaryRest(timestamp);
            } else if (FnvHash.Constants.EXTRACT == hash) {
                return parseExtract(ident);
            } else if (ident.equalsIgnoreCase("b") && lexer.token() == Token.LITERAL_CHARS) {
                String charValue = lexer.stringVal();
                lexer.nextToken();
                expr = new SQLBinaryExpr(charValue);
                return primaryRest(expr);
            }
        }

        return super.primaryRest(expr);
    }

    private SQLExpr parseExtract(String ident) {
        accept(Token.LPAREN);

        SQLMethodInvokeExpr extract = new SQLMethodInvokeExpr(ident);

        String fieldName = lexer.stringVal();
        lexer.nextToken();

        accept(Token.FROM);
        SQLExpr source = this.expr();

        extract.addArgument(new SQLIdentifierExpr(fieldName));
        extract.addArgument(source);

        accept(Token.RPAREN);

        return primaryRest(extract);
    }

    @Override
    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        // DM supports IDENTITY [(seed, increment)] syntax
        if (lexer.token() == Token.IDENTITY) {
            lexer.nextToken();
            column.setAutoIncrement(true);
            if (lexer.token() == Token.LPAREN) {
                column.setIdentity(parseIdentity());
            }
            return parseColumnRest(column);
        }
        return super.parseColumnRest(column);
    }

    @Override
    protected String alias() {
        String alias = super.alias();
        if (alias != null) {
            return alias;
        }
        switch (lexer.token()) {
        case INTERSECT:
            alias = lexer.stringVal();
            lexer.nextToken();
            return alias;
        default:
            break;
        }
        return alias;
    }
}
