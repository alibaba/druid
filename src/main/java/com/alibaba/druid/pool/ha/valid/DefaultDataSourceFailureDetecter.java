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
package com.alibaba.druid.pool.ha.valid;

import java.sql.Connection;
import java.sql.Statement;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

public class DefaultDataSourceFailureDetecter implements DataSourceFailureDetecter {

    private final static Log LOG           = LogFactory.getLog(DefaultDataSourceFailureDetecter.class);

    private long             maxWaitMillis = 3000;

    private String           validateSql   = "SELECT 1";

    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public String getValidateSql() {
        return validateSql;
    }

    public void setValidateSql(String validateSql) {
        this.validateSql = validateSql;
    }

    @Override
    public boolean isValid(DruidDataSource dataSource) {
        Connection conn = null;

        try {
            conn = dataSource.getConnection(maxWaitMillis);

            if (conn == null) {
                return false;
            }

            return isValidConnection(dataSource, conn);

        } catch (Exception ex) {
            LOG.error("check datasource valid errror", ex);
        } finally {
            JdbcUtils.close(conn);
        }

        return false;
    }

    public boolean isValidConnection(DruidDataSource dataSource, Connection conn) {
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            stmt.execute(getValidateSql());
        } catch (Exception ex) {
            LOG.error("check datasource valid errror", ex);
        } finally {
            JdbcUtils.close(stmt);
        }

        return true;
    }

}
