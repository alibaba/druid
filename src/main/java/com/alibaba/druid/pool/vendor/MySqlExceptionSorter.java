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

import com.alibaba.druid.pool.ExceptionSorter;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Properties;

public class MySqlExceptionSorter implements ExceptionSorter {

    @Override
    public boolean isExceptionFatal(SQLException e) {
        if (e instanceof SQLRecoverableException) {
            return true;
        }

        final String sqlState = e.getSQLState();
        final int errorCode = e.getErrorCode();

        if (sqlState != null && sqlState.startsWith("08")) {
            return true;
        }
        
        switch (errorCode) {
        // Communications Errors
            case 1040: // ER_CON_COUNT_ERROR
            case 1042: // ER_BAD_HOST_ERROR
            case 1043: // ER_HANDSHAKE_ERROR
            case 1047: // ER_UNKNOWN_COM_ERROR
            case 1081: // ER_IPSOCK_ERROR
            case 1129: // ER_HOST_IS_BLOCKED
            case 1130: // ER_HOST_NOT_PRIVILEGED
                // Authentication Errors
            case 1045: // ER_ACCESS_DENIED_ERROR
                // Resource errors
            case 1004: // ER_CANT_CREATE_FILE
            case 1005: // ER_CANT_CREATE_TABLE
            case 1015: // ER_CANT_LOCK
            case 1021: // ER_DISK_FULL
            case 1041: // ER_OUT_OF_RESOURCES
                // Out-of-memory errors
            case 1037: // ER_OUTOFMEMORY
            case 1038: // ER_OUT_OF_SORTMEMORY
                // Access denied
            case 1142: // ER_TABLEACCESS_DENIED_ERROR
            case 1227: // ER_SPECIFIC_ACCESS_DENIED_ERROR

            case 1290: // ER_OPTION_PREVENTS_STATEMENT
                return true;
            default:
                break;
        }
        
        // for oceanbase
        if (errorCode >= -10000 && errorCode <= -9000) {
            return true;
        }
        
        String className = e.getClass().getName();
        if (className.endsWith(".CommunicationsException")) {
            return true;
        }

        String message = e.getMessage();
        if (message != null && message.length() > 0) {
            if (message.startsWith("Streaming result set com.mysql.jdbc.RowDataDynamic")
                    && message.endsWith("is still active. No statements may be issued when any streaming result sets are open and in use on a given connection. Ensure that you have called .close() on any active streaming result sets before attempting more queries.")) {
                return true;
            }
            
            final String errorText = message.toUpperCase();

            if ((errorCode == 0 && (errorText.contains("COMMUNICATIONS LINK FAILURE")) //
            || errorText.contains("COULD NOT CREATE CONNECTION")) //
                || errorText.contains("NO DATASOURCE") //
                || errorText.contains("NO ALIVE DATASOURCE")) {
                return true;
            }
        }

        Throwable cause = e.getCause();
        for (int i = 0; i < 5 && cause != null; ++i) {
            if (cause instanceof SocketTimeoutException) {
                return true;
            }

            className = cause.getClass().getName();
            if (className.endsWith(".CommunicationsException")) {
                return true;
            }

            cause = cause.getCause();
        }
        
        return false;
    }

    @Override
    public void configFromProperties(Properties properties) {

    }

}
