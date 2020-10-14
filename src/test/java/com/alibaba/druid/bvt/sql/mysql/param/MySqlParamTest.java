package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class MySqlParamTest extends TestCase {
    public final static DbType dbType = JdbcConstants.MYSQL;

    public void test_for_mysql_param() throws Exception {
        String sql = "/* 0b853c4a26094480140194289e3d24/0.1.1.2.1//2e3b9cf7/ */select `miller_cart`.`CART_ID`,`miller_cart`.`SKU_ID`,`miller_cart`.`ITEM_ID`,`miller_cart`.`QUANTITY`,`miller_cart`.`USER_ID`,`miller_cart`.`SELLER_ID`,`miller_cart`.`STATUS`,`miller_cart`.`EXT_STATUS`,`miller_cart`.`TYPE`,`miller_cart`.`SUB_TYPE`,`miller_cart`.`GMT_CREATE`,`miller_cart`.`GMT_MODIFIED`,`miller_cart`.`ATTRIBUTE`,`miller_cart`.`ATTRIBUTE_CC`,`miller_cart`.`EX2` from `miller_cart_0304` `miller_cart` where ((`miller_cart`.`USER_ID` = 2732851504) AND ((`miller_cart`.`STATUS` = 1) AND (`miller_cart`.`TYPE` IN (0,5,10)))) limit 0,200";
        long hash1 = ParameterizedOutputVisitorUtils.parameterizeHash(sql, dbType, null, null);
        long hash2 = FnvHash.fnv1a_64_lower(ParameterizedOutputVisitorUtils.parameterize(sql, dbType));
        assertEquals(hash1, hash2);


        SQLSelectListCache cache = new SQLSelectListCache(dbType);
        cache.add("select `miller_cart`.`CART_ID`,`miller_cart`.`SKU_ID`,`miller_cart`.`ITEM_ID`,`miller_cart`.`QUANTITY`,`miller_cart`.`USER_ID`,`miller_cart`.`SELLER_ID`,`miller_cart`.`STATUS`,`miller_cart`.`EXT_STATUS`,`miller_cart`.`TYPE`,`miller_cart`.`SUB_TYPE`,`miller_cart`.`GMT_CREATE`,`miller_cart`.`GMT_MODIFIED`,`miller_cart`.`ATTRIBUTE`,`miller_cart`.`ATTRIBUTE_CC`,`miller_cart`.`EX2` from");

        List<Object> outParameters = new ArrayList<Object>(); // 这个参数如果为null时，性能会进一步提升
        long hash3 = ParameterizedOutputVisitorUtils.parameterizeHash(sql, dbType, cache, outParameters);
        assertEquals(hash1, hash3);
    }
}
