/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.pool.vendor;

import com.alibaba.druid.PoolTestCase;
import com.alibaba.druid.pool.vendor.GaussDBExceptionSorter;
import com.huawei.gaussdb.jdbc.util.GT;
import com.huawei.gaussdb.jdbc.util.PSQLException;
import com.huawei.gaussdb.jdbc.util.PSQLState;
import org.junit.Assert;

/**
 * @author Acewuye
 *
 * Notes: Original code of this class based on com.alibaba.druid.bvt.pool.vendor.PGExceptionSorterTest
 */
public class GaussDBExceptionSorterTest extends PoolTestCase {
    public void test_gaussdb() throws Exception {
        GaussDBExceptionSorter exSorter = new GaussDBExceptionSorter();

        PSQLException ex = new PSQLException(GT.tr("Expected an EOF from server, got: {0}", new Integer(0)),
                PSQLState.COMMUNICATION_ERROR);
        Assert.assertTrue(exSorter.isExceptionFatal(ex));
    }
}
