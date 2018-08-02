package com.alibaba.druid.sql.odps;

import com.alibaba.druid.bvt.pool.TestClone;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class Issue2102 extends TestCase {
    public void test_for_issue() throws Exception {
        String sql = "SELECT t1.job_id,t2.lz_id,t1.run_time,t1.submit_time,t1.finish_time,\n" +
                "  \t\tt2.queue_name,t2.service_level,t2.user_name,\"MAPREDUCE\" as job_type\n" +
                "  \tFROM \n" +
                "    \t(SELECT job_id,round((finish_time-submit_time)/1000) as run_time,\n" +
                "    \t\tfrom_unixtime(round(submit_time/1000),'yyyy-MM-dd HH:mm:ss') as submit_time,\n" +
                "    \t\tfrom_unixtime(round(finish_time/1000),'yyyy-MM-dd HH:mm:ss') as finish_time,ts FROM ods_base::t_job_mr_hour PARTITION(par_${YYYYMMDDHH})a \n" +
                "    \t\tWHERE finish_time>submit_time) t1\n" +
                "     \tJOIN \n" +
                "     \t(SELECT job_id,split(usp_param,'_')[0] as lz_id,ugi_groupname as queue_name,\n" +
                "\t\t servicelevel as service_level,tdw_username as user_name FROM ods_base::t_jobconf_mr_hour PARTITION(par_${YYYYMMDDHH})b)t2 \n" +
                "    \t ON (t1.job_id = t2.job_id)";

        SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
    }
}
