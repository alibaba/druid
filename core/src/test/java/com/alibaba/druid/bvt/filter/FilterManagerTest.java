package com.alibaba.druid.bvt.filter;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterManager;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void test_existsFilter_caseSensitive() throws Exception {
        List<Filter> filterList = new ArrayList<Filter>();
        filterList.add(new CaseTestFilter());
        String exactName = CaseTestFilter.class.getName();
        String lowerName = exactName.toLowerCase();
        Method existsFilter = FilterManager.class
                .getDeclaredMethod("existsFilter", List.class, String.class);
        existsFilter.setAccessible(true);
        assertTrue((Boolean) existsFilter.invoke(null, filterList, exactName));
        assertFalse((Boolean) existsFilter.invoke(null, filterList, lowerName),
                "existsFilter should be case-sensitive; class names that differ only in case are different classes");
    }

    public static class CaseTestFilter extends FilterAdapter {
    }

}
