package com.alibaba.druid.pool.xa;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import oracle.jdbc.internal.OracleConnection;

import com.alibaba.druid.util.OracleUtils;

public class DruidXAOracleResource extends DruidXAResource {

    private static String xa_start_post_816                    = "begin ? := JAVA_XA.xa_start_new(?,?,?,?,?); end;";
    private static String xa_forget_post_816                   = "begin ? := JAVA_XA.xa_forget_new (?,?,?); end;";
    private static String xa_prepare_post_816                  = "begin ? := JAVA_XA.xa_prepare_new (?,?,?); end;";
    private static String xa_rollback_post_816                 = "begin ? := JAVA_XA.xa_rollback_new (?,?,?); end;";
    private static String xa_commit_post_816                   = "begin ? := JAVA_XA.xa_commit_new (?,?,?,?); end;";
    private static String xa_end_post_816                      = "begin ? := JAVA_XA.xa_end_new(?,?,?,?); end;";

    private String        synchronizeBeforeRecoverNewCall      = "BEGIN sys.dbms_xa.dist_txn_sync \n; END;";

    private String        synchronizeBeforeRecoverOldCall      = "BEGIN sys.dbms_system.dist_txn_sync(0) \n; END;";

    private String        recoverySqlRows                      = "SELECT formatid, globalid, branchid FROM SYS.DBA_PENDING_TRANSACTIONS";

    private int           version;
    private boolean       needStackingForCommitRollbackPrepare = false;
    boolean               isTransLoose                         = false;

