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

    public static long fnv_64(String input) {
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



    /**
     * lower & normalized & fnv_1a_64
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

    public static long fnv_64_lower(String key) {
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

    public static long fnv_64_lower(String owner, String name) {
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
}
