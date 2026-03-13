package com.alibaba.druid.sql.dialect.dm.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.dm.ast.stmt.*;
import com.alibaba.druid.sql.parser.*;

import java.util.List;

public class DmStatementParser extends SQLStatementParser {
    public DmStatementParser(DmExprParser parser) {
        super(parser);
    }

    public DmStatementParser(String sql) {
        super(new DmExprParser(sql));
    }

    public DmStatementParser(String sql, SQLParserFeature... features) {
        super(new DmExprParser(sql, features));
    }

    public DmStatementParser(Lexer lexer) {
        super(new DmExprParser(lexer));
    }

    public DmSelectParser createSQLSelectParser() {
        return new DmSelectParser(this.exprParser, selectListCache);
    }

    public SQLUpdateStatement parseUpdateStatement() {
        accept(Token.UPDATE);

        DmUpdateStatement updateStatement = new DmUpdateStatement();

        SQLSelectParser selectParser = this.exprParser.createSelectParser();
        SQLTableSource tableSource = selectParser.parseTableSource();
        updateStatement.setTableSource(tableSource);

        parseUpdateSet(updateStatement);

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            SQLTableSource from = selectParser.parseTableSource();
            updateStatement.setFrom(from);
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            updateStatement.setWhere(this.exprParser.expr());
        }

        if (lexer.token() == Token.RETURNING) {
            lexer.nextToken();

            for (;;) {
                updateStatement.getReturning().add(this.exprParser.expr());
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
        }

        return updateStatement;
    }

    public DmInsertStatement parseInsert() {
        DmInsertStatement stmt = new DmInsertStatement();

        if (lexer.token() == Token.INSERT) {
            lexer.nextToken();
            accept(Token.INTO);

            SQLName tableName = this.exprParser.name();
            stmt.setTableName(tableName);

            if (lexer.token() == Token.AS) {
                lexer.nextToken();
                stmt.setAlias(lexer.stringVal());
                lexer.nextToken();
            } else if (lexer.token() == Token.IDENTIFIER) {
                stmt.setAlias(lexer.stringVal());
                lexer.nextToken();
            }
        }

        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            this.exprParser.exprList(stmt.getColumns(), stmt);
            accept(Token.RPAREN);
        }

        if (lexer.token() == (Token.VALUES)) {
            lexer.nextToken();

            for (;;) {
                accept(Token.LPAREN);
                SQLInsertStatement.ValuesClause valuesClause = new SQLInsertStatement.ValuesClause();
                this.exprParser.exprList(valuesClause.getValues(), valuesClause);
                stmt.addValueCause(valuesClause);

                accept(Token.RPAREN);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
        } else if (lexer.token() == (Token.SELECT)) {
            SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
            stmt.setQuery(queryExpr.getSubQuery());
        }

        return stmt;
    }

    public DmDeleteStatement parseDeleteStatement() {
        lexer.nextToken();
        DmDeleteStatement deleteStatement = new DmDeleteStatement();

        if (lexer.token() == (Token.FROM)) {
            lexer.nextToken();
        }

        SQLName tableName = exprParser.name();
        deleteStatement.setTableName(tableName);

        if (lexer.token() == Token.AS) {
            accept(Token.AS);
        }
        if (lexer.token() == Token.IDENTIFIER) {
            deleteStatement.setAlias(lexer.stringVal());
            lexer.nextToken();
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();

            if (lexer.token() == Token.CURRENT) {
                lexer.nextToken();
                accept(Token.OF);
                SQLName cursorName = this.exprParser.name();
                SQLExpr where = new SQLCurrentOfCursorExpr(cursorName);
                deleteStatement.setWhere(where);
            } else {
                SQLExpr where = this.exprParser.expr();
                deleteStatement.setWhere(where);
            }
        }

        return deleteStatement;
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        switch (lexer.token()) {
            case BEGIN:
            case START: {
                SQLStatement stmt = parseBlock();
                statementList.add(stmt);
                return true;
            }
            case WITH:
                statementList.add(parseWith());
                return true;
            default:
                break;
        }
        return false;
    }

