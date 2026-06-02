package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksSubmitTaskStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StarRocksSubmitTaskTest {
    @Test
    public void testSubmitTaskSimple() {
        String sql = "SUBMIT TASK\nAS\nINSERT INTO t1\nSELECT *\nFROM t2";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksSubmitTaskStatement.class, stmt);

        StarRocksSubmitTaskStatement submitTask = (StarRocksSubmitTaskStatement) stmt;
        assertNull(submitTask.getName());
        assertNull(submitTask.getScheduleStart());
        assertNull(submitTask.getScheduleEvery());
        assertTrue(submitTask.getProperties().isEmpty());
        assertNotNull(submitTask.getBody());

        String output = stmt.toString();
        assertEquals(sql, output);
    }

    @Test
    public void testSubmitTaskWithNameAndScheduleEvery() {
        String sql = "SUBMIT TASK my_task\nSCHEDULE EVERY(INTERVAL 1 DAY)\nAS\nINSERT INTO t1\nSELECT *\nFROM t2";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksSubmitTaskStatement.class, stmt);

        StarRocksSubmitTaskStatement submitTask = (StarRocksSubmitTaskStatement) stmt;
        assertNotNull(submitTask.getName());
        assertEquals("my_task", submitTask.getName().getSimpleName());
        assertNull(submitTask.getScheduleStart());
        assertNotNull(submitTask.getScheduleEvery());
        assertTrue(submitTask.getProperties().isEmpty());

        String output = stmt.toString();
        assertEquals(sql, output);
    }

    @Test
    public void testSubmitTaskFull() {
        String sql = "SUBMIT TASK etl_job\n"
                + "SCHEDULE START('2024-01-01 00:00:00') EVERY(INTERVAL 1 HOUR)\n"
                + "PROPERTIES (\n"
                + "\t\"warehouse\" = \"etl\"\n"
                + ")\n"
                + "AS\n"
                + "INSERT INTO t1\n"
                + "SELECT *\n"
                + "FROM t2";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksSubmitTaskStatement.class, stmt);

        StarRocksSubmitTaskStatement submitTask = (StarRocksSubmitTaskStatement) stmt;
        assertNotNull(submitTask.getName());
        assertEquals("etl_job", submitTask.getName().getSimpleName());
        assertNotNull(submitTask.getScheduleStart());
        assertNotNull(submitTask.getScheduleEvery());
        assertEquals(1, submitTask.getProperties().size());
        assertNotNull(submitTask.getBody());

        String output = stmt.toString();
        assertEquals(sql, output);
    }

    @Test
    public void testSubmitTaskWithMultipleProperties() {
        String sql = "SUBMIT TASK load_job\n"
                + "SCHEDULE EVERY(INTERVAL 30 MINUTE)\n"
                + "PROPERTIES (\n"
                + "\t\"warehouse\" = \"etl\",\n"
                + "\t\"session.query_timeout\" = \"3600\"\n"
                + ")\n"
                + "AS\n"
                + "INSERT INTO t1\n"
                + "SELECT *\n"
                + "FROM t2";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksSubmitTaskStatement.class, stmt);

        StarRocksSubmitTaskStatement submitTask = (StarRocksSubmitTaskStatement) stmt;
        assertEquals("load_job", submitTask.getName().getSimpleName());
        assertNull(submitTask.getScheduleStart());
        assertNotNull(submitTask.getScheduleEvery());
        assertEquals(2, submitTask.getProperties().size());

        String output = stmt.toString();
        assertEquals(sql, output);
    }
}
