package com.alibaba.druid.bvt.support.odps.udf;

import static org.junit.Assert.assertEquals;


import com.alibaba.druid.support.opds.udf.ExportConditions;

import junit.framework.TestCase;

public class ExportConditionsTest2 extends TestCase {
    ExportConditions udf = new ExportConditions();

    public void test_export_conditions() throws Exception {
        String result = udf.evaluate("select * from t where proof_account is not null");
        assertEquals("[[\"t\",\"proof_account\",\"IS NOT\",null]]", result);
    }
}
