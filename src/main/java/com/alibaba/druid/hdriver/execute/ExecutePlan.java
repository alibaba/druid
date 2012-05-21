package com.alibaba.druid.hdriver.execute;

import java.sql.SQLException;

import com.alibaba.druid.hdriver.jdbc.HPreparedStatementImpl;
import com.alibaba.druid.hdriver.jdbc.HResultSet;

public interface ExecutePlan {

    HResultSet executeQuery(HPreparedStatementImpl statement) throws SQLException;

    boolean execute(HPreparedStatementImpl statement) throws SQLException;
}
