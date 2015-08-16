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
package com.alibaba.druid.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.XAConnection;

import com.mysql.jdbc.Util;
import com.mysql.jdbc.jdbc2.optional.MysqlXAConnection;
import com.mysql.jdbc.jdbc2.optional.SuspendableXAConnection;

public class MySqlUtils {

    public static XAConnection createXAConnection(Connection physicalConn) throws SQLException {
    	com.mysql.jdbc.ConnectionImpl mysqlConn = (com.mysql.jdbc.ConnectionImpl)physicalConn;
    	if(mysqlConn.getPinGlobalTxToPhysicalConnection()) {

    		if (!Util.isJdbc4()) {
    			return new SuspendableXAConnection(mysqlConn);
    		}

    		return new com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection(mysqlConn);
    	
    	}
        return new MysqlXAConnection(mysqlConn, false);
    }
}
