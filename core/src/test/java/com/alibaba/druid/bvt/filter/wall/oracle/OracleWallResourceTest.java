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
package com.alibaba.druid.bvt.filter.wall.oracle;

import com.alibaba.druid.TestUtil;
import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.wall.Violation;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.spi.OracleWallProvider;

import java.util.List;

public class OracleWallResourceTest extends PGTest {
    public void test_0() throws Exception {
        exec_test("bvt/wall/oracle/oracle-01.sql");
    }

    public void test_2() throws Exception {
        exec_test("bvt/wall/oracle/oracle-02.sql");
    }

    public void exec_test(String resource) throws Exception {
        String sql = TestUtil.getResource(resource);
        OracleWallProvider provider = new OracleWallProvider();

        WallCheckResult checkResult = provider.check(sql);
        List<Violation> violations = checkResult.getViolations();
        if (!violations.isEmpty()) {
            fail(violations.get(0).getMessage());
        }
    }

}
