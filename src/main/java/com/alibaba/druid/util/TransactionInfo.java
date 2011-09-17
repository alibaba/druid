package com.alibaba.druid.util;

import java.util.ArrayList;
import java.util.List;

public class TransactionInfo {

    private final long         id;
    private final List<String> sqlList = new ArrayList<String>();
    private final long         startTimeMillis;
    private long               endTimeMillis;

    public TransactionInfo(long id){
        this.id = id;
        this.startTimeMillis = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

}
