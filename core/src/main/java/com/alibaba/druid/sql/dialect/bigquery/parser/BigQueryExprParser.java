package com.alibaba.druid.sql.dialect.bigquery.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.bigquery.BQ;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQueryCharExpr;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQueryModelExpr;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQueryTableExpr;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

import static com.alibaba.druid.util.FnvHash.fnv1a_64_lower;

public class BigQueryExprParser extends SQLExprParser {
    private static final String[] AGGREGATE_FUNCTIONS;
    private static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = {
                "ANY_VALUE",
                "ARRAY_AGG",
                "ARRAY_CONCAT_AGG",
                "AVG",
                "BIT_AND",
                "BIT_OR",
                "BIT_XOR",
                "COUNT",
                "COUNTIF",
                "FIRST_VALUE",
                "GROUPING",
                "LAST_VALUE",
                "LAG",
                "LEAD",
                "LOGICAL_AND",
                "LOGICAL_OR",
                "MAX",
                "MAX_BY",
                "MIN",
                "MIN_BY",
                "STRING_AGG",
                "SUM",
                "APPROX_QUANTILES"
        };
        AGGREGATE_FUNCTIONS_CODES = fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    {
        dbType = DbType.bigquery;
    }

    public BigQueryExprParser(String sql) {
        this(new BigQueryLexer(sql));
        this.lexer.nextToken();
    }

    public BigQueryExprParser(String sql, SQLParserFeature... features) {
        this(new BigQueryLexer(sql, features));
        this.lexer.nextToken();
    }

    public BigQueryExprParser(Lexer lexer) {
        super(lexer);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    static final long SAFE_CAST = fnv1a_64_lower("SAFE_CAST");

    protected SQLExpr methodRest(SQLExpr expr, boolean acceptLPAREN) {
        if (expr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) expr;
            long hashCode64 = identifierExpr.hashCode64();
            if (hashCode64 == FnvHash.Constants.STRUCT && acceptLPAREN) {
                SQLStructExpr struct = struct();
                if (lexer.isKeepSourceLocation()) {
                    struct.setSource(identifierExpr.getSourceLine(), identifierExpr.getSourceColumn());
                }
                return struct;
            }

            if (hashCode64 == SAFE_CAST && acceptLPAREN) {
                SQLCastExpr castExpr = new SQLCastExpr();
                lexer.nextToken();
                castExpr.setExpr(
                        expr());
                castExpr.setTry(true);
                accept(Token.AS);
                castExpr.setDataType(
                        parseDataType()
                );
                castExpr = parseCastFormat(castExpr);
                accept(Token.RPAREN);
                return castExpr;
            }

            String ident = identifierExpr.getName();
            if (ident.length() > 3 && ident.charAt(0) == '`' && ident.charAt(ident.length() - 1) == '`' && ident.indexOf('.') != -1) {
                expr = topPropertyExpr(ident);
            }
        }
        return super.methodRest(expr, acceptLPAREN);
    }

    protected SQLExpr parseSelectItemRest(String ident, long hash_lower) {
        if (ident.length() > 3
                && ident.charAt(0) == '`'
                && ident.charAt(ident.length() - 1) == '`'
                && ident.indexOf('.') != -1
        ) {
            return topPropertyExpr(ident);
        }
        if (hash_lower == FnvHash.Constants.ARRAY && lexer.token() == Token.LT) {
            return parseArrayExpr(ident);
        }
        return null;
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        if (lexer.nextIfIdentifier(FnvHash.Constants.OPTIONS)) {
            parseAssignItem(column.getColProperties(), column);
        }

        return super.parseColumnRest(column);
    }

    protected SQLStructDataType parseDataTypeStruct() {
        acceptIdentifier("STRUCT");
        return parseDataTypeStruct0();
    }

