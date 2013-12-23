package com.alibaba.druid.sql.dialect.odps.parser;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;

public class OdpsExprParser extends SQLExprParser {

    public final static String[] AGGREGATE_FUNCTIONS = { "AVG", //
            "COUNT", //
            "LAG",
            "LEAD",
            "MAX", //
            "MIN", //
            "STDDEV", //
            "SUM", //
            "ROW_NUMBER"//
                                                     };

    public OdpsExprParser(Lexer lexer){
        super(lexer);

        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
    }

    public OdpsExprParser(String sql){
        this(new OdpsLexer(sql));
        this.lexer.nextToken();
    }
}
