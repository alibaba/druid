package com.alibaba.druid.bvt.pool.vendor;

import com.alibaba.druid.PoolTestCase;
import com.alibaba.druid.pool.vendor.GaussDBExceptionSorter;
import com.huawei.gaussdb.jdbc.util.GT;
import com.huawei.gaussdb.jdbc.util.PSQLException;
import com.huawei.gaussdb.jdbc.util.PSQLState;
import org.junit.Assert;


public class GaussDBExceptionSorterTest extends PoolTestCase {
    public void test_gaussdb() throws Exception {
        GaussDBExceptionSorter exSorter = new GaussDBExceptionSorter();

        PSQLException ex = new PSQLException(GT.tr("Expected an EOF from server, got: {0}", new Integer(0)),
                PSQLState.COMMUNICATION_ERROR);
        Assert.assertTrue(exSorter.isExceptionFatal(ex));
    }
}
