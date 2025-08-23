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
package com.alibaba.druid.bvt.filter.wall.mysql;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;


import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class MySqlWallTest_union extends TestCase {
    public void testUnion() throws Exception {
        WallConfig config = new WallConfig();
        config.setSelectUnionCheck(true);

        assertFalse(WallUtils.isValidateMySql("select f1, f2 from t where id=1 union select 1, 2", config)); // not end of comment
        assertFalse(WallUtils.isValidateMySql("select f1, f2 from t where id=1 union select 1, 2 --", config));

        assertTrue(WallUtils.isValidateMySql("select f1, f2 from t union select 1, 2", config)); // no where

        assertFalse(WallUtils.isValidateMySql("select f1, f2 from t where id=1 union select null, '1', 2 --", config));

        assertTrue(WallUtils.isValidateMySql("select f1, f2 from t where id=1 union select c1, c2", config)); //union select item is not const

        assertTrue(WallUtils.isValidateMySql("SELECT typeid, typename FROM (SELECT typeid, typename FROM materialtype UNION ALL SELECT ? AS typeid, ? AS typename) a ORDER BY typeid",
                config)); // union select item has alias

        assertFalse(WallUtils.isValidateMySql("select f1, f2 from (select 1 as f1, 2 as f2) t union select 'u1', 'u2' --", config)); // from is subQuery

        assertTrue(WallUtils.isValidateMySql("select f1, f2 from t where id=1 union select 'u1' as u1, 'u2' as u2", config)); // union select item has alias
    }

    public void testUnion2() throws Exception {
//        assertFalse(
//                WallUtils.isValidateMySql("SELECT name, surname FROM users WHERE name='' UNION SELECT @@version, 'string1'")
//        );

        assertFalse(
                WallUtils.isValidateMySql("SELECT name, surname FROM users WHERE name='' UNION SELECT /*! @@version,*/ 'string1'")
        );

        assertFalse(
                WallUtils.isValidateMySql("SELECT name, surname FROM users WHERE name=' ' UNION SELECT /*! (select table_name FROM information_schema.tables limit 1,1),*/ 'string1'")
        );


    }
}
