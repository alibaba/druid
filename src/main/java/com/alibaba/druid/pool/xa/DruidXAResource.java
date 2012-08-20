package com.alibaba.druid.pool.xa;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public abstract class DruidXAResource implements XAResource {

    public final static Log                LOG                         = LogFactory.getLog(DruidXAResource.class);

    public static final int                XA_OK                       = 0;
    public static final short              DEFAULT_XA_TIMEOUT          = 60;
    protected boolean                      savedConnectionAutoCommit   = false;
    protected boolean                      savedXAConnectionAutoCommit = false;
    public static final int                TMNOFLAGS                   = 0;
    public static final int                TMNOMIGRATE                 = 2;
    public static final int                TMENDRSCAN                  = 8388608;
    public static final int                TMFAIL                      = 536870912;
    public static final int                TMMIGRATE                   = 1048576;
    public static final int                TMJOIN                      = 2097152;
    public static final int                TMONEPHASE                  = 1073741824;
    public static final int                TMRESUME                    = 134217728;
    public static final int                TMSTARTRSCAN                = 16777216;
    public static final int                TMSUCCESS                   = 67108864;
    public static final int                TMSUSPEND                   = 33554432;
    public static final int                ORATMREADONLY               = 256;
    public static final int                ORATMREADWRITE              = 512;
    public static final int                ORATMSERIALIZABLE           = 1024;
    public static final int                ORAISOLATIONMASK            = 65280;
    public static final int                ORATRANSLOOSE               = 65536;

    protected Connection                   connection                  = null;
    protected DruidXAPooledConnection      xaconnection                = null;
    protected int                          timeout                     = 60;
    protected String                       dblink                      = null;

    protected Hashtable<Xid, XidListEntry> xidHash                     = new Hashtable<Xid, XidListEntry>(50);

    Xid                                    lastActiveXid               = null;

    protected Xid                          activeXid                   = null;

    protected boolean                      canBeMigratablySuspended    = false;

    protected boolean                      isTMRScanStarted            = false;

    protected static final Xid[]           NO_XID                      = new Xid[0];

    public int getTransactionTimeout() throws XAException {
        return this.timeout;
    }

    public boolean setTransactionTimeout(int seconds) throws XAException {

        if (seconds < 0) {
            throw new XAException(-5);
        }

        this.timeout = seconds;

        return true;
    }

    protected void saveAndAlterAutoCommitModeForGlobalTransaction() throws XAException {
        try {
            this.savedConnectionAutoCommit = this.connection.getAutoCommit();
            this.connection.setAutoCommit(false);
            this.savedXAConnectionAutoCommit = this.connection.getAutoCommit();
            this.xaconnection.setAutoCommit(false); // TODO xaconnection
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
        }

    }

    protected final synchronized boolean removeXidFromList(Xid xid) {
        if (isSameXid(this.activeXid, xid)) {
            this.activeXid = null;
        }

        return this.xidHash.remove(xid) != null;
    }

    protected void resumeStacked(Xid xid) throws XAException {
        if (xid != null) {
            start(xid, 134217728);
            this.activeXid = xid;
        }

    }

    protected Xid suspendStacked(Xid xid) throws XAException {

        Xid stackedXid = null;

        if ((this.activeXid != null) && (!isSameXid(this.activeXid, xid))) {
            stackedXid = this.activeXid;

            if (!isXidSuspended(this.activeXid)) {
                end(this.activeXid, 33554432);
                this.lastActiveXid = this.activeXid;
                this.activeXid = null;
            }

        }

        Xid localXid1 = stackedXid;
        return localXid1;
    }

    protected boolean isXidSuspended(Xid xid) throws XAException {

        boolean returnValue = false;
        XidListEntry x = getMatchingXidListEntry(xid);

        if (x != null) {
            returnValue = x.isSuspended;
        }

        return returnValue;
    }

    protected void checkError(int error) throws XAException {
        // TODO
    }

    protected void checkError(int error, int defaultXAError) throws XAException {
        // TODO
    }

    protected void checkError(SQLException sqlex, int defaultXAError) throws XAException {
        // TODO
    }

    final boolean isSameXid(Xid xid1, Xid xid2) {
        return xid1 == xid2;
    }

    protected synchronized void createOrUpdateXid(Xid xid, boolean isSuspended, boolean[] isLocallySuspended) {

        XidListEntry x = getMatchingXidListEntry(xid);

        if (x != null) {
            isLocallySuspended[0] = true;

            x.isSuspended = isSuspended;
        } else {
            x = new XidListEntry(xid, isSuspended);
            this.xidHash.put(xid, x);
        }

        if (isSuspended) {
            this.lastActiveXid = this.activeXid;
            this.activeXid = null;
        } else {
            enterGlobalTxnMode();

            if ((this.lastActiveXid != null) && (isSameXid(xid, this.lastActiveXid))) {
                this.lastActiveXid = null;
            }
            this.activeXid = x.xid;
        }
    }

    protected void restoreAutoCommitModeForGlobalTransaction() throws XAException {
        if (this.savedConnectionAutoCommit) {
            try {
                this.connection.setAutoCommit(this.savedConnectionAutoCommit);
                this.xaconnection.setAutoCommit(this.savedXAConnectionAutoCommit); // TODO xaconn
            } catch (SQLException ignoreException) {

            }

        }
    }

    abstract void enterGlobalTxnMode();

    final synchronized XidListEntry getMatchingXidListEntry(Xid xid) {

        return (XidListEntry) this.xidHash.get(xid);
    }

    protected final synchronized boolean isXidListEmpty() {
        return this.xidHash.isEmpty();
    }

    class XidListEntry {

        Xid     xid;
        boolean isSuspended;

        XidListEntry(Xid x, boolean s){
            this.xid = x;
            this.isSuspended = s;
        }
    }
}
