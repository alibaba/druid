package com.alibaba.druid.bvt.mock;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockResultSetMetaData;

public class MockResultSetMetaDataTest extends TestCase {
    public void test_resultSet_metadata() throws Exception {
        MockResultSetMetaData meta = new MockResultSetMetaData();
        Assert.assertTrue(meta.isWrapperFor(MockResultSetMetaData.class));
        Assert.assertFalse(meta.isWrapperFor(BigDecimal.class));
        Assert.assertTrue(meta.unwrap(MockResultSetMetaData.class) instanceof MockResultSetMetaData);
    }
}
