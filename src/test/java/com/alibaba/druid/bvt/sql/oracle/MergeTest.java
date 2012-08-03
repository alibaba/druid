package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;

public class MergeTest extends TestCase {

    public void test_mergeCall() throws Exception {
        String sql = "{ call INTERFACE_DATA_EXTRACTION.INVOICE_INFO(?,?,?)}";

        ParameterizedOutputVisitorUtils.parameterize(sql, null);
    }

    public void test_mergeCall_oracle() throws Exception {
        String sql = "{ call INTERFACE_DATA_EXTRACTION.INVOICE_INFO(?,?,?)}";

        ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.ORACLE);
    }
    
    public void test_mergeCall_mysql() throws Exception {
        String sql = "{ call INTERFACE_DATA_EXTRACTION.INVOICE_INFO(?,?,?)}";
        
        ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.MYSQL);
    }
}
