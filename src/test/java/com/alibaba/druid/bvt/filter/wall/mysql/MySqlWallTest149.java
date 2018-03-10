package com.alibaba.druid.bvt.filter.wall.mysql;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import junit.framework.TestCase;
import org.junit.Assert;

public class MySqlWallTest149 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        String sql = "select t1.teacher_id from teacher_1 t1 left join teacher_2 t2 on t1.teacher_id";

        assertFalse(
                provider.checkValid(sql)
        );
    }
}
