package com.alibaba.druid.pool.vendor;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Implementation of ExceptionSorter for Oracle.
 */
public class OracleExceptionSorter implements ExceptionSorter, Serializable {

    private static final long serialVersionUID = -9146226891418913174L;

    public OracleExceptionSorter(){
    }

    public boolean isExceptionFatal(final SQLException e) {
        final int error_code = Math.abs(e.getErrorCode()); // I can't remember if the errors are negative or positive.

        if ((error_code == 28) // session has been killed
            || (error_code == 600) // Internal oracle error
            || (error_code == 1012) // not logged on
            || (error_code == 1014) // Oracle shutdown in progress
            || (error_code == 1033) // Oracle initialization or shutdown in progress
            || (error_code == 1034) // Oracle not available
            || (error_code == 1035) // ORACLE only available to users with RESTRICTED SESSION privilege
            || (error_code == 1089) // immediate shutdown in progress - no operations are permitted
            || (error_code == 1090) // shutdown in progress - connection is not permitted
            || (error_code == 1092) // ORACLE instance terminated. Disconnection forced
            || (error_code == 1094) // ALTER DATABASE CLOSE in progress. Connections not permitted
            || (error_code == 2396) // exceeded maximum idle time, please connect again
            || (error_code == 3106) // fatal two-task communication protocol error
            || (error_code == 3111) // break received on communication channel
            || (error_code == 3113) // end-of-file on communication channel
            || (error_code == 3114) // not connected to ORACLE
            || (error_code >= 12100 && error_code <= 12299) // TNS issues
        ) {
            return true;
        }

        final String error_text = (e.getMessage()).toUpperCase();

        // Exclude oracle user defined error codes (20000 through 20999) from consideration when looking for
        // certain strings.

        if ((error_code < 20000 || error_code >= 21000)
            && ((error_text.indexOf("SOCKET") > -1) // for control socket error
                || (error_text.indexOf("CONNECTION HAS ALREADY BEEN CLOSED") > -1) || (error_text.indexOf("BROKEN PIPE") > -1))) {
            return true;
        }

        return false;
    }
}
