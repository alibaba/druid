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
public class SQLServerWallTest_7 extends TestCase {

    public void test_true() throws Exception {
        WallProvider provider = new SQLServerWallProvider();

        provider.getConfig().setSelectHavingAlwayTrueCheck(true);

        Assert.assertTrue(provider.checkValid(//
        "select * from (select top 3000 jol.id as primaryKeyIds, jo.orderNo as 委托单号, bm.custname as 委托方名称, manuCustInfo.custname as 出证方, jo.chkerDate as 委托日期, bcc.connectName as 联系人, bcc.mobile as  电话,jol.barcode as 样品条码, jol.name as 样品名称,  jol.qtysub as 套件数量,jol.OnlyCode as 唯一识别码,jol.manufacture as 制造厂商,jol.sprc as 规格型号, jol.outno as 出厂编号, djod.name as 任务状态, djoc.name  委托类别, dct.name as 证书类型,bdp.organ_name as 分配室组, jol.task_id as 任务ID, be.user_name as 检定人员, verifyUser.user_name as 核验员, checkUser.user_name as 批准员, jol.ddate 交款日期,cjcs.name as 缴款状态, jc.moneys as 应收费, jc.moneystotal as 实收费, be.organ_id from jl_OrderList jol left outer join jl_CheckList jc on jol.checkList_id = jc.id left outer join jl_Order jo on jol.orderid = jo.id left outer join ba_dptinfo bdp on jol.dptid = bdp.id left outer join jl_Task jt on jol.task_id = jt.id left outer join ba_manucustinfo bm on jo.entrustCustId = bm.id left outer join ba_manucustinfo manuCustInfo on jo.custid = manuCustInfo.id left outer join ba_custconnect bcc on jo.linkPerson = bcc.id left outer join ba_employeeinfo be on jt.jdUserId = be.id left outer join ba_employeeinfo verifyUser on jt.verifyUser = verifyUser.id left outer join ba_employeeinfo checkUser on jt.checkUser = checkUser.id left outer join dict_jl_order_doType djod on jol.dotypeid = djod.id left outer join dict_jl_order_class djoc on jo.orderclassid = djoc.id left outer join dict_certificate_type dct on jt.certificatetype = dct.id left outer join dict_jl_chargestatus cjcs on jol.chargestatus = cjcs.id where jol.id >= (select min(primaryKeyId) from (select top  3000 jol.id as primaryKeyId from  jl_OrderList jol left outer join jl_CheckList jc on jol.checkList_id = jc.id left outer join jl_Order jo on jol.orderid = jo.id left outer join ba_dptinfo bdp on jol.dptid = bdp.id left outer join jl_Task jt on jol.task_id = jt.id left outer join ba_manucustinfo bm on jo.entrustCustId = bm.id left outer join ba_manucustinfo manuCustInfo on jo.custid = manuCustInfo.id left outer join ba_custconnect bcc on jo.linkPerson = bcc.id left outer join ba_employeeinfo be on jt.jdUserId = be.id left outer join ba_employeeinfo verifyUser on jt.verifyUser = verifyUser.id left outer join ba_employeeinfo checkUser on jt.checkUser = checkUser.id left outer join dict_jl_order_doType djod on jol.dotypeid = djod.id left outer join dict_jl_order_class djoc on jo.orderclassid = djoc.id left outer join dict_certificate_type dct on jt.certificatetype = dct.id left outer join dict_jl_chargestatus cjcs on jol.chargestatus = cjcs.id where jo.chkerDate >= ? and jo.chkerDate <= ? and jol.ddate >= ? order by jol.id desc, jo.id DESC) as T)  and jo.chkerDate >= ? and jo.chkerDate <= ? and jol.ddate >= ? order by jol.id asc, jo.id DESC) as T1 order by T1.primaryKeyIds desc"));

//        Assert.assertEquals(12, provider.getTableStats().size());
//        Assert.assertTrue(provider.getTableStats().containsKey("jl_OrderList"));
    }

}
