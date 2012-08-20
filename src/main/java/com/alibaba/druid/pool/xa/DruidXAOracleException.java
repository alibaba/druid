package com.alibaba.druid.pool.xa;

import javax.transaction.xa.XAException;

import oracle.jdbc.internal.OracleConnection;

public class DruidXAOracleException extends XAException {

    private static final long serialVersionUID = 1L;
    private int               xaError          = 0;
    private int               primary          = 0;
    private int               secondary        = 0;

    public DruidXAOracleException(){
    }

    public DruidXAOracleException(int error){
        super(errorConvert(error));

        this.xaError = errorConvert(error);
        this.primary = (error & 0xFFFF);
        this.secondary = (error >> 16);
    }

    public static XAException newXAException(OracleConnection conn, int error) {

        DruidXAOracleException xaexc = new DruidXAOracleException(error);

        int xacode = xaexc.getXAError();
        if (xacode == -7) {
            if (conn != null) {
                // TODO conn.setUsable(false);
            }

        }

        return xaexc;
    }

    public int getXAError() {
        return this.xaError;
    }

    public int getOracleError() {
        return this.primary;
    }

    public int getOracleSQLError() {
        return this.secondary;
    }

    public static int errorConvert(int err) {
        return errorConvert(err, -3);
    }

    public static int errorConvert(int err, int defaultErrorCode) {
        switch (err & 0xFFFF) {
            case 24756:
                return -4;
            case 25351:
            case 30006:
                return 4;
            case 24764:
                return 7;
            case 24765:
                return 6;
            case 24766:
                return 5;
            case 24767:
                return 3;
            case 28:
            case 1031:
            case 1033:
            case 1034:
            case 1041:
            case 1089:
            case 1090:
            case 1092:
            case 3113:
            case 3114:
            case 12571:
            case 17002:
            case 17008:
            case 17410:
            case 24796:
            case 25400:
            case 25401:
            case 25402:
            case 25403:
            case 25404:
            case 25405:
            case 25406:
            case 25407:
            case 25408:
            case 25409:
                return -7;
            case 2056:
            case 17448:
            case 24763:
            case 24768:
            case 24769:
            case 24770:
            case 24775:
            case 24776:
                return -6;
            case 2091:
            case 2092:
            case 24761:
                return 100;
        }
        return defaultErrorCode;
    }

}
