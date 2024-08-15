package com.alibaba.druid.sql.dialect.redshift.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import com.alibaba.druid.sql.dialect.redshift.stmt.RedshiftColumnEncode;
import com.alibaba.druid.sql.dialect.redshift.stmt.RedshiftTop;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

public class RedshiftExprParser
        extends PGExprParser {
    public RedshiftExprParser(String sql, SQLParserFeature... features) {
        super(new RedshiftLexer(sql, features));
        lexer.nextToken();
        dbType = DbType.redshift;
    }

    public RedshiftExprParser(Lexer lexer) {
        super(lexer);
        dbType = DbType.redshift;
    }

    public RedshiftTop parseTop() {
        if (lexer.token() == Token.TOP) {
            RedshiftTop top = new RedshiftTop();
            lexer.computeRowAndColumn(top);

            lexer.nextToken();

            if (lexer.token() == Token.LITERAL_INT) {
                top.setExpr(lexer.integerValue().intValue());
                lexer.nextToken();
            } else {
                top.setExpr(primary());
            }

            return top;
        }

        return null;
    }

    @Override
    protected SQLColumnDefinition parseColumnSpecific(SQLColumnDefinition column) {
        switch (lexer.token()) {
            case ENCODE: {
                lexer.nextToken();
                SQLExpr codecExpr = expr();
                RedshiftColumnEncode sqlColumnEncode = new RedshiftColumnEncode();
                sqlColumnEncode.setExpr(codecExpr);
                column.addConstraint(sqlColumnEncode);
                return parseColumnRest(column);
            }
            default:
                return column;
        }
    }
}
