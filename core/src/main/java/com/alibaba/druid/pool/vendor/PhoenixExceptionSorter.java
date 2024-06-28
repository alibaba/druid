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
package com.alibaba.druid.pool.vendor;

import com.alibaba.druid.pool.ExceptionSorter;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.sql.SQLException;
import java.util.Properties;

public class PhoenixExceptionSorter implements ExceptionSorter {
    private static final Log LOG = LogFactory.getLog(OracleExceptionSorter.class);

    /**
     * 解决phoenix 的错误 --Connection is null or closed
     *
     * @param e the exception
     * @return a boolean indicating whether the SQLException is fatal
     */
    @Override
    public boolean isExceptionFatal(SQLException e) {
        if (e.getMessage().contains("Connection is null or closed")) {
            LOG.error("剔除phoenix不可用的连接", e);
            return true;
        }

        return false;
    }

    @Override
    public void configFromProperties(Properties properties) {
    }

}
