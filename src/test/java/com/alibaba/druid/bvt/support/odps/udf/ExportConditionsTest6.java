package com.alibaba.druid.bvt.support.odps.udf;

import org.junit.Assert;

import com.alibaba.druid.support.opds.udf.ExportConditions;

import junit.framework.TestCase;

public class ExportConditionsTest6 extends TestCase {
    ExportConditions udf = new ExportConditions();
    
    public void test_export_conditions() throws Exception {
        String result = udf.evaluate("select * from t where name = 'a1'", "odps", true);
        Assert.assertEquals("[[\"t\",\"name\",\"=\",[\"a1\"]]]", result);
    }
}
