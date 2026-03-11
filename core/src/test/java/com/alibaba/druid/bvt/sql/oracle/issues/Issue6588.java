package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @see <a href="https://github.com/alibaba/druid/issues/6588">Issue 6588: Oracle NOT NULL ENABLE PRIMARY KEY</a>
 */
public class Issue6588 {
    @Test
    public void test_oracle_enable_primary_key() {
        String sql = "CREATE TABLE TB_AC_AR_DCUSTCHECKACC_CUSTCATEGORY\n"
                + "(\n"
                + "    PK             NUMBER(19, 0) NOT NULL ENABLE PRIMARY KEY,\n"
                + "    FK             NUMBER(19, 0),\n"
                + "    VERSION        NUMBER       DEFAULT 0,\n"
                + "    LINEID            NUMBER(10, 0) DEFAULT -1,\n"
                + "    BRANCHID       VARCHAR2(3) default 'ZDA',\n"
                + "    DELETEFLAG     NUMBER(1, 0) DEFAULT 0,\n"
                + "    CREATETIME     DATE,\n"
                + "    LASTMODIFYTIME DATE,\n"
                + "    CUSTTYPE       VARCHAR2(100),\n"
                + "    CUSTTYPEID     VARCHAR2(50),\n"
                + "    CUSTBIZTYPE    VARCHAR2(100),\n"
                + "    CUSTBIZTYPEID  VARCHAR2(50),\n"
                + "    REMARK         VARCHAR2(100)\n"
                + ");\n"
                + "COMMENT ON COLUMN TB_AC_AR_DCUSTCHECKACC_CUSTCATEGORY.CUSTTYPE IS '客户分类-对账客户分类KHFLDZ';\n"
                + "COMMENT ON COLUMN TB_AC_AR_DCUSTCHECKACC_CUSTCATEGORY.CUSTTYPEID IS '客户分类编码';\n"
                + "COMMENT ON COLUMN TB_AC_AR_DCUSTCHECKACC_CUSTCATEGORY.CUSTBIZTYPE IS '客户业务类别-取字典客户业务分类CustBizType';\n"
                + "COMMENT ON COLUMN TB_AC_AR_DCUSTCHECKACC_CUSTCATEGORY.CUSTBIZTYPEID IS '客户业务类别编码';\n"
                + "COMMENT ON COLUMN TB_AC_AR_DCUSTCHECKACC_CUSTCATEGORY.REMARK IS '备注';\n"
                + "COMMENT ON TABLE TB_AC_AR_DCUSTCHECKACC_CUSTCATEGORY IS '下游对账客户分类表';";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(7, statementList.size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.oracle);
    }

    @Test
    public void test_oracle_enable_unique() {
        String sql = "CREATE TABLE TEST_TABLE (\n"
                + "    ID NUMBER(19, 0) NOT NULL ENABLE UNIQUE,\n"
                + "    NAME VARCHAR2(100)\n"
                + ")";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.oracle);
    }
}
