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
package com.alibaba.druid.bvt.sql.mysql;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcUtils;

public class MySqlFormatTest2 extends TestCase {

    public void test_0() throws Exception {
        String text = "SELECT cq.uuid FROM ce_queue cq "
                + "WHERE cq.status = ? AND NOT EXISTS (SELECT ? FROM ce_queue cq2 WHERE cq.component_uuid = cq2.component_uuid AND cq2.status <> ?) "
                + "ORDER BY cq.created_at ASC, cq.id ASC";
        Assert.assertEquals("SELECT cq.uuid"
                + "\nFROM ce_queue cq"
                + "\nWHERE cq.status = ?"
                + "\n\tAND NOT EXISTS ("
                + "\n\t\tSELECT ?"
                + "\n\t\tFROM ce_queue cq2"
                + "\n\t\tWHERE cq.component_uuid = cq2.component_uuid"
                + "\n\t\t\tAND cq2.status <> ?"
                + "\n\t)"
                + "\nORDER BY cq.created_at ASC, cq.id ASC", SQLUtils.format(text, JdbcUtils.MYSQL));
    }
}
