package com.alibaba.druid.benckmark;

import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;

import java.io.InputStream;

public class TPCDS {
    private static String ddl;
    private static String[] QUERIES = new String[99];

    static {
        for (int i = 1; i <= 99; ++i) {
            String num = (i < 10 ? "0" : "") + i;
            String path = "tpcds/query" + num + ".sql";
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            String sql = Utils.read(is);
            QUERIES[i-1] = sql;
            JdbcUtils.close(is);
        }
        {
            String path = "tpcds/create_tables.sql";
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            ddl = Utils.read(is);
            JdbcUtils.close(is);
        }
    }

    public static String getQuery(int index) {
        return QUERIES[index - 1];
    }

    public static String getDDL() {
        return ddl;
    }
}
