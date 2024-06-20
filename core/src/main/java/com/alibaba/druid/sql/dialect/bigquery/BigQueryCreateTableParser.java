package com.alibaba.druid.sql.dialect.bigquery;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
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
        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            accept(Token.BY);

            for (;;) {
                SQLName name = exprParser.name();
                stmt.addPartitionColumn(new SQLColumnDefinition(name));
                if (lexer.nextIf(Token.COMMA)) {
                    continue;
                }
                break;
            }

            if (lexer.nextIfIdentifier("OPTIONS")) {
                exprParser.parseAssignItem(stmt.getTableOptions(), stmt);
            }
        }
    }
}
