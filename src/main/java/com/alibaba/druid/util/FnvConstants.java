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
package com.alibaba.druid.util;

public interface FnvConstants {
    long HIGH_PRIORITY = FnvHash.fnv1a_64_lower("HIGH_PRIORITY");
    long DISTINCTROW = FnvHash.fnv1a_64_lower("DISTINCTROW");
    long STRAIGHT_JOIN = FnvHash.fnv1a_64_lower("STRAIGHT_JOIN");
    long SQL_SMALL_RESULT = FnvHash.fnv1a_64_lower("SQL_SMALL_RESULT");
    long SQL_BIG_RESULT = FnvHash.fnv1a_64_lower("SQL_BIG_RESULT");
    long SQL_BUFFER_RESULT = FnvHash.fnv1a_64_lower("SQL_BUFFER_RESULT");
    long SQL_CACHE = FnvHash.fnv1a_64_lower("SQL_CACHE");
    long SQL_NO_CACHE = FnvHash.fnv1a_64_lower("SQL_NO_CACHE");
    long SQL_CALC_FOUND_ROWS = FnvHash.fnv1a_64_lower("SQL_CALC_FOUND_ROWS");
    long OUTFILE = FnvHash.fnv1a_64_lower("OUTFILE");
    long SETS = FnvHash.fnv1a_64_lower("SETS");
    long REGEXP = FnvHash.fnv1a_64_lower("REGEXP");
    long RLIKE = FnvHash.fnv1a_64_lower("RLIKE");
    long USING = FnvHash.fnv1a_64_lower("USING");
    long IGNORE = FnvHash.fnv1a_64_lower("IGNORE");
    long FORCE = FnvHash.fnv1a_64_lower("FORCE");
    long CROSS = FnvHash.fnv1a_64_lower("CROSS");
    long NATURAL = FnvHash.fnv1a_64_lower("NATURAL");
    long APPLY = FnvHash.fnv1a_64_lower("APPLY");
    long CONNECT = FnvHash.fnv1a_64_lower("CONNECT");
    long START = FnvHash.fnv1a_64_lower("START");
    long BTREE = FnvHash.fnv1a_64_lower("BTREE");
    long HASH = FnvHash.fnv1a_64_lower("HASH");
    long NO_WAIT = FnvHash.fnv1a_64_lower("NO_WAIT");
    long WAIT = FnvHash.fnv1a_64_lower("WAIT");
    long NOWAIT = FnvHash.fnv1a_64_lower("NOWAIT");
    long ERRORS = FnvHash.fnv1a_64_lower("ERRORS");
    long VALUE = FnvHash.fnv1a_64_lower("VALUE");
    long NEXT = FnvHash.fnv1a_64_lower("NEXT");
    long NEXTVAL = FnvHash.fnv1a_64_lower("NEXTVAL");
    long CURRVAL = FnvHash.fnv1a_64_lower("CURRVAL");
    long PREVVAL = FnvHash.fnv1a_64_lower("PREVVAL");
    long PREVIOUS = FnvHash.fnv1a_64_lower("PREVIOUS");
    long LOW_PRIORITY = FnvHash.fnv1a_64_lower("LOW_PRIORITY");
    long COMMIT_ON_SUCCESS = FnvHash.fnv1a_64_lower("COMMIT_ON_SUCCESS");
    long ROLLBACK_ON_FAIL = FnvHash.fnv1a_64_lower("ROLLBACK_ON_FAIL");
    long QUEUE_ON_PK = FnvHash.fnv1a_64_lower("QUEUE_ON_PK");
    long TARGET_AFFECT_ROW = FnvHash.fnv1a_64_lower("TARGET_AFFECT_ROW");
    long COLLATE = FnvHash.fnv1a_64_lower("COLLATE");
    long BOOLEAN = FnvHash.fnv1a_64_lower("BOOLEAN");
    long CHARSET = FnvHash.fnv1a_64_lower("CHARSET");
    long SEMI = FnvHash.fnv1a_64_lower("SEMI");
    long ANTI = FnvHash.fnv1a_64_lower("ANTI");
    long PRIOR = FnvHash.fnv1a_64_lower("PRIOR");
    long NOCYCLE = FnvHash.fnv1a_64_lower("NOCYCLE");
    long CONNECT_BY_ROOT = FnvHash.fnv1a_64_lower("CONNECT_BY_ROOT");
    long DATE = FnvHash.fnv1a_64_lower("DATE");
    long TIMESTAMP = FnvHash.fnv1a_64_lower("TIMESTAMP");
    long CURRENT = FnvHash.fnv1a_64_lower("CURRENT");
    long COUNT = FnvHash.fnv1a_64_lower("COUNT");
    long ROW_NUMBER = FnvHash.fnv1a_64_lower("ROW_NUMBER");
    long WM_CONAT = FnvHash.fnv1a_64_lower("WM_CONAT");
    long AVG = FnvHash.fnv1a_64_lower("AVG");
    long MAX = FnvHash.fnv1a_64_lower("MAX");
    long MIN = FnvHash.fnv1a_64_lower("MIN");
    long STDDEV = FnvHash.fnv1a_64_lower("STDDEV");
    long SUM = FnvHash.fnv1a_64_lower("SUM");
    long GROUP_CONCAT = FnvHash.fnv1a_64_lower("GROUP_CONCAT");
    long DEDUPLICATION = FnvHash.fnv1a_64_lower("DEDUPLICATION");
    long CONVERT = FnvHash.fnv1a_64_lower("CONVERT");
    long CHAR = FnvHash.fnv1a_64_lower("CHAR");
    long VARCHAR = FnvHash.fnv1a_64_lower("VARCHAR");
    long VARCHAR2 = FnvHash.fnv1a_64_lower("VARCHAR2");
    long NCHAR = FnvHash.fnv1a_64_lower("NCHAR");
    long NVARCHAR = FnvHash.fnv1a_64_lower("NVARCHAR");
    long NVARCHAR2 = FnvHash.fnv1a_64_lower("NVARCHAR2");
    long TINYTEXT = FnvHash.fnv1a_64_lower("TINYTEXT");
    long TEXT = FnvHash.fnv1a_64_lower("TEXT");
    long MEDIUMTEXT = FnvHash.fnv1a_64_lower("MEDIUMTEXT");
    long LONGTEXT = FnvHash.fnv1a_64_lower("LONGTEXT");
    long TRIM = FnvHash.fnv1a_64_lower("TRIM");
    long LEADING = FnvHash.fnv1a_64_lower("LEADING");
    long BOTH = FnvHash.fnv1a_64_lower("BOTH");
    long TRAILING = FnvHash.fnv1a_64_lower("TRAILING");
    long MOD = FnvHash.fnv1a_64_lower("MOD");
    long MATCH = FnvHash.fnv1a_64_lower("MATCH");
    long EXTRACT = FnvHash.fnv1a_64_lower("EXTRACT");
    long POSITION = FnvHash.fnv1a_64_lower("POSITION");
    long DUAL = FnvHash.fnv1a_64_lower("DUAL");
    long LEVEL = FnvHash.fnv1a_64_lower("LEVEL");
    long CONNECT_BY_ISCYCLE = FnvHash.fnv1a_64_lower("CONNECT_BY_ISCYCLE");
    long CURRENT_TIMESTAMP = FnvHash.fnv1a_64_lower("CURRENT_TIMESTAMP");
    long FALSE = FnvHash.fnv1a_64_lower("FALSE");
    long TRUE = FnvHash.fnv1a_64_lower("TRUE");
    long SET = FnvHash.fnv1a_64_lower("SET");
    long LESS = FnvHash.fnv1a_64_lower("LESS");
    long MAXVALUE = FnvHash.fnv1a_64_lower("MAXVALUE");
    long OFFSET = FnvHash.fnv1a_64_lower("OFFSET");
    long RAW = FnvHash.fnv1a_64_lower("RAW");
    long LONG = FnvHash.fnv1a_64_lower("LONG");
    long ROWNUM = FnvHash.fnv1a_64_lower("ROWNUM");
    long PRECISION = FnvHash.fnv1a_64_lower("PRECISION");
    long DOUBLE = FnvHash.fnv1a_64_lower("DOUBLE");
    long WITHOUT = FnvHash.fnv1a_64_lower("WITHOUT");

