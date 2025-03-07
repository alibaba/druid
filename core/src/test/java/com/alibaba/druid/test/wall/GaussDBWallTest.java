/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.test.wall;

import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.GaussDBWallProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Acewuye
 *
 * Notes: Original code of this class based on com.alibaba.druid.test.wall.PGWallTest
 */
public class GaussDBWallTest {
    @Test
    public void testDoublePrecision() throws Exception {
        WallProvider provider = new GaussDBWallProvider(new WallConfig(GaussDBWallProvider.DEFAULT_CONFIG_DIR));
        String sql = "CREATE TABLE test_pg_wall (col_int INT NOT NULL, col_double_x DOUBLE PRECISION NOT NULL DEFAULT 0, col_varchar VARCHAR(200) NULL)";
        WallCheckResult result = provider.check(sql);
        Assert.assertTrue(result.getViolations().isEmpty());
    }
}
