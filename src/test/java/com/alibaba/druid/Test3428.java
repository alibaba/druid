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
package com.alibaba.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * Created by admin on 2019/8/30.
 */
public class Test3428 {
    @Test
    public void test(){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ts.setNanos(999888777);
        List params= Arrays.asList(ts, new java.sql.Date(ts.getTime()), new java.sql.Time(ts.getTime()));
        String format = SQLUtils.format("INSERT INTO `test`(`_timestamp`, `_date`, `_time`) VALUES (?,?,?);", JdbcConstants.MYSQL, params);
        System.out.println(format);
    }
}
