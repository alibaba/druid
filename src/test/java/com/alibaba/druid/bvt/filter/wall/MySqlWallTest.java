/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

public class MySqlWallTest extends TestCase {

    public void testWall() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql("select 14,13,12,11,10,@@version_compile_os,8,7,6,5,4,3,2,1 FROM X"));
        Assert.assertTrue(WallUtils.isValidateMySql("select '@@version_compile_os' FROM X"));

        Assert.assertFalse(WallUtils.isValidateMySql("select hex(load_file(0x633A2F77696E646F77732F7265706169722F73616D))"));
        Assert.assertTrue(WallUtils.isValidateMySql("select 'hex(load_file(0x633A2F77696E646F77732F7265706169722F73616D))'"));
        
        Assert.assertFalse(WallUtils.isValidateMySql("select 15,version() FROM X"));
        Assert.assertTrue(WallUtils.isValidateMySql("select 15,'version'"));

        Assert.assertFalse(WallUtils.isValidateMySql("SELECT *FROM T UNION select 1 from information_schema.columns"));
        Assert.assertTrue(WallUtils.isValidateMySql("select 'information_schema.columns'"));
        
        Assert.assertFalse(WallUtils.isValidateMySql("SELECT *FROM T UNION select 1 from mysql.user"));
        Assert.assertTrue(WallUtils.isValidateMySql("select 'mysql.user'"));
        
        Assert.assertFalse(WallUtils.isValidateMySql("select 0x3C3F706870206576616C28245F504F53545B2763275D293F3E into outfile '\\www\\edu\\1.php'"));
        Assert.assertTrue(WallUtils.isValidateMySql("select 'outfile'"));
        
        Assert.assertFalse(WallUtils.isValidateMySql("select f1, f2 from t union select 1, 2"));
    }
}
