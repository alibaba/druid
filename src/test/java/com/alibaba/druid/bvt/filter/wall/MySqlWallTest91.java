/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.filter.wall;

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

        provider.getConfig().setUseAllow(true);
        provider.getConfig().setStrictSyntaxCheck(false);
        provider.getConfig().setMultiStatementAllow(true);
        provider.getConfig().setConditionAndAlwayTrueAllow(true);
        provider.getConfig().setNoneBaseStatementAllow(true);
        provider.getConfig().setSelectUnionCheck(false);
        provider.getConfig().setSchemaCheck(true);
        provider.getConfig().setLimitZeroAllow(true);
        provider.getConfig().setCommentAllow(true);

        return provider;
    }

    public void test_false() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "SELECT count(*) FROM i_user_commentary WHERE item_type = 7 AND item_id = 30 AND SLEEP(5) AND is_del=0";
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
            String sql = "SELECT * FROM mp_Sites WHERE SiteID = -1 OR -1 = -1 ORDER BY SiteID LIMIT 1 ";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false4() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "select cid,title,id,img,fan from duoduo_mall where cid = cid and 1=1 order by sort desc limit 17 ";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false5() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "select count(1) as cot from w36ma_picking where (picking_no='' or ''='') and (DATE_FORMAT(create_time,'%Y-%m-%d') = '' or ''='')";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false6() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = " select pg.*,an1.w36ma_name as create_name, an2.w36ma_name as print_name, an2.w36ma_name as receive_name, an2.w36ma_name as products_name, an2.w36ma_name as warehouse_name from w36ma_picking as pg left join iweb_admin as an1 on pg.create_name_id=an1.id left join iweb_admin as an2 on pg.print_name_id=an2.id left join iweb_admin as an3 on pg.receive_name_id=an3.id left join iweb_admin as an4 on pg.products_name_id=an4.id left join iweb_admin as an5 on pg.warehouse_name_id=an5.id where (pg.picking_no='' or ''='') and (DATE_FORMAT(pg.create_time,'%Y-%m-%d') = '' or ''='') limit 0,20 ";
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

}
