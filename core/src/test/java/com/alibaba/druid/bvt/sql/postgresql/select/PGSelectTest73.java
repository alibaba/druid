package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest73 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select a.*, avg(a.nv_roe_lag) over() as nv_roe_avg, avg(a.idx_roe_lag) over (order by a.stat_date) as idx_roe_avg,avg(a.if300_roe_lag) over() as if300_roe_avg, avg(a.ih50_roe_lag) over() as ih50_roe_avg,avg(a.ic500_roe_lag) over() as ic500_roe_avg,\n" +
                "max(a.nv_retrace) over (order by a.stat_date) as nv_retrace_max, max(a.idx_retrace) over (order by a.stat_date) as idx_retrace_max,max(a.if300_retrace) over (order by a.stat_date) as if300_retrace_max,max(a.ih50_retrace) over (order by a.stat_date) as ih50_retrace_max, max(a.ic500_retrace) over (order by a.stat_date) as ic500_retrace_max,\n" +
                "case when a.nv_flag>=0 then sum(1) over (order by a.stat_date) else sum(0) over (order by a.stat_date) end as nv_flag_up,count(1) filter(where a.nv_flag<=0) over (order by a.stat_date) as nv_flag_down,\n" +
                "count(1) filter(where a.idx_flag>=0) over (order by a.stat_date) as idx_flag_up,count(1) filter(where a.idx_flag<=0) over (order by a.stat_date) as idx_flag_down,\n" +
                "count(1) filter(where a.if300_flag>=0) over (order by a.stat_date) as if300_flag_up,count(1) filter(where a.if300_flag<=0) over (order by a.stat_date) as if300_flag_down,\n" +
                "count(1) filter(where a.ih50_flag>=0) over (order by a.stat_date) as ih50_flag_up,count(1) filter(where a.ih50_flag<=0) over (order by a.stat_date) as ih50_flag_down,\n" +
                "count(1) filter(where a.ic500_flag>=0) over (order by a.stat_date) as ic500_flag_up,count(1) filter(where a.ic500_flag<=0) over (order by a.stat_date) as ic500_flag_down,\n" +
                "count(1) filter(where a.idx_roe_lag>0) over (order by a.stat_date) as idx_win_cnt,count(1) filter(where a.idx_roe_lag>a.idx_roe_lag) over (order by a.stat_date) as idx_win_idx_cnt,\n" +
                "count(1) filter(where a.nv_roe_lag>0) over (order by a.stat_date) as nv_win_cnt,count(1) filter(where a.nv_roe_lag>a.idx_roe_lag) over (order by a.stat_date) as nv_win_idx_cnt,\n" +
                "count(1) filter(where a.if300_roe_lag>0) over (order by a.stat_date) as if300_win_cnt,count(1) filter(where a.if300_roe_lag>a.idx_roe_lag) over (order by a.stat_date) as if300_win_idx_cnt,\n" +
                "count(1) filter(where a.ih50_roe_lag>0) over (order by a.stat_date) as ih50_win_cnt,count(1) filter(where a.ih50_roe_lag>a.idx_roe_lag) over (order by a.stat_date) as ih50_win_idx_cnt,\n" +
                "count(1) filter(where a.ic500_roe_lag>0) over (order by a.stat_date) as ic500_win_cnt,count(1) filter(where a.ic500_roe_lag>a.idx_roe_lag) over (order by a.stat_date) as ic500_win_idx_cnt,\n" +
                "--根据所有输入点(X,Y)利用最小二乘法计算一个线性方程式。然后返回该直线的斜率\n" +
                "regr_slope(a.nv_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as nv_beta,regr_slope(a.idx_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as idx_beta,regr_slope(a.if300_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as if300_beta,regr_slope(a.ih50_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as ih50_beta,regr_slope(a.ic500_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as ic500_beta,\n" +
                "--根据所有输入点(X,Y)利用最小二乘法计算一个线性方程式。然后返回该直线的Y轴截距\n" +
                "regr_intercept(a.nv_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as nv_alpha,regr_intercept(a.idx_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as idx_alpha,regr_intercept(a.if300_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as if300_alpha,regr_intercept(a.ih50_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as ih50_alpha,regr_intercept(a.ic500_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as ic500_alpha,\n" +
                "--相关系数\n" +
                "corr(a.nv_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as nv_corr,corr(a.idx_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as idx_corr,corr(a.if300_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as if300_corr,corr(a.ih50_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as ih50_corr,corr(a.ic500_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as ic500_corr,\n" +
                "--样本标准差\n" +
                "stddev_samp(a.nv_roe_lag) over (order by a.stat_date) as nv_stddev,stddev_samp(a.idx_roe_lag) over (order by a.stat_date) as idx_stddev,stddev_samp(a.if300_roe_lag) over (order by a.stat_date) as if300_stddev,stddev_samp(a.ih50_roe_lag) over (order by a.stat_date) as ih50_stddev,stddev_samp(a.ic500_roe_lag) over (order by a.stat_date) as ic500_stddev,\n" +
                "stddev_samp(a.nv_roe_lag-a.idx_roe_lag) over (order by a.stat_date) as nv_stderr,stddev_samp(a.idx_roe_lag-a.idx_roe_lag) over (order by a.stat_date) as idx_stderr,stddev_samp(a.if300_roe_lag-a.idx_roe_lag) over (order by a.stat_date) as if300_stderr,stddev_samp(a.ih50_roe_lag-a.idx_roe_lag) over (order by a.stat_date) as ih50_stderr,stddev_samp(a.ic500_roe_lag-a.idx_roe_lag) over (order by a.stat_date) as ic500_stderr\n" +
                "from nv_a a";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        assertEquals("SELECT a.*, avg(a.nv_roe_lag) OVER () AS nv_roe_avg\n" +
                "\t, avg(a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS idx_roe_avg\n" +
                "\t, avg(a.if300_roe_lag) OVER () AS if300_roe_avg\n" +
                "\t, avg(a.ih50_roe_lag) OVER () AS ih50_roe_avg\n" +
                "\t, avg(a.ic500_roe_lag) OVER () AS ic500_roe_avg\n" +
                "\t, max(a.nv_retrace) OVER (ORDER BY a.stat_date) AS nv_retrace_max\n" +
                "\t, max(a.idx_retrace) OVER (ORDER BY a.stat_date) AS idx_retrace_max\n" +
                "\t, max(a.if300_retrace) OVER (ORDER BY a.stat_date) AS if300_retrace_max\n" +
                "\t, max(a.ih50_retrace) OVER (ORDER BY a.stat_date) AS ih50_retrace_max\n" +
                "\t, max(a.ic500_retrace) OVER (ORDER BY a.stat_date) AS ic500_retrace_max\n" +
                "\t, CASE \n" +
                "\t\tWHEN a.nv_flag >= 0 THEN sum(1) OVER (ORDER BY a.stat_date)\n" +
                "\t\tELSE sum(0) OVER (ORDER BY a.stat_date)\n" +
                "\tEND AS nv_flag_up, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.nv_flag <= 0) AS nv_flag_down\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.idx_flag >= 0) AS idx_flag_up\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.idx_flag <= 0) AS idx_flag_down\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.if300_flag >= 0) AS if300_flag_up\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.if300_flag <= 0) AS if300_flag_down\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.ih50_flag >= 0) AS ih50_flag_up\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.ih50_flag <= 0) AS ih50_flag_down\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.ic500_flag >= 0) AS ic500_flag_up\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.ic500_flag <= 0) AS ic500_flag_down\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.idx_roe_lag > 0) AS idx_win_cnt\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.idx_roe_lag > a.idx_roe_lag) AS idx_win_idx_cnt\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.nv_roe_lag > 0) AS nv_win_cnt\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.nv_roe_lag > a.idx_roe_lag) AS nv_win_idx_cnt\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.if300_roe_lag > 0) AS if300_win_cnt\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.if300_roe_lag > a.idx_roe_lag) AS if300_win_idx_cnt\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.ih50_roe_lag > 0) AS ih50_win_cnt\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.ih50_roe_lag > a.idx_roe_lag) AS ih50_win_idx_cnt\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.ic500_roe_lag > 0) AS ic500_win_cnt\n" +
                "\t, count(1) OVER (ORDER BY a.stat_date) FILTER (WHERE a.ic500_roe_lag > a.idx_roe_lag) AS ic500_win_idx_cnt\n" +
                "\t, regr_slope(a.nv_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS nv_beta\n" +
                "\t, regr_slope(a.idx_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS idx_beta\n" +
                "\t, regr_slope(a.if300_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS if300_beta\n" +
                "\t, regr_slope(a.ih50_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS ih50_beta\n" +
                "\t, regr_slope(a.ic500_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS ic500_beta\n" +
                "\t, regr_intercept(a.nv_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS nv_alpha\n" +
                "\t, regr_intercept(a.idx_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS idx_alpha\n" +
                "\t, regr_intercept(a.if300_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS if300_alpha\n" +
                "\t, regr_intercept(a.ih50_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS ih50_alpha\n" +
                "\t, regr_intercept(a.ic500_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS ic500_alpha\n" +
                "\t, corr(a.nv_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS nv_corr\n" +
                "\t, corr(a.idx_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS idx_corr\n" +
                "\t, corr(a.if300_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS if300_corr\n" +
                "\t, corr(a.ih50_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS ih50_corr\n" +
                "\t, corr(a.ic500_roe_lag, a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS ic500_corr\n" +
                "\t, stddev_samp(a.nv_roe_lag) OVER (ORDER BY a.stat_date) AS nv_stddev\n" +
                "\t, stddev_samp(a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS idx_stddev\n" +
                "\t, stddev_samp(a.if300_roe_lag) OVER (ORDER BY a.stat_date) AS if300_stddev\n" +
                "\t, stddev_samp(a.ih50_roe_lag) OVER (ORDER BY a.stat_date) AS ih50_stddev\n" +
                "\t, stddev_samp(a.ic500_roe_lag) OVER (ORDER BY a.stat_date) AS ic500_stddev\n" +
                "\t, stddev_samp(a.nv_roe_lag - a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS nv_stderr\n" +
                "\t, stddev_samp(a.idx_roe_lag - a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS idx_stderr\n" +
                "\t, stddev_samp(a.if300_roe_lag - a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS if300_stderr\n" +
                "\t, stddev_samp(a.ih50_roe_lag - a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS ih50_stderr\n" +
                "\t, stddev_samp(a.ic500_roe_lag - a.idx_roe_lag) OVER (ORDER BY a.stat_date) AS ic500_stderr\n" +
                "FROM nv_a a", stmt.toString());

        assertEquals("select a.*, avg(a.nv_roe_lag) over () as nv_roe_avg\n" +
                "\t, avg(a.idx_roe_lag) over (order by a.stat_date) as idx_roe_avg\n" +
                "\t, avg(a.if300_roe_lag) over () as if300_roe_avg\n" +
                "\t, avg(a.ih50_roe_lag) over () as ih50_roe_avg\n" +
                "\t, avg(a.ic500_roe_lag) over () as ic500_roe_avg\n" +
                "\t, max(a.nv_retrace) over (order by a.stat_date) as nv_retrace_max\n" +
                "\t, max(a.idx_retrace) over (order by a.stat_date) as idx_retrace_max\n" +
                "\t, max(a.if300_retrace) over (order by a.stat_date) as if300_retrace_max\n" +
                "\t, max(a.ih50_retrace) over (order by a.stat_date) as ih50_retrace_max\n" +
                "\t, max(a.ic500_retrace) over (order by a.stat_date) as ic500_retrace_max\n" +
                "\t, case \n" +
                "\t\twhen a.nv_flag >= 0 then sum(1) over (order by a.stat_date)\n" +
                "\t\telse sum(0) over (order by a.stat_date)\n" +
                "\tend as nv_flag_up, count(1) over (order by a.stat_date) filter (where a.nv_flag <= 0) as nv_flag_down\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.idx_flag >= 0) as idx_flag_up\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.idx_flag <= 0) as idx_flag_down\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.if300_flag >= 0) as if300_flag_up\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.if300_flag <= 0) as if300_flag_down\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.ih50_flag >= 0) as ih50_flag_up\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.ih50_flag <= 0) as ih50_flag_down\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.ic500_flag >= 0) as ic500_flag_up\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.ic500_flag <= 0) as ic500_flag_down\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.idx_roe_lag > 0) as idx_win_cnt\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.idx_roe_lag > a.idx_roe_lag) as idx_win_idx_cnt\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.nv_roe_lag > 0) as nv_win_cnt\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.nv_roe_lag > a.idx_roe_lag) as nv_win_idx_cnt\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.if300_roe_lag > 0) as if300_win_cnt\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.if300_roe_lag > a.idx_roe_lag) as if300_win_idx_cnt\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.ih50_roe_lag > 0) as ih50_win_cnt\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.ih50_roe_lag > a.idx_roe_lag) as ih50_win_idx_cnt\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.ic500_roe_lag > 0) as ic500_win_cnt\n" +
                "\t, count(1) over (order by a.stat_date) filter (where a.ic500_roe_lag > a.idx_roe_lag) as ic500_win_idx_cnt\n" +
                "\t, regr_slope(a.nv_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as nv_beta\n" +
                "\t, regr_slope(a.idx_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as idx_beta\n" +
                "\t, regr_slope(a.if300_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as if300_beta\n" +
                "\t, regr_slope(a.ih50_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as ih50_beta\n" +
                "\t, regr_slope(a.ic500_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as ic500_beta\n" +
                "\t, regr_intercept(a.nv_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as nv_alpha\n" +
                "\t, regr_intercept(a.idx_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as idx_alpha\n" +
                "\t, regr_intercept(a.if300_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as if300_alpha\n" +
                "\t, regr_intercept(a.ih50_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as ih50_alpha\n" +
                "\t, regr_intercept(a.ic500_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as ic500_alpha\n" +
                "\t, corr(a.nv_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as nv_corr\n" +
                "\t, corr(a.idx_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as idx_corr\n" +
                "\t, corr(a.if300_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as if300_corr\n" +
                "\t, corr(a.ih50_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as ih50_corr\n" +
                "\t, corr(a.ic500_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as ic500_corr\n" +
                "\t, stddev_samp(a.nv_roe_lag) over (order by a.stat_date) as nv_stddev\n" +
                "\t, stddev_samp(a.idx_roe_lag) over (order by a.stat_date) as idx_stddev\n" +
                "\t, stddev_samp(a.if300_roe_lag) over (order by a.stat_date) as if300_stddev\n" +
                "\t, stddev_samp(a.ih50_roe_lag) over (order by a.stat_date) as ih50_stddev\n" +
                "\t, stddev_samp(a.ic500_roe_lag) over (order by a.stat_date) as ic500_stddev\n" +
                "\t, stddev_samp(a.nv_roe_lag - a.idx_roe_lag) over (order by a.stat_date) as nv_stderr\n" +
                "\t, stddev_samp(a.idx_roe_lag - a.idx_roe_lag) over (order by a.stat_date) as idx_stderr\n" +
                "\t, stddev_samp(a.if300_roe_lag - a.idx_roe_lag) over (order by a.stat_date) as if300_stderr\n" +
                "\t, stddev_samp(a.ih50_roe_lag - a.idx_roe_lag) over (order by a.stat_date) as ih50_stderr\n" +
                "\t, stddev_samp(a.ic500_roe_lag - a.idx_roe_lag) over (order by a.stat_date) as ic500_stderr\n" +
                "from nv_a a", stmt.toLowerCaseString());
    }
}
