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
public class MySqlAlterTableTest45 extends TestCase {
    public void test_0() throws Exception {
        String sql = "ALTER TABLE xx\n" +
            "ADD \n" +
            "EXTPARTITION (\n" +
            "DBPARTITION xxx BY KEY('abc') TBPARTITION yyy BY KEY('abc'), \n" +
            "DBPARTITION yyy BY KEY('def') TBPARTITION yyy BY KEY('def'), \n" +
            "DBPARTITION yyy BY KEY('gpk')\n" +
            ")";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(1, stmtList.size());
        SQLStatement stmt = stmtList.get(0);
        String output = stmt.toString();
        assertEquals("ALTER TABLE xx\n\t" +
            "ADD \n\t" +
            "EXTPARTITION (\n\t" +
            "\tDBPARTITION xxx BY KEY('abc') TBPARTITION yyy BY KEY('abc'), \n\t" +
            "\tDBPARTITION yyy BY KEY('def') TBPARTITION yyy BY KEY('def'), \n\t" +
            "\tDBPARTITION yyy BY KEY('gpk')\n\t" +
            ")", output);
    }

    public void test_1() throws Exception {
        String sql = "ALTER TABLE xx\n" +
            "DROP \n" +
            "EXTPARTITION (\n" +
            "DBPARTITION xxx BY KEY('abc') TBPARTITION yyy BY KEY('abc'), \n" +
            "DBPARTITION yyy BY KEY('def') TBPARTITION yyy BY KEY('def'), \n" +
            "DBPARTITION yyy BY KEY('gpk')\n" +
            ")";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(1, stmtList.size());
        SQLStatement stmt = stmtList.get(0);
        String output = stmt.toString();
        assertEquals("ALTER TABLE xx\n\t" +
            "DROP \n\t" +
            "EXTPARTITION (\n\t" +
            "\tDBPARTITION xxx BY KEY('abc') TBPARTITION yyy BY KEY('abc'), \n\t" +
            "\tDBPARTITION yyy BY KEY('def') TBPARTITION yyy BY KEY('def'), \n\t" +
            "\tDBPARTITION yyy BY KEY('gpk')\n\t" +
            ")", output);
    }
}
