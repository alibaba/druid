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
package com.alibaba.druid.bvt.filter;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.index.IllegalSQLIndexFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import junit.framework.TestCase;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * 拦截SQL类型的场景
 * 1.查询left jion 超过3张表
 * 2.在字段上使用函数
 * 3.where条件为空
 * 4.where条件使用了 !=
 * 5.where条件使用了 not 关键字
 * 6.where条件没有索引索引，最左原则
 *
 * 测试不拦截的SQL，正常通过就可以了
 */
public class IllegalSQLIndexFilterExecuteTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);

        List<IllegalSQLIndexFilter.IndexInfo> indexInfos = new ArrayList<IllegalSQLIndexFilter.IndexInfo>();

        IllegalSQLIndexFilter.IndexInfo indexInfo = new IllegalSQLIndexFilter.IndexInfo();
        indexInfo.setDbName("db");
        indexInfo.setTableName("demo");
        indexInfo.setColumnName("columnName");
        indexInfos.add(indexInfo);

        Map<String, List<IllegalSQLIndexFilter.IndexInfo>> stringListMap = new HashMap<String, List<IllegalSQLIndexFilter.IndexInfo>>();
        stringListMap.put("demo", indexInfos);

        IllegalSQLIndexFilter illegalSQLIndexFilter = new IllegalSQLIndexFilter();
        illegalSQLIndexFilter.setIndexInfoMap(stringListMap);

        List<Filter> filters = new ArrayList<Filter>();
        filters.add(illegalSQLIndexFilter);

        dataSource.setProxyFilters(filters);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_stat() throws Exception {

        Assert.assertTrue(dataSource.isInited());
        String sql = "select x from demo where columnName = ?";

        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.execute();


        sql = "update demo set x = ? where columnName = ? ";

        stmt = conn.prepareStatement(sql);

        stmt.execute();
    }


}
