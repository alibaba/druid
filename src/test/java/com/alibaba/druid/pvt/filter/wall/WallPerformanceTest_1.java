/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.pvt.filter.wall;

import junit.framework.TestCase;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;

/**
 * @author yako 2014年10月21日 下午5:07:37
 */
public class WallPerformanceTest_1 extends TestCase {
    
    private int times = 3;

    @Override
    protected void setUp() throws Exception {
        // 预热
        WallProvider provider = new MySqlWallProvider();
        provider.setBlackListEnable(false);
        provider.setWhiteListEnable(false);
        for (int i = 0; i < 1000; i++) {
            provider.checkValid("select sum(payment_ft) from order_goods where order_id=1 AND (SELECT 4552 FROM(SELECT COUNT(*),CONCAT(CHAR(58,107,98,119,58),(SELECT (CASE WHEN (4552=4552) THEN 1 ELSE 0 END)),CHAR(58,98,105,101,58),FLOOR(RAND(0)*2))x FROM information_schema.tables GROUP BY x)a)");
        }
    }

    public void test_1() throws Exception {
        String sql = "SELECT plaza_id,plaza_name,plaza_logo,plaza_address,plaza_averageMoney,plaza_discount,2*ASIN(SQRT(POW(SIN(PI()*(22.54605355-plaza_latitude)/360),2)+COS(PI()*22.54605355/180)*COS(plaza_latitude*PI()/180)*POW(SIN(PI()*(114.02597366-plaza_longitude)/360),2)))*6378.137*1000 as jl FROM plaza where 2*ASIN(SQRT(POW(SIN(PI()*(22.54605355-plaza_latitude)/360),2)+COS(PI()*22.54605355/180)*COS(plaza_latitude*PI()/180)*POW(SIN(PI()*(114.02597366-plaza_longitude)/360),2)))*6378.137*1000<= 5000 and plaza_check=2 ORDER BY jl ASC";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.MYSQL));
        for (int i = 0; i < times; i++) {
            System.out.println("--1--use time：" + this.evaluate(sql, "mysql", 1000l));
        }
    }

    public void test_2() throws Exception {
        String sql = "SELECT TECH_ID, NAME, ALIAS_NAME, GENDER, MOBILE, PASSWORD, CITY_NAME,  ADDRESS,  LONGITUDE,  LATITUDE, HEAD_PORTRAIT_URL,  ID_CARD,  ID_BEFORE_URL,  ID_BACK_URL,  DATE_FORMAT(CREATE_TIME, '%Y%m%d%H%i%s'), DATE_FORMAT(OPERATION_TIME, '%Y%m%d%H%i%s'),  STATE,  REMARK, CONVERT(SERVICE_RANGE,char),  CONVERT(round(DISTANCE),char),  CONVERT(INTEGRAL,char), CONVERT(SUCCESS_MONEY,char),  CONVERT(success_orders,char), CONVERT(round(success_money/success_orders),char) avg_money,  PRODUCT_NUMS  from (  SELECT  a.*,  b.INTEGRAL, b.SUCCESS_MONEY,  b.success_orders, acos(sin('NULL'    * PI() / 180) * sin(latitude * PI() / 180) + cos('NULL'    * PI() / 180) * cos(latitude * PI() / 180) * cos('NULL'  * PI() / 180 - longitude * PI() / 180)) * 6371000 distance,  CONVERT(count(c.tech_id),char) PRODUCT_NUMS FROM tech_info a, tech_account_book b, tech_service_product_info c,tech_service_type_info d WHERE a.latitude <= ('NULL'    * PI() / 180 + 'NULL'  / 6371000) * 180 / PI() AND a.latitude >= ('NULL'    * PI() / 180 - 'NULL'  / 6371000) * 180 / PI() AND a.longitude <= ('NULL'   * PI() / 180 + ASIN(SIN('NULL'  / 6371000) / COS('NULL'    * PI() / 180))) * 180 / PI()  AND a.longitude >= ('NULL'   * PI() / 180 - ASIN(SIN('NULL'  / 6371000) / COS('NULL'    * PI() / 180))) * 180 / PI()  AND a.tech_id = b.tech_id and a.tech_id=c.tech_id and c.state=1 and a.tech_id=d.tech_id and d.service_type_id='NULL'   and c.service_type_id='NULL'   GROUP BY a.tech_id) T  order by distance limit 0,20";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.MYSQL));
        for (int i = 0; i < times; i++) {
            System.out.println("--2--use time：" + this.evaluate(sql, "mysql", 1000l));
        }
    }

    public void test_3() throws Exception {
        String sql = "create table EcOrder736ad4 ( [St_flag]  int not null default(0), [Cust_flag]  int not null default(0), [Pj_id]  bigint not null default(0), [Store_id]  bigint not null default(0), [Rtn]  int not null default(0), [Ec_type]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ec_nick]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Cust_no]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ship_to_no]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ship_to]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ec_order_no]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ec_order_line_no]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ec_ref_order_no]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Ec_ref_order_line_no]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Sku_key]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Sku_cd]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Cust_name]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [District]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Address]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [City]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Province]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Country]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Zip]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Phone]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Mobile]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''), [Email]  nvarchar(4000) COLLATE Chinese_PRC_CI_AS  not null default(''))";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.SQL_SERVER));
        for (int i = 0; i < times; i++) {
            System.out.println("--3--use time：" + this.evaluate(sql, "mssql", 1000l));
        }
    }

    public void test_4() throws Exception {
        String sql = "select top 1 '210002428' as currentMaterialid, inventory.warehouse,commoditysummary.materialname,commoditysummary.goodsname,inventory.customname,inboundnbr,commoditysummary.costunitprice from inventory  left join commoditysummary on commoditysummary.materialname=inventory.materialid and commoditysummary.customname=inventory.customname where (materialid='210002428' or CommoditySummary.KeyWord = (select keyword from commoditysummarykeyword where materialid = '210002428' and bak in('苏泊尔官方旗舰店') )) and inventory.customname in('苏泊尔官方旗舰店') and warehouse not in(select warehousename from logisticswarehouse where warehousetype ='物流宝仓' ) and dynamicsum >= 1 union select top 1 '运费' as currentMaterialid, inventory.warehouse,commoditysummary.materialname,commoditysummary.goodsname,inventory.customname,inboundnbr,commoditysummary.costunitprice from inventory  left join commoditysummary on commoditysummary.materialname=inventory.materialid and commoditysummary.customname=inventory.customname where (materialid='运费' or CommoditySummary.KeyWord = (select keyword from commoditysummarykeyword where materialid = '运费' and bak in('苏泊尔官方旗舰店') )) and inventory.customname in('苏泊尔官方旗舰店') and warehouse not in(select warehousename from logisticswarehouse where warehousetype ='物流宝仓' ) and dynamicsum >= 1 union select top 1 'ZPCGJ002' as currentMaterialid, inventory.warehouse,commoditysummary.materialname,commoditysummary.goodsname,inventory.customname,inboundnbr,commoditysummary.costunitprice from inventory  left join commoditysummary on commoditysummary.materialname=inventory.materialid and commoditysummary.customname=inventory.customname where (materialid='ZPCGJ002' or CommoditySummary.KeyWord = (select keyword from commoditysummarykeyword where materialid = 'ZPCGJ002' and bak in('苏泊尔官方旗舰店') )) and inventory.customname in('苏泊尔官方旗舰店') and warehouse not in(select warehousename from logisticswarehouse where warehousetype ='物流宝仓' ) and dynamicsum >= 1 union select top 1 'ZPXWB001' as currentMaterialid, inventory.warehouse,commoditysummary.materi";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.SQL_SERVER));
        for (int i = 0; i < times; i++) {
            System.out.println("--4--use time：" + this.evaluate(sql, "mssql", 1000l));
        }
    }

    public void test_5() throws Exception {
        String sql = "select a.Store_id,a.Ec_order_no,a.Ec_order_line_no,a.Pj_id,  Usm_sku_id=isnull(usm.Sku_id,0),Cu_unit_id=isnull(cu.Unit_id,0),Sp_unit_id=isnull(sp.Unit_id,0), oh.Order_head_id,oh.Order_status,oh.Order_no,Cust_id=oh.Cust_id,Ship_to_id=oh.Ship_to_id,Od_ref_order_id=oh.Ref_order_id,  oh.add_name,oh.Phone,oh.Fax,oh.Country,oh.Province,oh.City,oh.Area_or_loc,oh.Address_1,oh.Zip,oh.Address_2,  ol.Order_line_id,ol.Order_line_status,Sku_id=ol.Sku_id,P_discount_price=ol.P_discount_price,ol.Dn_if_id,Od_ref_order_line_id=ol.Ref_order_line_id from [EcOrder55be71] a              left  join Unit cu with(nolock) on cu.Pj_id = a.Pj_id and cu.Unit_no = a.Cust_no and cu.Unit_status>=0 and Unit_type='CU'  left  join Unit_sku_master usm with(nolock) on usm.Orig_unit_id=a.Store_id and usm.Unit_sku_cd=a.Sku_key and usm.Status>=0 and Io_type='IN'  left  join Unit sp with(nolock) on a.Store_id = sp.Parent_unit_id and a.Ship_to_no = sp.Unit_no and sp.Unit_status>=0  left  join Scm_order_head oh with(nolock) on oh.Rtn=a.Rtn and oh.Orig_order_no=a.Ec_order_no and oh.Supp_id=a.Store_id and oh.import_type='IFS' and oh.Order_status>=0  left  join Scm_order_line ol with(nolock) on ol.Order_head_id = oh.Order_head_id and ol.Order_line_no=a.Ec_order_line_no and ol.Order_line_status >= 0 order by a.Store_id,a.Ec_order_no,a.Ec_order_line_no,ol.create_time";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.SQL_SERVER));
        for (int i = 0; i < times; i++) {
            System.out.println("--5--use time：" + this.evaluate(sql, "mssql", 1000l));
        }
    }

    public void test_6() throws Exception {
        String sql = "select shoplocati0_.shop_location_id as shop1_25_0_, promotionp1_.id as id9_1_, shoplocati0_.latitude as latitude25_0_, shoplocati0_.longitude as longitude25_0_, shoplocati0_.remark as remark25_0_, shoplocati0_.shop_id as shop5_25_0_, promotionp1_.product_id as product5_9_1_, promotionp1_.remark as remark9_1_, promotionp1_.save_time as save3_9_1_, promotionp1_.status as status9_1_ from eShop.shop_location shoplocati0_, eShop.promotion_product promotionp1_, eShop.v_product_positive_ratio vproductpo2_, eShop.v_product_evaluate_num vproductev3_, eShop.product product4_, eShop.shop shop5_ where promotionp1_.product_id=product4_.product_id and shoplocati0_.shop_id=shop5_.shop_id and shoplocati0_.shop_id=product4_.shop_id and promotionp1_.product_id=vproductpo2_.product_id and promotionp1_.product_id=vproductev3_.product_id and shop5_.status='1' and product4_.status='1' and promotionp1_.status='1' and vproductev3_.total_num>='10' and acos(sin(36.686290*3.1415/180)*sin(shoplocati0_.latitude*3.1415/180)+cos(36.686290*3.1415/180)*cos(shoplocati0_.latitude*3.1415/180)*cos(117.080900*3.1415/180-(shoplocati0_.longitude*3.1415/180)))*6380<=15.000000 and vproductpo2_.positive_ratio>=1.000000 order by acos(sin(36.686290*3.1415/180)*sin(shoplocati0_.latitude*3.1415/180)+cos(36.686290*3.1415/180)*cos(shoplocati0_.latitude*3.1415/180)*cos(117.080900*3.1415/180-(shoplocati0_.longitude*3.1415/180)))*6380 asc, promotionp1_.id desc limit 10";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.MYSQL));
        for (int i = 0; i < times; i++) {
            System.out.println("--6--use time：" + this.evaluate(sql, "mysql", 1000l));
        }
    }

    public void test_7() throws Exception {
        String sql = "select * from( select  a.*,(select WorkOrderName from  WorkOrder where WorkOrder.WorkOrderID=a.WorkOrderID) as WorkOrderName, (select TrainName from  Train where Train.TrainID=a.TrainID) as TrainName, (select MemberName from  MemberInfo where MemberInfo.MemberID=a.MemberID) as MemberName, (select UserName from  SYS_UserInfo where SYS_UserInfo.UserID=a.TaskCreaterID) as UserInfo_UserName, (select ProcessName from  SYS_ProcessDefine where SYS_ProcessDefine.ProcessID=a.ProcessID) as ProcessName, (select ShopName from  shop where shop.ShopID=a.ShopID) as ShopName, (select UserName from  SYS_UserInfo where SYS_UserInfo.UserID=a.AssessID) as Assess_UserName, (select UserName from  SYS_UserInfo where SYS_UserInfo.UserID=a.Executuserid) as Executuser_UserName,  ROW_NUMBER() OVER(order by a.TaskID desc,a.TaskCreateDate asc) as row  from  [Task] as a where AccountID=48  and  (( a.TaskExecutorID = 12 and  a.TaskStatus !='2' and IFValid=1  ) or a.TaskID in   (select   a.TaskID  from  [Task] as a ,WorkOLDTaskMould as b where   a.TaskMouldOLDID=b.TaskMouldOLDID and  a.TaskExecutorID=0 and a.TaskStatus=0    and (  b.Deal_User like '%,12,%'     or b.Deal_Dept   like '%,18,%'  or b.Deal_Dept   like '%,53,%'  or b.Deal_Dept   like '%,129,%'  or b.Deal_Dept   like '%,151,%'  or b.Deal_Dept   like '%,79,%'  or b.Deal_Dept   like '%,116,%'  or b.Deal_Dept   like '%,117,%'  or b.Deal_Post  like '%,52,%'  or b.Deal_Post  like '%,6,%'  or b.Deal_Post  like '%,12,%'  or b.Deal_Post  like '%,13,%'  or b.Deal_Post  like '%,30,%'  or b.Deal_Post  like '%,64,%'  or b.Deal_Post  like '%,56,%'  or b.Deal_Group   like '%,6,%'  or b.Deal_Group   like '%,12,%'  or b.Deal_Group   like '%,14,%' ))) ) a where row between  1  and 100";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.MYSQL));
        for (int i = 0; i < times; i++) {
            System.out.println("--7--use time：" + this.evaluate(sql, "mysql", 1000l));
        }
    }

    public void test_8() throws Exception {
        String sql = "SELECT pr.order_no,pr.receipt_no,pr.datetime, pt.type_name,pr.amount,u.username FROM   payments_record  pr  inner join  tasks t on t.order_no= pr.order_no  inner join orders o on o.order_number=t.order_no inner join pay_type pt on pt.id=pr.payment_type inner join users u on u.id=pr.operator_id where t.order_no=o.order_number  and o.order_status=4 and t.task_status=6 and o.station_collection=1  and pr.client_status='success'  and pr.operator_id=29 and (pt.id=1 or pt.id=2) and pr.datetime between '2014-10-22 00:00:00' and '2014-10-22 23:59:59'";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.MYSQL));
        for (int i = 0; i < times; i++) {
            System.out.println("--8--use time：" + this.evaluate(sql, "mysql", 1000l));
        }
    }

    public void test_9() throws Exception {
        String sql = "SELECT  COUNT(*)  FROM  ReportSalesOrderList t0 LEFT JOIN ReportProductList t1 ON t0.SkuCode = t1.SkuCode WHERE  (ShopId = 0 OR 0 = 0) AND (ShopOrderNo = '' OR '' = '') AND (CustomerName LIKE CONCAT ('%', '', '%') OR '' = '')AND (t0.OrderType = '0' OR '0' = '0')AND (     (t0.orderStatus >0 AND t0.orderStatus <=45 AND 10 = 20 ) OR      (t0.orderStatus >45 AND t0.orderStatus <= 60 AND 10 = 30 ) OR     (t0.orderStatus >60 AND t0.orderStatus <= 70 AND 10 = 40 ) OR      (t0.orderStatus >70 AND t0.orderStatus <= 90 AND 10 = 50 )OR 10 = 10 ) AND (     ('30' = '10' AND t0.ShopPayTime >= '2014-10-01 00:00:00 ' AND t0.ShopPayTime < '2014-10-23 00:00:00 ' ) OR      ('30' = '20' AND t0.DeliveryTime >= '2014-10-01 00:00:00 ' AND t0.DeliveryTime < '2014-10-23 00:00:00 ' ) OR      ('30' = '30' AND t0.EndTime >= '2014-10-01 00:00:00 ' AND t0.EndTime < '2014-10-23 00:00:00 ' ) OR      ('30' = '40' AND t0.RefundTime >= '2014-10-01 00:00:00 ' AND t0.RefundTime < '2014-10-23 00:00:00 ' ) ) GROUP BY t0.OrderNo HAVING  ( GROUP_CONCAT(t1.ItemCode) LIKE CONCAT ('%', '', '%') OR GROUP_CONCAT(t1.ItemName) LIKE CONCAT ('%', '', '%') OR GROUP_CONCAT(t1.SkuCode)  LIKE CONCAT ('%', '', '%') OR GROUP_CONCAT(DISTINCT t1.SkuName) LIKE CONCAT ('%', '', '%')  OR '' = '') AND (GROUP_CONCAT(t1.BrandName)  LIKE CONCAT ('%', '', '%') Or '' = '')AND ('10' = '10' OR      ('10'= '20' AND GROUP_CONCAT(t0.RefundType) IS NULL )     OR('10' = '30' AND GROUP_CONCAT(t0.RefundType) != ''))";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.MYSQL));
        for (int i = 0; i < times; i++) {
            System.out.println("--9--use time：" + this.evaluate(sql, "mysql", 1000l));
        }
    }

    public void test_10() throws Exception {
        String sql = "insert into  saleB_63ceb9cbab074f3f91dda04a3fe79a57_z (billid,tblastmodate,seller_CourierId,r3_billtype,tags_color,seller_momo,buyer_momo,buyer_province,buyer_city,buyer_area,buyer_mail_code,post_fee,ele_shopname,ele_sordercode,type,codfee,codfamt,ExpressCode,PointFee,invoiceType,invoicePayer,invoiceContent) select 719685,Modified,0,isnull(r3_billtype,''),isnull(SellerFlag,''),isnull(SellerMemo,''),isnull(BuyerMessage,''),isnull(ReceiverState,''),isnull(ReceiverCity,''),isnull(ReceiverDistrict,''),isnull(ReceiverZip,''),isnull(PostFee,''),isnull(Title,''),isnull(Tid,''),isnull(type,''),isnull(codfee,''),isnull(ExpressAgencyFee,''),isnull(Ext5,''),isnull(PointFee,''),isnull(Ext1,''),isnull(Ext2,''),isnull(Ext3,'') from taobao_order where r3_billid=837361947087831 and shopConfigid=2";
        // System.out.println("sql: \n" + ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.MYSQL));
        for (int i = 0; i < times; i++) {
            System.out.println("--10--use time：" + this.evaluate(sql, "mssql", 1000l));
        }
    }

    public void test_11() throws Exception {
        String sql = "create view tempview_CTbillflow_material as SELECT billflow.billtype, billflow.billid, billflow.itemno, billflow.MaterialID, billflow.quantity,  billflow.ioflag, billflow.storeid, content.CT1, content.CT2 FROM billflow INNER JOIN content ON billflow.billtype = content.tableid AND billflow.billid = content.billid AND  billflow.itemno = content.itemno WHERE billflow.billflowid>0  and billflow.storeid<>1 and billflow.storeid<>2 and billflow.storeid<>3 and billflow.storeid<>5 and billflow.storeid<>7 and billflow.storeid<>8 and billflow.storeid<>9 and billflow.storeid<>10 and billflow.storeid<>11 and (1=2 or billflow.MaterialID=893 or billflow.MaterialID=904 or billflow.MaterialID=80 or billflow.MaterialID=8 or billflow.MaterialID=8) UNION ALL SELECT billflow.billtype, billflow.billid, billflow.itemno, billflow.MaterialID, billflow.quantity,  billflow.ioflag, billflow.storeid, content.CT1, content.CT2 FROM billflow INNER JOIN content ON billflow.billtype = content.tableid AND billflow.billid = content.billid AND  billflow.itemno = content.itemno inner join Material on billflow.MaterialID=Material.MaterialID and Material.feature=1 WHERE billflow.billflowid< 0 and (1=2 or content.CT1<>'' or content.CT2<>'')  and billflow.storeid<>1 and billflow.storeid<>2 and billflow.storeid<>3 and billflow.storeid<>5 and billflow.storeid<>7 and billflow.storeid<>8 and billflow.storeid<>9 and billflow.storeid<>10 and billflow.storeid<>11 and (1=2 or billflow.MaterialID=893 or billflow.MaterialID=904 or billflow.MaterialID=80 or billflow.MaterialID=8 or billflow.MaterialID=8) UNION ALL SELECT billflow.billtype, billflow.billid, billflow.itemno, billflow.MaterialID, billflow.quantity,  billflow.ioflag, billflow.storeid, content.CT1, content.CT2 FROM billflow INNER JOIN content ON billflow.billtype = content.tableid AND billflow.billid = content.billid AND  billflow.itemno = content.itemno inner join Material on billflow.MaterialID=Material.MaterialID and Mater";
        for (int i = 0; i < times; i++) {
            System.out.println("--11--use time：" + this.evaluate(sql, "mssql", 1000l));
        }
    }

    public void test_12() throws Exception {
        String sql = "select b.*,c.county,c.shangquan,c.naturevar,directionsvar,fitmentvar,c.villagename,c.titles,c.address,c.smallpath,c.imgcount,c.hall,c.layer,c.totallayer,c.tags from (select * from(select rentid,room,updatetime,minarea,minprice,avgprice,labelstate,unixdate,isimgs,zongdianid,mendianid,zongdianval,mendianval, Row_Number() over(order by minarea desc,rentid desc) as RowID from house_rent_search_kunshan  where isaudit=0 and isdel=0 and state=0 and countyid=@countyid and nature=@nature and directions=@directions) as a where RowID>=1 and RowID<=20) as b inner join house_rent_list_kunshan as c on b.rentid=c.rentid order by minarea desc,rentid desc";
        for (int i = 0; i < times; i++) {
            System.out.println("--12--use time：" + this.evaluate(sql, "mssql", 1000l));
        }
    }

    public void test_13() throws Exception {
        String sql = "SELECT COUNT(*) cn1 FROM KUKA_SystemOrder   WHERE  1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  AND 1=1  ";
        for (int i = 0; i < times; i++) {
            System.out.println("--13--use time：" + this.evaluate(sql, "mssql", 1000l));
        }
    }
    
    public void test_14() throws Exception {
        String sql = "SELECT COUNT(*) FROM `jfz_futures_department` `t` WHERE (((((t.state=1) AND (t.city='2005')) AND (t.mode_weekend=1)) AND (t.service_stock=1)) AND (t.software_program=1)) AND (1=0 OR (t.lat BETWEEN 31.201582181818 AND 31.237945818182 AND t.lng BETWEEN 121.43558877778 AND 121.48003322222) OR (t.lat BETWEEN 31.389604181818 AND 31.425967818182 AND t.lng BETWEEN 121.33426577778 AND 121.37871022222) OR (t.lat BETWEEN 31.376165181818 AND 31.412528818182 AND t.lng BETWEEN 121.34206277778 AND 121.38650722222) OR (t.lat BETWEEN 31.351904181818 AND 31.388267818182 AND t.lng BETWEEN 121.34012277778 AND 121.38456722222) OR (t.lat BETWEEN 31.345213181818 AND 31.381576818182 AND t.lng BETWEEN 121.34662677778 AND 121.39107122222) OR (t.lat BETWEEN 31.332292181818 AND 31.368655818182 AND t.lng BETWEEN 121.35711877778 AND 121.40156322222) OR (t.lat BETWEEN 31.307896181818 AND 31.344259818182 AND t.lng BETWEEN 121.37307277778 AND 121.41751722222) OR (t.lat BETWEEN 31.309006181818 AND 31.345369818182 AND t.lng BETWEEN 121.38302577778 AND 121.42747022222) OR (t.lat BETWEEN 31.303238181818 AND 31.339601818182 AND t.lng BETWEEN 121.39258377778 AND 121.43702822222) OR (t.lat BETWEEN 31.291669181818 AND 31.328032818182 AND t.lng BETWEEN 121.39793777778 AND 121.44238222222) OR (t.lat BETWEEN 31.281487181818 AND 31.317850818182 AND t.lng BETWEEN 121.40074077778 AND 121.44518522222) OR (t.lat BETWEEN 31.272909181818 AND 31.309272818182 AND t.lng BETWEEN 121.40584277778 AND 121.45028722222) OR (t.lat BETWEEN 31.261892181818 AND 31.298255818182 AND t.lng BETWEEN 121.40727977778 AND 121.45172422222) OR (t.lat BETWEEN 31.251923181818 AND 31.288286818182 AND t.lng BETWEEN 121.40681277778 AND 121.45125722222) OR (t.lat BETWEEN 31.244268181818 AND 31.280631818182 AND t.lng BETWEEN 121.40616577778 AND 121.45061022222) OR (t.lat BETWEEN 31.234541181818 AND 31.270904818182 AND t.lng BETWEEN 121.41410277778 AND 121.45854722222) OR (t.lat BETWEEN 31.227938181818 AND 31.264301818182 AND t.lng BETWEEN 121.42255077778 AND 121.46699522222) OR (t.lat BETWEEN 31.222164181818 AND 31.258527818182 AND t.lng BETWEEN 121.42657577778 AND 121.47102022222) OR (t.lat BETWEEN 31.210987181818 AND 31.247350818182 AND t.lng BETWEEN 121.43297177778 AND 121.47741622222) OR (t.lat BETWEEN 31.187516181818 AND 31.223879818182 AND t.lng BETWEEN 121.43404977778 AND 121.47849422222) OR (t.lat BETWEEN 31.179300181818 AND 31.215663818182 AND t.lng BETWEEN 121.43872077778 AND 121.48316522222) OR (t.lat BETWEEN 31.173029181818 AND 31.209392818182 AND t.lng BETWEEN 121.44116377778 AND 121.48560822222) OR (t.lat BETWEEN 31.159652181818 AND 31.196015818182 AND t.lng BETWEEN 121.45787277778 AND 121.50231722222) OR (t.lat BETWEEN 31.162062181818 AND 31.198425818182 AND t.lng BETWEEN 121.47034077778 AND 121.51478522222) OR (t.lat BETWEEN 31.165769181818 AND 31.202132818182 AND t.lng BETWEEN 121.47896477778 AND 121.52340922222) OR (t.lat BETWEEN 31.169507181818 AND 31.205870818182 AND t.lng BETWEEN 121.48510877778 AND 121.52955322222) OR (t.lat BETWEEN 31.172936181818 AND 31.209299818182 AND t.lng BETWEEN 121.49423577778 AND 121.53868022222) OR (t.lat BETWEEN 31.175130181818 AND 31.211493818182 AND t.lng BETWEEN 121.50947077778 AND 121.55391522222) OR (t.lat BETWEEN 31.175346181818 AND 31.211709818182 AND t.lng BETWEEN 121.52427477778 AND 121.56871922222) OR (t.lat BETWEEN 31.181153181818 AND 31.217516818182 AND t.lng BETWEEN 121.53430077778 AND 121.57874522222) OR (t.lat BETWEEN 31.191099181818 AND 31.227462818182 AND t.lng BETWEEN 121.54166677778 AND 121.58611122222) OR (t.lat BETWEEN 31.199345181818 AND 31.235708818182 AND t.lng BETWEEN 121.54698477778 AND 121.59142922222))";
        for (int i = 0; i < times; i++) {
            System.out.println("--14--use time：" + this.evaluate(sql, "mysql", 1000l));
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
            // provider.getConfig().setSelectUnionCheck(false);

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
