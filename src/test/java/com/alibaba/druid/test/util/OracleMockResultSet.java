package com.alibaba.druid.test.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.OracleDataFactory;
import oracle.jdbc.OracleResultSet;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CustomDatum;
import oracle.sql.CustomDatumFactory;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

import com.alibaba.druid.mock.MockResultSet;


public class OracleMockResultSet extends MockResultSet implements OracleResultSet {

    public OracleMockResultSet(Statement statement){
        super(statement);
    }

    @Override
    public ARRAY getARRAY(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ARRAY getARRAY(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BFILE getBFILE(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BFILE getBFILE(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BLOB getBLOB(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BLOB getBLOB(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BFILE getBfile(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BFILE getBfile(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CHAR getCHAR(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CHAR getCHAR(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CLOB getCLOB(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CLOB getCLOB(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultSet getCursor(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultSet getCursor(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CustomDatum getCustomDatum(int arg0, CustomDatumFactory arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CustomDatum getCustomDatum(String arg0, CustomDatumFactory arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DATE getDATE(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DATE getDATE(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public INTERVALDS getINTERVALDS(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public INTERVALDS getINTERVALDS(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public INTERVALYM getINTERVALYM(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public INTERVALYM getINTERVALYM(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NUMBER getNUMBER(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NUMBER getNUMBER(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OPAQUE getOPAQUE(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OPAQUE getOPAQUE(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ORAData getORAData(int arg0, ORADataFactory arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getObject(int i, OracleDataFactory oracleDataFactory) throws SQLException {
        return null;
    }

    @Override
    public Object getObject(String s, OracleDataFactory oracleDataFactory) throws SQLException {
        return null;
    }

    @Override
    public ORAData getORAData(String arg0, ORADataFactory arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Datum getOracleObject(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Datum getOracleObject(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RAW getRAW(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RAW getRAW(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public REF getREF(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public REF getREF(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ROWID getROWID(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ROWID getROWID(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public STRUCT getSTRUCT(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public STRUCT getSTRUCT(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMP getTIMESTAMP(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMP getTIMESTAMP(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPLTZ getTIMESTAMPLTZ(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPLTZ getTIMESTAMPLTZ(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPTZ getTIMESTAMPTZ(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPTZ getTIMESTAMPTZ(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateARRAY(int arg0, ARRAY arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateARRAY(String arg0, ARRAY arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBFILE(int arg0, BFILE arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBFILE(String arg0, BFILE arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBLOB(int arg0, BLOB arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBLOB(String arg0, BLOB arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBfile(int arg0, BFILE arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBfile(String arg0, BFILE arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCHAR(int arg0, CHAR arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCHAR(String arg0, CHAR arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCLOB(int arg0, CLOB arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCLOB(String arg0, CLOB arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCustomDatum(int arg0, CustomDatum arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCustomDatum(String arg0, CustomDatum arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateDATE(int arg0, DATE arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateDATE(String arg0, DATE arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateINTERVALDS(int arg0, INTERVALDS arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateINTERVALYM(int arg0, INTERVALYM arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNUMBER(int arg0, NUMBER arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNUMBER(String arg0, NUMBER arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateORAData(int arg0, ORAData arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateORAData(String arg0, ORAData arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateOracleObject(int arg0, Datum arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateOracleObject(String arg0, Datum arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateRAW(int arg0, RAW arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateRAW(String arg0, RAW arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateREF(int arg0, REF arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateREF(String arg0, REF arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateROWID(int arg0, ROWID arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateROWID(String arg0, ROWID arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateSTRUCT(int arg0, STRUCT arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateSTRUCT(String arg0, STRUCT arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isFromResultSetCache() throws SQLException {
        return false;
    }

    @Override
    public byte[] getCompileKey() throws SQLException {
        return new byte[0];
    }

    @Override
    public byte[] getRuntimeKey() throws SQLException {
        return new byte[0];
    }

    @Override
    public void updateTIMESTAMP(int arg0, TIMESTAMP arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateTIMESTAMPLTZ(int arg0, TIMESTAMPLTZ arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateTIMESTAMPTZ(int arg0, TIMESTAMPTZ arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public AuthorizationIndicator getAuthorizationIndicator(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AuthorizationIndicator getAuthorizationIndicator(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateINTERVALDS(String arg0, INTERVALDS arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateINTERVALYM(String arg0, INTERVALYM arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateTIMESTAMP(String arg0, TIMESTAMP arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateTIMESTAMPLTZ(String arg0, TIMESTAMPLTZ arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateTIMESTAMPTZ(String arg0, TIMESTAMPTZ arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

}
