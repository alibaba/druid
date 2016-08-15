/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

public class OracleWallTest3 extends TestCase {

    public void test_true() throws Exception {
        String sql = //
        "CREATE TRIGGER \"AO_4AEACD_WEBHOOK_D367380484\" " //
                + "BEFORE INSERT"//
                + "    ON \"AO_4AEACD_WEBHOOK_DAO\"   FOR EACH ROW "//
                + "BEGIN"//
                + "    SELECT \"AO_4AEACD_WEBHOOK_DAO_ID_SEQ\".NEXTVAL INTO :NEW.\"ID\" FROM DUAL;"//
                + "END;";
        Assert.assertTrue(WallUtils.isValidateOracle(sql));
    }
}
