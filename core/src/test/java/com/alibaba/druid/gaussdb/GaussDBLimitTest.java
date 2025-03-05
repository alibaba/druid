package com.alibaba.druid.gaussdb;

import com.alibaba.druid.DbType;

/**
 * @author lizongbo
 */
public class GaussDBLimitTest {
    /**
     * @param args
     */
    public static void main(String[] args) {
        DbType dbType = DbType.gaussdb;// "gaussdb";
        String sql = " select * from brandinfo where 1=1 and brandid > 100 order by brandid asc";
        String sqlLimit = com.alibaba.druid.sql.PagerUtils.limit(sql, dbType,
                2499, 100);
        System.out.println("sqlLimit == " + sqlLimit);
        String sqlCount = com.alibaba.druid.sql.PagerUtils.count(sql, dbType);
        System.out.println("sqlCount == " + sqlCount);

    }

}
