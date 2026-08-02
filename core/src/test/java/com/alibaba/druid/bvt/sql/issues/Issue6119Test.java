package com.alibaba.druid.bvt.sql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SQLUtils.refactor 改写表名时，select item 中三段式 {@code db.tbl.col} 的
 * 中段表名未被改写的问题。
 *
 * @author fudianchn
 * @see <a href="https://github.com/alibaba/druid/issues/6119">Issue #6119</a>
 */
public class Issue6119Test {
    @Test
    public void refactorThreePartTableName() {
        Map<String, String> mapping = Collections.singletonMap("tbl_b", "xxx");
        String out = SQLUtils.refactor("select db_a.tbl_b.col_c from db_a.tbl_b", DbType.mysql, mapping);
        // both the FROM table and the select-item owner chain must be rewritten
        assertFalse(out.contains("tbl_b"), "table name should be rewritten everywhere, was: " + out);
        assertTrue(out.contains("db_a.xxx.col_c"), "select-item owner must be rewritten, was: " + out);
    }

    @Test
    public void refactorTwoPartTableName() {
        Map<String, String> mapping = Collections.singletonMap("user_extra", "renamed");
        String out = SQLUtils.refactor("select user_extra.col from user_extra", DbType.mysql, mapping);
        assertFalse(out.contains("user_extra"), "was: " + out);
        assertTrue(out.contains("renamed.col"), "was: " + out);
    }
}
