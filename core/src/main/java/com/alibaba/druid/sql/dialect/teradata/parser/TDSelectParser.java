package com.alibaba.druid.sql.dialect.teradata.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLTop;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.teradata.ast.TDNormalize;
import com.alibaba.druid.sql.dialect.teradata.ast.TDSelectQueryBlock;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;

public class TDSelectParser extends SQLSelectParser {
    /**
     * <p>
     * [ request_modifier ] { SELECT | SEL }
     * [ WITH DELETED ROWS ]
     * [ AS JSON ]
     * [ select_list ]
     *    { { DISTINCT | ALL | normalize } [ { table_name.] * | column_name }.ALL ] |
     *        TOP_clause
     *    }
     *    { * | expr_spec [,...] }
     * [ FROM_clause ]
     *    FROM
     *        { table_name [ [AS] correlation_name ] |
     *            join |
     *            derived_table_spec(Not supported) |
     *            table_function(Not supported)  |
     *            table_operator(Not supported) }
     * [ WITH_clause ]
     *    WITH
     *        { query_name [ column_list ] AS ( select_expression ) |
     *            RECURSIVE recursive_query_name [ column_list ] AS ( seed seed_spec [...] )
     *        }
     * [ WHERE_clause ]
     * [ GROUP_BY_clause ]
     * [ HAVING_clause | QUALIFY_clause ]
     * [ SAMPLE_clause ] Not supported.
     *    SAMPLE
     *        [ WITH REPLACEMENT ]
     *        [ RANDOMIZED LOCALIZATION ]
     *        { { fraction_description | count_description } [,...] |
     *            when_clause ]
     *        }
     * [ EXPAND_ON_clause ] Not supported.
     *    EXPAND ON expand_expression [AS] expand_column_alias
     *        [ BY { interval_literal |
     *                ANCHOR [PERIOD] anchor_name [ AT time_literal ]}
     *        ] [ FOR period_expression ]
     * [ ORDER_BY_clause ]
     *    ORDER BY
     *        { expression | column_name | column_name_alias | column_position }
     *        [ ASC |DESC ] [ NULLS { FIRST | LAST } ]
     * [ WITH_clause [,...] ] [;]
     * </p>
     */
    public TDSelectParser(Lexer lexer) {
        super(lexer);
    }

    public TDSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
        dbType = DbType.teradata;
    }

    protected TDExprParser createExprParser() {
        return new TDExprParser(lexer);
    }

    protected SQLSelectQueryBlock createSelectQueryBlock() {
        return new TDSelectQueryBlock(dbType);
    }

    @Override
    protected void querySelectListBefore(SQLSelectQueryBlock x) {
        if (x instanceof TDSelectQueryBlock) {
            if (lexer.nextIf(Token.WITH)) {
                acceptIdentifier("DELETED");
                acceptIdentifier("ROWS");
                ((TDSelectQueryBlock) x).setWithDeletedRows(true);
            }
            if (lexer.nextIf(Token.AS)) {
                acceptIdentifier("JSON");
                ((TDSelectQueryBlock) x).setAsJson(true);
            }
        }
    }
    @Override
    protected void parseBeforeSelectList(SQLSelectQueryBlock queryBlock) {
        if (lexer.token() == Token.DISTINCT) {
            queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
            lexer.nextToken();
        } else if (lexer.token() == Token.UNIQUE) {
            queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
            lexer.nextToken();
        } else if (lexer.nextIfIdentifier("NORMALIZE") && queryBlock instanceof TDSelectQueryBlock) {
            TDNormalize tdNormalize = new TDNormalize();
            if (lexer.nextIf(Token.ON)) {
                if (lexer.nextIfIdentifier("MEETS")) {
                    tdNormalize.setMeets(true);
                    tdNormalize.setMeetsFirst(true);
                } else if (lexer.nextIfIdentifier("OVERLAPS")) {
                    tdNormalize.setOverlaps(true);
                    tdNormalize.setMeetsFirst(false);
                } else {
                    setErrorEndPos(lexer.pos());
                    printError(lexer.token());
                }
                if (lexer.nextIf(Token.OR)) {
                    if (lexer.nextIfIdentifier("MEETS")) {
                        tdNormalize.setMeets(true);
                    } else if (lexer.nextIfIdentifier("OVERLAPS")) {
                        tdNormalize.setOverlaps(true);
                    } else {
                        setErrorEndPos(lexer.pos());
                        printError(lexer.token());
                    }
                }
            }
            ((TDSelectQueryBlock) queryBlock).setNormalize(tdNormalize);
        }
    }

    @Override
    protected void parseTop(SQLSelectQueryBlock x) {
        if (x instanceof TDSelectQueryBlock) {
            SQLTop top = this.exprParser.parseTop();
            ((TDSelectQueryBlock) x).setTop(top);
        }
    }
}
