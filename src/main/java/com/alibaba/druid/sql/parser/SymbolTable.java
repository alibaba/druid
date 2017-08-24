package com.alibaba.druid.sql.parser;

/**
 * @author wenshao[szujobs@hotmail.com]
 */
public class SymbolTable {
    public static SymbolTable global = new SymbolTable(8192);

    private final Entry[] entries;
    private final int      indexMask;

    public SymbolTable(int tableSize){
        this.indexMask = tableSize - 1;
        this.entries = new Entry[tableSize];

//        this.addSymbol("id", 0, 2, "id".hashCode());
    }



    public String addSymbol(String buffer, int offset, int len, long hash) {
        final int bucket = ((int) hash) & indexMask;

        Entry entry = entries[bucket];
        if (entry != null) {
            if (hash == entry.hash //
                    && len == entry.len) {
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

    private static String subString(String src, int offset, int len) {
        char[] chars = new char[len];
        src.getChars(offset, offset + len, chars, 0);
        return new String(chars);
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