package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement.PartitionedBy;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.Token;

public class HiveCreateTableParser extends SQLCreateTableParser {

    public HiveCreateTableParser(String sql){
        super(new HiveLexer(sql));
        this.lexer.nextToken();
    }

    public HiveCreateTableParser(Lexer lexer){
        super(lexer);
    }

    protected HiveCreateTableStatement newCreateStatement() {
        return new HiveCreateTableStatement();
    }

    public SQLCreateTableStatement parseCrateTable(boolean acceptCreate) {
        HiveCreateTableStatement stmt = (HiveCreateTableStatement) super.parseCrateTable(acceptCreate);

        if (lexer.token() == Token.PARTITIONED) {
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
