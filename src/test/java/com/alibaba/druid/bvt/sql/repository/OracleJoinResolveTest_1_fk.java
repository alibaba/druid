package com.alibaba.druid.bvt.sql.repository;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

/**
 * Created by wenshao on 03/08/2017.
 */
public class OracleJoinResolveTest_1_fk extends TestCase {
    protected SchemaRepository repository = new SchemaRepository(JdbcConstants.ORACLE);

    public void test_for_issue() throws Exception {
        String sql_1 = "   CREATE TABLE \"ECC_CPR\".\"ECC_CPR_HC_CON_HEAHERS\"     \n" +
                "   (  \n" +
                "      \"HC_CON_ID\" NUMBER(10,0) NOT NULL ENABLE,   \n" +
                "      \"HC_CONTRACT_NUMBER\" VARCHAR2(400),   \n" +
                "      \"CONTRACT_NUMBER\" VARCHAR2(40),   \n" +
                "      \"CUSTOMER_ID\" NUMBER(10,0),   \n" +
                "      \"DEPT_ID\" NUMBER(10,0),   \n" +
                "      \"END_USER\" VARCHAR2(200),   \n" +
                "      \"USER_CON_NUMBER\" VARCHAR2(200),    \n" +
                "      \"CONTRACT_STATUS\" NUMBER(10,0) DEFAULT 10,    \n" +
                "      \"HC_RECEIVE_DATE\" DATE,   \n" +
                "      \"ISSUE_CONFIRM_DATE\" DATE,    \n" +
                "      \"CONCLUDE_ADDRESS\" VARCHAR2(200),   \n" +
                "      \"CONCLUDE_DATE\" DATE,   \n" +
                "      \"CHECK_DATE\" DATE,    \n" +
                "      \"E_ORDER_NUMBER\" VARCHAR2(32),    \n" +
                "      \"FIRST_SIGN_PERSON\" VARCHAR2(30),   \n" +
                "      \"SECOND_SIGN_PERSON\" VARCHAR2(30),    \n" +
                "      \"SMALL_SIGN_DATE\" DATE,   \n" +
                "      \"TRAIN_FLAG\" CHAR(1),   \n" +
                "      \"MEMO\" VARCHAR2(200),   \n" +
                "      \"SYNC_STATUS\" CHAR(1) DEFAULT 'N',    \n" +
                "      \"SYNC_TIME\" TIMESTAMP (6),    \n" +
                "      \"CREATED_BY\" NUMBER(10,0),    \n" +
                "      \"CREATION_DATE\" TIMESTAMP (6),    \n" +
                "      \"LAST_UPDATED_BY\" NUMBER(10,0),   \n" +
                "      \"LAST_UPDATE_DATE\" TIMESTAMP (6),   \n" +
                "      \"ENABLED_FLAG\" CHAR(1),   \n" +
                "      \"CONTRACT_TYPE\" NUMBER(10,0),   \n" +
                "      \"CONTRACT_ATTRIBUTE\" VARCHAR2(10),    \n" +
                "      \"DELIVER_METHOD\" VARCHAR2(10),    \n" +
                "      \"TRANSPORT_METHOD\" VARCHAR2(10),    \n" +
                "      \"MASTER_DEPT_ID\" NUMBER(10,0),    \n" +
                "      \"CONTRACT_CLASS\" VARCHAR2(30),    \n" +
                "      \"ACTUAL_EFFECT_DATE\" DATE,    \n" +
                "      \"FORMAL_FLAG\" CHAR(1),    \n" +
                "      \"THIRD_FLAG\" CHAR(1),   \n" +
                "      \"ACCEPTANCE_FORMAL_DATE\" DATE,    \n" +
                "      \"CURRENCY_CODE\" VARCHAR2(30),   \n" +
                "      \"ORG_ID\" NUMBER(10,0) DEFAULT 1,    \n" +
                "      \"PROJECT_NUMBER\" VARCHAR2(50),    \n" +
                "      \"CONTRACT_PROPERTY\" VARCHAR2(10),   \n" +
                "      \"MARKET_PROPERTY_ID\" VARCHAR2(11),    \n" +
                "      \"SUBMIT_TIME\" TIMESTAMP (6),    \n" +
                "      \"PRODUCT_BIG_TYPE\" NUMBER(10,0),    \n" +
                "      \"CONTRACT_SUM_CORPORATION\" VARCHAR2(200),   \n" +
                "      \"CONTRACT_SUM_MONEY\" NUMBER(18,4),    \n" +
                "      \"CONTRACT_MONEY_TYPE\" NUMBER(10,0),   \n" +
                "      \"TAKE_EFFECT_CONDITION\" VARCHAR2(200),    \n" +
                "      \"PROJECT_EST_OID\" NUMBER(10,0),   \n" +
                "      \"NEW_FLAG\" VARCHAR2(1) DEFAULT 'N',   \n" +
                "      \"LEGAL_ENTITY_ID\" NUMBER(10,0),   \n" +
                "      \"PO_FLAG\" VARCHAR2(1) DEFAULT 'N',    \n" +
                "      \"EFFECT_DEPT_ID\" NUMBER(10,0),    \n" +
                "      \"PROJECT_ID\" NUMBER(12,0),    \n" +
                "      \"SELLER\" VARCHAR2(60),    \n" +
                "      \"BUYER\" VARCHAR2(60),   \n" +
                "      \"BUYER_TYPE\" VARCHAR2(5),   \n" +
                "      \"SIGN_TYPE\" VARCHAR2(5),    \n" +
                "      \"TRANS_CON_ID\" VARCHAR2(200),   \n" +
                "      \"TRANS_CON_NUMBER\" VARCHAR2(1000),    \n" +
                "      \"CON_HEADER_ID\" NUMBER(10,0),   \n" +
                "      \"SUBMIT_FLAG\" VARCHAR2(1),    \n" +
                "      \"APPROVE_DATE\" TIMESTAMP (6),   \"CURR_HIS_ID\" NUMBER,   \"LAST_UPD_HIS_ID\" NUMBER,   \n" +
                "      \"TRANS_CON_FLAG\" VARCHAR2(1),   \"SUPPLY_FLAG\" VARCHAR2(1),    \"SUPPLY_PATH\" VARCHAR2(300),    \n" +
                "      \"CON_TYPE\" NUMBER(2,0),   \"TRANS_TYPE\" NUMBER(2,0),   \n" +
                "      \"TRANS_SUBCOMPANY\" VARCHAR2(100),   \"SIGN_CUSTOMER\" VARCHAR2(100),    \"MTO\" NUMBER(3,0),    \n" +
                "      \"NEED_SURVEY\" VARCHAR2(1),    \"TRANS_CURRENCY_CODE\" VARCHAR2(30),   \"TRANS_CONTRACT_MONEY\" NUMBER(15,2),    \n" +
                "      \"OPERATING_DEPARTMENT\" VARCHAR2(500),   \"SIEBLE_ID\" VARCHAR2(15),   \"ORDER_TYPE\" VARCHAR2(50),    \n" +
                "      \"SIEBLE_SALE_CONTRACT_ID\" VARCHAR2(15),   \"ECC_SALE_CONTRACT_ID\" NUMBER(10,0),    \"SALE_CONTRACT_NO\" VARCHAR2(40),    \n" +
                "      \"MID_TRANS_CURRENCY_CODE\" VARCHAR2(30),   \"PROJECT_TASK_ID\" VARCHAR2(15),   \"SALE_MODEL_ONE\" VARCHAR2(30),    \n" +
                "      \"SALE_MODEL_TWO\" VARCHAR2(30),    \"CONTRACTHEAD_TYPE\" VARCHAR2(30),   \n" +
                "      \"PO_RECEIVE_DATE\" DATE,   \"TRANS_CODE\" VARCHAR2(10),    \"TRANS_MONEY4\" VARCHAR2(30),    \n" +
                "      \"CALC_CON_ID\" VARCHAR2(10),   \"ELECTRONPO\" VARCHAR2(1),   \"TRANS_MONEY\" NUMBER(22,4),   \n" +
                "      \"IS_COUPON\" VARCHAR2(1),    \"TRADE_TERM\" VARCHAR2(50),    \"IS_TAX\" VARCHAR2(1),   \n" +
                "      \"TAX_CATEGORIES\" VARCHAR2(50),    \"TAX_CODE\" VARCHAR2(50),    \"PACT_CURRENCY\" VARCHAR2(10),   \n" +
                "      \"IS_AUTO_EMERGE\" VARCHAR2(1),   \"SEND_MAIL_TIME\" TIMESTAMP (6),   \"CREATE_TYPE\" VARCHAR2(10),    \n" +
                "      CONSTRAINT \"P_ECC_CPR_HC_CON_H\" PRIMARY KEY (\"HC_CON_ID\")   \n" +
                "      USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS    \n" +
                "      STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645   \n" +
                "        PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)   \n" +
                "      TABLESPACE \"ECC_CPR_IDX_TSP\"  ENABLE    \n" +
                ") PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING   \n" +
                "   STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645   \n" +
                "    PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)   \n" +
                "   TABLESPACE \"ECC_CPR_TSP\";\n"
                ;

        String sql_2 = "\nCREATE TABLE \"ECC_CPR\".\"ECC_CPR_HC_CON_PERSON\" (\n" +
                "\t\"LAST_UPDATE_DATE\" TIMESTAMP(6),\n" +
                "\t\"LAST_UPDATED_BY\" NUMBER,\n" +
                "\t\"CREATION_DATE\" TIMESTAMP(6),\n" +
                "\t\"CREATED_BY\" NUMBER,\n" +
                "\t\"LAST_UPDATE_LOGIN\" NUMBER,\n" +
                "\t\"CON_PERSON_ID\" NUMBER NOT NULL ENABLE,\n" +
                "\t\"HC_CON_ID\" NUMBER NOT NULL ENABLE,\n" +
                "\t\"PERSON_CLASS\" VARCHAR2(30),\n" +
                "\t\"CONTACT_NAME\" VARCHAR2(120),\n" +
                "\t\"CONTACT_TEL\" VARCHAR2(80),\n" +
                "\t\"SUBMIT_FLAG\" CHAR(1),\n" +
                "\t\"SYNC_STATUS\" CHAR(1) DEFAULT 'N',\n" +
                "\t\"SYNC_TIME\" TIMESTAMP(6),\n" +
                "\t\"ENABLED_FLAG\" CHAR(1),\n" +
                "\tCONSTRAINT \"ECC_CPR_HC_CON_PERSON_PK\" PRIMARY KEY (\"CON_PERSON_ID\")\n" +
                "\t\tUSING INDEX\n" +
                "\t\tPCTFREE 10\n" +
                "\t\tINITRANS 2\n" +
                "\t\tMAXTRANS 255\n" +
                "\t\tTABLESPACE \"ECC_CPR_IDX_TSP\"\n" +
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
                "\tCONSTRAINT \"ECC_CPR_HC_CON_PERSON_FK\" FOREIGN KEY (\"HC_CON_ID\")\n" +
                "\t\tREFERENCES \"ECC_CPR\".\"ECC_CPR_HC_CON_HEAHERS\" (\"HC_CON_ID\") ENABLE\n" +
                ")\n" +
                "PCTFREE 10\n" +
                "PCTUSED 40\n" +
                "INITRANS 1\n" +
                "MAXTRANS 255\n" +
                "NOCOMPRESS\n" +
                "LOGGING\n" +
                "TABLESPACE \"ECC_CPR_TSP\"\n" +
                "STORAGE (\n" +
                "\tINITIAL 65536\n" +
                "\tNEXT 1048576\n" +
                "\tMINEXTENTS 1\n" +
                "\tMAXEXTENTS 2147483645\n" +
                "\tPCTINCREASE 0\n" +
                "\tFREELISTS 1\n" +
                "\tFREELIST GROUPS 1\n" +
                "\tBUFFER_POOL DEFAULT\n" +
                ");";

        repository.console(sql_1);


        OracleCreateTableStatement stmt = (OracleCreateTableStatement) SQLParserUtils.createSQLStatementParser(sql_2, JdbcConstants.ORACLE).parseStatement();

        repository.resolve(stmt);
    }
}
