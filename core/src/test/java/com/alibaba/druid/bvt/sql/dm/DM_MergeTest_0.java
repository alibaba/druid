package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement;
import junit.framework.TestCase;

import java.util.List;

public class DM_MergeTest_0 extends TestCase {
    public void test_0() throws Exception {
        String sql = "MERGE INTO sys_user_online a using (select count(1) co from sys_user_online " +
                "where sessionid = ?) b on (b.co <> 0) when matched then update set login_name = ?, dept_name = ?, ipaddr = ?, login_location = ?, browser = ?, os = ?, status = ?, start_timestamp = ?, last_access_time = ?, expire_time = ? where sessionid = ? when not matched then insert(SESSIONID, LOGIN_NAME, DEPT_NAME, IPADDR,LOGIN_LOCATION, BROWSER, OS, STATUS, START_TIMESTAMP, LAST_ACCESS_TIME, EXPIRE_TIME) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        SQLMergeStatement stmt = (SQLMergeStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("MERGE INTO sys_user_online a\n" +
                        "USING (\n" +
                        "\tSELECT count(1) AS co\n" +
                        "\tFROM sys_user_online\n" +
                        "\tWHERE sessionid = ?\n" +
                        ") b ON (b.co <> 0)\n" +
                        "WHEN MATCHED THEN UPDATE\n" +
                        "SET login_name = ?,\n" +
                        "\tdept_name = ?,\n" +
                        "\tipaddr = ?,\n" +
                        "\tlogin_location = ?,\n" +
                        "\tbrowser = ?,\n" +
                        "\tos = ?,\n" +
                        "\tstatus = ?,\n" +
                        "\tstart_timestamp = ?,\n" +
                        "\tlast_access_time = ?,\n" +
                        "\texpire_time = ?\n" +
                        "WHERE sessionid = ?\n" +
                        "WHEN NOT MATCHED THEN INSERT (\n" +
                        "\tSESSIONID, LOGIN_NAME, DEPT_NAME, IPADDR, LOGIN_LOCATION,\n" +
                        "\tBROWSER, OS, STATUS, START_TIMESTAMP, LAST_ACCESS_TIME,\n" +
                        "\tEXPIRE_TIME\n" +
                        ")\n" +
                        "VALUES (\n" +
                        "\t?, ?, ?, ?, ?,\n" +
                        "\t?, ?, ?, ?, ?,\n" +
                        "\t?\n" +
                        ")",
                stmt.toString());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_1() throws Exception {
        String sql = "MERGE INTO sys_user_online a using (select count(1) co from sys_user_online " +
                "where sessionid = ?) b on (b.co <> 0) when matched then update set login_name = ?, dept_name = ?, ipaddr = ?, login_location = ?, browser = ?, os = ?, status = ?, start_timestamp = ?, last_access_time = ?, expire_time = ? where sessionid = ? when not matched then insert(SESSIONID, LOGIN_NAME, DEPT_NAME, IPADDR,LOGIN_LOCATION, BROWSER, OS, STATUS, START_TIMESTAMP, LAST_ACCESS_TIME, EXPIRE_TIME) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        SQLMergeStatement stmt = (SQLMergeStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("MERGE INTO sys_user_online a\n" +
                        "USING (\n" +
                        "\tSELECT count(1) AS co\n" +
                        "\tFROM sys_user_online\n" +
                        "\tWHERE sessionid = ?\n" +
                        ") b ON (b.co <> 0)\n" +
                        "WHEN MATCHED THEN UPDATE\n" +
                        "SET login_name = ?,\n" +
                        "\tdept_name = ?,\n" +
                        "\tipaddr = ?,\n" +
                        "\tlogin_location = ?,\n" +
                        "\tbrowser = ?,\n" +
                        "\tos = ?,\n" +
                        "\tstatus = ?,\n" +
                        "\tstart_timestamp = ?,\n" +
                        "\tlast_access_time = ?,\n" +
                        "\texpire_time = ?\n" +
                        "WHERE sessionid = ?\n" +
                        "WHEN NOT MATCHED THEN INSERT (\n" +
                        "\tSESSIONID, LOGIN_NAME, DEPT_NAME, IPADDR, LOGIN_LOCATION,\n" +
                        "\tBROWSER, OS, STATUS, START_TIMESTAMP, LAST_ACCESS_TIME,\n" +
                        "\tEXPIRE_TIME\n" +
                        ")\n" +
                        "VALUES (\n" +
                        "\t?, ?, ?, ?, ?,\n" +
                        "\t?, ?, ?, ?, ?,\n" +
                        "\t?\n" +
                        ")",
                stmt.toString());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }
}
