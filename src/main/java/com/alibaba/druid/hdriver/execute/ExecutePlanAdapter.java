package com.alibaba.druid.hdriver.execute;

import java.sql.SQLException;

import com.alibaba.druid.hdriver.jdbc.HPreparedStatementImpl;
import com.alibaba.druid.hdriver.jdbc.HResultSet;

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
