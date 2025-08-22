package com.alibaba.druid.util;

import junit.framework.TestCase;
import static org.junit.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author shenjianeng [ishenjianeng@qq.com]
 */
public class ConcurrentLruCacheTest extends TestCase {


    public void testConcurrentLruCache() {
        ConcurrentLruCache<String, String> cache = new ConcurrentLruCache<>(2);
        assertEquals(0, cache.size());
        assertEquals("k1value", cache.computeIfAbsent("k1", key -> key + "value"));


        assertEquals(1, cache.size());
        assertTrue(cache.contains("k1"));

        assertEquals("k2value", cache.computeIfAbsent("k2", key -> key + "value"));
        assertEquals(2, cache.size());
        assertTrue(cache.contains("k1"));
        assertTrue(cache.contains("k2"));

        assertEquals("k1value", cache.get("k1"));
        assertEquals("k2value", cache.get("k2"));


        assertEquals("k3value", cache.computeIfAbsent("k3", key -> key + "value"));

        assertEquals(2, cache.size());
        assertEquals(2, cache.keys().size());

        assertFalse(cache.contains("k1"));
        assertTrue(cache.contains("k2"));
        assertTrue(cache.contains("k3"));


        cache.clear();
        assertEquals(0, cache.size());

    }

    public void testComputeIfAbsent() {
        ConcurrentLruCache<String, String> cache = new ConcurrentLruCache<>(4);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(9);
        List<Future<String>> futures = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            Future<String> feature =
                    executorService.submit(() -> {
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        return cache.computeIfAbsent("key", key -> UUID.randomUUID().toString());

                    });
            countDownLatch.countDown();
            futures.add(feature);
        }

        Set<String> set = futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());

        executorService.shutdown();
        assertEquals(1, set.size());
    }
}
