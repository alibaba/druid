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
}
