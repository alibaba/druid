/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;

/**
 * @author yako 2014年10月21日 下午5:07:37
 */
public class WallPerformanceTest_1 extends TestCase {

    @Override
    protected void setUp() throws Exception {
        // 预热
        WallProvider provider = new MySqlWallProvider();
        provider.setBlackListEnable(false);
        provider.setWhiteListEnable(false);
        for (int i = 0; i < 1000 * 100; i++) {
            provider.checkValid("select sum(payment_ft) from order_goods where order_id=1 AND (SELECT 4552 FROM(SELECT COUNT(*),CONCAT(CHAR(58,107,98,119,58),(SELECT (CASE WHEN (4552=4552) THEN 1 ELSE 0 END)),CHAR(58,98,105,101,58),FLOOR(RAND(0)*2))x FROM information_schema.tables GROUP BY x)a)");
        }
    }

    public void test_1() throws Exception {
        String sql = "SELECT plaza_id,plaza_name,plaza_logo,plaza_address,plaza_averageMoney,plaza_discount,2*ASIN(SQRT(POW(SIN(PI()*(22.54605355-plaza_latitude)/360),2)+COS(PI()*22.54605355/180)*COS(plaza_latitude*PI()/180)*POW(SIN(PI()*(114.02597366-plaza_longitude)/360),2)))*6378.137*1000 as jl FROM plaza where 2*ASIN(SQRT(POW(SIN(PI()*(22.54605355-plaza_latitude)/360),2)+COS(PI()*22.54605355/180)*COS(plaza_latitude*PI()/180)*POW(SIN(PI()*(114.02597366-plaza_longitude)/360),2)))*6378.137*1000<= 5000 and plaza_check=2 ORDER BY jl ASC";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.MYSQL));
        for (int i = 0; i < 5; i++) {
            System.out.println("use time：" + this.evaluate(sql, "mysql", 3l));
        }
    }

    public void test_2() throws Exception {
        String sql = "SELECT   TECH_ID,    NAME,   ALIAS_NAME,     GENDER,     MOBILE,     PASSWORD,   CITY_NAME,  ADDRESS,    LONGITUDE,  LATITUDE,   HEAD_PORTRAIT_URL,  ID_CARD,    ID_BEFORE_URL,  ID_BACK_URL,    DATE_FORMAT(CREATE_TIME, '%Y%m%d%H%i%s'),   DATE_FORMAT(OPERATION_TIME, '%Y%m%d%H%i%s'),    STATE,  REMARK,     CONVERT(SERVICE_RANGE,char),    CONVERT(round(DISTANCE),char),  CONVERT(INTEGRAL,char), CONVERT(SUCCESS_MONEY,char),    CONVERT(success_orders,char),   CONVERT(round(success_money/success_orders),char) avg_money,    PRODUCT_NUMS    from (  SELECT  a.*,    b.INTEGRAL, b.SUCCESS_MONEY,    b.success_orders,   acos(sin('NULL'                        * PI() / 180) * sin(latitude * PI() / 180) + cos('NULL'                        * PI() / 180) * cos(latitude * PI() / 180) * cos('NULL'                          * PI() / 180 - longitude * PI() / 180)) * 6371000 distance,  CONVERT(count(c.tech_id),char) PRODUCT_NUMS FROM tech_info a, tech_account_book b, tech_service_product_info c,tech_service_type_info d WHERE a.latitude <= ('NULL'                        * PI() / 180 + 'NULL'                      / 6371000) * 180 / PI()   AND a.latitude >= ('NULL'                        * PI() / 180 - 'NULL'                      / 6371000) * 180 / PI()     AND a.longitude <= ('NULL'                         * PI() / 180 + ASIN(SIN('NULL'                      / 6371000) / COS('NULL'                        * PI() / 180))) * 180 / PI()  AND a.longitude >= ('NULL'                         * PI() / 180 - ASIN(SIN('NULL'                      / 6371000) / COS('NULL'                        * PI() / 180))) * 180 / PI()  AND a.tech_id = b.tech_id and a.tech_id=c.tech_id and c.state=1 and a.tech_id=d.tech_id and d.service_type_id='NULL'                           and c.service_type_id='NULL'                             GROUP BY a.tech_id) T  order by distance limit 0,20";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.MYSQL));
        for (int i = 0; i < 5; i++) {
            System.out.println("use time：" + this.evaluate(sql, "mysql", 3l));
        }
    }

    public Long evaluate(String sql, String dbType, Long num) {
        if (sql == null || dbType == null) {
            return new Long(-1);
        }

        try {
            WallProvider provider = null;
            if ("mssql".equalsIgnoreCase(dbType)) {
                provider = new SQLServerWallProvider();
            } else if ("mysql".equalsIgnoreCase(dbType)) {
                provider = new MySqlWallProvider();
            } else {
                return new Long(-1);
            }

            provider.getConfig().setStrictSyntaxCheck(false);
            provider.getConfig().setMultiStatementAllow(true);
            provider.getConfig().setConditionAndAlwayTrueAllow(true);
            provider.getConfig().setConditionAndAlwayFalseAllow(true);
            provider.getConfig().setNoneBaseStatementAllow(true);
            provider.getConfig().setLimitZeroAllow(true);
            provider.getConfig().setConditionDoubleConstAllow(true);

            provider.getConfig().setCommentAllow(true);
            provider.getConfig().setSelectUnionCheck(false);

            // add by yanhui.liyh
            provider.setBlackListEnable(false);
            provider.setWhiteListEnable(false);

            long time = System.nanoTime();
            for (int i = 0; i < num; i++) {
                provider.checkValid(sql);
            }

            return (System.nanoTime() - time) / num / 1000;
        } catch (Exception e) {
            return new Long(-1);
        }

    }

}
