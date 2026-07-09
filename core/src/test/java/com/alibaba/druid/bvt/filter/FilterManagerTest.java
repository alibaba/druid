package com.alibaba.druid.bvt.filter;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterManager;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FilterManagerTest {
    static {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(null);

            assertNotNull(FilterManager.getFilter("stat"));
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }

    @Test
    public void test_instance() throws Exception {
        new FilterManager();
    }

    @Test
    public void test_loadFilter() throws Exception {
        Exception error = null;

        try {
            FilterManager.loadFilter(new ArrayList<Filter>(), ErrorFilter.class.getName());
        } catch (SQLException e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
    public void test_loadFilter_2() throws Exception {
        Exception error = null;

        try {
            FilterManager.loadFilter(new ArrayList<Filter>(), ErrorFilter.class.getName());
        } catch (SQLException e) {
            error = e;
        }
        assertNotNull(error);
    }

    public static class ErrorFilter extends FilterAdapter {
        public ErrorFilter() {
            throw new RuntimeException();
        }
    }

    @org.junit.jupiter.api.Test
    public void test_existsFilter_caseSensitive() throws Exception {
        java.util.List<com.alibaba.druid.filter.Filter> filterList = new java.util.ArrayList<>();
        filterList.add(new CaseTestFilter());
        String exactName = CaseTestFilter.class.getName();
        String lowerName = exactName.toLowerCase();
        java.lang.reflect.Method existsFilter = com.alibaba.druid.filter.FilterManager.class
                .getDeclaredMethod("existsFilter", java.util.List.class, String.class);
        existsFilter.setAccessible(true);
        org.junit.jupiter.api.Assertions.assertTrue((Boolean) existsFilter.invoke(null, filterList, exactName));
        org.junit.jupiter.api.Assertions.assertFalse((Boolean) existsFilter.invoke(null, filterList, lowerName),
                "existsFilter should be case-sensitive; class names that differ only in case are different classes");
    }

    public static class CaseTestFilter extends com.alibaba.druid.filter.FilterAdapter {
    }

}
