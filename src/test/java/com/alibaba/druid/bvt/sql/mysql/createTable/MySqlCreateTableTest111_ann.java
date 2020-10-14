package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlCreateTableTest111_ann extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table t1 (\n" +
                "fid bigint, \n" +
                "feature array<float> ANNINDEX (type='FAST_INDEX,FLAT', distance='DotProduct', rttype='FLAT')" +
                ")";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ALIYUN_DRDS);
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(2, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE t1 (\n" +
                "\tfid bigint,\n" +
                "\tfeature ARRAY<float> ANNINDX (type = 'FLAT,FLAT_INDEX', DISTANCE = 'DotProduct', RTTYPE = 'FLAT')\n" +
                ")", stmt.toString());
    }
}