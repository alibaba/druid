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

/**
 * SQLServerWallTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class MySqlWallTest15 extends TestCase {

    public void test_true() throws Exception {
        Assert.assertTrue(WallUtils.isValidateMySql(//
        "SELECT m.*, m.icon AS micon, md.uid as md.uid, md.lastmsg,md.postnum," + //
                "md.rvrc,md.money,md.credit,md.currency,md.lastvisit,md.thisvisit," + //
                "md.onlinetime,md.lastpost,md.todaypost, md.monthpost,md.onlineip," + //
                "md.uploadtime,md.uploadnum,md.starttime,md.pwdctime,md.monoltime," + //
                "md.digests,md.f_num,md.creditpop, md.jobnum,md.lastgrab,md.follows,md.fans," + //
                "md.newfans,md.newreferto,md.newcomment,md.postcheck,md.punch, mi.customdata " + //
                "FROM pw_members m " + //
                "   LEFT JOIN pw_memberdata md ON m.uid=md.uid " + //
                "   LEFT JOIN pw_memberinfo mi ON mi.uid=m.uid " + //
                "WHERE m.uid IN (?)")); //
    }
}
