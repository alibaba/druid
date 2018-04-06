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
package com.alibaba.druid.pool.vendor;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Properties;

import com.alibaba.druid.pool.ExceptionSorter;

public class InformixExceptionSorter implements ExceptionSorter, Serializable {

    private static final long serialVersionUID = -5175884111768095263L;

    public boolean isExceptionFatal(SQLException e) {
        if (e instanceof SQLRecoverableException) {
            return true;
        }

        switch (e.getErrorCode()) {
            case -710: // Table has been dropped, altered or renamed JBAS-3120
            case -79716: // System or internal error
            case -79730: // Connection noit established
            case -79734: // INFORMIXSERVER has to be specified
            case -79735: // Can't instantiate protocol
            case -79736: // No connection/statement established yet
            case -79756: // Invalid connection URL
            case -79757: // Invalid subprotocol
            case -79758: // Invalid IP address
            case -79759: // Invalid port nnumber
            case -79760: // Invalid database name
            case -79788: // User name must be specified
            case -79811: // Connection without user/password not supported
            case -79812: // User/password does not match with datasource
            case -79836: // Proxy error: no database connection
            case -79837: // Proxy error: IO error
            case -79879: // Unexpected exception

                return true;
        }

        return false;
    }
    
    public void configFromProperties(Properties properties) {
        
    }

}
