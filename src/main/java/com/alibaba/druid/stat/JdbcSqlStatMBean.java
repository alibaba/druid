/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.stat;

import java.util.Date;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public interface JdbcSqlStatMBean {

    String getSql();

    Date getExecuteLastStartTime();

    Date getExecuteNanoSpanMaxOccurTime();

    Date getExecuteErrorLastTime();

    long getExecuteBatchSizeTotal();

    long getExecuteBatchSizeMax();

    long getExecuteSuccessCount();

    long getExecuteMillisTotal();

    long getExecuteMillisMax();

    long getErrorCount();

    long getConcurrentMax();

    long getRunningCount();

    String getName();

    String getFile();

    void reset();

    long getFetchRowCount();

    long getUpdateCount();

    long getExecuteCount();

    long getId();
}
