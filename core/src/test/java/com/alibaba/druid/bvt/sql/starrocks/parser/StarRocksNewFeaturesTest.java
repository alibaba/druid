package com.alibaba.druid.bvt.sql.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableSwap;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateDictionaryStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreatePipeStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateStorageVolumeStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StarRocksNewFeaturesTest {
    @Test
    public void testCreatePipeSimple() {
        String sql = "CREATE PIPE my_pipe AS INSERT INTO t1 SELECT * FROM FILES(\"path\" = \"s3://bucket/data/*.parquet\", \"format\" = \"parquet\")";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksCreatePipeStatement.class, stmt);
        StarRocksCreatePipeStatement pipe = (StarRocksCreatePipeStatement) stmt;
        assertEquals("my_pipe", pipe.getName().getSimpleName());
        assertNotNull(pipe.getBody());
    }

    @Test
    public void testCreatePipeOrReplace() {
        String sql = "CREATE OR REPLACE PIPE IF NOT EXISTS etl_pipe\n"
                + "PROPERTIES (\n\t\"AUTO_INGEST\" = \"TRUE\"\n)\n"
                + "AS\nINSERT INTO t1\nSELECT *\nFROM t2";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        StarRocksCreatePipeStatement pipe = (StarRocksCreatePipeStatement) stmt;
        assertTrue(pipe.isOrReplace());
        assertTrue(pipe.isIfNotExists());
        assertFalse(pipe.getProperties().isEmpty());
    }

    @Test
    public void testCreateDictionary() {
        String sql = "CREATE DICTIONARY dict1 USING source_table (key_col KEY, val_col VALUE)";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksCreateDictionaryStatement.class, stmt);
        StarRocksCreateDictionaryStatement dict = (StarRocksCreateDictionaryStatement) stmt;
        assertEquals("dict1", dict.getName().getSimpleName());
        assertEquals("source_table", dict.getSourceTable().getSimpleName());
        assertEquals(2, dict.getColumnMappings().size());
    }

    @Test
    public void testCreateDictionaryWithProperties() {
        String sql = "CREATE DICTIONARY dict2 USING lookup_table (id KEY, name VALUE, score VALUE)\n"
                + "PROPERTIES (\n\t\"dictionary_warm_up\" = \"TRUE\"\n)";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        StarRocksCreateDictionaryStatement dict = (StarRocksCreateDictionaryStatement) stmt;
        assertEquals(3, dict.getColumnMappings().size());
        assertFalse(dict.getProperties().isEmpty());
    }

    @Test
    public void testCreateStorageVolume() {
        String sql = "CREATE STORAGE VOLUME IF NOT EXISTS my_vol\n"
                + "TYPE = S3\n"
                + "LOCATIONS = ('s3://bucket/path/')\n"
                + "PROPERTIES (\n\t\"aws.s3.region\" = \"us-west-2\"\n)";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksCreateStorageVolumeStatement.class, stmt);
        StarRocksCreateStorageVolumeStatement vol = (StarRocksCreateStorageVolumeStatement) stmt;
        assertTrue(vol.isIfNotExists());
        assertEquals("my_vol", vol.getName().getSimpleName());
        assertNotNull(vol.getType());
        assertFalse(vol.getLocations().isEmpty());
        assertFalse(vol.getProperties().isEmpty());
    }

    @Test
    public void testCreateStorageVolumeWithComment() {
        String sql = "CREATE STORAGE VOLUME hdfs_vol\n"
                + "TYPE = HDFS\n"
                + "LOCATIONS = ('hdfs://namenode:8020/data')\n"
                + "COMMENT 'HDFS storage'";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        StarRocksCreateStorageVolumeStatement vol = (StarRocksCreateStorageVolumeStatement) stmt;
        assertNotNull(vol.getComment());
    }

    @Test
    public void testAlterTableSwap() {
        String sql = "ALTER TABLE t1 SWAP WITH t2";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(SQLAlterTableStatement.class, stmt);
        SQLAlterTableStatement alter = (SQLAlterTableStatement) stmt;
        assertEquals(1, alter.getItems().size());
        assertInstanceOf(SQLAlterTableSwap.class, alter.getItems().get(0));
        SQLAlterTableSwap swap = (SQLAlterTableSwap) alter.getItems().get(0);
        assertEquals("t2", swap.getName().getSimpleName());

        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("SWAP WITH"));
    }

    @Test
    public void testAlterTableSwapSchemaQualified() {
        String sql = "ALTER TABLE db1.t1 SWAP WITH db1.t2";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        SQLAlterTableStatement alter = (SQLAlterTableStatement) stmt;
        SQLAlterTableSwap swap = (SQLAlterTableSwap) alter.getItems().get(0);
        assertNotNull(swap.getName());
        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.contains("db1"));
    }

    @Test
    public void testAsofJoin() {
        String sql = "SELECT * FROM orders ASOF JOIN prices ON orders.symbol = prices.symbol AND orders.ts >= prices.ts";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertNotNull(stmt);
        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("ASOF"), "Output should contain ASOF: " + output);
    }

    @Test
    public void testAsofLeftJoin() {
        String sql = "SELECT * FROM orders ASOF LEFT JOIN prices ON orders.symbol = prices.symbol AND orders.ts >= prices.ts";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertNotNull(stmt);
        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("ASOF"), "Output should contain ASOF: " + output);
    }
}
