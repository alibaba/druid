package com.alibaba.druid.sql.dialect.teradata.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.dialect.teradata.ast.TDDateDataType;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class TDExprParser extends SQLExprParser {
    public TDExprParser(String sql, SQLParserFeature... features) {
        super(new TDLexer(sql, features));
        lexer.nextToken();
        dbType = DbType.teradata;
    }

    public TDExprParser(Lexer lexer) {
        super(lexer);
        lexer.nextToken();
        this.dbType = DbType.teradata;
    }

    @Override
    protected SQLDataType parseDataTypeDate(StringBuilder typeName, int sourceLine, int sourceColumn) {
        TDDateDataType dataType = new TDDateDataType(typeName.toString());
        dataType.setDbType(dbType);
        dataType.setSource(sourceLine, sourceColumn);
        if (lexer.nextIf(Token.FORMAT) || lexer.nextIfIdentifier(FnvHash.Constants.FORMAT)) {
            dataType.setFormat(expr());
        }
        return dataType;
    }

    @Override
    public SQLPrimaryKey parsePrimaryKey() {
        accept(Token.PRIMARY);
        accept(Token.INDEX);
        SQLPrimaryKeyImpl pk = new SQLPrimaryKeyImpl();
        accept(Token.LPAREN);
        orderBy(pk.getColumns(), pk);
        accept(Token.RPAREN);
        parsePrimaryKeyRest(pk);
        return pk;
    }
}
