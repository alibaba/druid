/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.parser;

/**
 * 
 * SQL Token 
 * @author wenshao 2011-5-18 下午05:16:49
 * @formatter:off
 */
public enum Token {
    FOR("FOR"), 
    IF("IF"), 
    INDEX("INDEX"), 
    PRIMARY("PRIMARY"), 
    KEY("KEY"), 
    DEFAULT("DEFAULT"), 
    CONSTRAINT("CONSTRAINT"), 
    CHECK("CHECK"), 
    VIEW("VIEW"), 
    CREATE("CREATE"),
    ALTER("ALTER"), 
    DROP("DROP"), 
    TABLE("TABLE"), 
    UPDATE("UPDATE"), 
    SET("SET"), 
    SELECT("SELECT"), 
    FROM("FROM"), 
    WHERE("WHERE"), 
    ORDER("ORDER"), 
    BY("BY"),
    GROUP("GROUP"), 
    HAVING("HAVING"), 
    INSERT("INSERT"), 
    INTO("INTO"), 
    NULL("NULL"), 
    NOT("NOT"), 
    AS("AS"), 
    DELETE("DELETE"), 
    DISTINCT("DISTINCT"),
    UNIQUE("UNIQUE"), 
    FOREIGN("FOREIGN"), 
    REFERENCES("REFERENCES"), 
    ALL("ALL"), 
    UNION("UNION"), 
    INTERSECT("INTERSECT"), 
    MINUS("MINUS"),
    INNER("INNER"), 
    LEFT("LEFT"), 
    RIGHT("RIGHT"), 
    FULL("FULL"), 
    OUTER("OUTER"), 
    JOIN("JOIN"), 
    ON("ON"), 
    SCHEMA("SCHEMA"), 
    CAST("CAST"),
    COLUMN("COLUMN"),
    USE("USE"),
    DATABASE("DATABASE"),
    TO("TO"),

    AND("AND"), 
    OR("OR"), 
    XOR("XOR"), 
    CASE("CASE"), 
    WHEN("WHEN"), 
    THEN("THEN"), 
    ELSE("ELSE"), 
    END("END"), 
    EXISTS("EXISTS"), 
    IN("IN"),

    NEW("NEW"), 
    ASC("ASC"), 
    DESC("DESC"), 
    IS("IS"), 
    LIKE("LIKE"), 
    ESCAPE("ESCAPE"), 
    BETWEEN("BETWEEN"), 
    VALUES("VALUES"), 
    INTERVAL("INTERVAL"),

    LOCK("LOCK"), 
    SOME("SOME"), 
    ANY("ANY"),
    TRUNCATE("TRUNCATE"),

    // mysql
    TRUE("TRUE"), 
    FALSE("FALSE"),
    LIMIT("LIMIT"),
    KILL("KILL"),
    IDENTIFIED("IDENTIFIED"),
    PASSWORD("PASSWORD"),
    DUAL("DUAL"),
    BINARY("BINARY"),
    SHOW("SHOW"),
    REPLACE("REPLACE"),
    
    //postgresql
    WINDOW("WINDOW"),
    OFFSET("OFFSET"),
    ROW("ROW"),
    ROWS("ROWS"),
    ONLY("ONLY"),
    FIRST("FIRST"),
    NEXT("NEXT"),
    FETCH("FETCH"),
    OF("OF"),
    SHARE("SHARE"),
    NOWAIT("NOWAIT"),
    RECURSIVE("RECURSIVE"),
    TEMPORARY("TEMPORARY"),
    TEMP("TEMP"),
    UNLOGGED("UNLOGGED"),
    RESTART("RESTART"),
    IDENTITY("IDENTITY"),
    CONTINUE("CONTINUE"),
    CASCADE("CASCADE"),
    RESTRICT("RESTRICT"),
    USING("USING"),
    CURRENT("CURRENT"),
    RETURNING("RETURNING"),
    COMMENT("COMMENT"),
    OVER("OVER"),
    
    // oracle
    START("START"),
    PRIOR("PRIOR"),
    CONNECT("CONNECT"),
    WITH("WITH"),
    EXTRACT("EXTRACT"),
    CURSOR("CURSOR"),
    MODEL("MODEL"),
    MERGE("MERGE"),
    MATCHED("MATCHED"),
    ERRORS("ERRORS"),
    REJECT("REJECT"),
    UNLIMITED("UNLIMITED"),
    BEGIN("BEGIN"),
    EXCLUSIVE("EXCLUSIVE"),
    MODE("MODE"),
    WAIT("WAIT"),
    ADVISE("ADVISE"),
    SESSION("SESSION"),
    PROCEDURE("PROCEDURE"),
    SYSDATE("SYSDATE"),
    DECLARE("DECLARE"),
    EXCEPTION("EXCEPTION"),
    GRANT("GRANT"),
    LOOP("LOOP"),
    GOTO("GOTO"),
    COMMIT("COMMIT"),
    SAVEPOINT("SAVEPOINT"),
    CROSS("CROSS"),
    
    // transact-sql
    TOP("TOP"),
    
    // hive

    EOF, 
    ERROR,
    IDENTIFIER,
    HINT,
    VARIANT,
    LITERAL_INT,
    LITERAL_FLOAT,
    LITERAL_HEX,
    LITERAL_CHARS,
    LITERAL_NCHARS,
    
    LITERAL_ALIAS,
    LINE_COMMENT,
    MULTI_LINE_COMMENT,
    
    // Oracle
    BINARY_FLOAT,
    BINARY_DOUBLE,

    LPAREN("("), 
    RPAREN(")"), 
    LBRACE("{"), 
    RBRACE("}"), 
    LBRACKET("["), 
    RBRACKET("]"), 
    SEMI(";"), 
    COMMA(","), 
    DOT("."), 
    DOTDOT(".."), 
    DOTDOTDOT("..,"), 
    EQ("="), 
    GT(">"), 
    LT("<"), 
    BANG("!"),
    TILDE("~"), 
    QUES("?"), 
    COLON(":"), 
    COLONCOLON(":"), 
    COLONEQ(":="), 
    EQEQ("=="), 
    LTEQ("<="), 
    LTEQGT("<=>"), 
    LTGT("<>"), 
    GTEQ(">="), 
    BANGEQ("!="), 
    BANGGT("!>"), 
    BANGLT("!<"),
    AMPAMP("&&"), 
    BARBAR("||"), 
    PLUS("+"), 
    SUB("-"), 
    STAR("*"), 
    SLASH("/"), 
    AMP("&"), 
    BAR("|"), 
    CARET("^"), 
    PERCENT("%"), 
    LTLT("<<"), 
    GTGT(">>"),
    MONKEYS_AT("@");

    public final String name;

    Token(){
        this(null);
    }

    Token(String name){
        this.name = name;
    }
}
