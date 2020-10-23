/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;


public class OracleSelectTest134 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select xu.employee_no\n" +
                ",xu.user_name\n" +
                ",head.tr_head_id 单号\n" +
                ",head.submit_date\n" +
                ",head.AMOUNT_SUBMITTED \n" +
                ",head.AMOUNT_APPROVED \n" +
                ",head.remark\n" +
                ",head.ou_code\n" +
                ",head.change_ou_reason\n" +
                ",'差旅报销' 单据类型\n" +
                ",flog.operation_userid 审批人工号\n" +
                ",xu2.user_name 审批人姓名\n" +
                ",flog.operation_type 审批类型\n" +
                ",flog.comments 审批评论\n" +
                ",flog.workno 原审批人工号\n" +
                ",flog.taskname 审批任务类型\n" +
                "from bpm_tes_tr_head head join xteam_user xu \n" +
                "on head.submit_xuser_id = xu.id\n" +
                "join WORKFLOW_BILL bill\n" +
                "on bill.business_id = head.tr_head_id and bill.workflow_type = 'SYS.FORM.009'\n" +
                "join WORKFLOW_LOG  flog\n" +
                "on bill.id = flog.bill_id\n" +
                "join xteam_user xu2\n" +
                "on flog.operation_userid = xu2.employee_no and xu2.organization_id = 1\n" +
                "where head.ou_code = 'R88' \n" +
                "and head.submit_date >= to_date('2017-01-01 00:00:00','yyyy-MM-dd hh24:mi:ss')\n" +
                "and head.submit_date <= to_date('2018-07-18 00:00:00','yyyy-MM-dd hh24:mi:ss')\n" +
                "and xu.employee_no in ('033346','006100','051890','041316','118747','151117','083217')\n" +
                "order by head.tr_head_id,flog.gmt_create\n" +
                "\n" +
                "union all\n" +
                "\n" +
                "select xu.employee_no\n" +
                ",xu.user_name\n" +
                ",head.er_head_id 单号\n" +
                ",head.submit_date\n" +
                ",head.AMOUNT_SUBMITTED \n" +
                ",head.AMOUNT_APPROVED \n" +
                ",head.remark\n" +
                ",head.ou_code\n" +
                ",head.change_ou_reason\n" +
                ",'费用报销' 单据类型\n" +
                ",flog.operation_userid 审批人工号\n" +
                ",xu2.user_name 审批人姓名\n" +
                ",flog.operation_type 审批类型\n" +
                ",flog.comments 审批评论\n" +
                ",flog.workno 原审批人工号\n" +
                ",flog.taskname 审批任务类型\n" +
                "from bpm_tes_tr_head head join xteam_user xu \n" +
                "on head.submit_xuser_id = xu.id\n" +
                "join WORKFLOW_BILL bill\n" +
                "on bill.business_id = head.tr_head_id and bill.workflow_type = 'SYS.FORM.009'\n" +
                "join WORKFLOW_LOG  flog\n" +
                "on bill.id = flog.bill_id\n" +
                "join xteam_user xu2\n" +
                "on flog.operation_userid = xu2.employee_no and xu2.organization_id = 1\n" +
                "where head.ou_code = 'R88' \n" +
                "and head.submit_date >= to_date('2017-01-01 00:00:00','yyyy-MM-dd hh24:mi:ss')\n" +
                "and head.submit_date <= to_date('2018-07-18 00:00:00','yyyy-MM-dd hh24:mi:ss')\n" +
                "and xu.employee_no in ('033346','006100','051890','041316','118747','151117','083217')\n" +
                "order by head.tr_head_id,flog.gmt_create";
