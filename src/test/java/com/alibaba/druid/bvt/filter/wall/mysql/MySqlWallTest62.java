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

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

/**
 * SQLServerWallTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class MySqlWallTest62 extends TestCase {

    public void test_true() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setSchemaCheck(true);

        Assert.assertTrue(provider.checkValid(//
        "select temp.*, u.CanComment, u.CanBeShared, u.CanForward, COALESCE(b.UserID,0) as isBlocked" + //
                "   , COALESCE(f.UserID,0) as Followed, COALESCE(ff.UserID,0) as IsFollowed" + //
                "   , COALESCE(ul.UserID,0) as liked, COALESCE(fff.UserID,0) as RIsFollowed " + //
                "from (select 294765 as UserID, 0  as RUserID, 7785977 as PicID " + //
                "       union all select 294765 as UserID, 0  as RUserID, 7780341 as PicID) temp     " + //
                "left join Users as u on u.UserID = temp.UserID   " + //
                "left join BlockUser as b on b.UserID = temp.UserID and b.BlockUserID = 294765     " + //
                "left join Fans as f on f.FansID = temp.UserID and f.UserID = 294765   " + //
                "left join Fans as ff ON ff.FansID = 294765 and ff.UserID = temp.UserID   " + //
                "left join Fans as fff ON fff.FansID = 294765 and fff.UserID = temp.RUserID   " + //
                "left join UserLikes as ul on ul.PicID = temp.PicID and ul.UserID = 294765"));

        Assert.assertEquals(4, provider.getTableStats().size());
    }
    
    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setSchemaCheck(true);
        provider.getConfig().setSelectUnionCheck(true);
        String sql = "SELECT 1, 2, 3" + //
                     " UNION ALL SELECT  a  from tt where c=1" + //
                     " UNION ALL SELECT 2 FROM dual --";
        Assert.assertFalse(provider.checkValid(sql));
        
        sql = "SELECT a from t where c=1 UNION ALL SELECT 2 FROM dual --";
        Assert.assertFalse(provider.checkValid(sql));
    }



}
