package com.alibaba.druid.benckmark;

import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;

import java.io.InputStream;

public class TPCH {
    private static final String insert_lineitem_200;
    private static String ddl;
    private static String[] QUERIES = new String[22];

    static {
        for (int i = 1; i <= 22; ++i) {
            String path = "tpch/q" + i + ".sql";
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            String sql = Utils.read(is);
            QUERIES[i-1] = sql;
            JdbcUtils.close(is);
        }
        {
            String path = "tpch/create_tables.sql";
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            ddl = Utils.read(is);
            JdbcUtils.close(is);
        }
        {
            String path = "tpch/insert_lineitem_200.sql";
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            insert_lineitem_200 = Utils.read(is);
            JdbcUtils.close(is);
        }
    }

    public static String getQuery(int index) {
        return QUERIES[index - 1];
    }

    public static String getDDL() {
        return ddl;
    }

    public static String getInsertLineitem200() {
        return insert_lineitem_200;
    }
}
