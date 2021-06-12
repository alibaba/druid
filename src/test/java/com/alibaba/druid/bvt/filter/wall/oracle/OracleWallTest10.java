/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.filter.wall.oracle;

import com.alibaba.druid.wall.WallUtils;
import junit.framework.TestCase;
import org.junit.Assert;

public class OracleWallTest10 extends TestCase {

    public void test_true() throws Exception {
        String sql = //
        "select\n" +
                "tpl.projectname as 项目名称,\n" +
                "tpl.address as 项目地址,\n" +
                "tpl.transactor as 联系人,\n" +
                "tpl.transactor_tel as 联系人电话,\n" +
                "dbms_lob.substr(wmsys.wm_concat(\n" +
                "case when (tap.type = 'LESS_THIS_MONTH_SALARY' AND tap.month = '4') then '4月份'\n" +
                "when (tap.type = 'LESS_THIS_MONTH_SALARY' AND tap.month = '5') then '5月份'\n" +
                "when (tap.type = 'LESS_THIS_MONTH_SALARY' AND tap.month = '6') then '6月份'\n" +
                "else null end\n" +
                ")) as YA,\n" +
                "dbms_lob.substr(wmsys.wm_concat(\n" +
                "case when tap.type = 'NO_PAY_MONTH' AND tap.month = '4' then '4月份'\n" +
                "when tap.type = 'NO_PAY_MONTH' AND tap.month = '5' then '5月份'\n" +
                "when tap.type = 'NO_PAY_MONTH' AND tap.month = '6' then '6月份'\n" +
                "else null end\n" +
                ")) as YB,\n" +
                "dbms_lob.substr(wmsys.wm_concat(\n" +
                "case when tap.type = 'SALARY_DATA_MONTH' AND tap.month = '4' then '4月份'\n" +
                "when tap.type = 'SALARY_DATA_MONTH' AND tap.month = '5' then '5月份'\n" +
                "when tap.type = 'SALARY_DATA_MONTH' AND tap.month = '6' then '6月份'\n" +
                "else null end\n" +
                ")) as YC\n" +
                "from t_alert_project tap\n" +
                "inner join t_project_list tpl on tpl.projectid = tap.projectid\n" +
                "where tap.month in ('4','5','6')\n" +
                "group by\n" +
                "tpl.projectname,\n" +
                "tpl.address,\n" +
                "tpl.transactor,\n" +
                "tpl.transactor_tel\n" +
                "order by tpl.projectname";
        Assert.assertTrue(WallUtils.isValidateOracle(sql));
    }
}
