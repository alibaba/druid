/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.odps.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLExternalRecordFormat;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveExprParser;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsNewExpr;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsTransformExpr;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsUDTFSQLSelectItem;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;
import java.util.List;

public class OdpsExprParser extends HiveExprParser {
    public static final String[] AGGREGATE_FUNCTIONS;

    public static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = {
                "AVG",
                "COUNT",
                "LAG",
                "LEAD",
                "MAX",
                "MIN",
                "STDDEV",
                "SUM",
                "ROW_NUMBER",
                "WM_CONCAT",
                "STRAGG",
                "COLLECT_LIST",
                "COLLECT_SET"//
        };
        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    @Override
    protected SQLExpr primaryAs(SQLExpr sqlExpr) {
        Lexer.SavePoint mark = lexer.mark();
        String str = lexer.stringVal();
        lexer.nextToken();
        switch (lexer.token()) {
            case COMMA:
            case RPAREN:
            case AS:
            case EQ:
            case EQEQ:
            case LT:
            case LTEQ:
            case GT:
            case GTEQ:
            case LTGT:
            case SEMI:
                sqlExpr = new SQLIdentifierExpr(str);
                break;
            case DOT:
                sqlExpr = primaryRest(
                        new SQLIdentifierExpr(str)
                );
                break;
            default:
                lexer.reset(mark);
                break;
        }
        return sqlExpr;
    }

    @Override
    protected SQLExpr primaryIn(SQLExpr sqlExpr) {
        String str = lexer.stringVal();
        lexer.nextToken();
        switch (lexer.token()) {
            case DOT:
            case COMMA:
            case LT:
            case EQ:
            case GT:
            case RPAREN:
            case IS:
            case AS:
                sqlExpr = new SQLIdentifierExpr(str);
                break;
            default:
                break;
        }
        if (sqlExpr != null) {
            return sqlExpr;
        }

        accept(Token.LPAREN);
        SQLInListExpr in = new SQLInListExpr();
        in.setExpr(
                this.expr()
        );
        if (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            this.exprList(in.getTargetList(), in);
        }
        accept(Token.RPAREN);
        sqlExpr = in;
        return sqlExpr;
    }

    @Override
    protected SQLExpr primaryColonColon(SQLExpr sqlExpr) {
        lexer.nextToken();
        SQLExpr temp = this.primary();
        if (temp instanceof SQLArrayExpr) {
            sqlExpr = temp;
        } else {
            SQLMethodInvokeExpr method = (SQLMethodInvokeExpr) temp;
            method.setOwner(new SQLIdentifierExpr(""));
            sqlExpr = method;
        }
        return sqlExpr;
    }

    @Override
    protected void methodRestUsing(SQLMethodInvokeExpr methodInvokeExpr) {
        if (lexer.identifierEquals(FnvHash.Constants.USING)) {
            lexer.nextToken();
            SQLExpr using = this.primary();
            methodInvokeExpr.setUsing(using);
        }
    }
    protected String doRestSpecific(SQLExpr expr) {
        String name = null;
        if ((lexer.token() == Token.LITERAL_INT || lexer.token() == Token.LITERAL_FLOAT)) {
            name = lexer.numberString();
            lexer.nextToken();
        } else if (lexer.token() == Token.DOT && expr.toString().equals("odps.sql.mapper")) {
                lexer.nextToken();
                name = lexer.stringVal();
                lexer.nextToken();
        }
        return name;
    }

    @Override
    protected String nameCommon() {
        String identName = lexer.stringVal();
        lexer.nextToken();
        return identName;
    }

    @Override
    protected SQLExpr relationalRestBang(SQLExpr expr) {
        lexer.nextToken();
        return notRationalRest(expr, false);
    }

