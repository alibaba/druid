package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateTableStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.Map;

public class StarRocksCreateTableParser extends SQLCreateTableParser {

    public StarRocksCreateTableParser(Lexer lexer) {
        super(new StarRocksExprParser(lexer));
    }

    public StarRocksCreateTableParser(String sql) {
        super(new StarRocksExprParser(sql));
    }

    public StarRocksCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }


    public void parseCreateTableRest(SQLCreateTableStatement stmt) {
        StarRocksCreateTableStatement srStmt = (StarRocksCreateTableStatement)stmt;

        if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            stmt.setEngine(
                    this.exprParser.expr()
            );
        }

        if (lexer.identifierEquals(FnvHash.Constants.DUPLICATE) || lexer.identifierEquals(FnvHash.Constants.AGGREGATE)
            || lexer.identifierEquals(FnvHash.Constants.UNIQUE) || lexer.identifierEquals(FnvHash.Constants.PRIMARY)) {
                SQLName model = this.exprParser.name();
                srStmt.setModelKey(model);
                if (lexer.token() == Token.KEY) {
                    accept(Token.KEY);
                    this.exprParser.exprList(srStmt.getParameters(), srStmt);
                }
        }

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            accept(Token.BY);
            SQLExpr expr = this.exprParser.expr();
            srStmt.setPartitionBy(expr);
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                if (lexer.token() == Token.PARTITION){
                    Map<SQLObject, SQLObject> lessThanMap = srStmt.getLessThanMap();
                    for (; ;) {
                        lexer.nextToken();
                        SQLExpr area = this.exprParser.expr();
                        if (lexer.token() == Token.VALUES) {
                            lexer.nextToken();
                            break;
                        }


                    }
                }


            }

        }
    }

    protected StarRocksCreateTableStatement newCreateStatement() {
        return new StarRocksCreateTableStatement();
    }

}
