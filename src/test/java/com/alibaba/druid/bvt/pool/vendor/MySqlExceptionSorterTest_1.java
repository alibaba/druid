package com.alibaba.druid.bvt.pool.vendor;

import com.alibaba.druid.PoolTestCase;
import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;
import com.alibaba.druid.util.MySqlUtils;

import java.lang.reflect.Constructor;
import java.sql.SQLException;

/**
 * Created by wenshao on 18/07/2017.
 */
public class MySqlExceptionSorterTest_1 extends PoolTestCase {
    public void test_0() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();

        Class clazz = MySqlUtils.getCommunicationsExceptionClass();

        if ("com.mysql.jdbc.CommunicationsException".equals(clazz.getName())) {
            Constructor constructor = null;
            for (Constructor item : clazz.getConstructors()) {
                if (item.getParameterTypes().length == 4) {
                    constructor = item;
                    break;
                }
            }

            SQLException rootException = new SQLException(
                    new SQLException("Could not retrieve transation read-only status server",
                            (Exception) constructor.newInstance(null, 0, 0, null)
                    )
            );

            assertTrue(sorter.isExceptionFatal(rootException));
        } else {
            Constructor constructor = null;
            for (Constructor item : clazz.getConstructors()) {
                if (item.getParameterTypes().length == 2) {
                    constructor = item;
                    break;
                }
            }

            SQLException rootException = new SQLException(
                    new SQLException("Could not retrieve transation read-only status server",
                            (Exception) constructor.newInstance(null, null)
                    )
            );

            assertTrue(sorter.isExceptionFatal(rootException));
        }
    }
}
