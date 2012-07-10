package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;

public class OracleFormatTest2 extends TestCase {

    public void test_formatOracle() throws Exception {
        String text = "UPDATE MEMBER SET GMT_MODIFIED = SYSDATE, STATUS = ?, email = CASE WHEN status = ? THEN rtrim(email, ? || id || ?) ELSE email END WHERE ID IN (?) AND STATUS <> ?";

        String formatedText = SQLUtils.format(text, JdbcUtils.ORACLE);
        System.out.println(formatedText);
    }
}
