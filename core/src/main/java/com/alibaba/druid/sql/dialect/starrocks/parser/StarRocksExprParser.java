package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLPartition;
import com.alibaba.druid.sql.ast.SQLPartitionValue;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.starrocks.ast.expr.StarRocksCharExpr;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

public class StarRocksExprParser extends SQLExprParser {
    public static final String[] AGGREGATE_FUNCTIONS;

    public static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = {
                "SUM",
                "MAX",
                "MIN",
                "REPLACE",
                "HLL_UNION",
                "BITMAP_UNION",
                "REPLACE_IF_NOT_NULL",
        };

        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public StarRocksExprParser(String sql) {
        this(new StarRocksLexer(sql));
        this.lexer.nextToken();
    }

    public StarRocksExprParser(String sql, DbType dbType, SQLParserFeature... features) {
        super(sql, dbType, features);
    }

    public StarRocksExprParser(Lexer lexer) {
        super(lexer, DbType.starrocks);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public StarRocksExprParser(String sql, boolean keepComments) {
        this(new StarRocksLexer(sql, true, keepComments));
        this.lexer.nextToken();
    }

    public StarRocksExprParser(String sql, boolean skipComment, boolean keepComments) {
        this(new StarRocksLexer(sql, skipComment, keepComments));
        this.lexer.nextToken();
    }

    public StarRocksExprParser(Lexer lexer, DbType dbType) {
        super(lexer, dbType);
    }

    public StarRocksExprParser(String sql, SQLParserFeature... features) {
        super(new StarRocksLexer(sql, features), DbType.starrocks);
        this.lexer.nextToken();
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        String text = lexer.stringVal();
        for (int i = 0; i < AGGREGATE_FUNCTIONS.length; ++i) {
            if (text.equalsIgnoreCase(AGGREGATE_FUNCTIONS[i])) {
                SQLCharExpr aggType = new StarRocksCharExpr(text);
                column.setAggType(aggType);
                lexer.nextToken();
            }
        }

        if (lexer.token() == Token.USING) {
            lexer.nextToken();
            SQLCharExpr bitmap = new StarRocksCharExpr(lexer.stringVal());
            column.setBitmap(bitmap);
            lexer.nextToken();
            accept(Token.COMMENT);
            SQLCharExpr indexComment = new StarRocksCharExpr(lexer.stringVal());
            column.setIndexComment(indexComment);
            lexer.nextToken();
        }

        return super.parseColumnRest(column);
    }
    @Override
    public SQLPartition parsePartition() {
        if (lexer.identifierEquals(FnvHash.Constants.DBPARTITION)
            || lexer.identifierEquals(FnvHash.Constants.TBPARTITION)
            || lexer.identifierEquals(FnvHash.Constants.SUBPARTITION)) {
            lexer.nextToken();
        } else {
            accept(Token.PARTITION);
        }

        SQLPartition partitionDef = new SQLPartition();

        SQLName name;
        if (lexer.token() == Token.LITERAL_INT) {
            Number number = lexer.integerValue();
            name = new SQLIdentifierExpr(number.toString());
            lexer.nextToken();
        } else {
            name = this.name();
        }
        partitionDef.setName(name);

        SQLPartitionValue values = this.parsePartitionValues();
        if (values != null) {
            partitionDef.setValues(values);
        }

        for (; ; ) {
            boolean storage = false;
            if (lexer.identifierEquals(FnvHash.Constants.DATA)) {
                lexer.nextToken();
                acceptIdentifier("DIRECTORY");
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                partitionDef.setDataDirectory(this.expr());
            } else if (lexer.token() == Token.TABLESPACE) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLName tableSpace = this.name();
                partitionDef.setTablespace(tableSpace);
            } else if (lexer.token() == Token.INDEX) {
                lexer.nextToken();
                acceptIdentifier("DIRECTORY");
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                partitionDef.setIndexDirectory(this.expr());
            } else if (lexer.identifierEquals(FnvHash.Constants.MAX_ROWS)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLExpr maxRows = this.primary();
                partitionDef.setMaxRows(maxRows);
            } else if (lexer.identifierEquals(FnvHash.Constants.MIN_ROWS)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLExpr minRows = this.primary();
                partitionDef.setMaxRows(minRows);
            } else if (lexer.identifierEquals(FnvHash.Constants.ENGINE) || //
                (storage = (lexer.token() == Token.STORAGE || lexer.identifierEquals(FnvHash.Constants.STORAGE)))) {
                if (storage) {
                    lexer.nextToken();
                }
                acceptIdentifier("ENGINE");

                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                SQLName engine = this.name();
                partitionDef.setEngine(engine);
            } else if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLExpr comment = this.primary();
                partitionDef.setComment(comment);
            } else {
                break;
            }
        }

        if (lexer.identifierEquals("LOCALITY")) {
            lexer.nextToken();
            accept(Token.EQ);
            SQLExpr locality = this.expr();
            partitionDef.setLocality(locality);
        }

        return partitionDef;
    }

}
