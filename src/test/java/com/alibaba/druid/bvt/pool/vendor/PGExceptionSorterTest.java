package com.alibaba.druid.bvt.pool.vendor;

import com.alibaba.druid.PoolTestCase;
import org.junit.Assert;
import org.postgresql.util.GT;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import junit.framework.TestCase;

import com.alibaba.druid.pool.vendor.PGExceptionSorter;

public class PGExceptionSorterTest extends PoolTestCase {

    public void test_pg() throws Exception {
        PGExceptionSorter exSorter = new PGExceptionSorter();

        PSQLException ex = new PSQLException(GT.tr("Expected an EOF from server, got: {0}", new Integer(0)),
                                             PSQLState.COMMUNICATION_ERROR);
        Assert.assertTrue(exSorter.isExceptionFatal(ex));
    }
}
