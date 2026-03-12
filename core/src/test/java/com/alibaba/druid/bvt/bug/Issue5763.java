package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5763">...</a>
 */
public class Issue5763 {
    @Test
    public void test_for_clone() throws Exception {
        String sql = "select user.col from user use index(a)";
        SQLStatement origin = SQLUtils.parseSingleMysqlStatement(sql);
        System.out.println("origin = " + origin);

        SQLStatement clone = origin.clone();
        System.out.println("clone = " + clone);
        assertEquals(origin.toString(), clone.toString());
        assertEquals(origin, clone);
    }
}
