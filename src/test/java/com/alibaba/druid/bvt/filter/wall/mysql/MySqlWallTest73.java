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
public class MySqlWallTest73 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        
        provider.getConfig().setCommentAllow(true);

        Assert.assertTrue(provider.checkValid(//
        "DELETE FROM D1 USING PCHS_DETAIL D1 " + //
        "   INNER JOIN (" + //
        "       SELECT D.DETAIL_UID " + //
        "       FROM PCHS_DETAIL D " + //
        "           INNER JOIN PCHS_BILL B ON D.BILL_UID=B.BILL_UID " + //
        "       WHERE B.COM_UID='0892E8A38EF83AB6B9E25C25D8085486' " + //
        "       LIMIT 1000 " + //
        "   ) D2 ON D1.DETAIL_UID=D2.DETAIL_UID"));

        Assert.assertEquals(3, provider.getTableStats().size());
    }

}