    public DruidXAOracleResource(DruidXAPooledConnection conn) throws SQLException, XAException{
        this.version = OracleUtils.getVersionNumber(conn);

        this.needStackingForCommitRollbackPrepare = (this.version < 9000);

        if (this.version < 8170) {
            throw new XAException(XAException.XAER_PROTO);
        }
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {

        synchronized (this.connection) {
            if (xid == null) {
                throw new XAException(-5);
            }

            Xid stackedXid = null;
            if (this.needStackingForCommitRollbackPrepare) {
                stackedXid = super.suspendStacked(xid);
            } else {
                removeXidFromList(xid);

                if (this.activeXid == null) {
                    exitGlobalTxnMode();
                }
            }
            try {
                try {
                    doCommit(xid, onePhase);
                } catch (SQLException sqle) {
                    checkError(sqle, -3);
                }
            } catch (XAException xae) {
                if (xae.errorCode == -7) {
                    try {
                        this.connection.close();
                    } catch (SQLException ea) {
                    }

                } else if (this.needStackingForCommitRollbackPrepare) {
                    super.resumeStacked(stackedXid);
                }
                throw xae;
            }

            if (this.needStackingForCommitRollbackPrepare) {
                super.resumeStacked(stackedXid);
            }
        }
    }

    protected void doCommit(Xid xid, boolean onePhase) throws XAException, SQLException {

        CallableStatement cstmt = null;
        try {
            cstmt = this.connection.prepareCall(xa_commit_post_816);

            cstmt.registerOutParameter(1, 2);
            cstmt.setInt(2, xid.getFormatId());
            cstmt.setBytes(3, xid.getGlobalTransactionId());
            cstmt.setBytes(4, xid.getBranchQualifier());
            cstmt.setInt(5, onePhase ? 1 : 0);

            cstmt.execute();

            int returnVal = cstmt.getInt(1);
            checkError(returnVal, -7);
        } catch (SQLException s) {
            int returnVal = s.getErrorCode();

            if (returnVal == 0) {
                throw new XAException(-6);
            }

            throw s;
        } finally {
            try {
                if (cstmt != null) cstmt.close();
            } catch (SQLException s) {
            }
            cstmt = null;
        }
    }

    @Override
    public void end(Xid xid, int flag) throws XAException {

        synchronized (this.connection) {
            int returnVal = -1;
            boolean isTMSUCCESS = false;
            boolean isTMFAIL = false;
            try {
                if (xid == null) {
                    throw new XAException(-5);
                }

                int validFlags = 638582786;
                if ((flag & validFlags) != flag) {
                    throw new XAException(-5);
                }

                Xid stackedXid = null;
                isTMSUCCESS = (flag & 0x4000000) != 0;
                isTMFAIL = (flag & 0x20000000) != 0;

                if ((isTMSUCCESS) || (isTMFAIL)) {
                    stackedXid = super.suspendStacked(xid);
                }
                try {
                    boolean isLocallySuspended = false;
                    boolean i = false;
                    if ((isTMSUCCESS) || (isTMFAIL)) {
                        isLocallySuspended = isXidSuspended(xid);

                        if (isLocallySuspended) {
                            super.resumeStacked(xid);
                        }

                        removeXidFromList(xid);
                    } else if (flag == 33554432) {
                        boolean[] flags = { false };
                        super.createOrUpdateXid(xid, true, flags);

                        i = flags[0];
                    }

                    returnVal = doEnd(xid, flag, i);
                } catch (SQLException sqle) {
                    checkError(sqle, -3);
                }

                if (stackedXid != null) {
                    super.resumeStacked(stackedXid);
                } else if (isXidListEmpty()) {
                    exitGlobalTxnMode();
                    this.activeXid = null;
                }

                checkError(returnVal);

                if (((isTMSUCCESS) && (flag != 67108864)) || ((isTMFAIL) && (flag != 536870912))) {
                    throw new XAException(-5);
                }

            } finally {
                restoreAutoCommitModeForGlobalTransaction();
            }

        }
    }

    protected int doEnd(Xid xid, int flag, boolean isLocallySuspended) throws XAException, SQLException {

        CallableStatement cstmt = null;
        int returnVal = -1;
        try {
            cstmt = this.connection.prepareCall(xa_end_post_816);

            cstmt.registerOutParameter(1, 2);
            cstmt.setInt(2, xid.getFormatId());
            cstmt.setBytes(3, xid.getGlobalTransactionId());
            cstmt.setBytes(4, xid.getBranchQualifier());
            cstmt.setInt(5, flag);
            cstmt.execute();

            returnVal = cstmt.getInt(1);
        } catch (SQLException s) {
            returnVal = s.getErrorCode();
            throw s;
        } finally {
            try {
                if (cstmt != null) cstmt.close();
            } catch (SQLException s) {
            }
            cstmt = null;
        }

        return returnVal;
    }

    public void forget(Xid xid) throws XAException {
        synchronized (this.connection) {
            int returnVal = 0;

            if (xid == null) {
                throw new XAException(-5);
            }

            removeXidFromList(xid);
            try {
                returnVal = doForget(xid);
            } catch (SQLException sqle) {
                checkError(sqle, -3);
            }

            checkError(returnVal);
        }
    }

    protected int doForget(Xid xid) throws XAException, SQLException {

        int returnVal = 0;
        CallableStatement cstmt = null;
        try {
            cstmt = this.connection.prepareCall(xa_forget_post_816);

            cstmt.registerOutParameter(1, 2);
            cstmt.setInt(2, xid.getFormatId());
            cstmt.setBytes(3, xid.getGlobalTransactionId());
            cstmt.setBytes(4, xid.getBranchQualifier());

            cstmt.execute();

            returnVal = cstmt.getInt(1);
        } catch (SQLException s) {
            returnVal = s.getErrorCode();

            if (returnVal == 0) {
                throw new XAException(-6);
            }

            throw s;
        } finally {
            try {
                if (cstmt != null) cstmt.close();
            } catch (SQLException s) {
            }
            cstmt = null;
        }

        return returnVal;
    }

    @Override
    public boolean isSameRM(XAResource xares) throws XAException {
        Connection conn1 = null;
        if ((xares instanceof DruidXAOracleResource)) {
            conn1 = ((DruidXAOracleResource) xares).getConnection();
        } else {
            return false;
        }
        try {
            if ((this.connection == null) || (((OracleConnection) this.connection).isClosed())) {
                return false;
            }
            String l_url = ((OracleConnection) this.connection).getURL();
            String l_prt = ((OracleConnection) this.connection).getProtocolType();

            if (conn1 != null) {
                return (conn1.equals(this.connection)) || (((OracleConnection) conn1).getURL().equals(l_url))
                       || ((((OracleConnection) conn1).getProtocolType().equals(l_prt)) && (l_prt.equals("kprb")));
            }
        } catch (SQLException sqe) {
            throw new XAException(-3);
        }

        return false;
    }

    @Override
    public int prepare(Xid xid) throws XAException {

        synchronized (this.connection) {
            int returnVal = 0;

            if (xid == null) {
                throw new XAException(-5);
            }

            Xid stackedXid = null;
            if (this.needStackingForCommitRollbackPrepare) {
                stackedXid = super.suspendStacked(xid);
            }
            try {
                try {
                    returnVal = doPrepare(xid);
                    if ((returnVal != 0) && (returnVal != 3)) {

                        int x_e = DruidXAOracleException.errorConvert(returnVal);

                        if ((x_e != 0) && (x_e != 3)) {
                            XAException ex = DruidXAOracleException.newXAException(getConnectionDuringExceptionHandling(),
                                                                                   returnVal);
                            ex.fillInStackTrace();
                            throw ex;
                        }

                        returnVal = x_e;
                    }

                } catch (SQLException sqle) {
                    checkError(sqle, -3);
                }
            } catch (XAException xaex) {
                if (xaex.errorCode == -7) {
                    try {
                        this.connection.close();
                    } catch (SQLException ea) {
                        //
                    }

                } else if (this.needStackingForCommitRollbackPrepare) {
                    super.resumeStacked(stackedXid);
                }
                throw xaex;
            }

            if (this.needStackingForCommitRollbackPrepare) {
                super.resumeStacked(stackedXid);
            }

            return returnVal;
        }
    }

    protected int doPrepare(Xid xid) throws XAException, SQLException {

        int returnVal = 0;
        CallableStatement cstmt = null;
        try {
            cstmt = this.connection.prepareCall(xa_prepare_post_816);

            cstmt.registerOutParameter(1, 2);
            cstmt.setInt(2, xid.getFormatId());
            cstmt.setBytes(3, xid.getGlobalTransactionId());
            cstmt.setBytes(4, xid.getBranchQualifier());

            cstmt.execute();

            returnVal = cstmt.getInt(1);
        } catch (SQLException s) {
            int errorCode = s.getErrorCode();

            if (errorCode == 0) {
                throw new XAException(-6);
            }

            throw s;
        } finally {
            try {
                if (cstmt != null) cstmt.close();
            } catch (SQLException s) {
            }
            cstmt = null;
        }

        return returnVal;
    }

    protected OracleConnection getConnectionDuringExceptionHandling() {
        return (OracleConnection) this.connection;
    }

    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        synchronized (this.connection) {

            if ((flag & 0x1800000) != flag) {

                throw new XAException(-5);
            }

            if (flag == 16777216) {
                this.isTMRScanStarted = true;
            } else {
                Xid[] arrayOfXid1;
                if ((this.isTMRScanStarted) && (flag == 8388608)) {
                    this.isTMRScanStarted = false;
                    arrayOfXid1 = NO_XID;

                    return arrayOfXid1;
                }
                if ((this.isTMRScanStarted) && (flag == 0)) {
                    arrayOfXid1 = NO_XID;

                    return arrayOfXid1;
                }
            }
            Statement stmt = null;
            ResultSet rset = null;
            ArrayList<DruidXid> xidCollection = new ArrayList<DruidXid>(50);
            try {
                stmt = this.connection.createStatement();
                try {
                    stmt.execute(this.synchronizeBeforeRecoverNewCall);
                } catch (Exception exc) {
                    stmt.execute(this.synchronizeBeforeRecoverOldCall);
                }

                rset = stmt.executeQuery(this.recoverySqlRows);

                while (rset.next()) {
                    xidCollection.add(new DruidXid(rset.getInt(1), rset.getBytes(2), rset.getBytes(3)));
                }

            } catch (SQLException sqe) {
                throw new XAException(-3);
            } finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                    if (rset != null) rset.close();
                } catch (Exception ignore) {
                }
            }
            int xidSize = xidCollection.size();
            Xid[] xids = new Xid[xidSize];
            System.arraycopy(xidCollection.toArray(), 0, xids, 0, xidSize);

