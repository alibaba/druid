package com.alibaba.druid.bvt.pool.vendor;

import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;
import com.mysql.jdbc.CommunicationsException;
import junit.framework.TestCase;

import java.net.SocketTimeoutException;
import java.sql.SQLException;

/**
 * Created by wenshao on 18/07/2017.
 */
public class MySqlExceptionSorterTest_2 extends TestCase {
    public void test_0() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();

        SQLException rootException = new SQLException(
                new SQLException("Could not retrieve transation read-only status server",
                        new SocketTimeoutException()
                )
        );

        assertTrue(sorter.isExceptionFatal(rootException));
    }
}
