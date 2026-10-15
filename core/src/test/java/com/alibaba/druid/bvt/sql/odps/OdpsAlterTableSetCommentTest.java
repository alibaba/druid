package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsAlterTableSetCommentTest {
    @Test
    public void test_if() throws Exception {
        String sql = "alter table sale_detail set comment 'new coments for table sale_detail';";
        assertEquals("ALTER TABLE sale_detail"
                + "\n\tSET COMMENT 'new coments for table sale_detail';", SQLUtils.formatOdps(sql));
    }
}
