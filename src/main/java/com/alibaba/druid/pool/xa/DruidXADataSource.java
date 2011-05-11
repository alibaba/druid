/*
 * Copyright 2011 Alibaba Group. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.alibaba.druid.pool.xa;

import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidXADataSource extends DruidDataSource implements XADataSource {

    private static final long serialVersionUID = 1L;

    @Override
    public XAConnection getXAConnection() throws SQLException {
        return null;
    }

    @Override
    public XAConnection getXAConnection(String user, String password) throws SQLException {
        return null;
    }

}
