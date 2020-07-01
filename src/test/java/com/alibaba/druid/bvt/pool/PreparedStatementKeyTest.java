package com.alibaba.druid.bvt.pool;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidPooledPreparedStatement.PreparedStatementKey;
import com.alibaba.druid.pool.PreparedStatementPool.MethodType;

public class PreparedStatementKeyTest extends TestCase {

    public void test_0() throws Exception {
        PreparedStatementKey k1 = new PreparedStatementKey("select 'a'", "c1", MethodType.M1, 101, 102, 103);
        Assert.assertEquals(101, k1.getResultSetType());
        Assert.assertEquals(102, k1.getResultSetConcurrency());
        Assert.assertEquals(103, k1.getResultSetHoldability());
    }

    public void test_eq() throws Exception {
        Assert.assertEquals(new PreparedStatementKey("select 'a'", "c1", MethodType.M1, 101, 102, 103),
                            new PreparedStatementKey("select 'a'", "c1", MethodType.M1, 101, 102, 103));

    }

    public void test_not_eq() throws Exception {
        Assert.assertFalse( //
        new PreparedStatementKey("select 'a'", "c1", MethodType.M1, 101, 102, 103).equals( //
        new PreparedStatementKey("select 'a'", "c1", MethodType.M1, 201, 102, 103)));

    }

    public void test_not_eq_1() throws Exception {
        Assert.assertFalse( //
        new PreparedStatementKey("select 'a'", "c1", MethodType.M1, 101, 102, 103).equals( //
        new PreparedStatementKey("select 'a'", "c1", MethodType.M1, 101, 202, 103)));

    }

    public void test_not_eq_2() throws Exception {
        Assert.assertFalse( //
        new PreparedStatementKey("select 'a'", "c1", MethodType.M1, 101, 102, 103).equals( //
        new PreparedStatementKey("select 'a'", "c1", MethodType.M1, 101, 102, 203)));

    }

    public void test_not_eq_3() throws Exception {
        Assert.assertFalse( //
        new PreparedStatementKey("select 'a'", "c1", MethodType.M1, 101, 102, 103).equals( //
        new PreparedStatementKey("select 'a'", "c1", MethodType.M2, 101, 102, 103)));

    }

    public void test_not_eq_4() throws Exception {
        Assert.assertFalse( //
        new PreparedStatementKey("select 'a'", "c1", MethodType.M1, 101, 102, 103).equals( //
        new PreparedStatementKey("select 'a'", "c2", MethodType.M1, 101, 102, 103)));

    }

    public void test_not_eq_5() throws Exception {
        Assert.assertFalse( //
        new PreparedStatementKey("select 'a'", "c1", MethodType.M1, 101, 102, 103).equals( //
        new PreparedStatementKey("select 'b'", "c1", MethodType.M1, 101, 102, 103)));

    }
}
