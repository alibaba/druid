package com.alibaba.druid.sql.dialect.postgresql.parser;

import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGAggregateExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGOrderBy;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGAnalytic;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

public class PGExprParser extends SQLExprParser{
    public PGExprParser(String sql) throws ParserException{
        super(sql);
    }

    public PGExprParser(Lexer lexer){
        super(lexer);
    }
    
    protected SQLAggregateExpr parseAggregateExpr(String method_name) throws ParserException {
        PGAggregateExpr aggregateExpr;
        if (lexer.token() == Token.ALL) {
            aggregateExpr = new PGAggregateExpr(method_name, 1);
            lexer.nextToken();
        } else if (lexer.token() == Token.DISTINCT) {
            aggregateExpr = new PGAggregateExpr(method_name, 0);
            lexer.nextToken();
        } else {
            aggregateExpr = new PGAggregateExpr(method_name, 1);
        }

        exprList(aggregateExpr.getArguments());

        accept(Token.RPAREN);
        
        if (lexer.token() == Token.OVER) {
        	lexer.nextToken();
        	PGAnalytic over = new PGAnalytic();
        	accept(Token.LPAREN);
        	
            if (identifierEquals("PARTITION")) {
                lexer.nextToken();
                accept(Token.BY);

                if (lexer.token() == (Token.LPAREN)) {
                    lexer.nextToken();
                    exprList(over.getPartitionBy());
                    accept(Token.RPAREN);
                } else {
                    exprList(over.getPartitionBy());
                }
            }


            over.setOrderBy(parseOrderBy());
            
            if (over.getOrderBy() != null) {
            	//TODO window
            }
        	
        	accept(Token.RPAREN);
        	aggregateExpr.setOver(over);

        }
        
        return aggregateExpr;
    }
    
    
    @Override
    public PGOrderBy parseOrderBy() {
        if (lexer.token() == (Token.ORDER)) {
        	PGOrderBy orderBy = new PGOrderBy();
            lexer.nextToken();

            if (identifierEquals("SIBLINGS")) {
                lexer.nextToken();
                orderBy.setSibings(true);
            }

            accept(Token.BY);

            orderBy.getItems().add(parseSelectOrderByItem());

            while (lexer.token() == (Token.COMMA)) {
                lexer.nextToken();
                orderBy.getItems().add(parseSelectOrderByItem());
            }

            return orderBy;
        }

        return null;
    }
}
