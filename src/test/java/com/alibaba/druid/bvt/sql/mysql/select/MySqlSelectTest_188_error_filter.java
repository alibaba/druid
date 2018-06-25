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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.ParserException;

import java.util.List;

public class MySqlSelectTest_188_error_filter extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "\"select a.*, avg(a.nv_roe_lag) over() as nv_roe_avg, avg(a.idx_roe_lag) over (order by a.stat_date) as idx_roe_avg,avg(a.if300_roe_lag) over() as if300_roe_avg, avg(a.ih50_roe_lag) over() as ih50_roe_avg,avg(a.ic500_roe_lag) over() as ic500_roe_avg,\\n\" +\n" +
                "                \"max(a.nv_retrace) over (order by a.stat_date) as nv_retrace_max, max(a.idx_retrace) over (order by a.stat_date) as idx_retrace_max,max(a.if300_retrace) over (order by a.stat_date) as if300_retrace_max,max(a.ih50_retrace) over (order by a.stat_date) as ih50_retrace_max, max(a.ic500_retrace) over (order by a.stat_date) as ic500_retrace_max,\\n\" +\n" +
                "                \"case when a.nv_flag>=0 then sum(1) over (order by a.stat_date) else sum(0) over (order by a.stat_date) end as nv_flag_up,count(1) filter(where a.nv_flag<=0) over (order by a.stat_date) as nv_flag_down,\\n\" +\n" +
                "                \"count(1) filter(where a.idx_flag>=0) over (order by a.stat_date) as idx_flag_up,count(1) filter(where a.idx_flag<=0) over (order by a.stat_date) as idx_flag_down,\\n\" +\n" +
                "                \"count(1) filter(where a.if300_flag>=0) over (order by a.stat_date) as if300_flag_up,count(1) filter(where a.if300_flag<=0) over (order by a.stat_date) as if300_flag_down,\\n\" +\n" +
                "                \"count(1) filter(where a.ih50_flag>=0) over (order by a.stat_date) as ih50_flag_up,count(1) filter(where a.ih50_flag<=0) over (order by a.stat_date) as ih50_flag_down,\\n\" +\n" +
                "                \"count(1) filter(where a.ic500_flag>=0) over (order by a.stat_date) as ic500_flag_up,count(1) filter(where a.ic500_flag<=0) over (order by a.stat_date) as ic500_flag_down,\\n\" +\n" +
                "                \"count(1) filter(where a.idx_roe_lag>0) over (order by a.stat_date) as idx_win_cnt,count(1) filter(where a.idx_roe_lag>a.idx_roe_lag) over (order by a.stat_date) as idx_win_idx_cnt,\\n\" +\n" +
                "                \"count(1) filter(where a.nv_roe_lag>0) over (order by a.stat_date) as nv_win_cnt,count(1) filter(where a.nv_roe_lag>a.idx_roe_lag) over (order by a.stat_date) as nv_win_idx_cnt,\\n\" +\n" +
                "                \"count(1) filter(where a.if300_roe_lag>0) over (order by a.stat_date) as if300_win_cnt,count(1) filter(where a.if300_roe_lag>a.idx_roe_lag) over (order by a.stat_date) as if300_win_idx_cnt,\\n\" +\n" +
                "                \"count(1) filter(where a.ih50_roe_lag>0) over (order by a.stat_date) as ih50_win_cnt,count(1) filter(where a.ih50_roe_lag>a.idx_roe_lag) over (order by a.stat_date) as ih50_win_idx_cnt,\\n\" +\n" +
                "                \"count(1) filter(where a.ic500_roe_lag>0) over (order by a.stat_date) as ic500_win_cnt,count(1) filter(where a.ic500_roe_lag>a.idx_roe_lag) over (order by a.stat_date) as ic500_win_idx_cnt,\\n\" +\n" +
                "                \"--根据所有输入点(X,Y)利用最小二乘法计算一个线性方程式。然后返回该直线的斜率\\n\" +\n" +
                "                \"regr_slope(a.nv_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as nv_beta,regr_slope(a.idx_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as idx_beta,regr_slope(a.if300_roe_lag, a.idx_roe_lag) over (order by a.stat_date) as if300_beta,regr_slope(a.ih50_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as ih50_beta,regr_slope(a.ic500_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as ic500_beta,\\n\" +\n" +
                "                \"--根据所有输入点(X,Y)利用最小二乘法计算一个线性方程式。然后返回该直线的Y轴截距\\n\" +\n" +
                "                \"regr_intercept(a.nv_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as nv_alpha,regr_intercept(a.idx_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as idx_alpha,regr_intercept(a.if300_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as if300_alpha,regr_intercept(a.ih50_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as ih50_alpha,regr_intercept(a.ic500_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as ic500_alpha,\\n\" +\n" +
                "                \"--相关系数\\n\" +\n" +
                "                \"corr(a.nv_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as nv_corr,corr(a.idx_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as idx_corr,corr(a.if300_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as if300_corr,corr(a.ih50_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as ih50_corr,corr(a.ic500_roe_lag,a.idx_roe_lag) over (order by a.stat_date) as ic500_corr,\\n\" +\n" +
                "                \"--样本标准差\\n\" +\n" +
                "                \"stddev_samp(a.nv_roe_lag) over (order by a.stat_date) as nv_stddev,stddev_samp(a.idx_roe_lag) over (order by a.stat_date) as idx_stddev,stddev_samp(a.if300_roe_lag) over (order by a.stat_date) as if300_stddev,stddev_samp(a.ih50_roe_lag) over (order by a.stat_date) as ih50_stddev,stddev_samp(a.ic500_roe_lag) over (order by a.stat_date) as ic500_stddev,\\n\" +\n" +
                "                \"stddev_samp(a.nv_roe_lag-a.idx_roe_lag) over (order by a.stat_date) as nv_stderr,stddev_samp(a.idx_roe_lag-a.idx_roe_lag) over (order by a.stat_date) as idx_stderr,stddev_samp(a.if300_roe_lag-a.idx_roe_lag) over (order by a.stat_date) as if300_stderr,stddev_samp(a.ih50_roe_lag-a.idx_roe_lag) over (order by a.stat_date) as ih50_stderr,stddev_samp(a.ic500_roe_lag-a.idx_roe_lag) over (order by a.stat_date) as ic500_stderr\\n\" +\n" +
                "                \"from nv_a a\"";

        Exception error = null;
        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            List<SQLStatement> statementList = parser.parseStatementList();
        } catch (ParserException ex) {
            error = ex;
            ex.printStackTrace();
        }
        assertNotNull(error);
    }
}
