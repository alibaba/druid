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
            return FnvHash.hashCode64(basic, name, 1, len - 1);
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
        long STRAIGHT_JOIN = fnv1a_64_lower("STRAIGHT_JOIN");
        long SQL_SMALL_RESULT = fnv1a_64_lower("SQL_SMALL_RESULT");
        long SQL_BIG_RESULT = fnv1a_64_lower("SQL_BIG_RESULT");
        long SQL_BUFFER_RESULT = fnv1a_64_lower("SQL_BUFFER_RESULT");
        long SQL_CACHE = fnv1a_64_lower("SQL_CACHE");
        long SQL_NO_CACHE = fnv1a_64_lower("SQL_NO_CACHE");
        long SQL_CALC_FOUND_ROWS = fnv1a_64_lower("SQL_CALC_FOUND_ROWS");
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
        long NO_WAIT = fnv1a_64_lower("NO_WAIT");
        long WAIT = fnv1a_64_lower("WAIT");
        long NOWAIT = fnv1a_64_lower("NOWAIT");
        long ERRORS = fnv1a_64_lower("ERRORS");
        long VALUE = fnv1a_64_lower("VALUE");
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
        long CHARSET = fnv1a_64_lower("CHARSET");
        long SEMI = fnv1a_64_lower("SEMI");
        long ANTI = fnv1a_64_lower("ANTI");
        long PRIOR = fnv1a_64_lower("PRIOR");
        long NOCYCLE = fnv1a_64_lower("NOCYCLE");
        long CONNECT_BY_ROOT = fnv1a_64_lower("CONNECT_BY_ROOT");
        long DATE = fnv1a_64_lower("DATE");
        long TIMESTAMP = fnv1a_64_lower("TIMESTAMP");
        long CURRENT = fnv1a_64_lower("CURRENT");
        long COUNT = fnv1a_64_lower("COUNT");
        long ROW_NUMBER = fnv1a_64_lower("ROW_NUMBER");
        long WM_CONAT = fnv1a_64_lower("WM_CONAT");
        long AVG = fnv1a_64_lower("AVG");
        long MAX = fnv1a_64_lower("MAX");
        long MIN = fnv1a_64_lower("MIN");
        long STDDEV = fnv1a_64_lower("STDDEV");
        long SUM = fnv1a_64_lower("SUM");
        long GROUP_CONCAT = fnv1a_64_lower("GROUP_CONCAT");
        long DEDUPLICATION = fnv1a_64_lower("DEDUPLICATION");
        long CONVERT = fnv1a_64_lower("CONVERT");
        long CHAR = fnv1a_64_lower("CHAR");
        long VARCHAR = fnv1a_64_lower("VARCHAR");
        long VARCHAR2 = fnv1a_64_lower("VARCHAR2");
        long NCHAR = fnv1a_64_lower("NCHAR");
        long NVARCHAR = fnv1a_64_lower("NVARCHAR");
        long NVARCHAR2 = fnv1a_64_lower("NVARCHAR2");
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
        long EXTRACT = fnv1a_64_lower("EXTRACT");
        long POSITION = fnv1a_64_lower("POSITION");
        long DUAL = fnv1a_64_lower("DUAL");
        long LEVEL = fnv1a_64_lower("LEVEL");
        long CONNECT_BY_ISCYCLE = fnv1a_64_lower("CONNECT_BY_ISCYCLE");
        long CURRENT_TIMESTAMP = fnv1a_64_lower("CURRENT_TIMESTAMP");
        long FALSE = fnv1a_64_lower("FALSE");
        long TRUE = fnv1a_64_lower("TRUE");
        long SET = fnv1a_64_lower("SET");
        long LESS = fnv1a_64_lower("LESS");
        long MAXVALUE = fnv1a_64_lower("MAXVALUE");
        long OFFSET = fnv1a_64_lower("OFFSET");
        long RAW = fnv1a_64_lower("RAW");
        long LONG = fnv1a_64_lower("LONG");
        long ROWNUM = fnv1a_64_lower("ROWNUM");
        long PRECISION = fnv1a_64_lower("PRECISION");
        long DOUBLE = fnv1a_64_lower("DOUBLE");
        long WITHOUT = fnv1a_64_lower("WITHOUT");

        long DEFINER = fnv1a_64_lower("DEFINER");
        long DETERMINISTIC = fnv1a_64_lower("DETERMINISTIC");
        long CONTAINS = fnv1a_64_lower("CONTAINS");
        long SQL = fnv1a_64_lower("SQL");
        long CALL = fnv1a_64_lower("CALL");
        long CHARACTER = fnv1a_64_lower("CHARACTER");

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
        long NULLS = fnv1a_64_lower("NULLS");
        long FIRST = fnv1a_64_lower("FIRST");
        long LAST = fnv1a_64_lower("LAST");
        long AUTO_INCREMENT = fnv1a_64_lower("AUTO_INCREMENT");
        long STORAGE = fnv1a_64_lower("STORAGE");
        long STORED = fnv1a_64_lower("STORED");
        long VIRTUAL = fnv1a_64_lower("VIRTUAL");
        long UNSIGNED = fnv1a_64_lower("UNSIGNED");
        long ZEROFILL = fnv1a_64_lower("ZEROFILL");
        long GLOBAL = fnv1a_64_lower("GLOBAL");
        long SESSION = fnv1a_64_lower("SESSION");
        long NAMES = fnv1a_64_lower("NAMES");
        long PARTIAL = fnv1a_64_lower("PARTIAL");
        long SIMPLE = fnv1a_64_lower("SIMPLE");
        long RESTRICT = fnv1a_64_lower("RESTRICT");
        long ON = fnv1a_64_lower("ON");
        long ACTION = fnv1a_64_lower("ACTION");
        long SEPARATOR = fnv1a_64_lower("SEPARATOR");
        long DATA = fnv1a_64_lower("DATA");
        long MAX_ROWS = fnv1a_64_lower("MAX_ROWS");
        long MIN_ROWS = fnv1a_64_lower("MIN_ROWS");
        long ENGINE = fnv1a_64_lower("ENGINE");
        long SKIP = fnv1a_64_lower("SKIP");
        long RECURSIVE = fnv1a_64_lower("RECURSIVE");
        long ROLLUP = fnv1a_64_lower("ROLLUP");
        long CUBE = fnv1a_64_lower("CUBE");

        long YEAR = fnv1a_64_lower("YEAR");
        long MONTH = fnv1a_64_lower("MONTH");
        long DAY = fnv1a_64_lower("DAY");
        long HOUR = fnv1a_64_lower("HOUR");
        long MINUTE = fnv1a_64_lower("MINUTE");
        long SECOND = fnv1a_64_lower("SECOND");

        long BEFORE = fnv1a_64_lower("BEFORE");
        long AFTER = fnv1a_64_lower("AFTER");
        long INSTEAD = fnv1a_64_lower("INSTEAD");
    }
}
