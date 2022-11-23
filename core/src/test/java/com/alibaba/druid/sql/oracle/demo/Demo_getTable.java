package com.alibaba.druid.sql.oracle.demo;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class Demo_getTable extends TestCase {
    public void test_for_demo() throws Exception {
        String sql = " CREATE TABLE \"ZEUS\".\"ACCOUNTS_DEL_20091231\" \n" +
                "   (  \"ID\" NUMBER NOT NULL ENABLE, \n" +
                "  \"SITE\" VARCHAR2(96) NOT NULL ENABLE, \n" +
                "  \"GMT_CREATE\" DATE NOT NULL ENABLE, \n" +
                "  \"CREATOR\" VARCHAR2(96), \n" +
                "  \"GMT_MODIFIED\" DATE NOT NULL ENABLE, \n" +
                "  \"MODIFIER\" VARCHAR2(96), \n" +
                "  \"IS_DELETED\" CHAR(1), \n" +
                "  \"CONTRACT_SERIAL\" VARCHAR2(192), \n" +
                "  \"MEMBER_ID\" VARCHAR2(60), \n" +
                "  \"CUSTOMER_ID\" NUMBER, \n" +
                "  \"PRODUCT_ID\" VARCHAR2(48), \n" +
                "  \"PRODUCT_PIC_NUM\" NUMBER, \n" +
                "  \"DOMAIN_NAME\" VARCHAR2(96), \n" +
                "  \"EMAIL\" VARCHAR2(384), \n" +
                "  \"ALT_EMAIL\" VARCHAR2(384), \n" +
                "  \"AV_STATUS\" VARCHAR2(48), \n" +
                "  \"COMPANY_STATUS\" VARCHAR2(48), \n" +
                "  \"PRODUCT_STATUS\" VARCHAR2(48), \n" +
                "  \"COLUMN_STATUS\" VARCHAR2(48), \n" +
                "  \"COL_CONTENT_STATUS\" VARCHAR2(48), \n" +
                "  \"VOICE_RECORD_STATUS\" VARCHAR2(48), \n" +
                "  \"CASH_STATUS\" VARCHAR2(48), \n" +
                "  \"CONFIRM_PRODUCT_NUM\" NUMBER, \n" +
                "  \"DATUM_IMPORT\" CHAR(1), \n" +
                "  \"CUSTOMER_CHECK\" CHAR(1), \n" +
                "  \"AREA_ID_2\" NUMBER, \n" +
                "  \"OWNER_2\" VARCHAR2(96), \n" +
                "  \"DISTRIBUTE_DATE\" DATE, \n" +
                "  \"CUST_CHECK_DATE\" DATE, \n" +
                "  \"DATUM_IMPORT_DATE\" DATE, \n" +
                "  \"VALIDATE_DATE\" DATE, \n" +
                "  \"REMARK_1\" VARCHAR2(4000), \n" +
                "  \"REMARK_2\" VARCHAR2(768), \n" +
                "  \"REMARK_3\" VARCHAR2(768), \n" +
                "  \"PASSWORD\" VARCHAR2(96)\n" +
                "   ) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING\n" +
                "  STORAGE(INITIAL 131072 NEXT 131072 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"ZEUSDATA\" ";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) stmtList.get(0);
        SQLName tableName = stmt.getName();
        System.out.println(tableName.toString());
        assertEquals("\"ZEUS\".\"ACCOUNTS_DEL_20091231\"", tableName.toString());
    }
}
