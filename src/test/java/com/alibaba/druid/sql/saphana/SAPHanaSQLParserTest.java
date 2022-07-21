package com.alibaba.druid.sql.saphana;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.saphana.ast.statement.SAPHanaDeleteStatement;
import com.alibaba.druid.sql.dialect.saphana.ast.statement.SAPHanaInsertStatement;
import com.alibaba.druid.sql.dialect.saphana.ast.statement.SAPHanaSelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;

/**
 * SAP HANA SQL 解析单元测试
 *
 * @author nukiyoam
 */
public class SAPHanaSQLParserTest extends TestCase {

    /**
     * 查询语句解析测试
     */
    public void testParserSelect() {
        String sql = "SELECT t.TABLE_NAME AS tableName, t.COMMENTS AS tableComment FROM SYS.TABLES t WHERE SCHEMA_NAME = 'DBADMIN' GROUP BY t.TABLE_NAME ORDER BY t.TABLE_OID DESC";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.sap_hana);
        SQLStatement statement = parser.parseStatement();
        assertTrue(statement instanceof SQLSelectStatement);
        SQLSelectQueryBlock queryBlock = ((SQLSelectStatement) statement).getSelect().getQueryBlock();
        assertTrue(queryBlock instanceof SAPHanaSelectQueryBlock);
        assertEquals(queryBlock.getSelectList().size(), 2);
        assertNotNull(queryBlock.getWhere());
        assertNotNull(queryBlock.getOrderBy());
        assertNotNull(queryBlock.getGroupBy());
    }

    /**
     * 插入语句解析测试
     */
    public void testParserInsert() {
        String sql = "INSERT INTO VBAK (MANDT,VBELN,ERDAT) VALUES (800, '0000002406', '2021-10-13')";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.sap_hana);
        SQLStatement statement = parser.parseStatement();
        assertTrue(statement instanceof SAPHanaInsertStatement);
        assertEquals(((SAPHanaInsertStatement) statement).getTableSource().getTableName(), "VBAK");
    }

    /**
     * 删除语句解析测试
     */
    public void testParserDelete() {
        String sql = "DELETE FROM VBAK WHERE ZBUSINE = 'xxx'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.sap_hana);
        SQLStatement statement = parser.parseStatement();
        assertTrue(statement instanceof SAPHanaDeleteStatement);
    }


    /**
     * 解析分页查询语句
     */
    public void testParserLimit() {
        String sql = "SELECT t.TABLE_NAME AS tableName, t.COMMENTS AS tableComment FROM SYS.TABLES t WHERE SCHEMA_NAME = 'DBADMIN' ORDER BY TABLE_OID DESC LIMIT 100 OFFSET 0";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.sap_hana);
        SQLStatement statement = parser.parseStatement();
        assertTrue(statement instanceof SQLSelectStatement);
        SQLSelectQueryBlock queryBlock = ((SQLSelectStatement) statement).getSelect().getQueryBlock();
        assertTrue(queryBlock instanceof SAPHanaSelectQueryBlock);
        SQLLimit limit = queryBlock.getLimit();
        assertNotNull(limit);
        SQLExpr offset = limit.getOffset();
        SQLExpr rowCount = limit.getRowCount();
        assertTrue(offset instanceof SQLIntegerExpr);
        assertTrue(rowCount instanceof SQLIntegerExpr);
        assertEquals(((SQLIntegerExpr) offset).getNumber(), 0);
        assertEquals(((SQLIntegerExpr) rowCount).getNumber(), 100);
    }
}
