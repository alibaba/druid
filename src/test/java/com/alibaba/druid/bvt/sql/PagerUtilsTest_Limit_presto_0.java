package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * 分页工具类的单元测试，presto SQL类型的
 *
 * @author zhangcanlong
 * @date 2022/02/08
 */
public class PagerUtilsTest_Limit_presto_0 extends TestCase {
    public void test_presto_0() throws Exception {
        String result = PagerUtils.limit("SELECT * FROM test", DbType.presto, 0, 10);
        System.out.println(result);
        String sureResult = "SELECT *\n" + "FROM test\n" + " LIMIT 10";
        Assert.assertEquals(sureResult, result);
    }

}
