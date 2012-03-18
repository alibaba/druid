package com.alibaba.druid.sql.dialect.sqlserver.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.sqlserver.ast.expr.SQLServerObjectReferenceExpr;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

public class SQLServerExprParser extends SQLExprParser {

    public SQLServerExprParser(Lexer lexer){
        super(lexer);
    }

    public SQLServerExprParser(String sql) throws ParserException{
        super(new SQLServerLexer(sql));
        this.lexer.nextToken();
    }

    public SQLExpr primaryRest(SQLExpr expr) throws ParserException {
        if (lexer.token() == Token.DOTDOT) {
            expr = nameRest((SQLName) expr);
        }
        
        return super.primaryRest(expr);
    }
    
    public SQLName nameRest(SQLName name) throws ParserException {
        if (lexer.token() == Token.DOTDOT) {
            lexer.nextToken();
            String text = lexer.stringVal();
            lexer.nextToken();

            name = new SQLServerObjectReferenceExpr(name, text);
        }

        return super.nameRest(name);
    }
}
