package com.alibaba.druid;

import com.alibaba.druid.stat.DruidDataSourceStatManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class PoolTestCase {
    @BeforeEach
    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        DruidDataSourceStatManager.clear();
    }
}
