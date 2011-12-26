package com.alibaba.druid.pool.ha.balance;

import com.alibaba.druid.pool.ha.MultiDataSource;

public abstract class AbstractBalancer implements Balancer {

    private MultiDataSource multiDataSource;

    @Override
    public void init(MultiDataSource multiDataSource) {
        this.multiDataSource = multiDataSource;
    }

    public MultiDataSource getMultiDataSource() {
        return multiDataSource;
    }

}
