package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;

public class SQLSelectParser extends SQLParser {

    public SQLSelectParser(String sql) {
        super(sql);
    }

    public SQLSelectParser(Lexer lexer) {
        super(lexer);
    }

    public SQLSelect select() throws ParserException {
        SQLSelect select = new SQLSelect();

        select.setQuery(query());
        select.setOrderBy(parseOrderBy());

        if (select.getOrderBy() == null) {
            select.setOrderBy(parseOrderBy());
        }

        return select;
    }

    public SQLSelectQuery queryRest(SQLSelectQuery selectQuery) throws ParserException {
        if (lexer.token() == Token.UNION) {
            lexer.nextToken();

            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            if (lexer.token() == Token.ALL) {
                union.setAll(true);
                lexer.nextToken();
            }

            SQLSelectQuery right = this.query();
            union.setRight(right);

            return union;
        }

        if (lexer.token() == Token.INTERSECT) {
            throw new ParserException("TODO");
        }

        if (lexer.token() == Token.MINUS) {
            throw new ParserException("TODO");
        }

        return selectQuery;
    }

    protected SQLSelectQuery query() throws ParserException {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select);
        }

        accept(Token.SELECT);

        SQLSelectQueryBlock queryBlock = new SQLSelectQueryBlock();

        if (lexer.token() == Token.DISTINCT) {
            queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
            lexer.nextToken();
        } else if (lexer.token() == Token.UNIQUE) {
            queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
            lexer.nextToken();
        } else if (lexer.token() == Token.ALL) {
            queryBlock.setDistionOption(SQLSetQuantifier.ALL);
            lexer.nextToken();
        }

        parseSelectList(queryBlock);

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseGroupBy(queryBlock);

        return queryRest(queryBlock);
    }

    protected void parseWhere(SQLSelectQueryBlock queryBlock) throws ParserException {
        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();

            queryBlock.setWhere(expr());
        }
    }

    protected void parseGroupBy(SQLSelectQueryBlock queryBlock) throws ParserException {
        if (lexer.token() == Token.GROUP) {
            lexer.nextToken();
            accept(Token.BY);

            SQLSelectGroupByClause groupBy = new SQLSelectGroupByClause();
            while (true) {
                groupBy.getItems().add(expr());
                if (lexer.token() != Token.COMMA) {
                    break;
                }

                lexer.nextToken();
            }

            if (lexer.token() == Token.HAVING) {
                lexer.nextToken();

                groupBy.setHaving(expr());
            }

            queryBlock.setGroupBy(groupBy);
        }
    }

    protected void parseSelectList(SQLSelectQueryBlock queryBlock) throws ParserException {
        queryBlock.getSelectList().add(new SQLSelectItem(expr(), as()));

        while (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            queryBlock.getSelectList().add(new SQLSelectItem(expr(), as()));
        }
    }

    public void parseFrom(SQLSelectQueryBlock queryBlock) throws ParserException {
        if (lexer.token() != Token.FROM) {
            return;
        }

        lexer.nextToken();

        queryBlock.setFrom(parseTableSource());
    }

    public SQLTableSource parseTableSource() throws ParserException {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            SQLTableSource tableSource;
            if (lexer.token() == Token.SELECT) {
                tableSource = new SQLSubqueryTableSource(select());
            } else if (lexer.token() == Token.LPAREN) {
                tableSource = parseTableSource();
            } else {
                throw new ParserException("TODO");
            }

            accept(Token.RPAREN);

            return parseTableSourceRest(tableSource);
        }

        if (lexer.token() == Token.SELECT) {
            throw new ParserException("TODO");
        }

        SQLExprTableSource tableReference = new SQLExprTableSource();

        parseTableSourceQueryTableExpr(tableReference);

        return parseTableSourceRest(tableReference);
    }

    private void parseTableSourceQueryTableExpr(SQLExprTableSource tableReference) throws ParserException {
        tableReference.setExpr(expr());
    }

    private SQLTableSource parseTableSourceRest(SQLTableSource tableSource) throws ParserException {
        if ((tableSource.getAlias() == null) || (tableSource.getAlias().length() == 0)) {
            if (lexer.token() != Token.LEFT && lexer.token() != Token.RIGHT && lexer.token() != Token.FULL) {
                tableSource.setAlias(as());
            }
        }

        SQLJoinTableSource.JoinType joinType = null;

        if (lexer.token() == Token.LEFT) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }

            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.LEFT_OUTER_JOIN;
        } else if (lexer.token() == Token.RIGHT) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.RIGHT_OUTER_JOIN;
        } else if (lexer.token() == Token.FULL) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.FULL_OUTER_JOIN;
        } else if (lexer.token() == Token.INNER) {
            lexer.nextToken();
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.INNER_JOIN;
        } else if (lexer.token() == Token.JOIN) {
            lexer.nextToken();
            joinType = SQLJoinTableSource.JoinType.JOIN;
        } else if (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            joinType = SQLJoinTableSource.JoinType.COMMA;
        }

        if (joinType != null) {
            SQLJoinTableSource join = new SQLJoinTableSource();
            join.setLeft(tableSource);
            join.setJoinType(joinType);
            join.setRight(parseTableSource());

            if (lexer.token() == Token.ON) {
                lexer.nextToken();
                join.setCondition(expr());
            }

            return join;
        }

        return tableSource;
    }

    public SQLExpr expr() {
        return createExprParser().expr();
    }

    protected SQLExprParser createExprParser() {
        return new SQLExprParser(lexer);
    }

    public SQLOrderBy parseOrderBy() throws ParserException {
        return createExprParser().parseOrderBy();
    }

    public void acceptKeyword(String ident) {
        if (lexer.token() == Token.IDENTIFIER && ident.equalsIgnoreCase(lexer.stringVal())) {
            lexer.nextToken();
        } else {
            setErrorEndPos(lexer.pos());
            throw new SQLParseException("syntax error, expect " + ident + ", actual " + lexer.token());
        }
    }

}
