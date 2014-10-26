/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
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
            System.out.println("--1--use time：" + this.evaluate(sql, "mysql", 1000l));
        }
    }

    public void test_2() throws Exception {
        String sql = "SELECT TECH_ID, NAME, ALIAS_NAME, GENDER, MOBILE, PASSWORD, CITY_NAME,  ADDRESS,  LONGITUDE,  LATITUDE, HEAD_PORTRAIT_URL,  ID_CARD,  ID_BEFORE_URL,  ID_BACK_URL,  DATE_FORMAT(CREATE_TIME, '%Y%m%d%H%i%s'), DATE_FORMAT(OPERATION_TIME, '%Y%m%d%H%i%s'),  STATE,  REMARK, CONVERT(SERVICE_RANGE,char),  CONVERT(round(DISTANCE),char),  CONVERT(INTEGRAL,char), CONVERT(SUCCESS_MONEY,char),  CONVERT(success_orders,char), CONVERT(round(success_money/success_orders),char) avg_money,  PRODUCT_NUMS  from (  SELECT  a.*,  b.INTEGRAL, b.SUCCESS_MONEY,  b.success_orders, acos(sin('NULL'    * PI() / 180) * sin(latitude * PI() / 180) + cos('NULL'    * PI() / 180) * cos(latitude * PI() / 180) * cos('NULL'  * PI() / 180 - longitude * PI() / 180)) * 6371000 distance,  CONVERT(count(c.tech_id),char) PRODUCT_NUMS FROM tech_info a, tech_account_book b, tech_service_product_info c,tech_service_type_info d WHERE a.latitude <= ('NULL'    * PI() / 180 + 'NULL'  / 6371000) * 180 / PI() AND a.latitude >= ('NULL'    * PI() / 180 - 'NULL'  / 6371000) * 180 / PI() AND a.longitude <= ('NULL'   * PI() / 180 + ASIN(SIN('NULL'  / 6371000) / COS('NULL'    * PI() / 180))) * 180 / PI()  AND a.longitude >= ('NULL'   * PI() / 180 - ASIN(SIN('NULL'  / 6371000) / COS('NULL'    * PI() / 180))) * 180 / PI()  AND a.tech_id = b.tech_id and a.tech_id=c.tech_id and c.state=1 and a.tech_id=d.tech_id and d.service_type_id='NULL'   and c.service_type_id='NULL'   GROUP BY a.tech_id) T  order by distance limit 0,20";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.MYSQL));
        for (int i = 0; i < 5; i++) {
            System.out.println("--2--use time：" + this.evaluate(sql, "mysql", 1000l));
        }
    }

    public void test_3() throws Exception {
        String sql = "create table EcOrder736ad4 ( [St_flag]  int not null default(0), [Cust_flag]  int not null default(0), [Pj_id]  bigint not null default(0), [Store_id]  bigint not null default(0), [Rtn]  int not null default(0), [Ec_type]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ec_nick]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Cust_no]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ship_to_no]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ship_to]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ec_order_no]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ec_order_line_no]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ec_ref_order_no]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ec_ref_order_line_no]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Sku_key]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Sku_cd]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Cust_name]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [District]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Address]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [City]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Province]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Country]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Zip]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Phone]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Mobile]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Email]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''))";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.SQL_SERVER));
        for (int i = 0; i < 5; i++) {
            System.out.println("--3--use time：" + this.evaluate(sql, "mssql", 1000l));
        }
    }

    public void test_4() throws Exception {
        String sql = "select top 1 '210002428' as currentMaterialid, inventory.warehouse,commoditysummary.materialname,commoditysummary.goodsname,inventory.customname,inboundnbr,commoditysummary.costunitprice from inventory  left join commoditysummary on commoditysummary.materialname=inventory.materialid and commoditysummary.customname=inventory.customname where (materialid='210002428' or CommoditySummary.KeyWord = (select keyword from commoditysummarykeyword where materialid = '210002428' and bak in('苏泊尔官方旗舰店') )) and inventory.customname in('苏泊尔官方旗舰店') and warehouse not in(select warehousename from logisticswarehouse where warehousetype ='物流宝仓' ) and dynamicsum >= 1 union select top 1 '运费' as currentMaterialid, inventory.warehouse,commoditysummary.materialname,commoditysummary.goodsname,inventory.customname,inboundnbr,commoditysummary.costunitprice from inventory  left join commoditysummary on commoditysummary.materialname=inventory.materialid and commoditysummary.customname=inventory.customname where (materialid='运费' or CommoditySummary.KeyWord = (select keyword from commoditysummarykeyword where materialid = '运费' and bak in('苏泊尔官方旗舰店') )) and inventory.customname in('苏泊尔官方旗舰店') and warehouse not in(select warehousename from logisticswarehouse where warehousetype ='物流宝仓' ) and dynamicsum >= 1 union select top 1 'ZPCGJ002' as currentMaterialid, inventory.warehouse,commoditysummary.materialname,commoditysummary.goodsname,inventory.customname,inboundnbr,commoditysummary.costunitprice from inventory  left join commoditysummary on commoditysummary.materialname=inventory.materialid and commoditysummary.customname=inventory.customname where (materialid='ZPCGJ002' or CommoditySummary.KeyWord = (select keyword from commoditysummarykeyword where materialid = 'ZPCGJ002' and bak in('苏泊尔官方旗舰店') )) and inventory.customname in('苏泊尔官方旗舰店') and warehouse not in(select warehousename from logisticswarehouse where warehousetype ='物流宝仓' ) and dynamicsum >= 1 union select top 1 'ZPXWB001' as currentMaterialid, inventory.warehouse,commoditysummary.materi";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.SQL_SERVER));
        for (int i = 0; i < 5; i++) {
            System.out.println("--4--use time：" + this.evaluate(sql, "mssql", 1000l));
        }
    }

    public void test_5() throws Exception {
        String sql = "select a.Store_id,a.Ec_order_no,a.Ec_order_line_no,a.Pj_id,  Usm_sku_id=isnull(usm.Sku_id,0),Cu_unit_id=isnull(cu.Unit_id,0),Sp_unit_id=isnull(sp.Unit_id,0), oh.Order_head_id,oh.Order_status,oh.Order_no,Cust_id=oh.Cust_id,Ship_to_id=oh.Ship_to_id,Od_ref_order_id=oh.Ref_order_id,  oh.add_name,oh.Phone,oh.Fax,oh.Country,oh.Province,oh.City,oh.Area_or_loc,oh.Address_1,oh.Zip,oh.Address_2,  ol.Order_line_id,ol.Order_line_status,Sku_id=ol.Sku_id,P_discount_price=ol.P_discount_price,ol.Dn_if_id,Od_ref_order_line_id=ol.Ref_order_line_id from [EcOrder55be71] a              left  join Unit cu with(nolock) on cu.Pj_id = a.Pj_id and cu.Unit_no = a.Cust_no and cu.Unit_status>=0 and Unit_type='CU'  left  join Unit_sku_master usm with(nolock) on usm.Orig_unit_id=a.Store_id and usm.Unit_sku_cd=a.Sku_key and usm.Status>=0 and Io_type='IN'  left  join Unit sp with(nolock) on a.Store_id = sp.Parent_unit_id and a.Ship_to_no = sp.Unit_no and sp.Unit_status>=0  left  join Scm_order_head oh with(nolock) on oh.Rtn=a.Rtn and oh.Orig_order_no=a.Ec_order_no and oh.Supp_id=a.Store_id and oh.import_type='IFS' and oh.Order_status>=0  left  join Scm_order_line ol with(nolock) on ol.Order_head_id = oh.Order_head_id and ol.Order_line_no=a.Ec_order_line_no and ol.Order_line_status >= 0 order by a.Store_id,a.Ec_order_no,a.Ec_order_line_no,ol.create_time";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.SQL_SERVER));
        for (int i = 0; i < 5; i++) {
            System.out.println("--5--use time：" + this.evaluate(sql, "mssql", 1000l));
        }
    }

    public void test_6() throws Exception {
        String sql = "select shoplocati0_.shop_location_id as shop1_25_0_, promotionp1_.id as id9_1_, shoplocati0_.latitude as latitude25_0_, shoplocati0_.longitude as longitude25_0_, shoplocati0_.remark as remark25_0_, shoplocati0_.shop_id as shop5_25_0_, promotionp1_.product_id as product5_9_1_, promotionp1_.remark as remark9_1_, promotionp1_.save_time as save3_9_1_, promotionp1_.status as status9_1_ from eShop.shop_location shoplocati0_, eShop.promotion_product promotionp1_, eShop.v_product_positive_ratio vproductpo2_, eShop.v_product_evaluate_num vproductev3_, eShop.product product4_, eShop.shop shop5_ where promotionp1_.product_id=product4_.product_id and shoplocati0_.shop_id=shop5_.shop_id and shoplocati0_.shop_id=product4_.shop_id and promotionp1_.product_id=vproductpo2_.product_id and promotionp1_.product_id=vproductev3_.product_id and shop5_.status='1' and product4_.status='1' and promotionp1_.status='1' and vproductev3_.total_num>='10' and acos(sin(36.686290*3.1415/180)*sin(shoplocati0_.latitude*3.1415/180)+cos(36.686290*3.1415/180)*cos(shoplocati0_.latitude*3.1415/180)*cos(117.080900*3.1415/180-(shoplocati0_.longitude*3.1415/180)))*6380<=15.000000 and vproductpo2_.positive_ratio>=1.000000 order by acos(sin(36.686290*3.1415/180)*sin(shoplocati0_.latitude*3.1415/180)+cos(36.686290*3.1415/180)*cos(shoplocati0_.latitude*3.1415/180)*cos(117.080900*3.1415/180-(shoplocati0_.longitude*3.1415/180)))*6380 asc, promotionp1_.id desc limit 10";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.MYSQL));
        for (int i = 0; i < 5; i++) {
            System.out.println("--6--use time：" + this.evaluate(sql, "mysql", 1000l));
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
