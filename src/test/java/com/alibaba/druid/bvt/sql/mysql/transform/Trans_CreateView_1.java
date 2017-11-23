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
public class Trans_CreateView_1 extends TestCase {
    public void test_createView() throws Exception {
        String sql = "  CREATE OR REPLACE FORCE VIEW \"CMIS\".\"LM_LOAN_MSGINFO\" (\"LOAN_NO\", \"OD_PRCP\", \"OD_DAYS\", \"OD_TNR\", \"ENTRYCS_DAYS\") AS \n" +
                "  select loan.loan_no as loan_no,coln.od_prcp,\n" +
                "to_date((select cur_prcs_dt from s_ctrl),'YYYY-MM-DD')-to_date(loan.next_due_dt,'YYYY-MM-DD') as od_days,\n" +
                "shd.od_tnr as od_tnr,\n" +
                "to_date((select cur_prcs_dt from s_ctrl),'YYYY-MM-DD')-to_date(coln.entry_dt,'YYYY-MM-DD') as entrycs_days from lm_loan loan\n" +
                "left join (select count(*) as od_tnr,loan_no\n" +
                "  from lm_pm_shd\n" +
                "  where ps_due_dt < (select cur_prcs_dt from s_ctrl)\n" +
                "   and setl_ind = 'N'\n" +
                "   and ps_od_ind = 'Y'\n" +
                "   and ps_perd_no <> 0 group by loan_no) shd on shd.loan_no=loan.loan_no\n" +
                "left join cs_coln coln on loan.loan_no = coln.loan_no\n" +
                "where loan.LOAN_OD_IND='Y' and loan.LOAN_STS='ACTV'";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);
        List<SQLStatement> targetList = FromSubqueryResolver.resolve((SQLCreateViewStatement) stmt);
        String targetSql = SQLUtils.toSQLString(targetList, JdbcConstants.ORACLE);
        assertEquals(2, targetList.size());
    }
}
