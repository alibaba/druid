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

import com.alibaba.druid.wall.WallUtils;

public class OracleWallTest6 extends TestCase {

    public void test_true() throws Exception {
        String sql = //
        "select *"
        + "  from (SELECT Distinct notice.NSERIAL,"
        + "                        unit.CUNIT_name,"
        + "                        us.CUSER_name,"
        + "                        to_char(notice.DCREATE, 'yyyy-mm-dd') DCREATE,"
        + "                        notice.csubject,"
        + "                        notice.ccontent"
        + "          From mewp_notice_info notice, bas_user us, bas_unit unit"
        + "          Left Join bas_area xsArea"
        + "            On unit.ccounty_code = xsArea.Carea_Code"
        + "          Left Join bas_area pqArea"
        + "            On unit.cpiece_code = pqArea.Carea_Code"
        + "          Left Join bas_area xzArea"
        + "            On unit.cctown_code = xzArea.Carea_Code"
        + "          Left Join bas_area czArea"
        + "            On unit.cvillage_code = czArea.Carea_Code"
        + "         Where notice.cunit_code = unit.cunit_code"
        + "           And notice.cuser_id = us.cuser_code"
        + "           And notice.nstate = 4"
        + "           And (notice.nrole = '****' or notice.nrole = '202')"
        + "           and notice.cunit_code in"
        + "               (select 'CP0008'"
        + "                  from dual"
        + "                union"
        + "                select Distinct t.cunit_code"
        + "                  from bas_unit t, bas_area us"
        + "                 where t.ccounty_code = us.carea_code"
        + "                   and t.ccounty_code = 'CP'"
        + "                   and t.ctype = '201'"
        + "                union"
        + "                select Distinct auser.cunit_code"
        + "                  from bas_user auser, bas_user_role ur"
        + "                 where auser.cuser_code = ur.cuser_code"
        + "                   and (ur.nrole_id = '1' or ur.nrole_id = '20'))"
        + "         order by DCREATE desc)"
        + " where rownum <= 30";
        Assert.assertTrue(WallUtils.isValidateOracle(sql));
    }
}
