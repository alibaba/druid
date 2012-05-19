package com.alibaba.druid.hbase.exec;

import com.alibaba.druid.hbase.HBasePreparedStatement;



public class InsertExecutePlan extends SingleTableExecutePlan {

    @Override
    public void execute(HBasePreparedStatement statement) {
        throw new UnsupportedOperationException();
    }
    
}
