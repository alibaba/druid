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

public class MySqlWallTest extends TestCase {
    public void testWall() throws Exception {
        assertFalse(WallUtils.isValidateMySql("SELECT * FROM X where id=1 and 1!=1 union select 14,13,12,11,10,@@version_compile_os,8,7,6,5,4,3,2,1 FROM X"));
        assertTrue(WallUtils.isValidateMySql("select '@@version_compile_os' FROM X"));

        assertFalse(WallUtils.isValidateMySql("SELECT * FROM X where id=1 and 1!=1 union select hex(load_file(0x633A2F77696E646F77732F7265706169722F73616D))"));
        assertTrue(WallUtils.isValidateMySql("select hex(load_file(0x633A2F77696E646F77732F7265706169722F73616D))"));
        assertTrue(WallUtils.isValidateMySql("select 'hex(load_file(0x633A2F77696E646F77732F7265706169722F73616D))'"));

        assertFalse(WallUtils.isValidateMySql("select * from t where fid = 1 union select 15,version() FROM X"));
        assertTrue(WallUtils.isValidateMySql("select 15,version() FROM X"));
        assertTrue(WallUtils.isValidateMySql("select 15,'version'"));

        assertFalse(WallUtils.isValidateMySql("SELECT *FROM T UNION select 1 from information_schema.columns"));
        assertTrue(WallUtils.isValidateMySql("select 'information_schema.columns'"));

        assertFalse(WallUtils.isValidateMySql("SELECT *FROM T UNION select 1 from mysql.user"));
        assertTrue(WallUtils.isValidateMySql("select 'mysql.user'"));

        assertFalse(WallUtils.isValidateMySql("select * FROM T WHERE id = 1 AND select 0x3C3F706870206576616C28245F504F53545B2763275D293F3E into outfile '\\www\\edu\\1.php'"));
        assertTrue(WallUtils.isValidateMySql("select 'outfile'"));

        //assertFalse(WallUtils.isValidateMySql("select f1, f2 from t where c1=1 union select 1, 2"));

        assertFalse(WallUtils.isValidateMySql("select c1 from t where 1=1 or id =1"));
        assertFalse(WallUtils.isValidateMySql("select c1 from t where id =1 or 1=1"));
        assertFalse(WallUtils.isValidateMySql("select c1 from t where id =1 || 1=1"));

        WallConfig config = new WallConfig();
        config.setHintAllow(false);
        assertFalse(WallUtils.isValidateMySql(
                "select * from person where id = '3'/**/union select v,b,a from (select 1,2,4/*! ,database() as b,user() as a,version() as v*/) a where '1'<>''",
                config));
    }
}
