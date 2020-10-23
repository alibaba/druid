package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
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
        SQLMergeStatement stmt = (SQLMergeStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("MERGE INTO sys_user_online a\n" +
                "USING (\n" +
                "\tSELECT count(1) AS co\n" +
                "\tFROM sys_user_online\n" +
                "\tWHERE sessionid = ?\n" +
                ") b ON (b.co <> 0) \n" +
                "WHEN MATCHED THEN UPDATE SET login_name = ?, dept_name = ?, ipaddr = ?, login_location = ?, browser = ?, os = ?, status = ?, start_timestamp = ?, last_access_time = ?, expire_time = ?\n" +
                "\tWHERE sessionid = ?\n" +
                "WHEN NOT MATCHED THEN INSERT (SESSIONID, LOGIN_NAME, DEPT_NAME, IPADDR, LOGIN_LOCATION, BROWSER, OS, STATUS, START_TIMESTAMP, LAST_ACCESS_TIME, EXPIRE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", stmt.toString());


    }
}
