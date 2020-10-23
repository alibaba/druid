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
package com.alibaba.druid.sql.parser;

import java.nio.charset.Charset;

/**
 * @author wenshao[szujobs@hotmail.com]
 */
public class SymbolTable {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final boolean JVM_16;

    static {
        String version = null;
        try {
            version = System.getProperty("java.specification.version");
        } catch (IllegalArgumentException ex) {
            // skip
        } catch (SecurityException error) {
            // skip
        }
        JVM_16 = "1.6".equals(version);
    }

    public static SymbolTable global = new SymbolTable(32768);

    private final Entry[] entries;
    private final int      indexMask;

    public SymbolTable(int tableSize){
        this.indexMask = tableSize - 1;
        this.entries = new Entry[tableSize];
    }

    public String addSymbol(String buffer, int offset, int len, long hash) {
        final int bucket = ((int) hash) & indexMask;

        Entry entry = entries[bucket];
        if (entry != null) {
            if (hash == entry.hash) {
                return entry.value;
            }

            String str = JVM_16
                    ? subString(buffer, offset, len)
                    : buffer.substring(offset, offset + len);

            return str;
        }

        String str = JVM_16
                ? subString(buffer, offset, len)
                : buffer.substring(offset, offset + len);
        entry = new Entry(hash, len, str);
        entries[bucket] = entry;
        return str;
    }

    public String addSymbol(byte[] buffer, int offset, int len, long hash) {
        final int bucket = ((int) hash) & indexMask;

        Entry entry = entries[bucket];
        if (entry != null) {
            if (hash == entry.hash) {
                return entry.value;
            }

            String str = subString(buffer, offset, len);

            return str;
        }

        String str = subString(buffer, offset, len);
        entry = new Entry(hash, len, str);
        entries[bucket] = entry;
        return str;
    }

    public String addSymbol(String symbol, long hash) {
        final int bucket = ((int) hash) & indexMask;

        Entry entry = entries[bucket];
        if (entry != null) {
            if (hash == entry.hash) {
                return entry.value;
            }

            return symbol;
        }

        entry = new Entry(hash, symbol.length(), symbol);
        entries[bucket] = entry;
        return symbol;
    }

    public String findSymbol(long hash) {
        final int bucket = ((int) hash) & indexMask;
        Entry entry = entries[bucket];
        if (entry != null && entry.hash == hash) {
            return entry.value;
        }
        return null;
    }

    private static String subString(String src, int offset, int len) {
        char[] chars = new char[len];
        src.getChars(offset, offset + len, chars, 0);
        return new String(chars);
    }

    private static String subString(byte[] bytes, int from, int len) {
        byte[] strBytes = new byte[len];
        System.arraycopy(bytes, from, strBytes, 0, len);
        return new String(strBytes, UTF8);
    }

    private static class Entry {
        public final long hash;
        public final int len;
        public final String value;

        public Entry(long hash, int len, String value) {
            this.hash = hash;
            this.len = len;
            this.value = value;
        }
    }
}