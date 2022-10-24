/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.alibaba.druid.sql.dialect.saphana.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nukiyoam
 */
public class SAPHanaLexer extends Lexer {
    public static final Keywords DEFAULT_SAP_HANA_KEYWORDS;
    public static SymbolTable quoteTable = new SymbolTable(8192);

    static {
        Map<String, Token> map = new HashMap<>(Keywords.DEFAULT_KEYWORDS.getKeywords());
        map.put("ALL", Token.ALL);
        map.put("ALTER", Token.ALTER);
        map.put("AS", Token.AS);
        map.put("BEFORE", Token.BEFORE);
        map.put("BEGIN", Token.BEGIN);
        map.put("BOTH", Token.BOTH);
        map.put("CASE", Token.CASE);
        map.put("CHAR", Token.CHAR);
        map.put("CONDITION", Token.CONDITION);
        map.put("CONNECT", Token.CONNECT);
        map.put("CROSS", Token.CROSS);
        map.put("CUBE", Token.CUBE);
        map.put("CURRENT_CONNECTION", Token.CURRENT_CONNECTION);
        map.put("CURRENT_DATE", Token.CURRENT_DATE);
        map.put("CURRENT_SCHEMA", Token.CURRENT_SCHEMA);
        map.put("CURRENT_TIME", Token.CURRENT_TIME);
        map.put("CURRENT_TIMESTAMP", Token.CURRENT_TIMESTAMP);
        map.put("CURRENT_TRANSACTION_ISOLATION_LEVEL", Token.CURRENT_TRANSACTION_ISOLATION_LEVEL);
        map.put("CURRENT_USER", Token.CURRENT_USER);
        map.put("CURRENT_UTCDATE", Token.CURRENT_UTCDATE);
        map.put("CURRENT_UTCTIME", Token.CURRENT_UTCTIME);
        map.put("CURRENT_UTCTIMESTAMP", Token.CURRENT_UTCTIMESTAMP);
        map.put("CURRVAL", Token.CURRVAL);
        map.put("CURSOR", Token.CURSOR);
        map.put("DECLARE", Token.DECLARE);
        map.put("DISTINCT", Token.DISTINCT);
        map.put("ELSE", Token.ELSE);
        map.put("ELSEIF", Token.ELSEIF);
        map.put("END", Token.END);
        map.put("EXCEPT", Token.EXCEPT);
        map.put("EXCEPTION", Token.EXCEPTION);
        map.put("EXEC", Token.EXEC);
        map.put("FALSE", Token.FALSE);
        map.put("FOR", Token.FOR);
        map.put("FROM", Token.FROM);
        map.put("FULL", Token.FULL);
        map.put("GROUP", Token.GROUP);
        map.put("HAVING", Token.HAVING);
        map.put("IF", Token.IF);
        map.put("IN", Token.IN);
        map.put("INNER", Token.INNER);
        map.put("INOUT", Token.INOUT);
        map.put("INTERSECT", Token.INTERSECT);
        map.put("INTO", Token.INTO);
        map.put("IS", Token.IS);
        map.put("JOIN", Token.JOIN);
        map.put("LATERAL", Token.LATERAL);
        map.put("LEADING", Token.LEADING);
        map.put("LEFT", Token.LEFT);
        map.put("LIMIT", Token.LIMIT);
        map.put("LOOP", Token.LOOP);
        map.put("MINUS", Token.MINUS);
        map.put("NATURAL", Token.NATURAL);
        map.put("NCHAR", Token.NCHAR);
        map.put("NEXTVAL", Token.NEXTVAL);
        map.put("NULL", Token.NULL);
        map.put("ON", Token.ON);
        map.put("ORDER", Token.ORDER);
        map.put("OUT", Token.OUT);
        map.put("PRIOR", Token.PRIOR);
        map.put("RETURN", Token.RETURN);
        map.put("RETURNS", Token.RETURNS);
        map.put("REVERSE", Token.REVERSE);
        map.put("RIGHT", Token.RIGHT);
        map.put("ROLLUP", Token.ROLLUP);
        map.put("ROWID", Token.ROWID);
        map.put("SELECT", Token.SELECT);
        map.put("SESSION_USER", Token.SESSION_USER);
        map.put("SET", Token.SET);
        map.put("SQL", Token.SQL);
        map.put("START", Token.START);
        map.put("SYSUUID", Token.SYSUUID);
        map.put("TABLESAMPLE", Token.TABLESAMPLE);
        map.put("TOP", Token.TOP);
        map.put("TRAILING", Token.TRAILING);
        map.put("TRUE", Token.TRUE);
        map.put("UNION", Token.UNION);
        map.put("UNKNOWN", Token.UNKNOWN);
        map.put("USING", Token.USING);
        map.put("UTCTIMESTAMP", Token.UTCTIMESTAMP);
        map.put("VALUES", Token.VALUES);
        map.put("WHEN", Token.WHEN);
        map.put("WHERE", Token.WHERE);
        map.put("WHILE", Token.WHILE);
        map.put("WITH", Token.WITH);

        DEFAULT_SAP_HANA_KEYWORDS = new Keywords(map);
    }

    {
        dbType = DbType.sap_hana;
    }

    public SAPHanaLexer(char[] input, int inputLength, boolean skipComment) {
        super(input, inputLength, skipComment);
        super.keywords = DEFAULT_SAP_HANA_KEYWORDS;
    }

    public SAPHanaLexer(String input) {
        this(input, true, true);
    }

    public SAPHanaLexer(String input, SQLParserFeature... features) {
        super(input, true);
        this.keepComments = true;
        super.keywords = DEFAULT_SAP_HANA_KEYWORDS;

        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    public SAPHanaLexer(String input, boolean skipComment, boolean keepComments) {
        super(input, skipComment);
        this.skipComment = skipComment;
        this.keepComments = keepComments;
        super.keywords = DEFAULT_SAP_HANA_KEYWORDS;
    }

}
