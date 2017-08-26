package com.alibaba.druid.benckmark.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MySqlParameterizedBenchmark extends TestCase {

    public void test_perf() throws Exception {
        String sql = "select id as id,    gmt_create as gmtCreate,    gmt_modified as gmtModified,    name as name,    owner as owner,    type as type,    statement as statement,    datasource as datasource,    meta as meta,    param_file as paramFile,    sharable as sharable,    data_type as dataType,    status as status,    config as config,    project_id as projectId,    plugins as plugins,    field_compare as fieldCompare,    field_ext as fieldExt,    openx as openx   from quark_s_dataset     where id = 12569434";
        String sql2 = "/* ///6ea6f232/ */select count(*) FROM (SELECT  * FROM cluster_ins_mapping  WHERE `engine` = 'MySQL' and `status`='Activation'  GROUP BY ip,port ) a;";
        String sql3 = "/* 0be5256b15035048614234260e129b/0.1//722b9d1a/ */          select count(*) from (SELECT message_id,employee_name,employee_id FROM tunning_overview where         instance_info IS NOT NULL         and instance_info = '11.179.218.9:3306'         and gmt_modified >= '2017-08-23 00:00:00' and gmt_modified <= '2017-08-24 00:00:00'         ) o join         (SELECT message_id,index_advice_count,index_advice,gmt_created FROM tunning_task_detail         where gmt_modified >= '2017-08-23 00:00:00'         and gmt_modified <= '2017-08-24 00:00:00'         and index_advice_count >= 0 and error_code='0000'         ) t  on o.message_id=t.message_id         where o.employee_name!='system' or ( o.employee_name='system' and t.index_advice_count>0);";

        for (int i = 0; i < 5; ++i) {
//            perf(sql); // 6740 6201 4752 4514
//            perf(sql2); // 2948 2928 2869 2780 2502
            perf(sql3); // 15093 10392 10416 10154 10007 9126 8907
//            perf_parse(sql); // 4643 4377 4345 3801 3627 3228
//            perf_parse(sql2); // 1918 1779 1666 1646
//            perf_parse(sql3); // 9174 5875 5805 5536
        }
    }
    public void perf(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            String psql = SqlHolder.of(sql).parameterize();
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_parse(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            SqlHolder.of(sql).ensureParsed();
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

}
