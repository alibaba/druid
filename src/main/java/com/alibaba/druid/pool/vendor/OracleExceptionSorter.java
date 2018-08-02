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
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Implementation of ExceptionSorter for Oracle.
 */
public class OracleExceptionSorter implements ExceptionSorter, Serializable {

    private final static Log  LOG              = LogFactory.getLog(OracleExceptionSorter.class);

    private static final long serialVersionUID = -9146226891418913174L;

    private Set<Integer>      fatalErrorCodes  = new HashSet<Integer>();

    public OracleExceptionSorter(){
        configFromProperties(System.getProperties());
    }
    
    public void configFromProperties(Properties properties) {
        String property = properties.getProperty("druid.oracle.fatalErrorCodes");
        if (property != null) {
            String[] items = property.split("\\,");
            for (String item : items) {
                if (item != null && item.length() > 0) {
                    try {
                        int code = Integer.parseInt(item);
                        fatalErrorCodes.add(code);
                    } catch (NumberFormatException e) {
                        LOG.error("parse druid.oracle.fatalErrorCodes error", e);
                    }
                }
            }
        }
    }

    public Set<Integer> getFatalErrorCodes() {
        return fatalErrorCodes;
    }

    public void setFatalErrorCodes(Set<Integer> fatalErrorCodes) {
        this.fatalErrorCodes = fatalErrorCodes;
    }

    public boolean isExceptionFatal(final SQLException e) {
        if (e instanceof SQLRecoverableException) {
            return true;
        }

        final int error_code = Math.abs(e.getErrorCode()); // I can't remember if the errors are negative or positive.

        switch (error_code) {
            case 28: // your session has been killed
            case 600: // Internal oracle error
            case 1012: // not logged on
            case 1014: // Oracle shutdown in progress
            case 1033: // Oracle initialization or shutdown in progress
            case 1034: // Oracle not available
            case 1035: // ORACLE only available to users with RESTRICTED SESSION privilege
            case 1089: // immediate shutdown in progress - no operations are permitted
            case 1090: // shutdown in progress - connection is not permitted
            case 1092: // ORACLE instance terminated. Disconnection forced
            case 1094: // ALTER DATABASE CLOSE in progress. Connections not permitted
            case 2396: // exceeded maximum idle time, please connect again
            case 3106: // fatal two-task communication protocol error
            case 3111: // break received on communication channel
            case 3113: // end-of-file on communication channel
            case 3114: // not connected to ORACLE

            case 3134: // Connections to this server version are no longer supported.
            case 3135: // connection lost contact
            case 3136: // inbound connection timed out
            case 3138: // Connection terminated due to security policy violation
            case 3142: // Connection was lost for the specified session and serial number. This is either due to session
                       // being killed or network problems.
            case 3143: // Connection was lost for the specified process ID and thread ID. This is either due to session
                       // being killed or network problems.
            case 3144: // Connection was lost for the specified process ID. This is either due to session being killed
                       // or network problems.
            case 3145: // I/O streaming direction error
            case 3149: // Invalid Oracle error code, Cause: An invalid Oracle error code was received by the server.

            case 6801: // TLI Driver: listen for SPX server reconnect failed
            case 6802: // TLI Driver: could not open the /etc/netware/yellowpages file
            case 6805: // TLI Driver: could not send datagram SAP packet for SPX
            case 9918: // Unable to get user privileges from SQL*Net
            case 9920: // Unable to get sensitivity label from connection
            case 9921: // Unable to get information label from connection

                // TTC(Two-Task Common) ERROR CODE
            case 17001: // Internal Error
            case 17002: // Io exception
            case 17008: // Closed Connection
            case 17009: // Closed Statement
            case 17024: // No data read
            case 17089: // internal error
            case 17409: // invalid buffer length
            case 17401: // Protocol violation
            case 17410: // No more data to read from socket
            case 17416: // FATAl
            case 17438: // Internal - Unexpected value
            case 17442: // Refcursor value is invalid

            case 25407: // connection terminated
            case 25408: // can not safely replay call
            case 25409: // failover happened during the network operation,cannot continue
            case 25425: // connection lost during rollback
            case 29276: // transfer timeout
            case 30676: // socket read or write failed
                return true;
            default:
                if (error_code >= 12100 && error_code <= 12299) { // TNS issues
                    return true;
                }
                break;
        }

        final String error_text = (e.getMessage()).toUpperCase();

        // Exclude oracle user defined error codes (20000 through 20999) from consideration when looking for
        // certain strings.

        if ((error_code < 20000 || error_code >= 21000)) {
            if ((error_text.contains("SOCKET")) // for control socket error
                || (error_text.contains("套接字")) // for control socket error
                || (error_text.contains("CONNECTION HAS ALREADY BEEN CLOSED")) //
                || (error_text.contains("BROKEN PIPE")) //
                || (error_text.contains("管道已结束")) //
            ) {
                return true;
            }

        }

        return fatalErrorCodes.contains(error_code);
    }
}
