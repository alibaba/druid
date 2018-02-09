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

import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Properties;

import com.alibaba.druid.pool.ExceptionSorter;

public class DB2ExceptionSorter implements ExceptionSorter {

    @Override
    public boolean isExceptionFatal(SQLException e) {
        if (e instanceof SQLRecoverableException) {
            return true;
        }

        String sqlState = e.getSQLState();
        if (sqlState != null && sqlState.startsWith("08")) { // Connection Exception
            return true;
        }

        int errorCode = e.getErrorCode();
        switch (errorCode) {
            case -512: // STATEMENT REFERENCE TO REMOTE OBJECT IS INVALID
            case -514: // THE CURSOR IS NOT IN A PREPARED STATE
            case -516: // THE DESCRIBE STATEMENT DOES NOT SPECIFY A PREPARED STATEMENT
            case -518: // THE EXECUTE STATEMENT DOES NOT IDENTIFY A VALID PREPARED STATEMENT
            case -525: // THE SQL STATEMENT CANNOT BE EXECUTED BECAUSE IT WAS IN ERROR AT BIND TIME FOR SECTION = sectno
                       // PACKAGE = pkgname CONSISTENCY TOKEN = contoken
            case -909: // THE OBJECT HAS BEEN DELETED OR ALTERED
            case -918: // THE SQL STATEMENT CANNOT BE EXECUTED BECAUSE A CONNECTION HAS BEEN LOST
            case -924: // DB2 CONNECTION INTERNAL ERROR, function-code,return-code,reason-code
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void configFromProperties(Properties properties) {

    }

}
