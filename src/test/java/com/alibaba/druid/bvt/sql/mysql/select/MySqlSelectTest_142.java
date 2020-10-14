package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_142 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select count(1) from ( select aid from ysf_saas_test.abc_user_behavior_search_d where aid is not null and ds = 20180111 and bhv_obj_type = 4 and bhv_obj in ('衣服')  INTERSECT select a0.aid from (  select distinct t0.aid as aid from ( select aid,  sum(frequency)  from ysf_saas_test.abc_user_behavior_pay_d_002 where aid is not null and ds=20180111 and bhv_obj_type = 1 and brand_id in (29493) and ext_field_7 in ('4')  group by aid having  sum(frequency) >1) t0 ) a0 join (  select distinct t0.aid as aid from ( select aid,  sum(ext_field_9)  from ysf_saas_test.abc_user_behavior_pay_d_002 where aid is not null and ds=20180111 and bhv_obj_type = 1 and brand_id in (29493) and ext_field_7 in ('4')  group by aid having  sum(ext_field_9) >1) t0 ) b0 on a0.aid = b0.aid  INTERSECT select aid from ysf_saas_test.abc_user_behavior_collect_item_d_002 where aid is not null and ds=20180111 and bhv_obj_type = 1 and brand_id in (29493)   )d;\n";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT count(1)\n" +
                "FROM (\n" +
                "\tSELECT aid\n" +
                "\tFROM ysf_saas_test.abc_user_behavior_search_d\n" +
                "\tWHERE aid IS NOT NULL\n" +
                "\t\tAND ds = 20180111\n" +
                "\t\tAND bhv_obj_type = 4\n" +
                "\t\tAND bhv_obj IN ('衣服')\n" +
                "\tINTERSECT\n" +
                "\tSELECT a0.aid\n" +
                "\tFROM (\n" +
                "\t\tSELECT DISTINCT t0.aid AS aid\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT aid, sum(frequency)\n" +
                "\t\t\tFROM ysf_saas_test.abc_user_behavior_pay_d_002\n" +
                "\t\t\tWHERE aid IS NOT NULL\n" +
                "\t\t\t\tAND ds = 20180111\n" +
                "\t\t\t\tAND bhv_obj_type = 1\n" +
                "\t\t\t\tAND brand_id IN (29493)\n" +
                "\t\t\t\tAND ext_field_7 IN ('4')\n" +
                "\t\t\tGROUP BY aid\n" +
                "\t\t\tHAVING sum(frequency) > 1\n" +
                "\t\t) t0\n" +
                "\t) a0\n" +
                "\t\tJOIN (\n" +
                "\t\t\tSELECT DISTINCT t0.aid AS aid\n" +
                "\t\t\tFROM (\n" +
                "\t\t\t\tSELECT aid, sum(ext_field_9)\n" +
                "\t\t\t\tFROM ysf_saas_test.abc_user_behavior_pay_d_002\n" +
                "\t\t\t\tWHERE aid IS NOT NULL\n" +
                "\t\t\t\t\tAND ds = 20180111\n" +
                "\t\t\t\t\tAND bhv_obj_type = 1\n" +
                "\t\t\t\t\tAND brand_id IN (29493)\n" +
                "\t\t\t\t\tAND ext_field_7 IN ('4')\n" +
                "\t\t\t\tGROUP BY aid\n" +
                "\t\t\t\tHAVING sum(ext_field_9) > 1\n" +
                "\t\t\t) t0\n" +
                "\t\t) b0\n" +
                "\t\tON a0.aid = b0.aid\n" +
                "\tINTERSECT\n" +
                "\tSELECT aid\n" +
                "\tFROM ysf_saas_test.abc_user_behavior_collect_item_d_002\n" +
                "\tWHERE aid IS NOT NULL\n" +
                "\t\tAND ds = 20180111\n" +
                "\t\tAND bhv_obj_type = 1\n" +
                "\t\tAND brand_id IN (29493)\n" +
                ") d;", stmt.toString());

        assertEquals("SELECT count(1)\n" +
                        "FROM (\n" +
                        "\tSELECT aid\n" +
                        "\tFROM ysf_saas_test.abc_user_behavior_search_d\n" +
                        "\tWHERE aid IS NOT NULL\n" +
                        "\t\tAND ds = ?\n" +
                        "\t\tAND bhv_obj_type = ?\n" +
                        "\t\tAND bhv_obj IN (?)\n" +
                        "\tINTERSECT\n" +
                        "\tSELECT a0.aid\n" +
                        "\tFROM (\n" +
                        "\t\tSELECT DISTINCT t0.aid AS aid\n" +
                        "\t\tFROM (\n" +
                        "\t\t\tSELECT aid, sum(frequency)\n" +
                        "\t\t\tFROM ysf_saas_test.abc_user_behavior_pay_d\n" +
                        "\t\t\tWHERE aid IS NOT NULL\n" +
                        "\t\t\t\tAND ds = ?\n" +
                        "\t\t\t\tAND bhv_obj_type = ?\n" +
                        "\t\t\t\tAND brand_id IN (?)\n" +
                        "\t\t\t\tAND ext_field_7 IN (?)\n" +
                        "\t\t\tGROUP BY aid\n" +
                        "\t\t\tHAVING sum(frequency) > ?\n" +
                        "\t\t) t0\n" +
                        "\t) a0\n" +
                        "\t\tJOIN (\n" +
                        "\t\t\tSELECT DISTINCT t0.aid AS aid\n" +
                        "\t\t\tFROM (\n" +
                        "\t\t\t\tSELECT aid, sum(ext_field_9)\n" +
                        "\t\t\t\tFROM ysf_saas_test.abc_user_behavior_pay_d\n" +
                        "\t\t\t\tWHERE aid IS NOT NULL\n" +
                        "\t\t\t\t\tAND ds = ?\n" +
                        "\t\t\t\t\tAND bhv_obj_type = ?\n" +
                        "\t\t\t\t\tAND brand_id IN (?)\n" +
                        "\t\t\t\t\tAND ext_field_7 IN (?)\n" +
                        "\t\t\t\tGROUP BY aid\n" +
                        "\t\t\t\tHAVING sum(ext_field_9) > ?\n" +
                        "\t\t\t) t0\n" +
                        "\t\t) b0\n" +
                        "\t\tON a0.aid = b0.aid\n" +
                        "\tINTERSECT\n" +
                        "\tSELECT aid\n" +
                        "\tFROM ysf_saas_test.abc_user_behavior_collect_item_d\n" +
                        "\tWHERE aid IS NOT NULL\n" +
                        "\t\tAND ds = ?\n" +
                        "\t\tAND bhv_obj_type = ?\n" +
                        "\t\tAND brand_id IN (?)\n" +
                        ") d;"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));
    }


}