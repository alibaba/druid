package com.alibaba.druid.hdriver;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.hdriver.impl.HPreparedStatementImpl;

public interface HConnection extends Connection {
    HPreparedStatementImpl prepareStatement(String sql) throws SQLException;
    
    HStatement createStatement() throws SQLException;;
}
