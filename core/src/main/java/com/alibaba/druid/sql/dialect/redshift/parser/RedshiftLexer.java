package com.alibaba.druid.sql.dialect.redshift.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGLexer;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class RedshiftLexer
        extends PGLexer {
    @Override
    protected Keywords loadKeywords() {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("BEGIN", Token.BEGIN);
        map.put("CASCADE", Token.CASCADE);
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
        map.put("RECURSIVE", Token.RECURSIVE);
        map.put("RESTART", Token.RESTART);

        map.put("RESTRICT", Token.RESTRICT);
        map.put("RETURNING", Token.RETURNING);
        map.put("ROW", Token.ROW);
        map.put("ROWS", Token.ROWS);
        map.put("SHARE", Token.SHARE);
        map.put("SHOW", Token.SHOW);
        map.put("START", Token.START);

        map.put("USING", Token.USING);
        map.put("WINDOW", Token.WINDOW);

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
        map.put("QUALIFY", Token.QUALIFY);
        map.put("TABLE", Token.TABLE);
        map.put("ENCODE", Token.ENCODE);
        map.put("DISTSTYLE", Token.DISTSTYLE);
        map.put("AUTO", Token.AUTO);
        map.put("EVEN", Token.EVEN);
        map.put("KEY", Token.KEY);
        map.put("ALL", Token.ALL);
        map.put("DISTKEY", Token.DISTKEY);
        map.put("COMPOUND", Token.COMPOUND);
        map.put("INTERLEAVED", Token.INTERLEAVED);
        map.put("SORTKEY", Token.SORTKEY);
        map.put("BACKUP", Token.BACKUP);

        return new Keywords(map);
    }

    public RedshiftLexer(String input, SQLParserFeature... features) {
        super(input, features);
        dbType = DbType.redshift;
    }
}
