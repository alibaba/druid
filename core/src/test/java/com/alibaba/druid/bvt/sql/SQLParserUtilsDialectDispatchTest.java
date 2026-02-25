package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SQLParserUtilsDialectDispatchTest {
    private static final String DIALECT = "mysql";

    @Test
    public void test_registeredProvider_hasPriority() {
        SQLParserUtils.unregisterDialectParserProvider(DIALECT);
        SQLParserUtils.registerDialectParserProvider(DIALECT, new MarkerProvider());
        try {
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser("select 1", DbType.mysql);
            assertTrue(parser instanceof MarkerStatementParser);
        } finally {
            SQLParserUtils.unregisterDialectParserProvider(DIALECT);
        }
    }

    @Test
    public void test_missingProvider_fallbackToBuiltinDispatch() {
        SQLParserUtils.unregisterDialectParserProvider(DIALECT);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser("select 1", DbType.mysql);
        assertEquals(MySqlStatementParser.class, parser.getClass());
    }

    @Test
    public void test_unregisterProvider_resumeBuiltinDispatch() {
        SQLParserUtils.unregisterDialectParserProvider(DIALECT);
        SQLParserUtils.registerDialectParserProvider(DIALECT, new MarkerProvider());
        SQLParserUtils.unregisterDialectParserProvider(DIALECT);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser("select 1", DbType.mysql);
        assertEquals(MySqlStatementParser.class, parser.getClass());
    }

    @Test
    public void test_concurrentRegisterAndLookup_keepValidProviderState() throws ExecutionException, InterruptedException {
        SQLParserUtils.unregisterDialectParserProvider(DIALECT);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
            tasks.add(new Callable<Void>() {
                @Override
                public Void call() {
                    for (int i = 0; i < 200; i++) {
                        SQLParserUtils.registerDialectParserProvider(DIALECT, new MarkerProvider());
                    }
                    return null;
                }
            });
            tasks.add(new Callable<Void>() {
                @Override
                public Void call() {
                    for (int i = 0; i < 200; i++) {
                        SQLParserUtils.unregisterDialectParserProvider(DIALECT);
                    }
                    return null;
                }
            });
            tasks.add(new Callable<Void>() {
                @Override
                public Void call() {
                    for (int i = 0; i < 400; i++) {
                        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser("select 1", DbType.mysql);
                        assertNotNull(parser);
                        assertTrue(parser instanceof MarkerStatementParser || parser instanceof MySqlStatementParser);
                    }
                    return null;
                }
            });

            List<Future<Void>> futures = executor.invokeAll(tasks);
            for (Future<Void> future : futures) {
                future.get();
            }
        } finally {
            SQLParserUtils.unregisterDialectParserProvider(DIALECT);
            executor.shutdownNow();
        }

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser("select 1", DbType.mysql);
        assertEquals(MySqlStatementParser.class, parser.getClass());
    }

    private static class MarkerProvider implements SQLParserUtils.DialectParserProvider {
        @Override
        public SQLStatementParser createSQLStatementParser(String sql, DbType dbType, SQLParserFeature... features) {
            return new MarkerStatementParser(sql, dbType);
        }

        @Override
        public SQLExprParser createExprParser(String sql, DbType dbType, SQLParserFeature... features) {
            return null;
        }

        @Override
        public Lexer createLexer(String sql, DbType dbType, SQLParserFeature... features) {
            return null;
        }
    }

    private static class MarkerStatementParser extends SQLStatementParser {
        MarkerStatementParser(String sql, DbType dbType) {
            super(new SQLExprParser(sql, dbType));
        }
    }
}