    long DEFINER = FnvHash.fnv1a_64_lower("DEFINER");
    long DETERMINISTIC = FnvHash.fnv1a_64_lower("DETERMINISTIC");
    long CONTAINS = FnvHash.fnv1a_64_lower("CONTAINS");
    long SQL = FnvHash.fnv1a_64_lower("SQL");
    long CALL = FnvHash.fnv1a_64_lower("CALL");
    long CHARACTER = FnvHash.fnv1a_64_lower("CHARACTER");

    long VALIDATE = FnvHash.fnv1a_64_lower("VALIDATE");
    long NOVALIDATE = FnvHash.fnv1a_64_lower("NOVALIDATE");
    long SIMILAR = FnvHash.fnv1a_64_lower("SIMILAR");
    long CASCADE = FnvHash.fnv1a_64_lower("CASCADE");
    long RELY = FnvHash.fnv1a_64_lower("RELY");
    long NORELY = FnvHash.fnv1a_64_lower("NORELY");
    long ROW = FnvHash.fnv1a_64_lower("ROW");
    long ROWS = FnvHash.fnv1a_64_lower("ROWS");
    long RANGE = FnvHash.fnv1a_64_lower("RANGE");
    long PRECEDING = FnvHash.fnv1a_64_lower("PRECEDING");
    long FOLLOWING = FnvHash.fnv1a_64_lower("FOLLOWING");
    long UNBOUNDED = FnvHash.fnv1a_64_lower("UNBOUNDED");
    long SIBLINGS = FnvHash.fnv1a_64_lower("SIBLINGS");
    long NULLS = FnvHash.fnv1a_64_lower("NULLS");
    long FIRST = FnvHash.fnv1a_64_lower("FIRST");
    long LAST = FnvHash.fnv1a_64_lower("LAST");
    long AUTO_INCREMENT = FnvHash.fnv1a_64_lower("AUTO_INCREMENT");
    long STORAGE = FnvHash.fnv1a_64_lower("STORAGE");
    long STORED = FnvHash.fnv1a_64_lower("STORED");
    long VIRTUAL = FnvHash.fnv1a_64_lower("VIRTUAL");
    long UNSIGNED = FnvHash.fnv1a_64_lower("UNSIGNED");
    long ZEROFILL = FnvHash.fnv1a_64_lower("ZEROFILL");
    long GLOBAL = FnvHash.fnv1a_64_lower("GLOBAL");
    long SESSION = FnvHash.fnv1a_64_lower("SESSION");
    long NAMES = FnvHash.fnv1a_64_lower("NAMES");
    long PARTIAL = FnvHash.fnv1a_64_lower("PARTIAL");
    long SIMPLE = FnvHash.fnv1a_64_lower("SIMPLE");
    long RESTRICT = FnvHash.fnv1a_64_lower("RESTRICT");
    long ON = FnvHash.fnv1a_64_lower("ON");
    long ACTION = FnvHash.fnv1a_64_lower("ACTION");
    long SEPARATOR = FnvHash.fnv1a_64_lower("SEPARATOR");
    long DATA = FnvHash.fnv1a_64_lower("DATA");
    long MAX_ROWS = FnvHash.fnv1a_64_lower("MAX_ROWS");
    long MIN_ROWS = FnvHash.fnv1a_64_lower("MIN_ROWS");
    long ENGINE = FnvHash.fnv1a_64_lower("ENGINE");
    long SKIP = FnvHash.fnv1a_64_lower("SKIP");
    long RECURSIVE = FnvHash.fnv1a_64_lower("RECURSIVE");
    long ROLLUP = FnvHash.fnv1a_64_lower("ROLLUP");
    long CUBE = FnvHash.fnv1a_64_lower("CUBE");
}
