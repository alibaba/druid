package com.alibaba.druid.spring;

public interface ISequenceDao {

    boolean compareAndSet(String name, int value, int expect);

    int getValue(String name);
}
