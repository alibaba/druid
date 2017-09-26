package com.alibaba.druid.bvt.pool.vendor;

import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;
import com.mysql.jdbc.CommunicationsException;
import junit.framework.TestCase;

import java.sql.SQLException;

/**
 * Created by wenshao on 18/07/2017.
 */
public class MySqlExceptionSorterTest_1 extends TestCase {
    public void test_0() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();

        SQLException rootException = new SQLException(
                new SQLException("Could not retrieve transation read-only status server",
                        new CommunicationsException(null, 0, 0, null)
                )
        );

        assertTrue(sorter.isExceptionFatal(rootException));
    }
}
