package com.alibaba.druid.hbase.exec;

import org.apache.hadoop.hbase.client.HTableInterface;

public class SingleTableQueryExecutePlan implements ExecutePlan {

    private HTableInterface htable;

    public HTableInterface getHtable() {
        return htable;
    }

    public void setHtable(HTableInterface htable) {
        this.htable = htable;
    }

}
