package com.alibaba.druid.hdriver;

import java.sql.PreparedStatement;
import java.sql.SQLException;



public interface HPreparedStatement extends PreparedStatement, HStatement {
    public HResultSet executeQuery() throws SQLException;
}
