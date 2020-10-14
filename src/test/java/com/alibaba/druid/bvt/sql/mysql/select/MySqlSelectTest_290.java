/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;


public class MySqlSelectTest_290 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT\n" +
                "        houseInfo.id\n" +
                "        ,contract.code\n" +
                "        ,actualReceiMoney.actual_received_date\n" +
                "        ,org.name AS orgName\n" +
                "        ,org.id AS orgId\n" +
                "        ,IFNULL(projectHead.sale_name,projectHead.name) AS 项目名称\n" +
                "        ,projectHead.id AS projectId\n" +
                "        ,midea_sd_wbs_item.name AS 标段\n" +
                "        ,mdmbidpck.bd_name as bdName\n" +
                "        ,houseInfo.product_type_id\n" +
                "        ,CONCAT(ptt.name,'-',productType.name) AS 业态\n" +
                "        ,GROUP_CONCAT( DISTINCT customerNameItem.customer_name) AS 业主\n" +
                "        ,GROUP_CONCAT( DISTINCT customerNameItem.mobile_phone) AS mobilePhone\n" +
                "        ,GROUP_CONCAT( DISTINCT customerNameItem.certificate_no ) AS certificateNo\n" +
                "        ,GROUP_CONCAT( DISTINCT customerNameItem.address ) AS address\n" +
                "        ,houseInfo.full_name  AS 房号\n" +
                "         ,IFNULL(houseInfo.sales_name,houseInfo.name) AS 房间号\n" +
                "        ,IFNULL(mdmwbsitem.sales_name,mdmwbsitem.name) AS 楼栋名称\n" +
                "\t\t\t\t,ifnull(hitem.mdm_full_name,'') 主数据房间长名称\n" +
                "        ,houseInfo.sales_name AS full_name\n" +
                "        ,houseType.house_type_name  户型名称\n" +
                "        ,houseType.house_type_form  户型结构\n" +
                "        ,pi.name AS  装修标准\n" +
                "        ,( CASE WHEN contract.online_contract_status = '网签' THEN CONCAT( '已网签-', contract.online_contract_no ) ELSE '未网签' END ) AS 合同备案号\n" +
                "        ,contract.code AS 合同编号\n" +
                "        ,orderinfo.sign_date  AS order_date\n" +
                "        ,str_to_date(orderinfo.vesting_date,'%Y-%m-%d')   认购归属时间\n" +
                "        ,str_to_date(contract.sign_date,'%Y-%m-%d')   签约时间\n" +
                "        ,str_to_date(contract.vesting_date,'%Y-%m-%d')  AS 签约归属时间\n" +
                "        ,str_to_date(contract.approve_date,'%Y-%m-%d')  审核时间\n" +
                "        /*/*,deal_usable_floor_area 合同套内面积 */\n" +
                "        /*/*,deal_floor_area  合同建筑面积*/\n" +
                "\t\t\t\t,(case when houseInfo.actual_inner_area is null or houseInfo.actual_inner_area = 0 \n" +
                "\t\t\t\t       then ifnull(houseInfo.forecast_inner_area,0)\n" +
                "\t\t\t\t\t\t\t else houseInfo.actual_inner_area\n" +
                "\t\t\t\t  end) as 合同套内面积\n" +
                "\t\t\t\t,(case when houseInfo.actual_floor_area is null or houseInfo.actual_floor_area = 0\n" +
                "\t\t\t\t       then ifnull(houseInfo.forecast_floor_area,0)\n" +
                "\t\t\t\t\t\t\t else houseInfo.actual_floor_area\n" +
                "\t\t\t\t\tend) as 合同建筑面积\n" +
                "        ,IF(tran.decoration_moneymanage=0,IFNULL( tran.deal_price, 0 ),IFNULL( tran.deal_price, 0 )+IFNULL(tran.decoration_amount,0)) AS 合同金额\n" +
                "        ,actualReceiMoney.totalMoney\n" +
                "        ,actualReceiMoney.totalMoney1 审核回款金额\n" +
                "        ,actualReceiMoney.totalMoney AS 回款金额\n" +
                "        ,ROUND(actualReceiMoney.totalMoney/IF(tran.decoration_moneymanage=0,IFNULL( tran.deal_price, 0 ),IFNULL( tran.deal_price, 0 )+IFNULL(tran.decoration_amount,0))*100,2) AS 回款比例\n" +
                "        ,IFNULL(tran.deal_price,0)AS 毛坯合同金额\n" +
                "       /* /*,ssdj+sssq+sslk+sssy+ssgjj AS 毛坯回款金额 */\n" +
                "\t\t\t\t,ssdj+sssq+sslk+sssy+ssgjj+sslybzj+sszj AS 毛坯回款金额\n" +
                "       /* /*,ROUND((ssdj+sssq+sslk+sssy+ssgjj)/deal_price*100,2) AS 毛坯回款比例 */\n" +
                "\t\t\t\t,ROUND((ssdj+sssq+sslk+sssy+ssgjj++sslybzj+sszj)/deal_price*100,2) AS 毛坯回款比例\n" +
                "        ,IFNULL(tran.decoration_amount,0) AS 装修合同金额\n" +
                "       /* /*,sszx+sszxfy+sszxk AS 装修回款金额 */\n" +
                "\t\t\t\t,sszx+sszxfy+sszxk+ss_sjzxf+ss_sjgzzxf AS 装修回款金额\n" +
                "        /*/*,ROUND((sszx+sszxfy+sszxk)/decoration_amount*100,2) AS 装修回款比例 */\n" +
                "\t\t\t\t,ROUND((sszx+sszxfy+sszxk+ss_sjzxf+ss_sjgzzxf)/decoration_amount*100,2) AS 装修回款比例\n" +
                "       /* /*,str_to_date(actualReceiMoney.actual_received_date,'%Y-%m-%d') AS 最后回款时间 */\n" +
                "\t\t\t\t,str_to_date(act1_date.actual_received_date,'%Y-%m-%d') AS 最后回款时间\n" +
                "        ,ROUND((CASE  \n" +
                "        WHEN tran.decoration_merge_flag =1 and decoration_moneymanage=1 THEN tran.deal_price_with_decoration/(tran.sta_price+IFNULL(tran.decoration_sta_price,0))\n" +
                "\t\t\t\t  WHEN tran.decoration_merge_flag =1 and decoration_moneymanage=0  THEN (tran.deal_price-decoration_amount)/tran.sta_price\n" +
                "        WHEN tran.decoration_merge_flag =0 THEN tran.deal_price/tran.sta_price\n" +
                "        END)*100,2) AS 最终折扣\n" +
                "        ,payment.name AS 付款方式\n" +
                "\t\t\t\t,sdd_item.NAME AS 付款方式类型\n" +
                "        ,depayment.name AS 装修付款方式\n" +
                "        ,paymentplan.ysdj 应收定金\n" +
                "        ,actualReceiMoney.ssdj 实收定金\n" +
                "        ,paymentplan.yssq 应收首期\n" +
                "        ,actualReceiMoney.sssq 实收首期\n" +
                "        ,paymentplan.yslk 应收楼款\n" +
                "        ,actualReceiMoney.sslk 实收楼款\n" +
                "        ,paymentplan.ysaj 应收按揭\n" +
                "        ,actualReceiMoney.ssaj 实收按揭\n" +
                "        ,paymentplan.ysbc  应收面积差款\n" +
                "        ,actualReceiMoney.ssbc 实收面积差款\n" +
                "        ,paymentplan.ysdsfy 应收代收费用\n" +
                "        ,actualReceiMoney.ssdsfy 实收代收费用\n" +
                "        ,salesTeam.salesName AS 置业顾问\n" +
                "        ,customerNameItem2.sourceChannel AS '渠道/推荐'\n" +
                "        /*/*,GROUP_CONCAT( DISTINCT customerNameItem.transaction_source order by customerNameItem.id) AS '渠道/推荐' */\n" +
                "        ,(CASE WHEN tran.employees_buy_flag=1 THEN '是' ELSE '否' END) AS 是否员工购房\n" +
                "        ,IF(houseInfo.talent_house_flag = 1, '是', '否' ) AS 是否人才房\n" +
                "        ,sddictitem.name as paymentTypeCodeName\n" +
                "\t\t\t\t,str_to_date(contract.online_contract_date,'%Y-%m-%d') as 网签日期\n" +
                "\t\t\t\t,str_to_date(contract.sign_date,'%Y-%m-%d') as 草签日期\n" +
                "\t\t\t\t,str_to_date(orderinfo.expect_sign_date,'%Y-%m-%d') as 预计签约日期\n" +
                "\t\t\t\t,(case when orderinfo.expect_sign_date is not null and orderinfo.expect_sign_date > contract.vesting_date\n" +
                "\t\t\t\t  then '不逾期'\n" +
                "\t\t\t\t\t else '逾期'\n" +
                "\t\t\t\t  end) as 是否逾期\n" +
                "\t\t\t\t,contract.appointed_deliver_date '交付日期' \n" +
                "\t\t\t\t,paymentplan.whkAmount '未回款金额' \n" +
                "\t\t\t\t,IFNULL(ui.sale_unit_name,ui.unit_no) 单元名称 /*/*add by zhangxiaojin 2019-05-21*/\n" +
                "        ,IFNULL(fi.sales_floor_name,fi.floor_no) 楼层 /* /*add by zhangxiaojin 2019-05-21*/\n" +
                "\t\t\t\t,sitem.`name` '客户属性'  /*/*add by zhangyong 2019-06-03*/\n" +
                "\t\t\t\t,tran.expire_date '税单到期日'  /*/*add by zhangyong 2019-06-03*/\n" +
                "\t\t\n" +
                "        FROM \n" +
                "        midea_sd_contract_info contract\n" +
                "        LEFT JOIN midea_sd_order_contract_transaction tran ON contract.transaction_id = tran.id\n" +
                "\t\t\t\tleft join midea_sd_sddict_item sitem on tran.purchase_nature=sitem.code\n" +
                "        LEFT JOIN midea_sd_transaction_customer customerNameItem ON tran.id = customerNameItem.transaction_id\n" +
                "        LEFT JOIN (\n" +
                "     \n" +
                "/*/*最开始交易的那匹客户,会出现同时插入的多个联名客户的情况,要进行选择 */\n" +
                " SELECT transaction_source.* from\n" +
                " (select tin.transaction_id,min(tin.transaction_source_px) transaction_source_px_min\n" +
                " FROM (\n" +
                "   select \n" +
                "   tc.transaction_id,/*/*交易id */\n" +
                "   tc.sort, \n" +
                "   CASE \n" +
                "   WHEN tc.transaction_source LIKE '%智美分销%' THEN  1\n" +
                "   WHEN tc.transaction_source LIKE '%智美置家%' THEN 2\n" +
                "   WHEN (IFNULL(tc.transaction_source ,'') NOT LIKE '%智美分销%' AND IFNULL(tc.transaction_source,'')  NOT LIKE '%智美置家%') AND tc.sort='1' THEN 3\n" +
                "   END transaction_source_px,\n" +
                "   DATE_FORMAT(tc.create_date,'%Y-%m-%d %H:%i') create_date/*/*创建日期 */\n" +
                "   FROM midea_sd_transaction_customer tc\n" +
                "   LEFT JOIN( \n" +
                "  /* /*粒度区分到分钟(只可能有一条,也肯定有一条) */\n" +
                "   select tc1.id,tc1.transaction_id,DATE_FORMAT(MIN(tc1.create_date),'%Y-%m-%d %H:%i') create_date FROM midea_sd_transaction_customer tc1\n" +
                "   GROUP BY tc1.transaction_id\n" +
                "   ) bestCreatDate ON bestCreatDate.transaction_id=tc.transaction_id AND bestCreatDate.create_date=DATE_FORMAT(tc.create_date,'%Y-%m-%d %H:%i')\n" +
                "   WHERE bestCreatDate.id is not null \n" +
                "   ) tin\n" +
                "   WHERE transaction_source_px is not null\n" +
                "   GROUP BY transaction_id \n" +
                "  ) transaction_source_min\n" +
                "  LEFT JOIN \n" +
                "  (\n" +
                "  /* /*最开始交易的那匹客户,会出现同时插入的多个联名客户的情况,要进行选择 */\n" +
                "select DISTINCT tin.transaction_id,tin.transaction_source AS sourceChannel,tin.transaction_source_px\n" +
                "   FROM (\n" +
                "   select \n" +
                "   tc.id,/*/*交易客户表id */\n" +
                "   tc.transaction_id,/*/*交易id*/\n" +
                "   tc.customer_name, /*/*客户名字 */\n" +
                "   tc.sort, /*/*排序 */\n" +
                "   tc.transaction_source, /*/*来访渠道 */\n" +
                "   tc.sales_org_id,\n" +
                "   tc.potential_customer_id,\n" +
                "   tc.customer_source_id,\n" +
                "   tc.mobile_phone,\n" +
                "   tc.certificate_type,\n" +
                "   tc.certificate_no,\n" +
                "   tc.address,\n" +
                "   tc.postal_code,\n" +
                "   CASE \n" +
                "   WHEN tc.transaction_source LIKE '%智美分销%' THEN  1\n" +
                "   WHEN tc.transaction_source LIKE '%智美置家%' THEN 2\n" +
                "   WHEN (IFNULL(tc.transaction_source ,'') NOT LIKE '%智美分销%' AND IFNULL(tc.transaction_source,'')  NOT LIKE '%智美置家%') AND tc.sort='1' THEN 3\n" +
                "   END transaction_source_px,\n" +
                "   DATE_FORMAT(tc.create_date,'%Y-%m-%d %H:%i') create_date /*/*创建日期 */\n" +
                "   FROM midea_sd_transaction_customer tc \n" +
                "   LEFT JOIN( \n" +
                "    /*/*粒度区分到分钟(只可能有一条,也肯定有一条)*/ \n" +
                "   select tc1.id,tc1.transaction_id,DATE_FORMAT(MIN(tc1.create_date),'%Y-%m-%d %H:%i') create_date FROM midea_sd_transaction_customer tc1\n" +
                "   GROUP BY tc1.transaction_id\n" +
                "   ) bestCreatDate ON bestCreatDate.transaction_id=tc.transaction_id AND bestCreatDate.create_date=DATE_FORMAT(tc.create_date,'%Y-%m-%d %H:%i')\n" +
                "   WHERE bestCreatDate.id is not null \n" +
                "   ) tin\n" +
                "   WHERE transaction_source_px is not null\n" +
                "   GROUP BY transaction_id,transaction_source_px\n" +
                ") transaction_source on transaction_source.transaction_id=transaction_source_min.transaction_id \n" +
                "and transaction_source_min.transaction_source_px_min=transaction_source.transaction_source_px\n" +
                "        \n" +
                "       ) customerNameItem2 ON tran.id = customerNameItem2.transaction_id\n" +
                "        LEFT JOIN midea_sd_project_head AS projectHead ON tran.project_id = projectHead.id\n" +
                "        LEFT JOIN midea_sd_orgnazation AS org ON org.id = projectHead.orgnazation_id\n" +
                "        LEFT JOIN midea_sd_house_info AS houseInfo ON tran.house_id = houseInfo.id\n" +
                "\t\t\t\tLEFT JOIN midea_sd_unit_info ui ON ui.id=houseInfo.unit_id   /*/*add by zhangxiaojin 2019-05-21*/\n" +
                "        LEFT JOIN midea_sd_floor_info fi ON fi.id=houseInfo.floor_id /*/*add by zhangxiaojin 2019-05-21 */\n" +
                "\t\t\t\tLEFT JOIN midea_sd_house_item hitem on hitem.id = houseInfo.id\n" +
                "        LEFT JOIN midea_sd_wbs_item build ON build.id = houseInfo.building_id\n" +
                "        left join midea_sd_mdm_wbs_item mdmwbsitem on mdmwbsitem.wbs_head_id = build.wbs_head_id\n" +
                "        left join midea_sd_mdm_bid_package mdmbidpck on mdmbidpck.id = mdmwbsitem.bd_id\n" +
                "        /* LEFT JOIN midea_sd_product_type AS productType ON houseInfo.product_type_id = productType.id */\n" +
                "        LEFT JOIN midea_sd_mdm_product mp ON mp.id=hitem.product_id \n" +
                "        LEFT JOIN midea_sd_wbs_attribute_parameter_item pi on pi.code =mp.decoration_type_code \n" +
                "        LEFT JOIN midea_sd_product_type productType ON mp.product_type_id=productType.id\n" +
                "        LEFT JOIN midea_sd_product_type ptt on ptt.id=productType.parent_id\n" +
                "        LEFT JOIN midea_sd_wbs_item ON tran.wbs_id = midea_sd_wbs_item.wbs_head_id\n" +
                "        LEFT JOIN midea_sd_house_type AS houseType ON houseType.id = houseInfo.house_type_id\n" +
                "        /*LEFT JOIN midea_sd_decoration_standard_config decoration ON tran.decoration_standard_config_id = decoration.id */\n" +
                "        LEFT JOIN midea_sd_after_sales aftersales ON contract.transaction_id = aftersales.transaction_id\n" +
                "        LEFT JOIN midea_sd_order_info orderinfo ON tran.id = orderinfo.transaction_id\n" +
                "        LEFT JOIN\n" +
                "        (SELECT\n" +
                "        octt.id,\n" +
                "        max(rh.actual_received_date) actual_received_date,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01001'  THEN actual_amount_total ELSE 0 END) AS ssdj,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01002' THEN actual_amount_total ELSE 0 END) AS sssq,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01003' THEN actual_amount_total ELSE 0 END) AS sslk,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01004' THEN actual_amount_total ELSE 0 END) AS ssbc,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01007' THEN actual_amount_total ELSE 0 END) AS sszx,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01005' THEN actual_amount_total ELSE 0 END) AS sszxfy,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01008' THEN actual_amount_total ELSE 0 END) AS sslybzj,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01009' THEN actual_amount_total ELSE 0 END) AS sszj,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT02001' THEN actual_amount_total ELSE 0 END) AS sssy,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT02002' THEN actual_amount_total ELSE 0 END) AS ssgjj,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT02003' THEN actual_amount_total ELSE 0 END) AS sszxk,\n" +
                "\t\t\t\tSUM(CASE WHEN fund_name_code = 'FIFT01010'  THEN actual_amount_total ELSE 0 END) AS ss_sjzxf,\n" +
                "\t\t\t\tSUM(CASE WHEN fund_name_code = 'FIFT01012'  THEN actual_amount_total ELSE 0 END) AS ss_sjgzzxf,\n" +
                "        SUM(CASE WHEN fund_type_code = 'FIFT02' THEN actual_amount_total ELSE 0 END) AS ssaj,\n" +
                "        SUM(CASE WHEN fund_type_code = 'FIFT03' THEN actual_amount_total ELSE 0 END) AS ssdsfy\n" +
                "        ,SUM(IF(ar.fund_type_code IN ('FIFT01','FIFT02') AND ar.fund_name_code!='FIFT01004',ar.actual_amount_total,0)) AS totalMoney\n" +
                "        ,SUM(IF(rh.review_date>'1990-01-01' AND ar.fund_type_code IN ('FIFT01','FIFT02') AND ar.fund_name_code!='FIFT01004',ar.actual_amount_total,0)) totalMoney1\n" +
                "        FROM\n" +
                "        midea_sd_actual_received_item ar\n" +
                "        LEFT JOIN midea_sd_order_contract_transaction octt ON octt.id = ar.transaction_id\n" +
                "        LEFT JOIN midea_sd_actual_received_head rh ON ar.actual_received_head_id = rh.id\n" +
                "        WHERE\n" +
                "        document_status_code != 'FI0007004'\n" +
                "        AND ( actual_amount_total > 0 OR ( actual_amount_total < 0 AND deductible = 1 ) )\n" +
                "--         <projectId> AND octt.project_id in ( ?{projectId} )</projectId>\n" +
                "--         <endDate1> AND rh.actual_received_date<= ?{endDate1}</endDate1>\n" +
                "        GROUP BY\n" +
                "        octt.id\n" +
                "        ) AS actualReceiMoney ON tran.id = actualReceiMoney.id\n" +
                "\t\t\t\tleft join (SELECT octt.id,\n" +
                "                          max(rh.actual_received_date) actual_received_date\n" +
                "                     FROM midea_sd_actual_received_item ar\n" +
                "                          LEFT JOIN midea_sd_order_contract_transaction octt ON octt.id = ar.transaction_id\n" +
                "                          LEFT JOIN midea_sd_actual_received_head rh ON ar.actual_received_head_id = rh.id\n" +
                "                    WHERE rh.document_status_code != 'FI0007004'\n" +
                "\t\t\t\t              and ar.fund_type_code IN ('FIFT01','FIFT02')\n" +
                "                      AND ar.actual_amount_total > 0\n" +
                "--                       <projectId> AND octt.project_id in ( ?{projectId} )</projectId>\n" +
                "--                       <endDate1> AND rh.actual_received_date<= ?{endDate1}</endDate1>\n" +
                "                    GROUP BY octt.id\n" +
                "                  ) AS act1_date ON tran.id = act1_date.id\n" +
                "        LEFT JOIN  \n" +
                "        (SELECT\n" +
                "        ort.id,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01001' THEN plan_amount_total ELSE 0 END) AS ysdj,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01002' THEN plan_amount_total ELSE 0 END) AS yssq,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01003' THEN plan_amount_total ELSE 0 END) AS yslk,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01004' THEN plan_amount_total ELSE 0 END) AS ysbc,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01005' THEN plan_amount_total ELSE 0 END) AS yszx,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01006' THEN plan_amount_total ELSE 0 END) AS yszxfy,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01007' THEN plan_amount_total ELSE 0 END) AS yslybzj,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT01008' THEN plan_amount_total ELSE 0 END) AS yszj,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT02001' THEN plan_amount_total ELSE 0 END) AS yssy,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT02002' THEN plan_amount_total ELSE 0 END) AS ysgjj,\n" +
                "        SUM(CASE WHEN fund_name_code = 'FIFT02003' THEN plan_amount_total ELSE 0 END) AS yszxk,\n" +
                "        SUM(CASE WHEN fund_type_code = 'FIFT02' THEN plan_amount_total ELSE 0 END) AS ysaj,\n" +
                "        SUM(CASE WHEN fund_type_code = 'FIFT03' THEN plan_amount_total ELSE 0 END) AS ysdsfy,\n" +
                "        SUM( plan_amount_total ) AS ystotalMoney,\n" +
                "\t\t\t\tSUM(CASE WHEN fund_type_code in ('FIFT02','FIFT01') THEN (plan_amount_total - received_amount_total) ELSE 0 END ) as whkAmount\n" +
                "        FROM\n" +
                "        midea_sd_payment_plan  pp\n" +
                "        JOIN midea_sd_order_contract_transaction ort ON ort.id = pp.transaction_id\n" +
                "        WHERE\n" +
                "        1=1\n" +
                "--         <projectId> AND ort.project_id in ( ?{projectId} )</projectId>\n" +
                "      GROUP BY\n" +
                "        ort.id\n" +
                "        ) AS paymentplan ON tran.id = paymentplan.id\n" +
                "        LEFT JOIN\n" +
                "        (SELECT\n" +
                "        ra2.transaction_id\n" +
                "        ,ra2.actual_reserve_amount\n" +
                "        FROM\n" +
                "        midea_sd_reserve_area ra1\n" +
                "        LEFT JOIN midea_sd_reserve_area_detail ra2 ON ra2.reserve_area_id = ra1.id\n" +
                "        INNER JOIN\n" +
                "        (SELECT\n" +
                "        r2.transaction_id\n" +
                "        ,MAX(r1.exec_date) exec_date\n" +
                "        FROM\n" +
                "        midea_sd_reserve_area r1\n" +
                "        LEFT JOIN midea_sd_reserve_area_detail r2 ON r2.reserve_area_id = r1.id\n" +
                "        WHERE\n" +
                "        r1.status_code = 'SD050104'\n" +
                "        -- <projectId> AND r1.project_id in ( ?{projectId} )</projectId>\n" +
                "        GROUP BY\n" +
                "        r2.contract_id\n" +
                "        ) ra3 ON ra3.transaction_id=ra2.transaction_id AND ra3.exec_date=ra1.exec_date\n" +
                "        WHERE\n" +
                "        ra1.status_code = 'SD050104'\n" +
                "        -- <projectId> AND ra1.project_id in ( ?{projectId} )</projectId> \n" +
                "        ) areaDetail ON areaDetail.transaction_id = tran.id\n" +
                "        LEFT JOIN midea_sd_payment_method payment ON tran.payment_method_id = payment.id\n" +
                "\t\t\t\tLEFT JOIN midea_sd_sddict_item sdd_item on payment.payment_type_code = sdd_item.CODE\n" +
                "        left join midea_sd_sddict_item sddictitem on sddictitem.code = payment.payment_type_code\n" +
                "        LEFT JOIN midea_sd_payment_method depayment ON tran.depayment_method_id = depayment.id\n" +
                "        LEFT JOIN\n" +
                "        (SELECT tran.id id,GROUP_CONCAT( DISTINCT midea_sd_user_account.name ) AS salesName\n" +
                "        FROM midea_sd_order_contract_transaction tran\n" +
                "        LEFT JOIN\tmidea_sd_transaction_sale_member salesMemberItem ON tran.id = salesMemberItem.transaction_id and salesMemberItem.modify_type != 1\n" +
                "        LEFT JOIN midea_sd_user_account ON salesMemberItem.sale_member_id = midea_sd_user_account.id\n" +
                "        WHERE\n" +
                "        1=1\n" +
                "        -- <projectId> AND tran.project_id in ( ?{projectId} )</projectId>\n" +
                "        GROUP BY tran.id\n" +
                "        ) salesTeam ON salesTeam.id = tran.id\n" +
                "        WHERE\n" +
                "        contract.status_code in ('SD040501','SD040502')\n" +
                "       -- <orgId> AND org.id in (?{orgId}) </orgId>\n" +
                "       -- <projectId> AND tran.project_id in ( ?{projectId} )</projectId>\n" +
                "       -- <startDate2>AND contract.vesting_date >= ?{startDate2} </startDate2>\n" +
                "       -- <endDate2> AND  contract.vesting_date <= ?{endDate2} </endDate2>\n" +
                "        GROUP BY contract.id\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT houseInfo.id, contract.code, actualReceiMoney.actual_received_date, org.name AS orgName, org.id AS orgId\n" +
                "\t, IFNULL(projectHead.sale_name, projectHead.name) AS 项目名称, projectHead.id AS projectId\n" +
                "\t, midea_sd_wbs_item.name AS 标段, mdmbidpck.bd_name AS bdName, houseInfo.product_type_id\n" +
                "\t, CONCAT(ptt.name, '-', productType.name) AS 业态\n" +
                "\t, GROUP_CONCAT(DISTINCT customerNameItem.customer_name) AS 业主, GROUP_CONCAT(DISTINCT customerNameItem.mobile_phone) AS mobilePhone\n" +
                "\t, GROUP_CONCAT(DISTINCT customerNameItem.certificate_no) AS certificateNo, GROUP_CONCAT(DISTINCT customerNameItem.address) AS address\n" +
                "\t, houseInfo.full_name AS 房号, IFNULL(houseInfo.sales_name, houseInfo.name) AS 房间号\n" +
                "\t, IFNULL(mdmwbsitem.sales_name, mdmwbsitem.name) AS 楼栋名称\n" +
                "\t, ifnull(hitem.mdm_full_name, '') AS 主数据房间长名称, houseInfo.sales_name AS full_name\n" +
                "\t, houseType.house_type_name AS 户型名称, houseType.house_type_form AS 户型结构, pi.name AS 装修标准\n" +
                "\t, CASE \n" +
                "\t\tWHEN contract.online_contract_status = '网签' THEN CONCAT('已网签-', contract.online_contract_no)\n" +
                "\t\tELSE '未网签'\n" +
                "\tEND AS 合同备案号, contract.code AS 合同编号, orderinfo.sign_date AS order_date\n" +
                "\t, str_to_date(orderinfo.vesting_date, '%Y-%m-%d') AS 认购归属时间\n" +
                "\t, str_to_date(contract.sign_date, '%Y-%m-%d') AS 签约时间\n" +
                "\t, str_to_date(contract.vesting_date, '%Y-%m-%d') AS 签约归属时间\n" +
                "\t, str_to_date(contract.approve_date, '%Y-%m-%d') AS 审核时间\n" +
                "\t, CASE \n" +
                "\t\tWHEN houseInfo.actual_inner_area IS NULL\n" +
                "\t\t\tOR houseInfo.actual_inner_area = 0\n" +
                "\t\tTHEN ifnull(houseInfo.forecast_inner_area, 0)\n" +
                "\t\tELSE houseInfo.actual_inner_area\n" +
                "\tEND AS 合同套内面积\n" +
                "\t, CASE \n" +
                "\t\tWHEN houseInfo.actual_floor_area IS NULL\n" +
                "\t\t\tOR houseInfo.actual_floor_area = 0\n" +
                "\t\tTHEN ifnull(houseInfo.forecast_floor_area, 0)\n" +
                "\t\tELSE houseInfo.actual_floor_area\n" +
                "\tEND AS 合同建筑面积\n" +
                "\t, IF(tran.decoration_moneymanage = 0, IFNULL(tran.deal_price, 0), IFNULL(tran.deal_price, 0) + IFNULL(tran.decoration_amount, 0)) AS 合同金额\n" +
                "\t, actualReceiMoney.totalMoney, actualReceiMoney.totalMoney1 AS 审核回款金额, actualReceiMoney.totalMoney AS 回款金额\n" +
                "\t, ROUND(actualReceiMoney.totalMoney / IF(tran.decoration_moneymanage = 0, IFNULL(tran.deal_price, 0), IFNULL(tran.deal_price, 0) + IFNULL(tran.decoration_amount, 0)) * 100, 2) AS 回款比例\n" +
                "\t, IFNULL(tran.deal_price, 0) AS 毛坯合同金额\n" +
                "\t, ssdj + sssq + sslk + sssy + ssgjj + sslybzj + sszj AS 毛坯回款金额\n" +
                "\t, ROUND((ssdj + sssq + sslk + sssy + ssgjj + +sslybzj + sszj) / deal_price * 100, 2) AS 毛坯回款比例\n" +
                "\t, IFNULL(tran.decoration_amount, 0) AS 装修合同金额\n" +
                "\t, sszx + sszxfy + sszxk + ss_sjzxf + ss_sjgzzxf AS 装修回款金额\n" +
                "\t, ROUND((sszx + sszxfy + sszxk + ss_sjzxf + ss_sjgzzxf) / decoration_amount * 100, 2) AS 装修回款比例\n" +
                "\t, str_to_date(act1_date.actual_received_date, '%Y-%m-%d') AS 最后回款时间\n" +
                "\t, ROUND(CASE \n" +
                "\t\tWHEN tran.decoration_merge_flag = 1\n" +
                "\t\t\tAND decoration_moneymanage = 1\n" +
                "\t\tTHEN tran.deal_price_with_decoration / (tran.sta_price + IFNULL(tran.decoration_sta_price, 0))\n" +
                "\t\tWHEN tran.decoration_merge_flag = 1\n" +
                "\t\t\tAND decoration_moneymanage = 0\n" +
                "\t\tTHEN (tran.deal_price - decoration_amount) / tran.sta_price\n" +
                "\t\tWHEN tran.decoration_merge_flag = 0 THEN tran.deal_price / tran.sta_price\n" +
                "\tEND * 100, 2) AS 最终折扣\n" +
                "\t, payment.name AS 付款方式, sdd_item.NAME AS 付款方式类型, depayment.name AS 装修付款方式, paymentplan.ysdj AS 应收定金, actualReceiMoney.ssdj AS 实收定金\n" +
                "\t, paymentplan.yssq AS 应收首期, actualReceiMoney.sssq AS 实收首期, paymentplan.yslk AS 应收楼款, actualReceiMoney.sslk AS 实收楼款, paymentplan.ysaj AS 应收按揭\n" +
                "\t, actualReceiMoney.ssaj AS 实收按揭, paymentplan.ysbc AS 应收面积差款, actualReceiMoney.ssbc AS 实收面积差款, paymentplan.ysdsfy AS 应收代收费用, actualReceiMoney.ssdsfy AS 实收代收费用\n" +
                "\t, salesTeam.salesName AS 置业顾问, customerNameItem2.sourceChannel AS \"渠道/推荐\"\n" +
                "\t, CASE \n" +
                "\t\tWHEN tran.employees_buy_flag = 1 THEN '是'\n" +
                "\t\tELSE '否'\n" +
                "\tEND AS 是否员工购房\n" +
                "\t, IF(houseInfo.talent_house_flag = 1, '是', '否') AS 是否人才房\n" +
                "\t, sddictitem.name AS paymentTypeCodeName, str_to_date(contract.online_contract_date, '%Y-%m-%d') AS 网签日期\n" +
                "\t, str_to_date(contract.sign_date, '%Y-%m-%d') AS 草签日期\n" +
                "\t, str_to_date(orderinfo.expect_sign_date, '%Y-%m-%d') AS 预计签约日期\n" +
                "\t, CASE \n" +
                "\t\tWHEN orderinfo.expect_sign_date IS NOT NULL\n" +
                "\t\t\tAND orderinfo.expect_sign_date > contract.vesting_date\n" +
                "\t\tTHEN '不逾期'\n" +
                "\t\tELSE '逾期'\n" +
                "\tEND AS 是否逾期, contract.appointed_deliver_date AS '交付日期', paymentplan.whkAmount AS '未回款金额'\n" +
                "\t, IFNULL(ui.sale_unit_name, ui.unit_no) AS 单元名称\n" +
                "\t, IFNULL(fi.sales_floor_name, fi.floor_no) AS 楼层, sitem.`name` AS '客户属性'\n" +
                "\t, tran.expire_date AS '税单到期日'\n" +
                "FROM midea_sd_contract_info contract\n" +
                "\tLEFT JOIN midea_sd_order_contract_transaction tran ON contract.transaction_id = tran.id\n" +
                "\tLEFT JOIN midea_sd_sddict_item sitem ON tran.purchase_nature = sitem.code\n" +
                "\tLEFT JOIN midea_sd_transaction_customer customerNameItem ON tran.id = customerNameItem.transaction_id\n" +
                "\tLEFT JOIN (\n" +
                "\t\t/*/*最开始交易的那匹客户,会出现同时插入的多个联名客户的情况,要进行选择 */\n" +
                "\t\tSELECT transaction_source.*\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT tin.transaction_id, min(tin.transaction_source_px) AS transaction_source_px_min\n" +
                "\t\t\tFROM (\n" +
                "\t\t\t\tSELECT tc.transaction_id, tc.sort\n" +
                "\t\t\t\t\t, CASE \n" +
                "\t\t\t\t\t\tWHEN tc.transaction_source LIKE '%智美分销%' THEN 1\n" +
                "\t\t\t\t\t\tWHEN tc.transaction_source LIKE '%智美置家%' THEN 2\n" +
                "\t\t\t\t\t\tWHEN IFNULL(tc.transaction_source, '') NOT LIKE '%智美分销%'\n" +
                "\t\t\t\t\t\t\tAND IFNULL(tc.transaction_source, '') NOT LIKE '%智美置家%'\n" +
                "\t\t\t\t\t\t\tAND tc.sort = '1'\n" +
                "\t\t\t\t\t\tTHEN 3\n" +
                "\t\t\t\t\tEND AS transaction_source_px, DATE_FORMAT(tc.create_date, '%Y-%m-%d %H:%i') AS create_date\n" +
                "\t\t\t\tFROM midea_sd_transaction_customer tc\n" +
                "\t\t\t\t\tLEFT JOIN (\n" +
                "\t\t\t\t\t\t/* /*粒度区分到分钟(只可能有一条,也肯定有一条) */\n" +
                "\t\t\t\t\t\tSELECT tc1.id, tc1.transaction_id\n" +
                "\t\t\t\t\t\t\t, DATE_FORMAT(MIN(tc1.create_date), '%Y-%m-%d %H:%i') AS create_date\n" +
                "\t\t\t\t\t\tFROM midea_sd_transaction_customer tc1\n" +
                "\t\t\t\t\t\tGROUP BY tc1.transaction_id\n" +
                "\t\t\t\t\t) bestCreatDate\n" +
                "\t\t\t\t\tON bestCreatDate.transaction_id = tc.transaction_id\n" +
                "\t\t\t\t\t\tAND bestCreatDate.create_date = DATE_FORMAT(tc.create_date, '%Y-%m-%d %H:%i')\n" +
                "\t\t\t\tWHERE bestCreatDate.id IS NOT NULL\n" +
                "\t\t\t) tin\n" +
                "\t\t\tWHERE transaction_source_px IS NOT NULL\n" +
                "\t\t\tGROUP BY transaction_id\n" +
                "\t\t) transaction_source_min\n" +
                "\t\t\tLEFT JOIN (\n" +
                "\t\t\t\t/* /*最开始交易的那匹客户,会出现同时插入的多个联名客户的情况,要进行选择 */\n" +
                "\t\t\t\tSELECT DISTINCT tin.transaction_id, tin.transaction_source AS sourceChannel, tin.transaction_source_px\n" +
                "\t\t\t\tFROM (\n" +
                "\t\t\t\t\tSELECT tc.id, tc.transaction_id, tc.customer_name, tc.sort, tc.transaction_source\n" +
                "\t\t\t\t\t\t, tc.sales_org_id, tc.potential_customer_id, tc.customer_source_id, tc.mobile_phone, tc.certificate_type\n" +
                "\t\t\t\t\t\t, tc.certificate_no, tc.address, tc.postal_code\n" +
                "\t\t\t\t\t\t, CASE \n" +
                "\t\t\t\t\t\t\tWHEN tc.transaction_source LIKE '%智美分销%' THEN 1\n" +
                "\t\t\t\t\t\t\tWHEN tc.transaction_source LIKE '%智美置家%' THEN 2\n" +
                "\t\t\t\t\t\t\tWHEN IFNULL(tc.transaction_source, '') NOT LIKE '%智美分销%'\n" +
                "\t\t\t\t\t\t\t\tAND IFNULL(tc.transaction_source, '') NOT LIKE '%智美置家%'\n" +
                "\t\t\t\t\t\t\t\tAND tc.sort = '1'\n" +
                "\t\t\t\t\t\t\tTHEN 3\n" +
                "\t\t\t\t\t\tEND AS transaction_source_px, DATE_FORMAT(tc.create_date, '%Y-%m-%d %H:%i') AS create_date\n" +
                "\t\t\t\t\tFROM midea_sd_transaction_customer tc\n" +
                "\t\t\t\t\t\tLEFT JOIN (\n" +
                "\t\t\t\t\t\t\t/*/*粒度区分到分钟(只可能有一条,也肯定有一条)*/\n" +
                "\t\t\t\t\t\t\tSELECT tc1.id, tc1.transaction_id\n" +
                "\t\t\t\t\t\t\t\t, DATE_FORMAT(MIN(tc1.create_date), '%Y-%m-%d %H:%i') AS create_date\n" +
                "\t\t\t\t\t\t\tFROM midea_sd_transaction_customer tc1\n" +
                "\t\t\t\t\t\t\tGROUP BY tc1.transaction_id\n" +
                "\t\t\t\t\t\t) bestCreatDate\n" +
                "\t\t\t\t\t\tON bestCreatDate.transaction_id = tc.transaction_id\n" +
                "\t\t\t\t\t\t\tAND bestCreatDate.create_date = DATE_FORMAT(tc.create_date, '%Y-%m-%d %H:%i')\n" +
                "\t\t\t\t\tWHERE bestCreatDate.id IS NOT NULL\n" +
                "\t\t\t\t) tin\n" +
                "\t\t\t\tWHERE transaction_source_px IS NOT NULL\n" +
                "\t\t\t\tGROUP BY transaction_id, transaction_source_px\n" +
                "\t\t\t) transaction_source\n" +
                "\t\t\tON transaction_source.transaction_id = transaction_source_min.transaction_id\n" +
                "\t\t\t\tAND transaction_source_min.transaction_source_px_min = transaction_source.transaction_source_px\n" +
                "\t) customerNameItem2\n" +
                "\tON tran.id = customerNameItem2.transaction_id\n" +
                "\tLEFT JOIN midea_sd_project_head projectHead ON tran.project_id = projectHead.id\n" +
                "\tLEFT JOIN midea_sd_orgnazation org ON org.id = projectHead.orgnazation_id\n" +
                "\tLEFT JOIN midea_sd_house_info houseInfo ON tran.house_id = houseInfo.id\n" +
                "\tLEFT JOIN midea_sd_unit_info ui ON ui.id = houseInfo.unit_id\n" +
                "\tLEFT JOIN midea_sd_floor_info fi ON fi.id = houseInfo.floor_id\n" +
                "\tLEFT JOIN midea_sd_house_item hitem ON hitem.id = houseInfo.id\n" +
                "\tLEFT JOIN midea_sd_wbs_item build ON build.id = houseInfo.building_id\n" +
                "\tLEFT JOIN midea_sd_mdm_wbs_item mdmwbsitem ON mdmwbsitem.wbs_head_id = build.wbs_head_id\n" +
                "\tLEFT JOIN midea_sd_mdm_bid_package mdmbidpck ON mdmbidpck.id = mdmwbsitem.bd_id\n" +
                "\tLEFT JOIN midea_sd_mdm_product mp ON mp.id = hitem.product_id\n" +
                "\tLEFT JOIN midea_sd_wbs_attribute_parameter_item pi ON pi.code = mp.decoration_type_code\n" +
                "\tLEFT JOIN midea_sd_product_type productType ON mp.product_type_id = productType.id\n" +
                "\tLEFT JOIN midea_sd_product_type ptt ON ptt.id = productType.parent_id\n" +
                "\tLEFT JOIN midea_sd_wbs_item ON tran.wbs_id = midea_sd_wbs_item.wbs_head_id\n" +
                "\tLEFT JOIN midea_sd_house_type houseType ON houseType.id = houseInfo.house_type_id\n" +
                "\tLEFT JOIN midea_sd_after_sales aftersales ON contract.transaction_id = aftersales.transaction_id\n" +
                "\tLEFT JOIN midea_sd_order_info orderinfo ON tran.id = orderinfo.transaction_id\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT octt.id, max(rh.actual_received_date) AS actual_received_date\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01001' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS ssdj\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01002' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS sssq\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01003' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS sslk\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01004' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS ssbc\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01007' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS sszx\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01005' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS sszxfy\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01008' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS sslybzj\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01009' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS sszj\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT02001' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS sssy\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT02002' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS ssgjj\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT02003' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS sszxk\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01010' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS ss_sjzxf\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01012' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS ss_sjgzzxf\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_type_code = 'FIFT02' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS ssaj\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_type_code = 'FIFT03' THEN actual_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS ssdsfy\n" +
                "\t\t\t, SUM(IF(ar.fund_type_code IN ('FIFT01', 'FIFT02')\n" +
                "\t\t\t\tAND ar.fund_name_code != 'FIFT01004', ar.actual_amount_total, 0)) AS totalMoney\n" +
                "\t\t\t, SUM(IF(rh.review_date > '1990-01-01'\n" +
                "\t\t\t\tAND ar.fund_type_code IN ('FIFT01', 'FIFT02')\n" +
                "\t\t\t\tAND ar.fund_name_code != 'FIFT01004', ar.actual_amount_total, 0)) AS totalMoney1\n" +
                "\t\tFROM midea_sd_actual_received_item ar\n" +
                "\t\t\tLEFT JOIN midea_sd_order_contract_transaction octt ON octt.id = ar.transaction_id\n" +
                "\t\t\tLEFT JOIN midea_sd_actual_received_head rh ON ar.actual_received_head_id = rh.id\n" +
                "\t\tWHERE document_status_code != 'FI0007004'\n" +
                "\t\t\tAND (actual_amount_total > 0\n" +
                "\t\t\t\tOR (actual_amount_total < 0\n" +
                "\t\t\t\t\tAND deductible = 1))\n" +
                "\t\tGROUP BY octt.id\n" +
                "\t) actualReceiMoney\n" +
                "\tON tran.id = actualReceiMoney.id\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT octt.id, max(rh.actual_received_date) AS actual_received_date\n" +
                "\t\tFROM midea_sd_actual_received_item ar\n" +
                "\t\t\tLEFT JOIN midea_sd_order_contract_transaction octt ON octt.id = ar.transaction_id\n" +
                "\t\t\tLEFT JOIN midea_sd_actual_received_head rh ON ar.actual_received_head_id = rh.id\n" +
                "\t\tWHERE rh.document_status_code != 'FI0007004'\n" +
                "\t\t\tAND ar.fund_type_code IN ('FIFT01', 'FIFT02')\n" +
                "\t\t\tAND ar.actual_amount_total > 0\n" +
                "\t\tGROUP BY octt.id\n" +
                "\t) act1_date\n" +
                "\tON tran.id = act1_date.id\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT ort.id\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01001' THEN plan_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS ysdj\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01002' THEN plan_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS yssq\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01003' THEN plan_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS yslk\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01004' THEN plan_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS ysbc\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01005' THEN plan_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS yszx\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01006' THEN plan_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS yszxfy\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01007' THEN plan_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS yslybzj\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT01008' THEN plan_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS yszj\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT02001' THEN plan_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS yssy\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT02002' THEN plan_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS ysgjj\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_name_code = 'FIFT02003' THEN plan_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS yszxk\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_type_code = 'FIFT02' THEN plan_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS ysaj\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_type_code = 'FIFT03' THEN plan_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS ysdsfy\n" +
                "\t\t\t, SUM(plan_amount_total) AS ystotalMoney\n" +
                "\t\t\t, SUM(CASE \n" +
                "\t\t\t\tWHEN fund_type_code IN ('FIFT02', 'FIFT01') THEN plan_amount_total - received_amount_total\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS whkAmount\n" +
                "\t\tFROM midea_sd_payment_plan pp\n" +
                "\t\t\tJOIN midea_sd_order_contract_transaction ort ON ort.id = pp.transaction_id\n" +
                "\t\tWHERE 1 = 1\n" +
                "\t\tGROUP BY ort.id\n" +
                "\t) paymentplan\n" +
                "\tON tran.id = paymentplan.id\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT ra2.transaction_id, ra2.actual_reserve_amount\n" +
                "\t\tFROM midea_sd_reserve_area ra1\n" +
                "\t\t\tLEFT JOIN midea_sd_reserve_area_detail ra2 ON ra2.reserve_area_id = ra1.id\n" +
                "\t\t\tINNER JOIN (\n" +
                "\t\t\t\tSELECT r2.transaction_id, MAX(r1.exec_date) AS exec_date\n" +
                "\t\t\t\tFROM midea_sd_reserve_area r1\n" +
                "\t\t\t\t\tLEFT JOIN midea_sd_reserve_area_detail r2 ON r2.reserve_area_id = r1.id\n" +
                "\t\t\t\tWHERE r1.status_code = 'SD050104'\n" +
                "\t\t\t\tGROUP BY r2.contract_id\n" +
                "\t\t\t) ra3\n" +
                "\t\t\tON ra3.transaction_id = ra2.transaction_id\n" +
                "\t\t\t\tAND ra3.exec_date = ra1.exec_date\n" +
                "\t\tWHERE ra1.status_code = 'SD050104'\n" +
                "\t) areaDetail\n" +
                "\tON areaDetail.transaction_id = tran.id\n" +
                "\tLEFT JOIN midea_sd_payment_method payment ON tran.payment_method_id = payment.id\n" +
                "\tLEFT JOIN midea_sd_sddict_item sdd_item ON payment.payment_type_code = sdd_item.CODE\n" +
                "\tLEFT JOIN midea_sd_sddict_item sddictitem ON sddictitem.code = payment.payment_type_code\n" +
                "\tLEFT JOIN midea_sd_payment_method depayment ON tran.depayment_method_id = depayment.id\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT tran.id AS id, GROUP_CONCAT(DISTINCT midea_sd_user_account.name) AS salesName\n" +
                "\t\tFROM midea_sd_order_contract_transaction tran\n" +
                "\t\t\tLEFT JOIN midea_sd_transaction_sale_member salesMemberItem\n" +
                "\t\t\tON tran.id = salesMemberItem.transaction_id\n" +
                "\t\t\t\tAND salesMemberItem.modify_type != 1\n" +
                "\t\t\tLEFT JOIN midea_sd_user_account ON salesMemberItem.sale_member_id = midea_sd_user_account.id\n" +
                "\t\tWHERE 1 = 1\n" +
                "\t\tGROUP BY tran.id\n" +
                "\t) salesTeam\n" +
                "\tON salesTeam.id = tran.id\n" +
                "WHERE contract.status_code IN ('SD040501', 'SD040502')\n" +
                "GROUP BY contract.id", stmt.toString());

        System.out.println(stmt.toString());
    }



}