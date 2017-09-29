package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class SQLSortTest extends TestCase {
    public void test_sort() throws Exception {
        String sql = "CREATE TABLE \"ECC_FND\".\"FORM_URL\" \n" +
                "   (\t\"FORM_URL_ID\" NUMBER(10,0) NOT NULL ENABLE, \n" +
                "\t\"FORM_ID\" NUMBER(10,0) NOT NULL ENABLE, \n" +
                "\t\"URL\" VARCHAR2(1024) NOT NULL ENABLE, \n" +
                "\t\"OPR_TYPE\" VARCHAR2(16) DEFAULT 'ADD' NOT NULL ENABLE, \n" +
                "\t\"ENABLE_FLAG\" VARCHAR2(10) DEFAULT 'T' NOT NULL ENABLE, \n" +
                "\t CONSTRAINT \"PK_FORM_URL12\" PRIMARY KEY (\"FORM_URL_ID\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"ECC_IDX_TSP\"  ENABLE, \n" +
                "\t CONSTRAINT \"FK_REFERE83\" FOREIGN KEY (\"FORM_ID\")\n" +
                "\t  REFERENCES \"ECC_FND\".\"FORM\" (\"FORM_ID\") ENABLE\n" +
                "   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING\n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"ECC_TSP\"; \n" +
                " \n" +
                " \n" +
                " \n" +
                " \n" +
                " \n" +
                "  CREATE TABLE \"ECC_FND\".\"FORM\" \n" +
                "   (\t\"FORM_ID\" NUMBER(10,0) NOT NULL ENABLE, \n" +
                "\t\"NAME\" VARCHAR2(32) NOT NULL ENABLE, \n" +
                "\t\"CODE\" VARCHAR2(128), \n" +
                "\t\"DESCRIPTION\" VARCHAR2(1024) NOT NULL ENABLE, \n" +
                "\t\"FORM_TYPE_ID\" NUMBER(10,0) NOT NULL ENABLE, \n" +
                "\t\"ENABLE_FLAG\" VARCHAR2(10) NOT NULL ENABLE, \n" +
                "\t CONSTRAINT \"PK_FORM7\" PRIMARY KEY (\"FORM_ID\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"ECC_IDX_TSP\"  ENABLE\n" +
                "   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING\n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"ECC_TSP\" ";

        String sortedSql = SQLUtils.sort(sql, JdbcConstants.ORACLE);
        assertEquals("CREATE TABLE \"ECC_FND\".\"FORM\" (\n" +
                "\t\"FORM_ID\" NUMBER(10, 0) NOT NULL ENABLE,\n" +
                "\t\"NAME\" VARCHAR2(32) NOT NULL ENABLE,\n" +
                "\t\"CODE\" VARCHAR2(128),\n" +
                "\t\"DESCRIPTION\" VARCHAR2(1024) NOT NULL ENABLE,\n" +
                "\t\"FORM_TYPE_ID\" NUMBER(10, 0) NOT NULL ENABLE,\n" +
                "\t\"ENABLE_FLAG\" VARCHAR2(10) NOT NULL ENABLE,\n" +
                "\tCONSTRAINT \"PK_FORM7\" PRIMARY KEY (\"FORM_ID\")\n" +
                "\t\tUSING INDEX\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tINITRANS 2\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"ECC_IDX_TSP\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tINITIAL 65536\n" +
                "\t\t\tNEXT 1048576\n" +
                "\t\t\tMINEXTENTS 1\n" +
                "\t\t\tMAXEXTENTS 2147483645\n" +
                "\t\t\tPCTINCREASE 0\n" +
                "\t\t\tFREELISTS 1\n" +
                "\t\t\tFREELIST GROUPS 1\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t)\n" +
                "\t\tCOMPUTE STATISTICS\n" +
                "\t\tENABLE\n" +
                ")\n" +
                "PCTFREE 10\n" +
                "PCTUSED 40\n" +
                "INITRANS 1\n" +
                "MAXTRANS 255\n" +
                "NOCOMPRESS\n" +
                "LOGGING\n" +
                "TABLESPACE \"ECC_TSP\"\n" +
                "STORAGE (\n" +
                "\tINITIAL 65536\n" +
                "\tNEXT 1048576\n" +
                "\tMINEXTENTS 1\n" +
                "\tMAXEXTENTS 2147483645\n" +
                "\tPCTINCREASE 0\n" +
                "\tFREELISTS 1\n" +
                "\tFREELIST GROUPS 1\n" +
                "\tBUFFER_POOL DEFAULT\n" +
                ")\n" +
                "CREATE TABLE \"ECC_FND\".\"FORM_URL\" (\n" +
                "\t\"FORM_URL_ID\" NUMBER(10, 0) NOT NULL ENABLE,\n" +
                "\t\"FORM_ID\" NUMBER(10, 0) NOT NULL ENABLE,\n" +
                "\t\"URL\" VARCHAR2(1024) NOT NULL ENABLE,\n" +
                "\t\"OPR_TYPE\" VARCHAR2(16) DEFAULT 'ADD' NOT NULL ENABLE,\n" +
                "\t\"ENABLE_FLAG\" VARCHAR2(10) DEFAULT 'T' NOT NULL ENABLE,\n" +
                "\tCONSTRAINT \"PK_FORM_URL12\" PRIMARY KEY (\"FORM_URL_ID\")\n" +
                "\t\tUSING INDEX\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tINITRANS 2\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"ECC_IDX_TSP\"\n" +
                "\t\tSTORAGE (\n" +
                "\t\t\tINITIAL 65536\n" +
                "\t\t\tNEXT 1048576\n" +
                "\t\t\tMINEXTENTS 1\n" +
                "\t\t\tMAXEXTENTS 2147483645\n" +
                "\t\t\tPCTINCREASE 0\n" +
                "\t\t\tFREELISTS 1\n" +
                "\t\t\tFREELIST GROUPS 1\n" +
                "\t\t\tBUFFER_POOL DEFAULT\n" +
                "\t\t)\n" +
                "\t\tCOMPUTE STATISTICS\n" +
                "\t\tENABLE,\n" +
                "\tCONSTRAINT \"FK_REFERE83\" FOREIGN KEY (\"FORM_ID\")\n" +
                "\t\tREFERENCES \"ECC_FND\".\"FORM\" (\"FORM_ID\") ENABLE\n" +
                ")\n" +
                "PCTFREE 10\n" +
                "PCTUSED 40\n" +
                "INITRANS 1\n" +
                "MAXTRANS 255\n" +
                "NOCOMPRESS\n" +
                "LOGGING\n" +
                "TABLESPACE \"ECC_TSP\"\n" +
                "STORAGE (\n" +
                "\tINITIAL 65536\n" +
                "\tNEXT 1048576\n" +
                "\tMINEXTENTS 1\n" +
                "\tMAXEXTENTS 2147483645\n" +
                "\tPCTINCREASE 0\n" +
                "\tFREELISTS 1\n" +
                "\tFREELIST GROUPS 1\n" +
                "\tBUFFER_POOL DEFAULT\n" +
                ");", sortedSql);
    }
}
