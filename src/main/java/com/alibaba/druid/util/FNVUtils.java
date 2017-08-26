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

public class FNVUtils {
    public final static long HIGH_PRIORITY = fnv_64_lower("HIGH_PRIORITY");
    public final static long DISTINCTROW = fnv_64_lower("DISTINCTROW");
    public final static long STRAIGHT_JOIN = fnv_64_lower("STRAIGHT_JOIN");
    public final static long SQL_SMALL_RESULT = fnv_64_lower("SQL_SMALL_RESULT");
    public final static long SQL_BIG_RESULT = fnv_64_lower("SQL_BIG_RESULT");
    public final static long SQL_BUFFER_RESULT = fnv_64_lower("SQL_BUFFER_RESULT");
    public final static long SQL_CACHE = fnv_64_lower("SQL_CACHE");
    public final static long SQL_NO_CACHE = fnv_64_lower("SQL_NO_CACHE");
    public final static long SQL_CALC_FOUND_ROWS = fnv_64_lower("SQL_CALC_FOUND_ROWS");
    public final static long OUTFILE = fnv_64_lower("OUTFILE");
    public final static long SETS = fnv_64_lower("SETS");
    public final static long REGEXP = fnv_64_lower("REGEXP");
    public final static long RLIKE = fnv_64_lower("RLIKE");
    public final static long USING = fnv_64_lower("USING");
    public final static long IGNORE = fnv_64_lower("IGNORE");
    public final static long FORCE = fnv_64_lower("FORCE");
    public final static long CROSS = fnv_64_lower("CROSS");
    public final static long NATURAL = fnv_64_lower("NATURAL");
    public final static long APPLY = fnv_64_lower("APPLY");
    public final static long CONNECT = fnv_64_lower("CONNECT");
    public final static long START = fnv_64_lower("START");
    public final static long BTREE = fnv_64_lower("BTREE");
    public final static long HASH = fnv_64_lower("HASH");
    public final static long NO_WAIT = fnv_64_lower("NO_WAIT");
    public final static long WAIT = fnv_64_lower("WAIT");
    public final static long NOWAIT = fnv_64_lower("NOWAIT");
    public final static long ERRORS = fnv_64_lower("ERRORS");
    public final static long VALUE = fnv_64_lower("VALUE");
    public final static long NEXT = fnv_64_lower("NEXT");
    public final static long LOW_PRIORITY = fnv_64_lower("LOW_PRIORITY");
    public final static long COMMIT_ON_SUCCESS = fnv_64_lower("COMMIT_ON_SUCCESS");
    public final static long ROLLBACK_ON_FAIL = fnv_64_lower("ROLLBACK_ON_FAIL");
    public final static long QUEUE_ON_PK = fnv_64_lower("QUEUE_ON_PK");
    public final static long TARGET_AFFECT_ROW = fnv_64_lower("TARGET_AFFECT_ROW");
    public final static long COLLATE = fnv_64_lower("COLLATE");
    public final static long BOOLEAN = fnv_64_lower("BOOLEAN");
    public final static long CHARSET = fnv_64_lower("CHARSET");
    public final static long SEMI = fnv_64_lower("SEMI");
    public final static long ANTI = fnv_64_lower("ANTI");
    public final static long PRIOR = fnv_64_lower("PRIOR");
    public final static long NOCYCLE = fnv_64_lower("NOCYCLE");
    public final static long CONNECT_BY_ROOT = fnv_64_lower("CONNECT_BY_ROOT");
    public final static long DATE = fnv_64_lower("DATE");
    public final static long CURRENT = fnv_64_lower("CURRENT");
    public final static long COUNT = fnv_64_lower("COUNT");
    public final static long AVG = fnv_64_lower("AVG");
    public final static long MAX = fnv_64_lower("MAX");
    public final static long MIN = fnv_64_lower("MIN");
    public final static long STDDEV = fnv_64_lower("STDDEV");
    public final static long SUM = fnv_64_lower("SUM");
    public final static long GROUP_CONCAT = fnv_64_lower("GROUP_CONCAT");
    public final static long DEDUPLICATION = fnv_64_lower("DEDUPLICATION");
    public final static long CONVERT = fnv_64_lower("CONVERT");
    public final static long CHAR = fnv_64_lower("CHAR");
    public final static long TRIM = fnv_64_lower("TRIM");
    public final static long LEADING = fnv_64_lower("LEADING");
    public final static long BOTH = fnv_64_lower("BOTH");
    public final static long TRAILING = fnv_64_lower("TRAILING");
    public final static long MOD = fnv_64_lower("MOD");
    public final static long MATCH = fnv_64_lower("MATCH");
    public final static long EXTRACT = fnv_64_lower("EXTRACT");
    public final static long POSITION = fnv_64_lower("POSITION");

    public static long fnv_64(String input) {
        if (input == null) {
            return 0;
        }

        long hash = 0xcbf29ce484222325L;
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            hash ^= c;
            hash *= 0x100000001b3L;
        }

        return hash;
    }

    public static long fnv_64_lower(String key) {
        long hashCode = 0xcbf29ce484222325L;
        for (int i = 0; i < key.length(); ++i) {
            char ch = key.charAt(i);

            if (ch >= 'A' && ch <= 'Z') {
                ch = (char) (ch + 32);
            }

            hashCode ^= ch;
            hashCode *= 0x100000001b3L;
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

    public static long[] fnv_64_lower(String[] strings, boolean sort) {
        long[] hashCodes = new long[strings.length];
        for (int i = 0; i < strings.length; i++) {
            hashCodes[i] = fnv_64_lower(strings[i]);
        }
        if (sort) {
            Arrays.sort(hashCodes);
        }
        return hashCodes;
    }
}
