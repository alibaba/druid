package com.alibaba.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * Created by admin on 2019/8/30.
 */
public class Test3428 {
    @Test
    public void test(){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ts.setNanos(999888777);
        List params= Arrays.asList(ts, new java.sql.Date(ts.getTime()), new java.sql.Time(ts.getTime()));
        String format = SQLUtils.format("INSERT INTO `test`(`_timestamp`, `_date`, `_time`) VALUES (?,?,?);", JdbcConstants.MYSQL, params);
        System.out.println(format);
    }
}
