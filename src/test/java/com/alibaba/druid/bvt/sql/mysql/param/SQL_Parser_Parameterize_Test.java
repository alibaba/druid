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
package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class SQL_Parser_Parameterize_Test extends TestCase {
    public void test_parameterized() throws Exception {
        final String dbType = JdbcConstants.MYSQL;

        List<Object> outParameters = new ArrayList<Object>();
        String sql = "select * from t where id = 101 and age = 102 or name = 'wenshao'";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType, outParameters);
        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "WHERE id = ?\n" +
                "\tAND age = ?\n" +
                "\tOR name = ?", psql);

        assertEquals(3, outParameters.size());
        assertEquals(101, outParameters.get(0));
        assertEquals(102, outParameters.get(1));
        assertEquals("wenshao", outParameters.get(2));
    }

    public void test_parameterized_2() throws Exception {
        final String dbType = JdbcConstants.MYSQL;

        List<Object> outParameters = new ArrayList<Object>();
        String sql = "select * from t where id = 101 or id in (1,2,3,4)";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType, null, outParameters, VisitorFeature.OutputParameterizedQuesUnMergeInList);
        assertEquals("SELECT *\n" +
                     "FROM t\n" +
                     "WHERE id = ?\n" +
                     "\tOR id IN ( ?, ?, ?, ?)", psql);

        assertEquals(5, outParameters.size());
        assertEquals(101, outParameters.get(0));
        assertEquals(1, outParameters.get(1));
        assertEquals(2, outParameters.get(2));
        assertEquals(3, outParameters.get(3));
        assertEquals(4, outParameters.get(4));
    }
}
