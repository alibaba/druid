package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, time = 3)
@Measurement(iterations = 3, time = 3)
// Threads.MAX means using Runtime.getRuntime().availableProcessors().
@Threads(Threads.MAX)
@Fork(1)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class UsingUnfairLockBenchmarkTest {
    private DruidDataSource dataSource;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        DruidDataSourceStatManager.clear();

        dataSource = new DruidDataSource();
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeoutMillis(100);
        dataSource.setLogAbandoned(true);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000);
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(new SlowDriver());
        int poolSize = Runtime.getRuntime().availableProcessors() / 2;
        dataSource.setMaxActive(poolSize);
        dataSource.setInitialSize(poolSize);
        dataSource.setMaxWait(2000);
        dataSource.setUseUnfairLock(true);
        dataSource.init();
    }

    public static class SlowDriver extends MockDriver {
        public MockConnection createMockConnection(MockDriver driver, String url, Properties connectProperties) {
            try {
                Thread.sleep(1000 * 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return super.createMockConnection(driver, url, connectProperties);
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        dataSource.close();
        assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    @Benchmark
    public void test_activeTrace() throws Exception {
        int count = 1000_00;
        int i = 0;
        try {
            for (; i < count; ++i) {
                Connection conn = dataSource.getConnection();
                assertNotNull(conn);
                conn.close();
                assertTrue(conn.isClosed());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assertEquals(count, i);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(UsingUnfairLockBenchmarkTest.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}
