package com.alibaba.druid.pool.ha.config;

import java.sql.SQLException;

public interface ConfigLoader {
    void load() throws SQLException;
}
