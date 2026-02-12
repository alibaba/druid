package com.alibaba.druid.sql.dialect.snowflake;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class SnowflakeLexer extends Lexer {
    public static final Keywords SNOWFLAKE_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        // Snowflake specific keywords
        map.put("BEGIN", Token.BEGIN);
        map.put("CASCADE", Token.CASCADE);
        map.put("CLONE", Token.CLONE);
        map.put("CONTINUE", Token.CONTINUE);
        map.put("CURRENT", Token.CURRENT);
        map.put("FETCH", Token.FETCH);
        map.put("FIRST", Token.FIRST);
        map.put("IDENTITY", Token.IDENTITY);
        map.put("LIMIT", Token.LIMIT);
        map.put("NEXT", Token.NEXT);
        map.put("NOWAIT", Token.NOWAIT);
        map.put("OF", Token.OF);
        map.put("OFFSET", Token.OFFSET);
        map.put("ONLY", Token.ONLY);
        map.put("QUALIFY", Token.QUALIFY);
        map.put("RECURSIVE", Token.RECURSIVE);
        map.put("RESTART", Token.RESTART);
        map.put("RESTRICT", Token.RESTRICT);
        map.put("RETURNING", Token.RETURNING);
        map.put("ROW", Token.ROW);
        map.put("ROWS", Token.ROWS);
        map.put("SHARE", Token.SHARE);
        map.put("SHOW", Token.SHOW);
        map.put("START", Token.START);
        map.put("TRUE", Token.TRUE);
        map.put("FALSE", Token.FALSE);
        map.put("ARRAY", Token.ARRAY);
        map.put("IF", Token.IF);
        map.put("TYPE", Token.TYPE);
        map.put("ILIKE", Token.ILIKE);
        map.put("MERGE", Token.MERGE);
        map.put("MATCHED", Token.MATCHED);
        map.put("PARTITION", Token.PARTITION);
        map.put("INTERVAL", Token.INTERVAL);
        map.put("LANGUAGE", Token.LANGUAGE);
        map.put("LOCAL", Token.LOCAL);
        map.put("TOP", Token.TOP);
        map.put("USING", Token.USING);
        map.put("WINDOW", Token.WINDOW);
        map.put("TABLESAMPLE", Token.TABLESAMPLE);
        map.put("REPEATABLE", Token.REPEATABLE);
        map.put("STAGE", Token.STAGE);
        map.put("PIPE", Token.PIPE);
        map.put("STREAM", Token.STREAM);
        map.put("TASK", Token.TASK);
        map.put("WAREHOUSE", Token.WAREHOUSE);
        map.put("COPY", Token.COPY);
        map.put("OVERWRITE", Token.OVERWRITE);
        map.put("PIVOT", Token.PIVOT);
        map.put("UNPIVOT", Token.UNPIVOT);
        map.put("LATERAL", Token.LATERAL);
        map.put("AT", Token.AT);
        map.put("BEFORE", Token.BEFORE);
        map.put("STATEMENT", Token.STATEMENT);
        map.put("TIMESTAMP", Token.TIMESTAMP);
        map.put("UNDROP", Token.UNDROP);
        map.put("MASKING", Token.MASKING);
        map.put("POLICY", Token.POLICY);
        map.put("NETWORK", Token.NETWORK);
        map.put("INTEGRATION", Token.INTEGRATION);
        map.put("NOTIFICATION", Token.NOTIFICATION);
        map.put("SECURITY", Token.SECURITY);
        map.put("API", Token.API);
        map.put("POOL", Token.POOL);
        map.put("MONITOR", Token.MONITOR);
        map.put("ALERT", Token.ALERT);
        map.put("APPLICATION", Token.APPLICATION);
        map.put("SERVICE", Token.SERVICE);
        map.put("IMAGE", Token.IMAGE);
        map.put("MINING", Token.MINING);

        SNOWFLAKE_KEYWORDS = new Keywords(map);
    }

    @Override
    protected Keywords loadKeywords() {
        return SNOWFLAKE_KEYWORDS;
    }

    public SnowflakeLexer(String input, SQLParserFeature... features) {
        super(input);
        dbType = DbType.snowflake;
        this.skipComment = true;
        this.keepComments = true;
        this.features |= SQLParserFeature.SupportUnicodeCodePoint.mask;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }
}
