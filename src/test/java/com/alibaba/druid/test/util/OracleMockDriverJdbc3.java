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
package com.alibaba.druid.test.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.mock.MockStatementBase;


public class OracleMockDriverJdbc3 extends MockDriver {
    public Connection connect(String url, Properties info) throws SQLException {
        return new OracleMockConnectionJdbc3(this, url, info);
    }
    
    @Override
    public int getMajorVersion() {
        return 10;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }
    
    public MockResultSet createMockResultSet(MockStatementBase stmt) {
        return new OracleMockResultSetJdbc3(stmt);
    }
    
    public OracleMockPreparedStatement createMockPreparedStatement(MockConnection conn, String sql) {
        return new OracleMockPreparedStatementJdbc3((OracleMockConnection) conn, sql);
    }
}
