package com.alibaba.druid.util;

import junit.framework.TestCase;
import org.junit.Assert;

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
        Assert.assertEquals(0, cache.size());
        Assert.assertEquals("k1value", cache.computeIfAbsent("k1", key -> key + "value"));


        Assert.assertEquals(1, cache.size());
        Assert.assertTrue(cache.contains("k1"));

        Assert.assertEquals("k2value", cache.computeIfAbsent("k2", key -> key + "value"));
        Assert.assertEquals(2, cache.size());
        Assert.assertTrue(cache.contains("k1"));
        Assert.assertTrue(cache.contains("k2"));

        Assert.assertEquals("k1value", cache.get("k1"));
        Assert.assertEquals("k2value", cache.get("k2"));


        Assert.assertEquals("k3value", cache.computeIfAbsent("k3", key -> key + "value"));

        Assert.assertEquals(2, cache.size());
        Assert.assertEquals(2, cache.keys().size());

        Assert.assertFalse(cache.contains("k1"));
        Assert.assertTrue(cache.contains("k2"));
        Assert.assertTrue(cache.contains("k3"));


        cache.clear();
        Assert.assertEquals(0, cache.size());

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
        Assert.assertEquals(1, set.size());
    }
}
