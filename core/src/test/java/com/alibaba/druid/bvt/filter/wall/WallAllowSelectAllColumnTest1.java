/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个场景，检测可疑的Having条件
 *
 * @author wenshao
 */
public class WallAllowSelectAllColumnTest1 {
    private String sql = "select count(*) from t where fid = ?";

    private WallConfig config = new WallConfig();

    @BeforeEach
    protected void setUp() throws Exception {
        config.setSelectAllColumnAllow(false);
    }

    @Test
    public void testMySql() throws Exception {
        assertTrue(WallUtils.isValidateMySql(sql, config));
    }

    @Test
    public void testORACLE() throws Exception {
        assertTrue(WallUtils.isValidateMySql(sql, config));
    }

    @Test
    public void testSQLServer() throws Exception {
        assertTrue(WallUtils.isValidateSqlServer(sql, config));
    }
}
