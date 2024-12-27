package com.alibaba.druid.benckmark.sql;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

public class SchemaStatVisitorTest {
    @Test
    public void testGetTableReferences() {
        String sql =
            "CREATE TEMP TABLE temp_participant_log AS (\n"
                + "WITH\n"
                + "transform AS (\n"
                + "  SELECT\n"
                + "    `godata-platform.udfs.standardRule`(status, ['cleanup']) as status_name\n"
                + "    FROM patch_source\n"
                + ")\n"
                + "SELECT\n"
                + "    event.order_no,\n"
                + "    event.status_name\n"
                + "  FROM\n"
                + "  (\n"
                + "    SELECT\n"
                + "      ARRAY_AGG(\n"
                + "        table ORDER BY event_timestamp DESC LIMIT 1\n"
                + "      )[OFFSET(0)] event\n"
                + "    FROM\n"
                + "      transform table\n"
                + "    GROUP BY\n"
                + "      order_no, status_name, bid_id, iteration_number, participant_id, participant_uuid\n"
                + "  )\n"
                + ");";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.bigquery);
        SchemaStatVisitor schemaStatVisitor = new SchemaStatVisitor(DbType.bigquery);
        SQLStatement stmt = parser.parseStatement();
        stmt.accept(schemaStatVisitor);
        List<Pair<SQLName, String>> tableReferences = schemaStatVisitor.getTableReferences();
        Assert.assertEquals("patch_source", tableReferences.get(0).getKey().getSimpleName());
        Assert.assertEquals("transform", tableReferences.get(1).getKey().getSimpleName());
        Assert.assertEquals("table", tableReferences.get(1).getValue());
    }
}
