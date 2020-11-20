package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;

public class HiveCreateTableTest_50 extends OracleTest {
    public void test_0() throws Exception {
        String createDdl = "create external table `ods.ods_dc_test_df` ( `id` bigint comment 'id',  `name` string comment '名称' ) " +
                "comment '测试表' partitioned by ( `dt` string ) " +
                "row format serde 'org.apache.hadoop.hive.ql.io.parquet.serde.parquethiveserde' " +
                "stored as inputformat 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat' " +
                "          outputformat 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat' " +
                "location 'hdfs://dc/user/hive/warehouse/ods.db/ods_dc_test_df'" +
                " tblproperties ( parquet.compression = 'snappy' , transient_lastDdlTime = '1603362950' )";


        assertEquals("CREATE EXTERNAL TABLE `ods.ods_dc_test_df` (\n" +
                "\t`id` bigint COMMENT 'id',\n" +
                "\t`name` string COMMENT '名称'\n" +
                ")\n" +
                "COMMENT '测试表'\n" +
                "PARTITIONED BY (\n" +
                "\t`dt` string\n" +
                ")\n" +
                "ROW FORMAT\n" +
                "\tSERDE 'org.apache.hadoop.hive.ql.io.parquet.serde.parquethiveserde'\n" +
                "STORED AS\n" +
                "\tINPUTFORMAT 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat'\n" +
                "\tOUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat'\n" +
                "LOCATION 'hdfs://dc/user/hive/warehouse/ods.db/ods_dc_test_df'\n" +
                "TBLPROPERTIES (\n" +
                "\t'parquet.compression' = 'snappy',\n" +
                "\t'transient_lastDdlTime' = '1603362950'\n" +
                ")",SQLUtils.formatHive(createDdl));

    }
}
