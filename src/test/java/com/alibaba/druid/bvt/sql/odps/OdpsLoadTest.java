package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;

public class OdpsLoadTest extends TestCase {
    
    public void test_load_0() throws Exception {
        String sql = "LOAD OVERWRITE TABLE tsv_load_tbl \n" +
                "FROM\n" +
                "LOCATION 'oss://accessId:accesssKey@oss-cn-hangzhou-zmf.aliyuncs.com/my_bucket_id/my_location/'\n" +
                "STORED BY 'com.aliyun.odps.TsvStorageHandler'\n" +
                "WITH SERDEPROPERTIES ('odps.text.option.delimiter'='\\t');";
        assertEquals("LOAD OVERWRITE INTO TABLE tsv_load_tbl\n" +
                "LOCATION 'oss://accessId:accesssKey@oss-cn-hangzhou-zmf.aliyuncs.com/my_bucket_id/my_location/'\n" +
                "STORED BY 'com.aliyun.odps.TsvStorageHandler'\n" +
                "WITH SERDEPROPERTIES (\n" +
                "\t'odps.text.option.delimiter' = '\t'\n" +
                ");", SQLUtils.formatOdps(sql));
    }
    

    public void test_load_1() throws Exception {
        String sql = "LOAD OVERWRITE TABLE oss_load_static_part PARTITION(ds='20190101')\n" +
                "FROM\n" +
                "LOCATION 'oss://<yourAccessKeyId>:<yourAccessKeySecret>@oss-cn-hangzhou-zmf.aliyuncs.com/my_bucket_id/my_location/'\n" +
                "STORED AS PARQUET;";
        assertEquals("LOAD OVERWRITE INTO TABLE oss_load_static_part PARTITION (ds = '20190101')\n" +
                "LOCATION 'oss://<yourAccessKeyId>:<yourAccessKeySecret>@oss-cn-hangzhou-zmf.aliyuncs.com/my_bucket_id/my_location/'\n" +
                "STORED AS PARQUET;", SQLUtils.formatOdps(sql));
    }



    public void test_load_2() throws Exception {
        String sql = "LOAD OVERWRITE TABLE oss_load_dyn_part PARTITION(ds)\n" +
                "FROM\n" +
                "LOCATION 'oss://accessId:accesssKey@oss-cn-hangzhou-zmf.aliyuncs.com/bucket/text_data/'\n" +
                "ROW FORMAT serde 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'\n" +
                "WITH SERDEPROPERTIES(\n" +
                "  'Fields terminator'='\\001',\n" +
                "  'Escape delimitor'='\\\\',\n" +
                "  'Collection items terminator'='\\002',\n" +
                "  'Map keys terminator'='\\003',\n" +
                "  'Lines terminator'='\\n',\n" +
                "  'Null defination'='\\\\N')\n" +
                "STORED AS TEXTFILE ;";
        assertEquals("LOAD OVERWRITE INTO TABLE oss_load_dyn_part PARTITION (ds)\n" +
                "LOCATION 'oss://accessId:accesssKey@oss-cn-hangzhou-zmf.aliyuncs.com/bucket/text_data/'\n" +
                "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'\n" +
                "WITH SERDEPROPERTIES (\n" +
                "\t'Fields terminator' = '\\001',\n" +
                "\t'Escape delimitor' = '\\\\',\n" +
                "\t'Collection items terminator' = '\\002',\n" +
                "\t'Map keys terminator' = '\\003',\n" +
                "\t'Lines terminator' = '\\n',\n" +
                "\t'Null defination' = '\\\\N'\n" +
                ")\n" +
                "STORED AS TEXTFILE;", SQLUtils.formatOdps(sql));
    }
}
