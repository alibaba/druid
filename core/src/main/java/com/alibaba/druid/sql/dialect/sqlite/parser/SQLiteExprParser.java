package com.alibaba.druid.sql.dialect.sqlite.parser;

import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

public class SQLiteExprParser extends SQLExprParser {
    private static final String[] AGGREGATE_FUNCTIONS;
    private static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = {
                "AVG", "COUNT", "GROUP_CONCAT", "MAX", "MIN", "SUM", "TOTAL"
        };
        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public SQLiteExprParser(String sql) {
        this(new SQLiteLexer(sql));
        this.lexer.nextToken();
    }

    public SQLiteExprParser(String sql, SQLParserFeature... features) {
        this(new SQLiteLexer(sql, features));
        this.lexer.nextToken();
    }

    public SQLiteExprParser(Lexer lexer) {
        super(lexer);
        dbType = lexer.getDbType();
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        column = super.parseColumnRest(column);

        if (lexer.identifierEquals("AUTOINCREMENT")
                || lexer.identifierEquals("AUTO_INCREMENT")) {
            lexer.nextToken();
            column.setAutoIncrement(true);
        }

        return column;
    }
}
