package com.alibaba.druid.bvt.sql.eval;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;

public class LikeTest extends TestCase {

    public void test_any_0() throws Exception {
        Assert.assertTrue(SQLEvalVisitorUtils.like("abcdefg", "%"));
    }

    public void test_any_1() throws Exception {
        Assert.assertTrue(SQLEvalVisitorUtils.like("abcdefg", "%%"));
    }

    public void test_any_2() throws Exception {
        Assert.assertTrue(SQLEvalVisitorUtils.like("abcdefg", "%b%"));
    }
    
    public void test_any_3() throws Exception {
        Assert.assertTrue(SQLEvalVisitorUtils.like("abcdefg", "a%"));
    }

    public void test_any_4() throws Exception {
        Assert.assertTrue(SQLEvalVisitorUtils.like("abcdefg", "%g"));
    }

    public void test_single_0() throws Exception {
        Assert.assertTrue(SQLEvalVisitorUtils.like("a", "_"));
    }

    public void test_single_1() throws Exception {
        Assert.assertTrue(SQLEvalVisitorUtils.like("ab", "__"));
    }
    
    public void test_single_2() throws Exception {
        Assert.assertTrue(SQLEvalVisitorUtils.like("abcd", "a__d"));
    }
    
    public void test_single_3() throws Exception {
        Assert.assertTrue(SQLEvalVisitorUtils.like("abcdf", "a__d_"));
    }
    
    public void test_range_0() throws Exception {
        Assert.assertTrue(SQLEvalVisitorUtils.like("abcdf", "abcd[a-f]"));
    }
    
    public void test_range_1() throws Exception {
        Assert.assertFalse(SQLEvalVisitorUtils.like("abcdf", "abcd[a-e]"));
    }
    
    public void test_range_2() throws Exception {
        Assert.assertTrue(SQLEvalVisitorUtils.like("ab", "a[abf]"));
    }
}
