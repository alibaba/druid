package com.alibaba.druid.bvt.support.odps.udf;

import org.junit.Assert;

import com.alibaba.druid.support.opds.udf.ExportConditions;

import junit.framework.TestCase;

public class ExportConditionsTest extends TestCase {
    ExportConditions udf = new ExportConditions();
    
    public void test_export_conditions() throws Exception {
        String result = udf.evaluate("select * from t where id = 3 and name = 'chensheng'");
        Assert.assertEquals("[[\"t\",\"id\",\"=\",3],[\"t\",\"name\",\"=\",\"chensheng\"]]", result);
    }
}
