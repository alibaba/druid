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
package com.alibaba.druid.test.util;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import oracle.jdbc.OracleParameterMetaData;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.BINARY_FLOAT;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CustomDatum;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

import com.alibaba.druid.mock.MockPreparedStatement;

public class OracleMockPreparedStatement
        extends MockPreparedStatement implements oracle.jdbc.internal.OraclePreparedStatement {

    private int executeBatch = 50;
    private int rowPrefetch;

    public OracleMockPreparedStatement(OracleMockConnection conn, String sql){
        super(conn, sql);
        this.rowPrefetch = conn.getDefaultRowPrefetch();
    }

    @Override
    public OracleParameterMetaData OracleGetParameterMetaData() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void defineParameterType(int arg0, int arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void defineParameterTypeBytes(int arg0, int arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void defineParameterTypeChars(int arg0, int arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public int getExecuteBatch() {
        return executeBatch;
    }

    public ResultSet getReturnResultSet() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public void registerReturnParameter(int arg0, int arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void registerReturnParameter(int arg0, int arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void registerReturnParameter(int arg0, int arg1, String arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public int sendBatch() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setARRAY(int arg0, ARRAY arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setARRAYAtName(String arg0, ARRAY arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setArrayAtName(String arg0, Array arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setAsciiStreamAtName(String arg0, InputStream arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBFILE(int arg0, BFILE arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBFILEAtName(String arg0, BFILE arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBLOB(int arg0, BLOB arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBLOBAtName(String arg0, BLOB arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBfile(int arg0, BFILE arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBfileAtName(String arg0, BFILE arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBigDecimalAtName(String arg0, BigDecimal arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBinaryDouble(int arg0, double arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBinaryDouble(int arg0, BINARY_DOUBLE arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBinaryDoubleAtName(String arg0, double arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBinaryDoubleAtName(String arg0, BINARY_DOUBLE arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBinaryFloat(int arg0, float arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBinaryFloat(int arg0, BINARY_FLOAT arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBinaryFloatAtName(String arg0, float arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBinaryFloatAtName(String arg0, BINARY_FLOAT arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBinaryStreamAtName(String arg0, InputStream arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBlobAtName(String arg0, Blob arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBooleanAtName(String arg0, boolean arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setByteAtName(String arg0, byte arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBytesAtName(String arg0, byte[] arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setBytesForBlob(int arg0, byte[] arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setBytesForBlobAtName(String arg0, byte[] arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCHAR(int arg0, CHAR arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCHARAtName(String arg0, CHAR arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCLOB(int arg0, CLOB arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCLOBAtName(String arg0, CLOB arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setClobAtName(String arg0, Clob arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCursor(int arg0, ResultSet arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCursorAtName(String arg0, ResultSet arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCustomDatum(int arg0, CustomDatum arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCustomDatumAtName(String arg0, CustomDatum arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDATE(int arg0, DATE arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDATEAtName(String arg0, DATE arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDateAtName(String arg0, Date arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDisableStmtCaching(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDoubleAtName(String arg0, double arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setExecuteBatch(int executeBatch) throws SQLException {
        this.executeBatch = executeBatch;
    }

    @Override
    public void setFixedCHAR(int arg0, String arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFixedCHARAtName(String arg0, String arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFloatAtName(String arg0, float arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFormOfUse(int arg0, short arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setINTERVALDS(int arg0, INTERVALDS arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setINTERVALDSAtName(String arg0, INTERVALDS arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setINTERVALYM(int arg0, INTERVALYM arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setINTERVALYMAtName(String arg0, INTERVALYM arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setIntAtName(String arg0, int arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLongAtName(String arg0, long arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNUMBER(int arg0, NUMBER arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNUMBERAtName(String arg0, NUMBER arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNullAtName(String arg0, int arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNullAtName(String arg0, int arg1, String arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setOPAQUE(int arg0, OPAQUE arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setOPAQUEAtName(String arg0, OPAQUE arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setORAData(int arg0, ORAData arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setORADataAtName(String arg0, ORAData arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setObjectAtName(String arg0, Object arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setObjectAtName(String arg0, Object arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setObjectAtName(String arg0, Object arg1, int arg2, int arg3) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setOracleObject(int arg0, Datum arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setOracleObjectAtName(String arg0, Datum arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setPlsqlIndexTable(int arg0, Object arg1, int arg2, int arg3, int arg4, int arg5) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRAW(int arg0, RAW arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRAWAtName(String arg0, RAW arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setREF(int arg0, REF arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setREFAtName(String arg0, REF arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setROWID(int arg0, ROWID arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setROWIDAtName(String arg0, ROWID arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRefAtName(String arg0, Ref arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRefType(int arg0, REF arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRefTypeAtName(String arg0, REF arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSTRUCT(int arg0, STRUCT arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSTRUCTAtName(String arg0, STRUCT arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setShortAtName(String arg0, short arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStringAtName(String arg0, String arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStringForClob(int arg0, String arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStringForClobAtName(String arg0, String arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStructDescriptor(int arg0, StructDescriptor arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStructDescriptorAtName(String arg0, StructDescriptor arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTIMESTAMP(int arg0, TIMESTAMP arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTIMESTAMPAtName(String arg0, TIMESTAMP arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTIMESTAMPLTZ(int arg0, TIMESTAMPLTZ arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTIMESTAMPLTZAtName(String arg0, TIMESTAMPLTZ arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTIMESTAMPTZ(int arg0, TIMESTAMPTZ arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTIMESTAMPTZAtName(String arg0, TIMESTAMPTZ arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTimeAtName(String arg0, Time arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTimestampAtName(String arg0, Timestamp arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setTimestampAtName(String arg0, Timestamp arg1, Calendar arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setURLAtName(String arg0, URL arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUnicodeStreamAtName(String arg0, InputStream arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearDefines() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void closeWithKey(String arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public int creationState() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void defineColumnType(int arg0, int arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void defineColumnType(int arg0, int arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void defineColumnType(int arg0, int arg1, String arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void defineColumnType(int arg0, int arg1, int arg2, short arg3) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void defineColumnTypeBytes(int arg0, int arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void defineColumnTypeChars(int arg0, int arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    public int getLobPrefetchSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    public long getRegisteredQueryId() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public String[] getRegisteredTableNames() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRowPrefetch() {
        return rowPrefetch;
    }

    @Override
    public boolean isNCHAR(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public void setLobPrefetchSize(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRowPrefetch(int rowPrefetch) throws SQLException {
        this.rowPrefetch = rowPrefetch;
    }

    public long getChecksum() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void registerBindChecksumListener(BindChecksumListener bindChecksumListener) throws SQLException {

    }

    @Override
    public boolean getFixedString() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getcacheState() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getserverCursor() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getstatementType() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setFixedString(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void enterExplicitCache() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void enterImplicitCache() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void exitExplicitCacheToActive() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void exitExplicitCacheToClose() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void exitImplicitCacheToActive() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void exitImplicitCacheToClose() throws SQLException {
        // TODO Auto-generated method stub

    }

    public String getOriginalSql() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public void setCharacterStreamAtName(String arg0, Reader arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCheckBindTypes(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setInternalBytes(int arg0, byte[] arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setAsciiStreamAtName(String arg0, InputStream arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setAsciiStreamAtName(String arg0, InputStream arg1, long arg2) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBinaryStreamAtName(String arg0, InputStream arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBinaryStreamAtName(String arg0, InputStream arg1, long arg2) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBlobAtName(String arg0, InputStream arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBlobAtName(String arg0, InputStream arg1, long arg2) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setCharacterStreamAtName(String arg0, Reader arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setCharacterStreamAtName(String arg0, Reader arg1, long arg2) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setClobAtName(String arg0, Reader arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setClobAtName(String arg0, Reader arg1, long arg2) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDateAtName(String arg0, Date arg1, Calendar arg2) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNCharacterStreamAtName(String arg0, Reader arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNCharacterStreamAtName(String arg0, Reader arg1, long arg2) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNClobAtName(String arg0, NClob arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNClobAtName(String arg0, Reader arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNClobAtName(String arg0, Reader arg1, long arg2) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNStringAtName(String arg0, String arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setRowIdAtName(String arg0, RowId arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setSQLXMLAtName(String arg0, SQLXML arg1) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTimeAtName(String arg0, Time arg1, Calendar arg2) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDatabaseChangeRegistration(DatabaseChangeRegistration arg0) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public SqlKind getSqlKind() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSnapshotSCN(long arg0) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public long getQueryId() throws SQLException {
        return 0;
    }

    @Override
    public byte[] getCompileKey() throws SQLException {
        return new byte[0];
    }

    @Override
    public void setACProxy(Object o) {

    }

    @Override
    public Object getACProxy() {
        return null;
    }
}
