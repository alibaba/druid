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
package com.alibaba.druid.pool.vendor;

import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.pool.ExceptionSorter;

public class MySqlExceptionSorter implements ExceptionSorter {

    @Override
    public boolean isExceptionFatal(SQLException e) {
        int loopCount = 20;

        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof SQLException) {
                SQLException sqlException = (SQLException) cause;

                if (isExceptionFatal0(sqlException)) {
                    return true;
                }
            }
            cause = cause.getCause();
            if (--loopCount < 0) {
                break;
            }
        }
        return false;
    }

    private boolean isExceptionFatal0(SQLException e) {
        String sqlState = e.getSQLState();
        final int errorCode = Math.abs(e.getErrorCode());

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
                return true;
        }

        if (StringUtils.isNotBlank(e.getMessage())) {
            final String errorText = e.getMessage().toUpperCase();

            if (errorCode == 0
                && (errorText.indexOf("COMMUNICATIONS LINK FAILURE") > -1 || errorText
                    .indexOf("COULD NOT CREATE CONNECTION") > -1)
                || errorText.indexOf("NO DATASOURCE") > -1
                || errorText.indexOf("NO ALIVE DATASOURCE") > -1) {
                return true;
            }
        }
        return false;
    }

}
