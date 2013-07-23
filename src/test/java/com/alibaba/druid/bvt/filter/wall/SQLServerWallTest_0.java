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
import com.alibaba.druid.wall.spi.SQLServerWallProvider;

/**
 * SQLServerWallTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class SQLServerWallTest_0 extends TestCase {

    private WallProvider initWallProvider() {
        WallProvider provider = new SQLServerWallProvider();

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

    public void test_false() {
        WallProvider provider = initWallProvider();
        {
            String sql = "SELECT KL_ArticleContent,KL_ArticleTitle FROM dbo.KL_Article WHERE KL_ArticleId =13 And (Select Top 1 unicode(substring(isNull(cast(name as varchar(8000)),char(32)),7,1)) From (Select Top 9 [dbid],[name] From master..sysdatabases Order by [dbid] desc) T Order by [dbid]) between 105 and 105";
            Assert.assertFalse(provider.checkValid(sql));
        }
        {
            String sql = "SELECT KL_ArticleContent,KL_ArticleTitle FROM dbo.KL_Article WHERE KL_ArticleId =13 and (select unicode(substring(isNull(cast(db_name() as varchar(8000)),char(32)),1,1))) between 105 and 108";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_false1() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "select OrderId,Order_Time,oResult,oState,show_Exp_Num,Is_Exp_Print,sel_Exp_Id,Order_Th,Th_Audit_Time,Th_Delay_Days from Pro_Order_List where OrderId='2012110125252' AND HOST_NAME()=HOST_NAME() AND 'kbwg'='kbwg'";
            Assert.assertFalse(provider.checkValid(sql));
        }
        {
            String sql = "update dbo.dml_health_check set name='123' where id=OBJECT_ID(N'taobao_order')";
            Assert.assertFalse(provider.checkValid(sql));
        }
    }

    public void test_true1() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "select Fg.name as TableFg, dsp.name as TexImageFg, FtCat.name as FulltextCatalog, OBJECTPROPERTY(tbl.object_id, 'TableTextInRowLimit') as TextInrowLimit, OBJECTPROPERTY(tbl.object_id, 'IsIndexable'), user_name(tbl.principal_id) as DirectOwner, tbl.is_replicated, tbl.lock_escalation_desc from sys.tables tbl left outer join sys.data_spaces dsp on dsp.data_space_id = tbl.lob_data_space_id left outer join (sys.fulltext_indexes fti inner join sys.fulltext_catalogs FtCat on FtCat.fulltext_catalog_id = fti.fulltext_catalog_id ) on fti.object_id = tbl.object_id inner join (sys.indexes idx inner join sys.data_spaces Fg on (idx.index_id = 0 or idx.index_id = 1) and Fg.data_space_id = idx.data_space_id) on idx.object_id = tbl.object_id and (idx.index_id = 0 or idx.index_id = 1) where tbl.object_id = object_id(N'dbo.nqh_TelOrder') ";
            Assert.assertTrue(provider.checkValid(sql));
        }
    }

    public void test_true() throws Exception {
        WallProvider provider = initWallProvider();
        {
            String sql = "SELECT 'Server[@Name=' + quotename(CAST(serverproperty(N'Servername') AS sysname),'''') + ']' + '/Collation[@Name=' + quotename(cl.name,'''') + ']' AS [Urn], cl.name AS [Name], CAST(COLLATIONPROPERTY(name, 'CodePage') AS int) AS [CodePage], CAST(COLLATIONPROPERTY(name, 'LCID') AS int) AS [LocaleID], CAST(COLLATIONPROPERTY(name, 'ComparisonStyle') AS int) AS [ComparisonStyle], cl.description AS [Description], CAST(COLLATIONPROPERTY(name, 'Version') AS int) AS [CollationVersion] FROM sys.fn_helpcollations() cl ORDER BY [Name] ASC";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "SELECT bp.Class_desc, bp.Permission_name, bp.Type, bp.Covering_permission_name, bp.Parent_class_desc, bp.Parent_covering_permission_name FROM sys.fn_builtin_permissions(null) as bp order by bp.class_desc";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "select isExist = case when exists(select * from dbo.sysobjects where id = object_id ('SYS_LOG_2005')) then 1 else 0 end";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "select length from syscolumns(nolock) where id = object_id('boat') and (name in ('boat_bag','skill_state'))";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "select c.COLUMN_NAME, COLUMNPROPERTY(OBJECT_ID(c.TABLE_SCHEMA + '.' + c.TABLE_NAME), c.COLUMN_NAME, 'IsIdentity') from INFORMATION_SCHEMA.KEY_COLUMN_USAGE c join INFORMATION_SCHEMA.TABLE_CONSTRAINTS p on p.CONSTRAINT_NAME = c.CONSTRAINT_NAME where c.TABLE_NAME = @p1 and c.TABLE_SCHEMA = @p2 and p.TABLE_SCHEMA = @p2 and p.CONSTRAINT_TYPE = 'PRIMARY KEY'";
            Assert.assertTrue(provider.checkValid(sql));
        }
        {
            String sql = "INSERT INTO tempdep SELECT tbl.object_id AS [ID], tbl.name AS [Name], SCHEMA_NAME(tbl.schema_id) AS [Schema], db_name(), 3 FROM sys.tables AS tbl WHERE (tbl.name=@_msparam_0 and SCHEMA_NAME(tbl.schema_id)=@_msparam_1)INSERT INTO tempdep SELECT tbl.object_id AS [ID], tbl.name AS [Name], SCHEMA_NAME(tbl.schema_id) AS [Schema], db_name(), 3 FROM sys.tables AS tbl WHERE (tbl.name=@_msparam_2 and SCHEMA_NAME(tbl.schema_id)=@_msparam_3)INSERT INTO tempdep SELECT tbl.object_id AS [ID], tbl.name AS [Name], SCHEMA_NAME(tbl.schema_id) AS [Schema], db_name(), 3 FROM sys.tables AS tbl WHERE (tbl.name=@_msparam_4 and SCHEMA_NAME(tbl.schema_id)=@_msparam_5)INSERT INTO tempdep SELECT tbl.object_id AS [ID], tbl.name AS [Name], SCHEMA_NAME(tbl.schema_id) AS [Schema], db_name(), 3 FROM sys.tables AS tbl WHERE (tbl.name=@_msparam_6 and SCHEMA_NAME(tbl.schema_id)=@_msparam_7)INSERT INTO tempdep SELECT tbl.object_id AS [ID], tbl.name AS [Name], SCHEMA_NAME(tbl.schema_id) AS [Schema], db_name(), 3 FROM sys.tables AS tbl WHERE (tbl.name=@_msparam_8 and SCHEMA_NAME(tbl.schema_id)=@_msparam_9)";
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
            String sql = "update master.dbo.dml_health_check set id=1";
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
