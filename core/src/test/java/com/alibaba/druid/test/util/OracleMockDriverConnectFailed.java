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
package com.alibaba.druid.test.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OracleMockDriverConnectFailed extends OracleMockDriver {

    public static CyclicBarrier CONNECT_BARIER = new CyclicBarrier(2);

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        try {
            CONNECT_BARIER.await(100, TimeUnit.MILLISECONDS);
        } catch (BrokenBarrierException e) {
            throw new SQLException("mock connect failed: BrokenBarrierException replaced by SQLException");
        } catch (InterruptedException e) {
            throw new SQLException("mock connect failed: InterruptedException replaced by SQLException");
        } catch (TimeoutException e) {
            throw new SQLException("mock connect failed: TimeoutException replaced by SQLException");
        }
        throw new SQLException("mock connect failed");
    }
}
