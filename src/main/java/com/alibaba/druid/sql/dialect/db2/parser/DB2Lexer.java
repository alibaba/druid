/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.db2.parser;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;


public class DB2Lexer extends Lexer {

    public final static Keywords DEFAULT_DB2_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());
        
        map.put("FETCH", Token.FETCH);
        map.put("FIRST", Token.FIRST);
        map.put("ONLY", Token.ONLY);
        map.put("OPTIMIZE", Token.OPTIMIZE);
        
        DEFAULT_DB2_KEYWORDS = new Keywords(map);
    }

    public DB2Lexer(String input){
        super(input);
        super.keywods = DEFAULT_DB2_KEYWORDS;
    }
}
