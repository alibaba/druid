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
package com.alibaba.druid.sql.dialect.dm.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * 达梦数据库词法分析器
 * 达梦数据库兼容 Oracle 语法，同时支持部分 MySQL 语法
 */
public class DMLexer extends Lexer {
    public static final Keywords DM_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        // Oracle 兼容关键字
        map.put("BEGIN", Token.BEGIN);
        map.put("CASCADE", Token.CASCADE);
        map.put("CONNECT", Token.CONNECT);
        map.put("CONTINUE", Token.CONTINUE);
        map.put("CROSS", Token.CROSS);
        map.put("CURRENT", Token.CURRENT);
        map.put("CURSOR", Token.CURSOR);

        map.put("FETCH", Token.FETCH);
        map.put("FIRST", Token.FIRST);
        map.put("IDENTITY", Token.IDENTITY);
        map.put("LIMIT", Token.LIMIT);
        map.put("NEXT", Token.NEXT);
        map.put("NOWAIT", Token.NOWAIT);
        map.put("OF", Token.OF);

        map.put("OFFSET", Token.OFFSET);
        map.put("ONLY", Token.ONLY);
        map.put("PRIOR", Token.PRIOR);
        map.put("RECURSIVE", Token.RECURSIVE);
        map.put("RESTART", Token.RESTART);
        map.put("RESTRICT", Token.RESTRICT);
        map.put("RETURN", Token.RETURN);
        map.put("RETURNING", Token.RETURNING);

        map.put("ROW", Token.ROW);
        map.put("ROWS", Token.ROWS);
        map.put("SAVEPOINT", Token.SAVEPOINT);
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

        map.put("MERGE", Token.MERGE);
        map.put("MATCHED", Token.MATCHED);
        map.put("PARTITION", Token.PARTITION);

        // 达梦特有关键字
        map.put("TOP", Token.TOP);
        map.put("ROWNUM", Token.IDENTIFIER);
        map.put("SYSDATE", Token.IDENTIFIER);
        map.put("SYSTIMESTAMP", Token.IDENTIFIER);

        map.put("LOCAL", Token.LOCAL);
        map.put("GLOBAL", Token.GLOBAL);
        map.put("TEMPORARY", Token.TEMPORARY);
        map.put("TEMP", Token.TEMP);

        // 序列相关
        map.put("SEQUENCE", Token.SEQUENCE);
        map.put("NEXTVAL", Token.IDENTIFIER);
        map.put("CURRVAL", Token.IDENTIFIER);

        DM_KEYWORDS = new Keywords(map);
    }

    @Override
    protected Keywords loadKeywords() {
        return DM_KEYWORDS;
    }

    public DMLexer(String input) {
        super(input, true);
        this.keepComments = true;
        super.dbType = DbType.dm;
    }

    public DMLexer(String input, SQLParserFeature... features) {
        super(input, true);
        this.keepComments = true;
        super.dbType = DbType.dm;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }
}
