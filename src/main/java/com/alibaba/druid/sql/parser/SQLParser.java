package com.alibaba.druid.sql.parser;

public class SQLParser {
    protected final Lexer lexer;

    public SQLParser(String sql) {
        this(new Lexer(sql));
        this.lexer.nextToken();
    }

    public SQLParser(Lexer lexer) {
        this.lexer = lexer;
    }

    protected boolean identifierEquals(String text) {
        return lexer.token() == Token.IDENTIFIER && lexer.stringVal().equalsIgnoreCase(text);
    }

    protected void acceptIdentifier(String text) {
        if (identifierEquals(text)) {
            lexer.nextToken();
        } else {
            setErrorEndPos(lexer.pos());
            throw new SQLParseException("syntax error, expect " + text + ", actual " + lexer.token());
        }
    }

    protected final String as() throws ParserException {
        String rtnValue = null;

        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            // QS_TODO remove alias token
            if (lexer.token() == Token.LITERAL_ALIAS) {
                rtnValue = "'" + lexer.stringVal() + "'";
                lexer.nextToken();
                return rtnValue;
            }

            if (lexer.token() == Token.IDENTIFIER) {
                rtnValue = lexer.stringVal();
                lexer.nextToken();
                return rtnValue;
            }

            throw new ParserException("Error", 0, 0);
        }

        if (lexer.token() == Token.LITERAL_ALIAS) {
            rtnValue = "'" + lexer.stringVal() + "'";
            lexer.nextToken();
        } else if (lexer.token() == Token.IDENTIFIER) {
            rtnValue = "'" + lexer.stringVal() + "'";
            lexer.nextToken();
        }
        return rtnValue;
    }

    public void accept(Token token) {
        if (lexer.token() == token) {
            lexer.nextToken();
        } else {
            setErrorEndPos(lexer.pos());
            throw new SQLParseException("syntax error, expect " + token + ", actual " + lexer.token());
        }
    }

    private int errorEndPos = -1;

    protected void setErrorEndPos(int errPos) {
        if (errPos > errorEndPos) errorEndPos = errPos;
    }
}
