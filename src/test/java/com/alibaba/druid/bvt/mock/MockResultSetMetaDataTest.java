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
package com.alibaba.druid.bvt.mock;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSetMetaData;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockResultSetMetaData;
import com.alibaba.druid.util.jdbc.ResultSetMetaDataBase;
import com.alibaba.druid.util.jdbc.ResultSetMetaDataBase.ColumnMetaData;

public class MockResultSetMetaDataTest extends TestCase {

    public void test_resultSet_metadata() throws Exception {
        MockResultSetMetaData meta = new MockResultSetMetaData();
        Assert.assertTrue(meta.isWrapperFor(MockResultSetMetaData.class));
        Assert.assertFalse(meta.isWrapperFor(BigDecimal.class));
        Assert.assertTrue(meta.unwrap(MockResultSetMetaData.class) instanceof MockResultSetMetaData);
        Assert.assertTrue(meta.unwrap(ResultSetMetaDataBase.class) instanceof MockResultSetMetaData);
        Assert.assertTrue(meta.unwrap(ResultSetMetaData.class) instanceof MockResultSetMetaData);
        Assert.assertTrue(meta.unwrap(null) == null);
        Assert.assertTrue(meta.unwrap(java.sql.ResultSetMetaData.class) != null);
        Assert.assertTrue(meta.unwrap(Object.class) != null);
        Assert.assertTrue(meta.unwrap(Date.class) == null);

        ColumnMetaData column = new ColumnMetaData();
        meta.getColumns().add(column);

        meta.isAutoIncrement(1);
        meta.isCaseSensitive(1);
        meta.isSearchable(1);
        meta.isCurrency(1);
        meta.isNullable(1);
        meta.isSigned(1);
        meta.getColumnDisplaySize(1);
        meta.getColumnLabel(1);
        meta.getSchemaName(1);
        meta.getPrecision(1);
        meta.getScale(1);
        meta.getTableName(1);
        meta.getCatalogName(1);
        meta.getColumnTypeName(1);
        meta.isReadOnly(1);
        meta.isWritable(1);
        meta.isDefinitelyWritable(1);
        meta.getColumnClassName(1);

    }
}
