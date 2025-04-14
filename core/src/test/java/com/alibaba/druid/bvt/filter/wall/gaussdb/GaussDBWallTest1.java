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
package com.alibaba.druid.bvt.filter.wall.gaussdb;

import com.alibaba.druid.wall.WallUtils;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * @author Acewuye
 *
 * Notes: Original code of this class based on com.alibaba.druid.bvt.filter.wall.pg.PGWallTest1
 */
public class GaussDBWallTest1 extends TestCase {
    public void test_wall() throws Exception {
        Assert.assertTrue(WallUtils.isValidateGaussDB(
                "select pg_encoding_to_char(encoding) from pg_database where datname = current_database()"));
    }
}
