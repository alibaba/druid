package com.alibaba.druid.postgresql;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import org.junit.Test;

import java.util.List;

public class PGLexerTest {
    @Test
    public void test() {
        String sql = "explain SELECT count(1) as count, status FROM common_v2_125.app_job_list WHERE (group_ids ??| array['-1']) GROUP BY status";
        PGSQLStatementParser pgsqlStatementParser = new PGSQLStatementParser(sql, SQLParserFeature.KeepComments);
        List<SQLStatement> sqlStatements = pgsqlStatementParser.parseStatementList();
        for (SQLStatement sqlStatement : sqlStatements) {
            System.out.println(sqlStatement);
        }
    }
}
