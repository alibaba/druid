package com.alibaba.druid.bvt.sql.mysql.alterTable;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * @author shicai.xsc 2018/9/13 下午3:35
 * @desc
 * @since 5.0.0.0
 */
public class MySqlAlterTableTest46_add_column extends TestCase {
    public void test_0() throws Exception {
        String sql = "ALTER TABLE test_pk ADD COLUMN remark2 varchar(255) DEFAULT NULL , ALGORITHM=inplace,LOCK=NONE";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(1, stmtList.size());
        SQLStatement stmt = stmtList.get(0);
        assertEquals("ALTER TABLE test_pk\n" +
                "\tADD COLUMN remark2 varchar(255) DEFAULT NULL,\n" +
                "\tALGORITHM = inplace,\n" +
                "\tLOCK = NONE", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "ALTER TABLE test_pk modify COLUMN remark2 varchar(32) DEFAULT NULL , ALGORITHM=copy,LOCK=SHARED";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(1, stmtList.size());
        SQLStatement stmt = stmtList.get(0);
        assertEquals("ALTER TABLE test_pk\n" +
                "\tMODIFY COLUMN remark2 varchar(32) DEFAULT NULL,\n" +
                "\tALGORITHM = copy,\n" +
                "\tLOCK = SHARED", stmt.toString());
    }

}
