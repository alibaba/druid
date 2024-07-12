package com.alibaba.druid.sql.dialect.bigquery.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQuerySelectAsStruct;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;
import java.util.List;

import static com.alibaba.druid.util.FnvHash.fnv1a_64_lower;

public class BigQueryExprParser extends SQLExprParser {
    private static final String[] AGGREGATE_FUNCTIONS;
    private static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = {"ARRAY_AGG", "AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM", "ROW_NUMBER",
                "ROWNUMBER"};
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
                accept(Token.RPAREN);
                return castExpr;
            }
        }
        return super.methodRest(expr, acceptLPAREN);
    }

    private SQLStructExpr struct() {
        SQLStructExpr structExpr = new SQLStructExpr();
        accept(Token.LPAREN);
        aliasedItems(structExpr.getItems(), structExpr);
        accept(Token.RPAREN);
        return structExpr;
    }

    protected void aliasedItems(List<SQLAliasedExpr> items, SQLObject parent) {
        while (true) {
            SQLExpr expr = expr();
            String alias = null;
            if (lexer.nextIf(Token.AS)) {
                alias = alias();
            }

            SQLAliasedExpr aliasedExpr = new SQLAliasedExpr(expr, alias);
            aliasedExpr.setParent(parent);
            items.add(aliasedExpr);

            if (lexer.nextIfComma()) {
                if (lexer.token() == Token.FROM) {
                    break;
                }
                continue;
            }
            break;
        }
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        if (lexer.nextIfIdentifier(FnvHash.Constants.OPTIONS)) {
            parseAssignItem(column.getColProperties(), column);
        }

        return super.parseColumnRest(column);
    }

    protected SQLStructDataType parseDataTypeStruct() {
        acceptIdentifier("STRUCT");

        SQLStructDataType struct = new SQLStructDataType(dbType);
        accept(Token.LT);
        for (; ; ) {
            SQLName name = this.name();

            SQLDataType dataType = this.parseDataType();
            SQLStructDataType.Field field = struct.addField(name, dataType);

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
        return super.primary();
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        if (lexer.token() == Token.LBRACKET) {
            SQLArrayExpr array = new SQLArrayExpr();
            array.setExpr(expr);
            lexer.nextToken();
            this.exprList(array.getValues(), array);
            accept(Token.RBRACKET);
            expr = array;
            return primaryRest(expr);
        }

        if (expr instanceof SQLPropertyExpr) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
            SQLExpr owner = propertyExpr.getOwner();
            if (owner instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) owner;
                long hashCode64 = identifierExpr.hashCode64();
                if (hashCode64 == FnvHash.Constants.DATE
                        || hashCode64 == FnvHash.Constants.DATETIME
                        || hashCode64 == FnvHash.Constants.TIMESTAMP
                ) {
                    String name = null;
                    if (lexer.nextIf(Token.DOT)) {
                        name = lexer.stringVal();
                        lexer.nextToken();
                    }

                    SQLExpr timeZone = null;
                    if (lexer.nextIf(Token.COMMA)) {
                        timeZone = this.primary();
                    }
                    accept(Token.RPAREN);
                    SQLMethodInvokeExpr func = new SQLMethodInvokeExpr((SQLIdentifierExpr) owner);
                    func.addArgument(
                            name != null
                                    ? new SQLPropertyExpr(new SQLIdentifierExpr(propertyExpr.getName()), name)
                                    : new SQLIdentifierExpr(identifierExpr.getName())
                    );
                    if (timeZone != null) {
                        func.addArgument(timeZone);
                    }
                    expr = func;
                }
            }
        }

        return super.primaryRest(expr);
    }

    public SQLDataType parseDataType() {
        if (lexer.nextIf(Token.ANY)) {
            acceptIdentifier("TYPE");
            return new SQLDataTypeImpl("ANY TYPE");
        }
        return parseDataType(true);
    }

    protected SQLExpr dotRest(SQLExpr expr) {
        return super.dotRest(expr);
    }

    protected SQLExpr parseQueryExpr() {
        Lexer.SavePoint mark = lexer.markOut();
        if (lexer.nextIf(Token.SELECT)
                && lexer.nextIf(Token.AS)
                && lexer.nextIfIdentifier(FnvHash.Constants.STRUCT)
        ) {
            return new SQLQueryExpr(
                    new SQLSelect(
                            parseSelectAsStruct()));
        } else {
            lexer.reset(mark);
        }

        return super.parseQueryExpr();
    }

    protected BigQuerySelectAsStruct parseSelectAsStruct() {
        BigQuerySelectAsStruct selectAsStruct = new BigQuerySelectAsStruct();
        aliasedItems(selectAsStruct.getItems(), selectAsStruct);
        if (lexer.nextIf(Token.FROM)) {
            selectAsStruct.setFrom(
                    this.createSelectParser().parseTableSource());
        }
        return selectAsStruct;
    }
}
