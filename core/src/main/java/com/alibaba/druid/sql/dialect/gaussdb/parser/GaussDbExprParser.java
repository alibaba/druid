package com.alibaba.druid.sql.dialect.gaussdb.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbPartitionValue;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

import static com.alibaba.druid.util.FnvHash.fnv1a_64_lower;

public class GaussDbExprParser extends PGExprParser {
    private static final String[] AGGREGATE_FUNCTIONS;
    private static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = {
                "SUM", "MAX", "MIN", "AVG", "MEDIAN", "PERCENTILE_CONT", "PERCENTILE_DISC", "COUNT", "ARRAY_AGG",
                "STRING_AGG", "LIST_AGG", "GROUP_CONCAT", "COVAR_POP", "COVAR_SAMP", "STDDEV_POP", "STDDEV_SAMP",
                "VAR_POP", "VAR_SAMP", "BIT_AND", "BIT_OR", "BOOL_AND", "BOOL_OR", "CORR", "EVERY", "RANK", "REGR_AVGX",
                "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY",
                "STDDEV", "VARIANCE", "CHECKSUM"
        };
        AGGREGATE_FUNCTIONS_CODES = fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public GaussDbExprParser(String sql, SQLParserFeature... features) {
        this(new GaussDbLexer(sql, features));
        this.lexer.nextToken();
        dbType = DbType.gaussdb;
    }

    public GaussDbExprParser(GaussDbLexer lexer) {
        super(lexer);
        this.dbType = DbType.gaussdb;
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public SQLPartition parsePartition() {
        accept(Token.PARTITION);
        SQLPartition partitionDef = new SQLPartition();
        SQLName name = this.name();
        partitionDef.setName(name);
        partitionDef.setValues(this.parsePartitionValues(false));
        return partitionDef;
    }

    public SQLPartition parseDistribution() {
        acceptIdentifier("SLICE");
        SQLPartition partitionDef = new SQLPartition();
        SQLName name = this.name();
        partitionDef.setName(name);
        partitionDef.setValues(this.parsePartitionValues(true));
        return partitionDef;
    }

    public SQLPartitionValue parsePartitionValues(boolean isDistribute) {
        GaussDbPartitionValue values = null;
        boolean isInterval = false;
        if (lexer.token() == Token.VALUES) {
            lexer.nextToken();
            if (lexer.token() == Token.IN) {
                lexer.nextToken();
                values = new GaussDbPartitionValue(SQLPartitionValue.Operator.In);
                accept(Token.LPAREN);
                this.exprList(values.getItems(), values);
                accept(Token.RPAREN);
            } else if (lexer.identifierEquals(FnvHash.Constants.LESS)) {
                lexer.nextToken();
                acceptIdentifier("THAN");
                values = new GaussDbPartitionValue(SQLPartitionValue.Operator.LessThan);
                if (lexer.identifierEquals(FnvHash.Constants.MAXVALUE)) {
                    SQLIdentifierExpr maxValue = new SQLIdentifierExpr(lexer.stringVal());
                    lexer.nextToken();
                    maxValue.setParent(values);
                    values.addItem(maxValue);
                } else {
                    //添加dataNode信息
                    //   PARTITION p1 VALUES LESS THAN (200) TABLESPACE tbs_test_range1_p1
                    accept(Token.LPAREN);
                    this.exprList(values.getItems(), values);
                    accept(Token.RPAREN);
                }
            } else if (lexer.token() == Token.LPAREN) {
                values = new GaussDbPartitionValue(SQLPartitionValue.Operator.List);
                lexer.nextToken();
                this.exprList(values.getItems(), values);
                accept(Token.RPAREN);
                if (lexer.token() == Token.TABLESPACE) {
                    lexer.nextToken();
                    values.setSpaceName(this.expr());
                }
                else if (lexer.identifierEquals(FnvHash.Constants.DATANODE)) {
                    lexer.nextToken();
                    values.setDataNodes(this.expr());
                }
            }
        } else if (lexer.token() == Token.START) {
            lexer.nextToken();
            values = new GaussDbPartitionValue(SQLPartitionValue.Operator.StartEndEvery);
            isInterval = true;
            values.setStart(this.expr());
        }
        if (lexer.token() == Token.END) {
            lexer.nextToken();
            if (!isInterval) {
                values = new GaussDbPartitionValue(SQLPartitionValue.Operator.StartEndEvery);
            }
            values.setEnd(this.expr());
            if (lexer.identifierEquals(FnvHash.Constants.EVERY)) {
                lexer.nextToken();
                if (!isInterval) {
                    values = new GaussDbPartitionValue(SQLPartitionValue.Operator.StartEndEvery);
                }
                values.setEvery(this.expr());
            }
        }
        if (lexer.token() == Token.TABLESPACE) {
            lexer.nextToken();
            values.setSpaceName(this.expr());
        } else if (lexer.identifierEquals(FnvHash.Constants.DATANODE)) {
            lexer.nextToken();
            values.setDataNodes(this.expr());
        }
        if (values != null) {
            values.setDistribute(isDistribute);
        }
        return values;
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        switch (lexer.token()) {
            case IDENTIFIER:
                long hash = lexer.hashLCase();
                if (hash == FnvHash.Constants.DELTA || hash == FnvHash.Constants.PREFIX ||
                        hash == FnvHash.Constants.NUMSTR || hash == FnvHash.Constants.NOCOMPRESS ||
                        hash == FnvHash.Constants.DICTIONARY) {
                    column.setCompression(new SQLCharExpr(lexer.stringVal()));
                    lexer.nextToken();
                    return parseColumnRest(column);
                } else {
                    return super.parseColumnRest(column);
                }
            case ON:
                lexer.nextToken();
                if (lexer.token() == Token.UPDATE) {
                    lexer.nextToken();
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("syntax error, expect ");
                    sb.append((Token.UPDATE.name != null ? Token.UPDATE.name : Token.UPDATE.toString()));
                    sb.append(", actual ");
                    sb.append((lexer.token().name != null ? lexer.token().name : lexer.token().toString()));
                    sb.append(" ");
                    sb.append(lexer.info());
                    throw new ParserException(sb.toString());
                }
                MySqlExprParser mySqlExprParser = new MySqlExprParser(lexer);
                SQLExpr expr = mySqlExprParser.primary();
                column.setOnUpdate(expr);
            default:
                return super.parseColumnRest(column);
        }
    }

    protected SQLAggregateExpr parseAggregateExprRest(SQLAggregateExpr aggregateExpr) {
        if (lexer.token() == Token.ORDER) {
            SQLOrderBy orderBy = this.parseOrderBy();
            aggregateExpr.setOrderBy(orderBy);
            //为了兼容之前的逻辑
            aggregateExpr.putAttribute("ORDER BY", orderBy);
        }
        if (lexer.identifierEquals(FnvHash.Constants.SEPARATOR)) {
            lexer.nextToken();

            SQLExpr seperator = this.primary();
            seperator.setParent(aggregateExpr);

            aggregateExpr.putAttribute("SEPARATOR", seperator);
        }
        return aggregateExpr;
    }
}
