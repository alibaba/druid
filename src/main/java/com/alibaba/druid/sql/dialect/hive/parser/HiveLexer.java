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
package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.*;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.LayoutCharacters.EOI;

public class HiveLexer extends Lexer {
    public final static Keywords DEFAULT_HIVE_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("OF", Token.OF);
        map.put("CONCAT", Token.CONCAT);
        map.put("CONTINUE", Token.CONTINUE);
        map.put("MERGE", Token.MERGE);
        map.put("MATCHED", Token.MATCHED);
        map.put("USING", Token.USING);

        map.put("ROW", Token.ROW);
        map.put("LIMIT", Token.LIMIT);
        map.put("PARTITIONED", Token.PARTITIONED);
        map.put("PARTITION", Token.PARTITION);
        map.put("OVERWRITE", Token.OVERWRITE);
//        map.put("SORT", Token.SORT);
        map.put("IF", Token.IF);
        map.put("TRUE", Token.TRUE);
        map.put("FALSE", Token.FALSE);
        map.put("RLIKE", Token.RLIKE);
        map.put("CONSTRAINT", Token.CONSTRAINT);
        map.put("DIV", Token.DIV);

        DEFAULT_HIVE_KEYWORDS = new Keywords(map);
    }

    public HiveLexer(String input){
        super(input);
        this.skipComment = true;
        this.keepComments = true;
        dbType = DbType.hive;
        this.features |= SQLParserFeature.SupportUnicodeCodePoint.mask;
        super.keywords = DEFAULT_HIVE_KEYWORDS;
    }

    public HiveLexer(String input, SQLParserFeature... features){
        super(input);
        dbType = DbType.hive;
        this.skipComment = true;
        this.keepComments = true;
        super.keywords = DEFAULT_HIVE_KEYWORDS;
        this.features |= SQLParserFeature.SupportUnicodeCodePoint.mask;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    protected final void scanString() {
        scanString2();
    }

    public void scanComment() {
        scanHiveComment();
    }
}
