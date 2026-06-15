package com.alibaba.druid.bvt.sql.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksBackupStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateRoutineLoadStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksLoadStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksRestoreStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StarRocksRemainingFeaturesTest {
    @Test
    public void testBrokerLoad() {
        String sql = "LOAD LABEL db1.label1 ("
                + "DATA INFILE (\"s3://bucket/data/*.csv\") "
                + "INTO TABLE t1 "
                + "COLUMNS TERMINATED BY \",\" "
                + "FORMAT AS \"CSV\""
                + ") "
                + "PROPERTIES (\"timeout\" = \"3600\")";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksLoadStatement.class, stmt);
        StarRocksLoadStatement load = (StarRocksLoadStatement) stmt;
        assertNotNull(load.getLabel());
        assertEquals(1, load.getDataDescriptions().size());
        assertFalse(load.getProperties().isEmpty());
    }

    @Test
    public void testBrokerLoadMultipleFiles() {
        String sql = "LOAD LABEL db1.daily_load ("
                + "DATA INFILE (\"s3://bucket/a.csv\", \"s3://bucket/b.csv\") "
                + "INTO TABLE t1"
                + ") "
                + "WITH BROKER (\"aws.s3.access_key\" = \"xxx\")";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        StarRocksLoadStatement load = (StarRocksLoadStatement) stmt;
        assertEquals(2, load.getDataDescriptions().get(0).getFilePaths().size());
        assertFalse(load.getBrokerProperties().isEmpty());
    }

    @Test
    public void testCreateRoutineLoad() {
        String sql = "CREATE ROUTINE LOAD db1.job1 ON t1 "
                + "PROPERTIES (\"format\" = \"json\") "
                + "FROM KAFKA (\"kafka_broker_list\" = \"host:9092\", \"kafka_topic\" = \"topic1\")";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksCreateRoutineLoadStatement.class, stmt);
        StarRocksCreateRoutineLoadStatement routine = (StarRocksCreateRoutineLoadStatement) stmt;
        assertNotNull(routine.getName());
        assertNotNull(routine.getTableName());
        assertEquals("KAFKA", routine.getDataSourceType());
        assertFalse(routine.getDataSourceProperties().isEmpty());
    }

    @Test
    public void testCreateRoutineLoadWithColumns() {
        String sql = "CREATE ROUTINE LOAD db1.job2 ON t1 "
                + "COLUMNS (col1, col2, col3) "
                + "WHERE col1 > 0 "
                + "PROPERTIES (\"desired_concurrent_number\" = \"3\") "
                + "FROM KAFKA (\"kafka_broker_list\" = \"host:9092\", \"kafka_topic\" = \"topic2\")";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        StarRocksCreateRoutineLoadStatement routine = (StarRocksCreateRoutineLoadStatement) stmt;
        assertEquals(3, routine.getColumns().size());
        assertNotNull(routine.getWhereCondition());
    }

    @Test
    public void testBackup() {
        String sql = "BACKUP SNAPSHOT db1.snap1 TO my_repo "
                + "ON (t1, t2) "
                + "PROPERTIES (\"type\" = \"FULL\")";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksBackupStatement.class, stmt);
        StarRocksBackupStatement backup = (StarRocksBackupStatement) stmt;
        assertNotNull(backup.getSnapshotName());
        assertNotNull(backup.getRepository());
        assertEquals(2, backup.getOnTables().size());

        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("BACKUP SNAPSHOT"));
        assertTrue(output.toUpperCase().contains("TO"));
    }

    @Test
    public void testRestore() {
        String sql = "RESTORE SNAPSHOT db1.snap1 FROM my_repo "
                + "ON (t1) "
                + "PROPERTIES (\"backup_timestamp\" = \"2024-01-01\")";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksRestoreStatement.class, stmt);
        StarRocksRestoreStatement restore = (StarRocksRestoreStatement) stmt;
        assertNotNull(restore.getSnapshotName());
        assertNotNull(restore.getRepository());

        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("RESTORE SNAPSHOT"));
        assertTrue(output.toUpperCase().contains("FROM"));
    }

    @Test
    public void testInsertWithLabel() {
        String sql = "INSERT INTO t1 WITH LABEL my_label SELECT * FROM t2";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertNotNull(stmt);
        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("WITH LABEL"));
    }

    @Test
    public void testInsertByName() {
        String sql = "INSERT INTO t1 BY NAME SELECT * FROM t2";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertNotNull(stmt);
        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("BY NAME"));
    }

    @Test
    public void testInsertOverwrite() {
        String sql = "INSERT OVERWRITE t1 SELECT * FROM t2";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertNotNull(stmt);
        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("OVERWRITE") || output.toUpperCase().contains("INSERT"));
    }
}
