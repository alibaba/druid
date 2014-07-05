package com.alibaba.druid.bvt.filter;

import java.sql.SQLException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterManager;

public class FilterManagerTest extends TestCase {

    public void test_instance() throws Exception {
        new FilterManager();
    }

    public void test_loadFilter() throws Exception {
        Exception error = null;

        try {
            FilterManager.loadFilter(new ArrayList<Filter>(), ErrorFilter.class.getName());
        } catch (SQLException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
    
    public void test_loadFilter_2() throws Exception {
        Exception error = null;
        
        try {
            FilterManager.loadFilter(new ArrayList<Filter>(), ErrorFilter.class.getName());
        } catch (SQLException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public static class ErrorFilter extends FilterAdapter {

        public ErrorFilter(){
            throw new RuntimeException();
        }
    }
}