    protected SQLStructDataType parseDataTypeStruct0() {
        SQLStructDataType struct = new SQLStructDataType(dbType);
        accept(Token.LT);
        for (; ; ) {
            SQLName name;
            String str = lexer.stringVal();
            if (BQ.DIALECT.isBuiltInDataType(str)) {
                Lexer.SavePoint mark = lexer.markOut();
                lexer.nextToken();
                String tokenName = lexer.token() == Token.IDENTIFIER ? lexer.stringVal() : lexer.token().name;
                if (tokenName != null
                        && Character.isLetter(tokenName.charAt(0))
                        && BQ.DIALECT.isBuiltInDataType(lexer.stringVal())
                ) {
                    name = new SQLIdentifierExpr(str);
                } else {
                    lexer.reset(mark);
                    name = null;
                }
            } else {
                name = new SQLIdentifierExpr(str);
                lexer.nextToken();
            }

            SQLDataType dataType = this.parseDataType();
            SQLStructDataType.Field field = struct.addField(name, dataType);
            parseFieldConstraints(field);
            if (lexer.nextIfIdentifier(FnvHash.Constants.OPTIONS)) {
                parseAssignItem(field.getOptions(), field);
            }

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        if (lexer.token() == Token.GTGTGT) {
            lexer.setToken(Token.GTGT);
        } else if (lexer.token() == Token.GTGT) {
            lexer.setToken(Token.GT);
        } else {
            accept(Token.GT);
        }
        return struct;
    }

    private void parseFieldConstraints(SQLStructDataType.Field field) {
        switch (lexer.token()) {
            case DEFAULT:
                field.addConstraint(parseColumnDefault());
                break;
            case NOT: {
                lexer.nextToken();
                accept(Token.NULL);
                SQLNotNullConstraint notNull = new SQLNotNullConstraint();
                field.addConstraint(notNull);
                break;
            }
            case PRIMARY:
                SQLColumnPrimaryKey pk = new SQLColumnPrimaryKey();
                lexer.nextToken();
                accept(Token.KEY);
                accept(Token.NOT);
                acceptIdentifier("ENFORCED");
                pk.setNotEnforced(true);
                field.addConstraint(pk);
                break;
            case REFERENCES:
                SQLColumnReference ref = parseReference();
                accept(Token.NOT);
                acceptIdentifier("ENFORCED");
                ref.setNotEnforced(true);
                field.addConstraint(ref);
                break;
            default:
                break;
        }
    }

    private SQLColumnDefault parseColumnDefault() {
        SQLColumnDefault columnDefault = new SQLColumnDefault();
        accept(Token.DEFAULT);
        if (lexer.token() == Token.LPAREN) {
            while (lexer.token() == Token.LPAREN) {
                accept(Token.LPAREN);
            }

            columnDefault.setDefaultExpr(this.primary());

            while (lexer.token() == Token.RPAREN) {
                accept(Token.RPAREN);
            }
        } else {
            columnDefault.setDefaultExpr(this.primary());
        }
        return columnDefault;
    }

    public void parsePrimaryKeyRest(SQLPrimaryKeyImpl primaryKey) {
        accept(Token.NOT);
        acceptIdentifier("ENFORCED");
        primaryKey.setNotEnforced(true);
        super.parsePrimaryKeyRest(primaryKey);
    }

    @Override
    protected void parseForeignKeyRest(SQLForeignKeyImpl foreignKey) {
        accept(Token.NOT);
        acceptIdentifier("ENFORCED");
        foreignKey.setNotEnforced(true);
        super.parseForeignKeyRest(foreignKey);
    }

    public SQLExpr primary() {
        if (lexer.nextIf(Token.DOT)) {
            String name = lexer.stringVal();
            SQLExpr expr = new SQLPropertyExpr((SQLExpr) null, name);
            lexer.nextToken();
            if (lexer.nextIf(Token.DOT)) {
                expr = dotRest(expr);
            }
            return primaryRest(expr);
        }

        if (lexer.token() == Token.WITH) {
            return primaryRest(
                    parseQueryExpr()
            );
        }

        if (lexer.identifierEquals(FnvHash.Constants.MODEL)) {
            Lexer.SavePoint mark = lexer.markOut();
            lexer.nextToken();
            if (lexer.token() == Token.IDENTIFIER) {
                BigQueryModelExpr model = new BigQueryModelExpr();
                model.setName(
                        name()
                );
                return model;
            } else {
                lexer.reset(mark);
            }
        }
        if (lexer.token() == Token.TABLE) {
            Lexer.SavePoint mark = lexer.markOut();
            lexer.nextToken();
            if (lexer.token() == Token.IDENTIFIER) {
                BigQueryTableExpr model = new BigQueryTableExpr();
                model.setName(
                        name()
                );
                return model;
            } else {
                lexer.reset(mark);
            }
        }
        return super.primary();
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        if (expr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) expr;
            String ident = identifierExpr.getName();
            if (lexer.token() == Token.LITERAL_CHARS || lexer.token() == Token.LITERAL_ALIAS) {
                boolean isAlias = lexer.token() == Token.LITERAL_ALIAS;
                boolean isSpace;
                if (ident.equalsIgnoreCase("b") || ident.equalsIgnoreCase("r")) {
                    isSpace = false;
                } else if (ident.equalsIgnoreCase("json")) {
                    isSpace = true;
                } else {
                    throw new ParserException("Not supported prefix: " + ident + " for bigquery");
                }
                String charValue = lexer.stringVal();
                lexer.nextToken();
                expr = new BigQueryCharExpr(charValue, ident, isSpace, isAlias);
            }
        }

        Lexer.SavePoint savePoint = lexer.markOut();
        if (lexer.identifierEquals(FnvHash.Constants.AT)) {
            lexer.nextToken();
            if (lexer.nextIfIdentifier(FnvHash.Constants.TIME)) {
                acceptIdentifier("ZONE");
                SQLExpr timeZone = primary();
                expr = new SQLAtTimeZoneExpr(expr, timeZone);
            } else {
                lexer.reset(savePoint);
            }
        }

        return super.primaryRest(expr);
    }

