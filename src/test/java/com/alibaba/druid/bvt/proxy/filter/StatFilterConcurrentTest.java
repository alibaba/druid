package com.alibaba.druid.bvt.proxy.filter;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcSqlStat;

public class StatFilterConcurrentTest extends TestCase {

    private DruidDataSource dataSource;
    private StatFilter      statFilter;
    private int             LOOP_COUNT = 1000 * 1;

    public void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxActive(100);

        statFilter = new StatFilter();
        dataSource.getProxyFilters().add(statFilter);
        dataSource.setConnectionProperties("executeSleep=1");
    }

    public void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_stat() throws Exception {
        concurrent(100);
        for (JdbcSqlStat sqlStat : statFilter.getDataSourceStat().getSqlStatMap().values()) {
            System.out.println(sqlStat.getConcurrentMax());
        }
    }

    public void concurrent(int threadCount) throws Exception {
        Thread[] threads = new Thread[threadCount];
        final CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new Thread() {

                public void run() {
                    try {
                        for (int i = 0; i < LOOP_COUNT; ++i) {
                            Connection conn = dataSource.getConnection();
                            Statement stmt = conn.createStatement();
                            stmt.executeUpdate("select 1");
                            stmt.close();
                            conn.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
        }

        for (int i = 0; i < threadCount; ++i) {
            threads[i].start();
        }
        endLatch.await();

    }
}
