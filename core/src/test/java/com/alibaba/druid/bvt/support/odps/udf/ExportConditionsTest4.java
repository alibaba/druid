package com.alibaba.druid.bvt.support.odps.udf;

import com.alibaba.druid.support.opds.udf.ExportConditions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExportConditionsTest4 {
    ExportConditions udf = new ExportConditions();

    @Test
    public void test_export_conditions() throws Exception {
        String result = udf.evaluate("select * from t where trim(name) <> 'abc'");
        assertEquals("[[\"t\",\"name\",\"<>\",\"abc\"]]", result);
    }
}
