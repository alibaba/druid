package com.alibaba.druid.bvt.mock;

import java.math.BigDecimal;
import java.sql.Date;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockResultSetMetaData;
import com.alibaba.druid.mock.MockResultSetMetaData.ColumnMetaData;

public class MockResultSetMetaDataTest extends TestCase {

    public void test_resultSet_metadata() throws Exception {
        MockResultSetMetaData meta = new MockResultSetMetaData();
        Assert.assertTrue(meta.isWrapperFor(MockResultSetMetaData.class));
        Assert.assertFalse(meta.isWrapperFor(BigDecimal.class));
        Assert.assertTrue(meta.unwrap(MockResultSetMetaData.class) instanceof MockResultSetMetaData);
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
