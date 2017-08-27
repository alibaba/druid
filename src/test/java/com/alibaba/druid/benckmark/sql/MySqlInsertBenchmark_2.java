package com.alibaba.druid.benckmark.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class MySqlInsertBenchmark_2 extends TestCase {
    static String sql = "INSERT INTO test_table VALUES (1, '1', '2017-10-10', true, false, '2017-10-10 10:10:10', '10:10:10', 1.111, NULL), (2, '2', '2017-10-10', true, false, '2017-10-10 10:10:10', '10:10:10', 2.222, NULL)" +
            ", (2, '2', '2017-09-09', true, false, '2017-10-10 10:10:10', '10:10:10', 3.333, NULL)" +
            ", (3, '3', '2017-10-10', true, false, '2017-10-10 10:10:10', '11:11:11', 4.333, NULL)" +
            ", (4, '4', '2017-10-10', true, false, '2017-10-10 10:10:10', '11:11:11', 4.333, NULL)" +
            ", (5, '5', '2017-10-10', true, false, '2017-10-10 10:10:10', '11:11:11', 4.333, NULL);";
    List<SQLStatement> stmtList;

    protected void setUp() throws Exception {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        parser.config(SQLParserFeature.KeepInsertValueClauseOriginalString, true);
        stmtList = parser.parseStatementList();
    }

    public void test_perf() throws Exception {
        System.out.println(sql);
        for (int i = 0; i < 5; ++i) {
//            perf(); // 5043
            perf_toString(); // 2101
//            perf_toString_featured(); // 7493
        }
    }

    public void perf() {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {


            List<SQLStatement> stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
//            stmt.toString();
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_keepInsertValueClauseStrinng() {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {

            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
            parser.config(SQLParserFeature.KeepInsertValueClauseOriginalString, true);
            List<SQLStatement> stmtList = parser.parseStatementList();
//            stmt.toString();
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_toString() {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            SQLUtils.toMySqlString(stmtList.get(0));
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_toString_featured() {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000 * 10; ++i) {
            SQLUtils.toMySqlString(stmtList.get(0), VisitorFeature.OutputUseInsertValueClauseOriginalString);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }
}
