package com.alibaba.druid.sql.dialect.dm.parser;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.dm.ast.DMSQLSelectQueryBlock;
import com.alibaba.druid.sql.parser.*;

/**
 * @author two brother
 * @date 2021/7/22 10:52
 */
public class DMSQLSelectParser extends SQLSelectParser {
    public DMSQLSelectParser(String sql) {
        super(sql);
    }

    public DMSQLSelectParser(Lexer lexer) {
        super(lexer);
    }

    public DMSQLSelectParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public DMSQLSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
    }


    @Override
    protected void parseSelectBefore(SQLSelectQueryBlock queryBlock) {
        DMSQLSelectQueryBlock dmQueryBlock= (DMSQLSelectQueryBlock) queryBlock;
        if (lexer.token() == Token.TOP) {
            lexer.nextToken();
            if (lexer.token() == Token.LITERAL_INT) {
                dmQueryBlock.setArg0(lexer.integerValue().intValue());
                lexer.nextToken();

            }
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                if (lexer.token() == Token.LITERAL_INT) {
                    dmQueryBlock.setArg0(lexer.integerValue().intValue());
                    lexer.nextToken();
                }

            }

            if (lexer.token() == Token.PERCENT) {
                dmQueryBlock.setPERCENT(true);
                lexer.nextToken();
            }
            if (lexer.token() == Token.WITH) {
                dmQueryBlock.setWITH(true);
                lexer.nextToken();
            }
            if (lexer.token() == Token.TIES) {
                dmQueryBlock.setTIES(true);
                lexer.nextToken();
            }

        }
    }

    @Override
    protected SQLSelectQueryBlock createSqlSelectQueryBlock() {
        return new DMSQLSelectQueryBlock();
    }
}
