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
package com.alibaba.druid.bvt.proxy;

import java.io.Reader;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.Statement;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.filter.logging.Log4jFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.stat.JdbcStatManager;

public class ResultSetProxyImplTest extends TestCase {

    String sql = "SELECT 1";
    
    protected void tearDown() throws Exception {
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    public void test_resultset() throws Exception {

        MockDriver driver = new MockDriver();
        DataSourceProxyConfig config = new DataSourceProxyConfig();
        config.setUrl("");
        config.setRawUrl("jdbc:mock:");
        DataSourceProxyImpl dataSource = new DataSourceProxyImpl(driver, config);

        {
            StatFilter filter = new StatFilter();
            filter.init(dataSource);
            config.getFilters().add(filter);
        }
        {
            Log4jFilter filter = new Log4jFilter();
            filter.init(dataSource);
            config.getFilters().add(filter);
        }

        Connection conn = dataSource.connect(null);

        conn.setClientInfo("name", null);

        Statement stmt = conn.createStatement();
        ResultSetProxy rs = (ResultSetProxy) stmt.executeQuery(sql);

        rs.insertRow();
        rs.refreshRow();
        rs.moveToInsertRow();
        rs.moveToCurrentRow();
        rs.next();

        rs.updateRef(1, null);
        rs.updateArray(1, null);
        rs.updateRowId(1, null);
        rs.updateNString(1, null);
        rs.updateNClob(1, (NClob) null);
        rs.updateNClob(1, (Reader) null);
        rs.updateNClob(1, (Reader) null, 0);
        rs.updateSQLXML(1, null);
        rs.updateNCharacterStream(1, null);
        rs.updateNCharacterStream(1, null, 0);

        rs.getArray("1");
        rs.updateRef("1", null);
        rs.updateArray("1", null);
        rs.updateRowId("1", null);
        rs.updateNString("1", null);
        rs.updateNClob("1", (NClob) null);
        rs.updateNClob("1", (Reader) null);
        rs.updateNClob("1", (Reader) null, 0);
        rs.updateSQLXML("1", null);
        rs.updateNCharacterStream("1", null);
        rs.updateNCharacterStream("1", null, 0);
    }

}