//        System.out.println(sql);

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT xu.employee_no, xu.user_name, head.tr_head_id AS 单号, head.submit_date, head.AMOUNT_SUBMITTED\n" +
                "\t, head.AMOUNT_APPROVED, head.remark, head.ou_code, head.change_ou_reason, '差旅报销' AS 单据类型\n" +
                "\t, flog.operation_userid AS 审批人工号, xu2.user_name AS 审批人姓名, flog.operation_type AS 审批类型, flog.comments AS 审批评论, flog.workno AS 原审批人工号\n" +
                "\t, flog.taskname AS 审批任务类型\n" +
                "FROM bpm_tes_tr_head head\n" +
                "JOIN xteam_user xu ON head.submit_xuser_id = xu.id \n" +
                "JOIN WORKFLOW_BILL bill ON bill.business_id = head.tr_head_id\n" +
                "\tAND bill.workflow_type = 'SYS.FORM.009' \n" +
                "JOIN WORKFLOW_LOG flog ON bill.id = flog.bill_id \n" +
                "\tJOIN xteam_user xu2 ON flog.operation_userid = xu2.employee_no\n" +
                "\tAND xu2.organization_id = 1 \n" +
                "WHERE head.ou_code = 'R88'\n" +
                "\tAND head.submit_date >= to_date('2017-01-01 00:00:00', 'yyyy-MM-dd hh24:mi:ss')\n" +
                "\tAND head.submit_date <= to_date('2018-07-18 00:00:00', 'yyyy-MM-dd hh24:mi:ss')\n" +
                "\tAND xu.employee_no IN (\n" +
                "\t\t'033346', \n" +
                "\t\t'006100', \n" +
                "\t\t'051890', \n" +
                "\t\t'041316', \n" +
                "\t\t'118747', \n" +
                "\t\t'151117', \n" +
                "\t\t'083217'\n" +
                "\t)\n" +
                "ORDER BY head.tr_head_id, flog.gmt_create\n" +
                "UNION ALL\n" +
                "SELECT xu.employee_no, xu.user_name, head.er_head_id AS 单号, head.submit_date, head.AMOUNT_SUBMITTED\n" +
                "\t, head.AMOUNT_APPROVED, head.remark, head.ou_code, head.change_ou_reason, '费用报销' AS 单据类型\n" +
                "\t, flog.operation_userid AS 审批人工号, xu2.user_name AS 审批人姓名, flog.operation_type AS 审批类型, flog.comments AS 审批评论, flog.workno AS 原审批人工号\n" +
                "\t, flog.taskname AS 审批任务类型\n" +
                "FROM bpm_tes_tr_head head\n" +
                "JOIN xteam_user xu ON head.submit_xuser_id = xu.id \n" +
                "JOIN WORKFLOW_BILL bill ON bill.business_id = head.tr_head_id\n" +
                "\tAND bill.workflow_type = 'SYS.FORM.009' \n" +
                "JOIN WORKFLOW_LOG flog ON bill.id = flog.bill_id \n" +
                "\tJOIN xteam_user xu2 ON flog.operation_userid = xu2.employee_no\n" +
                "\tAND xu2.organization_id = 1 \n" +
                "WHERE head.ou_code = 'R88'\n" +
                "\tAND head.submit_date >= to_date('2017-01-01 00:00:00', 'yyyy-MM-dd hh24:mi:ss')\n" +
                "\tAND head.submit_date <= to_date('2018-07-18 00:00:00', 'yyyy-MM-dd hh24:mi:ss')\n" +
                "\tAND xu.employee_no IN (\n" +
                "\t\t'033346', \n" +
                "\t\t'006100', \n" +
                "\t\t'051890', \n" +
                "\t\t'041316', \n" +
                "\t\t'118747', \n" +
                "\t\t'151117', \n" +
                "\t\t'083217'\n" +
                "\t)\n" +
                "ORDER BY head.tr_head_id, flog.gmt_create", stmt.toString());

        assertEquals("select xu.employee_no, xu.user_name, head.tr_head_id as 单号, head.submit_date, head.AMOUNT_SUBMITTED\n" +
                "\t, head.AMOUNT_APPROVED, head.remark, head.ou_code, head.change_ou_reason, '差旅报销' as 单据类型\n" +
                "\t, flog.operation_userid as 审批人工号, xu2.user_name as 审批人姓名, flog.operation_type as 审批类型, flog.comments as 审批评论, flog.workno as 原审批人工号\n" +
                "\t, flog.taskname as 审批任务类型\n" +
                "from bpm_tes_tr_head head\n" +
                "join xteam_user xu on head.submit_xuser_id = xu.id \n" +
                "join WORKFLOW_BILL bill on bill.business_id = head.tr_head_id\n" +
                "\tand bill.workflow_type = 'SYS.FORM.009' \n" +
                "join WORKFLOW_LOG flog on bill.id = flog.bill_id \n" +
                "\tjoin xteam_user xu2 on flog.operation_userid = xu2.employee_no\n" +
                "\tand xu2.organization_id = 1 \n" +
                "where head.ou_code = 'R88'\n" +
                "\tand head.submit_date >= to_date('2017-01-01 00:00:00', 'yyyy-MM-dd hh24:mi:ss')\n" +
                "\tand head.submit_date <= to_date('2018-07-18 00:00:00', 'yyyy-MM-dd hh24:mi:ss')\n" +
                "\tand xu.employee_no in (\n" +
                "\t\t'033346', \n" +
                "\t\t'006100', \n" +
                "\t\t'051890', \n" +
                "\t\t'041316', \n" +
                "\t\t'118747', \n" +
                "\t\t'151117', \n" +
                "\t\t'083217'\n" +
                "\t)\n" +
                "order by head.tr_head_id, flog.gmt_create\n" +
                "union all\n" +
                "select xu.employee_no, xu.user_name, head.er_head_id as 单号, head.submit_date, head.AMOUNT_SUBMITTED\n" +
                "\t, head.AMOUNT_APPROVED, head.remark, head.ou_code, head.change_ou_reason, '费用报销' as 单据类型\n" +
                "\t, flog.operation_userid as 审批人工号, xu2.user_name as 审批人姓名, flog.operation_type as 审批类型, flog.comments as 审批评论, flog.workno as 原审批人工号\n" +
                "\t, flog.taskname as 审批任务类型\n" +
                "from bpm_tes_tr_head head\n" +
                "join xteam_user xu on head.submit_xuser_id = xu.id \n" +
                "join WORKFLOW_BILL bill on bill.business_id = head.tr_head_id\n" +
                "\tand bill.workflow_type = 'SYS.FORM.009' \n" +
                "join WORKFLOW_LOG flog on bill.id = flog.bill_id \n" +
                "\tjoin xteam_user xu2 on flog.operation_userid = xu2.employee_no\n" +
                "\tand xu2.organization_id = 1 \n" +
                "where head.ou_code = 'R88'\n" +
                "\tand head.submit_date >= to_date('2017-01-01 00:00:00', 'yyyy-MM-dd hh24:mi:ss')\n" +
                "\tand head.submit_date <= to_date('2018-07-18 00:00:00', 'yyyy-MM-dd hh24:mi:ss')\n" +
                "\tand xu.employee_no in (\n" +
                "\t\t'033346', \n" +
                "\t\t'006100', \n" +
                "\t\t'051890', \n" +
                "\t\t'041316', \n" +
                "\t\t'118747', \n" +
                "\t\t'151117', \n" +
                "\t\t'083217'\n" +
                "\t)\n" +
                "order by head.tr_head_id, flog.gmt_create", stmt.toLowerCaseString());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(4, visitor.getTables().size());
        assertEquals(25, visitor.getColumns().size());
        assertEquals(14, visitor.getConditions().size());
        assertEquals(4, visitor.getRelationships().size());
        assertEquals(4, visitor.getOrderByColumns().size());

//        assertTrue(visitor.containsColumn("srm1.CONSIGNEE_ADDRESS", "id"));
    }

}