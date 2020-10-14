package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class MySqlParameterizedOutputVisitorTest_77 extends TestCase {
    public void test_restore() throws Exception {

        String sql = "select\n" +
                "    cbm_name as cbm_name,\n" +
                "    team_name as team,\n" +
                "    month_earn as month_gaap,\n" +
                "    month_kpi as month_kpi,\n" +
                "    month_kpi - month_earn as month_gaap_gap,\n" +
                "    case when month_kpi =#{month_kpi} then 0 else  month_earn * 100 / month_kpi end as month_complete_rate,\n" +
                "    year_earn as year_gaap,\n" +
                "    year_kpi as year_kpi,\n" +
                "    year_kpi - year_earn as year_gaap_gap,\n" +
                "    case when year_kpi=0 then 0 else year_earn * 100 / year_kpi end as year_complete_rate,\n" +
                "    next_month_kpi as next_month_kpi,\n" +
                "    pipeline as pipeline,\n" +
                "    case when next_month_kpi=0 then 0 else pipeline * 100 / next_month_kpi end as next_month_forecast_rate,\n" +
                "    gaap_cunliang as gaap_cunliang,\n" +
                "    cid_cnt as cid_cnt\n" +
                "from\n" +
                "    (select\n" +
                "        cbm_id,\n" +
                "        cbm_name,\n" +
                "        team_path,\n" +
                "        team_name,\n" +
                "        sum(month_earn) as month_earn,\n" +
                "        sum(year_earn) as year_earn,\n" +
                "        sum(gaap_cunliang) as gaap_cunliang,\n" +
                "        sum(pipeline) as pipeline,\n" +
                "        sum(month_kpi) as month_kpi,\n" +
                "        sum(next_month_kpi) as next_month_kpi,\n" +
                "        sum(year_kpi) as year_kpi\n" +
                "    from\n" +
                "        (select\n" +
                "            cbm_id,\n" +
                "            cbm_name,\n" +
                "            team_path,\n" +
                "            team_name,\n" +
                "            sum(gaap_d_m) as month_earn,\n" +
                "            sum(gaap_fy) as year_earn,\n" +
                "            sum(reserve_gaap_m) as gaap_cunliang,\n" +
                "            sum(ppl_fcst) as pipeline,\n" +
                "            0 as month_kpi,\n" +
                "            0 as next_month_kpi,\n" +
                "            0 as year_kpi\n" +
                "        from dwd_aly_sop_gaap_di\n" +
                "        where stat_date = #{thedate} and \n" +
                "     case when #{sys_is_cbm}='true' then   cbm_id = #{sys_emp_id} else \n" +
                "            team_path like concat(#{sys_team_path}, '%') end \n" +
                "        group by cbm_id, cbm_name, team_path, team_name\n" +
                "        union all\n" +
                "        select\n" +
                "            assess_id as cbm_id,\n" +
                "            assess_name as cbm_name,\n" +
                "            team_path,\n" +
                "            team_name,\n" +
                "            0 as month_earn,\n" +
                "            0 as year_earn,\n" +
                "            0 as gaap_cunliang,\n" +
                "            0 as pipeline,\n" +
                "            sum(case when kpi_month = cast(#{thismonth} as bigint) then target_amount else 0 end) as month_kpi,\n" +
                "            sum(case when kpi_month = cast(#{nextmonth} as bigint) then target_amount else 0 end) as next_month_kpi,\n" +
                "            sum(case when kpi_month = cast(#{fiscalyear} as bigint) then target_amount else 0 end) as year_kpi\n" +
                "        from dim_aly_sales_kpi\n" +
                "        where (kpi_month = cast(#{thismonth} as bigint) or kpi_month = cast(#{nextmonth} as bigint) or\n" +
                "            fiscal_year = cast(#{fiscalyear} as bigint))  and assess_type = 1\n" +
                "             and target_type = 1 and \n" +
                "     case when #{sys_is_cbm}='true' then assess_id = #{sys_emp_id} else \n" +
                "            team_path like concat(#{sys_team_path}, '%') end \n" +
                "\n" +
                "        group by assess_id, assess_name, team_path, team_name\n" +
                "        ) ti_1\n" +
                "    group by cbm_id, cbm_name, team_path, team_name\n" +
                "    ) t_1\n" +
                "left outer join\n" +
                "    (select\n" +
                "        cbm_id,\n" +
                "        team_path,\n" +
                "        count(distinct cid_id) as cid_cnt\n" +
                "    from rpt_aly_sop_cust_info_d\n" +
                "    where stat_date = #{thedate} and case when #{sys_is_cbm}='true' then   cbm_id = #{sys_emp_id} else \n" +
                "            team_path like concat(#{sys_team_path}, '%') end \n" +
                "    group by cbm_id, team_path\n" +
                "    ) t_2\n" +
                "on t_1.cbm_id = t_2.cbm_id and t_1.team_path = t_2.team_path";

        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("month_kpi",1);
        parameters.put("thedate","20181115");
        parameters.put("sys_is_cbm","true");
        parameters.put("sys_emp_id",80882);
        parameters.put("sys_team_path","0.7940.227.7936.");
        parameters.put("thismonth","201811");
        parameters.put("nextmonth","201812");
        parameters.put("fiscalyear","2019");

        String rsql = ParameterizedOutputVisitorUtils.restore(sql, DbType.mysql, parameters);
        assertEquals("SELECT cbm_name AS cbm_name, team_name AS team, month_earn AS month_gaap, month_kpi AS month_kpi\n" +
                "\t, month_kpi - month_earn AS month_gaap_gap\n" +
                "\t, CASE \n" +
                "\t\tWHEN month_kpi = NULL THEN 0\n" +
                "\t\tELSE month_earn * 100 / month_kpi\n" +
                "\tEND AS month_complete_rate, year_earn AS year_gaap, year_kpi AS year_kpi, year_kpi - year_earn AS year_gaap_gap\n" +
                "\t, CASE \n" +
                "\t\tWHEN year_kpi = 0 THEN 0\n" +
                "\t\tELSE year_earn * 100 / year_kpi\n" +
                "\tEND AS year_complete_rate, next_month_kpi AS next_month_kpi, pipeline AS pipeline\n" +
                "\t, CASE \n" +
                "\t\tWHEN next_month_kpi = 0 THEN 0\n" +
                "\t\tELSE pipeline * 100 / next_month_kpi\n" +
                "\tEND AS next_month_forecast_rate, gaap_cunliang AS gaap_cunliang, cid_cnt AS cid_cnt\n" +
                "FROM (\n" +
                "\tSELECT cbm_id, cbm_name, team_path, team_name\n" +
                "\t\t, sum(month_earn) AS month_earn, sum(year_earn) AS year_earn\n" +
                "\t\t, sum(gaap_cunliang) AS gaap_cunliang, sum(pipeline) AS pipeline\n" +
                "\t\t, sum(month_kpi) AS month_kpi, sum(next_month_kpi) AS next_month_kpi\n" +
                "\t\t, sum(year_kpi) AS year_kpi\n" +
                "\tFROM (\n" +
                "\t\tSELECT cbm_id, cbm_name, team_path, team_name\n" +
                "\t\t\t, sum(gaap_d_m) AS month_earn, sum(gaap_fy) AS year_earn\n" +
                "\t\t\t, sum(reserve_gaap_m) AS gaap_cunliang, sum(ppl_fcst) AS pipeline\n" +
                "\t\t\t, 0 AS month_kpi, 0 AS next_month_kpi, 0 AS year_kpi\n" +
                "\t\tFROM dwd_aly_sop_gaap_di\n" +
                "\t\tWHERE stat_date = NULL\n" +
                "\t\t\tAND CASE \n" +
                "\t\t\t\tWHEN NULL = 'true' THEN cbm_id = NULL\n" +
                "\t\t\t\tELSE team_path LIKE concat(NULL, '%')\n" +
                "\t\t\tEND\n" +
                "\t\tGROUP BY cbm_id, cbm_name, team_path, team_name\n" +
                "\t\tUNION ALL\n" +
                "\t\tSELECT assess_id AS cbm_id, assess_name AS cbm_name, team_path, team_name, 0 AS month_earn\n" +
                "\t\t\t, 0 AS year_earn, 0 AS gaap_cunliang, 0 AS pipeline\n" +
                "\t\t\t, sum(CASE \n" +
                "\t\t\t\tWHEN kpi_month = CAST(NULL AS bigint) THEN target_amount\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS month_kpi\n" +
                "\t\t\t, sum(CASE \n" +
                "\t\t\t\tWHEN kpi_month = CAST(NULL AS bigint) THEN target_amount\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS next_month_kpi\n" +
                "\t\t\t, sum(CASE \n" +
                "\t\t\t\tWHEN kpi_month = CAST(NULL AS bigint) THEN target_amount\n" +
                "\t\t\t\tELSE 0\n" +
                "\t\t\tEND) AS year_kpi\n" +
                "\t\tFROM dim_aly_sales_kpi\n" +
                "\t\tWHERE (kpi_month = CAST(NULL AS bigint)\n" +
                "\t\t\t\tOR kpi_month = CAST(NULL AS bigint)\n" +
                "\t\t\t\tOR fiscal_year = CAST(NULL AS bigint))\n" +
                "\t\t\tAND assess_type = 1\n" +
                "\t\t\tAND target_type = 1\n" +
                "\t\t\tAND CASE \n" +
                "\t\t\t\tWHEN NULL = 'true' THEN assess_id = NULL\n" +
                "\t\t\t\tELSE team_path LIKE concat(NULL, '%')\n" +
                "\t\t\tEND\n" +
                "\t\tGROUP BY assess_id, assess_name, team_path, team_name\n" +
                "\t) ti_1\n" +
                "\tGROUP BY cbm_id, cbm_name, team_path, team_name\n" +
                ") t_1\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT cbm_id, team_path, count(DISTINCT cid_id) AS cid_cnt\n" +
                "\t\tFROM rpt_aly_sop_cust_info_d\n" +
                "\t\tWHERE stat_date = NULL\n" +
                "\t\t\tAND CASE \n" +
                "\t\t\t\tWHEN NULL = 'true' THEN cbm_id = NULL\n" +
                "\t\t\t\tELSE team_path LIKE concat(NULL, '%')\n" +
                "\t\t\tEND\n" +
                "\t\tGROUP BY cbm_id, team_path\n" +
                "\t) t_2\n" +
                "\tON t_1.cbm_id = t_2.cbm_id\n" +
                "\t\tAND t_1.team_path = t_2.team_path", rsql);

    }

}
