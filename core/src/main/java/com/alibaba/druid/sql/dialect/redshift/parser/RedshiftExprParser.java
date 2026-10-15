package com.alibaba.druid.sql.dialect.redshift.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import com.alibaba.druid.sql.dialect.redshift.stmt.RedshiftColumnEncode;
import com.alibaba.druid.sql.dialect.redshift.stmt.RedshiftColumnKey;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

public class RedshiftExprParser
        extends PGExprParser {
    public static final String[] AGGREGATE_FUNCTIONS;
    public static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = {
                "AVG", "COUNT", "LISTAGG", "MAX", "MIN", "STDDEV",
                "SUM", "ROW_NUMBER", "PERCENTILE_CONT", "PERCENTILE_DISC", "RANK",
                "DENSE_RANK", "PERCENT_RANK", "CUME_DIST"
        };

        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public RedshiftExprParser(String sql, SQLParserFeature... features) {
        super(new RedshiftLexer(sql, features));
        lexer.nextToken();
        dbType = DbType.redshift;
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public RedshiftExprParser(Lexer lexer) {
        super(lexer);
        dbType = DbType.redshift;
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    @Override
    protected SQLColumnDefinition parseColumnSpecific(SQLColumnDefinition column) {
        switch (lexer.token()) {
            case ENCODE: {
                lexer.nextToken();
                SQLExpr codecExpr;
                if (lexer.token() == Token.AUTO) {
                    codecExpr = new SQLIdentifierExpr("AUTO");
                    lexer.nextToken();
                } else {
                    codecExpr = expr();
                }
                RedshiftColumnEncode sqlColumnEncode = new RedshiftColumnEncode();
                sqlColumnEncode.setExpr(codecExpr);
                column.addConstraint(sqlColumnEncode);
                return parseColumnRest(column);
            }
            case SORTKEY:
            case DISTKEY:
                RedshiftColumnKey key = new RedshiftColumnKey();
                if (lexer.token() == Token.DISTKEY) {
                    key.setDistKey(true);
                } else {
                    key.setSortKey(true);
                }
                lexer.nextToken();
                column.addConstraint(key);
                return parseColumnRest(column);
            case IDENTITY:
                lexer.nextToken();
                SQLColumnDefinition.Identity identity = parseIdentity();
                column.setIdentity(identity);
                return parseColumnRest(column);
            default:
                return column;
        }
    }

    @Override
    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        if (lexer.identifierEquals(FnvHash.Constants.GENERATED)) {
            lexer.nextToken();
            accept(Token.BY);
            accept(Token.DEFAULT);
            accept(Token.AS);
            accept(Token.IDENTITY);
            SQLColumnDefinition.Identity id = parseIdentity();
            column.setGeneratedAlwaysAs(id);
        } else if (lexer.identifierEquals(FnvHash.Constants.COLLATE)) {
            lexer.nextToken();
            column.setCollateExpr(expr());
        }
        return super.parseColumnRest(column);
    }

    @Override
    protected SQLColumnDefinition.Identity parseIdentity() {
        accept(Token.LPAREN);
        SQLColumnDefinition.Identity ident = new SQLColumnDefinition.Identity();
        parseIdentifySpecific();
        if (lexer.token() == Token.LITERAL_INT) {
            ident.setSeed(lexer.integerValue().intValue());
            lexer.nextToken();
        } else {
            throw new ParserException("TODO : " + lexer.info());
        }

        if (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            if (lexer.token() == Token.LITERAL_INT) {
                ident.setIncrement(lexer.integerValue().intValue());
                lexer.nextToken();
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }
        }

        accept(Token.RPAREN);
        return ident;
    }
}
