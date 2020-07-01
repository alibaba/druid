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
package com.alibaba.druid.test.wall;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

import com.alibaba.druid.util.Utils;
import com.alibaba.druid.wall.Violation;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import org.junit.Assert;
import org.junit.Test;

public class MySqlResourceWallTest extends TestCase {

    private String[] items;


    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        provider.getConfig().setConditionDoubleConstAllow(true);

        provider.getConfig().setUseAllow(true);
        provider.getConfig().setStrictSyntaxCheck(false);
        provider.getConfig().setMultiStatementAllow(true);
        provider.getConfig().setConditionAndAlwayTrueAllow(true);
        provider.getConfig().setNoneBaseStatementAllow(true);
        provider.getConfig().setSelectUnionCheck(false);
        provider.getConfig().setSchemaCheck(true);
        provider.getConfig().setLimitZeroAllow(true);
        provider.getConfig().setCommentAllow(true);

        for (int i = 0; i < items.length; ++i) {
            String sql = items[i];
            if (sql.indexOf("''=''") != -1) {
                continue;
            }
//            if (i <= 121) {
//                continue;
//            }
            WallCheckResult result = provider.check(sql);
            if (result.getViolations().size() > 0) {
                Violation violation = result.getViolations().get(0);
                System.out.println("error (" + i + ") : " + violation.getMessage());
                System.out.println(sql);
                break;
            }
        }
        System.out.println(provider.getViolationCount());
//        String sql = "SELECT name, '******' password, createTime from user where name like 'admin' AND (CASE WHEN (7885=7885) THEN 1 ELSE 0 END)";

//        Assert.assertFalse(provider.checkValid(sql));
    }



    @Test
    public void test_lock_table() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setNoneBaseStatementAllow(true);

        String sql = "lock tables etstsun write";
        WallCheckResult result = provider.check(sql);
        if (result.getViolations().size() > 0) {
            Violation violation = result.getViolations().get(0);
            System.out.println("error () : " + violation.getMessage());
        }
        Assert.assertTrue(provider.checkValid(sql));


        sql = "lock tables etstsun LOW_PRIORITY write";
        result = provider.check(sql);
        if (result.getViolations().size() > 0) {
            Violation violation = result.getViolations().get(0);
            System.out.println("error () : " + violation.getMessage());
        }
        Assert.assertTrue(provider.checkValid(sql));


        sql = "UNLOCK TABLES";
        result = provider.check(sql);
        if (result.getViolations().size() > 0) {
            Violation violation = result.getViolations().get(0);
            System.out.println("error () : " + violation.getMessage());
        }
        Assert.assertTrue(provider.checkValid(sql));


        sql = "lock table dsdfsdf read";
        result = provider.check(sql);
        if (result.getViolations().size() > 0) {
            Violation violation = result.getViolations().get(0);
            System.out.println("error () : " + violation.getMessage());
        }
        Assert.assertTrue(provider.checkValid(sql));


        sql = "lock table dsdfsdf read local";
        result = provider.check(sql);
        if (result.getViolations().size() > 0) {
            Violation violation = result.getViolations().get(0);
            System.out.println("error () : " + violation.getMessage());
        }
        Assert.assertTrue(provider.checkValid(sql));


    }

}
