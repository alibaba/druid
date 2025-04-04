/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.spark.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.hive.parser.HiveLexer;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.DialectFeature.ParserFeature.*;
/**
 * @author peiheng.qph
 * @version $Id: AntsparkLexer.java, v 0.1 2018年09月14日 15:04 peiheng.qph Exp $
 */
public class SparkLexer extends HiveLexer {
    @Override
    protected Keywords loadKeywords() {
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
        map.put("CACHE", Token.CACHE);
        map.put("QUALIFY", Token.QUALIFY);
        map.put("OR", Token.OR);

        return new Keywords(map);
    }

    public SparkLexer(String input) {
        this(input, DbType.spark);
    }

    public SparkLexer(String input, DbType dbType) {
        super(input, dbType);
    }

    public SparkLexer(String input, SQLParserFeature... features) {
        super(input, DbType.spark, features);
    }
    public SparkLexer(String input, DbType dbType, SQLParserFeature... features) {
        super(input, dbType, features);
    }
    @Override
    protected void initDialectFeature() {
        super.initDialectFeature();
        this.dialectFeature.configFeature(
                QueryTable,
                ParseSelectItemPrefixX,
                JoinRightTableFrom
        );
    }
}
