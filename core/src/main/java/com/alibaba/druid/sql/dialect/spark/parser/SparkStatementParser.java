/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.spark.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.spark.ast.stmt.SparkCreateScanStatement;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.Token;

/**
 * @author peiheng.qph
 * @version $Id: AntsparkStateMentParser.java, v 0.1 2018年09月14日 15:07 peiheng.qph Exp $
 */
public class SparkStatementParser extends HiveStatementParser {
    public SparkStatementParser(String sql) {
        super(new SparkExprParser(sql));
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new SparkCreateTableParser(this.exprParser);
    }

    protected void alterTableUnset(SQLAlterTableStatement stmt) {
        acceptIdentifier("TBLPROPERTIES");
        accept(Token.LPAREN);
        exprParser.names(stmt.getUnsetTableOptions(), stmt);
        accept(Token.RPAREN);
    }

    public SQLStatement parseCreateScan() {
        SparkCreateScanStatement stmt = new SparkCreateScanStatement();
        accept(Token.CREATE);
        acceptIdentifier("SCAN");
        stmt.setName(exprParser.name());
        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            stmt.setOn(this.exprParser.name());
        }
        if (lexer.token() == Token.USING) {
            lexer.nextToken();
            stmt.setUsing(this.exprParser.expr());
        }
        if (lexer.identifierEquals("OPTIONS")) {
            lexer.nextToken();
            accept(Token.LPAREN);

            for (; ; ) {
                SQLAssignItem item = this.exprParser.parseAssignItem();
                stmt.addOption(item);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
            accept(Token.RPAREN);
        }

        return stmt;
    }
}
