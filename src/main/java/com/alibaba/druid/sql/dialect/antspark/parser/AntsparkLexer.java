/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.antspark.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author peiheng.qph
 * @version $Id: AntsparkLexer.java, v 0.1 2018年09月14日 15:04 peiheng.qph Exp $
 */
public class AntsparkLexer extends Lexer {
    public final static Keywords DEFAULT_ANTSPARK_KEYWORDS;

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

        DEFAULT_ANTSPARK_KEYWORDS = new Keywords(map);
    }
    public AntsparkLexer(String input) {
        super(input);
        dbType= DbType.antspark;
        super.keywords = DEFAULT_ANTSPARK_KEYWORDS;
    }

}