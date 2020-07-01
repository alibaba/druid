/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sandzhang [sandzhangtoo@gmail.com]
 */
public class DaemonThreadFactory implements ThreadFactory {

    private AtomicInteger threadNo = new AtomicInteger(1);
    private final String  nameStart;
    private final String  nameEnd  = "]";

    public DaemonThreadFactory(String poolName){
        nameStart = "[" + poolName + "-";
    }

    public Thread newThread(Runnable r) {
        String threadName = nameStart + threadNo.getAndIncrement() + nameEnd;
        Thread newThread = new Thread(r, threadName);
        newThread.setDaemon(true);
        if (newThread.getPriority() != Thread.NORM_PRIORITY) {
            newThread.setPriority(Thread.NORM_PRIORITY);
        }
        return newThread;
    }

}
