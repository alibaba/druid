package com.alibaba.druid.sql.dialect.impala.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLExprHint;
import com.alibaba.druid.sql.dialect.hive.parser.HiveExprParser;
import com.alibaba.druid.sql.dialect.impala.ast.ImpalaSQLPartitionValue;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

public class ImpalaExprParser extends HiveExprParser {
    public ImpalaExprParser(Lexer lexer) {
        super(lexer);
    }
    public ImpalaExprParser(String sql, SQLParserFeature... features) {
        super(new ImpalaLexer(sql, features));
        this.lexer.nextToken();
        dbType = DbType.impala;
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        if (lexer.nextIfIdentifier("ENCODING")) {
            column.setEncode(this.expr());
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.COMPRESSION)) {
            column.setCompression(this.expr());
        }

        if (lexer.nextIfIdentifier("BLOCK_SIZE")) {
            column.setBlockSize(this.integerExpr());
        }

        return super.parseColumnRest(column);
    }

    public SQLPartitionSingle parsePartition() {
        accept(Token.PARTITION);
        SQLPartitionSingle partitionDef = new SQLPartitionSingle();
        ImpalaSQLPartitionValue values = new ImpalaSQLPartitionValue();
        SQLName name;
        if (lexer.token() == Token.LITERAL_INT) {
            Number number = lexer.integerValue();
            lexer.nextToken();
            if (lexer.token() == Token.LT || lexer.token() == Token.LTEQ) {
                SQLPartitionValue.Operator leftOperator = getOperator(lexer.token());
                lexer.nextToken();
                values.setLeftOperator(leftOperator);
                values.setLeftBound(number.intValue());
                accept(Token.VALUES);
                if (lexer.token() == Token.LT || lexer.token() == Token.LTEQ) {
                    SQLPartitionValue.Operator rightOperator = getOperator(lexer.token());
                    lexer.nextToken();
                    values.setRightOperator(rightOperator);
                    values.setRightBound(lexer.integerValue().intValue());
                    accept(Token.LITERAL_INT);
                }
            }
        } else if (lexer.token() == Token.VALUES) {
            accept(Token.VALUES);
            values.setRightOperator(getOperator(lexer.token()));
            lexer.nextToken();
            values.setRightBound(lexer.integerValue().intValue());
            accept(Token.LITERAL_INT);
        } else if (lexer.identifierEquals(FnvHash.Constants.VALUE)) {
            acceptIdentifier("VALUE");
            accept(Token.EQ);
            values.setOperator(SQLPartitionValue.Operator.Equal);
            if (lexer.nextIf(Token.LPAREN)) {
                // for multiple values
                for (; ; ) {
                    if (lexer.token() == Token.LITERAL_INT) {
                        values.addItem(new SQLIntegerExpr(lexer.integerValue().intValue()));
                        lexer.nextToken();
                    } else if (lexer.token() == Token.LITERAL_CHARS) {
                        values.addItem(new SQLCharExpr(lexer.stringVal()));
                        lexer.nextToken();
                    }
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            } else {
                // for single specific value
                SQLCharExpr charExpr = new SQLCharExpr(lexer.stringVal());
                values.addItem(charExpr);
                lexer.nextToken();
            }
        }
        partitionDef.setValues(values);
        name = new SQLIdentifierExpr(values.constructPartitionName());
        partitionDef.setName(name);
        return partitionDef;
    }

    private SQLPartitionValue.Operator getOperator(Token token) {
        switch (token) {
            case LT:
                return SQLPartitionValue.Operator.LessThan;
            case LTEQ:
                return SQLPartitionValue.Operator.LessThanEqual;
            default:
                return null;
        }
    }

    @Override
    public void parseHints(List hints) {
        if (lexer.token() == Token.LBRACKET) {
            lexer.nextToken();
            hints.add(new SQLExprHint(expr()));
            accept(Token.RBRACKET);
        } else {
            super.parseHints(hints);
        }

    }
}
