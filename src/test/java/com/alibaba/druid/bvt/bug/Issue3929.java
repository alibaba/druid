package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

public class Issue3929 extends TestCase {
    public void test_0() throws Exception {
        String sql = "replace into table1 (id,name) values('1','aa'),('2','bb')";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("replace into table1 (id, name)\n" +
                "values ('1', 'aa'), ('2', 'bb')", stmt.toLowerCaseString());
    }
}
