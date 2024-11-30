package com.alibaba.druid.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.Utils;

import java.util.*;

/**
 * @since 1.2.25
 */
public class SQLDialect {
    private final char quoteChar;
    private final DbType dbType;
    private final Keyword keywords;
    private final Keyword aliasKeyword;
    private final Keyword builtInFunctions;
    private final Keyword builtInTables;
    private final Keyword builtInDataTypes;

    private SQLDialect(
            DbType dbType,
            char quoteChar,
            Keyword keywords,
            Keyword aliasKeyword,
            Keyword builtInDataTypes,
            Keyword builtInFunctions,
            Keyword builtInTables
    ) {
        this.dbType = dbType;
        this.quoteChar = quoteChar;
        this.keywords = keywords;
        this.aliasKeyword = aliasKeyword;
        this.builtInDataTypes = builtInDataTypes;
        this.builtInFunctions = builtInFunctions;
        this.builtInTables = builtInTables;
    }

    public void dumpBuiltInDataTypes(Collection<String> dataTypes) {
        builtInDataTypes.dumpNames(dataTypes);
    }

    public DbType getDbType() {
        return dbType;
    }

    public char getQuoteChar() {
        return quoteChar;
    }

    public boolean isKeyword(String name) {
        return keywords.contains(name);
    }

    public boolean isAliasKeyword(String name) {
        return aliasKeyword.contains(name);
    }

    public boolean isBuiltInDataType(String name) {
        return builtInDataTypes.contains(name);
    }

    public boolean isBuiltInFunction(String name) {
        return builtInFunctions.contains(name);
    }

    public boolean isBuiltInTable(String name) {
        return builtInTables.contains(name);
    }

    public static SQLDialect of(DbType dbType) {
        String dir = "META-INF/druid/parser/".concat(dbType.name().toLowerCase());
        Properties props = Utils.loadProperties(dir.concat("/dialect.properties"));

        char quoteChar = '"';
        {
            String quote = props.getProperty("quote");
            if (quote != null && quote.length() == 1) {
                quoteChar = quote.charAt(0);
            }
        }

        return new SQLDialect(
                dbType,
                quoteChar,
                new Keyword(
                        Utils.readLines(dir.concat("/keywords"))),
                new Keyword(
                        Utils.readLines(dir.concat("/alias_keywords"))),
                new Keyword(
                        Utils.readLines(dir.concat("/builtin_datatypes"))),
                new Keyword(
                        Utils.readLines(dir.concat("/builtin_functions"))),
                new Keyword(
                        Utils.readLines(dir.concat("/builtin_tables")))
        );
    }

    private static final class Keyword {
        private final long[] hashes;
        private final String[] names;

        public Keyword(List<String> reservedKeywords) {
            Map<Long, String> map = new TreeMap<>();
            for (String keyword : reservedKeywords) {
                long hash = FnvHash.fnv1a_64_lower(keyword);
                map.put(hash, keyword);
            }

            int index = 0;
            long[] hashes = new long[map.size()];
            String[] names = new String[map.size()];
            for (Map.Entry<Long, String> entry : map.entrySet()) {
                hashes[index] = entry.getKey();
                names[index] = entry.getValue();
                index++;
            }
            this.hashes = hashes;
            this.names = names;
        }

        public boolean contains(String keyword) {
            long hash = FnvHash.fnv1a_64_lower(keyword);
            return Arrays.binarySearch(hashes, hash) >= 0;
        }

        public void dumpNames(Collection<String> names) {
            names.addAll(Arrays.asList(this.names));
        }
    }
}