    public SQLDataType parseDataType(boolean restrict) {
        if (lexer.nextIf(Token.ANY)) {
            acceptIdentifier("TYPE");
            return new SQLDataTypeImpl("ANY TYPE");
        }
        return super.parseDataType(restrict);
    }

    protected SQLExpr dotRest(SQLExpr expr) {
        return super.dotRest(expr);
    }

    public SQLSelectParser createSelectParser() {
        return new BigQuerySelectParser(this, null);
    }

    public SQLExpr exprRest(SQLExpr expr) {
        if (lexer.token() == Token.LT && expr instanceof SQLIdentifierExpr && ((SQLIdentifierExpr) expr).nameEquals("STRUCT")) {
            SQLStructExpr structExpr = new SQLStructExpr();
            structExpr.setDataType(
                    parseDataTypeStruct0()
            );
            accept(Token.LPAREN);
            aliasedItems(structExpr.getItems(), structExpr);
            accept(Token.RPAREN);
            expr = structExpr;
        }
        return super.exprRest(expr);
    }

    protected String nameCommon() {
        String identName = lexer.stringVal();
        lexer.nextToken();
        return identName;
    }

    @Override
    protected SQLCastExpr parseCastFormat(SQLCastExpr cast) {
        if (lexer.nextIfIdentifier("FORMAT")) {
            cast.setFormat(
                    this.expr()
            );
        }
        return cast;
    }

    protected SQLExpr primaryIdentifierRest(long hash_lower, String ident) {
        if (ident.length() > 3 && ident.charAt(0) == '`' && ident.charAt(ident.length() - 1) == '`' && ident.indexOf('.') != -1) {
            return topPropertyExpr(ident);
        }
        return super.primaryIdentifierRest(hash_lower, ident);
    }

    public SQLName nameRest(SQLName name) {
        if (name instanceof SQLIdentifierExpr) {
            String ident = ((SQLIdentifierExpr) name).getName();
            if (ident.length() > 3 && ident.charAt(0) == '`' && ident.charAt(ident.length() - 1) == '`' && ident.indexOf('.') != -1) {
                return topPropertyExpr(ident);
            }
        }
        return super.nameRest(name);
    }

    @Override
    protected SQLExpr primaryCommon(SQLExpr sqlExpr) {
        sqlExpr = new SQLIdentifierExpr(lexer.stringVal());
        lexer.nextToken();
        return sqlExpr;
    }
}
