package com.alibaba.druid.pool.xa;

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

public class DruidXid implements Xid {

    private int             formatId;
    private byte[]          gtrid        = null;
    private byte[]          bqual        = null;
    private byte[]          txctx        = null;
    public static final int MAXGTRIDSIZE = 64;
    public static final int MAXBQUALSIZE = 64;
    private int             state;

    public DruidXid(int fId, byte[] gId, byte[] bId) throws XAException{
        this(fId, gId, bId, null);
    }

    public DruidXid(int fId, byte[] gId, byte[] bId, byte[] context) throws XAException{

        this.formatId = fId;

        if ((gId != null) && (gId.length > 64)) {
            throw new XAException(-4);
        }

        this.gtrid = gId;

        if ((bId != null) && (bId.length > 64)) {
            throw new XAException(-4);
        }

        this.bqual = bId;
        this.txctx = context;
        this.state = 0;
    }

    public void setState(int k) {
        this.state = k;
    }

    public int getState() {
        return this.state;
    }

    public int getFormatId() {
        return this.formatId;
    }

    public byte[] getGlobalTransactionId() {
        return this.gtrid;
    }

    public byte[] getBranchQualifier() {
        return this.bqual;
    }

    public byte[] getTxContext() {
        return this.txctx;
    }

    public void setTxContext(byte[] context) {
        this.txctx = context;
    }

    public static final boolean isLocalTransaction(Xid xid) {

        byte[] gtrid = xid.getGlobalTransactionId();

        if (gtrid == null) {
            return true;
        }
        for (int i = 0; i < gtrid.length; i++) {
            if (gtrid[i] == 0) {
                continue;
            }
            return false;
        }
        return true;
    }
}
