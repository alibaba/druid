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
package com.alibaba.druid.bvt.sql.mysql.visitor;

import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlParameterizedOutputVisitorTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT appsheetserialno FROM app_trans WHERE nodeid = _gbk '619' "
                     + " AND alino = _gbk '2013110900031031001700thfund00163619'"
                     + " AND apserialno = _gbk '201405120002300002170013205458'";
        System.out.println(ParameterizedOutputVisitorUtils.parameterize(sql, "mysql"));
        System.out.println("-----------------------");

        sql = "SELECT p.AppId, p.PlayerId, p.Nickname, p.CreateTime FROM acc_playeruser AS pu INNER JOIN acc_player AS p ON (pu.AppId = p.AppId AND pu.PlayerId = p.PlayerId) WHERE pu.UserId=x'881A58F6204D4E048F66E41596A66A57';";
        System.out.println(ParameterizedOutputVisitorUtils.parameterize(sql, "mysql"));
        System.out.println("-----------------------");

        sql = "SET @now := NOW();   INSERT characters (uid, name, race, career, sex, creation)   VALUES    (4068548, '', 58, 0, 0, @now);      SET @id := LAST_INSERT_ID();     INSERT characters_data (character_id, data, creation)   VALUES (@id, '\0\0\0\0\0\0\0\04068548\0\0\0:\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0<\0\0\0d\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0d\0\0\0__\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0', @now);      SELECT @id; ";
        System.out.println(ParameterizedOutputVisitorUtils.parameterize(sql, "mysql"));
        System.out.println("-----------------------");

        sql = "INSERT INTO qrtz_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP, DESCRIPTION, JOB_CLASS_NAME, IS_DURABLE, IS_NONCONCURRENT, IS_UPDATE_DATA, REQUESTS_RECOVERY, JOB_DATA)  VALUES('DefaultQuartzScheduler', 'taobao.item.recommend.delete_368815794_2_35391685928', 'tasks', null, 'cn.agooo.job.TasksJob', 0, 0, 0, 0, x'ACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C770800000010000000017400077461736B7349647372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000000002063227800')";
        System.out.println(ParameterizedOutputVisitorUtils.parameterize(sql, "mysql"));
        System.out.println("-----------------------");

        sql = "SELECT * FROM sync_contactsdata WHERE __id=x'2EEE5AE7CB0E4AF697D966AE8BF046B8'";
        System.out.println(ParameterizedOutputVisitorUtils.parameterize(sql, "mysql"));
        System.out.println("-----------------------");

        sql = "select TID,POSTFEE,RECEIVERADDRESS from tradegroup003 where (0 = 650954981082695 or tid=650954981082695) And SELLERNICK='______________' And BUYERNICK='yingge7' and CREATED > date_sub(now(), interval 48 hour) and STATUS = 'WAIT_BUYER_PAY' and func_isNotFollowgroup003(tradegroup003.tid,'______________') = 0";
        System.out.println(ParameterizedOutputVisitorUtils.parameterize(sql, "mysql"));
        System.out.println("-----------------------");

        sql = "SHOW COLUMNS FROM `pms_purchase_ops`/*20140512152820##%2Fbase.php%3Fshopid%3D%26module%3Dpms%26action%3DqualityItem%26op_id%3D756%26params%3Dcase%253Aupdate%252Ctable%253Aquality%252Corder_id%253A201405090006DL%252Creceive_no%253AQCI01201405090006DL%252Creceive_treat%253AALL_DONE%260.5652460628381133*/;";
        System.out.println(ParameterizedOutputVisitorUtils.parameterize(sql, "mysql"));
        System.out.println("-----------------------");
    }

    public void test_1() throws Exception {
        String sql = "create  or replace  view  vtmp_log_node_204180 as select `twf_log_group_user`.`uni_id` as `user_id`, `twf_log_group_user`.`control_group_type` from `twf_log_group_user` where `twf_log_group_user`.`subjob_id` = 204180";
        System.out.println(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
        System.out.println("-----------------------");

        sql = " create view V_CustomerGoodsNf as select a.*,b.GoodsNO,b.GoodsName,b.SpecName,c.CustomerName,c.NickName,c.Tel,c.Email,  StatusExt = (CASE a.curStatus WHEN (0) THEN ('待通知')        WHEN (1) THEN ('已通知') WHEN (2) THEN ('已过期') WHEN (3) THEN ('被取消')END)  from G_Customer_GoodsNotify a  left outer join  V_GoodsListBySpec b on a.GoodsID=b.GoodsID and a.SpecID=b.SpecID left outer join G_Customer_CustomerList c on a.CustomerID=c.CustomerID";
        System.out.println(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.SQL_SERVER));
        System.out.println("-----------------------");

        sql = "CREATE view V_API_GoodsMatch as SELECT a.SpecName, a.GoodsNO, a.GoodsName, bMatch = CONVERT(bit, (CASE isnull(b.Numiid, '') WHEN ('') THEN (0) ELSE (1) END)), issys1 = (CASE isnull(b.issys, '') WHEN ('') THEN ('无') WHEN (0) THEN ('未同步') WHEN (1) THEN ('待同步') WHEN (2) THEN ('同步失败') WHEN (3) THEN ('同步成功') END), bFixNum = isnull(b.bFixNum, 0), bVirNum = isnull(b.bVirNum, 0), issys = isnull(b.issys, 0), FixNum = isnull(b.FixNum, 0), VirNumBase = isnull(b.VirNumBase, 0), VirNumInc = isnull(b.VirNumInc, 0), b.TBName, b.TBSku, b.UpdateTime, b.syscount, b.sysLog, c.ShopName, b.TBOuterID, b.SKUOuterID, d .FlagName, b.GoodsID, b.SpecID, b.Numiid, b.Skuid, b.BTBGoods, b.sysGoodsType, b.ID, bstop = CONVERT(bit, isnull(b.bstop, 0)), b.bSingletb, b.SingleNumPer, b.VirNumTop, isnull(b.goodstype, '0') AS goodstype, CASE b.SpecID WHEN - 2 THEN 1 ELSE 0 END AS pcbs FROM V_GoodsSpec a LEFT OUTER JOIN G_API_SysMatch b ON a.GoodsID = b.GoodsID AND CASE b.SpecID WHEN - 2 THEN 1 ELSE a.specid END = CASE b.SpecID WHEN - 2 THEN 1 ELSE b.SpecID END AND b.GoodsType = 0 LEFT OUTER JOIN G_Cfg_ShopList c ON b.ShopID = c.ShopID LEFT OUTER JOIN dbo.G_Cfg_RecordFlag d ON a.FlagID = d .FlagID UNION ALL SELECT '' AS SpecName, a.GoodsNO, a.GoodsName, bMatch = CONVERT(bit, (CASE isnull(b.Numiid, '') WHEN ('') THEN (0) ELSE (1) END)), issys1 = (CASE isnull(b.issys, '') WHEN ('') THEN ('无') WHEN (0) THEN ('未同步') WHEN (1) THEN ('待同步') WHEN (2) THEN ('同步失败') WHEN (3) THEN ('同步成功') END), bFixNum = isnull(b.bFixNum, 0), bVirNum = isnull(b.bVirNum, 0), issys = isnull(b.issys, 0), FixNum = isnull(b.FixNum, 0), VirNumBase = isnull(b.VirNumBase, 0), VirNumInc = isnull(b.VirNumInc, 0), b.TBName, b.TBSku, b.UpdateTime, b.syscount, b.sysLog, c.ShopName, b.TBOuterID, b.SKUOuterID, '' AS FlagName, b.GoodsID, b.SpecID, b.Numiid, b.Skuid, b.BTBGoods, b.sysGoodsType, b.ID, bstop = CONVERT(bit, isnull(b.bstop, 0)), b.bSingletb, b.SingleNumPer, b.VirNumTop, b.goodstype, 0 AS pcbs FROM g_goods_goodslistfit a LEFT OUTER JOIN g_api_sysma";
        System.out.println(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.SQL_SERVER));
        System.out.println("-----------------------");
    }

}
