package com.alibaba.druid.benckmark.pool;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;

public class AsyncClose {
    public static void main(String[] args) throws Exception {
        Class.forName("com.alibaba.druid.mock.MockDriver");
        DataSource ds = createDruid();
        System.out.println("init done");

       for (int i = 0; i < 10; ++i) {
           perf(ds);
       }

        System.out.println("query done");
    }

    public static void perf(DataSource ds) throws Exception {
        long start = System.currentTimeMillis();
        final int count = 1000 * 1000;
        for (int i = 0; i < count; ++i) {
            Connection conn = ds.getConnection();

            PreparedStatement ps = conn.prepareStatement("select 1");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            }

            rs.close();
            ps.close();
            conn.close();
        }
        long millis = System.currentTimeMillis() - start;

        System.out.println("millis " + millis + ", qps " + NumberFormat.getInstance().format(1000 * 1000 * 1000 / millis));
    }

    public static DataSource createDruid() throws Exception {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl("jdbc:fake:dragoon_v25masterdb");
        ds.setUsername("tddl5");
        ds.setPassword("tddl5");
        ds.setFilters("stat");
//         ds.setAsyncCloseConnectionEnable(true);
        ds.init();

        return ds;
    }
}