            Xid[] arrayOfXid2 = xids;

            return arrayOfXid2;
        }
    }

    @Override
    public void rollback(Xid xid) throws XAException {

        synchronized (this.connection) {
            int returnVal = 0;

            if (xid == null) {
                throw new XAException(-5);
            }

            Xid stackedXid = null;
            if (this.needStackingForCommitRollbackPrepare) {
                stackedXid = super.suspendStacked(xid);
            } else {
                removeXidFromList(xid);

                if (this.activeXid == null) exitGlobalTxnMode();
            }
            try {
                doRollback(xid);
            } catch (SQLException sqle) {
                checkError(sqle, -3);
            }

            if (this.needStackingForCommitRollbackPrepare) {
                super.resumeStacked(stackedXid);
            }

            checkError(returnVal);
        }
    }

    protected void doRollback(Xid xid) throws XAException, SQLException {

        CallableStatement cstmt = null;
        try {
            cstmt = this.connection.prepareCall(xa_rollback_post_816);

            cstmt.registerOutParameter(1, 2);
            cstmt.setInt(2, xid.getFormatId());
            cstmt.setBytes(3, xid.getGlobalTransactionId());
            cstmt.setBytes(4, xid.getBranchQualifier());

            cstmt.execute();

            int returnVal = cstmt.getInt(1);

            checkError(returnVal, -7);
        } catch (SQLException s) {
            int errorCode = s.getErrorCode();

            if (errorCode == 0) {
                throw new XAException(-6);
            }

            throw s;
        } finally {
            try {
                if (cstmt != null) cstmt.close();
            } catch (SQLException s) {
            }
            cstmt = null;
        }
    }

    @Override
    public void start(Xid xid, int flag) throws XAException {
        synchronized (this.connection) {
            int returnVal = -1;
            try {
                if (xid == null) {
                    throw new XAException(-5);
                }

                int isolFlag = flag & 0xFF00;

                flag &= -65281;

                int otherFlag = flag & 0x10000 | (this.isTransLoose ? 65536 : 0);

                flag &= -65537;

                if (((flag & 0x8200002) != flag) || ((otherFlag != 0) && ((otherFlag & 0x10000) != 65536))) {
                    throw new XAException(-5);
                }

                if (((isolFlag & 0xFF00) != 0) && (isolFlag != 256) && (isolFlag != 512) && (isolFlag != 1024)) {
                    throw new XAException(-5);
                }

                if (((flag & 0x8200000) != 0) && (((isolFlag & 0xFF00) != 0) || ((otherFlag & 0x10000) != 0))) {

                    throw new XAException(-5);
                }

                flag |= isolFlag | otherFlag;

                saveAndAlterAutoCommitModeForGlobalTransaction();
                try {
                    returnVal = doStart(xid, flag);
                } catch (SQLException sqle) {
                    checkError(sqle, -3);
                }

                checkError(returnVal);

                boolean[] isLocallySuspended = { false };
                createOrUpdateXid(xid, false, isLocallySuspended);
            } catch (XAException ea) {
                restoreAutoCommitModeForGlobalTransaction();

                throw ea;
            }
        }
    }

    protected void enterGlobalTxnMode() {
        ((OracleConnection) this.connection).setTxnMode(1);
    }

    protected void exitGlobalTxnMode() {
        ((OracleConnection) this.connection).setTxnMode(0);
    }

    protected int doStart(Xid xid, int flag) throws XAException, SQLException {

        int returnVal = -1;
        CallableStatement cstmt = null;
        try {
            cstmt = this.connection.prepareCall(xa_start_post_816);

            cstmt.registerOutParameter(1, 2);
            cstmt.setInt(2, xid.getFormatId());
            cstmt.setBytes(3, xid.getGlobalTransactionId());
            cstmt.setBytes(4, xid.getBranchQualifier());
            cstmt.setInt(5, this.timeout);
            cstmt.setInt(6, flag);

            cstmt.execute();

            returnVal = cstmt.getInt(1);
        } catch (SQLException s) {
            returnVal = s.getErrorCode();

            if (returnVal == 0) {
                throw new XAException(-6);
            }

            throw s;
        } finally {
            try {
                if (cstmt != null) cstmt.close();
            } catch (SQLException s) {
            }
            cstmt = null;
        }

        return returnVal;
    }

}
