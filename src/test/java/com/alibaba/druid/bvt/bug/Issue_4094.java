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
package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

import junit.framework.TestCase;
import org.junit.Assert;

/**
 * https://github.com/alibaba/druid/issues/4094
 */
public class Issue_4094 extends TestCase {

   public void test_betweent_lost(){
       // 此sql可以正常执行
       String sql ="SELECT B.CUSTOMER_ID customerId,B.CN_NAME NAME,OPENING_DATE,CLOSING_DATE,count(A.OPENING_DATE) over (partition BY B.CUSTOMER_ID ORDER BY trunc(A.OPENING_DATE,'mm') RANGE BETWEEN interval '3' month (4) preceding AND CURRENT ROW) AS createCount,count(A.CLOSING_DATE) over (partition BY B.CUSTOMER_ID ORDER BY trunc(NVL(A.CLOSING_DATE,SYSDATE),'mm') RANGE BETWEEN interval '3' month (4) preceding AND CURRENT ROW) AS cancelCount,row_number () over (partition BY B.CUSTOMER_ID ORDER BY trunc(A.OPENING_DATE) DESC) AS row_flg FROM account_info A,CUSTOMER_INFO B WHERE A.CUSTOMER_ID=B.CUSTOMER_ID";
       String format = SQLUtils.format(sql, DbType.oracle);
       assertEquals("SELECT B.CUSTOMER_ID AS customerId, B.CN_NAME AS NAME, OPENING_DATE, CLOSING_DATE\n"
           + "\t, count(A.OPENING_DATE) OVER (PARTITION BY B.CUSTOMER_ID ORDER BY trunc(A.OPENING_DATE, 'mm') RANGE  "
           + "BETWEEN INTERVAL '3' MONTH(4) PRECEDING AND CURRENT ROW) AS createCount\n"
           + "\t, count(A.CLOSING_DATE) OVER (PARTITION BY B.CUSTOMER_ID ORDER BY trunc(NVL(A.CLOSING_DATE, SYSDATE),"
           + " 'mm') RANGE  BETWEEN INTERVAL '3' MONTH(4) PRECEDING AND CURRENT ROW) AS cancelCount\n"
           + "\t, row_number() OVER (PARTITION BY B.CUSTOMER_ID ORDER BY trunc(A.OPENING_DATE) DESC) AS row_flg\n"
           + "FROM account_info A, CUSTOMER_INFO B\n"
           + "WHERE A.CUSTOMER_ID = B.CUSTOMER_ID", format);
   }
}