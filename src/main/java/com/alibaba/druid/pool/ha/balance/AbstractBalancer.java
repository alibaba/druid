package com.alibaba.druid.pool.ha.balance;

import com.alibaba.druid.pool.ha.MultiDataSource;

public abstract class AbstractBalancer implements Balancer {

    private MultiDataSource multiDataSource;

    private boolean         inited = false;

    @Override
    public synchronized void init(MultiDataSource multiDataSource) {
        if (this.inited) {
            return;
        }
        
        if (multiDataSource == null) {
            throw new IllegalStateException();
        }

        this.multiDataSource = multiDataSource;

        inited = true;
    }
    
    public boolean isInited() {
        return inited;
    }

    public MultiDataSource getMultiDataSource() {
        return multiDataSource;
    }

}
