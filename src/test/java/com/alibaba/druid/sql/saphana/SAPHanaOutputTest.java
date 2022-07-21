package com.alibaba.druid.sql.saphana;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;

/**
 * SAP HANA 语句输出测试
 *
 * @author nukiyoam
 */
public class SAPHanaOutputTest extends TestCase {

    public void testLimitOutput() {
        String sql = "SELECT t.TABLE_NAME AS tableName, t.COMMENTS AS tableComment FROM SYS.TABLES t WHERE SCHEMA_NAME = 'DBADMIN' ORDER BY TABLE_OID DESC";
        String limitSql = PagerUtils.limit(sql, DbType.sap_hana, 1, 100);
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(limitSql, DbType.sap_hana);
        SQLSelectStatement statement = (SQLSelectStatement) parser.parseStatement();
        String outputSql = SQLUtils.toSAPHanaString(statement);
        assertEquals(limitSql, outputSql);
    }
}
