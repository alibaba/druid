package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;

public class OracleFormatTest2 extends TestCase {

    public void test_formatOracle() throws Exception {
        String text = "SELECT count(*) FROM T1, (SELECT DISTINCT parent_id AS parentId FROM T2 a1 WHERE FSEQ IN (?) AND NOT order_from = ? AND status IN (?) ) b WHERE ID = b.parentId AND GMT_CREATE >= to_date(?, ?) AND GMT_CREATE <= to_date(?, ?)";

        String formatedText = SQLUtils.format(text, JdbcUtils.ORACLE);
        System.out.println(formatedText);
    }
}
