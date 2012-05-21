package com.alibaba.druid.hdriver;

import java.sql.ResultSet;
import java.sql.SQLException;


public interface HResultSet extends ResultSet {
    HStatement getStatement() throws SQLException;
}
