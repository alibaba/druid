package com.alibaba.druid.bvt.quartz;

import java.util.Properties;

import junit.framework.TestCase;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzTest extends TestCase {

    StdSchedulerFactory factory;
    Scheduler scheduler;
    
    @Override
    protected void setUp() throws Exception {
        Properties props = new Properties();
        props.load(getClass().getClassLoader().getResourceAsStream("bvt/quartz/quartz.properties"));
        factory = new StdSchedulerFactory(props);
        scheduler = factory.getScheduler();
    }
    
    protected void tearDown() throws Exception {
        scheduler.shutdown();
    }
    
    public void testQuartz() throws Exception {
        scheduler.start();
    }

}
