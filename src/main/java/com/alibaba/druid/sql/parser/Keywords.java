/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.parser;

import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class Keywords {

    private final Map<String, Token> keywords;

    private long[] hashArray;
    private Token[] tokens;

    public final static Keywords     DEFAULT_KEYWORDS;

    public final static Keywords     SQLITE_KEYWORDS;
    public final static Keywords     DM_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

        map.put("ALL", Token.ALL);
        map.put("ALTER", Token.ALTER);
        map.put("AND", Token.AND);
        map.put("ANY", Token.ANY);
        map.put("AS", Token.AS);

        map.put("ENABLE", Token.ENABLE);
        map.put("DISABLE", Token.DISABLE);

        map.put("ASC", Token.ASC);
        map.put("BETWEEN", Token.BETWEEN);
        map.put("BY", Token.BY);
        map.put("CASE", Token.CASE);
        map.put("CAST", Token.CAST);

        map.put("CHECK", Token.CHECK);
        map.put("CONSTRAINT", Token.CONSTRAINT);
        map.put("CREATE", Token.CREATE);
        map.put("DATABASE", Token.DATABASE);
        map.put("DEFAULT", Token.DEFAULT);
        map.put("COLUMN", Token.COLUMN);
        map.put("TABLESPACE", Token.TABLESPACE);
        map.put("PROCEDURE", Token.PROCEDURE);
        map.put("FUNCTION", Token.FUNCTION);

        map.put("DELETE", Token.DELETE);
        map.put("DESC", Token.DESC);
        map.put("DISTINCT", Token.DISTINCT);
        map.put("DROP", Token.DROP);
        map.put("ELSE", Token.ELSE);
        map.put("EXPLAIN", Token.EXPLAIN);
        map.put("EXCEPT", Token.EXCEPT);

        map.put("END", Token.END);
        map.put("ESCAPE", Token.ESCAPE);
        map.put("EXISTS", Token.EXISTS);
        map.put("FOR", Token.FOR);
        map.put("FOREIGN", Token.FOREIGN);

        map.put("FROM", Token.FROM);
        map.put("FULL", Token.FULL);
        map.put("GROUP", Token.GROUP);
        map.put("HAVING", Token.HAVING);
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
        map.put("LEFT", Token.LEFT);

        map.put("LIKE", Token.LIKE);
        map.put("LOCK", Token.LOCK);
        map.put("MINUS", Token.MINUS);
        map.put("NOT", Token.NOT);

        map.put("NULL", Token.NULL);
        map.put("ON", Token.ON);
        map.put("OR", Token.OR);
        map.put("ORDER", Token.ORDER);
        map.put("OUTER", Token.OUTER);

        map.put("PRIMARY", Token.PRIMARY);
        map.put("REFERENCES", Token.REFERENCES);
        map.put("RIGHT", Token.RIGHT);
        map.put("SCHEMA", Token.SCHEMA);
        map.put("SELECT", Token.SELECT);

        map.put("SET", Token.SET);
        map.put("SOME", Token.SOME);
        map.put("TABLE", Token.TABLE);
        map.put("THEN", Token.THEN);
        map.put("TRUNCATE", Token.TRUNCATE);

        map.put("UNION", Token.UNION);
        map.put("UNIQUE", Token.UNIQUE);
        map.put("UPDATE", Token.UPDATE);
        map.put("VALUES", Token.VALUES);
        map.put("VIEW", Token.VIEW);
        map.put("SEQUENCE", Token.SEQUENCE);
        map.put("TRIGGER", Token.TRIGGER);
        map.put("USER", Token.USER);

        map.put("WHEN", Token.WHEN);
        map.put("WHERE", Token.WHERE);
        map.put("XOR", Token.XOR);

        map.put("OVER", Token.OVER);
        map.put("TO", Token.TO);
        map.put("USE", Token.USE);

        map.put("REPLACE", Token.REPLACE);

        map.put("COMMENT", Token.COMMENT);
        map.put("COMPUTE", Token.COMPUTE);
        map.put("WITH", Token.WITH);
        map.put("GRANT", Token.GRANT);
        map.put("REVOKE", Token.REVOKE);
        
        // MySql procedure: add by zz
        map.put("WHILE", Token.WHILE);
        map.put("DO", Token.DO);
        map.put("DECLARE", Token.DECLARE);
        map.put("LOOP", Token.LOOP);
        map.put("LEAVE", Token.LEAVE);
        map.put("ITERATE", Token.ITERATE);
        map.put("REPEAT", Token.REPEAT);
        map.put("UNTIL", Token.UNTIL);
        map.put("OPEN", Token.OPEN);
        map.put("CLOSE", Token.CLOSE);
        map.put("CURSOR", Token.CURSOR);
        map.put("FETCH", Token.FETCH);
        map.put("OUT", Token.OUT);
        map.put("INOUT", Token.INOUT);

        map.put("LIMIT", Token.LIMIT);

        DEFAULT_KEYWORDS = new Keywords(map);

        {
            Map<String, Token> sqlitemap = new HashMap<String, Token>();

            sqlitemap.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

            sqlitemap.put("LIMIT", Token.LIMIT);
            SQLITE_KEYWORDS = new Keywords(sqlitemap);
        }

        {
            Map<String, Token> sqlitemap = new HashMap<String, Token>();

            sqlitemap.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

            sqlitemap.put("MERGE", Token.MERGE);
            sqlitemap.put("MATCHED", Token.MATCHED);
            sqlitemap.put("USING", Token.USING);
            DM_KEYWORDS = new Keywords(sqlitemap);
        }
    }

    public boolean containsValue(Token token) {
        return this.keywords.containsValue(token);
    }

    public Keywords(Map<String, Token> keywords){
        this.keywords = keywords;

        this.hashArray = new long[keywords.size()];
        this.tokens = new Token[keywords.size()];

        int index = 0;
        for (String k : keywords.keySet()) {
            hashArray[index++] = FnvHash.fnv1a_64_lower(k);
        }
        Arrays.sort(hashArray);
        for (Map.Entry<String, Token> entry : keywords.entrySet()) {
            long k = FnvHash.fnv1a_64_lower(entry.getKey());
            index = Arrays.binarySearch(hashArray, k);
            tokens[index] = entry.getValue();
        }
    }

public Token getKeyword(long hash) {
    int index = Arrays.binarySearch(hashArray, hash);
    if (index < 0) {
        return null;
    }
    return tokens[index];
}

    public Token getKeyword(String key) {
        long k = FnvHash.fnv1a_64_lower(key);
        int index = Arrays.binarySearch(hashArray, k);
        if (index < 0) {
            return null;
        }
        return tokens[index];
//        return keywords.get(key);
    }

    public Map<String, Token> getKeywords() {
        return keywords;
    }

}
