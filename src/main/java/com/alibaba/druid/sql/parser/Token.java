/*
 * Copyright 2011 Alibaba Group. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.alibaba.druid.sql.parser;

public enum Token {
    FOR("FOR"), INDEX("INDEX"), PRIMARY("PRIMARY"), KEY("KEY"), DEFAULT("DEFAULT"), CONSTRAINT("CONSTRAINT"),
    CHECK("CHECK"), VIEW("VIEW"), CREATE("CREATE"), ALTER("ALTER"), DROP("DROP"), TABLE("TABLE"), UPDATE("UPDATE"),
    SET("SET"), SELECT("SELECT"), FROM("FROM"), WHERE("WHERE"), ORDER("ORDER"), BY("BY"), GROUP("GROUP"),
    HAVING("HAVING"), INSERT("INSERT"), INTO("INTO"), NULL("NULL"), NOT("NOT"), AS("AS"), DELETE("DELETE"),
    DISTINCT("DISTINCT"), UNIQUE("UNIQUE"), FOREIGN("FOREIGN"), REFERENCE("REFERENCE"), REFERENCES("REFERENCES"),
    ALL("ALL"), UNION("UNION"), INTERSECT("INTERSECT"), MINUS("MINUS"), INNER("INNER"), LEFT("LEFT"), RIGHT("RIGHT"),
    FULL("FULL"), OUTER("OUTER"), JOIN("JOIN"), ON("ON"), SCHEMA("SCHEMA"), CAST("CAST"),

    AND("AND"), OR("OR"), XOR("XOR"), CASE("CASE"), WHEN("WHEN"), THEN("THEN"), ELSE("ELSE"), END("END"),
    EXISTS("EXISTS"), IN("IN"),

    NEW("NEW"), ASC("ASC"), DESC("DESC"), IS("IS"), LIKE("LIKE"), ESCAPE("ESCAPE"), BETWEEN("BETWEEN"),
    VALUES("VALUES"), INTERVAL("INTERVAL"),

    LOCK("LOCK"), SOME("SOME"), ANY("ANY"),

    EOF,
    ERROR,
    IDENTIFIER,
    HINT,
    // QS_TODO add support in Lexer
    SYS_VAR,
    USR_VAR,
    /** number composed purely of digit */
    LITERAL_NUM_PURE_DIGIT,
    /** number composed of digit mixed with <code>.</code> or <code>e</code> */
    LITERAL_NUM_MIX_DIGIT,
    LITERAL_HEX,
    // QS_TODO add syntax support
    LITERAL_BIT,
    LITERAL_CHARS,
    LITERAL_NCHARS,
    // QS_TODO remove alias token
    LITERAL_ALIAS,

    LPAREN("("), RPAREN(")"), LBRACE("{"), RBRACE("}"), LBRACKET("["), RBRACKET("]"), SEMI(";"), COMMA(","), DOT("."),
    EQ("="), GT(">"), LT("<"), BANG("!"), TILDE("~"), QUES("?"), COLON(":"), COLONEQ(":="), EQEQ("=="), LTEQ("<="),
    LTEQGT("<=>"), LTGT("<>"), GTEQ(">="), BANGEQ("!="), BANGGT("!>"), BANGLT("!<"), AMPAMP("&&"), BARBAR("||"),
    PLUS("+"), SUB("-"), STAR("*"), SLASH("/"), AMP("&"), BAR("|"), CARET("^"), PERCENT("%"), LTLT("<<"), GTGT(">>"),
    MONKEYS_AT("@");

    public final String name;

    Token(){
        this(null);
    }

    Token(String name){
        this.name = name;
    }
}
