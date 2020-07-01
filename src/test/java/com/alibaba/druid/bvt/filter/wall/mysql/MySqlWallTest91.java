/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.filter.wall.mysql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

/**
 * SQLServerWallTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class MySqlWallTest91 extends TestCase {

    private WallProvider initWallProvider() {
        WallProvider provider = new MySqlWallProvider();

        provider.getConfig().setStrictSyntaxCheck(false);
        provider.getConfig().setMultiStatementAllow(true);
        provider.getConfig().setConditionAndAlwayTrueAllow(true);
        provider.getConfig().setNoneBaseStatementAllow(true);
        provider.getConfig().setLimitZeroAllow(true);
        provider.getConfig().setConditionDoubleConstAllow(true);

        provider.getConfig().setCommentAllow(true);
        provider.getConfig().setSelectUnionCheck(false);

        return provider;
    }

    public void test_false() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "SELECT t.session_id,t.voip_send,t.voip_rece,t.appid,t.begin_time,substring_index(t1.callbackUrl,':',1) AS servicetype, SUBSTRING_INDEX(substring_index(t1.callbackUrl,'/',3),'/',-1) AS hostname, CASE WHEN SUBSTR(t1.callbackUrl,-2)='**' THEN REPLACE(t1.callbackUrl,'**','OfflineMsgNotify') ELSE t1.callbackUrl END AS url FROM `im_session_info` t INNER JOIN ccp_application t1 ON t.appid=t1.appId WHERE t.end_time IS NULL AND ((DATABASE()='openser' AND t1.`status`=2) OR ((DATABASE()<>'openser') AND t1.`status` IN (1,2,3))) AND INSTR(t1.funInfo,'1004')>0 AND (0=0 OR (0>0 AND t.id<0)) ORDER BY t.id DESC LIMIT 100; ";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false1() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "SELECT * FROM `group` WHERE group.nickname like '%' AND 8709=IF(((66+67)>222),SLEEP(5),8709) AND 'Ttyv' LIKE 'Ttyv%' OR group.fullname like '%' AND 8709=IF(((66+67)>222),SLEEP(5),8709) AND 'Ttyv' LIKE 'Ttyv%'";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false2() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "SELECT user.id,user.name FROM `user` WHERE user.name like '%' AND 7961=IF(((27+33)>159),SLEEP(5),7961) AND 'dewN' LIKE 'dewN%' AND user.activated = 1";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false3() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "SELECT * FROM mp_Sites WHERE SiteID = -1 OR -1 = -1 --ORDER BY SiteID LIMIT 1 ";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false4() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "select cid,title,id,img,fan from duoduo_mall where cid = cid and 1=1 --order by sort desc limit 17 ";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false5() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "select count(1) as cot from w36ma_picking where (picking_no='' or ''='') and (DATE_FORMAT(create_time,'%Y-%m-%d') = '' or ''='') --";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false6() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = " select pg.*,an1.w36ma_name as create_name, an2.w36ma_name as print_name, an2.w36ma_name as receive_name, an2.w36ma_name as products_name, an2.w36ma_name as warehouse_name from w36ma_picking as pg left join iweb_admin as an1 on pg.create_name_id=an1.id left join iweb_admin as an2 on pg.print_name_id=an2.id left join iweb_admin as an3 on pg.receive_name_id=an3.id left join iweb_admin as an4 on pg.products_name_id=an4.id left join iweb_admin as an5 on pg.warehouse_name_id=an5.id where (pg.picking_no='' or ''='') and (DATE_FORMAT(pg.create_time,'%Y-%m-%d') = '' or ''='') --limit 0,20 ";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false7() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "select sysuser0_.sysuser_id as sysuser1_163_, sysuser0_.sysuser_name as sysuser2_163_, sysuser0_.sysuser_loginname as sysuser3_163_, sysuser0_.sysuser_password as sysuser4_163_, sysuser0_.sysuser_mobilenum as sysuser5_163_, sysuser0_.sysuser_email as sysuser6_163_, sysuser0_.sysuser_phonenum as sysuser7_163_, sysuser0_.sysuser_createtime as sysuser8_163_, sysuser0_.sysuser_lastupdate as sysuser9_163_, sysuser0_.sysuser_status as sysuser10_163_, sysuser0_.sysuser_loginip as sysuser11_163_, sysuser0_.sysuser_interfacesn as sysuser12_163_, sysuser0_.customer_customer_id as customer13_163_, sysuser0_.role_role_id as role14_163_ from sysuser sysuser0_ where sysuser0_.sysuser_status=1 and sysuser0_.role_role_id=5 and sysuser0_.sysuser_loginname='sms_bftl2' and USER()=USER() and 'EYrc'='EYrc'";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false8() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "select sum(payment_ft) from order_goods where order_id=72353 AND (SELECT 3791 FROM(SELECT COUNT(*),CONCAT(CHAR(58,110,106,120,58),(SELECT (CASE WHEN (3791=3791) THEN 1 ELSE 0 END)),CHAR(58,116,116,113,58),FLOOR(RAND(0)*2))x FROM information_schema.tables GROUP BY x)a)";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false10() {
        WallProvider provider = initWallProvider();
        {
            String sql = "select count(*) from messages a where a.id in (2 and 1 AND 9881=IF((ORD(MID((IFNULL(CAST(DATABASE() AS CHAR),0x20)),6,1))>117),SLEEP(5),9881)) and a.message <> 'hello' and a.message like 'Little'";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false11() {
        WallProvider provider = initWallProvider();
        {
            String sql = "select * from messages where id=1 limit (select count(*) from products group by concat(version(),0x27202020,floor(rand(0)*2-1)));";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false12() {
        WallProvider provider = initWallProvider();
        {
            String sql = "select * from dede_admin where id=1 order by if((ascii(substr(user(),1,1))>95),1,2);";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false13() {
        WallProvider provider = initWallProvider();
        {
            String sql = "select * from dede_admin where id=1 limit if((ascii(substr(user(),1,1))>95),1,0);";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_true1() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "SELECT *, DATABASE(), NOW() AS a_now FROM `acp_globalauth` WHERE `username` = 'info@nyip.cn'";
            Assert.assertTrue(provider.checkValid(sql));
        }
    }

    public void test_true2() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "DELETE SYS_ACCNBAL WHERE ISNULL(AMTN_Y,0)=0 AND ISNULL(AMTN_B,0)=0 AND ISNULL(AMTN_D,0)=0 AND ISNULL(AMTN_C,0)=0";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "select * from (select '已结算' as JSFare,TB_LogisticsCostSettle.s_id,TB_LogisticsCostSettle.XSD_billsn,TB_LogisticsCostSettle.OrderDD_list,TB_LogisticsCostSettle.OrderId_list,TBShop.ShopName,TB_LogisticsCostSettle.consign_time,TB_LogisticsCostSettle.Logistics_ID,TB_LogisticsCompanySet.Company,isnull(TB_LogisticsCostSettle.TBFareMoney,0.00)TBFareMoney,TB_LogisticsCostSettle.TBProdSJZNNumber,isnull(TB_LogisticsCostSettle.SJLogisticsCostMoney,0.00)LogisticsCostMoney,isnull(TB_LogisticsCostSettle.SJLogisticsCostMoney,0.00)SJLogisticsCostMoney,isnull(TB_LogisticsCostSettle.SJLogisticsCostMoney,0)-isnull(TB_LogisticsCostSettle.TBJSFareMoney,0) as NotMoney,TB_LogisticsCostSettle.TBAddressee,TB_LogisticsCostSettle.TBAddress,TB_LogisticsCostSettle.TBMobile,TB_LogisticsCostSettle.TBLinephone,TB_LogisticsCostSettle.TBJSFareMoney,TB_LogisticsCostSettle.Express,TB_LogisticsCostSettle.Fhuser_list,TB_LogisticsCostSettle.LogisticsCost_billsn,dbo.F_GetLogisticsFKDMoney(TB_LogisticsCostSettle.s_id,TB_LogisticsCostSettle.LogisticsCost_billsn) fkdmoney,isnull(TB_LogisticsCostSettle.TBFareMoney,0)-isnull(TB_LogisticsCostSettle.SJLogisticsCostMoney,0) LogisticsProfitMoney,isnull(TB_LogisticsCostSettle.IsEnterFare,0)IsEnterFare from TB_LogisticsCostSettle left join TBShop on TB_LogisticsCostSettle.TBShop_ID=TBShop.ShopID left join TB_LogisticsCompanySet on TB_LogisticsCostSettle.Logistics_ID=TB_LogisticsCompanySet.s_ID where isnull(TB_LogisticsCostSettle.IsEnterFare,0)=1 or (isnull(TB_LogisticsCostSettle.IsEnterFare,0)=2 and isnull(TB_LogisticsCostSettle.SJLogisticsCostMoney,0)-isnull(TB_LogisticsCostSettle.TBJSFareMoney,0)>0))MTwhere 1=1and MT.consign_time>='2013-04-28 00:00:00' and MT.consign_time<='2013-05-28 23:59:59'";
            Assert.assertTrue(provider.checkValid(sql));
        }
    }

    public void test_false9() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "select  PROJECT_NAME, TABLE_NAME, EXPORT_COLUMNS, CURRENT_TIMESTAMP() start_time from (SELECT PROJECT_NAME, TABLE_NAME, EXPORT_COLUMNS, @rank := @rank + 1 AS rank FROM (  SELECT  PROJECT_NAME,  TABLE_NAME,  (   SELECT   CASE   WHEN GROUP_CONCAT(COLUMN_name) LIKE 'ID,%' THEN   SUBSTR(    GROUP_CONCAT(COLUMN_name),    4   )   ELSE   GROUP_CONCAT(COLUMN_name)   END   FROM   Information_schema.`COLUMNS` A   WHERE   A.table_name = B.TABLE_NAME   ORDER BY   ORDINAL_POSITION  ) EXPORT_COLUMNS  FROM  ETL_EXPORT b   ORDER BY  PROJECT_NAME,  TABLE_NAME ) tmp, (SELECT @rank := 0) a) b WHERE rank='2';";
            Assert.assertFalse(provider.checkValid(sql));
        }
        {
            String sql = "select  PROJECT_NAME, TABLE_NAME, EXPORT_COLUMNS, CURRENT_TIMESTAMP() start_time, case when type=1 then ' where day_id = 20130101 '  when type=2 and substr('20130101','7,2')='01' then 'where month_id=201301 ' else 'where 3=5 ' end export_where_data from (SELECT PROJECT_NAME, TABLE_NAME, EXPORT_COLUMNS, type, @rank := @rank + 1 AS rank FROM (  SELECT  PROJECT_NAME,  TABLE_NAME,  type,  (   SELECT   CASE   WHEN GROUP_CONCAT(COLUMN_name) LIKE 'ID,%' THEN   SUBSTR(    GROUP_CONCAT(COLUMN_name),    4   )   ELSE   GROUP_CONCAT(COLUMN_name)   END   FROM   Information_schema.`COLUMNS` A   WHERE   A.table_name = concat(B.TABLE_NAME,'_','201301')   ORDER BY   ORDINAL_POSITION  ) EXPORT_COLUMNS  FROM  ETL_EXPORT b  where project_name in ('acc')  ORDER BY  PROJECT_NAME,  TABLE_NAME ) tmp, (SELECT @rank := 0) a) b WHERE rank='3';";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_true4() {
        WallProvider provider = initWallProvider();
        {
            String sql = "SELECT 10006, @";
            Assert.assertTrue(provider.checkValid(sql));
        }
    }

    public void test_true5() {
        WallProvider provider = initWallProvider();
        {
            String sql = "select * from view_featureWarm where 1 = 1 and MaterialID in (select Materialid from material where Code like '%%' and name like '%%' and specs like '%%') and sumbalcqty <> 0";
            Assert.assertTrue(provider.checkValid(sql));
        }
    }

    public void test_true() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "SELECT CONVERT(load_file(concat(@@datadir, 'sandbox', '/', 'ccp_application', '.frm')) USING utf8) AS source";
            Assert.assertTrue(provider.checkValid(sql));
        }

        {
            String sql = "select c.COLUMN_NAME, COLUMNPROPERTY(OBJECT_ID(c.TABLE_SCHEMA + '.' + c.TABLE_NAME), c.COLUMN_NAME, 'IsIdentity') from sys.KEY_COLUMN_USAGE c join sys.TABLE_CONSTRAINTS p on p.CONSTRAINT_NAME = c.CONSTRAINT_NAME where c.TABLE_NAME = @p1 and c.TABLE_SCHEMA = @p2 and p.TABLE_SCHEMA = @p2 and p.CONSTRAINT_TYPE = 'PRIMARY KEY'";
            Assert.assertTrue(provider.checkValid(sql));
        }

        {
            String sql = "SELECT user_name(), @@MAX_PRECISION, is_member('db_owner'), permissions(), DatabasePropertyEx(db_name(), N'collation'), SERVERPROPERTY('IsFullTextInstalled'), schema_name()";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "select col.name, col.column_id, st.name as DT_name, schema_name(st.schema_id) as DT_schema, col.max_length, col.precision, col.scale, bt.name as BT_name, col.collation_name, col.is_nullable, col.is_ansi_padded, col.is_rowguidcol, col.is_identity, case when(idc.column_id is null) then null else CONVERT(nvarchar(40), idc.seed_value) end, case when(idc.column_id is null) then null else CONVERT(nvarchar(40), idc.increment_value) end, CONVERT(bit, case when(cmc.column_id is null) then 0 else 1 end) as is_computed, convert(bit, ColumnProperty(col.object_id, col.name, N'IsIdNotForRepl')) as IsIdNotForRepl, col.is_replicated, col.is_non_sql_subscribed, col.is_merge_published, col.is_dts_replicated, col.rule_object_id, robj.name as Rul_name, schema_name(robj.schema_id) as Rul_schema, col.default_object_id, OBJECTPROPERTY(col.default_object_id, N'IsDefaultCnst') as is_defcnst, dobj.name as def_name, schema_name(dobj.schema_id) as def_schema, CONVERT(bit, case when (ftc.column_id is null) then 0 else 1 end) as is_FullTextCol, col_name(col.object_id, ftc.type_column_id) FT_type_column, ftc.language_id as FT_language_id, case when(cmc.column_id is null) then null else cmc.definition end as formular, case when(cmc.column_id is null) then null else cmc.is_persisted end as is_persisted, defCst.definition, COLUMNPROPERTY(col.object_id, col.name, 'IsDeterministic') as IsDeterministic, xmlcoll.name as xmlSchema_name, schema_name(xmlcoll.schema_id) as xmlSchema_schema, col.is_xml_document, col.is_sparse, col.is_column_set from sys.columns col left outer join sys.types st on st.user_type_id = col.user_type_id left outer join sys.types bt on bt.user_type_id = col.system_type_id left outer join sys.objects robj on robj.object_id = col.rule_object_id and robj.type = 'R' left outer join sys.objects dobj on dobj.object_id = col.default_object_id and dobj.type = 'D' left outer join sys.default_constraints defCst on defCst.parent_object_id = col.object_id and defCst.parent_column_id = col.column_id left outer join sys.identity_columns idc o";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "SELECT * FROM `www_subject` WHERE subject like '哎%' or subject like '%' ORDER BY aid desc LIMIT 30 ";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "SELECT COUNT(*) FROM syscolumns WHERE id=OBJECT_ID(N'taobao_order') AND name='r3_billtype'";
            Assert.assertTrue(provider.checkValid(sql));
        }

        {
            String sql = "select col.name, st.name as DT_name, case when (st.name in ('nchar', 'nvarchar') and (col.max_length > 0)) then col.max_length / 2 else col.max_length end, col.precision, col.scale, bt.name as BT_name, col.is_nullable, col.is_identity,col.is_rowguidcol, OBJECTPROPERTY(col.default_object_id, N'IsDefaultCnst') as is_defcnst, CONVERT(bit, case when(cmc.column_id is null) then 0 else 1 end) as is_computed, case when(cmc.column_id is null) then null else cmc.definition end as formular, col.collation_name, col.system_type_id from nowshop.sys.all_columns col left outer join nowshop.sys.types st on st.user_type_id = col.user_type_id left outer join nowshop.sys.types bt on bt.user_type_id = col.system_type_id left outer join nowshop.sys.identity_columns idc on idc.object_id = col.object_id and idc.column_id = col.column_id left outer join nowshop.sys.computed_columns cmc on cmc.object_id = col.object_id and cmc.column_id = col.column_id where col.object_id = object_id(N'nowshop.dbo.hr_shop') order by col.column_id";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "select * from tb_product_word where name='' or CONCAT(name,style)='' or CONCAT(shop,style)='' or CONCAT(ename,style)=''";
            Assert.assertTrue(provider.checkValid(sql));
        }
    }
    
    public void test() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "SELECT * FROM `oammxncom2014`.`ecs_free_bank` where 1 and 1='1'";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "SELECT * FROM `oammxncom2014`.`ecs_free_bank` where 1 or 1='1'";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "SELECT * FROM `oammxncom2014`.`ecs_free_bank` where true or 1='1'";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "SELECT * FROM `oammxncom2014`.`ecs_free_bank` where 'a' or 1='1'";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "SELECT * FROM `oammxncom2014`.`ecs_free_bank` where id=1 or 1='1' --";
            Assert.assertFalse(provider.checkValid(sql));
        }
//        {
//            String sql = "SELECT * FROM `oammxncom2014`.`ecs_free_bank` where id=1 or true --";
//            Assert.assertFalse(provider.checkValid(sql));
//        }
    }
}
