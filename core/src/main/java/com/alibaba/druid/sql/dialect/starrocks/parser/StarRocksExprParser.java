package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

public class StarRocksExprParser extends SQLExprParser {
    public static final String[] AGGREGATE_FUNCTIONS;

    public static final long[] AGGREGATE_FUNCTIONS_CODES;

    public static final String[] SINGLE_WORD_TABLE_OPTIONS;

    public static final long[] SINGLE_WORD_TABLE_OPTIONS_CODES;

    static {
        String[] strings = {
                "AVG",
                "ANY_VALUE",
                "BIT_AND",
                "BIT_OR",
                "BIT_XOR",
                "COUNT",
                "GROUP_CONCAT",
                "LISTAGG",
                "MAX",
                "MIN",
                "STD",
                "STDDEV",
                "STDDEV_POP",
                "STDDEV_SAMP",
                "SUM",
                "VAR_SAMP",
                "VARIANCE",
                "JSON_ARRAYAGG",
                "JSON_OBJECTAGG",
        };

        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }

        // https://dev.mysql.com/doc/refman/5.7/en/create-table.html
        String[] options = {"AUTO_INCREMENT", "AVG_ROW_LENGTH", /*"CHARACTER SET",*/ "CHECKSUM", "COLLATE", "COMMENT",
                "COMPRESSION", "CONNECTION", /*"{DATA|INDEX} DIRECTORY",*/ "DELAY_KEY_WRITE", "ENCRYPTION", "ENGINE",
                "INSERT_METHOD", "KEY_BLOCK_SIZE", "MAX_ROWS", "MIN_ROWS", "PACK_KEYS", "PASSWORD", "ROW_FORMAT",
                "STATS_AUTO_RECALC", "STATS_PERSISTENT", "STATS_SAMPLE_PAGES", "TABLESPACE", "UNION",
                "STORAGE_TYPE", "STORAGE_POLICY"};
        SINGLE_WORD_TABLE_OPTIONS_CODES = FnvHash.fnv1a_64_lower(options, true);
        SINGLE_WORD_TABLE_OPTIONS = new String[SINGLE_WORD_TABLE_OPTIONS_CODES.length];
        for (String str : options) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(SINGLE_WORD_TABLE_OPTIONS_CODES, hash);
            SINGLE_WORD_TABLE_OPTIONS[index] = str;
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
        super(lexer, DbType.mysql);
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
//        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
//        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
//        if (sql.length() > 6) {
//            char c0 = sql.charAt(0);
//            char c1 = sql.charAt(1);
//            char c2 = sql.charAt(2);
//            char c3 = sql.charAt(3);
//            char c4 = sql.charAt(4);
//            char c5 = sql.charAt(5);
//            char c6 = sql.charAt(6);
//
//            if (c0 == 'S' && c1 == 'E' && c2 == 'L' && c3 == 'E' && c4 == 'C' && c5 == 'T' && c6 == ' ') {
//                lexer.reset(6, ' ', Token.SELECT);
//                return;
//            }
//
//            if (c0 == 's' && c1 == 'e' && c2 == 'l' && c3 == 'e' && c4 == 'c' && c5 == 't' && c6 == ' ') {
//                lexer.reset(6, ' ', Token.SELECT);
//                return;
//            }
//
//            if (c0 == 'I' && c1 == 'N' && c2 == 'S' && c3 == 'E' && c4 == 'R' && c5 == 'T' && c6 == ' ') {
//                lexer.reset(6, ' ', Token.INSERT);
//                return;
//            }
//
//            if (c0 == 'i' && c1 == 'n' && c2 == 's' && c3 == 'e' && c4 == 'r' && c5 == 't' && c6 == ' ') {
//                lexer.reset(6, ' ', Token.INSERT);
//                return;
//            }
//
//            if (c0 == 'U' && c1 == 'P' && c2 == 'D' && c3 == 'A' && c4 == 'T' && c5 == 'E' && c6 == ' ') {
//                lexer.reset(6, ' ', Token.UPDATE);
//                return;
//            }
//
//            if (c0 == 'u' && c1 == 'p' && c2 == 'd' && c3 == 'a' && c4 == 't' && c5 == 'e' && c6 == ' ') {
//                lexer.reset(6, ' ', Token.UPDATE);
//                return;
//            }
//
//            if (c0 == '/' && c1 == '*' && (isEnabled(SQLParserFeature.OptimizedForParameterized) && !isEnabled(SQLParserFeature.TDDLHint))) {
//                StarRocksLexer srLexer = (StarRocksLexer) lexer;
//                srLexer.skipFirstHintsOrMultiCommentAndNextToken();
//                return;
//            }
//        }
        this.lexer.nextToken();

    }
}
