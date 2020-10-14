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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.Executor;

import oracle.jdbc.*;
import oracle.jdbc.aq.AQDequeueOptions;
import oracle.jdbc.aq.AQEnqueueOptions;
import oracle.jdbc.aq.AQMessage;
import oracle.jdbc.aq.AQNotificationRegistration;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.pool.OracleConnectionCacheCallback;
import oracle.sql.ARRAY;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.BINARY_FLOAT;
import oracle.sql.DATE;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;
import oracle.sql.TypeDescriptor;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockConnectionClosedException;
import com.alibaba.druid.mock.MockDriver;

public class OracleMockConnection extends MockConnection implements oracle.jdbc.OracleConnection {

    private int defaultRowPrefetch = 10;

    public OracleMockConnection(){
        super();
    }

    public OracleMockConnection(MockDriver driver, String url, Properties connectProperties){
        super(driver, url, connectProperties);

        String val = (String) connectProperties.get("defaultRowPrefetch");
        if (val != null) {
            defaultRowPrefetch = Integer.parseInt(val);
        }
    }

    public Connection _getPC() {
        // TODO Auto-generated method stub
        return null;
    }

    public void abort() throws SQLException {
        // TODO Auto-generated method stub

    }

    public void applyConnectionAttributes(Properties arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void archive(int arg0, int arg1, String arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void cancel() throws SQLException {
        // TODO Auto-generated method stub

    }

    public void clearAllApplicationContext(String arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void addLogicalTransactionIdEventListener(LogicalTransactionIdEventListener logicalTransactionIdEventListener) throws SQLException {

    }

    @Override
    public void addLogicalTransactionIdEventListener(LogicalTransactionIdEventListener logicalTransactionIdEventListener, Executor executor) throws SQLException {

    }

    @Override
    public void removeLogicalTransactionIdEventListener(LogicalTransactionIdEventListener logicalTransactionIdEventListener) throws SQLException {

    }

    @Override
    public LogicalTransactionId getLogicalTransactionId() throws SQLException {
        return null;
    }

    @Override
    public boolean isDRCPEnabled() throws SQLException {
        return false;
    }

    @Override
    public boolean isDRCPMultitagEnabled() throws SQLException {
        return false;
    }

    @Override
    public String getDRCPReturnTag() throws SQLException {
        return null;
    }

    @Override
    public String getDRCPPLSQLCallbackName() throws SQLException {
        return null;
    }

    @Override
    public boolean attachServerConnection() throws SQLException {
        return false;
    }

    @Override
    public void detachServerConnection(String s) throws SQLException {

    }

    @Override
    public boolean needToPurgeStatementCache() throws SQLException {
        return false;
    }

    @Override
    public DRCPState getDRCPState() throws SQLException {
        return null;
    }

    @Override
    public void beginRequest() throws SQLException {

    }

    @Override
    public void endRequest() throws SQLException {

    }

    @Override
    public boolean setShardingKeyIfValid(OracleShardingKey oracleShardingKey, OracleShardingKey oracleShardingKey1, int i) throws SQLException {
        return false;
    }

    @Override
    public void setShardingKey(OracleShardingKey oracleShardingKey, OracleShardingKey oracleShardingKey1) throws SQLException {

    }

    @Override
    public boolean setShardingKeyIfValid(OracleShardingKey oracleShardingKey, int i) throws SQLException {
        return false;
    }

    @Override
    public void setShardingKey(OracleShardingKey oracleShardingKey) throws SQLException {

    }

    @Override
    public boolean isValid(ConnectionValidation connectionValidation, int i) throws SQLException {
        return false;
    }

    @Override
    public String getEncryptionProviderName() throws SQLException {
        return null;
    }

    @Override
    public String getChecksumProviderName() throws SQLException {
        return null;
    }

    public void close(Properties arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void close(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public ARRAY createARRAY(String arg0, Object arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public BINARY_DOUBLE createBINARY_DOUBLE(double arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public BINARY_FLOAT createBINARY_FLOAT(float arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public DATE createDATE(Date arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public DATE createDATE(Time arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public DATE createDATE(Timestamp arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public DATE createDATE(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public DATE createDATE(Date arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public DATE createDATE(Time arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public DATE createDATE(Timestamp arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public INTERVALDS createINTERVALDS(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public INTERVALYM createINTERVALYM(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    // TODO Auto-generated method stub
    public NUMBER createNUMBER(boolean arg0) throws SQLException {
        return null;
    }

    public NUMBER createNUMBER(byte arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public NUMBER createNUMBER(short arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public NUMBER createNUMBER(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public NUMBER createNUMBER(long arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public NUMBER createNUMBER(float arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public NUMBER createNUMBER(double arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public NUMBER createNUMBER(BigDecimal arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public NUMBER createNUMBER(BigInteger arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public NUMBER createNUMBER(String arg0, int arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public Array createOracleArray(String arg0, Object arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMP createTIMESTAMP(Date arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMP createTIMESTAMP(DATE arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMP createTIMESTAMP(Time arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMP createTIMESTAMP(Timestamp arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMP createTIMESTAMP(Timestamp timestamp, Calendar calendar) throws SQLException {
        return null;
    }

    public TIMESTAMP createTIMESTAMP(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMPLTZ createTIMESTAMPLTZ(Date arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMPLTZ createTIMESTAMPLTZ(Time arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMPLTZ createTIMESTAMPLTZ(Timestamp arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMPLTZ createTIMESTAMPLTZ(String arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMPLTZ createTIMESTAMPLTZ(DATE arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMPTZ createTIMESTAMPTZ(Date arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMPTZ createTIMESTAMPTZ(Time arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMPTZ createTIMESTAMPTZ(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMPTZ createTIMESTAMPTZ(DATE arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMPTZ createTIMESTAMPTZ(Date arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMPTZ createTIMESTAMPTZ(Time arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp timestamp, ZoneId zoneId) throws SQLException {
        return null;
    }

    public TIMESTAMPTZ createTIMESTAMPTZ(String arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TypeDescriptor[] getAllTypeDescriptorsInCurrentSchema() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getAuthenticationAdaptorName() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean getAutoClose() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public CallableStatement getCallWithKey(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public Properties getConnectionAttributes() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public int getConnectionReleasePriority() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean getCreateStatementAsRefCursor() {
        // TODO Auto-generated method stub
        return false;
    }

    public String getCurrentSchema() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDataIntegrityAlgorithmName() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public int getDefaultExecuteBatch() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getDefaultRowPrefetch() {
        return defaultRowPrefetch;
    }

    public TimeZone getDefaultTimeZone() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getDescriptor(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getEncryptionAlgorithmName() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public short getEndToEndECIDSequenceNumber() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public String[] getEndToEndMetrics() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean getExplicitCachingEnabled() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean getImplicitCachingEnabled() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean getIncludeSynonyms() {
        // TODO Auto-generated method stub
        return false;
    }

    public Object getJavaObject(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public Properties getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean getRemarksReporting() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean getRestrictGetTables() {
        // TODO Auto-generated method stub
        return false;
    }

    public String getSQLType(Object arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSessionTimeZone() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSessionTimeZoneOffset() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public int getStatementCacheSize() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public PreparedStatement getStatementWithKey(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public int getStmtCacheSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    public short getStructAttrCsId() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public TypeDescriptor[] getTypeDescriptorsFromList(String[][] arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public TypeDescriptor[] getTypeDescriptorsFromListInCurrentSchema(String[] arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public Properties getUnMatchedConnectionAttributes() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserName() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean getUsingXAFlag() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean getXAErrorFlag() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isLogicalConnection() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isProxySession() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isUsable() {
        // TODO Auto-generated method stub
        return false;
    }

    public void openProxySession(int arg0, Properties arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void oracleReleaseSavepoint(OracleSavepoint arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void oracleRollback(OracleSavepoint arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public OracleSavepoint oracleSetSavepoint() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public OracleSavepoint oracleSetSavepoint(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public OracleConnection physicalConnectionWithin() {
        // TODO Auto-generated method stub
        return null;
    }

    public int pingDatabase() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int pingDatabase(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public CallableStatement prepareCallWithKey(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public PreparedStatement prepareStatementWithKey(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public void purgeExplicitCache() throws SQLException {
        // TODO Auto-generated method stub

    }

    public void purgeImplicitCache() throws SQLException {
        // TODO Auto-generated method stub

    }

    public void putDescriptor(String arg0, Object arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void registerConnectionCacheCallback(OracleConnectionCacheCallback arg0, Object arg1, int arg2)
                                                                                                          throws SQLException {
        // TODO Auto-generated method stub

    }

    public void registerSQLType(String arg0, Class arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void registerSQLType(String arg0, String arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void registerTAFCallback(OracleOCIFailover arg0, Object arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setApplicationContext(String arg0, String arg1, String arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setAutoClose(boolean arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setConnectionReleasePriority(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setCreateStatementAsRefCursor(boolean arg0) {
        // TODO Auto-generated method stub

    }

    public void setDefaultExecuteBatch(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setDefaultRowPrefetch(int defaultRowPrefetch) throws SQLException {
        this.defaultRowPrefetch = defaultRowPrefetch;
    }

    public void setDefaultTimeZone(TimeZone arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setEndToEndMetrics(String[] arg0, short arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setExplicitCachingEnabled(boolean arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setImplicitCachingEnabled(boolean arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setIncludeSynonyms(boolean arg0) {
        // TODO Auto-generated method stub

    }

    public void setPlsqlWarnings(String arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setRemarksReporting(boolean arg0) {
        // TODO Auto-generated method stub

    }

    public void setRestrictGetTables(boolean arg0) {
        // TODO Auto-generated method stub

    }

    public void setSessionTimeZone(String arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setStatementCacheSize(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setStmtCacheSize(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setStmtCacheSize(int arg0, boolean arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setUsingXAFlag(boolean arg0) {
        // TODO Auto-generated method stub

    }

    public void setWrapper(oracle.jdbc.OracleConnection arg0) {
        // TODO Auto-generated method stub

    }

    public void setXAErrorFlag(boolean arg0) {
        // TODO Auto-generated method stub

    }

    public void startup(String arg0, int arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void unregisterDatabaseChangeNotification(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void unregisterDatabaseChangeNotification(long arg0, String arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void unregisterDatabaseChangeNotification(int arg0, String arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    public oracle.jdbc.OracleConnection unwrap() {
        // TODO Auto-generated method stub
        return null;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (isClosed()) {
            throw new MockConnectionClosedException();
        }

        return this.getDriver().createMockPreparedStatement(this, sql);
    }

    public void shutdown(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void commit(EnumSet<CommitOption> arg0) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public AQMessage dequeue(String arg0, AQDequeueOptions arg1, byte[] arg2) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AQMessage dequeue(String arg0, AQDequeueOptions arg1, String arg2) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void enqueue(String arg0, AQEnqueueOptions arg1, AQMessage arg2) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DatabaseChangeRegistration getDatabaseChangeRegistration(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AQNotificationRegistration[] registerAQNotification(String[] arg0, Properties[] arg1, Properties arg2)
                                                                                                                 throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DatabaseChangeRegistration registerDatabaseChangeNotification(Properties arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void shutdown(DatabaseShutdownMode arg0) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startup(DatabaseStartupMode arg0) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startup(DatabaseStartupMode databaseStartupMode, String s) throws SQLException {

    }

    @Override
    public void unregisterAQNotification(AQNotificationRegistration arg0) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void unregisterDatabaseChangeNotification(DatabaseChangeRegistration arg0) throws SQLException {
        // TODO Auto-generated method stub
        
    }
}
