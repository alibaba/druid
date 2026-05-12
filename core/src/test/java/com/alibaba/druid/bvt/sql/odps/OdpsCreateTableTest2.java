package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.parser.Token;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsCreateTableTest2 {
    @Test
    public void test_0() throws Exception {
        String sql = "CREATE TABLE sale_detail as select * from dual;";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        assertEquals("CREATE TABLE sale_detail"
                + "\nAS"
                + "\nSELECT *"
                + "\nFROM dual;", output);
    }
}
