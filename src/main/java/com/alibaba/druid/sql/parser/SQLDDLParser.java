package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.NotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLTableConstaint;

public class SQLDDLParser extends SQLStatementParser {
    public SQLDDLParser(String sql) {
        super(sql);
    }

    public SQLDDLParser(Lexer lexer) {
        super(lexer);
    }

    protected SQLColumnDefinition parseColumn() {
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setName(this.exprParser.name());
        column.setDataType(this.exprParser.parseDataType());

        return parseColumnRest(column);
    }

    protected SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        if (lexer.token() == Token.DEFAULT) {
            lexer.nextToken();
            column.setDefaultExpr(this.exprParser.expr());
            return parseColumnRest(column);
        }

        if (lexer.token() == Token.NOT) {
            lexer.nextToken();
            accept(Token.NULL);
            column.getConstaints().add(new NotNullConstraint());
            return parseColumnRest(column);
        }

        return column;
    }

    protected SQLTableConstaint parseConstraint() {
        SQLName name = null;
        if (lexer.token() == Token.CONSTRAINT) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.IDENTIFIER) {
            name = this.exprParser.name();
        }

        if (lexer.token() == Token.PRIMARY) {
            lexer.nextToken();
            accept(Token.KEY);

            throw new ParserException("TODO");
        }
        
        if (name != null) {
            
        }

        throw new ParserException("TODO");
    }
}
