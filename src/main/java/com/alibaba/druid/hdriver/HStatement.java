package com.alibaba.druid.hdriver;

import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.druid.hdriver.impl.jdbc.HBaseConnectionImpl;

public interface HStatement extends Statement {
    HBaseConnectionImpl getConnection() throws SQLException;
}
