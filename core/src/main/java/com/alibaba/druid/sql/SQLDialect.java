package com.alibaba.druid.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.Utils;

import java.util.*;

/**
 * @since 1.2.25
 */
public class SQLDialect {
    private int quoteChars;
    private final DbType dbType;
    private final Keyword keywords;
    private final Keyword aliasKeyword;
    private final Keyword builtInFunctions;
    private final Keyword builtInTables;
    private final Keyword builtInDataTypes;

    private SQLDialect(
            DbType dbType,
            List<Quote> quotes,
            Keyword keywords,
            Keyword aliasKeyword,
            Keyword builtInDataTypes,
            Keyword builtInFunctions,
            Keyword builtInTables
    ) {
        this.dbType = dbType;
        this.keywords = keywords;
        this.aliasKeyword = aliasKeyword;
        this.builtInDataTypes = builtInDataTypes;
        this.builtInFunctions = builtInFunctions;
        this.builtInTables = builtInTables;
        this.quoteChars = DEFAULT_QUOTE_INT;
        for (Quote quote : quotes) {
            quoteChars = Quote.register(quoteChars, quote);
        }
    }

    public static final int DEFAULT_QUOTE_INT = 0;
    public void dumpBuiltInDataTypes(Collection<String> dataTypes) {
        builtInDataTypes.dumpNames(dataTypes);
    }

    public DbType getDbType() {
        return dbType;
    }

    public int getQuoteChars() {
        return quoteChars;
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

        List<Quote> quoteChars = new ArrayList<>();
        {
            String quotes = props.getProperty("quote");
            if (quotes != null) {
                for (String quote : quotes.split(",")) {
                    if (quote != null && quote.length() == 1) {
                        if (Quote.of(quote.charAt(0)) != null) {
                            quoteChars.add(Quote.of(quote.charAt(0)));
                        }
                    }
                }
            }
        }
        if (quoteChars.isEmpty()) {
            quoteChars.add(Quote.DOUBLE_QUOTE);
        }
        return new SQLDialect(
                dbType,
                quoteChars,
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

    public enum Quote {
        SINGLE_QUOTE('\''),
        DOUBLE_QUOTE('"'),
        BACK_QUOTE('`'),
        BRACKETS_QUOTE('[');
        public final int mask;
        public final char sign;
        private Quote(char sign) {
            this.mask = 1 << ordinal();
            this.sign = sign;
        }

        public static int register(int features, Quote quote) {
            return features | quote.mask;
        }

        public static Quote of(char sign) {
            if (sign == '[') {
                return BRACKETS_QUOTE;
            } else if (sign == '\'') {
                return SINGLE_QUOTE;
            } else if (sign == '"') {
                return DOUBLE_QUOTE;
            } else if (sign == '`') {
                return BACK_QUOTE;
            }
            return null;
        }
        public static boolean isValidQuota(int features, Quote quote) {
            if (quote == null) {
                return false;
            }
            return (features & quote.mask) != 0;
        }

        public static char getQuote(int features) {
            if ((features & Quote.BACK_QUOTE.mask) != 0) {
                return Quote.BACK_QUOTE.sign;
            } else if ((features & Quote.SINGLE_QUOTE.mask) != 0) {
                return Quote.SINGLE_QUOTE.sign;
            } else if ((features & Quote.DOUBLE_QUOTE.mask) != 0) {
                return Quote.DOUBLE_QUOTE.sign;
            } else if ((features & Quote.BRACKETS_QUOTE.mask) != 0) {
                return Quote.BRACKETS_QUOTE.sign;
            }
            return ' ';
        }
    }
}
