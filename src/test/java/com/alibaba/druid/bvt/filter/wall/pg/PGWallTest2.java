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
package com.alibaba.druid.bvt.filter.wall.pg;

import com.alibaba.druid.wall.WallUtils;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * SQLServerWallTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class PGWallTest2 extends TestCase {
    public void test_wall() throws Exception {
        assertTrue(WallUtils.isValidatePostgres(//
        "select u.id, (\n" +
                "\tWITH RECURSIVE users AS (\n" +
                "\t\tselect id from t_user limit 1\n" +
                "\t) select id from users\n" +
                ") from t_user u \n" +
                "limit 1;"));
    }

    public void test_wall_2() throws Exception {
        assertTrue(WallUtils.isValidatePostgres(//
                "select 1 from t_user u\n" +
                        "join (\n" +
                        "\tWITH RECURSIVE users AS (\n" +
                        "\t\tselect id from t_user limit 1\n" +
                        "\t) select id from users\n" +
                        ") t on u.id = t.id;"));
    }
}
