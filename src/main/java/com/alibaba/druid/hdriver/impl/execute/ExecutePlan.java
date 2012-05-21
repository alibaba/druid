package com.alibaba.druid.hdriver.impl.execute;

import java.sql.SQLException;

import com.alibaba.druid.hdriver.HResultSet;
import com.alibaba.druid.hdriver.impl.jdbc.HPreparedStatementImpl;

public interface ExecutePlan {

    HResultSet executeQuery(HPreparedStatementImpl statement) throws SQLException;

    boolean execute(HPreparedStatementImpl statement) throws SQLException;
}
