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

import java.util.Arrays;

public final class FnvHash {
    public final static long BASIC = 0xcbf29ce484222325L;
    public final static long PRIME = 0x100000001b3L;

    public static long fnv1a_64(String input) {
        if (input == null) {
            return 0;
        }

        long hash = BASIC;
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            hash ^= c;
            hash *= PRIME;
        }

        return hash;
    }

    public static long fnv1a_64(StringBuilder input) {
        if (input == null) {
            return 0;
        }

        long hash = BASIC;
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            hash ^= c;
            hash *= PRIME;
        }

        return hash;
    }

    public static long fnv1a_64(String input, int offset, int end) {
        if (input == null) {
            return 0;
        }

        if (input.length() < end) {
            end = input.length();
        }

        long hash = BASIC;
        for (int i = offset; i < end; ++i) {
            char c = input.charAt(i);
            hash ^= c;
            hash *= PRIME;
        }

        return hash;
    }

    public static long fnv1a_64(byte[] input, int offset, int end) {
        if (input == null) {
            return 0;
        }

        if (input.length < end) {
            end = input.length;
        }

        long hash = BASIC;
        for (int i = offset; i < end; ++i) {
            byte c = input[i];
            hash ^= c;
            hash *= PRIME;
        }

        return hash;
    }

    public static long fnv1a_64(char[] chars) {
        if (chars == null) {
            return 0;
        }
        long hash = BASIC;
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            hash ^= c;
            hash *= PRIME;
        }

        return hash;
    }

    /**
     * lower and normalized and fnv_1a_64
     * @param name
     * @return
     */
    public static long hashCode64(String name) {
        if (name == null) {
            return 0;
        }

        boolean quote = false;

        int len = name.length();
        if (len > 2) {
            char c0 = name.charAt(0);
            char c1 = name.charAt(len - 1);
            if ((c0 == '`' && c1 == '`')
                    || (c0 == '"' && c1 == '"')
                    || (c0 == '\'' && c1 == '\'')
                    || (c0 == '[' && c1 == ']')) {
                quote = true;
            }
        }
        if (quote) {
            return FnvHash.hashCode64(name, 1, len - 1);
        } else {
            return FnvHash.hashCode64(name, 0, len);
        }
    }

    public static long fnv1a_64_lower(String key) {
        long hashCode = BASIC;
        for (int i = 0; i < key.length(); ++i) {
            char ch = key.charAt(i);

            if (ch >= 'A' && ch <= 'Z') {
                ch = (char) (ch + 32);
            }

            hashCode ^= ch;
            hashCode *= PRIME;
        }

        return hashCode;
    }

    public static long fnv1a_64_lower(StringBuilder key) {
        long hashCode = BASIC;
        for (int i = 0; i < key.length(); ++i) {
            char ch = key.charAt(i);

            if (ch >= 'A' && ch <= 'Z') {
                ch = (char) (ch + 32);
            }

            hashCode ^= ch;
            hashCode *= PRIME;
        }

        return hashCode;
    }

    public static long fnv1a_64_lower(long basic, StringBuilder key) {
        long hashCode = basic;
        for (int i = 0; i < key.length(); ++i) {
            char ch = key.charAt(i);

            if (ch >= 'A' && ch <= 'Z') {
                ch = (char) (ch + 32);
            }

            hashCode ^= ch;
            hashCode *= PRIME;
        }

        return hashCode;
    }

    public static long hashCode64(String key, int offset, int end) {
        long hashCode = BASIC;
        for (int i = offset; i < end; ++i) {
            char ch = key.charAt(i);

            if (ch >= 'A' && ch <= 'Z') {
                ch = (char) (ch + 32);
            }

            hashCode ^= ch;
            hashCode *= PRIME;
        }

        return hashCode;
    }

    public static long hashCode64(long basic, String name) {
        if (name == null) {
            return basic;
        }

        boolean quote = false;

        int len = name.length();
        if (len > 2) {
            char c0 = name.charAt(0);
            char c1 = name.charAt(len - 1);
            if ((c0 == '`' && c1 == '`')
                    || (c0 == '"' && c1 == '"')
                    || (c0 == '\'' && c1 == '\'')
                    || (c0 == '[' && c1 == ']')) {
                quote = true;
            }
        }
        if (quote) {
            int offset = 1;
            int end = len - 1;
            for (int i = end - 1; i >= 0; --i) {
                char ch = name.charAt(i);
                if (ch == ' ') {
                    end--;
                } else {
                    break;
                }
            }
            return FnvHash.hashCode64(basic, name, offset, end);
        } else {
            return FnvHash.hashCode64(basic, name, 0, len);
        }
    }

    public static long hashCode64(long basic, String key, int offset, int end) {
        long hashCode = basic;
        for (int i = offset; i < end; ++i) {
            char ch = key.charAt(i);

            if (ch >= 'A' && ch <= 'Z') {
                ch = (char) (ch + 32);
            }

            hashCode ^= ch;
            hashCode *= PRIME;
        }

        return hashCode;
    }

    public static long fnv_32_lower(String key) {
        long hashCode = 0x811c9dc5;
        for (int i = 0; i < key.length(); ++i) {
            char ch = key.charAt(i);
            if (ch == '_' || ch == '-') {
                continue;
            }

            if (ch >= 'A' && ch <= 'Z') {
                ch = (char) (ch + 32);
            }

            hashCode ^= ch;
            hashCode *= 0x01000193;
        }

        return hashCode;
    }

    public static long[] fnv1a_64_lower(String[] strings, boolean sort) {
        long[] hashCodes = new long[strings.length];
        for (int i = 0; i < strings.length; i++) {
            hashCodes[i] = fnv1a_64_lower(strings[i]);
        }
        if (sort) {
            Arrays.sort(hashCodes);
        }
        return hashCodes;
    }

    /**
     * normalized and lower and fnv1a_64_hash
     * @param owner
     * @param name
     * @return
     */
    public static long hashCode64(String owner, String name) {
        long hashCode = BASIC;

        if (owner != null) {
            String item = owner;

            boolean quote = false;

            int len = item.length();
            if (len > 2) {
                char c0 = item.charAt(0);
                char c1 = item.charAt(len - 1);
                if ((c0 == '`' && c1 == '`')
                        || (c0 == '"' && c1 == '"')
                        || (c0 == '\'' && c1 == '\'')
                        || (c0 == '[' && c1 == ']')) {
                    quote = true;
                }
            }

            int start = quote ? 1 : 0;
            int end   = quote ? len - 1 : len;
            for (int j = start; j < end; ++j) {
                char ch = item.charAt(j);

                if (ch >= 'A' && ch <= 'Z') {
                    ch = (char) (ch + 32);
                }

                hashCode ^= ch;
                hashCode *= PRIME;
            }

            hashCode ^= '.';
            hashCode *= PRIME;
        }


        if (name != null) {
            String item = name;

            boolean quote = false;

            int len = item.length();
            if (len > 2) {
                char c0 = item.charAt(0);
                char c1 = item.charAt(len - 1);
                if ((c0 == '`' && c1 == '`')
                        || (c0 == '"' && c1 == '"')
                        || (c0 == '\'' && c1 == '\'')
                        || (c0 == '[' && c1 == ']')) {
                    quote = true;
                }
            }

            int start = quote ? 1 : 0;
            int end   = quote ? len - 1 : len;
            for (int j = start; j < end; ++j) {
                char ch = item.charAt(j);

                if (ch >= 'A' && ch <= 'Z') {
                    ch = (char) (ch + 32);
                }

                hashCode ^= ch;
                hashCode *= PRIME;
            }
        }

        return hashCode;
    }

    public static interface Constants {
        long HIGH_PRIORITY = fnv1a_64_lower("HIGH_PRIORITY");
        long DISTINCTROW = fnv1a_64_lower("DISTINCTROW");
        long STRAIGHT = fnv1a_64_lower("STRAIGHT");
        long STRAIGHT_JOIN = fnv1a_64_lower("STRAIGHT_JOIN");
        long SQL_SMALL_RESULT = fnv1a_64_lower("SQL_SMALL_RESULT");
        long SQL_BIG_RESULT = fnv1a_64_lower("SQL_BIG_RESULT");
        long SQL_BUFFER_RESULT = fnv1a_64_lower("SQL_BUFFER_RESULT");
        long CACHE = fnv1a_64_lower("CACHE");
        long SQL_CACHE = fnv1a_64_lower("SQL_CACHE");
        long SQL_NO_CACHE = fnv1a_64_lower("SQL_NO_CACHE");
        long SQL_CALC_FOUND_ROWS = fnv1a_64_lower("SQL_CALC_FOUND_ROWS");
        long TOP = fnv1a_64_lower("TOP");
        long OUTFILE = fnv1a_64_lower("OUTFILE");
        long SETS = fnv1a_64_lower("SETS");
        long REGEXP = fnv1a_64_lower("REGEXP");
        long RLIKE = fnv1a_64_lower("RLIKE");
        long USING = fnv1a_64_lower("USING");
        long IGNORE = fnv1a_64_lower("IGNORE");
        long FORCE = fnv1a_64_lower("FORCE");
        long CROSS = fnv1a_64_lower("CROSS");
        long NATURAL = fnv1a_64_lower("NATURAL");
        long APPLY = fnv1a_64_lower("APPLY");
        long CONNECT = fnv1a_64_lower("CONNECT");
        long START = fnv1a_64_lower("START");
        long BTREE = fnv1a_64_lower("BTREE");
        long HASH = fnv1a_64_lower("HASH");
        long LIST = fnv1a_64_lower("LIST");
        long NO_WAIT = fnv1a_64_lower("NO_WAIT");
        long WAIT = fnv1a_64_lower("WAIT");
        long NOWAIT = fnv1a_64_lower("NOWAIT");
        long ERRORS = fnv1a_64_lower("ERRORS");
        long VALUE = fnv1a_64_lower("VALUE");
        long OBJECT = fnv1a_64_lower("OBJECT");
        long NEXT = fnv1a_64_lower("NEXT");
        long NEXTVAL = fnv1a_64_lower("NEXTVAL");
        long CURRVAL = fnv1a_64_lower("CURRVAL");
        long PREVVAL = fnv1a_64_lower("PREVVAL");
        long PREVIOUS = fnv1a_64_lower("PREVIOUS");
        long LOW_PRIORITY = fnv1a_64_lower("LOW_PRIORITY");
        long COMMIT_ON_SUCCESS = fnv1a_64_lower("COMMIT_ON_SUCCESS");
        long ROLLBACK_ON_FAIL = fnv1a_64_lower("ROLLBACK_ON_FAIL");
        long QUEUE_ON_PK = fnv1a_64_lower("QUEUE_ON_PK");
        long TARGET_AFFECT_ROW = fnv1a_64_lower("TARGET_AFFECT_ROW");
        long COLLATE = fnv1a_64_lower("COLLATE");
        long BOOLEAN = fnv1a_64_lower("BOOLEAN");
        long SMALLINT = fnv1a_64_lower("SMALLINT");
        long SHORT = fnv1a_64_lower("SHORT");
        long TINY = fnv1a_64_lower("TINY");
        long TINYINT = fnv1a_64_lower("TINYINT");
        long CHARSET = fnv1a_64_lower("CHARSET");
        long SEMI = fnv1a_64_lower("SEMI");
        long ANTI = fnv1a_64_lower("ANTI");
        long PRIOR = fnv1a_64_lower("PRIOR");
        long NOCYCLE = fnv1a_64_lower("NOCYCLE");
        long CYCLE = fnv1a_64_lower("CYCLE");
        long CONNECT_BY_ROOT = fnv1a_64_lower("CONNECT_BY_ROOT");

        long DATE = fnv1a_64_lower("DATE");
        long GSON = fnv1a_64_lower("GSON");
        long NEW = fnv1a_64_lower("NEW");
        long NEWDATE = fnv1a_64_lower("NEWDATE");
        long DATETIME = fnv1a_64_lower("DATETIME");
        long TIME = fnv1a_64_lower("TIME");
        long ZONE = fnv1a_64_lower("ZONE");
        long JSON = fnv1a_64_lower("JSON");
        long TIMESTAMP = fnv1a_64_lower("TIMESTAMP");
        long TIMESTAMPTZ = fnv1a_64_lower("TIMESTAMPTZ");
        long CLOB = fnv1a_64_lower("CLOB");
        long NCLOB = fnv1a_64_lower("NCLOB");
        long TINYBLOB = fnv1a_64_lower("TINYBLOB");
        long BLOB = fnv1a_64_lower("BLOB");
        long XMLTYPE = fnv1a_64_lower("XMLTYPE");
        long BFILE = fnv1a_64_lower("BFILE");
        long UROWID = fnv1a_64_lower("UROWID");
        long ROWID = fnv1a_64_lower("ROWID");
        long REF = fnv1a_64_lower("REF");
        long INTEGER = fnv1a_64_lower("INTEGER");
        long INT = fnv1a_64_lower("INT");
        long INT24 = fnv1a_64_lower("INT24");
        long BINARY_FLOAT = fnv1a_64_lower("BINARY_FLOAT");
        long BINARY_DOUBLE = fnv1a_64_lower("BINARY_DOUBLE");
        long FLOAT = fnv1a_64_lower("FLOAT");
        long REAL = fnv1a_64_lower("REAL");
        long NUMBER = fnv1a_64_lower("NUMBER");
        long NUMERIC = fnv1a_64_lower("NUMERIC");
        long DEC = fnv1a_64_lower("DEC");
        long DECIMAL = fnv1a_64_lower("DECIMAL");

        long CURRENT = fnv1a_64_lower("CURRENT");
        long COUNT = fnv1a_64_lower("COUNT");
        long ROW_NUMBER = fnv1a_64_lower("ROW_NUMBER");
        long FIRST_VALUE = fnv1a_64_lower("FIRST_VALUE");
        long LAST_VALUE = fnv1a_64_lower("LAST_VALUE");
        long WM_CONCAT = fnv1a_64_lower("WM_CONCAT");
        long AVG = fnv1a_64_lower("AVG");
        long MAX = fnv1a_64_lower("MAX");
        long MIN = fnv1a_64_lower("MIN");
        long STDDEV = fnv1a_64_lower("STDDEV");
        long RANK = fnv1a_64_lower("RANK");
        long SUM = fnv1a_64_lower("SUM");
        long ARBITRARY = fnv1a_64_lower("ARBITRARY");
        long GROUP_CONCAT = fnv1a_64_lower("GROUP_CONCAT");
        long CONVERT_TZ = fnv1a_64_lower("CONVERT_TZ");
        long DEDUPLICATION = fnv1a_64_lower("DEDUPLICATION");
        long CONVERT = fnv1a_64_lower("CONVERT");
        long CHAR = fnv1a_64_lower("CHAR");
        long ENUM = fnv1a_64_lower("ENUM");
        long STRING = fnv1a_64_lower("STRING");
        long VARCHAR = fnv1a_64_lower("VARCHAR");
        long VARCHAR2 = fnv1a_64_lower("VARCHAR2");
        long NCHAR = fnv1a_64_lower("NCHAR");
        long NVARCHAR = fnv1a_64_lower("NVARCHAR");
        long NVARCHAR2 = fnv1a_64_lower("NVARCHAR2");
        long NCHAR_VARYING = fnv1a_64_lower("nchar varying");
        long VARBINARY = fnv1a_64_lower("VARBINARY");
        long TINYTEXT = fnv1a_64_lower("TINYTEXT");
        long TEXT = fnv1a_64_lower("TEXT");
        long MEDIUMTEXT = fnv1a_64_lower("MEDIUMTEXT");
        long LONGTEXT = fnv1a_64_lower("LONGTEXT");
        long TRIM = fnv1a_64_lower("TRIM");
        long LEADING = fnv1a_64_lower("LEADING");
        long BOTH = fnv1a_64_lower("BOTH");
        long TRAILING = fnv1a_64_lower("TRAILING");
        long MOD = fnv1a_64_lower("MOD");
        long MATCH = fnv1a_64_lower("MATCH");
        long AGAINST = fnv1a_64_lower("AGAINST");
        long EXTRACT = fnv1a_64_lower("EXTRACT");
        long POLYGON = fnv1a_64_lower("POLYGON");
        long CIRCLE = fnv1a_64_lower("CIRCLE");
        long LSEG = fnv1a_64_lower("LSEG");
        long POINT = fnv1a_64_lower("POINT");
        long BOX = fnv1a_64_lower("BOX");
        long MACADDR = fnv1a_64_lower("MACADDR");
        long INET = fnv1a_64_lower("INET");
        long CIDR = fnv1a_64_lower("CIDR");
        long POSITION = fnv1a_64_lower("POSITION");
        long DUAL = fnv1a_64_lower("DUAL");
        long LEVEL = fnv1a_64_lower("LEVEL");
        long CONNECT_BY_ISCYCLE = fnv1a_64_lower("CONNECT_BY_ISCYCLE");
        long CURRENT_TIMESTAMP = fnv1a_64_lower("CURRENT_TIMESTAMP");
        long LOCALTIMESTAMP = fnv1a_64_lower("LOCALTIMESTAMP");
        long LOCALTIME = fnv1a_64_lower("LOCALTIME");
        long SESSIONTIMEZONE = fnv1a_64_lower("SESSIONTIMEZONE");
        long DBTIMEZONE = fnv1a_64_lower("DBTIMEZONE");
        long CURRENT_DATE = fnv1a_64_lower("CURRENT_DATE");
        long CURRENT_TIME = fnv1a_64_lower("CURRENT_TIME");
        long CURTIME = fnv1a_64_lower("CURTIME");
        long CURRENT_USER = fnv1a_64_lower("CURRENT_USER");
        long FALSE = fnv1a_64_lower("FALSE");
        long TRUE = fnv1a_64_lower("TRUE");
        long LESS = fnv1a_64_lower("LESS");
        long MAXVALUE = fnv1a_64_lower("MAXVALUE");
        long OFFSET = fnv1a_64_lower("OFFSET");
        long LIMIT = fnv1a_64_lower("LIMIT");
        long RAW = fnv1a_64_lower("RAW");
        long LONG_RAW = fnv1a_64_lower("LONG RAW");
        long LONG = fnv1a_64_lower("LONG");
        long BYTE = fnv1a_64_lower("BYTE");
        long ROWNUM = fnv1a_64_lower("ROWNUM");
        long SYSDATE = fnv1a_64_lower("SYSDATE");
        long NOW = fnv1a_64_lower("NOW");
        long ADDTIME = fnv1a_64_lower("ADDTIME");
        long SUBTIME = fnv1a_64_lower("SUBTIME");
        long TIMEDIFF = fnv1a_64_lower("TIMEDIFF");
        long SQLCODE = fnv1a_64_lower("SQLCODE");
        long PRECISION = fnv1a_64_lower("PRECISION");
        long DOUBLE = fnv1a_64_lower("DOUBLE");
        long DOUBLE_PRECISION = fnv1a_64_lower("DOUBLE PRECISION");
        long WITHOUT = fnv1a_64_lower("WITHOUT");
        long BITAND = fnv1a_64_lower("BITAND");

        long DEFINER = fnv1a_64_lower("DEFINER");
        long EVENT = fnv1a_64_lower("EVENT");
        long RESOURCE = fnv1a_64_lower("RESOURCE");
        long RESOURCES = fnv1a_64_lower("RESOURCES");
        long FILE = fnv1a_64_lower("FILE");
        long JAR = fnv1a_64_lower("JAR");
        long PY = fnv1a_64_lower("PY");
        long ARCHIVE = fnv1a_64_lower("archive");
        long DETERMINISTIC = fnv1a_64_lower("DETERMINISTIC");
        long CONTAINS = fnv1a_64_lower("CONTAINS");
        long SQL = fnv1a_64_lower("SQL");
        long CALL = fnv1a_64_lower("CALL");
        long CHARACTER = fnv1a_64_lower("CHARACTER");
        long UNNEST = fnv1a_64_lower("UNNEST");

        long VALIDATE = fnv1a_64_lower("VALIDATE");
        long NOVALIDATE = fnv1a_64_lower("NOVALIDATE");
        long SIMILAR = fnv1a_64_lower("SIMILAR");
        long CASCADE = fnv1a_64_lower("CASCADE");
        long RELY = fnv1a_64_lower("RELY");
        long NORELY = fnv1a_64_lower("NORELY");
        long ROW = fnv1a_64_lower("ROW");
        long ROWS = fnv1a_64_lower("ROWS");
        long RANGE = fnv1a_64_lower("RANGE");
        long PRECEDING = fnv1a_64_lower("PRECEDING");
        long FOLLOWING = fnv1a_64_lower("FOLLOWING");
        long UNBOUNDED = fnv1a_64_lower("UNBOUNDED");
        long SIBLINGS = fnv1a_64_lower("SIBLINGS");
        long RESPECT = fnv1a_64_lower("RESPECT");
        long NULLS = fnv1a_64_lower("NULLS");
        long FIRST = fnv1a_64_lower("FIRST");
        long LAST = fnv1a_64_lower("LAST");
        long AUTO_INCREMENT = fnv1a_64_lower("AUTO_INCREMENT");
        long STORAGE = fnv1a_64_lower("STORAGE");
        long STORED = fnv1a_64_lower("STORED");
        long VIRTUAL = fnv1a_64_lower("VIRTUAL");
        long SIGNED = fnv1a_64_lower("SIGNED");
        long UNSIGNED = fnv1a_64_lower("UNSIGNED");
        long ZEROFILL = fnv1a_64_lower("ZEROFILL");
        long GLOBAL = fnv1a_64_lower("GLOBAL");
        long LOCAL = fnv1a_64_lower("LOCAL");
        long TEMPORARY = fnv1a_64_lower("TEMPORARY");
        long NONCLUSTERED = fnv1a_64_lower("NONCLUSTERED");
        long SESSION = fnv1a_64_lower("SESSION");
        long NAMES = fnv1a_64_lower("NAMES");
        long PARTIAL = fnv1a_64_lower("PARTIAL");
        long SIMPLE = fnv1a_64_lower("SIMPLE");
        long RESTRICT = fnv1a_64_lower("RESTRICT");
        long ON = fnv1a_64_lower("ON");
        long ACTION = fnv1a_64_lower("ACTION");
        long SEPARATOR = fnv1a_64_lower("SEPARATOR");
        long DATA = fnv1a_64_lower("DATA");
        long MIGRATE = fnv1a_64_lower("MIGRATE");
        long MAX_ROWS = fnv1a_64_lower("MAX_ROWS");
        long MIN_ROWS = fnv1a_64_lower("MIN_ROWS");
        long PACK_KEYS = fnv1a_64_lower("PACK_KEYS");
        long ENGINE = fnv1a_64_lower("ENGINE");
        long SKIP = fnv1a_64_lower("SKIP");
        long RECURSIVE = fnv1a_64_lower("RECURSIVE");
        long ROLLUP = fnv1a_64_lower("ROLLUP");
        long CUBE = fnv1a_64_lower("CUBE");

        long YEAR = fnv1a_64_lower("YEAR");
        long QUARTER = fnv1a_64_lower("QUARTER");
        long MONTH = fnv1a_64_lower("MONTH");
        long WEEK = fnv1a_64_lower("WEEK");
        long WEEKDAY = fnv1a_64_lower("WEEKDAY");
        long WEEKOFYEAR = fnv1a_64_lower("WEEKOFYEAR");
        long YEARWEEK = fnv1a_64_lower("YEARWEEK");
        long YEAR_OF_WEEK = fnv1a_64_lower("YEAR_OF_WEEK");
        long YOW = fnv1a_64_lower("YOW");
        long YEARMONTH = fnv1a_64_lower("YEARMONTH");
        long TO_TIMESTAMP = fnv1a_64_lower("TO_TIMESTAMP");
        long DAY = fnv1a_64_lower("DAY");
        long DAYOFMONTH = fnv1a_64_lower("DAYOFMONTH");
        long DAYOFWEEK = fnv1a_64_lower("DAYOFWEEK");
        long DATE_TRUNC = fnv1a_64_lower("DATE_TRUNC");
        long DAYOFYEAR = fnv1a_64_lower("DAYOFYEAR");
        long MONTH_BETWEEN = fnv1a_64_lower("MONTH_BETWEEN");
        long TIMESTAMPADD = fnv1a_64_lower("TIMESTAMPADD");
        long HOUR = fnv1a_64_lower("HOUR");
        long MINUTE = fnv1a_64_lower("MINUTE");
        long SECOND = fnv1a_64_lower("SECOND");
        long MICROSECOND = fnv1a_64_lower("MICROSECOND");
        long CURDATE = fnv1a_64_lower("CURDATE");
        long CUR_DATE = fnv1a_64_lower("CUR_DATE");
        long DATE_DIFF = fnv1a_64_lower("DATE_DIFF");

        long SECONDS = fnv1a_64_lower("SECONDS");
        long MINUTES = fnv1a_64_lower("MINUTES");
        long HOURS = fnv1a_64_lower("HOURS");
        long DAYS = fnv1a_64_lower("DAYS");
        long MONTHS = fnv1a_64_lower("MONTHS");
        long YEARS = fnv1a_64_lower("YEARS");

        long BEFORE = fnv1a_64_lower("BEFORE");
        long AFTER = fnv1a_64_lower("AFTER");
        long INSTEAD = fnv1a_64_lower("INSTEAD");

        long DEFERRABLE = fnv1a_64_lower("DEFERRABLE");
        long AS = fnv1a_64_lower("AS");
        long DELAYED = fnv1a_64_lower("DELAYED");
        long GO = fnv1a_64_lower("GO");
        long WAITFOR = fnv1a_64_lower("WAITFOR");
        long EXEC = fnv1a_64_lower("EXEC");
        long EXECUTE = fnv1a_64_lower("EXECUTE");

        long SOURCE = fnv1a_64_lower("SOURCE");

        long STAR = fnv1a_64_lower("*");

        long TO_CHAR = fnv1a_64_lower("TO_CHAR");
        long UNIX_TIMESTAMP = fnv1a_64_lower("UNIX_TIMESTAMP");
        long FROM_UNIXTIME = fnv1a_64_lower("FROM_UNIXTIME");
        long TO_UNIXTIME = fnv1a_64_lower("TO_UNIXTIME");
        long SYS_GUID = fnv1a_64_lower("SYS_GUID");
        long LAST_DAY = fnv1a_64_lower("LAST_DAY");
        long MAKEDATE = fnv1a_64_lower("MAKEDATE");
        long ASCII = fnv1a_64_lower("ASCII");
        long DAYNAME = fnv1a_64_lower("DAYNAME");

        long STATISTICS = fnv1a_64_lower("STATISTICS");
        long TRANSACTION = fnv1a_64_lower("TRANSACTION");
        long OFF = fnv1a_64_lower("OFF");
        long IDENTITY_INSERT = fnv1a_64_lower("IDENTITY_INSERT");
        long PASSWORD = fnv1a_64_lower("PASSWORD");
        long SOCKET = fnv1a_64_lower("SOCKET");
        long OWNER = fnv1a_64_lower("OWNER");
        long PORT = fnv1a_64_lower("PORT");
        long PUBLIC = fnv1a_64_lower("PUBLIC");
        long SYNONYM = fnv1a_64_lower("SYNONYM");
        long MATERIALIZED = fnv1a_64_lower("MATERIALIZED");
        long BITMAP = fnv1a_64_lower("BITMAP");
        long LABEL = fnv1a_64_lower("LABEL");
        long PACKAGE = fnv1a_64_lower("PACKAGE");
        long PACKAGES = fnv1a_64_lower("PACKAGES");
        long TRUNC = fnv1a_64_lower("TRUNC");
        long SYSTIMESTAMP = fnv1a_64_lower("SYSTIMESTAMP");
        long TYPE = fnv1a_64_lower("TYPE");
        long RECORD = fnv1a_64_lower("RECORD");
        long MAP = fnv1a_64_lower("MAP");
        long MAPJOIN = fnv1a_64_lower("MAPJOIN");
        long MAPPED = fnv1a_64_lower("MAPPED");
        long MAPPING = fnv1a_64_lower("MAPPING");
        long COLPROPERTIES = fnv1a_64_lower("COLPROPERTIES");
        long ONLY = fnv1a_64_lower("ONLY");
        long MEMBER = fnv1a_64_lower("MEMBER");
        long STATIC = fnv1a_64_lower("STATIC");
        long FINAL = fnv1a_64_lower("FINAL");
        long INSTANTIABLE = fnv1a_64_lower("INSTANTIABLE");
        long UNSUPPORTED = fnv1a_64_lower("UNSUPPORTED");
        long VARRAY = fnv1a_64_lower("VARRAY");
        long WRAPPED = fnv1a_64_lower("WRAPPED");
        long AUTHID = fnv1a_64_lower("AUTHID");
        long UNDER = fnv1a_64_lower("UNDER");
        long USERENV = fnv1a_64_lower("USERENV");
        long NUMTODSINTERVAL = fnv1a_64_lower("NUMTODSINTERVAL");

        long LATERAL = fnv1a_64_lower("LATERAL");
        long NONE = fnv1a_64_lower("NONE");
        long PARTITIONING = fnv1a_64_lower("PARTITIONING");
        long VALIDPROC = fnv1a_64_lower("VALIDPROC");
        long COMPRESS = fnv1a_64_lower("COMPRESS");
        long YES = fnv1a_64_lower("YES");
        long WMSYS = fnv1a_64_lower("WMSYS");

        long DEPTH = fnv1a_64_lower("DEPTH");
        long BREADTH = fnv1a_64_lower("BREADTH");

        long SCHEDULE = fnv1a_64_lower("SCHEDULE");
        long COMPLETION = fnv1a_64_lower("COMPLETION");
        long RENAME = fnv1a_64_lower("RENAME");
        long AT = fnv1a_64_lower("AT");
        long LANGUAGE = fnv1a_64_lower("LANGUAGE");
        long LOGFILE = fnv1a_64_lower("LOGFILE");
        long LOG = fnv1a_64_lower("LOG");
        long INITIAL_SIZE = fnv1a_64_lower("INITIAL_SIZE");
        long MAX_SIZE = fnv1a_64_lower("MAX_SIZE");
        long NODEGROUP = fnv1a_64_lower("NODEGROUP");
        long EXTENT_SIZE = fnv1a_64_lower("EXTENT_SIZE");
        long AUTOEXTEND_SIZE = fnv1a_64_lower("AUTOEXTEND_SIZE");
        long FILE_BLOCK_SIZE = fnv1a_64_lower("FILE_BLOCK_SIZE");
        long BLOCK_SIZE = fnv1a_64_lower("BLOCK_SIZE");
        long REPLICA_NUM = fnv1a_64_lower("REPLICA_NUM");
        long TABLET_SIZE = fnv1a_64_lower("TABLET_SIZE");
        long PCTFREE = fnv1a_64_lower("PCTFREE");
        long USE_BLOOM_FILTER = fnv1a_64_lower("USE_BLOOM_FILTER");
        long SERVER = fnv1a_64_lower("SERVER");
        long HOST = fnv1a_64_lower("HOST");
        long ADD = fnv1a_64_lower("ADD");
        long REMOVE = fnv1a_64_lower("REMOVE");
        long MOVE = fnv1a_64_lower("MOVE");
        long ALGORITHM = fnv1a_64_lower("ALGORITHM");
        long LINEAR = fnv1a_64_lower("LINEAR");
        long EVERY = fnv1a_64_lower("EVERY");
        long STARTS = fnv1a_64_lower("STARTS");
        long ENDS = fnv1a_64_lower("ENDS");
        long BINARY = fnv1a_64_lower("BINARY");
        long GEOMETRY = fnv1a_64_lower("GEOMETRY");
        long ISOPEN = fnv1a_64_lower("ISOPEN");
        long CONFLICT = fnv1a_64_lower("CONFLICT");
        long NOTHING = fnv1a_64_lower("NOTHING");
        long COMMIT = fnv1a_64_lower("COMMIT");
        long DESCRIBE = fnv1a_64_lower("DESCRIBE");
        long SQLXML = fnv1a_64_lower("SQLXML");
        long BIT = fnv1a_64_lower("BIT");
        long LONGBLOB = fnv1a_64_lower("LONGBLOB");

        long RS = fnv1a_64_lower("RS");
        long RR = fnv1a_64_lower("RR");
        long CS = fnv1a_64_lower("CS");
        long UR = fnv1a_64_lower("UR");

        long INT4 = fnv1a_64_lower("INT4");
        long VARBIT = fnv1a_64_lower("VARBIT");
        long DECODE = fnv1a_64_lower("DECODE");
        long IF = fnv1a_64_lower("IF");
        long EXTERNAL = fnv1a_64_lower("EXTERNAL");
        long SORTED = fnv1a_64_lower("SORTED");
        long CLUSTERED = fnv1a_64_lower("CLUSTERED");
        long LIFECYCLE = fnv1a_64_lower("LIFECYCLE");
        long LOCATION = fnv1a_64_lower("LOCATION");
        long PARTITIONS = fnv1a_64_lower("PARTITIONS");
        long FORMAT = fnv1a_64_lower("FORMAT");
        long ENCODE = fnv1a_64_lower("ENCODE");

        long SELECT = fnv1a_64_lower("SELECT");
        long DELETE = fnv1a_64_lower("DELETE");
        long UPDATE = fnv1a_64_lower("UPDATE");
        long INSERT = fnv1a_64_lower("INSERT");
        long REPLACE = fnv1a_64_lower("REPLACE");
        long TRUNCATE = fnv1a_64_lower("TRUNCATE");
        long CREATE = fnv1a_64_lower("CREATE");
        long MERGE = fnv1a_64_lower("MERGE");
        long SHOW = fnv1a_64_lower("SHOW");
        long ALTER = fnv1a_64_lower("ALTER");
        long DESC = fnv1a_64_lower("DESC");
        long SET = fnv1a_64_lower("SET");
        long KILL = fnv1a_64_lower("KILL");
        long MSCK = fnv1a_64_lower("MSCK");
        long USE = fnv1a_64_lower("USE");
        long ROLLBACK = fnv1a_64_lower("ROLLBACK");
        long GRANT = fnv1a_64_lower("GRANT");
        long REVOKE = fnv1a_64_lower("REVOKE");
        long DROP = fnv1a_64_lower("DROP");
        long USER = fnv1a_64_lower("USER");

        long USAGE = fnv1a_64_lower("USAGE");
        long PCTUSED = fnv1a_64_lower("PCTUSED");
        long OPAQUE = fnv1a_64_lower("OPAQUE");
        long INHERITS = fnv1a_64_lower("INHERITS");
        long DELIMITED = fnv1a_64_lower("DELIMITED");
        long ARRAY = fnv1a_64_lower("ARRAY");
        long SCALAR = fnv1a_64_lower("SCALAR");
        long STRUCT = fnv1a_64_lower("STRUCT");
        long UNIONTYPE = fnv1a_64_lower("UNIONTYPE");

        long TDDL = fnv1a_64_lower("TDDL");
        long CONCURRENTLY = fnv1a_64_lower("CONCURRENTLY");
        long TABLES = fnv1a_64_lower("TABLES");
        long NOCACHE = fnv1a_64_lower("NOCACHE");
        long NOPARALLEL = fnv1a_64_lower("NOPARALLEL");
        long EXIST = fnv1a_64_lower("EXIST");
        long EXISTS = fnv1a_64_lower("EXISTS");
        long SOUNDS = fnv1a_64_lower("SOUNDS");
        long TBLPROPERTIES = fnv1a_64_lower("TBLPROPERTIES");
        long TABLEGROUP = fnv1a_64_lower("TABLEGROUP");
        long TABLEGROUPS = fnv1a_64_lower("TABLEGROUPS");
        long DIMENSION = fnv1a_64_lower("DIMENSION");
        long OPTIONS = fnv1a_64_lower("OPTIONS");
        long OPTIMIZER = fnv1a_64_lower("OPTIMIZER");

        long FULLTEXT = fnv1a_64_lower("FULLTEXT");
        long SPATIAL = fnv1a_64_lower("SPATIAL");

        long SUBPARTITION_AVAILABLE_PARTITION_NUM = fnv1a_64_lower("SUBPARTITION_AVAILABLE_PARTITION_NUM");
        long EXTRA = fnv1a_64_lower("EXTRA");
        long DATABASES = fnv1a_64_lower("DATABASES");
        long COLUMNS = fnv1a_64_lower("COLUMNS");
        long PROCESS = fnv1a_64_lower("PROCESS");
        long PROCESSLIST = fnv1a_64_lower("PROCESSLIST");
        long MPP = fnv1a_64_lower("MPP");
        long SERDE = fnv1a_64_lower("SERDE");
        long SORT = fnv1a_64_lower("SORT");
        long ZORDER = fnv1a_64_lower("ZORDER");
        long FIELDS = fnv1a_64_lower("FIELDS");
        long COLLECTION = fnv1a_64_lower("COLLECTION");
        long SKEWED = fnv1a_64_lower("SKEWED");
        long SYMBOL = fnv1a_64_lower("SYMBOL");
        long LOAD = fnv1a_64_lower("LOAD");
        long VIEWS = fnv1a_64_lower("VIEWS");
        long SUBSTR = fnv1a_64_lower("SUBSTR");
        long TO_BASE64 = fnv1a_64_lower("TO_BASE64");
        long REGEXP_SUBSTR = fnv1a_64_lower("REGEXP_SUBSTR");
        long REGEXP_COUNT = fnv1a_64_lower("REGEXP_COUNT");
        long REGEXP_EXTRACT = fnv1a_64_lower("REGEXP_EXTRACT");
        long REGEXP_EXTRACT_ALL = fnv1a_64_lower("REGEXP_EXTRACT_ALL");
        long REGEXP_LIKE = fnv1a_64_lower("REGEXP_LIKE");
        long REGEXP_REPLACE = fnv1a_64_lower("REGEXP_REPLACE");
        long REGEXP_SPLIT = fnv1a_64_lower("REGEXP_SPLIT");
        long CONCAT = fnv1a_64_lower("CONCAT");
        long LCASE = fnv1a_64_lower("LCASE");
        long UCASE = fnv1a_64_lower("UCASE");
        long LOWER = fnv1a_64_lower("LOWER");
        long UPPER = fnv1a_64_lower("UPPER");
        long LENGTH = fnv1a_64_lower("LENGTH");
        long LOCATE = fnv1a_64_lower("LOCATE");
        long UDF_SYS_ROWCOUNT = fnv1a_64_lower("UDF_SYS_ROWCOUNT");
        long CHAR_LENGTH = fnv1a_64_lower("CHAR_LENGTH");
        long CHARACTER_LENGTH = fnv1a_64_lower("CHARACTER_LENGTH");
        long SUBSTRING = fnv1a_64_lower("SUBSTRING");
        long SUBSTRING_INDEX = fnv1a_64_lower("SUBSTRING_INDEX");
        long LEFT = fnv1a_64_lower("LEFT");
        long RIGHT = fnv1a_64_lower("RIGHT");
        long RTRIM = fnv1a_64_lower("RTRIM");
        long LEN = fnv1a_64_lower("LEN");
        long GREAST = fnv1a_64_lower("GREAST");
        long LEAST = fnv1a_64_lower("LEAST");
        long IFNULL = fnv1a_64_lower("IFNULL");
        long NULLIF = fnv1a_64_lower("NULLIF");
        long GREATEST = fnv1a_64_lower("GREATEST");
        long COALESCE = fnv1a_64_lower("COALESCE");
        long ISNULL = fnv1a_64_lower("ISNULL");
        long NVL = fnv1a_64_lower("NVL");
        long NVL2 = fnv1a_64_lower("NVL2");
        long TO_DATE = fnv1a_64_lower("TO_DATE");
        long DATEADD = fnv1a_64_lower("DATEADD");
        long DATE_ADD = fnv1a_64_lower("DATE_ADD");
        long ADDDATE = fnv1a_64_lower("ADDDATE");
        long DATE_SUB = fnv1a_64_lower("DATE_SUB");
        long SUBDATE = fnv1a_64_lower("SUBDATE");
        long DATE_PARSE = fnv1a_64_lower("DATE_PARSE");
        long STR_TO_DATE = fnv1a_64_lower("STR_TO_DATE");
        long CLOTHES_FEATURE_EXTRACT_V1 = fnv1a_64_lower("CLOTHES_FEATURE_EXTRACT_V1");
        long CLOTHES_ATTRIBUTE_EXTRACT_V1 = fnv1a_64_lower("CLOTHES_ATTRIBUTE_EXTRACT_V1");

        long GENERIC_FEATURE_EXTRACT_V1 = fnv1a_64_lower("GENERIC_FEATURE_EXTRACT_V1");
        long FACE_FEATURE_EXTRACT_V1 = fnv1a_64_lower("FACE_FEATURE_EXTRACT_V1");
        long TEXT_FEATURE_EXTRACT_V1 = fnv1a_64_lower("TEXT_FEATURE_EXTRACT_V1");

        long JSON_TABLE = fnv1a_64_lower("JSON_TABLE");
        long JSON_EXTRACT = fnv1a_64_lower("JSON_EXTRACT");
        long JSON_EXTRACT_SCALAR = fnv1a_64_lower("json_extract_scalar");
        long JSON_ARRAY_GET = fnv1a_64_lower("JSON_ARRAY_GET");
        long ADD_MONTHS = fnv1a_64_lower("ADD_MONTHS");
        long ABS = fnv1a_64_lower("ABS");
        long ACOS = fnv1a_64_lower("ACOS");
        long ASIN = fnv1a_64_lower("ASIN");
        long ATAN = fnv1a_64_lower("ATAN");
        long ATAN2 = fnv1a_64_lower("ATAN2");
        long COS = fnv1a_64_lower("COS");
        long FLOOR = fnv1a_64_lower("FLOOR");
        long CEIL = fnv1a_64_lower("CEIL");
        long SQRT = fnv1a_64_lower("SQRT");
        long LEAD = fnv1a_64_lower("LEAD");
        long LAG = fnv1a_64_lower("LAG");
        long CEILING = fnv1a_64_lower("CEILING");
        long POWER = fnv1a_64_lower("POWER");
        long EXP = fnv1a_64_lower("EXP");
        long LN = fnv1a_64_lower("LN");
        long LOG10 = fnv1a_64_lower("LOG10");
        long INTERVAL = fnv1a_64_lower("INTERVAL");
        long FROM_DAYS = fnv1a_64_lower("FROM_DAYS");
        long TO_DAYS = fnv1a_64_lower("TO_DAYS");

        long BIGINT = fnv1a_64_lower("BIGINT");
        long LONGLONG = fnv1a_64_lower("LONGLONG");
        long DISCARD = fnv1a_64_lower("DISCARD");
        long EXCHANGE = fnv1a_64_lower("EXCHANGE");
        long ROLE = fnv1a_64_lower("ROLE");
        long OVERWRITE = fnv1a_64_lower("OVERWRITE");
        long NO = fnv1a_64_lower("NO");
        long CATALOG = fnv1a_64_lower("CATALOG");
        long CATALOGS = fnv1a_64_lower("CATALOGS");
        long FUNCTIONS = fnv1a_64_lower("FUNCTIONS");
        long SCHEMAS = fnv1a_64_lower("SCHEMAS");
        long CHANGE = fnv1a_64_lower("CHANGE");
        long MODIFY = fnv1a_64_lower("MODIFY");
        long BEGIN = fnv1a_64_lower("BEGIN");
        long PATH = fnv1a_64_lower("PATH");
        long ENCRYPTION = fnv1a_64_lower("ENCRYPTION");
        long COMPRESSION = fnv1a_64_lower("COMPRESSION");
        long KEY_BLOCK_SIZE = fnv1a_64_lower("KEY_BLOCK_SIZE");
        long CHECKSUM = fnv1a_64_lower("CHECKSUM");
        long CONNECTION = fnv1a_64_lower("CONNECTION");
        long DATASOURCES = fnv1a_64_lower("DATASOURCES");
        long NODE = fnv1a_64_lower("NODE");
        long HELP = fnv1a_64_lower("HELP");
        long BROADCASTS = fnv1a_64_lower("BROADCASTS");
        long MASTER = fnv1a_64_lower("MASTER");
        long SLAVE = fnv1a_64_lower("SLAVE");
        long SQL_DELAY_CUTOFF = fnv1a_64_lower("SQL_DELAY_CUTOFF");
        long SOCKET_TIMEOUT = fnv1a_64_lower("SOCKET_TIMEOUT");
        long FORBID_EXECUTE_DML_ALL = fnv1a_64_lower("FORBID_EXECUTE_DML_ALL");
        long SCAN = fnv1a_64_lower("SCAN");
        long NOLOGFILE = fnv1a_64_lower("NOLOGFILE");
        long NOBADFILE = fnv1a_64_lower("NOBADFILE");
        long TERMINATED = fnv1a_64_lower("TERMINATED");
        long LTRIM = fnv1a_64_lower("LTRIM");
        long MISSING = fnv1a_64_lower("MISSING");
        long SUBPARTITION = fnv1a_64_lower("SUBPARTITION");
        long SUBPARTITIONS = fnv1a_64_lower("SUBPARTITIONS");
        long GENERATED = fnv1a_64_lower("GENERATED");
        long ALWAYS = fnv1a_64_lower("ALWAYS");
        long VISIBLE = fnv1a_64_lower("VISIBLE");
        long INCLUDING = fnv1a_64_lower("INCLUDING");
        long EXCLUDING = fnv1a_64_lower("EXCLUDING");
        long ROUTINE = fnv1a_64_lower("ROUTINE");
        long IDENTIFIED = fnv1a_64_lower("IDENTIFIED");
        long DELIMITER = fnv1a_64_lower("DELIMITER");
        long UNKNOWN = fnv1a_64_lower("UNKNOWN");
        long WEIGHT_STRING = fnv1a_64_lower("WEIGHT_STRING");
        long REVERSE = fnv1a_64_lower("REVERSE");
        long DATE_FORMAT = fnv1a_64_lower("DATE_FORMAT");
        long DAY_OF_WEEK = fnv1a_64_lower("DAY_OF_WEEK");
        long DATEDIFF = fnv1a_64_lower("DATEDIFF");
        long GET_FORMAT = fnv1a_64_lower("GET_FORMAT");
        long TIMESTAMPDIFF = fnv1a_64_lower("TIMESTAMPDIFF");
        long MONTHNAME = fnv1a_64_lower("MONTHNAME");
        long PERIOD_ADD = fnv1a_64_lower("PERIOD_ADD");
        long PERIOD_DIFF = fnv1a_64_lower("PERIOD_DIFF");
        long ROUND = fnv1a_64_lower("ROUND");
        long DBPARTITION = fnv1a_64_lower("DBPARTITION");
        long TBPARTITION = fnv1a_64_lower("TBPARTITION");
        long EXTPARTITION = fnv1a_64_lower("EXTPARTITION");
        long STARTWITH = fnv1a_64_lower("STARTWITH");
        long TBPARTITIONS = fnv1a_64_lower("TBPARTITIONS");
        long DBPARTITIONS = fnv1a_64_lower("DBPARTITIONS");
        long PARTITIONED = fnv1a_64_lower("PARTITIONED");
        long PARALLEL = fnv1a_64_lower("PARALLEL");
        long ALLOW = fnv1a_64_lower("ALLOW");
        long DISALLOW = fnv1a_64_lower("DISALLOW");
        long PIVOT = fnv1a_64_lower("PIVOT");
        long MODEL = fnv1a_64_lower("MODEL");
        long KEEP = fnv1a_64_lower("KEEP");
        long REFERENCE = fnv1a_64_lower("REFERENCE");
        long RETURN = fnv1a_64_lower("RETURN");
        long RETURNS = fnv1a_64_lower("RETURNS");
        long ROWTYPE = fnv1a_64_lower("ROWTYPE");
        long WINDOW = fnv1a_64_lower("WINDOW");
        long MULTIVALUE = fnv1a_64_lower("MULTIVALUE");
        long OPTIONALLY = fnv1a_64_lower("OPTIONALLY");
        long ENCLOSED = fnv1a_64_lower("ENCLOSED");
        long ESCAPED = fnv1a_64_lower("ESCAPED");
        long ESCAPE = fnv1a_64_lower("ESCAPE");
        long LINES = fnv1a_64_lower("LINES");
        long STARTING = fnv1a_64_lower("STARTING");
        long DISTRIBUTE = fnv1a_64_lower("DISTRIBUTE");
        long DISTRIBUTED = fnv1a_64_lower("DISTRIBUTED");
        long CLUSTER = fnv1a_64_lower("CLUSTER");
        long RUNNING = fnv1a_64_lower("RUNNING");
        long CLUSTERING = fnv1a_64_lower("CLUSTERING");
        long PCTVERSION = fnv1a_64_lower("PCTVERSION");
        long IDENTITY = fnv1a_64_lower("IDENTITY");
        long INCREMENT = fnv1a_64_lower("INCREMENT");
        long MINVALUE = fnv1a_64_lower("MINVALUE");
        long ANN = fnv1a_64_lower("ANN");
        long ANN_DISTANCE = fnv1a_64_lower("ANN_DISTANCE");
        long SUPPLEMENTAL = fnv1a_64_lower("SUPPLEMENTAL");
        long SUBSTITUTABLE = fnv1a_64_lower("SUBSTITUTABLE");
        long BASICFILE = fnv1a_64_lower("BASICFILE");
        long IN_MEMORY_METADATA = fnv1a_64_lower("IN_MEMORY_METADATA");
        long CURSOR_SPECIFIC_SEGMENT = fnv1a_64_lower("CURSOR_SPECIFIC_SEGMENT");
        long DEFER = fnv1a_64_lower("DEFER");
        long UNDO_LOG_LIMIT = fnv1a_64_lower("UNDO_LOG_LIMIT");
        long DBPROPERTIES = fnv1a_64_lower("DBPROPERTIES");
        long ANNINDEX = fnv1a_64_lower("ANNINDEX");
        long RTTYPE = fnv1a_64_lower("RTTYPE");
        long DISTANCE = fnv1a_64_lower("DISTANCE");
        long IDXPROPERTIES = fnv1a_64_lower("IDXPROPERTIES");
        long RECOVER = fnv1a_64_lower("RECOVER");
        long BACKUP = fnv1a_64_lower("BACKUP");
        long RESTORE = fnv1a_64_lower("RESTORE");
        long NOSCAN = fnv1a_64_lower("NOSCAN");
        long EXTENDED = fnv1a_64_lower("EXTENDED");
        long FORMATTED = fnv1a_64_lower("FORMATTED");
        long DEPENDENCY = fnv1a_64_lower("DEPENDENCY");
        long AUTHORIZATION = fnv1a_64_lower("AUTHORIZATION");
        long ANALYZE = fnv1a_64_lower("ANALYZE");
        long EXPORT = fnv1a_64_lower("EXPORT");
        long IMPORT = fnv1a_64_lower("IMPORT");
        long TABLESAMPLE = fnv1a_64_lower("TABLESAMPLE");
        long BUCKET = fnv1a_64_lower("BUCKET");
        long BUCKETS = fnv1a_64_lower("BUCKETS");
        long UNARCHIVE = fnv1a_64_lower("UNARCHIVE");
        long SEQUENCES = fnv1a_64_lower("SEQUENCES");
        long OUTLINE = fnv1a_64_lower("OUTLINE");
        long ORD = fnv1a_64_lower("ORD");
        long SPACE = fnv1a_64_lower("SPACE");
        long REPEAT = fnv1a_64_lower("REPEAT");
        long SLOW = fnv1a_64_lower("SLOW");
        long PLAN = fnv1a_64_lower("PLAN");
        long PLANCACHE = fnv1a_64_lower("PLANCACHE");
        long RECYCLEBIN = fnv1a_64_lower("RECYCLEBIN");
        long PURGE = fnv1a_64_lower("PURGE");
        long FLASHBACK = fnv1a_64_lower("FLASHBACK");
        long INPUTFORMAT = fnv1a_64_lower("INPUTFORMAT");
        long OUTPUTFORMAT = fnv1a_64_lower("OUTPUTFORMAT");
        long DUMP = fnv1a_64_lower("DUMP");
        long BROADCAST = fnv1a_64_lower("BROADCAST");
        long GROUP = fnv1a_64_lower("GROUP");
        long GROUPING = fnv1a_64_lower("GROUPING");
        long WITH = fnv1a_64_lower("WITH");
        long WHO = fnv1a_64_lower("WHO");
        long GRANTS = fnv1a_64_lower("GRANTS");
        long STATISTIC = fnv1a_64_lower("STATISTIC");
        long STATISTIC_LIST = fnv1a_64_lower("STATISTIC_LIST");
        long STATUS = fnv1a_64_lower("STATUS");
        long FULL = fnv1a_64_lower("FULL");
        long STATS = fnv1a_64_lower("STATS");
        long OUTLINES = fnv1a_64_lower("OUTLINES");
        long VERSION = fnv1a_64_lower("VERSION");
        long CONFIG = fnv1a_64_lower("CONFIG");
        long USERS = fnv1a_64_lower("USERS");
        long PHYSICAL_PROCESSLIST = fnv1a_64_lower("PHYSICAL_PROCESSLIST");
        long PHYSICAL = fnv1a_64_lower("PHYSICAL");
        long DISTANCEMEASURE = fnv1a_64_lower("DISTANCEMEASURE");
        long UNIT = fnv1a_64_lower("UNIT");
        long DB = fnv1a_64_lower("DB");
        long STEP = fnv1a_64_lower("STEP");
        long HEX = fnv1a_64_lower("HEX");
        long UNHEX = fnv1a_64_lower("UNHEX");
        long POLICY = fnv1a_64_lower("POLICY");
        long QUERY_TASK = fnv1a_64_lower("QUERY_TASK");
        long UUID = fnv1a_64_lower("UUID");
        long PCTTHRESHOLD = fnv1a_64_lower("PCTTHRESHOLD");
        long UNUSABLE = fnv1a_64_lower("UNUSABLE");
        long FILTER = fnv1a_64_lower("FILTER");
        long BIT_COUNT = fnv1a_64_lower("BIT_COUNT");
        long STDDEV_SAMP = fnv1a_64_lower("STDDEV_SAMP");
        long PERCENT_RANK = fnv1a_64_lower("PERCENT_RANK");
        long DENSE_RANK = fnv1a_64_lower("DENSE_RANK");
        long CUME_DIST = fnv1a_64_lower("CUME_DIST");
        long CARDINALITY = fnv1a_64_lower("CARDINALITY");
        long TRY_CAST = fnv1a_64_lower("TRY_CAST");
        long COVERING = fnv1a_64_lower("COVERING");

        long CHARFILTER = fnv1a_64_lower("CHARFILTER");
        long CHARFILTERS = fnv1a_64_lower("CHARFILTERS");
        long TOKENIZER = fnv1a_64_lower("TOKENIZER");
        long TOKENIZERS = fnv1a_64_lower("TOKENIZERS");
        long TOKENFILTER = fnv1a_64_lower("TOKENFILTER");
        long TOKENFILTERS = fnv1a_64_lower("TOKENFILTERS");
        long ANALYZER = fnv1a_64_lower("ANALYZER");
        long ANALYZERS = fnv1a_64_lower("ANALYZERS");
        long DICTIONARY = fnv1a_64_lower("DICTIONARY");
        long DICTIONARIES = fnv1a_64_lower("DICTIONARIES");
        long QUERY = fnv1a_64_lower("QUERY");
        long META = fnv1a_64_lower("META");
        long TRY = fnv1a_64_lower("TRY");
        long D = fnv1a_64_lower("D");
        long T = fnv1a_64_lower("T");
        long TS = fnv1a_64_lower("TS");
        long FN = fnv1a_64_lower("FN");
        long COPY = fnv1a_64_lower("COPY");
        long CREDENTIALS = fnv1a_64_lower("CREDENTIALS");
        long ACCESS_KEY_ID = fnv1a_64_lower("ACCESS_KEY_ID");
        long ACCESS_KEY_SECRET = fnv1a_64_lower("ACCESS_KEY_SECRET");
        long BERNOULLI = fnv1a_64_lower("BERNOULLI");
        long SYSTEM = fnv1a_64_lower("SYSTEM");
        long SYNC = fnv1a_64_lower("SYNC");
        long INIT = fnv1a_64_lower("INIT");
        long BD = fnv1a_64_lower("BD");
        long FORMAT_DATETIME = fnv1a_64_lower("FORMAT_DATETIME");
        long WITHIN = fnv1a_64_lower("WITHIN");
        long RULE = fnv1a_64_lower("RULE");
        long EXPLAIN = fnv1a_64_lower("EXPLAIN");
        long ISOLATION = fnv1a_64_lower("ISOLATION");
        long READ = fnv1a_64_lower("READ");
        long UNCOMMITTED = fnv1a_64_lower("UNCOMMITTED");
        long COMMITTED = fnv1a_64_lower("COMMITTED");
        long REPEATABLE = fnv1a_64_lower("REPEATABLE");
        long SERIALIZABLE = fnv1a_64_lower("SERIALIZABLE");

        long _LATIN1 = fnv1a_64_lower("_LATIN1");
        long _GBK = fnv1a_64_lower("_GBK");
        long _BIG5 = fnv1a_64_lower("_BIG5");
        long _UTF8 = fnv1a_64_lower("_UTF8");
        long _UTF8MB4 = fnv1a_64_lower("_UTF8MB4");
        long _UTF16 = fnv1a_64_lower("_UTF16");
        long _UTF16LE = fnv1a_64_lower("_UTF16LE");
        long _UTF32 = fnv1a_64_lower("_UTF32");
        long _UCS2 = fnv1a_64_lower("_UCS2");
        long _UJIS = fnv1a_64_lower("_UJIS");
        long X = fnv1a_64_lower("X");
        long TRANSFORM = fnv1a_64_lower("TRANSFORM");
        long NESTED = fnv1a_64_lower("NESTED");
        long RESTART = fnv1a_64_lower("RESTART");

        long ASOF = fnv1a_64_lower("ASOF");
        long JSON_SET = fnv1a_64_lower("JSON_SET");
        long JSONB_SET = fnv1a_64_lower("JSONB_SET");

        long TUNNEL = fnv1a_64_lower("TUNNEL");
        long DOWNLOAD = fnv1a_64_lower("DOWNLOAD");
        long UPLOAD = fnv1a_64_lower("UPLOAD");
    }
}
