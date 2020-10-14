/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.antspark.parser;

import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

/**
 *
 * @author peiheng.qph
 * @version $Id: AntsparkStateMentParser.java, v 0.1 2018年09月14日 15:07 peiheng.qph Exp $
 */
public class AntsparkStatementParser extends SQLStatementParser {
    public AntsparkStatementParser(String sql) {
        super(new AntsparkExprParser(sql));
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new AntsparkCreateTableParser(this.exprParser);
    }

}