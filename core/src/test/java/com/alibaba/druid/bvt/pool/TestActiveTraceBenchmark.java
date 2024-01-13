package com.alibaba.druid.bvt.pool;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

import junit.framework.TestCase;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
@Threads(4)
@Fork(1)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class TestActiveTraceBenchmark extends TestCase {
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
        dataSource.setMaxActive(16);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    @Benchmark
    public void test_activeTrace() throws Exception {
        int count = 1000;
        int i = 0;
        try {
            for (; i < count; ++i) {
                Connection conn = dataSource.getConnection();
                Assert.assertNotNull(conn);
                mockFileIO();
                conn.close();
                Assert.assertTrue(conn.isClosed());
            }
        } finally {
            Assert.assertEquals(count, i);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(TestActiveTraceBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }

    private void mockFileIO() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("log4j.properties");) {
            Properties properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
             // do nothing
        }
    }
}

