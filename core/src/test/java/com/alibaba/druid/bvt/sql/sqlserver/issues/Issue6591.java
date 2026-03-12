package com.alibaba.druid.bvt.sql.sqlserver.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Fix SQL Server ALTER VIEW parsing when AS keyword is omitted before SELECT.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6591">Issue #6591</a>
 */
public class Issue6591 {
    @Test
    public void test_alter_view_without_as() {
        String sql = "ALTER VIEW [dbo].[V_CollectInstock]\n"
                + "    SELECT a.Supplier AS 'Customer',\n"
                + "           a.VoucherCode AS VoucherCode,\n"
                + "           '采购入库' AS TYPE\n"
                + "    FROM T_Oper_InStockVoucher a";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.sqlserver);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        assertNotNull(stmtList.get(0));
    }

    @Test
    public void test_alter_view_with_as() {
        String sql = "ALTER VIEW [dbo].[V_CollectInstock] AS\n"
                + "    SELECT a.Supplier AS 'Customer',\n"
                + "           a.VoucherCode AS VoucherCode,\n"
                + "           '采购入库' AS TYPE\n"
                + "    FROM T_Oper_InStockVoucher a";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.sqlserver);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        assertNotNull(stmtList.get(0));
    }

    @Test
    public void test_create_view_without_as() {
        String sql = "CREATE VIEW [dbo].[V_CollectInstock]\n"
                + "    SELECT a.Supplier AS 'Customer',\n"
                + "           a.VoucherCode AS VoucherCode\n"
                + "    FROM T_Oper_InStockVoucher a";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.sqlserver);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        assertNotNull(stmtList.get(0));
    }
}
