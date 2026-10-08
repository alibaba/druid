package com.alibaba.druid.pool;

import com.alibaba.druid.mock.MockDriver;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateConnectionRuntimeExceptionRetryTest {
    @Test
    public void test_runtimeException_retry_is_rate_limited() throws Exception {
        // JDBC allows a driver to return null from connect(); createPhysicalConnection then
        // throws a NullPointerException, which lands in the RuntimeException branch of the
        // create threads. Before the fix that branch looped without any delay, producing
        // tens of thousands of identical ERROR log lines per second (#6733).
        final AtomicLong attempts = new AtomicLong();
        MockDriver driver = new MockDriver() {
            @Override
            public Connection connect(String url, Properties info) {
                attempts.incrementAndGet();
                return null;
            }
        };

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:npe");
        dataSource.setDriver(driver);
        dataSource.setTimeBetweenConnectErrorMillis(200);
        dataSource.setConnectionErrorRetryAttempts(0);
        dataSource.init();

        // A borrower waits for a connection; the create thread then enters the retry loop
        // where the driver keeps returning null and createPhysicalConnection throws a NPE.
        Thread borrower = new Thread(() -> {
            try {
                dataSource.getConnection(1000L);
            } catch (Throwable ignored) {
                // borrow times out; the create thread keeps retrying in the background
            }
        });
        borrower.start();

        Thread.sleep(2000L);
        dataSource.close();
        borrower.join(3000L);

        // Roughly 1300 ms of retrying with a 200 ms interval; a tight loop would produce
        // tens of thousands of attempts instead of a handful.
        long count = attempts.get();
        assertTrue(count <= 20, "too many create attempts within the window: " + count);
    }
}
