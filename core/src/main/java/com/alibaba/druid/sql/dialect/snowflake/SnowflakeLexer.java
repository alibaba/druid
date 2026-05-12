package com.alibaba.druid.sql.dialect.snowflake;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.DialectFeature.ParserFeature.*;

public class SnowflakeLexer extends Lexer {
    static final Keywords SNOWFLAKE_KEYWORDS;
    static final DialectFeature SNOWFLAKE_FEATURE = new DialectFeature(
            Arrays.asList(
                    SQLDateExpr,
                    SQLTimestampExpr,
                    GroupByAll,
                    DialectFeature.LexerFeature.NextTokenColon
            ),
            null
    );

    static {
        Map<String, Token> map = new HashMap<String, Token>();

        map.put("ALL", Token.ALL);
        map.put("ALTER", Token.ALTER);
        map.put("AND", Token.AND);
        map.put("ANY", Token.ANY);
        map.put("AS", Token.AS);
        map.put("ASC", Token.ASC);
        map.put("BETWEEN", Token.BETWEEN);
        map.put("BY", Token.BY);
        map.put("CASE", Token.CASE);
        map.put("CAST", Token.CAST);
        map.put("CHECK", Token.CHECK);
        map.put("COLUMN", Token.COLUMN);
        map.put("COMMENT", Token.COMMENT);
        map.put("CONSTRAINT", Token.CONSTRAINT);
        map.put("CREATE", Token.CREATE);
        map.put("CROSS", Token.CROSS);
        map.put("CURRENT", Token.CURRENT);
        map.put("CURSOR", Token.CURSOR);
        map.put("DEFAULT", Token.DEFAULT);
        map.put("DELETE", Token.DELETE);
        map.put("DESC", Token.DESC);
        map.put("DISABLE", Token.DISABLE);
        map.put("DISTINCT", Token.DISTINCT);
        map.put("DROP", Token.DROP);
        map.put("ELSE", Token.ELSE);
        map.put("ENABLE", Token.ENABLE);
        map.put("END", Token.END);
        map.put("ESCAPE", Token.ESCAPE);
        map.put("EXCEPT", Token.EXCEPT);
        map.put("EXISTS", Token.EXISTS);
        map.put("EXTRACT", Token.EXTRACT);
        map.put("FALSE", Token.FALSE);
        map.put("FETCH", Token.FETCH);
        map.put("FOR", Token.FOR);
        map.put("FOREIGN", Token.FOREIGN);
        map.put("FROM", Token.FROM);
        map.put("FULL", Token.FULL);
        map.put("FUNCTION", Token.FUNCTION);
        map.put("GRANT", Token.GRANT);
        map.put("GROUP", Token.GROUP);
        map.put("HAVING", Token.HAVING);
        map.put("IF", Token.IF);
        map.put("ILIKE", Token.ILIKE);
        map.put("IN", Token.IN);
        map.put("INDEX", Token.INDEX);
        map.put("INNER", Token.INNER);
        map.put("INSERT", Token.INSERT);
        map.put("INTERSECT", Token.INTERSECT);
        map.put("INTERVAL", Token.INTERVAL);
        map.put("INTO", Token.INTO);
        map.put("IS", Token.IS);
        map.put("JOIN", Token.JOIN);
        map.put("KEY", Token.KEY);
        map.put("LATERAL", Token.LATERAL);
        map.put("LEFT", Token.LEFT);
        map.put("LIKE", Token.LIKE);
        map.put("LIMIT", Token.LIMIT);
        map.put("MERGE", Token.MERGE);
        map.put("MATCHED", Token.MATCHED);
        map.put("MINUS", Token.MINUS);
        map.put("NOT", Token.NOT);
        map.put("NULL", Token.NULL);
        map.put("OF", Token.OF);
        map.put("ON", Token.ON);
        map.put("OR", Token.OR);
        map.put("ORDER", Token.ORDER);
        map.put("OUTER", Token.OUTER);
        map.put("OVER", Token.OVER);
        map.put("OVERWRITE", Token.OVERWRITE);
        map.put("PARTITION", Token.PARTITION);
        map.put("PRIMARY", Token.PRIMARY);
        map.put("PROCEDURE", Token.PROCEDURE);
        map.put("QUALIFY", Token.QUALIFY);
        map.put("RECURSIVE", Token.RECURSIVE);
        map.put("REFERENCES", Token.REFERENCES);
        map.put("REPLACE", Token.REPLACE);
        map.put("RETURN", Token.RETURN);
        map.put("REVOKE", Token.REVOKE);
        map.put("RIGHT", Token.RIGHT);
        map.put("ROWS", Token.ROWS);
        map.put("SELECT", Token.SELECT);
        map.put("SET", Token.SET);
        map.put("SHOW", Token.SHOW);
        map.put("SOME", Token.SOME);
        map.put("TABLE", Token.TABLE);
        map.put("TABLESAMPLE", Token.TABLESAMPLE);
        map.put("THEN", Token.THEN);
        map.put("TO", Token.TO);
        map.put("TOP", Token.TOP);
        map.put("TRUE", Token.TRUE);
        map.put("TRUNCATE", Token.TRUNCATE);
        map.put("UNION", Token.UNION);
        map.put("UNIQUE", Token.UNIQUE);
        map.put("UPDATE", Token.UPDATE);
        map.put("USE", Token.USE);
        map.put("USING", Token.USING);
        map.put("VALUES", Token.VALUES);
        map.put("VIEW", Token.VIEW);
        map.put("WHEN", Token.WHEN);
        map.put("WHERE", Token.WHERE);
        map.put("WINDOW", Token.WINDOW);
        map.put("WITH", Token.WITH);
        map.put("BEGIN", Token.BEGIN);
        map.put("EXCEPTION", Token.EXCEPTION);
        map.put("LOOP", Token.LOOP);
        map.put("WHILE", Token.WHILE);
        map.put("DO", Token.DO);
        map.put("START", Token.START);
        map.put("COMMENT", Token.COMMENT);
        map.put("SEQUENCE", Token.SEQUENCE);
        map.put("SESSION", Token.SESSION);
        map.put("GRANT", Token.GRANT);
        map.put("REVOKE", Token.REVOKE);
        map.put("COMMIT", Token.COMMIT);
        map.put("SAVEPOINT", Token.SAVEPOINT);
        map.put("DECLARE", Token.DECLARE);
        map.put("IMMEDIATE", Token.IMMEDIATE);
        map.put("TEMPORARY", Token.TEMPORARY);
        map.put("TEMP", Token.TEMP);
        map.put("IF", Token.IF);
        map.put("EXISTS", Token.EXISTS);
        map.put("REPLACE", Token.REPLACE);
        map.put("OFFSET", Token.OFFSET);
        map.put("FETCH", Token.FETCH);
        map.put("FIRST", Token.FIRST);
        map.put("NEXT", Token.NEXT);
        map.put("TYPE", Token.TYPE);
        map.put("LOGGING", Token.LOGGING);
        map.put("SCHEMA", Token.SCHEMA);
        map.put("DATABASE", Token.DATABASE);
        map.put("PRIOR", Token.PRIOR);

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

    @Override
    protected void initDialectFeature() {
        this.dialectFeature = SNOWFLAKE_FEATURE;
    }

    @Override
    protected void scanVariable_at() {
        if (ch != '@') {
            throw new ParserException("illegal variable. " + info());
        }

        mark = pos;
        bufPos = 1;
        char c;

        // Handle @@ for session variables
        final char c1 = charAt(pos + 1);
        if (c1 == '@') {
            ++pos;
            bufPos++;
        }

        // Scan identifier characters including path separators for stage paths
        for (; ; ) {
            c = charAt(++pos);

            // Snowflake stage paths can contain / and other characters
            if (Character.isLetterOrDigit(c) || c == '_' || c == '$' || c == '/' || c == '.' || c == '-') {
                bufPos++;
                continue;
            }

            break;
        }

        this.ch = charAt(pos);
        stringVal = addSymbol();
        token = Token.VARIANT;
    }

    @Override
    public void scanComment() {
        if ((ch == '/' && charAt(pos + 1) == '/')
                || (ch == '-' && charAt(pos + 1) == '-')) {
            scanSingleLineComment();
        } else if (ch == '/' && charAt(pos + 1) == '*') {
            scanMultiLineComment();
        } else {
            throw new IllegalStateException();
        }
    }
}
