package com.alibaba.druid.support.ibatis;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ibatis.common.util.PaginatedList;
import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.execution.BatchException;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.impl.SqlMapSessionImpl;

@SuppressWarnings("deprecation")
public class SqlMapClientImplWrapper extends SqlMapClientImpl {

    private SqlMapClientImpl raw;

    private static Method    getLocalSqlMapSessionMethod = null;

    public SqlMapClientImplWrapper(SqlMapClientImpl raw){
        super(raw.getDelegate());
        this.raw = raw;
    }

    protected SqlMapSessionWrapper getLocalSqlMapSessionWrapper() {
        try {
            if (getLocalSqlMapSessionMethod == null) {
                getLocalSqlMapSessionMethod = raw.getClass().getDeclaredMethod("getLocalSqlMapSession");
                getLocalSqlMapSessionMethod.setAccessible(true);
            }
            SqlMapSessionImpl sessionImpl = (SqlMapSessionImpl) getLocalSqlMapSessionMethod.invoke(raw);
            IbatisUtils.set(sessionImpl, this);
            return new SqlMapSessionWrapper(raw, sessionImpl);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public SqlMapSession openSession(Connection conn) {
        SqlMapSession session = raw.openSession(conn);
        IbatisUtils.setClientImpl(session, this);
        return new SqlMapSessionWrapper(raw, session);
    }

    public SqlMapSession getSession() {
        SqlMapSession session = raw.getSession();
        IbatisUtils.setClientImpl(session, this);
        return new SqlMapSessionWrapper(raw, session);
    }

    // /////
    public Object insert(String id, Object param) throws SQLException {
        return getLocalSqlMapSessionWrapper().insert(id, param);
    }

    public Object insert(String id) throws SQLException {
        return getLocalSqlMapSessionWrapper().insert(id);
    }

    public int update(String id, Object param) throws SQLException {
        return getLocalSqlMapSessionWrapper().update(id, param);
    }

    public int update(String id) throws SQLException {
        return getLocalSqlMapSessionWrapper().update(id);
    }

    public int delete(String id, Object param) throws SQLException {
        return getLocalSqlMapSessionWrapper().delete(id, param);
    }

    public int delete(String id) throws SQLException {
        return getLocalSqlMapSessionWrapper().delete(id);
    }

    public Object queryForObject(String id, Object paramObject) throws SQLException {
        return getLocalSqlMapSessionWrapper().queryForObject(id, paramObject);
    }

    public Object queryForObject(String id) throws SQLException {
        return getLocalSqlMapSessionWrapper().queryForObject(id);
    }

    public Object queryForObject(String id, Object paramObject, Object resultObject) throws SQLException {
        return getLocalSqlMapSessionWrapper().queryForObject(id, paramObject, resultObject);
    }

    @SuppressWarnings("rawtypes")
    public List queryForList(String id, Object paramObject) throws SQLException {
        return getLocalSqlMapSessionWrapper().queryForList(id, paramObject);
    }

    @SuppressWarnings("rawtypes")
    public List queryForList(String id) throws SQLException {
        return getLocalSqlMapSessionWrapper().queryForList(id);
    }

    @SuppressWarnings("rawtypes")
    public List queryForList(String id, Object paramObject, int skip, int max) throws SQLException {
        return getLocalSqlMapSessionWrapper().queryForList(id, paramObject, skip, max);
    }

    @SuppressWarnings("rawtypes")
    public List queryForList(String id, int skip, int max) throws SQLException {
        return getLocalSqlMapSessionWrapper().queryForList(id, skip, max);
    }

    /**
     * @deprecated All paginated list features have been deprecated
     */
    public PaginatedList queryForPaginatedList(String id, Object paramObject, int pageSize) throws SQLException {
        return getLocalSqlMapSessionWrapper().queryForPaginatedList(id, paramObject, pageSize);
    }

    /**
     * @deprecated All paginated list features have been deprecated
     */
    public PaginatedList queryForPaginatedList(String id, int pageSize) throws SQLException {
        return getLocalSqlMapSessionWrapper().queryForPaginatedList(id, pageSize);
    }

    @SuppressWarnings("rawtypes")
    public Map queryForMap(String id, Object paramObject, String keyProp) throws SQLException {
        return getLocalSqlMapSessionWrapper().queryForMap(id, paramObject, keyProp);
    }

    @SuppressWarnings("rawtypes")
    public Map queryForMap(String id, Object paramObject, String keyProp, String valueProp) throws SQLException {
        return getLocalSqlMapSessionWrapper().queryForMap(id, paramObject, keyProp, valueProp);
    }

    public void queryWithRowHandler(String id, Object paramObject, RowHandler rowHandler) throws SQLException {
        getLocalSqlMapSessionWrapper().queryWithRowHandler(id, paramObject, rowHandler);
    }

    public void queryWithRowHandler(String id, RowHandler rowHandler) throws SQLException {
        getLocalSqlMapSessionWrapper().queryWithRowHandler(id, rowHandler);
    }

    public void startTransaction() throws SQLException {
        getLocalSqlMapSessionWrapper().startTransaction();
    }

    public void startTransaction(int transactionIsolation) throws SQLException {
        getLocalSqlMapSessionWrapper().startTransaction(transactionIsolation);
    }

    public void commitTransaction() throws SQLException {
        getLocalSqlMapSessionWrapper().commitTransaction();
    }

    public void endTransaction() throws SQLException {
        try {
            getLocalSqlMapSessionWrapper().endTransaction();
        } finally {
            getLocalSqlMapSessionWrapper().close();
        }
    }

    public void startBatch() throws SQLException {
        getLocalSqlMapSessionWrapper().startBatch();
    }

    public int executeBatch() throws SQLException {
        return getLocalSqlMapSessionWrapper().executeBatch();
    }

    @SuppressWarnings("rawtypes")
    public List executeBatchDetailed() throws SQLException, BatchException {
        return getLocalSqlMapSessionWrapper().executeBatchDetailed();
    }

    public void setUserConnection(Connection connection) throws SQLException {
        try {
            getLocalSqlMapSessionWrapper().setUserConnection(connection);
        } finally {
            if (connection == null) {
                getLocalSqlMapSessionWrapper().close();
            }
        }
    }
}
