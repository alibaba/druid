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
package com.alibaba.druid.pool.vendor;

import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.pool.ValidConnectionCheckerAdapter;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A MSSQLValidConnectionChecker.
 */
public class MSSQLValidConnectionChecker extends ValidConnectionCheckerAdapter implements ValidConnectionChecker, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log  LOG              = LogFactory.getLog(MSSQLValidConnectionChecker.class);

    public MSSQLValidConnectionChecker(){

    }

    public boolean isValidConnection(final Connection c, String validateQuery, int validationQueryTimeout) {
        try {
            if (c.isClosed()) {
                return false;
            }
        } catch (SQLException ex) {
            // skip
            return false;
        }

        Statement stmt = null;

        try {
            stmt = c.createStatement();
            stmt.setQueryTimeout(validationQueryTimeout);
            stmt.execute(validateQuery);
            return true;
        } catch (SQLException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("warning: connection validation failed for current managed connection.");
            }
            return false;
        } finally {
            JdbcUtils.close(stmt);
        }
    }

}
