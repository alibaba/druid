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
package com.alibaba.druid.pool.ha.cobar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.valid.DefaultDataSourceFailureDetecter;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

public class CobarFailureDetecter extends DefaultDataSourceFailureDetecter {

    private final static Log LOG                 = LogFactory.getLog(CobarFailureDetecter.class);

    private int              queryTimeoutSeconds = 30;

    public CobarFailureDetecter(){
        this.setValidateSql("SHOW COBAR_STATUS");
    }

    public int getQueryTimeoutSeconds() {
        return queryTimeoutSeconds;
    }

    public void setQueryTimeoutSeconds(int queryTimeoutSeconds) {
        this.queryTimeoutSeconds = queryTimeoutSeconds;
    }

    public boolean isValidConnection(DruidDataSource dataSource, Connection conn) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            stmt.setQueryTimeout(queryTimeoutSeconds);
            rs = stmt.executeQuery(getValidateSql());
            if (!rs.next()) {
                return false;
            }

            String status = rs.getString(1);

            if ("on".equalsIgnoreCase(status)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            LOG.error("check datasource valid errror", ex);
            return false;
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }

    }
}
