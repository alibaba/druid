package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.repository.SchemaRepository;
import org.junit.Test;

public class BigQuerySchemaTest {
    @Test
    public void test_schema() {
    String sql =
        "CREATE TABLE IF NOT EXISTS `a-b-c.d.e` (\n"
            + "    feature_name string,\n"
            + "    feature_value float64,\n"
            + "    feature_bindings string,\n"
            + "    event_timestamp timestamp,\n"
            + "    load_timestamp timestamp,\n"
            + "    feature_end_timestamp timestamp,\n"
            + "    )\n"
            + "    PARTITION BY date(event_timestamp)\n"
            + "    ;";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.bigquery);
        SQLStatement sqlStatement = parser.parseStatement();
        SchemaRepository schemaRepository = new SchemaRepository(DbType.bigquery);
        schemaRepository.accept(sqlStatement);
    }
}
