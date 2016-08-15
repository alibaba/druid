/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.spring;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SequenceService implements SequenceServiceMBean, ISequenceService {

    private ISequenceDao   dao;
    private String        name      = "druid-spring-test";

    private String        seed;

    private AtomicInteger increment = new AtomicInteger();

    public ISequenceDao getDao() {
        return dao;
    }

    public void setDao(ISequenceDao dao) {
        this.dao = dao;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeed() {
        return this.seed;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long nextValue() {
        if (seed == null) {
            init();
        }

        int value = increment.getAndIncrement();

        String text = seed + "9" + Integer.toString(value);

        return Long.parseLong(text);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public synchronized void init() {
        this.seed = nextSeed();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public synchronized String nextSeed() {
        for (;;) {
            int value = dao.getValue(name);
            if (dao.compareAndSet(name, value + 1, value)) {
                if (value == 0) {
                    return "";
                } else {
                    return Integer.toString(value, 9);
                }
            }
        }
    }

}
