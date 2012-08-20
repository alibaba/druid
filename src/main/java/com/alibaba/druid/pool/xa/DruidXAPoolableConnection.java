package com.alibaba.druid.pool.xa;

import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class DruidXAPoolableConnection extends DruidPooledConnection implements XAResource {

    private final static Log     LOG       = LogFactory.getLog(DruidXAPoolableConnection.class);

    protected final XAConnection xaConnection;

    protected final XAResource   xaResource;

    protected Xid                currentXid;

    private Lock                 stateLock = new ReentrantLock();

    public DruidXAPoolableConnection(DruidConnectionHolder holder, XAConnection xaConnection) throws SQLException{
        super(holder);

        this.xaConnection = xaConnection;
        this.xaResource = xaConnection.getXAResource();
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        xaResource.commit(xid, onePhase);
    }

    @Override
    public void end(Xid xid, int arg1) throws XAException {
        // TODO Auto-generated method stub

    }

    @Override
    public void forget(Xid xid) throws XAException {
        xaResource.forget(xid);
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return xaResource.getTransactionTimeout();
    }

    @Override
    public boolean isSameRM(XAResource other) throws XAException {
        return xaResource.isSameRM(other);
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        return xaResource.prepare(xid);
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        return xaResource.recover(flag);
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        xaResource.rollback(xid);
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        return xaResource.setTransactionTimeout(seconds);
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        if (holder == null) {
            throw new XAException("connection is closed");
        }

        try {
            xaResource.start(xid, flags);
        } catch (XAException e) {
            // JBAS-3336 Connections that fail in enlistment should not be returned
            // to the pool
            if (isFailedXA(e.errorCode)) {
                LOG.error("Start transaction failed for " + this);
            }

            throw e;
        }

        stateLock.lock();
        try {
            currentXid = xid;
        } finally {
            stateLock.unlock();
        }
    }

    private boolean isFailedXA(int errorCode) {
        return (errorCode == XAException.XAER_RMERR || errorCode == XAException.XAER_RMFAIL);
    }
}
