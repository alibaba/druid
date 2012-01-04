package com.alibaba.druid.bvt.util;

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
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Properties;
import java.util.TimeZone;

import oracle.jdbc.OracleOCIFailover;
import oracle.jdbc.OracleSavepoint;
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

    @Override
    public Connection _getPC() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void abort() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void applyConnectionAttributes(Properties arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void archive(int arg0, int arg1, String arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancel() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearAllApplicationContext(String arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void close(Properties arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void close(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void commit(EnumSet<CommitOption> arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public ARRAY createARRAY(String arg0, Object arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BINARY_DOUBLE createBINARY_DOUBLE(double arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BINARY_FLOAT createBINARY_FLOAT(float arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DATE createDATE(Date arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DATE createDATE(Time arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DATE createDATE(Timestamp arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DATE createDATE(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DATE createDATE(Date arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DATE createDATE(Time arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DATE createDATE(Timestamp arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public INTERVALDS createINTERVALDS(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public INTERVALYM createINTERVALYM(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NUMBER createNUMBER(boolean arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NUMBER createNUMBER(byte arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NUMBER createNUMBER(short arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NUMBER createNUMBER(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NUMBER createNUMBER(long arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NUMBER createNUMBER(float arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NUMBER createNUMBER(double arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NUMBER createNUMBER(BigDecimal arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NUMBER createNUMBER(BigInteger arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NUMBER createNUMBER(String arg0, int arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Array createOracleArray(String arg0, Object arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMP createTIMESTAMP(Date arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMP createTIMESTAMP(DATE arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMP createTIMESTAMP(Time arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMP createTIMESTAMP(Timestamp arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMP createTIMESTAMP(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPLTZ createTIMESTAMPLTZ(Date arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPLTZ createTIMESTAMPLTZ(Time arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPLTZ createTIMESTAMPLTZ(Timestamp arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPLTZ createTIMESTAMPLTZ(String arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPLTZ createTIMESTAMPLTZ(DATE arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Date arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Time arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(DATE arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Date arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Time arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(String arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
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
    public TypeDescriptor[] getAllTypeDescriptorsInCurrentSchema() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAuthenticationAdaptorName() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getAutoClose() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public CallableStatement getCallWithKey(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Properties getConnectionAttributes() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getConnectionReleasePriority() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getCreateStatementAsRefCursor() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getCurrentSchema() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDataIntegrityAlgorithmName() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DatabaseChangeRegistration getDatabaseChangeRegistration(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getDefaultExecuteBatch() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getDefaultRowPrefetch() {
        return defaultRowPrefetch;
    }

    @Override
    public TimeZone getDefaultTimeZone() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getDescriptor(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEncryptionAlgorithmName() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public short getEndToEndECIDSequenceNumber() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String[] getEndToEndMetrics() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getExplicitCachingEnabled() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getImplicitCachingEnabled() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getIncludeSynonyms() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object getJavaObject(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Properties getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getRemarksReporting() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getRestrictGetTables() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getSQLType(Object arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSessionTimeZone() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSessionTimeZoneOffset() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getStatementCacheSize() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public PreparedStatement getStatementWithKey(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getStmtCacheSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public short getStructAttrCsId() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public TypeDescriptor[] getTypeDescriptorsFromList(String[][] arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TypeDescriptor[] getTypeDescriptorsFromListInCurrentSchema(String[] arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Properties getUnMatchedConnectionAttributes() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getUserName() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getUsingXAFlag() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getXAErrorFlag() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isLogicalConnection() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isProxySession() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isUsable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void openProxySession(int arg0, Properties arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void oracleReleaseSavepoint(OracleSavepoint arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void oracleRollback(OracleSavepoint arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public OracleSavepoint oracleSetSavepoint() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OracleSavepoint oracleSetSavepoint(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OracleConnection physicalConnectionWithin() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int pingDatabase() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int pingDatabase(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public CallableStatement prepareCallWithKey(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PreparedStatement prepareStatementWithKey(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void purgeExplicitCache() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void purgeImplicitCache() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void putDescriptor(String arg0, Object arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public AQNotificationRegistration[] registerAQNotification(String[] arg0, Properties[] arg1, Properties arg2)
                                                                                                                 throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerConnectionCacheCallback(OracleConnectionCacheCallback arg0, Object arg1, int arg2)
                                                                                                          throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public DatabaseChangeRegistration registerDatabaseChangeNotification(Properties arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerSQLType(String arg0, Class arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerSQLType(String arg0, String arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerTAFCallback(OracleOCIFailover arg0, Object arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setApplicationContext(String arg0, String arg1, String arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setAutoClose(boolean arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setConnectionReleasePriority(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCreateStatementAsRefCursor(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDefaultExecuteBatch(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDefaultRowPrefetch(int defaultRowPrefetch) throws SQLException {
        this.defaultRowPrefetch = defaultRowPrefetch;
    }

    @Override
    public void setDefaultTimeZone(TimeZone arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setEndToEndMetrics(String[] arg0, short arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setExplicitCachingEnabled(boolean arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setImplicitCachingEnabled(boolean arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setIncludeSynonyms(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setPlsqlWarnings(String arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRemarksReporting(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRestrictGetTables(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSessionTimeZone(String arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStatementCacheSize(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStmtCacheSize(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStmtCacheSize(int arg0, boolean arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUsingXAFlag(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setWrapper(oracle.jdbc.OracleConnection arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setXAErrorFlag(boolean arg0) {
        // TODO Auto-generated method stub

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
    public void startup(String arg0, int arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterAQNotification(AQNotificationRegistration arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterDatabaseChangeNotification(DatabaseChangeRegistration arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterDatabaseChangeNotification(int arg0) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterDatabaseChangeNotification(long arg0, String arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterDatabaseChangeNotification(int arg0, String arg1, int arg2) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public oracle.jdbc.OracleConnection unwrap() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (isClosed()) {
            throw new MockConnectionClosedException();
        }

        return new OracleMockPreparedStatement(this, sql);
    }
}
