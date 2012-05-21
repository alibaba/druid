package com.alibaba.druid.hdriver;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.hdriver.impl.mapping.HMapping;

public interface HConnection extends Connection {
    HPreparedStatement prepareStatement(String sql) throws SQLException;
    
    HPreparedStatement prepareStatement(String sql, HMapping mapping) throws SQLException;
    
    HStatement createStatement() throws SQLException;;
}
