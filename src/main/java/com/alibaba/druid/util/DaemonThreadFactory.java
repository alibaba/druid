package com.alibaba.druid.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class DaemonThreadFactory implements ThreadFactory {

    private AtomicInteger threadNo = new AtomicInteger(1);
    private final String  nameStart;
    private final String  nameEnd  = "]";

    public DaemonThreadFactory(String poolName){
        nameStart = "[" + poolName + "-";
    }

    public Thread newThread(Runnable r) {
        Thread newThread = new Thread(r, nameStart + threadNo.getAndIncrement() + nameEnd);
        newThread.setDaemon(true);
        if (newThread.getPriority() != Thread.NORM_PRIORITY) {
            newThread.setPriority(Thread.NORM_PRIORITY);
        }
        return newThread;
    }

}
