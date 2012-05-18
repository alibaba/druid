package com.alibaba.druid.hbase.exec;

import java.util.List;

import com.alibaba.druid.hbase.HBaseConnection;

public interface ExecutePlan {
    void execute(HBaseConnection connection, List<Object> paramerers);
}
