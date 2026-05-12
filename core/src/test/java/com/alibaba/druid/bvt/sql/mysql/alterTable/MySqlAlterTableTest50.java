package com.alibaba.druid.bvt.sql.mysql.alterTable;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author shicai.xsc 2018/9/13 下午3:35
 * @desc
 * @since 5.0.0.0
 */
public class MySqlAlterTableTest50 {
    @Test
    public void test_0() throws Exception {
        String sql = "alter table ren_test ALGORITHM=INPLACE,LOCK=NONE,add index idx_name (name) ;";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(1, stmtList.size());
        SQLStatement stmt = stmtList.get(0);
        String output = stmt.toString();
//        assertEquals("ALTER TABLE ren_test\n" +
//                "\tALGORITHM = INPLACE,\n" +
//                "\tADD INDEX idx_name (name)\n" +
//                "\tLOCK = NONE;", output);
    }
}
