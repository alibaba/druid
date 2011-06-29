package com.alibaba.druid.pool.vendor;

import java.sql.SQLException;

public class MySqlExceptionSorter implements ExceptionSorter {

    @Override
    public boolean isExceptionFatal(SQLException e) {
        String state = e.getSQLState();
        if (state != null && state.startsWith("08")) { // per Mark Matthews at MySQL
            return true;
        }

        final int errorCode = e.getErrorCode();
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
                
            default:
                break;
        }

        return false;
    }

}