    @Override
    protected void parseDataTypeComplex(StringBuilder typeName) {
        if (lexer.token() == Token.LT && dbType == DbType.odps) {
            lexer.nextToken();
            typeName.append('<');
            for (; ; ) {
                SQLDataType itemType = this.parseDataType();
                typeName.append(itemType.toString());
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    typeName.append(", ");
                } else {
                    break;
                }
            }
            accept(Token.GT);
            typeName.append('>');
        }
    }

    @Override
    protected void parseColumnCommentLiteralCharsRest(StringBuilder stringVal) {
        for (; ; ) {
            if (lexer.token() == Token.LITERAL_ALIAS) {
                String tmp = lexer.stringVal();
                if (tmp.length() > 2 && tmp.charAt(0) == '"' && tmp.charAt(tmp.length() - 1) == '"') {
                    tmp = tmp.substring(1, tmp.length() - 1);
                }

                stringVal.append(tmp);
                lexer.nextToken();
            } else if (lexer.token() == Token.LITERAL_CHARS) {
                stringVal.append(lexer.stringVal());
                lexer.nextToken();
            } else {
                break;
            }
        }
    }

    @Override
    protected void parseAssignItemDot() {
        if (lexer.token() == Token.DOT) {
            lexer.nextToken();
        }
    }

    @Override
    protected void parseAssignItemNcToBeExecuted() {
        if (lexer.identifierEquals("NC_TO_BE_EXECUTED")) {
            lexer.nextToken(); // skip
        }
    }

    @Override
    protected boolean parseAssignItemTblProperties(SQLAssignItem item) {
        if (lexer.token() == Token.LPAREN) {
            SQLListExpr list = new SQLListExpr();
            this.exprList(list.getItems(), list);
            item.setTarget(new SQLIdentifierExpr("tblproperties"));
            item.setValue(list);
            return true;
        }
        return false;
    }

    @Override
    protected SQLExpr parseAssignItemSQLPropertyExprAndSub(SQLExpr sqlExpr) {
        if (sqlExpr instanceof SQLPropertyExpr && lexer.token() == Token.SUB) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) sqlExpr;
            String name = propertyExpr.getName() + '-';
            lexer.nextToken();
            if (lexer.token() == Token.IDENTIFIER) {
                name += lexer.stringVal();
                lexer.nextToken();
            }
            propertyExpr.setName(name);
            return this.primaryRest(propertyExpr);
        }
        return sqlExpr;
    }

    @Override
    protected SQLExpr parseAssignItemSQLPropertyExpr(SQLExpr sqlExpr) {
        if (sqlExpr instanceof SQLPropertyExpr) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) sqlExpr;

            if (identifierEquals("DATEADD")) {
                String func = lexer.stringVal();
                lexer.nextToken();
                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    accept(Token.RPAREN);
                    func += "()";
                }

                String name = propertyExpr.getName() + func;
                propertyExpr.setName(name);
            } else if (propertyExpr.getName().equalsIgnoreCase("enab") && identifierEquals("le")) {
                String name = propertyExpr.getName() + lexer.stringVal();
                lexer.nextToken();
                propertyExpr.setName(name);
            } else if (propertyExpr.getName().equalsIgnoreCase("sq") && identifierEquals("l")) {
                String name = propertyExpr.getName() + lexer.stringVal();
                lexer.nextToken();
                propertyExpr.setName(name);
            } else if (propertyExpr.getName().equalsIgnoreCase("s") && identifierEquals("ql")) {
                String name = propertyExpr.getName() + lexer.stringVal();
                lexer.nextToken();
                propertyExpr.setName(name);
                sqlExpr = this.primaryRest(propertyExpr);
            } else if (lexer.token() == Token.BY) {
                String name = propertyExpr.getName() + ' ' + lexer.stringVal();
                lexer.nextToken();
                propertyExpr.setName(name);
                sqlExpr = this.primaryRest(propertyExpr);
            }
        }
        return sqlExpr;
    }

    @Override
    protected boolean parseAssignItemSQLMethodInvokeExpr(SQLExpr sqlExpr, SQLAssignItem item) {
        if (sqlExpr instanceof SQLMethodInvokeExpr) {
            SQLMethodInvokeExpr func = (SQLMethodInvokeExpr) sqlExpr;

            SQLExpr owner = func.getOwner();
            if (owner != null) {
                item.setTarget(new SQLPropertyExpr(owner, func.getMethodName()));
            } else {
                item.setTarget(new SQLIdentifierExpr(func.getMethodName()));
            }

            SQLListExpr properties = new SQLListExpr();
            for (SQLExpr argument : func.getArguments()) {
                properties.addItem(argument);
            }

            item.setValue(properties);
            return true;
        }
        return false;
    }

    @Override
    protected void parseAssignItemEq(SQLObject parent) {
        if (parent instanceof SQLSetStatement || parent == null) {
            lexer.nextTokenForSet();
        } else {
            lexer.nextToken();
        }
    }

    @Override
    protected void parseAssignItemSQLIdentifierExprAndVariant(SQLIdentifierExpr ident) {
        if (lexer.identifierEquals(FnvHash.Constants.CLUSTER)
                && ident.nameHashCode64() == FnvHash.Constants.RUNNING
        ) {
            String str = ident.getName() + " " + lexer.stringVal();
            lexer.nextToken();
            ident.setName(str);
        } else if (lexer.token() == Token.IDENTIFIER) {
            ident.setName(ident.getName() + ' ' + lexer.stringVal());
            lexer.nextToken();
            while (lexer.token() == Token.IDENTIFIER) {
                ident.setName(ident.getName() + ' ' + lexer.stringVal());
                lexer.nextToken();
            }
        }
    }
    @Override
    protected void parseAssignItemSQLIdentifierExpr(SQLExpr sqlExpr) {
        if (sqlExpr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identExpr = (SQLIdentifierExpr) sqlExpr;
            if ((identExpr.getName().equalsIgnoreCase("et")
                    || identExpr.getName().equalsIgnoreCase("odps")
            )
                    && lexer.token() == Token.IDENTIFIER) {
                SQLExpr expr = this.primary();
                identExpr.setName(
                        identExpr.getName() + ' ' + expr.toString()
                );
            }
        }
    }

    @Override
    protected SQLExpr parseAssignItemOnLiteralFloat(SQLExpr sqlExpr) {
        while (lexer.token() == Token.LITERAL_FLOAT && lexer.numberString().startsWith(".")) {
            if (sqlExpr instanceof SQLNumberExpr) {
                String numStr = ((SQLNumberExpr) sqlExpr).getLiteral();
                numStr += lexer.numberString();
                sqlExpr = new SQLIdentifierExpr(numStr);
                lexer.nextToken();
            } else if (sqlExpr instanceof SQLIdentifierExpr) {
                String ident = ((SQLIdentifierExpr) sqlExpr).getName();
                ident += lexer.numberString();
                sqlExpr = new SQLIdentifierExpr(ident);
                lexer.nextToken();
            } else {
                break;
            }
        }
        return sqlExpr;
    }

    @Override
    protected void parseAssignItemOnComma(SQLExpr sqlExpr, SQLAssignItem item, SQLObject parent) {
        if (lexer.token() == Token.COMMA
                && parent instanceof SQLSetStatement) {
            SQLListExpr listExpr = new SQLListExpr();
            listExpr.addItem(sqlExpr);
            sqlExpr.setParent(listExpr);
            do {
                lexer.nextToken();
                if (lexer.token() == Token.SET && dbType == DbType.odps) {
                    break;
                }
                SQLExpr listItem = this.expr();
                listItem.setParent(listExpr);
                listExpr.addItem(listItem);
            }
            while (lexer.token() == Token.COMMA);
            item.setValue(listExpr);
        } else {
            item.setValue(sqlExpr);
        }
    }

    public OdpsExprParser(Lexer lexer) {
        super(lexer);
        this.dbType = DbType.odps;

        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public OdpsExprParser(String sql, SQLParserFeature... features) {
        this(new OdpsLexer(sql, features));
        this.lexer.nextToken();
    }

    protected SQLExpr parseAliasExpr(String alias) {
        String chars = alias.substring(1, alias.length() - 1);
        return new SQLCharExpr(chars);
    }

    static final long GSONBUILDER = FnvHash.fnv1a_64_lower("GSONBUILDER");

    @Override
    public SQLSelectItem parseSelectItem() {
        SQLExpr expr;
        if (lexer.token() == Token.IDENTIFIER) {
            String stringVal = lexer.stringVal();
            long hash_lower = lexer.hashLCase();

            int sourceLine = -1, sourceColumn = -1;
            if (lexer.isKeepSourceLocation()) {
                lexer.computeRowAndColumn();
                sourceLine = lexer.getPosLine();
                sourceColumn = lexer.getPosColumn();
            }

            lexer.nextTokenComma();

            if (FnvHash.Constants.DATETIME == hash_lower
                    && lexer.stringVal().charAt(0) != '`'
                    && (lexer.token() == Token.LITERAL_CHARS
                    || lexer.token() == Token.LITERAL_ALIAS)
            ) {
                String literal = lexer.stringVal();
                lexer.nextToken();

                SQLDateTimeExpr ts = new SQLDateTimeExpr(literal);
                expr = ts;
            } else if (FnvHash.Constants.DATE == hash_lower
                    && lexer.stringVal().charAt(0) != '`'
                    && (lexer.token() == Token.LITERAL_CHARS
                    || lexer.token() == Token.LITERAL_ALIAS)
            ) {
                String literal = lexer.stringVal();
                lexer.nextToken();

                SQLDateExpr d = new SQLDateExpr(literal);
                expr = d;
            } else if (FnvHash.Constants.TIMESTAMP == hash_lower
                    && lexer.stringVal().charAt(0) != '`'
                    && (lexer.token() == Token.LITERAL_CHARS
                    || lexer.token() == Token.LITERAL_ALIAS)
            ) {
                String literal = lexer.stringVal();
                lexer.nextToken();

                SQLTimestampExpr ts = new SQLTimestampExpr(literal);
                expr = ts;
            } else {
                expr = new SQLIdentifierExpr(stringVal);
                if (lexer.token() != Token.COMMA) {
                    expr = this.primaryRest(expr);
                    expr = this.exprRest(expr);
                }
            }

            if (sourceLine != -1) {
                expr.setSource(sourceLine, sourceColumn);
            }
        } else {
            expr = expr();
        }

        String alias = null;
        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                OdpsUDTFSQLSelectItem selectItem = new OdpsUDTFSQLSelectItem();

                selectItem.setExpr(expr);

                for (; ; ) {
                    alias = lexer.stringVal();
                    lexer.nextToken();

                    selectItem.getAliasList().add(alias);

                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }

                accept(Token.RPAREN);

                return selectItem;
            } else {
                alias = alias();
            }
        } else {
            alias = as();
        }

        SQLSelectItem item = new SQLSelectItem(expr, alias);

        if (lexer.hasComment() && lexer.isKeepComments()) {
            item.addAfterComment(lexer.readAndResetComments());
        }

        return item;
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        if (lexer.token() == Token.COLON) {
            lexer.nextToken();
            if (lexer.token() == Token.LITERAL_INT && expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
                Number integerValue = lexer.integerValue();
                lexer.nextToken();
                propertyExpr.setName(propertyExpr.getName() + ':' + integerValue.intValue());
                return propertyExpr;
            }
            expr = dotRest(expr);
            if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr spe = (SQLPropertyExpr) expr;
                spe.setSplitString(":");
            }
            return expr;
        }

        if (lexer.token() == Token.LBRACKET) {
            SQLArrayExpr array = new SQLArrayExpr();
            array.setExpr(expr);
            lexer.nextToken();
            this.exprList(array.getValues(), array);
            accept(Token.RBRACKET);
            return primaryRest(array);
        } else if ((lexer.token() == Token.LITERAL_CHARS || lexer.token() == Token.LITERAL_ALIAS) && expr instanceof SQLCharExpr) {
            SQLCharExpr charExpr = new SQLCharExpr(lexer.stringVal());
            lexer.nextTokenValue();
            SQLMethodInvokeExpr concat = new SQLMethodInvokeExpr("concat", null, expr, charExpr);

            while (lexer.token() == Token.LITERAL_CHARS || lexer.token() == Token.LITERAL_ALIAS) {
                charExpr = new SQLCharExpr(lexer.stringVal());
                lexer.nextToken();
                concat.addArgument(charExpr);
            }

            expr = concat;
        }

        if (lexer.token() == Token.LPAREN
                && expr instanceof SQLIdentifierExpr
                && ((SQLIdentifierExpr) expr).nameHashCode64() == FnvHash.Constants.TRANSFORM) {
            String name = lexer.stringVal();
            OdpsTransformExpr transformExpr = new OdpsTransformExpr();
            lexer.nextToken();
            List<SQLExpr> inputColumns = transformExpr.getInputColumns();
            this.exprList(inputColumns, transformExpr);
            accept(Token.RPAREN);

            if (inputColumns.size() == 2
                    && inputColumns.get(1) instanceof SQLBinaryOpExpr
                    && ((SQLBinaryOpExpr) inputColumns.get(1)).getOperator() == SQLBinaryOperator.SubGt
            ) {
                SQLMethodInvokeExpr methodInvokeExpr = new SQLMethodInvokeExpr(name);
                for (SQLExpr item : inputColumns) {
                    methodInvokeExpr.addArgument(item);
                }
                return primaryRest(methodInvokeExpr);
            }

            if (lexer.identifierEquals(FnvHash.Constants.ROW)) {
                SQLExternalRecordFormat recordFormat = this.parseRowFormat();
                transformExpr.setInputRowFormat(recordFormat);
            }

            if (lexer.token() == Token.USING || lexer.identifierEquals(FnvHash.Constants.USING)) {
                lexer.nextToken();
                transformExpr.setUsing(this.expr());
            }

            if (lexer.identifierEquals(FnvHash.Constants.RESOURCES)) {
                lexer.nextToken();
                this.exprList(transformExpr.getResources(), transformExpr);
            }

            if (lexer.token() == Token.AS) {
                lexer.nextToken();
                List<SQLColumnDefinition> outputColumns = transformExpr.getOutputColumns();

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    for (; ; ) {
                        SQLColumnDefinition column = this.parseColumn();
                        outputColumns.add(column);
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    accept(Token.RPAREN);
                } else {
                    SQLColumnDefinition column = new SQLColumnDefinition();
                    column.setName(this.name());
                    outputColumns.add(column);
                }
            }

            if (lexer.identifierEquals(FnvHash.Constants.ROW)) {
                SQLExternalRecordFormat recordFormat = this.parseRowFormat();
                transformExpr.setOutputRowFormat(recordFormat);
            }

            return transformExpr;
        }

        if (expr instanceof SQLIdentifierExpr
                && ((SQLIdentifierExpr) expr).nameHashCode64() == FnvHash.Constants.NEW) {
            SQLIdentifierExpr ident = (SQLIdentifierExpr) expr;

            OdpsNewExpr newExpr = new OdpsNewExpr();
            if (lexer.token() == Token.IDENTIFIER) { //.GSON
                Lexer.SavePoint mark = lexer.mark();

                StringBuilder methodName = new StringBuilder(lexer.stringVal());
                lexer.nextToken();
                switch (lexer.token()) {
                    case ON:
                    case WHERE:
                    case GROUP:
                    case ORDER:
                    case INNER:
                    case JOIN:
                    case FULL:
                    case OUTER:
                    case LEFT:
                    case RIGHT:
                    case LATERAL:
                    case FROM:
                    case COMMA:
                    case RPAREN:
                        return ident;
                    default:
                        break;
                }

                while (lexer.token() == Token.DOT) {
                    lexer.nextToken();
                    methodName.append('.').append(lexer.stringVal());
                    lexer.nextToken();
                }

                newExpr.setMethodName(methodName.toString());

                if (lexer.token() == Token.LT) {
                    lexer.nextToken();
                    for (; ; ) {
                        if (lexer.token() == Token.GT) {
                            break;
                        }
                        SQLDataType paramType = this.parseDataType(false);
                        paramType.setParent(newExpr);
                        newExpr.getTypeParameters().add(paramType);
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    accept(Token.GT);
                }

                if (lexer.token() == Token.LBRACKET) {
                    lexer.nextToken();
                    this.exprList(newExpr.getArguments(), newExpr);
                    accept(Token.RBRACKET);
                    if (lexer.token() == Token.LBRACKET) {
                        lexer.nextToken();
                        accept(Token.RBRACKET);
                    }
                    newExpr.setArray(true);

                    if (lexer.token() == Token.LBRACE) {
                        lexer.nextToken();
                        for (; ; ) {
                            if (lexer.token() == Token.RPAREN) {
                                break;
                            }

                            SQLExpr item = this.expr();
                            newExpr.getInitValues().add(item);
                            item.setParent(newExpr);

                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                            break;
                        }
                        accept(Token.RBRACE);
                    }
                    if (lexer.token() == Token.LBRACKET) {
                        expr = primaryRest(newExpr);
                    } else {
                        expr = newExpr;
                    }
                } else {
                    accept(Token.LPAREN);
                    this.exprList(newExpr.getArguments(), newExpr);
                    accept(Token.RPAREN);
                    expr = newExpr;
                }
            } else if (lexer.identifierEquals("java") || lexer.identifierEquals("com")) {
                SQLName name = this.name();
                StringBuilder strName = new StringBuilder();
                strName.append(ident.getName()).append(' ').append(name.toString());
                if (lexer.token() == Token.LT) {
                    lexer.nextToken();
                    for (int i = 0; lexer.token() != Token.GT; i++) {
                        if (i != 0) {
                            strName.append(", ");
                        }
                        SQLName arg = this.name();
                        strName.append(arg.toString());
                    }
                    lexer.nextToken();
                }
                ident.setName(strName.toString());
            }
        }

        if (expr == null) {
            return null;
        }

        return super.primaryRest(expr);
    }

    public SQLExpr relationalRest(SQLExpr expr) {
        if (lexer.identifierEquals("REGEXP")) {
            lexer.nextToken();
            SQLExpr rightExp = bitOr();

            rightExp = relationalRest(rightExp);

            return new SQLBinaryOpExpr(expr, SQLBinaryOperator.RegExp, rightExp, dbType);
        }

        return super.relationalRest(expr);
    }

    @Override
    public OdpsSelectParser createSelectParser() {
        return new OdpsSelectParser(this);
    }

    @Override
    protected SQLExpr relationalRestEqeq(SQLExpr expr) {
        Lexer.SavePoint mark = lexer.mark();
        lexer.nextToken();
        SQLExpr rightExp;
        try {
            if (lexer.token() == Token.SEMI) {
                lexer.reset(mark);
                return expr;
            }
            rightExp = bitOr();
        } catch (EOFParserException e) {
            throw new ParserException("EOF, " + expr + "=", e);
        }

        if (lexer.token() == Token.COLONEQ) {
            lexer.nextToken();
            SQLExpr colonExpr = expr();
            rightExp = new SQLBinaryOpExpr(rightExp, SQLBinaryOperator.Assignment, colonExpr, dbType);
        }
        return new SQLBinaryOpExpr(expr, SQLBinaryOperator.Equality, rightExp, dbType);
    }

    @Override
    protected SQLExpr methodRest(SQLExpr expr, boolean acceptLPAREN) {
        if (expr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) expr;
            long hashCode64 = identifierExpr.hashCode64();
            if (hashCode64 == FnvHash.Constants.STRUCT && acceptLPAREN) {
                SQLStructExpr struct = struct();
                if (lexer.isKeepSourceLocation()) {
                    struct.setSource(identifierExpr.getSourceLine(), identifierExpr.getSourceColumn());
                }
                return struct;
            }
        }
        return super.methodRest(expr, acceptLPAREN);
    }
}
