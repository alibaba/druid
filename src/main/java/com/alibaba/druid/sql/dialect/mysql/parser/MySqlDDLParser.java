/*
 * Copyright 2011 Alibaba Group.
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
package com.alibaba.druid.sql.dialect.mysql.parser;

import java.util.List;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCheck;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraintNull;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraintState;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleForeignKey;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePrimaryKey;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleReferencesConstaint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleTableColumn;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public abstract class MySqlDDLParser extends SQLStatementParser {
    public MySqlDDLParser(String sql) throws ParserException {
        super(sql);
    }

    public MySqlDDLParser(Lexer lexer) {
        super(lexer);
    }

    protected boolean parseConstaint(List<OracleConstraint> constaints) throws ParserException {
        String constaintName = null;
        if (lexer.token() == Token.CONSTRAINT) {
            lexer.nextToken();
            if (lexer.token() == Token.IDENTIFIER) {
                constaintName = lexer.stringVal();
                lexer.nextToken();
            }
        }

        if (lexer.token() == (Token.CHECK)) {
            lexer.nextToken();
            OracleCheck check = new OracleCheck();
            check.setName(constaintName);
            accept(Token.LPAREN);
            check.setCondition(this.exprParser.expr());
            accept(Token.RPAREN);

            check.setState(parseConstraintState());

            constaints.add(check);

            return true;
        }
        OracleConstraintNull nullable;
        if (lexer.token() == (Token.NOT)) {
            lexer.nextToken();
            accept(Token.NULL);

            nullable = new OracleConstraintNull();
            nullable.setName(constaintName);
            nullable.setNullable(false);

            nullable.setState(parseConstraintState());

            constaints.add(nullable);
            return true;
        }

        if (lexer.token() == (Token.NULL)) {
            lexer.nextToken();

            nullable = new OracleConstraintNull();
            nullable.setName(constaintName);
            nullable.setNullable(true);

            nullable.setState(parseConstraintState());

            constaints.add(nullable);
            return true;
        }

        if (lexer.token() == (Token.UNIQUE)) {
            throw new ParserException("TODO");
        }

        if (lexer.token() == (Token.REFERENCE)) {
            throw new ParserException("TODO");
        }

        if (lexer.token() == (Token.FOREIGN)) {
            lexer.nextToken();
            accept(Token.KEY);

            OracleForeignKey foreignKey = new OracleForeignKey();

            foreignKey.setName(constaintName);

            accept(Token.LPAREN);
            this.exprParser.names(foreignKey.getColumns());
            accept(Token.RPAREN);

            accept(Token.REFERENCES);
            foreignKey.setRefObject(this.exprParser.name());

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                this.exprParser.names(foreignKey.getRefColumns());
                accept(Token.RPAREN);
            }

            foreignKey.setState(parseConstraintState());

            constaints.add(foreignKey);
            return true;
        }

        if (lexer.token() == (Token.SCHEMA)) {
            throw new ParserException("TODO");
        }

        if (lexer.token() == (Token.PRIMARY)) {
            lexer.nextToken();
            accept(Token.KEY);

            OraclePrimaryKey primaryKey = new OraclePrimaryKey();
            primaryKey.setName(constaintName);

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                this.exprParser.names(primaryKey.getColumns());
                accept(Token.RPAREN);
            }

            primaryKey.setState(parseConstraintState());

            constaints.add(primaryKey);

            return true;
        }

        if (identifierEquals("Scope")) {
            lexer.nextToken();

            if (lexer.token() == (Token.IS)) {
                throw new RuntimeException("TODO");
            }
            if (lexer.token() == (Token.FOR)) {
                throw new ParserException("TODO");
            }
            throw new ParserException("TODO");
        }

        if (identifierEquals("REF")) {
            throw new ParserException("TODO");
        }

        if (lexer.token() == (Token.REFERENCES)) {
            lexer.nextToken();
            OracleReferencesConstaint ref = new OracleReferencesConstaint();
            ref.setRefObject(this.exprParser.name());

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                this.exprParser.names(ref.getRefColumns());
                accept(Token.RPAREN);
            }

            if (lexer.token() == (Token.ON)) {
                lexer.nextToken();
                accept(Token.DELETE);
            }
            constaints.add(ref);

            return true;
        }

        return false;
    }

    protected OracleConstraintState parseConstraintState() throws ParserException {
        OracleConstraintState state = null;
        while (true) {
            if (identifierEquals("DEFERRABLE")) throw new ParserException("TODO");
            if (identifierEquals("INITIALY")) throw new ParserException("TODO");
            if (identifierEquals("RELY")) throw new ParserException("TODO");
            if (identifierEquals("NORELY")) throw new ParserException("TODO");
            if (identifierEquals("USING")) throw new ParserException("TODO");
            if (identifierEquals("ENABLE")) throw new ParserException("TODO");
            if (!(identifierEquals("DISABLE"))) break;
            if (state == null) {
                state = new OracleConstraintState();
            }

            lexer.nextToken();
            if (identifierEquals("CASCADE")) {
                lexer.nextToken();
                state.getStates().add(new SQLIdentifierExpr("DISABLE CASCADE"));
            }
            state.getStates().add(new SQLIdentifierExpr("DISABLE"));
        }

        if (identifierEquals("VALIDATE")) throw new ParserException("TODO");
        if (identifierEquals("NOVALIDATE")) throw new ParserException("TODO");
        if (identifierEquals("EXCEPTIONS")) {
            throw new ParserException("TODO");
        }

        return state;
    }

    protected void parsePhysicalAttributesClause() throws ParserException {
        if (identifierEquals("PCTREE")) throw new ParserException("TODO");
        if (identifierEquals("PCTUSED")) throw new ParserException("TODO");
        if (identifierEquals("INITRANS")) throw new ParserException("TODO");
        if (identifierEquals("STORAGE")) throw new ParserException("TODO");
    }

    protected OracleTableColumn parseColumn() throws ParserException {
        OracleTableColumn column = new OracleTableColumn();
        column.setName(lexer.stringVal());
        lexer.nextToken();
        column.setDataType(this.exprParser.parseDataType());

        if (identifierEquals("SORT")) {
            lexer.nextToken();
            column.setSort(true);
        }

        if (lexer.token() == Token.DEFAULT) {
            lexer.nextToken();
            column.setDefaultValue(this.exprParser.expr());
        }

        if (identifierEquals("ENCTRYPT")) {
            throw new ParserException("TODO");
        }
        return column;
    }
}
