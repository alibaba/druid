package com.alibaba.druid.bvt.sql.mysql.transform;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.transform.FromSubqueryResolver;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by wenshao on 16/07/2017.
 */
public class Trans_CreateView_0 extends TestCase {
    public void test_createView() throws Exception {
        String sql = "CREATE OR REPLACE FORCE VIEW \"CMIS\".\"WFI_AGENT_LOGEND\" (\"INSTANCEID\", \"WFID\", \"WFSIGN\", \"NODEID\", \"NODESTARTTIME\", \"ORIGINALUSER\", \"REPLACER\", \"SYSID\", \"PK_VALUE\", \"APPL_TYPE\", \"WFI_END_ORG\", \"WFI_STATUS\") AS \n" +
                "  SELECT INSTANCEID,WFID,WFSIGN,NODEID,NODESTARTTIME,ORIGINALUSER,REPLACER,SYSID,PK_VALUE,APPL_TYPE,WFI_END_ORG,WFI_STATUS FROM (SELECT ROWID ROWIDTMP, AA.* FROM (SELECT A.INSTANCEID INSTANCEID,A.WFID WFID,A.WFSIGN WFSIGN,A.NODEID NODEID,A.NODESTARTTIME NODESTARTTIME,A.ORIGINALUSER ORIGINALUSER,A.REPLACER REPLACER,A.SYSID SYSID,B.PK_VALUE PK_VALUE,B.APPL_TYPE APPL_TYPE,B.WFI_END_ORG WFI_END_ORG,B.WFI_STATUS WFI_STATUS FROM WF_AGENT_LOG A, WFI_JOIN_HIS B WHERE A.INSTANCEID=B.INSTANCEID) AA) WHERE ROWIDTMP IN (SELECT ROWIDMAX FROM ( SELECT MAX(ROWID) ROWIDMAX,INSTANCEID FROM(SELECT A.INSTANCEID INSTANCEID FROM WF_AGENT_LOG A, WFI_JOIN_HIS B WHERE A.INSTANCEID=B.INSTANCEID) AA GROUP BY  INSTANCEID ))\n" +
                " WITH READ ONLY";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);
        List<SQLStatement> targetList = FromSubqueryResolver.resolve((SQLCreateViewStatement) stmt);
        String targetSql = SQLUtils.toSQLString(targetList, JdbcConstants.ORACLE);
        assertEquals(5, targetList.size());
    }
}