    public DmSelectStatement parseSelect() {
        DmSelectParser selectParser = createSQLSelectParser();
        SQLSelect select = selectParser.select();
        return new DmSelectStatement(select);
    }

    public SQLStatement parseWith() {
        SQLWithSubqueryClause with = this.parseWithQuery();

        if (lexer.token() == Token.INSERT) {
            DmInsertStatement stmt = this.parseInsert();
            stmt.setWith(with);
            return stmt;
        }

        if (lexer.token() == Token.SELECT) {
            DmSelectStatement stmt = this.parseSelect();
            stmt.getSelect().setWithSubQuery(with);
            return stmt;
        }

        if (lexer.token() == Token.DELETE) {
            DmDeleteStatement stmt = this.parseDeleteStatement();
            stmt.setWith(with);
            return stmt;
        }

        if (lexer.token() == Token.UPDATE) {
            DmUpdateStatement stmt = (DmUpdateStatement) this.parseUpdateStatement();
            stmt.setWith(with);
            return stmt;
        }

        throw new ParserException("TODO. " + lexer.info());
    }

    protected SQLAlterTableAlterColumn parseAlterColumn() {
        if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
        }

        SQLColumnDefinition column = this.exprParser.parseColumn();

        SQLAlterTableAlterColumn alterColumn = new SQLAlterTableAlterColumn();
        alterColumn.setColumn(column);

        if (column.getDataType() == null && column.getConstraints().isEmpty()) {
            if (lexer.token() == Token.SET) {
                lexer.nextToken();
                if (lexer.token() == Token.NOT) {
                    lexer.nextToken();
                    accept(Token.NULL);
                    alterColumn.setSetNotNull(true);
                } else {
                    accept(Token.DEFAULT);
                    SQLExpr defaultValue = this.exprParser.expr();
                    alterColumn.setSetDefault(defaultValue);
                }
            } else if (lexer.token() == Token.DROP) {
                lexer.nextToken();
                if (lexer.token() == Token.NOT) {
                    lexer.nextToken();
                    accept(Token.NULL);
                    alterColumn.setDropNotNull(true);
                } else {
                    accept(Token.DEFAULT);
                    alterColumn.setDropDefault(true);
                }
            }
        }
        return alterColumn;
    }

    public SQLStatement parseShow() {
        accept(Token.SHOW);
        SQLShowTablesStatement stmt = new SQLShowTablesStatement();
        stmt.setDatabase(this.exprParser.name());
        return stmt;
    }

    @Override
    public SQLStatement parseCommit() {
        SQLCommitStatement stmt = new SQLCommitStatement();
        stmt.setDbType(this.dbType);
        lexer.nextToken();
        return stmt;
    }

    public SQLCreateIndexStatement parseCreateIndex() {
        accept(Token.CREATE);
        SQLCreateIndexStatement stmt = new SQLCreateIndexStatement(getDbType());
        if (lexer.token() == Token.UNIQUE) {
            lexer.nextToken();
            if (lexer.identifierEquals("CLUSTERED")) {
                lexer.nextToken();
                stmt.setType("UNIQUE CLUSTERED");
            } else {
                stmt.setType("UNIQUE");
            }
        } else if (lexer.identifierEquals("FULLTEXT")) {
            stmt.setType("FULLTEXT");
            lexer.nextToken();
        }

        accept(Token.INDEX);

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        if (lexer.token() != Token.ON) {
            stmt.setName(this.exprParser.name());
        }

        accept(Token.ON);

        stmt.setTable(this.exprParser.name());

        if (lexer.token() == Token.USING) {
            lexer.nextToken();
            String using = lexer.stringVal();
            accept(Token.IDENTIFIER);
            stmt.setUsing(using);
        }

        accept(Token.LPAREN);

        for (;;) {
            SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
            item.setParent(stmt);
            stmt.addItem(item);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);

        if (lexer.token() == Token.TABLESPACE) {
            lexer.nextToken();
            SQLName tablespace = this.exprParser.name();
            stmt.setTablespace(tablespace);
        }

        return stmt;
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new DmCreateTableParser(this.exprParser);
    }
}
