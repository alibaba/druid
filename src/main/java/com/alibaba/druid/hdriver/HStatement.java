package com.alibaba.druid.hdriver;

import java.sql.SQLException;
import java.sql.Statement;

public interface HStatement extends Statement {
    HConnection getConnection() throws SQLException;
}
