package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_140_union extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select count(1) from\n" +
                "(\n" +
                "select distinct aid from ysf_saas.abc_user_behavior_search_d where aid is not null and ds>=20180218 and ds<=20180304 and bhv_obj_type = 4 and bhv_obj in ('衣服') \n" +
                "INTERSECT\n" +
                "select distinct t0.aid as aid from ( select aid,  count(distinct aid,ds)  from ysf_saas.abc_user_behavior_view_item_d_002 where aid is not null and ds>=20180218 and ds<=20180304 and bhv_obj_type = 1 and brand_id in (29493)  group by aid having  count(distinct aid,ds) >2) t0 \n" +
                "INTERSECT\n" +
                "select distinct aid from ysf_saas.abc_user_behavior_collect_item_d_002 where aid is not null and ds>=20180203 and ds<=20180304 and bhv_obj_type = 1 and brand_id in (29493)  \n" +
                "INTERSECT\n" +
                "select distinct a0.aid from ysf_saas.ods_abif_aid_fetched_tags_bus_ads_v11 a0 where a0.pred_age_level in ('13','12','10','11') \n" +
                ")d;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT count(1)\n" +
                "FROM (\n" +
                "\tSELECT DISTINCT aid\n" +
                "\tFROM ysf_saas.abc_user_behavior_search_d\n" +
                "\tWHERE aid IS NOT NULL\n" +
                "\t\tAND ds >= 20180218\n" +
                "\t\tAND ds <= 20180304\n" +
                "\t\tAND bhv_obj_type = 4\n" +
                "\t\tAND bhv_obj IN ('衣服')\n" +
                "\tINTERSECT\n" +
                "\tSELECT DISTINCT t0.aid AS aid\n" +
                "\tFROM (\n" +
                "\t\tSELECT aid, count(DISTINCT aid, ds)\n" +
                "\t\tFROM ysf_saas.abc_user_behavior_view_item_d_002\n" +
                "\t\tWHERE aid IS NOT NULL\n" +
                "\t\t\tAND ds >= 20180218\n" +
                "\t\t\tAND ds <= 20180304\n" +
                "\t\t\tAND bhv_obj_type = 1\n" +
                "\t\t\tAND brand_id IN (29493)\n" +
                "\t\tGROUP BY aid\n" +
                "\t\tHAVING count(DISTINCT aid, ds) > 2\n" +
                "\t) t0\n" +
                "\tINTERSECT\n" +
                "\tSELECT DISTINCT aid\n" +
                "\tFROM ysf_saas.abc_user_behavior_collect_item_d_002\n" +
                "\tWHERE aid IS NOT NULL\n" +
                "\t\tAND ds >= 20180203\n" +
                "\t\tAND ds <= 20180304\n" +
                "\t\tAND bhv_obj_type = 1\n" +
                "\t\tAND brand_id IN (29493)\n" +
                "\tINTERSECT\n" +
                "\tSELECT DISTINCT a0.aid\n" +
                "\tFROM ysf_saas.ods_abif_aid_fetched_tags_bus_ads_v11 a0\n" +
                "\tWHERE a0.pred_age_level IN ('13', '12', '10', '11')\n" +
                ") d;", stmt.toString());

        assertEquals("SELECT count(1)\n" +
                        "FROM (\n" +
                        "\tSELECT DISTINCT aid\n" +
                        "\tFROM ysf_saas.abc_user_behavior_search_d\n" +
                        "\tWHERE aid IS NOT NULL\n" +
                        "\t\tAND ds >= ?\n" +
                        "\t\tAND ds <= ?\n" +
                        "\t\tAND bhv_obj_type = ?\n" +
                        "\t\tAND bhv_obj IN (?)\n" +
                        "\tINTERSECT\n" +
                        "\tSELECT DISTINCT t0.aid AS aid\n" +
                        "\tFROM (\n" +
                        "\t\tSELECT aid, count(DISTINCT aid, ds)\n" +
                        "\t\tFROM ysf_saas.abc_user_behavior_view_item_d\n" +
                        "\t\tWHERE aid IS NOT NULL\n" +
                        "\t\t\tAND ds >= ?\n" +
                        "\t\t\tAND ds <= ?\n" +
                        "\t\t\tAND bhv_obj_type = ?\n" +
                        "\t\t\tAND brand_id IN (?)\n" +
                        "\t\tGROUP BY aid\n" +
                        "\t\tHAVING count(DISTINCT aid, ds) > ?\n" +
                        "\t) t0\n" +
                        "\tINTERSECT\n" +
                        "\tSELECT DISTINCT aid\n" +
                        "\tFROM ysf_saas.abc_user_behavior_collect_item_d\n" +
                        "\tWHERE aid IS NOT NULL\n" +
                        "\t\tAND ds >= ?\n" +
                        "\t\tAND ds <= ?\n" +
                        "\t\tAND bhv_obj_type = ?\n" +
                        "\t\tAND brand_id IN (?)\n" +
                        "\tINTERSECT\n" +
                        "\tSELECT DISTINCT a0.aid\n" +
                        "\tFROM ysf_saas.ods_abif_aid_fetched_tags_bus_ads_v a0\n" +
                        "\tWHERE a0.pred_age_level IN (?)\n" +
                        ") d;"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));
    }


}