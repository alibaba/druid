package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BigQueryCommentTest {
    @Test
    public void test_0() throws Exception {
        String sql = "SELECT id\n" +
                "    , updatedTimestamp as updated_timestamp\n" +
                "    , event_timestamp\n" +
                "\n" +
                "    -- comment1 --\n" +
                "    , current_timestamp() as last_modified_timestamp\n" +
                "    , execution_time as load_timestamp\n" +
                "\n" +
                "    -- comment2   \n" +
                "    -- comment3\n" +
                "    , row_number() over(partition by pid, orderId, status order by event_timestamp desc) rn\n" +
                "FROM t1\n" +
                "WHERE\n" +
                "    -- comment4\n" +
                "    et >= TIMESTAMP(date_sub(fact_start_date, interval 2 day))\n" +
                "AND\n" +
                "    et < TIMESTAMP(fact_end_date)";

        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(
                sql,
                DbType.bigquery,
                SQLParserFeature.KeepComments
        );
        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();
        assertEquals(6, queryBlock.getSelectList().size());

        assertEquals("SELECT id, updatedTimestamp AS updated_timestamp, event_timestamp -- comment1 --\n" +
                "\t, current_timestamp() AS last_modified_timestamp, execution_time AS load_timestamp -- comment2\n" +
                "\t-- comment3\n" +
                "\t, row_number() OVER (PARTITION BY pid, orderId, status ORDER BY event_timestamp DESC) AS rn\n" +
                "FROM t1\n" +
                "WHERE -- comment4\n" +
                "et >= TIMESTAMP(date_sub(fact_start_date, INTERVAL 2 DAY))\n" +
                "\tAND et < TIMESTAMP(fact_end_date)", SQLUtils.toSQLString(stmt, DbType.bigquery));
    }
}
