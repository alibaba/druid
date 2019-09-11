/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
 * @author wenshao [szujobs@hotmail.com]
 */
public enum Token {
    SELECT("SELECT"), 
    DELETE("DELETE"), 
    INSERT("INSERT"), 
    UPDATE("UPDATE"), 
    
    FROM("FROM"), 
    HAVING("HAVING"), 
    WHERE("WHERE"), 
    ORDER("ORDER"), 
    BY("BY"),
    GROUP("GROUP"), 
    INTO("INTO"), 
    AS("AS"), 
    
    CREATE("CREATE"),
    ALTER("ALTER"), 
    DROP("DROP"), 
    SET("SET"), 
   
    NULL("NULL"), 
    NOT("NOT"), 
    DISTINCT("DISTINCT"),

    TABLE("TABLE"), 
    TABLESPACE("TABLESPACE"), 
    VIEW("VIEW"), 
    SEQUENCE("SEQUENCE"), 
    TRIGGER("TRIGGER"), 
    USER("USER"), 
    INDEX("INDEX"), 
    SESSION("SESSION"),
    PROCEDURE("PROCEDURE"),
    FUNCTION("FUNCTION"),
    
    PRIMARY("PRIMARY"), 
    KEY("KEY"), 
    DEFAULT("DEFAULT"), 
    CONSTRAINT("CONSTRAINT"), 
    CHECK("CHECK"), 
    UNIQUE("UNIQUE"), 
    FOREIGN("FOREIGN"), 
    REFERENCES("REFERENCES"), 
    
    EXPLAIN("EXPLAIN"), 
    FOR("FOR"), 
    IF("IF"),
    SORT("SORT"),
   
   
    ALL("ALL"), 
    UNION("UNION"), 
    EXCEPT("EXCEPT"), 
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
    ELSIF("ELSIF"),
    END("END"), 
    EXISTS("EXISTS"), 
    IN("IN"),
    CONTAINS("CONTAINS"),
    RLIKE("RLIKE"),
    FULLTEXT("FULLTEXT"),

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

    RETURN("RETURN"),

    // mysql
    TRUE("TRUE"), 
    FALSE("FALSE"),
    LIMIT("LIMIT"),
    KILL("KILL"),
    IDENTIFIED("IDENTIFIED"),
    PASSWORD("PASSWORD"),
    ALGORITHM("ALGORITHM"),
    DUAL("DUAL"),
    BINARY("BINARY"),
    SHOW("SHOW"),
    REPLACE("REPLACE"),

    BITS,

    // MySql procedure add by zz
    WHILE("WHILE"),
    DO("DO"),
    LEAVE("LEAVE"),
    ITERATE("ITERATE"),
    REPEAT("REPEAT"),
    UNTIL("UNTIL"),
    OPEN("OPEN"),
    CLOSE("CLOSE"),
    OUT("OUT"),
    INOUT("INOUT"),
    EXIT("EXIT"),
    UNDO("UNDO"),
    SQLSTATE("SQLSTATE"),
    CONDITION("CONDITION"),
    DIV("DIV"),
    
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
    TYPE("TYPE"),
    ILIKE("ILIKE"),

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
    SYSDATE("SYSDATE"),
    DECLARE("DECLARE"),
    EXCEPTION("EXCEPTION"),
    GRANT("GRANT"),
    REVOKE("REVOKE"),
    LOOP("LOOP"),
    GOTO("GOTO"),
    COMMIT("COMMIT"),
    SAVEPOINT("SAVEPOINT"),
    CROSS("CROSS"),
    
    PCTFREE("PCTFREE"),
    INITRANS("INITRANS"),
    MAXTRANS("MAXTRANS"),
    INITIALLY("INITIALLY"),
    ENABLE("ENABLE"),
    DISABLE("DISABLE"),
    SEGMENT("SEGMENT"),
    CREATION("CREATION"),
    IMMEDIATE("IMMEDIATE"),
    DEFERRED("DEFERRED"),
    STORAGE("STORAGE"),
    MINEXTENTS("MINEXTENTS"),
    MAXEXTENTS("MAXEXTENTS"),
    MAXSIZE("MAXSIZE"),
    PCTINCREASE("PCTINCREASE"),
    FLASH_CACHE("FLASH_CACHE"),
    CELL_FLASH_CACHE("CELL_FLASH_CACHE"),
    NONE("NONE"),
    LOB("LOB"),
    STORE("STORE"),
    CHUNK("CHUNK"),
    CACHE("CACHE"),
    NOCACHE("NOCACHE"),
    LOGGING("LOGGING"),
    NOCOMPRESS("NOCOMPRESS"),
    KEEP_DUPLICATES("KEEP_DUPLICATES"),
    EXCEPTIONS("EXCEPTIONS"),
    PURGE("PURGE"),
    
    COMPUTE("COMPUTE"),
    ANALYZE("ANALYZE"),
    OPTIMIZE("OPTIMIZE"),
    
    // transact-sql
    TOP("TOP"),
    
    ARRAY("ARRAY"),
    DISTRIBUTE("DISTRIBUTE"),
    
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
    
    // odps,hive
    PARTITION,
    PARTITIONED,
    OVERWRITE,
    
    // Teradata
    SEL("SEL"),
    LOCKING("LOCKING"),
    ACCESS("ACCESS"),
    VOLATILE("VOLATILE"),
    MULTISET("MULTISET"),
    POSITION("POSITION"),
    RANGE_N("RANGE_N"),
    FORMAT("FORMAT"),
    QUALIFY("QUALIFY"),
    MOD("MOD"),
    
    CONCAT("CONCAT"), // DB2

    UPSERT("UPSERT"), // Phoenix

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
    LT_SUB_GT("<->"),
    BANG("!"),
    BANGBANG("!!"),
    BANG_TILDE("!~"),
    BANG_TILDE_STAR("!~*"),
    TILDE("~"),
    TILDE_STAR("~*"),
    TILDE_EQ("~="),
    QUES("?"),
    QUESQUES("??"),
    QUESBAR("?|"),
    QUESAMP("?&"),
    COLON(":"), 
    COLONCOLON("::"), 
    COLONEQ(":="), 
    EQEQ("=="),
    EQGT("=>"),
    LTEQ("<="), 
    LTEQGT("<=>"), 
    LTGT("<>"), 
    GTEQ(">="), 
    BANGEQ("!="), 
    BANGGT("!>"), 
    BANGLT("!<"),
    AMPAMP("&&"), 
    BARBAR("||"), 
    BARBARSLASH("||/"), 
    BARSLASH("|/"), 
    PLUS("+"), 
    SUB("-"), 
    SUBGT("->"), 
    SUBGTGT("->>"), 
    STAR("*"), 
    SLASH("/"), 
    AMP("&"), 
    BAR("|"), 
    CARET("^"),
    CARETEQ("^="),
    PERCENT("%"), 
    LTLT("<<"), 
    GTGT(">>"),
    MONKEYS_AT("@"),
    MONKEYS_AT_AT("@@"),
    POUND("#"),
    POUNDGT("#>"),
    POUNDGTGT("#>>"),
    MONKEYS_AT_GT("@>"),
    LT_MONKEYS_AT("<@"),
    DOLLAR("$"),
    ;

    public final String name;

    Token(){
        this(null);
    }

    Token(String name){
        this.name = name;
    }
}
