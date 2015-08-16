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

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

/**
 * SQLServerWallTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class MySqlWallTest49 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        Assert.assertTrue(provider.checkValid(//
        "select temp.*, u.CanComment, u.CanBeShared, " + //
                "   u.CanForward, COALESCE(b.UserID,0) as isBlocked, " + //
                "   COALESCE(f.UserID,0) as Followed, COALESCE(ff.UserID,0) as IsFollowed, " + //
                "   COALESCE(ul.UserID,0) as liked, " + //
                "   COALESCE(fff.UserID,0) as RIsFollowed " + //
                "from " + "(select 281319 as UserID, 0  as RUserID, 7797549 as PicID " + //
                "   union all " + //
                "   select 55608 as UserID, 0  as RUserID, 7797527 as PicID " + //
                "   union all " + //
                "   select 281319 as UserID, 0  as RUserID, 7797233 as PicID " + //
                "   union all " + //
                "   select 281319 as UserID, 0  as RUserID, 7797221 as PicID " + //
                "   union all select 281319 as UserID, 0  as RUserID, 7797217 as PicID " + //
                "   union all select 281319 as UserID, 0  as RUserID, 7797189 as PicID " + //
                "   union all select 12271 as UserID, 0  as RUserID, 7796057 as PicID " + //
                "   union all select 401697 as UserID, 494381  as RUserID, 7795057 as PicID " + //
                "   union all select 401697 as UserID, 470693  as RUserID, 7795041 as PicID " + //
                "   union all select 401697 as UserID, 470693  as RUserID, 7795039 as PicID) temp     " + //
                "left join Users as u on u.UserID = temp.UserID   " + //
                "left join BlockUser as b on b.UserID = temp.UserID and b.BlockUserID = 281319 " + //
                "left join Fans as f on f.FansID = temp.UserID and f.UserID = 281319   " + //
                "left join Fans as ff ON ff.FansID = 281319 and ff.UserID = temp.UserID   " + //
                "left join Fans as fff ON fff.FansID = 281319 and fff.UserID = temp.RUserID   " + //
                "left join UserLikes as ul on ul.PicID = temp.PicID and ul.UserID = 281319"));

        Assert.assertEquals(4, provider.getTableStats().size());
    }
}
