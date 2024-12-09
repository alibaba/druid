package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import com.alibaba.druid.sql.SQLUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;

public class BigQueryDirTest extends SQLResourceTest {
    public BigQueryDirTest() {
        super(DbType.bigquery);
    }

    protected File dir(String path) {
        return new File(path);
    }

    @Ignore
    @Test
    public void dirTest() throws Exception {
    //        File dir = new File("/Users/wenshao/Downloads/goto_1894_sql");
        File dir = new File("/Users/lingo/workspace/alibaba/druid/goto/all_etl_jobs/");
        File[] files = dir.listFiles();
        Arrays.sort(files, Comparator.comparing(e -> e.getName().toLowerCase()));
        long total = files.length;
        long success = 0L;
        for (File file : files) {
            if (file.getName().equals(".DS_Store")) {
                continue;
            }

            
            String sql = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            try {
                SQLUtils.parseStatements(sql, DbType.bigquery);
                success += 1;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(file.getAbsolutePath());
            }
        }
        System.out.println("success: " + success + "/" + total);
    }

    @Ignore
    @Test
    public void fileTest() throws Exception {
        File file = new File("/Users/lingo/workspace/alibaba/druid/goto/all_etl_jobs/gojek-mart-kafka-gofood_challenges_campaign.sql");
        String sql = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        SQLUtils.parseStatements(sql, DbType.bigquery);
    }
}
