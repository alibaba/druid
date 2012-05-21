package com.alibaba.druid.hdriver.impl.execute;

import java.sql.SQLException;

import com.alibaba.druid.hdriver.impl.jdbc.HPreparedStatementImpl;
import com.alibaba.druid.hdriver.impl.jdbc.HResultSet;

public class ExecutePlanAdapter implements ExecutePlan {

    @Override
    public HResultSet executeQuery(HPreparedStatementImpl statement) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(HPreparedStatementImpl statement) throws SQLException {
        throw new UnsupportedOperationException();
    }

}
