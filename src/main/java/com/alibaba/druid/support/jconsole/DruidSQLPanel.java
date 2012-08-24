/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.support.jconsole;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.json.JSONUtils;

public class DruidSQLPanel extends DruidPanel {

    private static final long serialVersionUID = 1L;

    protected void doInBackground(MBeanServerConnection conn) throws Exception {
        ObjectName name = new ObjectName(DruidStatService.MBEAN_NAME);
        String url = "/sql.json";
        String result = (String) conn.invoke(name, "service", new String[] { url }, new String[] { String.class.getName() });
        Object o = JSONUtils.parse(result);

        System.out.println(o);
    }
}
