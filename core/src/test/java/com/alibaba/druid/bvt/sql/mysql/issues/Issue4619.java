package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Issue4619 {
    protected final DbType dbType = DbType.mysql;

    @Test
    public void test_idle2() throws Exception {
        String sql = "select 1 " +
                "from t0\n" +
                "         join (WITH t1 AS (SELECT m.*, ROW_NUMBER() OVER (PARTITION BY bill_id ORDER BY modify_time DESC) AS rn\n" +
                "                           FROM bill_history AS m)\n" +
                "               SELECT *\n" +
                "               FROM t1\n" +
                "               WHERE rn = 1) bh on t0.bill_id = bh.bill_id\n" +
                "where bh.usable_status = true";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();
        assertEquals("SELECT 1\n" +
                "FROM t0\n" +
                "\tJOIN (\n" +
                "\t\tWITH t1 AS (\n" +
                "\t\t\t\tSELECT m.*, ROW_NUMBER() OVER (PARTITION BY bill_id ORDER BY modify_time DESC) AS rn\n" +
                "\t\t\t\tFROM bill_history m\n" +
                "\t\t\t)\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM t1\n" +
                "\t\tWHERE rn = 1\n" +
                "\t) bh\n" +
                "\tON t0.bill_id = bh.bill_id\n" +
                "WHERE bh.usable_status = true", stmt.toString());
    }
}
