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
package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsSelectTest extends TestCase {

    public void test_distribute_by() throws Exception {
        String sql = "select region from sale_detail distribute by region;";//
        Assert.assertEquals("SELECT region" //
                            + "\nFROM sale_detail" //
                            + "\nDISTRIBUTE BY region;", SQLUtils.formatOdps(sql));
    }
    
    public void test_distribute_by_1() throws Exception {
        String sql = " select region from sale_detail distribute by region sort by f1;";//
        Assert.assertEquals("SELECT region" //
                            + "\nFROM sale_detail" //
                            + "\nDISTRIBUTE BY region SORT BY f1;", SQLUtils.formatOdps(sql));
    }
    
    public void test_distribute_by_2() throws Exception {
        String sql = " select region from sale_detail distribute by region sort by f1 asc;";//
        Assert.assertEquals("SELECT region" //
                            + "\nFROM sale_detail" //
                            + "\nDISTRIBUTE BY region SORT BY f1 ASC;", SQLUtils.formatOdps(sql));
        Assert.assertEquals("select region" //
                            + "\nfrom sale_detail" //
                            + "\ndistribute by region sort by f1 asc;", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }

}
