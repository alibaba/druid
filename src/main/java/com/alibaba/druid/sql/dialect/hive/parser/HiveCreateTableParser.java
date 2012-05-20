package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement.PartitionedBy;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

public class HiveCreateTableParser extends SQLCreateTableParser {

    public HiveCreateTableParser(String sql){
        super(new SQLExprParser(sql));
    }

    public HiveCreateTableParser(SQLExprParser exprParser){
        super(exprParser);
    }

    protected HiveCreateTableStatement newCreateStatement() {
        return new HiveCreateTableStatement();
    }

    public SQLCreateTableStatement parseCrateTable(boolean acceptCreate) {
        HiveCreateTableStatement stmt = (HiveCreateTableStatement) super.parseCrateTable(acceptCreate);

        if (identifierEquals("PARTITIONED")) {
            lexer.nextToken();
            accept(Token.BY);
            accept(Token.LPAREN);

            PartitionedBy partitionedBy = new PartitionedBy();
            partitionedBy.setName(exprParser.name().toString());
            partitionedBy.setType(exprParser.parseDataType());

            accept(Token.RPAREN);
            
            stmt.setPartitionedBy(partitionedBy);
        }

        return stmt;
    }
}
