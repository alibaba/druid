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
package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsSelectTest2 extends TestCase {

    public void test_distribute_by() throws Exception {
        String sql = "SELECT user_id,user_flag,cat1_id,wireless_client_type,alipay_num,last_buy_time,md5,my_udf_001('t_datax_odps2ots_resource_tcif_dmp_user_topup_d',md5) AS datax_pt FROM mytable_001 WHERE ds='20150819'  DISTRIBUTE BY cast (datax_pt as BIGINT) SORT BY md5,user_id,user_flag,cat1_id,wireless_client_type";//
        Assert.assertEquals("SELECT user_id"
                + "\n\t, user_flag"
                + "\n\t, cat1_id"
                + "\n\t, wireless_client_type"
                + "\n\t, alipay_num"
                + "\n\t, last_buy_time"
                + "\n\t, md5"
                + "\n\t, my_udf_001('t_datax_odps2ots_resource_tcif_dmp_user_topup_d', md5) AS datax_pt"
                + "\nFROM mytable_001"
                + "\nWHERE ds = '20150819'"
                + "\nDISTRIBUTE BY CAST(datax_pt AS BIGINT) SORT BY md5, user_id, user_flag, cat1_id, wireless_client_type", SQLUtils.formatOdps(sql));
    }
    

}
