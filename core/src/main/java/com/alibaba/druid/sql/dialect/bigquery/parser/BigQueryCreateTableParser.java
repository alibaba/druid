package com.alibaba.druid.sql.dialect.bigquery.parser;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.db2.parser.DB2ExprParser;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

public class BigQueryCreateTableParser extends SQLCreateTableParser {
    public BigQueryCreateTableParser(String sql) {
        super(new DB2ExprParser(sql));
    }

    public BigQueryCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        for (;;) {
            if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();
                accept(Token.BY);

                boolean brace = lexer.nextIf(Token.LPAREN);
                for (; ; ) {
                    SQLName name;
                    name = exprParser.name();
                    if (name instanceof SQLIdentifierExpr
                            && ((SQLIdentifierExpr) name).getName().equalsIgnoreCase("DATE")
                            && lexer.nextIf(Token.LPAREN)
                    ) {
                        name = exprParser.name();
                        accept(Token.RPAREN);
                        name.putAttribute("function", "DATE");
                    }
                    stmt.addPartitionColumn(new SQLColumnDefinition(name));
                    if (lexer.nextIf(Token.COMMA)) {
                        continue;
                    }
                    break;
                }
                if (brace) {
                    accept(Token.RPAREN);
                }
                continue;
            }

            if (lexer.nextIfIdentifier("CLUSTERED")) {
                accept(Token.BY);
                SQLSelectOrderByItem item = exprParser.parseSelectOrderByItem();
                item.setParent(stmt);
                stmt.getClusteredBy().add(item);
            }

            if (lexer.nextIfIdentifier("OPTIONS")) {
                exprParser.parseAssignItem(stmt.getTableOptions(), stmt);
                continue;
            }

            if (lexer.nextIfIdentifier("CLONE")) {
                stmt.setLike(exprParser.name());
                continue;
            }

            break;
        }
    }
}
