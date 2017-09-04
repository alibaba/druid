package com.alibaba.druid.benckmark.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallUtils;
import com.alibaba.druid.wall.WallVisitor;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
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
        String sql4 = "UPDATE ROLLBACK_ON_FAIL TARGET_AFFECT_ROW 1 "
                + "`table_3966` AS `table_3966_11` SET `version` = `version` + 3, `gmt_modified` = NOW(), `optype` = ?, `feature` = ? "
                + "WHERE `sub_biz_order_id` = ? AND `biz_order_type` = ? AND `id` = ? AND `ti_id` = ? AND `optype` = ? AND `root_id` = ?";
        String sql5= "SELECT id, item_id, rule_id, tag_id, ext , gmt_create, gmt_modified FROM wukong_preview_item_tag WHERE item_id = ? AND rule_id = ?";

        for (int i = 0; i < 5; ++i) {
//            perf(sql); // 6740 6201 4752 4514 4391 4218 4127 4124
//            perf(sql2); // 2948 2928 2869 2780 2502
//            perf(sql3); // 15093 10392 10416 10154 10007 9126 8907
//            perf(sql4); // 4429 4190 4023 3747
//            perf(sql5); // 1917

//            perf_parse(sql); // 4643 4377 4345 3801 3627 3228 2961 2959
//            perf_parse(sql2); // 1918 1779 1666 1646
//            perf_parse(sql3); // 9174 5875 5805 5536 5717
//            perf_parse(sql4); // 2953 2502
            perf_parse(sql5); // 1339

//            perf_lexer(sql5); // 813

//            perf_hashCode64(sql5); // 181

//            perf_stat(sql); // 15214 11793 13628 13561 13259 9946 7637 7444 7389 7326 7176 6687 5973 5660

//            perf_resolve(sql); // 4970 4586 3600 3595

//            perf_wall(sql); // 9695 5017
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

    public void perf_stat(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            SqlHolder holder = SqlHolder.of(sql);
            holder.ensureParsed();

            //new SchemaRepository(JdbcConstants.MYSQL).resolve(holder.ast);
            SQLASTVisitor visitor = new MySqlSchemaStatVisitor();
            holder.ast.accept(visitor);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_resolve(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            SqlHolder holder = SqlHolder.of(sql);
            holder.ensureParsed();

            new SchemaRepository(JdbcConstants.MYSQL).resolve(holder.ast);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    MySqlWallProvider provider = new MySqlWallProvider();
    public void perf_wall(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            provider.checkValid(sql);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_lexer(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            int literal_count = 0;

            MySqlLexer lexer = new MySqlLexer(sql);

            for (; ; ) {
                lexer.nextToken();
                Token token = lexer.token();

                if (token == Token.LITERAL_INT || token == Token.LITERAL_CHARS) {
                    literal_count++;
                    break;
                } else if (token == Token.EOF || token == Token.ERROR) {
                    break;
                }
            }

        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_hashCode64(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            FnvHash.fnv1a_64(sql);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }
}
