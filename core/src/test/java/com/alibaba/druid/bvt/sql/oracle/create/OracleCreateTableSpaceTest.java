package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableSpaceStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import org.junit.Assert;

import java.util.List;

/**
 * <a href="https://docs.oracle.com/en/database/oracle/oracle-database/23/sqlrf/CREATE-TABLESPACE.html">CREATE TABLESPACE</a>
 */
public class OracleCreateTableSpaceTest extends OracleTest {

    // basic tablespace
    public void test_0() throws Exception {
        String sql = "CREATE TABLESPACE omf_ts1";
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);
        SQLStatement statement = statementList.get(0);
        Assert.assertTrue(statement instanceof OracleCreateTableSpaceStatement);
        Assert.assertEquals("CREATE TABLESPACE omf_ts1", ((OracleCreateTableSpaceStatement) statement).getSql());

        sql = "CREATE TABLESPACE omf_ts2 DATAFILE AUTOEXTEND OFF";
        parser = new OracleStatementParser(sql);
        statementList = parser.parseStatementList();
        print(statementList);
        statement = statementList.get(0);
        Assert.assertTrue(statement instanceof OracleCreateTableSpaceStatement);
        Assert.assertEquals("CREATE TABLESPACE omf_ts2 DATAFILE AUTOEXTEND OFF", ((OracleCreateTableSpaceStatement) statement).getSql());

        sql = "CREATE TABLESPACE tbs_01 DATAFILE 'tbs_f2.dbf' SIZE 40M ONLINE";
        // parser need to update
    }

}

