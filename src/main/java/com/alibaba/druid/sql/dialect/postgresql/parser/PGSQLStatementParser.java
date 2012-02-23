package com.alibaba.druid.sql.dialect.postgresql.parser;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGCurrentOfExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithQuery;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGTruncateStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class PGSQLStatementParser extends SQLStatementParser {

    public PGSQLStatementParser(String sql) throws ParserException{
        this(new PGLexer(sql));
        this.lexer.nextToken();
    }

    public PGSQLStatementParser(Lexer lexer){
        super(lexer);
    }

    public PGSelectParser createSQLSelectParser() {
        return new PGSelectParser(this.lexer);
    }
    
    public SQLUpdateStatement parseUpdateStatement() throws ParserException {
        accept(Token.UPDATE);

        PGUpdateStatement udpateStatement = new PGUpdateStatement();

        SQLTableSource tableSource = this.exprParser.createSelectParser().parseTableSource();
        udpateStatement.setTableSource(tableSource);

        accept(Token.SET);

        for (;;) {
            SQLUpdateSetItem item = new SQLUpdateSetItem();
            item.setColumn(this.exprParser.name());
            accept(Token.EQ);
            item.setValue(this.exprParser.expr());

            udpateStatement.getItems().add(item);

            if (lexer.token() == (Token.COMMA)) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            udpateStatement.setWhere(this.exprParser.expr());
        }
        
        if (lexer.token() == Token.RETURNING) {
            lexer.nextToken();
            
            for (;;) {
                udpateStatement.getReturning().add(this.exprParser.expr());
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
        }

        return udpateStatement;
    }

    public PGInsertStatement parseInsert() {
        accept(Token.INSERT);
        accept(Token.INTO);

        PGInsertStatement stmt = new PGInsertStatement();

        SQLName tableName = this.exprParser.name();
        stmt.setTableName(tableName);

        if (lexer.token() == Token.IDENTIFIER) {
            stmt.setAlias(lexer.stringVal());
            lexer.nextToken();
        }

        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            this.exprParser.exprList(stmt.getColumns());
            accept(Token.RPAREN);
        }

        if (lexer.token() == (Token.VALUES)) {
            lexer.nextToken();

            for (;;) {
                accept(Token.LPAREN);
                SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
                this.exprParser.exprList(values.getValues());
                stmt.getValuesList().add(values);
                accept(Token.RPAREN);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
        } else if (lexer.token() == (Token.SELECT)) {
            SQLQueryExpr queryExpr = (SQLQueryExpr) this.createExprParser().expr();
            stmt.setQuery(queryExpr.getSubQuery());
        }
        
        if (lexer.token() == Token.RETURNING) {
            lexer.nextToken();
            SQLExpr returning = this.exprParser.expr();
            stmt.setReturning(returning);
        }
        return stmt;
    }

    public PGDeleteStatement parseDeleteStatement() throws ParserException {
        lexer.nextToken();
        PGDeleteStatement deleteStatement = new PGDeleteStatement();

        if (lexer.token() == (Token.FROM)) {
            lexer.nextToken();
        }
        if (lexer.token() == (Token.ONLY)) {
            lexer.nextToken();
            deleteStatement.setOnly(true);
        }

        SQLName tableName = exprParser.name();

        deleteStatement.setTableName(tableName);

        if (lexer.token() == Token.USING) {
            lexer.nextToken();
            for (;;) {
                SQLName name = this.createExprParser().name();
                deleteStatement.getUsing().add(name);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();

            if (lexer.token() == Token.CURRENT) {
                lexer.nextToken();
                accept(Token.OF);
                SQLName cursorName = this.exprParser.name();
                SQLExpr where = new PGCurrentOfExpr(cursorName);
                deleteStatement.setWhere(where);
            } else {
                SQLExpr where = this.exprParser.expr();
                deleteStatement.setWhere(where);
            }
        }

        if (lexer.token() == Token.RETURNING) {
            lexer.nextToken();
            accept(Token.STAR);
            deleteStatement.setReturning(true);
        }

        return deleteStatement;
    }

    public SQLStatement parseTruncate() {
        accept(Token.TRUNCATE);

        PGTruncateStatement stmt = new PGTruncateStatement();

        if (lexer.token() == Token.TABLE) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.ONLY) {
            lexer.nextToken();
            stmt.setOnly(true);
        }

        for (;;) {
            SQLName name = this.exprParser.name();
            stmt.getTableNames().add(name);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        if (lexer.token() == Token.RESTART) {
            lexer.nextToken();
            accept(Token.IDENTITY);
            stmt.setRestartIdentity(Boolean.TRUE);
        } else if (lexer.token() == Token.SHARE) {
            lexer.nextToken();
            accept(Token.IDENTITY);
            stmt.setRestartIdentity(Boolean.FALSE);
        }

        if (lexer.token() == Token.CASCADE) {
            lexer.nextToken();
            stmt.setCascade(Boolean.TRUE);
        } else if (lexer.token() == Token.RESTRICT) {
            lexer.nextToken();
            stmt.setCascade(Boolean.FALSE);
        }

        return stmt;
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.WITH) {
            SQLStatement stmt = parseWith();
            statementList.add(stmt);
            return true;
        }

        return false;
    }
    
    public PGWithClause parseWithClause() {
        lexer.nextToken();

        PGWithClause withClause = new PGWithClause();

        if (lexer.token() == Token.RECURSIVE) {
            lexer.nextToken();
            withClause.setRecursive(true);
        }

        for (;;) {
            PGWithQuery withQuery = withQuery();
            withClause.getWithQuery().add(withQuery);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            } else {
                break;
            }
        }
        return withClause;
    }

    private PGWithQuery withQuery() {
        PGWithQuery withQuery = new PGWithQuery();
        withQuery.setName(this.createExprParser().expr());
        
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            for (;;) {
                SQLExpr expr = this.createExprParser().expr();
                withQuery.getColumns().add(expr);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else {
                    break;
                }
            }

            accept(Token.RPAREN);
        }

        accept(Token.AS);

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLStatement query;
            if (lexer.token() == Token.SELECT) {
                query = this.parseSelect();
            } else if (lexer.token() == Token.INSERT) {
                query = this.parseInsert();
            } else if (lexer.token() == Token.UPDATE) {
                query = this.parseUpdateStatement();
            } else if (lexer.token() == Token.DELETE) {
                query = this.parseDeleteStatement();
            } else {
                throw new ParserException("syntax error, support token '" + lexer.token() + "'");
            }
            withQuery.setQuery(query);
            
            accept(Token.RPAREN);
        }
        
        return withQuery;
    }
    
    public PGSelectStatement parseSelect() throws ParserException {
        return new PGSelectStatement(createSQLSelectParser().select());
    }
    
    public SQLStatement parseWith() {
        PGWithClause with = this.parseWithClause();
        if (lexer.token() == Token.INSERT) {
            PGInsertStatement stmt = this.parseInsert();
            stmt.setWith(with);
            return stmt;
        }

        if (lexer.token() == Token.SELECT) {
            PGSelectStatement stmt = this.parseSelect();
            stmt.setWith(with);
            return stmt;
        }
        throw new ParserException("TODO");
    }
}
