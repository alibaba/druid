package com.alibaba.druid.hdriver.impl.execute;

import java.sql.SQLException;

import com.alibaba.druid.hdriver.impl.jdbc.HPreparedStatementImpl;
import com.alibaba.druid.hdriver.impl.jdbc.HResultSet;

public interface ExecutePlan {

    HResultSet executeQuery(HPreparedStatementImpl statement) throws SQLException;

    boolean execute(HPreparedStatementImpl statement) throws SQLException;
}
