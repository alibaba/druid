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
        String sql = "select id as id,    gmt_create as gmtCreate,    gmt_modified as gmtModified,    name as name,    owner as owner,    type as type,    statement as statement,    datasource as datasource,    meta as meta,    param_file as paramFile,    sharable as sharable,    data_type as dataType,    status as status,    config as config,    project_id as projectId,    plugins as plugins,    field_compare as fieldCompare,    field_ext as fieldExt,    open as open   from quark_s_dataset     where id = 12569434";

        for (int i = 0; i < 5; ++i) {
//            perf(sql); // 6740 6201
            perf_parse(sql); // 4643 4377
        }
    }
    public void perf(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {


            String psql = SqlHolder.of(sql).parameterize();
//            stmt.toString();
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_parse(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {


            SqlHolder.of(sql).ensureParsed();
//            stmt.toString();
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }
}
