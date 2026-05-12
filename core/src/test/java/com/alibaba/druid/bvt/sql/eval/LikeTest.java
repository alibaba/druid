package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LikeTest {
    @Test
    public void test_any_0() throws Exception {
        assertTrue(SQLEvalVisitorUtils.like("abcdefg", "%"));
    }

    @Test
    public void test_any_1() throws Exception {
        assertTrue(SQLEvalVisitorUtils.like("abcdefg", "%%"));
    }

    @Test
    public void test_any_2() throws Exception {
        assertTrue(SQLEvalVisitorUtils.like("abcdefg", "%b%"));
    }

    @Test
    public void test_any_3() throws Exception {
        assertTrue(SQLEvalVisitorUtils.like("abcdefg", "a%"));
    }

    @Test
    public void test_any_4() throws Exception {
        assertTrue(SQLEvalVisitorUtils.like("abcdefg", "%g"));
    }

    @Test
    public void test_single_0() throws Exception {
        assertTrue(SQLEvalVisitorUtils.like("a", "_"));
    }

    @Test
    public void test_single_1() throws Exception {
        assertTrue(SQLEvalVisitorUtils.like("ab", "__"));
    }

    @Test
    public void test_single_2() throws Exception {
        assertTrue(SQLEvalVisitorUtils.like("abcd", "a__d"));
    }

    @Test
    public void test_single_3() throws Exception {
        assertTrue(SQLEvalVisitorUtils.like("abcdf", "a__d_"));
    }

    @Test
    public void test_range_0() throws Exception {
        assertTrue(SQLEvalVisitorUtils.like("abcdf", "abcd[a-f]"));
    }

    @Test
    public void test_range_1() throws Exception {
        assertFalse(SQLEvalVisitorUtils.like("abcdf", "abcd[a-e]"));
    }

    @Test
    public void test_range_2() throws Exception {
        assertTrue(SQLEvalVisitorUtils.like("ab", "a[abf]"));
    }
}
