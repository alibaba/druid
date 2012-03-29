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
