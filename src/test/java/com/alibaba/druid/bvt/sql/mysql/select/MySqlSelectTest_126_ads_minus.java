package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_126_ads_minus extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select q.case_id as case_id\n" +
                "from (\n" +
                "\tselect p.id as case_id,p.gmt_create as create_time,p.member_id as member_id\n" +
                "\t\t,b.biz_2_name as biz_2_name,b.biz_3_name as biz_3_name,b.biz_4_name as biz_4_name,d.deal_nick as deal_nick,d.ad_account as ad_account,d.org_name as dept_name\n" +
                "\t\t,case when d.org_level=7 then org_6_name when d.org_level=6 then org_5_name when d.org_level=5 then org_4_name when d.org_level=4 then org_3_name when d.org_level=3 then org_2_name when d.org_level=2 then org_1_name else '' end as group_name\n" +
                "\t\t,d.company_id as company_id,d.company_type_id as company_type_id,p.biz_type as biz_type \n" +
                "\tfrom tpp_case_phone p \n" +
                "\t\tjoin dim_tb_crm_call_biz b on p.biz_type=b.biz_id \n" +
                "\t\tjoin dim_tb_crm_deal d on p.owner=d.deal_id \n" +
                "\twhere p.end_time is null  \n" +
                "\t\tand p.owner <> 0 and d.ad_account is not null and (d.org_2_id='109704' \n" +
                "\t\t\tor d.org_2_id='111059' or d.org_2_id='110989' or d.org_2_id='110694' or d.org_2_id='110301' or d.org_2_id='110296') \n" +
                "\t\tand p.gmt_create >= '2018-02-26 13:11:00' and p.gmt_create <= '2018-02-26 13:12:58' ) q  \n" +
                "\t\twhere q.case_id in ((select case_id from tpp_action_case where action_code=999990 ) MINUS (select case_id from tpp_action_case where action_code=999997))";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT q.case_id AS case_id\n" +
                "FROM (\n" +
                "\tSELECT p.id AS case_id, p.gmt_create AS create_time, p.member_id AS member_id, b.biz_2_name AS biz_2_name, b.biz_3_name AS biz_3_name\n" +
                "\t\t, b.biz_4_name AS biz_4_name, d.deal_nick AS deal_nick, d.ad_account AS ad_account, d.org_name AS dept_name\n" +
                "\t\t, CASE \n" +
                "\t\t\tWHEN d.org_level = 7 THEN org_6_name\n" +
                "\t\t\tWHEN d.org_level = 6 THEN org_5_name\n" +
                "\t\t\tWHEN d.org_level = 5 THEN org_4_name\n" +
                "\t\t\tWHEN d.org_level = 4 THEN org_3_name\n" +
                "\t\t\tWHEN d.org_level = 3 THEN org_2_name\n" +
                "\t\t\tWHEN d.org_level = 2 THEN org_1_name\n" +
                "\t\t\tELSE ''\n" +
                "\t\tEND AS group_name, d.company_id AS company_id, d.company_type_id AS company_type_id, p.biz_type AS biz_type\n" +
                "\tFROM tpp_case_phone p\n" +
                "\t\tJOIN dim_tb_crm_call_biz b ON p.biz_type = b.biz_id\n" +
                "\t\tJOIN dim_tb_crm_deal d ON p.owner = d.deal_id\n" +
                "\tWHERE p.end_time IS NULL\n" +
                "\t\tAND p.owner <> 0\n" +
                "\t\tAND d.ad_account IS NOT NULL\n" +
                "\t\tAND (d.org_2_id = '109704'\n" +
                "\t\t\tOR d.org_2_id = '111059'\n" +
                "\t\t\tOR d.org_2_id = '110989'\n" +
                "\t\t\tOR d.org_2_id = '110694'\n" +
                "\t\t\tOR d.org_2_id = '110301'\n" +
                "\t\t\tOR d.org_2_id = '110296')\n" +
                "\t\tAND p.gmt_create >= '2018-02-26 13:11:00'\n" +
                "\t\tAND p.gmt_create <= '2018-02-26 13:12:58'\n" +
                ") q\n" +
                "WHERE q.case_id IN (\n" +
                "\tSELECT case_id\n" +
                "\tFROM tpp_action_case\n" +
                "\tWHERE action_code = 999990\n" +
                "\tMINUS\n" +
                "\t(SELECT case_id\n" +
                "\tFROM tpp_action_case\n" +
                "\tWHERE action_code = 999997)\n" +
                ")", stmt.toString());
    }


}