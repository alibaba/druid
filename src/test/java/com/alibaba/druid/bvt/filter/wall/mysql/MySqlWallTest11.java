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

import com.alibaba.druid.wall.WallUtils;

/**
 * SQLServerWallTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class MySqlWallTest11 extends TestCase {

    public void test_true() throws Exception {
        assertTrue(WallUtils.isValidateMySql(//
        "SELECT *, MATCH(question_content_fulltext) " + //
                "AGAINST('2015431867 2636826089 3807520117 2796321160 2615920174' IN BOOLEAN MODE) AS score " + //
                "FROM aws_question " + //
                "WHERE MATCH(question_content_fulltext) " + //
                "   AGAINST('2015431867 2636826089 3807520117 2796321160 2615920174' IN BOOLEAN MODE)  " + //
                "ORDER BY score DESC, agree_count DESC LIMIT 10")); //
    }
}
