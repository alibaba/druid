package com.alibaba.druid.test;

import com.alibaba.druid.PoolTestCase;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

public class QuartzTest extends PoolTestCase {
    StdSchedulerFactory factory;
    StdScheduler scheduler;
    Properties props;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        props = new Properties();
        props.load(getClass().getClassLoader().getResourceAsStream("bvt/quartz/quartz.properties"));
        factory = new StdSchedulerFactory(props);
        scheduler = (StdScheduler) factory.getScheduler();

        scheduler.getContext();
    }

    protected void tearDown() throws Exception {
        scheduler.shutdown();

        Thread.sleep(1000 * 1000);
        super.tearDown();
    }

    public void testQuartz() throws Exception {
        scheduler.start();
    }
}
