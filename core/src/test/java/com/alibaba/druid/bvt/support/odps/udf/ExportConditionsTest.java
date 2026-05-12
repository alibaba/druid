package com.alibaba.druid.bvt.support.odps.udf;

import com.alibaba.druid.support.opds.udf.ExportConditions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExportConditionsTest {
    ExportConditions udf = new ExportConditions();

    @Test
    public void test_export_conditions() throws Exception {
        String result = udf.evaluate("select * from t where id = 3 and name = 'chensheng'");
        assertEquals("[[\"t\",\"id\",\"=\",3],[\"t\",\"name\",\"=\",\"chensheng\"]]", result);
    }
}
