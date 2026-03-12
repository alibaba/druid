package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分页工具类的单元测试，presto SQL类型的
 *
 * @author zhangcanlong
 * @date 2022/02/08
 */
public class PagerUtilsTest_Limit_presto_0 {
    @Test
    public void test_presto_0() throws Exception {
        String result = PagerUtils.limit("SELECT * FROM test", DbType.presto, 0, 10);
        System.out.println(result);
        String sureResult = "SELECT *\n" + "FROM test\n" + " LIMIT 10";
        assertEquals(sureResult, result);
    }
}
